package com.example.android.quicktap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quicktap.setup.DBAssetHelper;

public class ResultsListActivity extends AppCompatActivity {

    QuickTapSQLiteOpenHelper mHelper;
    Window mWindow;
    CursorAdapter mCursorAdapter;
    int mSearchId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);
        DBAssetHelper dbSetup = new DBAssetHelper(ResultsListActivity.this);
        dbSetup.getReadableDatabase();


        mSearchId = getIntent().getIntExtra(QuickTapSQLiteOpenHelper.RESULTS_SEARCH_ID, -1);

        //TODO - handle case where searchId = -1
        mHelper = new QuickTapSQLiteOpenHelper(ResultsListActivity.this);
        Cursor cursor = mHelper.getResultsBySearchId(mSearchId);

        mWindow = this.getWindow();
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        setTitle("QuickTap");


        mCursorAdapter = new CursorAdapter(ResultsListActivity.this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                //TODO - for now just reuse this layout, but later make new one
                return LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, final Cursor cursor) {
                TextView beerNameTextView = (TextView) view.findViewById(R.id.textViewSearchText);

                final String beerName = cursor.getString(cursor.getColumnIndex(QuickTapSQLiteOpenHelper.RESULTS_BEER_NAME));
                beerNameTextView.setText(beerName);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO - something...
                        //adds to list
                        Cursor cursor = mHelper.searchBeerList(beerName.toUpperCase());
                        if (cursor.getCount() == 0) {
                            mHelper.addDrink(beerName.toUpperCase(), "1");
                        } else {
                            int drinkCount = mHelper.getCountByName(beerName.toUpperCase());
                            int mUpdatedCount = drinkCount + 1;
                            mHelper.updateDrinkCount(mUpdatedCount, beerName.toUpperCase());
                        }
                        Toast.makeText(ResultsListActivity.this, beerName.toUpperCase() + " has been added!", Toast.LENGTH_SHORT).show();


                        //delete from Results table where searchId = mSearchId
                        mHelper.deleteResultsBySearchId(mSearchId);

                        //delete from Search table where id = mSearchId
                        mHelper.deleteSearch(mSearchId);

                        //opens large screen
                        Intent intent = new Intent(ResultsListActivity.this, LargeDisplayActivity.class);
                        intent.putExtra("BeerToDisplayKey", beerName);
                        startActivity(intent);
                    }
                });
            }
        };

        final ListView listView = (ListView) findViewById(R.id.resultsListView);
        listView.setAdapter(mCursorAdapter);

        /*
        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String itemNameRemove = ((TextView) view.findViewById(R.id.textViewBeerName)).getText().toString();
                mHelper.removeDrink(itemNameRemove);
                Cursor cursorInside = mHelper.getBeerList();
                mCursorAdapter.swapCursor(cursorInside);
                return true;
            }
        };
        listView.setOnItemLongClickListener(longClickListener);
        */

    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = mHelper.getResultsBySearchId(mSearchId);
        mCursorAdapter.changeCursor(cursor);

    }
}

package com.example.android.quicktap;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quicktap.setup.DBAssetHelper;

public class SearchListActivity extends AppCompatActivity {

    QuickTapSQLiteOpenHelper mHelper;
    Window mWindow;
    CursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        DBAssetHelper dbSetup = new DBAssetHelper(SearchListActivity.this);
        dbSetup.getReadableDatabase();

        mHelper = new QuickTapSQLiteOpenHelper(SearchListActivity.this);
        Cursor cursor = mHelper.getSearches();

        mWindow = this.getWindow();
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        setTitle("QuickTap");


        mCursorAdapter = new CursorAdapter(SearchListActivity.this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, final Cursor cursor) {
                TextView queryText = (TextView) view.findViewById(R.id.textViewSearchText);

                String searchText = cursor.getString(cursor.getColumnIndex(QuickTapSQLiteOpenHelper.SEARCH_QUERY_TEXT));
                queryText.setText(searchText);

                final int searchId = cursor.getInt(cursor.getColumnIndex(QuickTapSQLiteOpenHelper.SEARCH_COL_ID));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SearchListActivity.this, "searchId: " + searchId, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        final ListView listView = (ListView) findViewById(R.id.beerCountListView);
        listView.setAdapter(mCursorAdapter);

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = mHelper.getSearches();
        mCursorAdapter.changeCursor(cursor);

    }
}

package com.example.android.quicktap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.quicktap.setup.DBAssetHelper;

public class ListActivity extends AppCompatActivity {

    QuickTapSQLiteOpenHelper mHelper;
    Window mWindow;
    CursorAdapter mCursorAdapter;
    Toolbar mTopToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        DBAssetHelper dbSetup = new DBAssetHelper(ListActivity.this);
        dbSetup.getReadableDatabase();

        mHelper = new QuickTapSQLiteOpenHelper(ListActivity.this);
        Cursor cursor = mHelper.getBeerList();

        mWindow = this.getWindow();
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTopToolbar.setTitleTextColor(getResources().getColor(R.color.colorIconBorder));
        setTitle("QuickTap");


        mCursorAdapter = new CursorAdapter(ListActivity.this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.beer_count_list, parent, false);
            }

            @Override
            public void bindView(View view, Context context, final Cursor cursor) {
                TextView beerName = (TextView) view.findViewById(R.id.textViewBeerName);
                TextView beerCount = (TextView) view.findViewById(R.id.textViewBeerCount);

                String beerNameString = cursor.getString(cursor.getColumnIndex(QuickTapSQLiteOpenHelper.COL_BEER_NAME));
                String beerCountString = cursor.getString(cursor.getColumnIndex(QuickTapSQLiteOpenHelper.COL_BEER_COUNT));

                beerName.setText(beerNameString);
                beerCount.setText("Count: " + beerCountString);
            }
        };

        final ListView listView = (ListView) findViewById(R.id.beerCountListView);
        listView.setAdapter(mCursorAdapter);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String beerName = ((TextView) view.findViewById(R.id.textViewBeerName)).getText().toString();
                Intent intent = new Intent(ListActivity.this, LargeDisplayActivity.class);
                intent.putExtra("BeerToDisplayKey", beerName);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(onItemClickListener);


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

        Cursor cursor = mHelper.getBeerList();
        mCursorAdapter.changeCursor(cursor);

    }
}

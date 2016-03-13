package com.example.android.quicktap;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.quicktap.setup.DBAssetHelper;

public class ListActivity extends AppCompatActivity {

    QuickTapSQLiteOpenHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        DBAssetHelper dbSetup = new DBAssetHelper(ListActivity.this);
        dbSetup.getReadableDatabase();

        mHelper = new QuickTapSQLiteOpenHelper(ListActivity.this);
        final Cursor cursor = mHelper.getBeerList();

        final CursorAdapter cursorAdapter = new CursorAdapter(ListActivity.this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.beer_count_list, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView beerName = (TextView) view.findViewById(R.id.textViewBeerName);
                TextView beerCount = (TextView) view.findViewById(R.id.textViewBeerCount);

                String beerNameString = cursor.getString(cursor.getColumnIndex(QuickTapSQLiteOpenHelper.COL_BEER_NAME));
                String beerCountString = cursor.getString(cursor.getColumnIndex(QuickTapSQLiteOpenHelper.COL_BEER_COUNT));

                beerName.setText(beerNameString);
                beerCount.setText("Beer count: " + beerCountString);
            }
        };

        final ListView listView = (ListView) findViewById(R.id.beerCountListView);
        listView.setAdapter(cursorAdapter);

        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String itemNameRemove = ((TextView) view.findViewById(R.id.textViewBeerName)).getText().toString();
                mHelper.removeDrink(itemNameRemove);
                Cursor cursorInside = mHelper.getBeerList();
                cursorAdapter.swapCursor(cursorInside);
                return true;
            }
        };
        listView.setOnItemLongClickListener(longClickListener);

    }
}

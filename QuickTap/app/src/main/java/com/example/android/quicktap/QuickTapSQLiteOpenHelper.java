package com.example.android.quicktap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ShowMe on 3/12/16.
 */
public class QuickTapSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = QuickTapSQLiteOpenHelper.class.getCanonicalName();

    private static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "QUICKTAP_DB";
    public static final String BEER_LIST_TABLE_NAME = "BEER_LIST";
    public static final String SEARCH_LIST_TABLE_NAME = "SEARCH_LIST";
    public static final String RESULTS_LIST_TABLE_NAME = "RESULTS_LIST";

    public static final String COL_ID = "_id";
    public static final String COL_BEER_NAME = "BEER_NAME";
    public static final String COL_BEER_COUNT = "BEER_COUNT";
    public static final String[] BEER_COLUMNS = {COL_ID, COL_BEER_NAME, COL_BEER_COUNT};
    private static final String CREATE_BEER_LIST_TABLE =
            "CREATE TABLE " + BEER_LIST_TABLE_NAME +
                    "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_BEER_NAME + " TEXT, " +
                    COL_BEER_COUNT + " TEXT)";

    public static final String SEARCH_COL_ID = "_id";
    public static final String SEARCH_QUERY_TEXT = "QUERY_TEXT";
    public static final String[] SEARCH_COLUMNS = {SEARCH_COL_ID, SEARCH_QUERY_TEXT};
    public static final String CREATE_SEARCH_LIST_TABLE =
            "CREATE TABLE " + SEARCH_LIST_TABLE_NAME + "(" +
                    SEARCH_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SEARCH_QUERY_TEXT + " TEXT)";

    public static final String RESULTS_COL_ID = "_id";
    public static final String RESULTS_SEARCH_ID = "SEARCH_ID"; // foreign key to search table
    public static final String RESULTS_BEER_NAME = "BEER_NAME";
    public static final String[] RESULTS_COLUMNS = {RESULTS_COL_ID, RESULTS_BEER_NAME};
    public static final String CREATE_RESULTS_LIST_TABLE =
            "CREATE TABLE " + RESULTS_LIST_TABLE_NAME + "(" +
                    RESULTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RESULTS_SEARCH_ID + " INTEGER, " +
                    RESULTS_BEER_NAME + " TEXT)";

    private static QuickTapSQLiteOpenHelper mInstance;

    public static QuickTapSQLiteOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new QuickTapSQLiteOpenHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    QuickTapSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BEER_LIST_TABLE);
        db.execSQL(CREATE_SEARCH_LIST_TABLE);
        db.execSQL(CREATE_RESULTS_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BEER_LIST_TABLE_NAME);
        this.onCreate(db);
    }

    public Cursor getBeerList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BEER_LIST_TABLE_NAME, // a. table
                BEER_COLUMNS, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                COL_BEER_COUNT+" DESC", // g. order by
                null); // h. limit
        return cursor;
    }

    public String getNameById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BEER_LIST_TABLE_NAME, // a. table
                new String[]{COL_BEER_NAME}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_BEER_NAME));
        } else {
            return "Description not found";
        }
    }

    public int getCountByName(String beerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BEER_LIST_TABLE_NAME, // a. table
                new String[]{COL_BEER_COUNT}, // b. column names
                COL_BEER_NAME + " = ?", // c. selections
                new String[]{beerName}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(COL_BEER_COUNT));
        } else {
            return 0;
        }
    }

    public String getIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BEER_LIST_TABLE_NAME, // a. table
                new String[]{COL_ID}, // b. column names
                COL_BEER_NAME + " = ?", // c. selections
                new String[]{name}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_BEER_COUNT));
        } else {
            return "Description not found";
        }
    }

    public Cursor searchBeerList(String query) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(BEER_LIST_TABLE_NAME, // a. table
                BEER_COLUMNS, // b. column names
                COL_BEER_NAME + " LIKE ?", // c. selections
                new String[]{"%" + query + "%"}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        return cursor;
    }

    public void updateDrinkCount(int drinkCount, String beerName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = ("UPDATE " + BEER_LIST_TABLE_NAME + " SET " + COL_BEER_COUNT + " = " + drinkCount + " WHERE " + COL_BEER_NAME + " = \"" + beerName + "\"");
        db.execSQL(command);
    }

    public long addDrink(String name, String count) {
        ContentValues values = new ContentValues();
        values.put(COL_BEER_NAME, name);
        values.put(COL_BEER_COUNT, count);
        SQLiteDatabase db = this.getWritableDatabase();
        long returnId = db.insert(BEER_LIST_TABLE_NAME, null, values);
        db.close();
        return returnId;
    }

    public void removeDrink(String name) {
        String selection = "BEER_NAME=  ?";
        String[] selectionArgs = {name};
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("BEER_LIST", selection, selectionArgs);
    }

    public long addSearch(String queryText) {
        ContentValues values = new ContentValues();
        values.put(SEARCH_QUERY_TEXT, queryText);
        SQLiteDatabase db = this.getWritableDatabase();
        long returnId = db.insert(SEARCH_LIST_TABLE_NAME, null, values);
        Log.d(TAG, "addSearch: " + queryText + " saved at id " + returnId);
        db.close();
        return returnId;
    }

    public Cursor getSearches() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(SEARCH_LIST_TABLE_NAME, SEARCH_COLUMNS, null, null, null, null, SEARCH_COL_ID);
    }

    public void deleteSearch(int searchId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SEARCH_LIST_TABLE_NAME, SEARCH_COL_ID + " = ?", new String[]{String.valueOf(searchId)});
        db.close();
    }

    public long addResult(int searchId, String beerName) {
        ContentValues values = new ContentValues();
        values.put(RESULTS_SEARCH_ID, searchId);
        values.put(RESULTS_BEER_NAME, beerName);
        SQLiteDatabase db = getWritableDatabase();
        long returnId = db.insert(RESULTS_LIST_TABLE_NAME, null, values);
        Log.d(TAG, "addResult: " + beerName + " saved at id " + returnId);
        db.close();
        return returnId;
    }

    public Cursor getResultsBySearchId(int searchId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(RESULTS_LIST_TABLE_NAME, RESULTS_COLUMNS,
                RESULTS_SEARCH_ID + " = ?",
                new String[]{String.valueOf(searchId)},
                null, null, RESULTS_COL_ID);
    }

    public void deleteResultsBySearchId(int searchId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(RESULTS_LIST_TABLE_NAME, RESULTS_SEARCH_ID + " = ?", new String[]{String.valueOf(searchId)});
        db.close();
    }
}

package com.example.android.quicktap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ShowMe on 3/12/16.
 */
public class QuickTapSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = QuickTapSQLiteOpenHelper.class.getCanonicalName();

    private static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "QUICKTAP_DB";
    public static final String BEER_LIST_TABLE_NAME = "BEER_LIST";

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
                null, // g. order by
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

    public String getCountById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BEER_LIST_TABLE_NAME, // a. table
                new String[]{COL_BEER_COUNT}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
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

    public String getIdByName(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BEER_LIST_TABLE_NAME, // a. table
                new String[]{COL_ID}, // b. column names
                COL_BEER_NAME + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
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

    public void updateDrinkCount(int drinkCount, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = ("UPDATE " + BEER_LIST_TABLE_NAME + " SET " + COL_BEER_COUNT + " = " + drinkCount + " WHERE " + COL_ID + " = " + id);
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
}

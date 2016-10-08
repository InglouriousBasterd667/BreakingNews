package com.example.awfulman.breakingnews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by mikhaillyapich on 02.10.16.
 */

public class NewsDB {
    private final Context mCtx;

    private DataBaseHelper mDBHelper;
    private SQLiteDatabase mDB;
    private String category;
    public NewsDB(Context ctx){
        mCtx = ctx;
    }

    public void open(String category) {
        mDBHelper = new DataBaseHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
        this.category = "'"+ category + "'";
    }

    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    public Cursor getDataByCategory(){
        return mDB.query(DataBaseHelper.DataBaseEntry.DATABASE_TABLE, null, DataBaseHelper.DataBaseEntry.CATEGORY_COLUMN + " = " + category, null, null, null, null);
    }

    public void addRec(String ctg, String title, String text, String date, int img) {
        ContentValues cv = new ContentValues();
        cv.put(DataBaseHelper.DataBaseEntry.CATEGORY_COLUMN, ctg);
        cv.put(DataBaseHelper.DataBaseEntry.TITLE_COLUMN, title);
        cv.put(DataBaseHelper.DataBaseEntry.TEXT_COLUMN, text);
        cv.put(DataBaseHelper.DataBaseEntry.DATE_COLUMN, date);
        cv.put(DataBaseHelper.DataBaseEntry.IMG_COLUMN, img);
        mDB.insert(DataBaseHelper.DataBaseEntry.DATABASE_TABLE, null, cv);
    }
}

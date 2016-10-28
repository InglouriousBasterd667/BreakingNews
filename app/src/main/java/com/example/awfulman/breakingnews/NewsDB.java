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
        this.category = "'%"+ category + "%'";
    }
    public void delete_by_id(int id){
        mDB.delete(DataBaseHelper.DataBaseNewsEntry.DATABASE_TABLE, DataBaseHelper.DataBaseNewsEntry._ID + " = " + id, null);
    }
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    public Cursor getDataByCategory(){
        String orderBy = DataBaseHelper.DataBaseNewsEntry.TITLE_COLUMN;
        if(category.equals("'%All%'")){
            return mDB.rawQuery("Select " + DataBaseHelper.DataBaseNewsEntry.IMG_COLUMN
                                + ", (" + DataBaseHelper.DataBaseNewsEntry.TITLE_COLUMN + " || " + "\"-\" || "
                                + "news." + DataBaseHelper.DataBaseNewsEntry.CATEGORY_COLUMN + ") as title, "
                                + DataBaseHelper.DataBaseNewsEntry.TEXT_COLUMN + ", "
                                + "news." + DataBaseHelper.DataBaseNewsEntry._ID
                                + ", " + DataBaseHelper.DataBaseNewsEntry.DATE_COLUMN + "  From " + DataBaseHelper.DataBaseNewsEntry.DATABASE_TABLE
                                + " inner join " + DataBaseHelper.DataBaseCategoriesEntry.DATABASE_TABLE +" on news.category = categories.category"
                                + " order by " + orderBy, null);
        }

        return mDB.query(DataBaseHelper.DataBaseNewsEntry.DATABASE_TABLE, null, DataBaseHelper.DataBaseNewsEntry.CATEGORY_COLUMN + " like " + category, null, null, null, orderBy);

    }

    public void addRec(String ctg, String title, String text, String date, int img) {
        ContentValues cv = new ContentValues();
        cv.put(DataBaseHelper.DataBaseNewsEntry.CATEGORY_COLUMN, ctg);
        cv.put(DataBaseHelper.DataBaseNewsEntry.TITLE_COLUMN, title);
        cv.put(DataBaseHelper.DataBaseNewsEntry.TEXT_COLUMN, text);
        cv.put(DataBaseHelper.DataBaseNewsEntry.DATE_COLUMN, date);
        cv.put(DataBaseHelper.DataBaseNewsEntry.IMG_COLUMN, img);
        mDB.insert(DataBaseHelper.DataBaseNewsEntry.DATABASE_TABLE, null, cv);
    }
}

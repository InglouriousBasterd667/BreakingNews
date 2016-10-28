package com.example.awfulman.breakingnews;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.StringBuilderPrinter;

/**
 * Created by mikhaillyapich on 01.10.16.
 */

public class DataBaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DataBaseNewsEntry implements BaseColumns{
        public static final String DATABASE_TABLE = "news";
        public static final String CATEGORY_COLUMN = "category";
        public static final String IMG_COLUMN = "image";
        public static final String TITLE_COLUMN = "title";
        public static final String TEXT_COLUMN = "text";
        public static final String DATE_COLUMN = "data";
    }

    public static abstract class DataBaseCategoriesEntry implements BaseColumns{
        public static final String DATABASE_TABLE = "categories";
        public static final String CATEGORY_COLUMN = "category";
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }



    private static final String DATABASE_NEWS_CREATE_SCRIPT = "create table if not exists "
            + DataBaseNewsEntry.DATABASE_TABLE + " (" + DataBaseNewsEntry._ID
            + " integer primary key autoincrement, " + DataBaseNewsEntry.CATEGORY_COLUMN
            + " text not null, " + DataBaseNewsEntry.IMG_COLUMN + " text, " + DataBaseNewsEntry.TITLE_COLUMN + " text, " + DataBaseNewsEntry.TEXT_COLUMN + " text, "
            + DataBaseNewsEntry.DATE_COLUMN + " text);";

    private static final String DATABASE_CATEGORIES_CREATE_SCRIPT = "create table if not exists "
            + DataBaseCategoriesEntry.DATABASE_TABLE + " (" + DataBaseCategoriesEntry._ID
            + " integer primary key autoincrement, " + DataBaseCategoriesEntry.CATEGORY_COLUMN + " text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + DataBaseNewsEntry.DATABASE_TABLE);
            db.execSQL(DATABASE_NEWS_CREATE_SCRIPT);

            db.execSQL("DROP TABLE IF EXISTS " + DataBaseCategoriesEntry.DATABASE_TABLE);
            db.execSQL(DATABASE_CATEGORIES_CREATE_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        db.execSQL("DROP TABLE IF IT EXISTS " + DataBaseNewsEntry.DATABASE_TABLE);
        onCreate(db);
    }
}

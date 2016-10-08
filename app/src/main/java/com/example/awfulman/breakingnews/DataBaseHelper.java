package com.example.awfulman.breakingnews;

import android.content.Context;
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

    public static abstract class DataBaseEntry implements BaseColumns{
        public static final String DATABASE_TABLE = "news";
        public static final String CATEGORY_COLUMN = "category";
        public static final String IMG_COLUMN = "image";
        public static final String TITLE_COLUMN = "title";
        public static final String TEXT_COLUMN = "text";
        public static final String DATE_COLUMN = "data";
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DataBaseEntry.DATABASE_TABLE + " (" + DataBaseEntry._ID
            + " integer primary key autoincrement, " + DataBaseEntry.CATEGORY_COLUMN
            + " text not null, " + DataBaseEntry.IMG_COLUMN + " integer, " + DataBaseEntry.TITLE_COLUMN + " text, " + DataBaseEntry.TEXT_COLUMN + " text, "
            + DataBaseEntry.DATE_COLUMN + " text);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseEntry.DATABASE_TABLE);
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + DataBaseEntry.DATABASE_TABLE);
        // Создаём новую таблицу
        onCreate(db);
    }
}

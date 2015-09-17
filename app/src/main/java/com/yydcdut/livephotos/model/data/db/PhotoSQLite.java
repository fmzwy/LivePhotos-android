package com.yydcdut.livephotos.model.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuyidong on 15/9/10.
 */
public class PhotoSQLite extends SQLiteOpenHelper {
    public static final String TABLE_SANDBOX = "sandbox";
    public static final String TABLE_GALLERY = "gallery";

    public PhotoSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_SANDBOX + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data BLOB NOT NULL, " +
                "time LONG NOT NULL, " +
                "width INTEGER NOT NULL, " +
                "height INTEGER NOT NULL, " +
                "belong LONG NOT NULL);";
        db.execSQL(sql);
        String sql_gallery = "create table " + TABLE_GALLERY + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "belong LONG NOT NULL);";
        db.execSQL(sql_gallery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

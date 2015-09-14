package com.yydcdut.livephotos.model.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuyidong on 15/9/10.
 */
public class SandSQLite extends SQLiteOpenHelper {
    public static final String TABLE = "sandbox";

    public SandSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data BLOB NOT NULL, " +
                "time LONG NOT NULL, " +
                "width INTEGER NOT NULL, " +
                "height INTEGER NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

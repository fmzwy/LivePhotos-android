package com.yydcdut.livephotos.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.livephotos.LiveApplication;
import com.yydcdut.livephotos.model.data.SandSQLite;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;
import com.yydcdut.livephotos.model.data.utils.DatabaseContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/9/10.
 */
public class SandBoxDB {
    private static final String NAME = "SandBox.db";
    private static final int VERSION = 1;
    private SandSQLite mSandSQLite;

    private static SandBoxDB sInstance = new SandBoxDB();

    private SandBoxDB() {
        DatabaseContext dbContext = new DatabaseContext(LiveApplication.getContext());
        mSandSQLite = new SandSQLite(dbContext, NAME, null, VERSION);
    }

    public static SandBoxDB getInstance() {
        return sInstance;
    }


    /**
     * 查询
     *
     * @return
     */
    public List<SandPhoto> findAll() {
        List<SandPhoto> sandPhotoList = new ArrayList<>();
        SQLiteDatabase db = mSandSQLite.getReadableDatabase();
        Cursor cursor = db.query(SandSQLite.TABLE, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            byte[] data = cursor.getBlob(cursor.getColumnIndex("data"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            int width = cursor.getInt(cursor.getColumnIndex("width"));
            int height = cursor.getInt(cursor.getColumnIndex("height"));
            SandPhoto sandPhoto = new SandPhoto(id, data, width, height, time);
            sandPhotoList.add(sandPhoto);
        }
        cursor.close();
        db.close();
        return sandPhotoList;
    }

    /**
     * 查询
     *
     * @return
     */
    public List<SandPhoto> findByTime(long needTime) {
        long firstTime = needTime - 2000;
        long lastTime = needTime + 2000;
        List<SandPhoto> sandPhotoList = new ArrayList<>();
        SQLiteDatabase db = mSandSQLite.getReadableDatabase();
        Cursor cursor = db.query(SandSQLite.TABLE, null,
                "time >= ?  AND time <= ? ",
                new String[]{firstTime + "", lastTime + ""},
                null, null,
                "time asc");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            byte[] data = cursor.getBlob(cursor.getColumnIndex("data"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            int width = cursor.getInt(cursor.getColumnIndex("width"));
            int height = cursor.getInt(cursor.getColumnIndex("height"));
            SandPhoto sandPhoto = new SandPhoto(id, data, width, height, time);
            sandPhotoList.add(sandPhoto);
        }
        cursor.close();
        db.close();
        return sandPhotoList;
    }

    /**
     * 保存
     *
     * @param sandPhoto
     * @return
     */
    public long save(SandPhoto sandPhoto) {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", sandPhoto.data);
        contentValues.put("time", sandPhoto.time);
        contentValues.put("width", sandPhoto.width);
        contentValues.put("height", sandPhoto.height);
        long id = db.insert(SandSQLite.TABLE, null, contentValues);
        db.close();
        return id;
    }

    /**
     * 删除
     *
     * @param sandPhoto
     * @return
     */
    public int delete(SandPhoto sandPhoto) {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        int rows = db.delete(SandSQLite.TABLE, "_id = ?", new String[]{sandPhoto.getId() + ""});
        db.close();
        return rows;
    }

    /**
     * 删除全部
     *
     * @return
     */
    public int deleteAll() {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        int rows = db.delete(SandSQLite.TABLE, null, null);
        db.close();
        return rows;
    }

    public int deleteByTime(long small, long big) {
        SQLiteDatabase db = mSandSQLite.getWritableDatabase();
        int rows = db.delete(SandSQLite.TABLE, "time < ? AND time > ?", new String[]{big + "", small + ""});
        db.close();
        return rows;
    }
}

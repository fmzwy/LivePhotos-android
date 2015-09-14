package com.yydcdut.livephotos.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.livephotos.model.data.PhotoSQLite;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/9/10.
 */
public class SandBoxDB extends BaseSQLite {

    private static SandBoxDB sInstance = new SandBoxDB();

    private SandBoxDB() {
        super();
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
        SQLiteDatabase db = mPhotoSQLite.getReadableDatabase();
        Cursor cursor = db.query(PhotoSQLite.TABLE_SANDBOX, null, null, null, null, null, null);
        List<SandPhoto> sandPhotoList = new ArrayList<>(cursor.getCount());
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
    public List<SandPhoto> find(long belong) {
        SQLiteDatabase db = mPhotoSQLite.getReadableDatabase();
        Cursor cursor = db.query(PhotoSQLite.TABLE_SANDBOX, null,
                "belong = ? ",
                new String[]{belong + ""},
                null, null,
                "time asc");
        List<SandPhoto> sandPhotoList = new ArrayList<>(cursor.getCount());
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
    public long save(SandPhoto sandPhoto, long belong) {
        SQLiteDatabase db = mPhotoSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", sandPhoto.data);
        contentValues.put("time", sandPhoto.time);
        contentValues.put("width", sandPhoto.width);
        contentValues.put("height", sandPhoto.height);
        contentValues.put("belong", belong);
        long id = db.insert(PhotoSQLite.TABLE_SANDBOX, null, contentValues);
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
        SQLiteDatabase db = mPhotoSQLite.getWritableDatabase();
        int rows = db.delete(PhotoSQLite.TABLE_SANDBOX, "_id = ?", new String[]{sandPhoto.getId() + ""});
        db.close();
        return rows;
    }

    /**
     * 删除全部
     *
     * @return
     */
    public int deleteAll() {
        SQLiteDatabase db = mPhotoSQLite.getWritableDatabase();
        int rows = db.delete(PhotoSQLite.TABLE_SANDBOX, null, null);
        db.close();
        return rows;
    }

    public SandPhoto getCenterSandPhoto(long belong) {
        SandPhoto sandPhoto = null;
        SQLiteDatabase db = mPhotoSQLite.getReadableDatabase();
        Cursor cursor = db.query(PhotoSQLite.TABLE_SANDBOX, new String[]{"count(*)", "_id"}, "belong = ?", new String[]{belong + ""}, null, null, null);
        long id = -1;
        int count = -1;
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            count = cursor.getInt(cursor.getColumnIndex("count(*)"));
        }
        cursor.close();
        if (id != -1 && count != -1) {
            Cursor cursorSingle = db.query(PhotoSQLite.TABLE_SANDBOX, null, "_id = ?", new String[]{(id - count / 2) + ""}, null, null, null);
            while (cursorSingle.moveToNext()) {
                byte[] data = cursorSingle.getBlob(cursorSingle.getColumnIndex("data"));
                long time = cursorSingle.getLong(cursorSingle.getColumnIndex("time"));
                int width = cursorSingle.getInt(cursorSingle.getColumnIndex("width"));
                int height = cursorSingle.getInt(cursorSingle.getColumnIndex("height"));
                sandPhoto = new SandPhoto(id, data, width, height, time);
            }
            cursorSingle.close();
        } else {
            throw new IllegalArgumentException("没有找到!!");
        }
        db.close();
        return sandPhoto;
    }

}

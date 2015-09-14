package com.yydcdut.livephotos.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yydcdut.livephotos.model.data.PhotoSQLite;
import com.yydcdut.livephotos.model.data.bean.GalleryPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/9/14.
 */
public class GalleryDB extends BaseSQLite {
    private static GalleryDB sInstance = new GalleryDB();

    private GalleryDB() {
        super();
    }

    public static GalleryDB getInstance() {
        return sInstance;
    }

    /**
     * 查询
     *
     * @return
     */
    public List<GalleryPhoto> findAll() {
        SQLiteDatabase db = mPhotoSQLite.getReadableDatabase();
        Cursor cursor = db.query(PhotoSQLite.TABLE_GALLERY, null, null, null, null, null, null);
        List<GalleryPhoto> galleryPhotoList = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            long belong = cursor.getLong(cursor.getColumnIndex("belong"));
            GalleryPhoto galleryPhoto = new GalleryPhoto(id, belong);
            galleryPhotoList.add(galleryPhoto);
        }
        cursor.close();
        db.close();
        return galleryPhotoList;
    }

    /**
     * 保存
     *
     * @param belong
     * @return
     */
    public long save(long belong) {
        SQLiteDatabase db = mPhotoSQLite.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("belong", belong);
        long id = db.insert(PhotoSQLite.TABLE_GALLERY, null, contentValues);
        db.close();
        return id;
    }

}

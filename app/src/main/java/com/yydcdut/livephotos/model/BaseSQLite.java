package com.yydcdut.livephotos.model;

import com.yydcdut.livephotos.LiveApplication;
import com.yydcdut.livephotos.model.data.PhotoSQLite;
import com.yydcdut.livephotos.model.data.utils.DatabaseContext;

/**
 * Created by yuyidong on 15/9/14.
 */
abstract class BaseSQLite {
    private static final String NAME = "photo.db";
    private static final int VERSION = 1;
    protected PhotoSQLite mPhotoSQLite;

    protected BaseSQLite() {
        DatabaseContext dbContext = new DatabaseContext(LiveApplication.getContext());
        mPhotoSQLite = new PhotoSQLite(dbContext, NAME, null, VERSION);
    }

}

package com.yydcdut.livephotos;

import android.app.Application;
import android.content.Context;

import com.yydcdut.livephotos.utils.FileManager;

/**
 * Created by yuyidong on 15/9/10.
 */
public class LiveApplication extends Application {
    private static Context mContext;

    public LiveApplication() {
        mContext = this;
        FileManager.init();
    }

    public static Context getContext() {
        return mContext;
    }

}

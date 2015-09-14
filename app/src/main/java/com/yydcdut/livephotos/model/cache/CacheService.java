package com.yydcdut.livephotos.model.cache;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yydcdut.livephotos.model.ICameraBinder;
import com.yydcdut.livephotos.model.SandBoxDB;
import com.yydcdut.livephotos.model.cache.structure.CacheQueue;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;

import java.util.List;

/**
 * Created by yuyidong on 15/9/11.
 */
public class CacheService extends Service implements CacheQueue.OnDataCacheFinishListener<SandPhoto> {
    private CacheQueue<SandPhoto> mCacheQueue;
    private int mPreviewWidth;
    private int mPreviewHeight;

    @Override
    public IBinder onBind(Intent intent) {
        return new CameraBinder();
    }

    public class CameraBinder extends Binder implements ICameraBinder {
        @Override
        public void add(byte[] data, long time) {
            if (mCacheQueue == null || mPreviewWidth == 0 || mPreviewHeight == 0) {
                throw new IllegalArgumentException("应该先执行 init()");
            }
            SandPhoto sandPhoto = new SandPhoto(SandPhoto.ID_NONE, data, mPreviewWidth, mPreviewHeight, time);
            mCacheQueue.add(sandPhoto);
        }

        @Override
        public void capture(long time) {
            mCacheQueue.wannaSaveData(time);
        }

        @Override
        public void init(long delta, int width, int height) {
            //计算每秒会产生多少帧
            if (delta > 1000) {
                throw new IllegalArgumentException("每帧数据应该小于1s");
            }
            int frames = (int) (1000 / delta);
            mCacheQueue = new CacheQueue<>(frames);
            mCacheQueue.setOnDataCacheFinishListener(CacheService.this);
            mPreviewWidth = width;
            mPreviewHeight = height;
        }
    }

    @Override
    public void onFinish(final List<SandPhoto> list, final long belong) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (SandPhoto sandPhoto : list) {
                    SandBoxDB.getInstance().save(sandPhoto, belong);
                }
            }
        }).start();
    }

}

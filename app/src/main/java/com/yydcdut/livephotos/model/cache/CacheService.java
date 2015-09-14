package com.yydcdut.livephotos.model.cache;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yydcdut.livephotos.model.ICameraBinder;
import com.yydcdut.livephotos.model.data.bean.ITimer;

/**
 * Created by yuyidong on 15/9/11.
 */
public class CacheService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new CameraBinder();
    }

    public class CameraBinder extends Binder implements ICameraBinder {
        @Override
        public void add(byte[] data, int width, int height, long time) {
            CachePhoto cachePhoto = new CachePhoto(data, width, height, time);
        }

        @Override
        public void capture(long time) {
        }

        @Override
        public void setFrameDelta(long delta) {

        }

    }


    class CachePhoto implements ITimer {
        final public byte[] data;
        final public int width;
        final public int height;
        final public long time;

        public CachePhoto(byte[] data, int width, int height, long time) {
            this.data = data;
            this.width = width;
            this.height = height;
            this.time = time;
        }

        @Override
        public long getTime() {
            return time;
        }
    }
}

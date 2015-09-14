package com.yydcdut.livephotos.model.local;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yydcdut.livephotos.IMake;
import com.yydcdut.livephotos.model.ICameraBinder;
import com.yydcdut.livephotos.model.SandBoxDB;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yuyidong on 15/9/10.
 */
public class CameraService extends Service {
    private boolean mGotoStop = false;
    private byte[] mSaveObject = new byte[1];
    private Queue<SandPhoto> mSaveQueue;

    private IMake mPhotoService;
    private boolean mIsBind = false;

    @Override
    public IBinder onBind(Intent intent) {
        mSaveQueue = new LinkedList<>();
        new Thread(new SavePhotoRunnable()).start();
        bindService(getApplicationContext());
        return new CameraBinder();
    }

    /**
     * 存图线程
     */
    class SavePhotoRunnable implements Runnable {

        @Override
        public void run() {
            while (!mGotoStop) {
                SandPhoto sandPhoto = mSaveQueue.poll();
                Log.i("yuyidong", "mSaveQueue---->" + mSaveQueue.size());
                if (sandPhoto == null) {
                    synchronized (mSaveObject) {
                        try {
                            mSaveObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    savePhoto(sandPhoto);
                }
            }
        }
    }

    private void savePhoto(SandPhoto sandPhoto) {
        SandBoxDB.getInstance().save(sandPhoto);
        SandBoxDB.getInstance().deleteByTime(sandPhoto.time - 3300, sandPhoto.time - 3000);
    }


    public class CameraBinder extends Binder implements ICameraBinder {
        @Override
        public void add(byte[] data, int width, int height, long time) {
            SandPhoto sandPhoto = new SandPhoto(-1, data, width, height, time);
            mSaveQueue.offer(sandPhoto);
            synchronized (mSaveObject) {
                mSaveObject.notifyAll();
            }
        }

        @Override
        public void capture(long time) {
            //todo 自己也保存一张图下来
            try {
                mPhotoService.make(time);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setFrameDelta(long delta) {

        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mSaveQueue.size() != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (mSaveQueue.size() != 0) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
                    }
                    mGotoStop = true;
                }
            }).start();
        }


        unBindService(getApplicationContext());
        return super.onUnbind(intent);
    }

    private void bindService(Context context) {
        Intent intent = new Intent(context, PhotoService.class);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPhotoService = IMake.Stub.asInterface(service);
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPhotoService = null;
            mIsBind = false;
        }
    };

    private void unBindService(Context context) {
        if (mIsBind) {
            context.unbindService(mServiceConnection);
            mIsBind = false;
        }
    }

}

package com.yydcdut.livephotos.model.local;

import android.app.Service;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yydcdut.livephotos.IMake;
import com.yydcdut.livephotos.model.SandBoxDB;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;
import com.yydcdut.livephotos.utils.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by yuyidong on 15/9/11.
 */
public class PhotoService extends Service {
    private boolean mGotoStop = false;
    private byte[] mMakeObject = new byte[1];
    private Queue<Progress> mProgressQueue;
    private boolean mIsCurrentProgressFinished = true;

    @Override
    public IBinder onBind(Intent intent) {
        mProgressQueue = new LinkedList<>();
        new Thread(new MakePhotoRunnable()).start();
        return mStub;
    }

    private String initDir(String dirName) {
        String dirPath;
        File dir;
        dirPath = FileManager.getAppDir() + dirName + File.separator + "";
        dir = new File(dirPath);
        dir.mkdir();
        return dirPath;
    }


    IMake.Stub mStub = new IMake.Stub() {
        @Override
        public void make(long time) throws RemoteException {
            mProgressQueue.add(new Progress(time, initDir(System.currentTimeMillis() + "")));
            //先拍一张照
            synchronized (mMakeObject) {
                mMakeObject.notifyAll();
            }
        }
    };

    class MakePhotoRunnable implements Runnable {

        @Override
        public void run() {
            while (!mGotoStop) {
                Progress progress = mProgressQueue.poll();
                if (progress != null && !progress.isProgressOver()) {
                    Log.i("yuyidong", "PhotoService   in");
                    mIsCurrentProgressFinished = false;
                    List<SandPhoto> sandPhotoList = SandBoxDB.getInstance().findByTime(progress.timeAdd());
                    Log.i("yuyidong", "PhotoService   sandPhotoList---->" + sandPhotoList.size());
//                    //todo 数据还没有存入数据库，但是作图这边就已经从数据库中读这个时间段的数据了
                    if (sandPhotoList == null || sandPhotoList.size() == 0) {
                        wait4Data(progress);
                    }
                    sandPhotoList = SandBoxDB.getInstance().findByTime(progress.getTime());
                    for (SandPhoto sandPhoto : sandPhotoList) {
                        makePhoto(sandPhoto, progress.getDir());
                    }
                } else {
                    Log.i("yuyidong", "PhotoService   wait");
                    mIsCurrentProgressFinished = true;
                    synchronized (mMakeObject) {
                        try {
                            mMakeObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private void wait4Data(Progress progress) {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<SandPhoto> sandPhotoList = SandBoxDB.getInstance().findByTime(progress.getTime());
                if (sandPhotoList.size() != 0) {
                    break;
                }
            }
        }
    }


    private void makePhoto(SandPhoto sandPhoto, String dir) {
        File file = new File(dir + sandPhoto.time + ".jpg");
        OutputStream outputStream = null;
        Log.i("yuyidong", "makePhoto   begin");
        try {
            outputStream = new FileOutputStream(file);
            YuvImage yuvImage = new YuvImage(sandPhoto.data, ImageFormat.NV21, sandPhoto.width, sandPhoto.height, null);
            yuvImage.compressToJpeg(new Rect(0, 0, sandPhoto.width, sandPhoto.height), 90, outputStream);
            Log.i("yuyidong", "makePhoto   file--->" + dir + sandPhoto.time + ".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("yuyidong", "makePhoto   FileNotFoundException");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("yuyidong", "makePhoto   end");
        }
    }


    private class Progress {
        /**
         * 前后1秒的照片
         */
        private static final long DURATION_DELTA = 1000l;
        /**
         * 时间间隔，100ms，最多3张照片
         */
        private static final long DURATION = 500l;
        private final long firstTime;
        private final long lastTime;
        private long nowTime;
        private boolean isOver = false;
        private String dir;

        public Progress(long captureTime, String dir) {
            this.dir = dir;
            this.firstTime = captureTime - DURATION_DELTA - DURATION;
            this.lastTime = captureTime + DURATION_DELTA;
            nowTime = firstTime;
        }

        public long timeAdd() {
            nowTime += DURATION;
            if (nowTime >= lastTime) {
                isOver = true;
            }
            return nowTime;
        }

        public boolean isProgressOver() {
            return isOver;
        }

        public long getTime() {
            return nowTime;
        }

        public String getDir() {
            return dir;
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mProgressQueue.size() == 0 && mIsCurrentProgressFinished) {
            exitService();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mProgressQueue.size() == 0 && mIsCurrentProgressFinished) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    exitService();
                }
            }).start();
        }
        return super.onUnbind(intent);
    }

    private void exitService() {
        mGotoStop = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 2000);
    }
}

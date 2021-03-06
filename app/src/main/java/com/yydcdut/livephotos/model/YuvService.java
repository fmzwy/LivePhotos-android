package com.yydcdut.livephotos.model;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yydcdut.livephotos.IMake;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;
import com.yydcdut.livephotos.model.data.db.SandBoxDB;
import com.yydcdut.livephotos.utils.Blur;
import com.yydcdut.livephotos.utils.Const;
import com.yydcdut.livephotos.utils.FileManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by yuyidong on 15/9/14.
 */
public class YuvService extends Service {
    private static final int MAX_YUV_NUMBER = 5;

    private boolean mGotoStop = false;
    private byte[] mMakeObject = new byte[1];

    private Queue<Progress> mProgressQueue;

    @Override
    public IBinder onBind(Intent intent) {
        mProgressQueue = new LinkedList<>();
        new Thread(new MakePhotoRunnable()).start();
        return mStub;
    }

    class MakePhotoRunnable implements Runnable {

        @Override
        public void run() {
            while (!mGotoStop) {
                Progress progress = mProgressQueue.poll();
                if (progress != null) {
                    long belong = progress.belong;
                    String path = progress.path;
                    SandPhoto centerPhoto = SandBoxDB.getInstance().getCenterSandPhoto(belong);
                    //每个progress做5张图
                    List<SandPhoto> sandPhotos = SandBoxDB.getInstance().find(belong, progress.from, MAX_YUV_NUMBER);
                    Log.i("yuyidong", "MakePhotoRunnable  " + sandPhotos.size());
                    for (SandPhoto sandPhoto : sandPhotos) {
                        makePhoto(sandPhoto, path);
                        if (sandPhoto.time == centerPhoto.time) {
                            doBlur(centerPhoto, path);
                        }
                    }
                    sandPhotos.clear();
                    sandPhotos = null;
                } else {
                    sendBroadcast(new Intent(Const.ACTION));
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
    }

    private void makePhoto(SandPhoto sandPhoto, String dir) {
        File file = new File(dir + sandPhoto.time + ".jpg");
        ByteArrayOutputStream byteArrayOutputStream = null;
        BufferedOutputStream bos = null;
        Matrix matrix = new Matrix();
        matrix.setRotate(90f);
        try {
            YuvImage yuvImage = new YuvImage(sandPhoto.data, ImageFormat.NV21, sandPhoto.width, sandPhoto.height, null);
            byteArrayOutputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, sandPhoto.width, sandPhoto.height), 90, byteArrayOutputStream);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
            Bitmap newBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);// 将图片压缩到流中
            bos.flush();// 输出
            Log.i("yuyidong", "makePhoto   end--->" + sandPhoto.getId());
        } catch (FileNotFoundException e) {
            Log.i("yuyidong", "makePhoto   FileNotFoundException--->" + sandPhoto.getId());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("yuyidong", "makePhoto   IOException--->" + sandPhoto.getId());
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doBlur(SandPhoto sandPhoto, String dir) {
        File file = new File(dir + "blur.jpg");
        ByteArrayOutputStream byteArrayOutputStream = null;
        BufferedOutputStream bos = null;
        Matrix matrix = new Matrix();
        matrix.setRotate(90f);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(dir + sandPhoto.time + ".jpg");
            Bitmap blurBitmap = Blur.blur(bitmap);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            blurBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);// 将图片压缩到流中
            bos.flush();// 输出
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("yuyidong", "makePhoto  blur   end");
        }
    }

    IMake.Stub mStub = new IMake.Stub() {
        @Override
        public void make(long belong) throws RemoteException {
            int current = 0;
            int total = SandBoxDB.getInstance().getCount(belong);
            while (current < total) {
                Progress progress = new Progress(initDir(belong + ""), belong, current);
                mProgressQueue.offer(progress);
                current = current + MAX_YUV_NUMBER > total ? total : current + MAX_YUV_NUMBER;
            }
            //先拍一张照
            synchronized (mMakeObject) {
                mMakeObject.notifyAll();
            }
        }
    };

    private String initDir(String dirName) {
        String dirPath = FileManager.getAppDir() + dirName + File.separator + "";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dirPath;
    }

    private class Progress {
        final String path;
        final long belong;
        final int from;

        public Progress(String path, long belong, int from) {
            this.path = path;
            this.belong = belong;
            this.from = from;
        }
    }

}

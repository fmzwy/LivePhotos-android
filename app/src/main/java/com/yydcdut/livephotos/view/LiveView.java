package com.yydcdut.livephotos.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.yydcdut.livephotos.utils.FileManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuyidong on 15/9/15.
 */
public class LiveView extends ImageView implements Handler.Callback, Runnable {
    //注意OOM
    private static final int CACHE_SIZE = 5;
    private Bitmap[] mBitmaps = new Bitmap[CACHE_SIZE];
    private BitmapFactory.Options[] mOptionses = new BitmapFactory.Options[CACHE_SIZE];
    private long mFrames = 0l;

    private int mCurrent = 0;
    private int mTotalFilesNumber = 0;

    private String mDir;
    private String[] mFiles;

    private Handler mHandler;

    private ExecutorService mPool;

    public LiveView(Context context) {
        this(context, null);
    }

    public LiveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPool = Executors.newFixedThreadPool(5);
        mHandler = new Handler(this);
    }

    public void start(long frame) {
        mFrames = frame;
        new Thread(this).start();
    }

    private void doShow(int number) throws ExecutionException, InterruptedException {
        setImageBitmap(mBitmaps[number % 5]);
        Log.i("yuyidong", "mBitmaps  mBitmaps" + (number % 5) + "--->" + mBitmaps[number % 5].toString());
        if (mOptionses[number % 5] == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inBitmap = mBitmaps[number % 5];
            options.inMutable = true;
            options.inSampleSize = 1;
            mOptionses[number % 5] = options;
        }
        mBitmaps[number % 5] = mPool.submit(new BitmapCallable(mOptionses[number % 5], mDir + mFiles[number])).get();
    }

    public void init(String dir) throws ExecutionException, InterruptedException {
        mDir = dir;
        mFiles = FileManager.getNames(dir);
        mTotalFilesNumber = mFiles.length;
        mBitmaps[0] = mPool.submit(new BitmapCallable(null, dir + mFiles[0])).get();
        setImageBitmap(mBitmaps[0]);
        mBitmaps[1] = mPool.submit(new BitmapCallable(null, dir + mFiles[1])).get();
        mBitmaps[2] = mPool.submit(new BitmapCallable(null, dir + mFiles[2])).get();
        mBitmaps[3] = mPool.submit(new BitmapCallable(null, dir + mFiles[3])).get();
        mBitmaps[4] = mPool.submit(new BitmapCallable(null, dir + mFiles[4])).get();
    }

    @Override
    public boolean handleMessage(Message msg) {
        try {
            Log.i("yuyidong", "msg.what--->" + msg.what);
            doShow(msg.what);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        while (mCurrent < mTotalFilesNumber) {
            mHandler.sendEmptyMessage(mCurrent++);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class BitmapCallable implements Callable<Bitmap> {
        private BitmapFactory.Options mOptions;
        private String mPath;

        public BitmapCallable(BitmapFactory.Options options, String path) {
            mOptions = options;
            mPath = path;
        }

        @Override
        public Bitmap call() throws Exception {
            Bitmap bitmap;
            if (mOptions == null) {
                bitmap = BitmapFactory.decodeFile(mPath);
            } else {
                bitmap = BitmapFactory.decodeFile(mPath, mOptions);
            }
            return bitmap;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPool.shutdown();
    }
}

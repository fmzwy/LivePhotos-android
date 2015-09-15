package com.yydcdut.livephotos.controller;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.livephotos.R;
import com.yydcdut.livephotos.model.SandBoxDB;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;
import com.yydcdut.livephotos.utils.FileManager;

import java.io.File;

/**
 * Created by yuyidong on 15/9/14.
 */
public class LivePhotoActivity extends AppCompatActivity implements View.OnTouchListener, Handler.Callback {
    private Drawable[] mDrawables;
    private AnimationDrawable mFrameAnim;

    private static final int STATE_DOWN = 1;
    private static final int STATE_UP = 2;
    private static final int STATE_OTHER = 3;
    private int mState = STATE_OTHER;
    private Handler mStateHandler;

    private ImageView mCenterImage;
    private ImageView mBlurImage;
    private ImageView mLiveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo 进度条
        Bundle bundle = getIntent().getExtras();
        long belong = bundle.getLong("belong");
        setContentView(R.layout.activity_live);
        mStateHandler = new Handler(this);
        mCenterImage = (ImageView) findViewById(R.id.img_center);
        mCenterImage.setOnTouchListener(this);
        mBlurImage = (ImageView) findViewById(R.id.img_blur);
        mLiveImage = (ImageView) findViewById(R.id.img_live);
        String dir = FileManager.getAppDir() + belong + File.separator;
        initCenter(dir, belong);
        initBlur(dir);
        initLive(dir);
    }

    private void initCenter(String dir, long belong) {
        SandPhoto sandPhoto = SandBoxDB.getInstance().getCenterSandPhoto(belong);
        String path = dir + sandPhoto.time + ".jpg";
        ImageLoader.getInstance().displayImage("file:" + File.separator + File.separator + path, mCenterImage);
    }

    private void initBlur(String dir) {
        String path = dir + "blur.jpg";
        ImageLoader.getInstance().displayImage("file:" + File.separator + File.separator + path, mBlurImage);
    }

    private void initLive(String dir) {
        String[] fileNames = FileManager.getNames(dir);
        mDrawables = new Drawable[fileNames.length];
        Log.i("yuyidong", "fileNames.length--->" + fileNames.length);
        mFrameAnim = new AnimationDrawable();
        for (int i = 0; i < mDrawables.length; i++) {
            String path = dir + fileNames[i];
            mDrawables[i] = new BitmapDrawable(ImageLoader.getInstance().loadImageSync("file:" + File.separator + File.separator + path));
            mFrameAnim.addFrame(mDrawables[i], 135);
        }
        mLiveImage.setBackgroundDrawable(mFrameAnim);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("yuyidong", "ACTION_DOWN");
                mState = STATE_DOWN;
                if (!mStateHandler.hasMessages(1)) {
                    mStateHandler.sendEmptyMessageDelayed(1, 1000);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("yuyidong", "ACTION_UP");
                mState = STATE_UP;
                mStateHandler.removeMessages(1);
                break;
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mState == STATE_DOWN) {
            showBlur();
        }
        return false;
    }

    private void showBlur() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mBlurImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLiveImage.setVisibility(View.VISIBLE);
                mFrameAnim.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBlurImage.startAnimation(animation);

    }
}

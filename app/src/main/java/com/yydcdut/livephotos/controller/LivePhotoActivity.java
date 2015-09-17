package com.yydcdut.livephotos.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.livephotos.R;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;
import com.yydcdut.livephotos.model.data.db.SandBoxDB;
import com.yydcdut.livephotos.utils.FileManager;
import com.yydcdut.livephotos.view.LiveView;
import com.yydcdut.livephotos.view.LoadingLayout;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by yuyidong on 15/9/14.
 */
public class LivePhotoActivity extends AppCompatActivity implements View.OnTouchListener, Handler.Callback,
        LiveView.OnLiveFinishedListener, Animation.AnimationListener {
    private static final int STATE_DOWN = 1;
    private static final int STATE_UP = 2;
    private static final int STATE_OTHER = 3;
    private int mState = STATE_OTHER;
    private Handler mHandler;
    private static final int MSG_STATE = 1;

    private ImageView mCenterImage;
    private ImageView mBlurImage;
    private LiveView mLiveImage;
    private LoadingLayout mLoadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo 进度条
        Bundle bundle = getIntent().getExtras();
        long belong = bundle.getLong("belong");
        setContentView(R.layout.activity_live);
        mHandler = new Handler(this);
        mLoadingLayout = (LoadingLayout) findViewById(R.id.layout_loading);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mCenterImage = (ImageView) findViewById(R.id.img_center);
        mCenterImage.setOnTouchListener(this);
        mBlurImage = (ImageView) findViewById(R.id.img_blur);
        mLiveImage = (LiveView) findViewById(R.id.img_live);
        String dir = FileManager.getAppDir() + belong + File.separator;
        initCenter(dir, belong);
        initBlur(dir);
        try {
            initLive(dir);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private void initLive(String dir) throws ExecutionException, InterruptedException {
        mLiveImage.init(dir, new LiveView.OnLiveInitFinishedListener() {
            @Override
            public void onInitFinished() {
                mLoadingLayout.setVisibility(View.GONE);
            }
        });
        mLiveImage.setOnLiveFinishedListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mState = STATE_DOWN;
                if (!mHandler.hasMessages(MSG_STATE)) {
                    mHandler.sendEmptyMessageDelayed(MSG_STATE, 1000);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mState = STATE_UP;
                mHandler.removeMessages(MSG_STATE);
                break;
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_STATE:
                if (mState == STATE_DOWN) {
                    showBlur();
                }
                break;
        }
        return false;
    }

    private int mVisible = VISIBLE_NOTHING;
    private static final int VISIBLE_NOTHING = 0;
    private static final int VISIBLE_SHOW_BLUR = 1;
    private static final int VISIBLE_HIDE_BLUR = 2;
    private static final int VISIBLE_SHOW_LIVE = 3;
    private static final int VISIBLE_HIDE_LIVE = 4;


    private void showBlur() {
        Animation animationBlur = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_in_blur);
        mVisible = VISIBLE_SHOW_BLUR;
        animationBlur.setAnimationListener(this);
        mBlurImage.startAnimation(animationBlur);
    }

    @Override
    public void onFinish() {
        Animation animationLive = AnimationUtils.loadAnimation(LivePhotoActivity.this, R.anim.anim_alpha_out_live);
        mVisible = VISIBLE_HIDE_LIVE;
        animationLive.setAnimationListener(this);
        mLiveImage.startAnimation(animationLive);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        switch (mVisible) {
            case VISIBLE_SHOW_BLUR:
                mBlurImage.setVisibility(View.VISIBLE);
                break;
            case VISIBLE_SHOW_LIVE:
                mLiveImage.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        switch (mVisible) {
            case VISIBLE_SHOW_BLUR:
                Animation animationLive = AnimationUtils.loadAnimation(LivePhotoActivity.this, R.anim.anim_alpha_in_live);
                mVisible = VISIBLE_SHOW_LIVE;
                animationLive.setAnimationListener(this);
                mLiveImage.startAnimation(animationLive);
                break;
            case VISIBLE_SHOW_LIVE:
                mLiveImage.start(80);
                break;
            case VISIBLE_HIDE_LIVE:
                mLiveImage.setVisibility(View.GONE);
                Animation animationBlur = AnimationUtils.loadAnimation(LivePhotoActivity.this, R.anim.anim_alpha_out_live);
                mVisible = VISIBLE_HIDE_BLUR;
                animationBlur.setAnimationListener(this);
                mBlurImage.startAnimation(animationBlur);
                try {
                    mLiveImage.reset();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case VISIBLE_HIDE_BLUR:
                mBlurImage.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}

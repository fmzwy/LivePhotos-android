package com.yydcdut.livephotos.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.yydcdut.livephotos.R;
import com.yydcdut.livephotos.model.ICameraBinder;
import com.yydcdut.livephotos.model.cache.CacheService;

/**
 * Created by yuyidong on 15/9/11.
 */
public class APIBelow14SurfaceActivity extends CameraSurfaceActivity implements View.OnClickListener, PreviewCallback {
    private ICameraBinder mCameraBinder;
    private boolean mIsBind = false;
    private Camera.Size mPreviewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.fab_capture).setOnClickListener(this);
        findViewById(R.id.btn_gallery).setOnClickListener(this);
        findViewById(R.id.btn_setting).setOnClickListener(this);
        if (!mIsBind) {
            Intent intent = new Intent(this, CacheService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    void startPreview(Camera.Size size, Camera camera) {
        mPreviewSize = size;
        camera.setPreviewCallback(this);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCameraBinder = (ICameraBinder) service;
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCameraBinder = null;
            mIsBind = false;
        }
    };
    private long mBeforeTime = -1;
    private long mAfterTime = -1;
    private boolean mIsInit = false;
    private long mDeltaTotal = 0;
    private int mCurrentFrameNumber = 0;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //初始化数据
        if (!mIsInit) {
            mAfterTime = System.currentTimeMillis();
            if (mAfterTime - mBeforeTime > 10000) {
                mBeforeTime = mAfterTime;
            } else if (mCurrentFrameNumber < 10) {
                //为了计算平均一帧要多少毫秒，那么就丢失10帧数据来计算平均值
                mCurrentFrameNumber++;
                mDeltaTotal += (mAfterTime - mBeforeTime);
            } else if (mCameraBinder != null && mCurrentFrameNumber == 10) {
                mCameraBinder.init(mDeltaTotal / mCurrentFrameNumber, mPreviewSize.width, mPreviewSize.height);
                mIsInit = true;
            }
            mBeforeTime = System.currentTimeMillis();
        }
        if (mCameraBinder != null && mIsInit) {
            mCameraBinder.add(data, System.currentTimeMillis());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_capture:
                if (mCameraBinder != null) {
                    long belong = System.currentTimeMillis();
                    mCameraBinder.capture(belong);
                }
                break;
            case R.id.btn_gallery:
                startActivity(new Intent(this, GalleryActivity.class));
                break;
            case R.id.btn_setting:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBind) {
            unbindService(mServiceConnection);
            mIsBind = false;
        }
    }

}

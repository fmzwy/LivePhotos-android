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
import com.yydcdut.livephotos.model.local.CameraService;

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
        if (!mIsBind) {
            Intent intent = new Intent(this, CameraService.class);
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
    private boolean mIsSetDelta = false;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mAfterTime = System.currentTimeMillis();
        if (mAfterTime - mBeforeTime > 10000) {
            mBeforeTime = mAfterTime;
        } else if (mCameraBinder != null) {
            mCameraBinder.setFrameDelta(mAfterTime - mBeforeTime);
            mIsSetDelta = true;
        }
        mBeforeTime = System.currentTimeMillis();

        if (mCameraBinder != null && mIsSetDelta) {
            mCameraBinder.add(data, mPreviewSize.width, mPreviewSize.height, System.currentTimeMillis());
        }
    }


    @Override
    public void onClick(View v) {
        if (mCameraBinder != null) {
            mCameraBinder.capture(System.currentTimeMillis());
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

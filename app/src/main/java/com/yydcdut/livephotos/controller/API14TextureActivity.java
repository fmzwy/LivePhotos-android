package com.yydcdut.livephotos.controller;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

import com.yydcdut.livephotos.R;

/**
 * Created by yuyidong on 15/9/16.
 */
public class API14TextureActivity extends CameraTextureView implements View.OnClickListener, Runnable {
    private TextureView mTextureView;

    private boolean mIsPreviewing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.fab_capture).setOnClickListener(this);
    }

    @Override
    void startPreview(Camera.Size size, Camera camera) {
        mIsPreviewing = true;
    }

    @Override
    void getTextureView(TextureView textureView) {
        mTextureView = textureView;
    }

    @Override
    void stopPreview() {
        mIsPreviewing = false;
    }

    @Override
    public void run() {

    }

    @Override
    public void onClick(View v) {

    }

}

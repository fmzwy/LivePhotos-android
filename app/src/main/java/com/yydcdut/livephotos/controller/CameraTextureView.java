package com.yydcdut.livephotos.controller;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.yydcdut.livephotos.R;
import com.yydcdut.livephotos.view.AutoFitTextureView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yuyidong on 15/9/16.
 */
public abstract class CameraTextureView extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    /* View */
    private AutoFitTextureView mAutoFitTextureView;
    /* Screen */
    private int mScreenWidth = -1;
    private int mScreenHeight = -1;
    /* Camera */
    private Camera mCamera;
    /* Size */
    private Camera.Size mPreviewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | 128);
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_texture_camera);
        int height = 20;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            height = getStatusBarHeight();
        }
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_margin);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.setMargins(0, height, 0, 0);
        relativeLayout.setLayoutParams(layoutParams);

        mAutoFitTextureView = (AutoFitTextureView) findViewById(R.id.tv_auto);
        mAutoFitTextureView.setSurfaceTextureListener(this);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        mCamera = Camera.open();
    }

    private Camera.Size getSuitablePreviewSize(List<Camera.Size> previewList) {
        Camera.Size previewSize = null;
        Collections.sort(previewList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return (rhs.width * rhs.height - lhs.width * lhs.height);
            }
        });
        float screenScale = mScreenHeight / (float) mScreenWidth;
        for (Camera.Size preSize : previewList) {
            if (preSize.width * preSize.height > 1200000) {
                continue;
            }
            float preScale = preSize.width / (float) preSize.height;

            if (Math.abs(preScale - screenScale) < 0.03) {
                previewSize = preSize;
                break;
            }

        }
        if (previewSize == null) {
            previewSize = previewList.get(0);
        }
        return previewSize;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera.setDisplayOrientation(90);
        mPreviewSize = getSuitablePreviewSize(mCamera.getParameters().getSupportedPreviewSizes());
        mAutoFitTextureView.setAspectRatio(mPreviewSize.height, mPreviewSize.width);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        getTextureView(mAutoFitTextureView);
        startPreview(mPreviewSize, mCamera);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stopPreview();
        mCamera.stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    /**
     * 得到StatusBar大小
     *
     * @return
     */
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    abstract void startPreview(Camera.Size size, Camera camera);

    abstract void getTextureView(TextureView textureView);

    abstract void stopPreview();
}

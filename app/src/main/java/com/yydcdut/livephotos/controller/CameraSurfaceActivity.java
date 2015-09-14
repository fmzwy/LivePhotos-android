package com.yydcdut.livephotos.controller;

import android.content.res.TypedArray;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import com.yydcdut.livephotos.R;
import com.yydcdut.livephotos.view.AutoFitSurfaceView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class CameraSurfaceActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    /* View */
    private AutoFitSurfaceView mAutoFitSurfaceView;
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
        setContentView(R.layout.activity_surface);

        mAutoFitSurfaceView = (AutoFitSurfaceView) findViewById(R.id.sv_auto);
        mAutoFitSurfaceView.getHolder().addCallback(this);

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
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.setDisplayOrientation(90);
        mPreviewSize = getSuitablePreviewSize(mCamera.getParameters().getSupportedPreviewSizes());
        mAutoFitSurfaceView.setAspectRatio(mPreviewSize.height, mPreviewSize.width);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        startPreview(mPreviewSize, mCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    /**
     * 得到actionbar大小
     *
     * @return
     */
    public int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    abstract void startPreview(Camera.Size size, Camera camera);

}

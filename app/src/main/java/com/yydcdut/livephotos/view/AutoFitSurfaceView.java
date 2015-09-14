package com.yydcdut.livephotos.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

/**
 * Created by yuyidong on 15-4-8.
 */
public class AutoFitSurfaceView extends SurfaceView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AutoFitSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
                int inputHeight = width * mRatioHeight / mRatioWidth;
                if (inputHeight < height) {
                    int padding = (height - inputHeight) / 2;
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
                    lp.setMargins(0, padding, 0, 0);
                    this.setLayoutParams(lp);
                }
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
                int inputWidth = height * mRatioWidth / mRatioHeight;
                if (inputWidth < width) {
                    int padding = (width - inputWidth) / 2;
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
                    lp.setMargins(padding, 0, 0, 0);
                    this.setLayoutParams(lp);
                }
            }
        }
    }

}

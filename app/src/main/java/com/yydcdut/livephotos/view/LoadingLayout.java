package com.yydcdut.livephotos.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yydcdut.livephotos.R;

/**
 * Created by yuyidong on 15/9/15.
 */
public class LoadingLayout extends FrameLayout {
    private ImageView mLoadingView;
    private AnimationDrawable mAnimationDrawable;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLoadingView = (ImageView) findViewById(R.id.img_loading);
        mLoadingView.setImageResource(R.drawable.loading);
        mAnimationDrawable = (AnimationDrawable) mLoadingView.getDrawable();
    }

    @Override
    public void setVisibility(int visibility) {
        switch (visibility) {
            case VISIBLE:
                mAnimationDrawable.start();
                break;
            case INVISIBLE:
            case GONE:
                mAnimationDrawable.stop();
                break;
        }
        super.setVisibility(visibility);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}

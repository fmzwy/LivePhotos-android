package com.yydcdut.livephotos.model;

/**
 * Created by yuyidong on 15/9/11.
 */
public interface ICameraBinder {
    void add(byte[] data, int width, int height, long time);

    void capture(long time);

    void setFrameDelta(long delta);
}

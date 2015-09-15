package com.yydcdut.livephotos.model;

/**
 * Created by yuyidong on 15/9/11.
 */
public interface ICameraBinder {
    void add(byte[] data, long time);

    void capture(long belong);

    void init(long delta, int width, int height);

}

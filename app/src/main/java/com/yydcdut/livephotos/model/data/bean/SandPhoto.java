package com.yydcdut.livephotos.model.data.bean;

/**
 * Created by yuyidong on 15/9/10.
 */
public class SandPhoto {
    public static final int ID_NONE = -1;
    private long id;
    final public byte[] data;
    final public int width;
    final public int height;
    final public long time;

    public SandPhoto(long id, byte[] data, int width, int height, long time) {
        this.id = id;
        this.data = data;
        this.width = width;
        this.height = height;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SandPhoto{" +
                "id=" + id +
                ", data=" + "忽略" +
                ", width=" + width +
                ", height=" + height +
                ", time=" + time +
                '}';
    }

}

package com.yydcdut.livephotos.model.cache.structure;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuyidong on 15/9/11.
 */
public class CacheQueue<T> {
    private LinkedList<T> mSaveList;
    private final int mMaxQueueSize;
    private LinkedList<T> mCacheList;

    private boolean mWannaSave = false;
    private long mBelong = -1l;

    public CacheQueue(int maxSize) {
        mMaxQueueSize = maxSize;
        mCacheList = new LinkedList<>();
    }

    public void add(T t) {
        T data = t;
        if (mCacheList.size() < mMaxQueueSize) {
            mCacheList.add(t);
        } else {
            mCacheList.remove(0);
            mCacheList.add(t);
        }
        if (mWannaSave && mSaveList == null) {
            mSaveList = new LinkedList<>(mCacheList);
        } else if (mWannaSave && mSaveList != null) {
            if (mSaveList.size() != mMaxQueueSize * 2 + 1) {
                mSaveList.add(data);
            } else {
                if (mOnDataCacheFinishListener != null) {
                    mOnDataCacheFinishListener.onFinish(mSaveList, mBelong);
                }
                mWannaSave = false;
                mSaveList = null;
                mBelong = -1l;
            }
        }
    }

    /**
     * 是否想保存数据
     *
     * @return false 正在有数据保存中，此次保存数据操作失败
     */
    public boolean wannaSaveData(long belong) {
        if (mWannaSave || mSaveList != null) {
            return false;
        } else {
            mBelong = belong;
            mWannaSave = true;
            return true;
        }
    }

    public boolean isCacheQueueFull() {
        return mCacheList.size() < mMaxQueueSize;
    }

    public void clear() {
        mCacheList.clear();
    }

    private OnDataCacheFinishListener mOnDataCacheFinishListener;

    public interface OnDataCacheFinishListener<T> {
        void onFinish(List<T> list, long belong);
    }

    public void setOnDataCacheFinishListener(OnDataCacheFinishListener onDataCacheFinishListener) {
        mOnDataCacheFinishListener = onDataCacheFinishListener;
    }
}

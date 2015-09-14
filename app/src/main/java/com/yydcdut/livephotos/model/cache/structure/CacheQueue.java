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
        T data = null;
        if (mCacheList.size() < mMaxQueueSize) {
            mCacheList.add(t);
        } else {
            data = mCacheList.remove(0);
            mCacheList.add(t);
        }
        if (mWannaSave && mSaveList == null) {
            //当此队列还没满的时候拍照的话，初始化的List不进行数据copy
            if (mCacheList.size() != mMaxQueueSize) {
                mSaveList = new LinkedList<>();
            } else {
                mSaveList = new LinkedList<>(mCacheList);
            }
        } else if (mWannaSave && mSaveList != null) {
            //直到mSaveList存满才结束，如果mCacheList还没满的时候就要开始存的话，那么data为null，就先舍弃掉
            if (mSaveList.size() != mMaxQueueSize * 2 + 1) {
                if (data != null) {
                    mSaveList.add(data);
                }
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

    private OnDataCacheFinishListener mOnDataCacheFinishListener;

    public interface OnDataCacheFinishListener<T> {
        void onFinish(List<T> list, long belong);
    }

    public void setOnDataCacheFinishListener(OnDataCacheFinishListener onDataCacheFinishListener) {
        mOnDataCacheFinishListener = onDataCacheFinishListener;
    }
}

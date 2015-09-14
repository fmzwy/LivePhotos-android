package com.yydcdut.livephotos.model.cache.structure;


import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yuyidong on 15/9/11.
 */
public class CacheTimeQueue<T> {
    private int mNumber;


    private final int mMaxQueueSize;
    private LinkedList<T> mLinkedList;

    public CacheTimeQueue(int maxSize) {
        mMaxQueueSize = maxSize;
        mLinkedList = new LinkedList<>();
    }

    public void add(T t) {
        if (mLinkedList.size() <= mMaxQueueSize) {
            mLinkedList.add(t);
        } else {
            mLinkedList.remove(0);
            mLinkedList.add(t);
        }
        if (mNumber == mMaxQueueSize) {
            if (mOnCacheGetListener != null) {
                ArrayList<T> arrayList = new ArrayList<>(mLinkedList);
                mOnCacheGetListener.onNeedCacheAfterListener(arrayList);
                mOnCacheGetListener = null;
            }
        }
    }

    public boolean isFull() {
        return mLinkedList.size() < mMaxQueueSize;
    }

    public ArrayList<T> needCacheBefore(OnCacheGetListener onCacheGetListener) {
        mOnCacheGetListener = onCacheGetListener;
        ArrayList<T> arrayList = new ArrayList<>(mLinkedList);
        return arrayList;
    }

    private OnCacheGetListener mOnCacheGetListener;

    public interface OnCacheGetListener<T> {
        void onNeedCacheAfterListener(ArrayList<T> arrayList);
    }
}

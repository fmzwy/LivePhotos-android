package com.yydcdut.livephotos;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.yydcdut.livephotos.model.SandBoxDB;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;

import java.util.List;

/**
 * Created by yuyidong on 15/9/10.
 */
public class SandBoxSQLiteTest extends InstrumentationTestCase {

    public void testEasyCamera() {
        SandPhoto sandPhoto = new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, System.currentTimeMillis());
        long id = SandBoxDB.getInstance().save(sandPhoto);
        Log.i("yuyidong", "id---->" + id);
        List<SandPhoto> sandPhotoList = SandBoxDB.getInstance().findAll();
        for (SandPhoto sandPhoto1 : sandPhotoList) {
            Log.i("yuyidong", "easyPhoto--->" + sandPhoto1.toString());
            int rows = SandBoxDB.getInstance().delete(sandPhoto1);
            Log.i("yuyidong", "rows--->" + rows);
        }
    }

    public void testEasyCameraFindByTime() {
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 1000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 2000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 3000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 4000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 5000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 6000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 7000l)));
        List<SandPhoto> sandPhotoList = SandBoxDB.getInstance().findByTime(5000l);
        for (SandPhoto sandPhoto1 : sandPhotoList) {

        }
    }

    public void testDeleteAll() {
        Log.i("yuyidong", "rows--->" + SandBoxDB.getInstance().deleteAll());
    }

    public void testDeleteByTime() {
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 1000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 2000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 3000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 4000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 5000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 6000l)));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 7000l)));
        Log.i("yuyidong", "rows--->" + SandBoxDB.getInstance().deleteByTime(3000l, 3l));

    }

}

package com.yydcdut.livephotos;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.yydcdut.livephotos.model.data.bean.SandPhoto;
import com.yydcdut.livephotos.model.data.db.SandBoxDB;

import java.util.List;

/**
 * Created by yuyidong on 15/9/10.
 */
public class SandBoxSQLiteTest extends InstrumentationTestCase {

    public void testEasyCameraFindByTime() {
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 1000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 2000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 3000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 4000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 5000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 6000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 7000l), 1));
        List<SandPhoto> sandPhotoList = SandBoxDB.getInstance().find(5000l);
        for (SandPhoto sandPhoto1 : sandPhotoList) {

        }
    }

    public void testDeleteAll() {
        Log.i("yuyidong", "rows--->" + SandBoxDB.getInstance().deleteAll());
    }

    public void testCenterBelong() {
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 1000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 2000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 3000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 4000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 5000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 6000l), 1));
        Log.i("yuyidong", "id---->" + SandBoxDB.getInstance().save(new SandPhoto(-1, new byte[]{'a', 'b', 'c'}, 199, 200, 7000l), 1));
        SandPhoto sandPhoto = SandBoxDB.getInstance().getCenterSandPhoto(1);
        Log.i("yuyidong", "sandPhoto--->" + sandPhoto);

    }

}

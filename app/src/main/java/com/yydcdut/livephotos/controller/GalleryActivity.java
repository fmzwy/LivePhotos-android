package com.yydcdut.livephotos.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yydcdut.livephotos.R;
import com.yydcdut.livephotos.model.data.bean.GalleryPhoto;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;
import com.yydcdut.livephotos.model.data.db.GalleryDB;
import com.yydcdut.livephotos.model.data.db.SandBoxDB;
import com.yydcdut.livephotos.utils.FileManager;

import java.io.File;
import java.util.List;

/**
 * Created by yuyidong on 15/9/14.
 */
public class GalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<GalleryPhoto> mGalleryPhotoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        GridView gridView = (GridView) findViewById(R.id.gv_gallery);
        mGalleryPhotoList = GalleryDB.getInstance().findAll();
        gridView.setAdapter(mGridAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("belong", mGalleryPhotoList.get(position).belong);
        Intent intent = new Intent(this, LivePhotoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    private BaseAdapter mGridAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return mGalleryPhotoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mGalleryPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SandPhoto sandPhoto = SandBoxDB.getInstance().getCenterSandPhoto(mGalleryPhotoList.get(position).belong);
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(GalleryActivity.this).inflate(R.layout.item_gallery, null);
                vh.mImageView = (ImageView) convertView.findViewById(R.id.img_item);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            String path = FileManager.getAppDir() + mGalleryPhotoList.get(position).belong + File.separator + sandPhoto.time + ".jpg";
            ImageLoader.getInstance().displayImage("file:" + File.separator + File.separator + path, vh.mImageView);
            return convertView;
        }

        class ViewHolder {
            ImageView mImageView;
        }
    };
}

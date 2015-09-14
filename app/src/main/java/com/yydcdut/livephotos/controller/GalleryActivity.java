package com.yydcdut.livephotos.controller;

import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.yydcdut.livephotos.R;
import com.yydcdut.livephotos.model.GalleryDB;
import com.yydcdut.livephotos.model.SandBoxDB;
import com.yydcdut.livephotos.model.data.bean.GalleryPhoto;
import com.yydcdut.livephotos.model.data.bean.SandPhoto;

import java.util.List;

/**
 * Created by yuyidong on 15/9/14.
 */
public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        GridView gridView = (GridView) findViewById(R.id.gv_gallery);
        gridView.setAdapter(new GridAdapter());
    }


    class GridAdapter extends BaseAdapter {
        private List<GalleryPhoto> mGalleryPhotoList;

        public GridAdapter() {
            mGalleryPhotoList = GalleryDB.getInstance().findAll();
        }

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
            //todo OOM
            YuvImage yuvImage = new YuvImage(sandPhoto.data, ImageFormat.NV21, sandPhoto.width, sandPhoto.height, null);


//            Bitmap bitmap = BitmapFactory.decodeByteArray(sandPhoto.data, 0, sandPhoto.data.length);
//            vh.mImageView.setImageBitmap(bitmap);
            return convertView;
        }

        class ViewHolder {
            ImageView mImageView;
        }
    }
}

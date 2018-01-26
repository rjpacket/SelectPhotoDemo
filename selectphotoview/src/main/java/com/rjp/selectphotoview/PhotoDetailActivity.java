package com.rjp.selectphotoview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

public class PhotoDetailActivity extends Activity {

    private ImageView detailImage;
    private VideoView detailVideoView;

    public static void trendTo(Context mContext, String filePath, int type){
        Intent intent = new Intent(mContext, PhotoDetailActivity.class);
        intent.putExtra("filePath", filePath);
        intent.putExtra("type", type);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        detailImage = (ImageView) findViewById(R.id.detail_image);
        detailVideoView = (VideoView) findViewById(R.id.detail_video_view);

        Intent intent = getIntent();
        if(intent.hasExtra("type")){
            int type = intent.getIntExtra("type", PhotoType.TYPE_IMAGE);
            String filePath = intent.getStringExtra("filePath");
            switch (type){
                case PhotoType.TYPE_IMAGE:
                    detailImage.setVisibility(View.VISIBLE);
                    detailImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
                    break;
                case PhotoType.TYPE_VIDEO:
                case PhotoType.TYPE_AUDIO:
                    detailVideoView.setVisibility(View.VISIBLE);
                    detailVideoView.setVideoPath(filePath);
                    detailVideoView.start();
                    break;
            }
        }
    }
}

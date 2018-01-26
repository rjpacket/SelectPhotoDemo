package com.rjp.selectphototest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rjp.selectphotoview.SelectPhotoView;

public class MainActivity extends Activity {

    private SelectPhotoView selectPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectPhotoView = (SelectPhotoView) findViewById(R.id.select_photo_view);
        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoView.setSelectType(SelectPhotoView.SELECT_IMAGE);
            }
        });

        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoView.setSelectType(SelectPhotoView.SELECT_VIDEO);
            }
        });

        findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoView.setSelectType(SelectPhotoView.SELECT_AUDIO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectPhotoView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        selectPhotoView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

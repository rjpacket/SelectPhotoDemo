package com.rjp.selectphotoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author Gimpo create on 2018/1/24 18:21
 *         email : jimbo922@163.com
 */

public class PhotoView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private ImageView ivDelete;
    private ImageView ivPlay;
    private ImageView ivPhoto;
    private OnClickListener listener;
    private PhotoModel photoModel;
    private int iconAudioDefault;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.layout_item_view, this);
        ivDelete = (ImageView) findViewById(R.id.item_delete);
        ivPlay = (ImageView) findViewById(R.id.item_play);
        ivPhoto = (ImageView) findViewById(R.id.item_photo);
        ivDelete.setOnClickListener(this);
        ivPhoto.setOnClickListener(this);
    }

    /**
     * 设置child宽度的时候再去 设置显示图片
     * @param width 宽度
     */
    public void setMeasureWidth(int width) {
        ViewGroup.LayoutParams layoutParams = ivPhoto.getLayoutParams();
        layoutParams.width = layoutParams.height = width;
        ivPhoto.setLayoutParams(layoutParams);

        if (photoModel != null) {
            switch (photoModel.getType()) {
                case PhotoType.TYPE_IMAGE:
                    ivDelete.setVisibility(VISIBLE);
                    ivPlay.setVisibility(GONE);
                    Bitmap bitmap = BitmapFactory.decodeFile(photoModel.getFilePath());
                    Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap, width, width);
                    ivPhoto.setImageBitmap(bitmap1);
                    break;
                case PhotoType.TYPE_AUDIO:
                    ivDelete.setVisibility(VISIBLE);
                    ivPlay.setVisibility(GONE);
                    ivPhoto.setImageResource(iconAudioDefault);
                    break;
                case PhotoType.TYPE_VIDEO:
                    ivDelete.setVisibility(VISIBLE);
                    ivPlay.setVisibility(VISIBLE);
                    Bitmap bitmap4 = ThumbnailUtils.createVideoThumbnail(photoModel.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                    Bitmap bitmap5 = ThumbnailUtils.extractThumbnail(bitmap4, width, width);
                    ivPhoto.setImageBitmap(bitmap5);
                    break;
                case PhotoType.TYPE_ADD_PICTURE:
                    ivDelete.setVisibility(GONE);
                    ivPlay.setVisibility(GONE);
                    ivPhoto.setImageResource(R.mipmap.bg_add_picture);
                    break;
            }
        }
    }

    public void setPhotoModel(PhotoModel photoModel) {
        this.photoModel = photoModel;
        ivDelete.setTag(photoModel);
        ivPhoto.setTag(photoModel);
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //只返回图片的大小信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 图片的压缩
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public void setIconDelete(int iconDelete) {
        ivDelete.setImageResource(iconDelete);
    }

    public void setIconPlay(int iconPlay) {
        ivPlay.setImageResource(iconPlay);
    }

    public void setIconAudioDefalt(int iconAudioDefault) {
        this.iconAudioDefault = iconAudioDefault;
    }
}

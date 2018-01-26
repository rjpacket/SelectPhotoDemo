package com.rjp.selectphotoview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.rjp.selectphotoview.PermissionUtils.WRITE_EXTERNAL_STORAGE;

/**
 * 这个控件通常放在scrollview中，不建议使用GridView，一般上传的图片不会太多
 *
 * @author Gimpo create on 2018/1/24 17:55
 *         email : jimbo922@163.com
 */

public class SelectPhotoView extends ViewGroup implements View.OnClickListener, PermissionUtils.OnPermissionsCallBack {
    private static final int TAKE_PHOTO = 1001;

    private static final int PICK_IMAGE = 2001;
    private static final int PICK_AUDIO = 2002;
    private static final int PICK_VIDEO = 2003;

    public static final int SELECT_IMAGE = 55;
    public static final int SELECT_AUDIO = 65;
    public static final int SELECT_VIDEO = 75;

    private static int CODE_WRITE_EXTERNAL_STORAGE = 9001;

    private Context mContext;
    private List<PhotoModel> photoModels = new ArrayList<>();

    private int numCount;
    private float lineSpace;
    private int itemWidth;
    private int selectType = SELECT_IMAGE;
    private int iconDelete;
    private int iconPlay;
    private int iconAudioDefault;
    private PermissionUtils permissionUtils;
    private OnPhotoClickListener photoClickListener;

    public SelectPhotoView(Context context) {
        this(context, null);
    }

    public SelectPhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.SelectPhotoView);
            numCount = array.getInt(R.styleable.SelectPhotoView_photo_count, 3);
            lineSpace = array.getDimension(R.styleable.SelectPhotoView_photo_lineSpace, dp2px(mContext, 10));
            iconDelete = array.getResourceId(R.styleable.SelectPhotoView_icon_delete, R.mipmap.icon_delete);
            iconPlay = array.getResourceId(R.styleable.SelectPhotoView_icon_play, R.mipmap.icon_play);
            iconAudioDefault = array.getResourceId(R.styleable.SelectPhotoView_icon_audioDefault, R.mipmap.icon_mp3);
        }
        addPhotoModel(new PhotoModel(PhotoType.TYPE_ADD_PICTURE));
        permissionUtils = new PermissionUtils((Activity) mContext, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        itemWidth = (int) ((width - lineSpace * (numCount - 1)) / numCount);
        if (photoModels != null) {
            int size = photoModels.size();
            int hang = size / numCount + (size % numCount == 0 ? 0 : 1);
            setMeasuredDimension(width, (int) (hang * itemWidth + lineSpace * (hang - 1)));
        } else {
            setMeasuredDimension(width, itemWidth);
        }
        computeLocation(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 计算每一个view的位置
     */
    private void computeLocation(int widthMeasureSpec, int heightMeasureSpec) {
        if (photoModels != null && photoModels.size() > 0) {
            int size = photoModels.size();
            for (int i = 0; i < size; i++) {
                PhotoModel photoModel = photoModels.get(i);
                photoModel.setLeft((int) ((i % numCount) * (itemWidth + lineSpace)));
                photoModel.setTop((int) ((i / numCount) * (itemWidth + lineSpace)));
                photoModel.setRight((int) ((i % numCount) * (itemWidth + lineSpace)) + itemWidth);
                photoModel.setBottom((int) ((i / numCount) * (itemWidth + lineSpace)) + itemWidth);

                photoModel.getItemView().setMeasureWidth(itemWidth);  // 设置图片为正方形
                measureChild(photoModel.getItemView(), widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (photoModels != null && photoModels.size() > 0) {
            int size = photoModels.size();
            for (int i = 0; i < size; i++) {
                PhotoModel photoModel = photoModels.get(i);
                getChildAt(i).layout(photoModel.getLeft(), photoModel.getTop(), photoModel.getRight(), photoModel.getBottom());
            }
        }
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setSelectType(int type){
        this.selectType = type;
    }

    /**
     * 新增一个model
     *
     * @param newModel model
     */
    public void addPhotoModel(PhotoModel newModel) {
        photoModels.add(0, newModel);
        refreshLayout();
    }

    /**
     * 布局刷新
     */
    private void refreshLayout() {
        removeAllViews();
        for (PhotoModel photoModel : photoModels) {
            PhotoView photoView = new PhotoView(mContext);
            photoView.setPhotoModel(photoModel);
            photoView.setIconDelete(iconDelete);
            photoView.setIconPlay(iconPlay);
            photoView.setIconAudioDefalt(iconAudioDefault);
            photoView.setListener(this);
            photoModel.setItemView(photoView);
            addView(photoView);
        }
    }

    @Override
    public void onClick(View v) {
        PhotoModel photoModel = (PhotoModel) v.getTag();
        int i = v.getId();
        if (i == R.id.item_delete) {
            photoModels.remove(photoModel);
            refreshLayout();
        } else if (i == R.id.item_photo) {
            switch (photoModel.getType()) {
                case PhotoType.TYPE_IMAGE:
                    if(photoClickListener == null){
                        PhotoDetailActivity.trendTo(mContext, photoModel.getFilePath(), PhotoType.TYPE_IMAGE);
                    }else{
                        photoClickListener.onPhotoClick(photoModel);
                    }
                    break;
                case PhotoType.TYPE_AUDIO:
                    if(photoClickListener == null){
                        PhotoDetailActivity.trendTo(mContext, photoModel.getFilePath(), PhotoType.TYPE_AUDIO);
                    }else{
                        photoClickListener.onPhotoClick(photoModel);
                    }
                    break;
                case PhotoType.TYPE_VIDEO:
                    if(photoClickListener == null){
                        PhotoDetailActivity.trendTo(mContext, photoModel.getFilePath(), PhotoType.TYPE_VIDEO);
                    }else{
                        photoClickListener.onPhotoClick(photoModel);
                    }
                    break;
                case PhotoType.TYPE_ADD_PICTURE:
                    permissionUtils.checkPermissions(CODE_WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
                    break;
            }
        }
    }

    /**
     * 需要在activity里面调用一次
     *
     * @param requestCode 请求码
     * @param resultCode 返回码
     * @param data 数据
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE:
                Uri imageUri = data.getData();
                String[] iamgeColumns = {MediaStore.Images.Media.DATA};
                Cursor imageCursor = mContext.getContentResolver().query(imageUri, iamgeColumns, null, null, null);
                if(imageCursor != null){
                    if(imageCursor.moveToFirst()){
                        int columnIndex = imageCursor.getColumnIndex(iamgeColumns[0]);
                        String iamgePath = imageCursor.getString(columnIndex);
                        addPhotoModel(new PhotoModel(PhotoType.TYPE_IMAGE, iamgePath));
                    }
                }
                break;
            case PICK_AUDIO:
                Uri audioUri = data.getData();
                String[] audioColumns = {MediaStore.Images.Media.DATA, MediaStore.Audio.Media.ALBUM_ID};
                Cursor audioCursor = mContext.getContentResolver().query(audioUri, audioColumns, null, null, null);
                if(audioCursor != null){
                    if(audioCursor.moveToFirst()){
                        String audioPath = audioCursor.getString(audioCursor.getColumnIndex(audioColumns[0]));
                        addPhotoModel(new PhotoModel(PhotoType.TYPE_AUDIO, audioPath));
                    }
                }
                break;
            case PICK_VIDEO:
                Uri videoUri = data.getData();
                String[] videoColumns = {MediaStore.Images.Media.DATA};
                Cursor videoCursor = mContext.getContentResolver().query(videoUri, videoColumns, null, null, null);
                if(videoCursor != null){
                    if(videoCursor.moveToFirst()){
                        String videoPath = videoCursor.getString(videoCursor.getColumnIndex(videoColumns[0]));
                        addPhotoModel(new PhotoModel(PhotoType.TYPE_VIDEO, videoPath));
                    }
                }
                break;
        }
    }

    /**
     * 获取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ((Activity) mContext).startActivityForResult(intent, PICK_IMAGE);
    }

    /**
     * 获取音频
     */
    private void pickAudio() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "audio/*");
        ((Activity) mContext).startActivityForResult(intent, PICK_AUDIO);
    }

    /**
     * 获取视频
     */
    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
        ((Activity) mContext).startActivityForResult(intent, PICK_VIDEO);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionUtils.recheckPermissions(requestCode, permissions, grantResults);
    }

    @Override
    public void onGranted(int requestCode) {
        switch (selectType) {
            case SELECT_IMAGE:
                pickPhoto();
                break;
            case SELECT_AUDIO:
                pickAudio();
                break;
            case SELECT_VIDEO:
                pickVideo();
                break;
        }
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {
        Toast.makeText(mContext, "文件权限被关闭，请去设置里面打开之后再操作", Toast.LENGTH_SHORT).show();
        startAppSettings(mContext);
    }

    /**
     * 去设置 开启应用权限
     * @param context 上下文
     */
    public static void startAppSettings(Context context) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener photoClickListener) {
        this.photoClickListener = photoClickListener;
    }

    public interface OnPhotoClickListener{
        void onPhotoClick(PhotoModel photoModel);
    }
}

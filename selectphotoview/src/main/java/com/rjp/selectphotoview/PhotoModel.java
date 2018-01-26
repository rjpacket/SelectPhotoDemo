package com.rjp.selectphotoview;

/**
 * @author Gimpo create on 2018/1/24 18:32
 *         email : jimbo922@163.com
 */

public class PhotoModel {
    private int left;
    private int top;
    private int right;
    private int bottom;

    private int type = PhotoType.TYPE_IMAGE;
    private String filePath;
    private PhotoView itemView;

    public PhotoModel() {

    }

    public PhotoModel(int type) {
        this.type = type;
    }

    public PhotoModel(int type, String filePath) {
        this.type = type;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public PhotoView getItemView() {
        return itemView;
    }

    public void setItemView(PhotoView itemView) {
        this.itemView = itemView;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

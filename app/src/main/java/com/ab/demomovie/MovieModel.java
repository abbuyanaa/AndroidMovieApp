package com.ab.demomovie;

public class MovieModel {

    private int mId;
    private String mTitle;
    private int mDur;
    private String mImage;

    public MovieModel(int mId, String mTitle, int mDur, String mImage) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDur = mDur;
        this.mImage = mImage;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmDur() {
        return mDur;
    }

    public void setmDur(int mDur) {
        this.mDur = mDur;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }
}

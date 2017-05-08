package com.flynetwifi.nhstream.Cards;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mauro on 2/6/17.
 */
public class MovieRecommendedCard {

    @SerializedName("title") private String mTitle = "";
    @SerializedName("description") private String mDescription = "";
    @SerializedName("extraText") private String mExtraText = "";
    @SerializedName("card") private String mImageUrl;
    @SerializedName("footerColor") private String mFooterColor = null;
    @SerializedName("selectedColor") private String mSelectedColor = null;
    @SerializedName("logo") private String mLogo = null;
    @SerializedName("footerIconLocalImageResource") private String mFooterResource = null;
    @SerializedName("type") private MovieRecommendedCard.Type mType;
    @SerializedName("id") private int mId;
    @SerializedName("width") private int mWidth;
    @SerializedName("height") private int mHeight;

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmExtraText() {
        return mExtraText;
    }

    public void setmExtraText(String mExtraText) {
        this.mExtraText = mExtraText;
    }

    private String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmFooterColor() {
        return mFooterColor;
    }

    public void setmFooterColor(String mFooterColor) {
        this.mFooterColor = mFooterColor;
    }

    public String getmSelectedColor() {
        return mSelectedColor;
    }

    public void setmSelectedColor(String mSelectedColor) {
        this.mSelectedColor = mSelectedColor;
    }

    public String getmLogo() {
        return mLogo;
    }

    public void setmLogo(String mLogo) {
        this.mLogo = mLogo;
    }

    public String getmFooterResource() {
        return mFooterResource;
    }

    public void setmFooterResource(String mFooterResource) {
        this.mFooterResource = mFooterResource;
    }

    public Type getmType() {
        return mType;
    }

    public void setmType(Type mType) {
        this.mType = mType;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public URI getImageURI() {
        if (getmImageUrl() == null) return null;
        try {
            return new URI(getmImageUrl());
        } catch (URISyntaxException e) {
            Log.d("URI exception: ", getmImageUrl());
            return null;
        }
    }



    public String getFooterLocalImageResourceName() {
        return mFooterResource;
    }

    public enum Type {

        MOVIE_COMPLETE,
        MOVIE,
        MOVIE_BASE,
        ICON,
        SQUARE_BIG,
        SINGLE_LINE,
        GAME,
        SQUARE_SMALL,
        DEFAULT,
        SIDE_INFO,
        SIDE_INFO_TEST_1,
        TEXT,
        CHARACTER,
        GRID_SQUARE,
        VIDEO_GRID

    }


}

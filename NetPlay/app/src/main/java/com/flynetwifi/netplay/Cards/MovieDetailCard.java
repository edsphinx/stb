package com.flynetwifi.netplay.Cards;

import com.google.gson.annotations.SerializedName;

public class MovieDetailCard {
    @SerializedName("id") private String mId = "";
    @SerializedName("title") private String mTitle = "";
    @SerializedName("description") private String mDescription = "";
    @SerializedName("text") private String mText = "";
    @SerializedName("logo") private String mLogo = null;
    @SerializedName("background") private String mBackground = null;
    @SerializedName("stream") private String mStream = "";
    @SerializedName("recommended") private MovieRecommendedCard[] mRecommended = null;
    @SerializedName("year") private int mYear = 0;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

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

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public String getmLogo() {
        return mLogo;
    }

    public void setmLogo(String mLogo) {
        this.mLogo = mLogo;
    }

    public String getmBackground() {
        return mBackground;
    }

    public void setmBackground(String mBackground) {
        this.mBackground = mBackground;
    }

    public String getmStream() {
        return mStream;
    }

    public void setmStream(String mStream) {
        this.mStream = mStream;
    }

    public MovieRecommendedCard[] getmRecommended() {
        return mRecommended;
    }

    public void setmRecommended(MovieRecommendedCard[] mRecommended) {
        this.mRecommended = mRecommended;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }
}

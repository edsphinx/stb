package com.nuevoshorizontes.nhstream.Cards;

import com.google.gson.annotations.SerializedName;

public class SeriesChapterCard {

    @SerializedName("id") private String mId = "";
    @SerializedName("nombre") private String mNombre = "";
    @SerializedName("logo") private String mLogo = "";
    @SerializedName("stream") private String mStream = "";
    private int mPosicion;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmNombre() {
        return mNombre;
    }

    public void setmNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getmLogo() {
        return mLogo;
    }

    public void setmLogo(String mLogo) {
        this.mLogo = mLogo;
    }

    public String getmStream() {
        return mStream;
    }

    public void setmStream(String mStream) {
        this.mStream = mStream;
    }

    public int getmPosicion() {
        return mPosicion;
    }

    public void setmPosicion(int mPosicion) {
        this.mPosicion = mPosicion;
    }
}

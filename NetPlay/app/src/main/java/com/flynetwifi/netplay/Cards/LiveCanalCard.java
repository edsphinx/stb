package com.flynetwifi.netplay.Cards;


import com.google.gson.annotations.SerializedName;

public class LiveCanalCard {
    @SerializedName("id") private Integer mId = null;
    @SerializedName("numero") private Integer mNumero = null;
    @SerializedName("titulo") private String mTitle = "";
    @SerializedName("descripcion") private String mDescription = "";
    @SerializedName("logo") private String mLogo = null;
    @SerializedName("stream") private String mStream = "";
    @SerializedName("posicion") private int mPosicion = 0;

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public Integer getmNumero() {
        return mNumero;
    }

    public void setmNumero(Integer mNumero) {
        this.mNumero = mNumero;
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

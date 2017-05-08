package com.nuevoshorizontes.nhstream.Cards;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fonseca on 5/3/17.
 */

public class LiveFavoriteCanalCard {
    @SerializedName("id") private Integer mId = null;
    @SerializedName("numero") private Integer mNumero = null;
    @SerializedName("titulo") private String mTitle = "";
    @SerializedName("descripcion") private String mDescription = "";
    @SerializedName("logo") private String mLogo = null;
    @SerializedName("thumbnail") private String mThumbnail = null;
    @SerializedName("stream") private String mStream = "";
    @SerializedName("record") private String mRecord = "";
    @SerializedName("row") private int mRow = 0;
    @SerializedName("posicion") private int mPosicion = 0;
    private int mEstado = 0;

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public Integer getmNumero() {
        return mNumero;
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

    public String getmLogo() {
        return mLogo;
    }

    public String getmStream() {
        return mStream;
    }

    public String getmRecord(){
        return  mRecord;
    }

    public void setmLogo(String mLogo) {
        this.mLogo = mLogo;
    }

    public void setmNumero(Integer mNumero) {
        this.mNumero = mNumero;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public void setmStream(String mStream) {
        this.mStream = mStream;
    }

    public void setmRecord(String mRecord) {
        this.mRecord = mRecord;
    }

    public int getmPosicion() {
        return mPosicion;
    }

    public void setmPosicion(int mPosicion) {
        this.mPosicion = mPosicion;
    }

    public int getmEstado() {
        return mEstado;
    }

    public void setmEstado(int mEstado) {
        this.mEstado = mEstado;
    }

    public int getmRow() {
        return mRow;
    }

    public void setmRow(int mRow) {
        this.mRow = mRow;
    }
}

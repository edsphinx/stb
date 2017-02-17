package com.flynetwifi.netplay.Cards;

import com.google.gson.annotations.SerializedName;

public class MessagesCard {
    @SerializedName("id")
    private int mId = 0;
    @SerializedName("nombre")
    private String mNombre = "";
    @SerializedName("fecha")
    private String mFecha = "";
    @SerializedName("enlace")
    private String mEnlace = "";

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmNombre() {
        return mNombre;
    }

    public void setmNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getmFecha() {
        return mFecha;
    }

    public void setmFecha(String mFecha) {
        this.mFecha = mFecha;
    }

    public String getmEnlace() {
        return mEnlace;
    }

    public void setmEnlace(String mEnlace) {
        this.mEnlace = mEnlace;
    }
}

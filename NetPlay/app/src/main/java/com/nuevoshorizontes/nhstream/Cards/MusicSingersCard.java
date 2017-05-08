package com.flynetwifi.nhstream.Cards;

import com.google.gson.annotations.SerializedName;


public class MusicSingersCard {
    @SerializedName("id") private String mId;
    @SerializedName("nombre") private String mNombre;
    @SerializedName("imagen") private String mImagen;

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

    public String getmImagen() {
        return mImagen;
    }

    public void setmImagen(String mImagen) {
        this.mImagen = mImagen;
    }
}

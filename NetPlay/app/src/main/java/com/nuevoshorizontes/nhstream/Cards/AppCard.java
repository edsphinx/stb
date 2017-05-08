package com.flynetwifi.nhstream.Cards;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

public class AppCard {

    @SerializedName("titulo") private String mTitulo = "";
    @SerializedName("paquete") private String mPaquete = "";
    @SerializedName("imagen") private Drawable mImagen = null;

    public String getmTitulo() {
        return mTitulo;
    }

    public void setmTitulo(String mTitulo) {
        this.mTitulo = mTitulo;
    }

    public String getmPaquete() {
        return mPaquete;
    }

    public void setmPaquete(String mPaquete) {
        this.mPaquete = mPaquete;
    }

    public Drawable getmImagen() {
        return mImagen;
    }

    public void setmImagen(Drawable mImagen) {
        this.mImagen = mImagen;
    }
}

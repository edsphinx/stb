package com.flynetwifi.netplay.Cards;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public class LiveActionCard {
    @SerializedName("id") private Integer mId = null;
    @SerializedName("titulo") private String mTitulo = "";
    @SerializedName("localImageResource") private String mLocalImageResource = null;

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public String getmTitulo() {
        return mTitulo;
    }

    public void setmTitulo(String mTitulo) {
        this.mTitulo = mTitulo;
    }

    private String getmLocalImageResource() {
        return mLocalImageResource;
    }

    public void setmLocalImageResource(String mLocalImageResource) {
        this.mLocalImageResource = mLocalImageResource;
    }

    public int getLocalImageResourceId(Context context){
        return context.getResources().getIdentifier(getmLocalImageResource(), "drawable",
                context.getPackageName());
    }
}

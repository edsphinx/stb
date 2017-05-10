package com.nuevoshorizontes.nhstream.Cards;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class MusicCard {
    @SerializedName("tipo") private int mTipo = -1;
    @SerializedName("data") private Map<String, String>[] mData = null;

    public int getmTipo() {
        return mTipo;
    }

    public void setmTipo(int mTipo) {
        this.mTipo = mTipo;
    }

    public Map<String, String>[] getmData() {
        return mData;
    }

    public void setmData(Map<String, String>[] mData) {
        this.mData = mData;
    }
}

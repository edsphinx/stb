package com.flynetwifi.netplay.Cards;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mauro on 2/7/17.
 */
public class SeriesSeasonCard {

    @SerializedName("id") private String mId = "";
    @SerializedName("nombre") private String mNombre = "";
    @SerializedName("capitulos") private SeriesChapterCard[] mCapitulos = null;

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

    public SeriesChapterCard[] getmCapitulos() {
        return mCapitulos;
    }

    public void setmCapitulos(SeriesChapterCard[] mCapitulos) {
        this.mCapitulos = mCapitulos;
    }
}

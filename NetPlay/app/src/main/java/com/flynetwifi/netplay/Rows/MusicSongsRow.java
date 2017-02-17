package com.flynetwifi.netplay.Rows;

import com.flynetwifi.netplay.Cards.MusicSongCard;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicSongsRow {

    @SerializedName("canciones") private MusicSongCard[] canciones = null;
    public static final int TYPE_DEFAULT = 0;
    private boolean mShadow = true;
    private String mTitle;
    private List<MusicSongCard> mCards;

    public MusicSongCard[] getCanciones() {
        return canciones;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<MusicSongCard> getmCards() {
        return mCards;
    }

    public void setmCards(List<MusicSongCard> mCards) {
        this.mCards = mCards;
    }
}

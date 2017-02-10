package com.flynetwifi.netplay.Rows;

import com.flynetwifi.netplay.Cards.MusicSongCard;
import com.google.gson.annotations.SerializedName;

public class MusicSongsRow {

    @SerializedName("canciones") private MusicSongCard[] canciones = null;

    public MusicSongCard[] getCanciones() {
        return canciones;
    }
}

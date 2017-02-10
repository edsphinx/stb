package com.flynetwifi.netplay.Cards;

import com.google.gson.annotations.SerializedName;

public class MusicSongCard {
    @SerializedName("nombre") private String nombre = "";
    @SerializedName("cantante") private String cantante = "";
    @SerializedName("genero") private String genero = "";
    @SerializedName("stream") private String stream = "";

    public String getNombre() {
        return nombre;
    }

    public String getCantante() {
        return cantante;
    }

    public String getGenero() {
        return genero;
    }

    public String getStream() {
        return stream;
    }
}

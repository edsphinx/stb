package com.flynetwifi.netplay.Cards;

import com.google.gson.annotations.SerializedName;

public class MusicSongCard {
    @SerializedName("id") private String id = "";
    @SerializedName("nombre") private String nombre = "";
    @SerializedName("cantante") private String cantante = "";
    @SerializedName("genero") private String genero = "";
    @SerializedName("stream") private String stream = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

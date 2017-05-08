package com.flynetwifi.nhstream.Rows;

import com.flynetwifi.nhstream.Cards.MusicGendersCard;

import java.util.List;

public class MusicGendersRow {
    private List<MusicGendersCard> generos = null;

    public List<MusicGendersCard> getGeneros() {
        return generos;
    }

    public void setGeneros(List<MusicGendersCard> generos) {
        this.generos = generos;
    }
}

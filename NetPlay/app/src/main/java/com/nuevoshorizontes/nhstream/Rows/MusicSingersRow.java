package com.nuevoshorizontes.nhstream.Rows;

import com.nuevoshorizontes.nhstream.Cards.MusicSingersCard;

import java.util.List;

public class MusicSingersRow {

    private List<MusicSingersCard> cantantes = null;

    public List<MusicSingersCard> getCantantes() {
        return cantantes;
    }

    public void setCantantes(List<MusicSingersCard> cantantes) {
        this.cantantes = cantantes;
    }
}

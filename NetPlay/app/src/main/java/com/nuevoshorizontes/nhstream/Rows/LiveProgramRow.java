package com.nuevoshorizontes.nhstream.Rows;


import com.nuevoshorizontes.nhstream.Cards.LiveProgramCard;
import com.google.gson.annotations.SerializedName;

public class LiveProgramRow {
    @SerializedName("canal") private String name;
    @SerializedName("programas") private LiveProgramCard[] programaCards;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LiveProgramCard[] getProgramaCards() {
        return programaCards;
    }

    public void setProgramaCards(LiveProgramCard[] programaCards) {
        this.programaCards = programaCards;
    }
}

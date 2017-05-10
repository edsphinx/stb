package com.nuevoshorizontes.nhstream.Rows;


import com.nuevoshorizontes.nhstream.Cards.LiveActionCard;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiveActionsRow {
    @SerializedName("titulo") private String mTitle;
    @SerializedName("cards") private List<LiveActionCard> mCards;

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<LiveActionCard> getmCards() {
        return mCards;
    }

    public void setmCards(List<LiveActionCard> mCards) {
        this.mCards = mCards;
    }
}

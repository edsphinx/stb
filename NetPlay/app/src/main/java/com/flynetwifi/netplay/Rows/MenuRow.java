package com.flynetwifi.netplay.Rows;


import com.flynetwifi.netplay.Cards.MenuCard;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MenuRow {
    @SerializedName("titulo") private String mTitle;
    @SerializedName("cards") private List<MenuCard> mCards;

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<MenuCard> getmCards() {
        return mCards;
    }

    public void setmCards(List<MenuCard> mCards) {
        this.mCards = mCards;
    }
}

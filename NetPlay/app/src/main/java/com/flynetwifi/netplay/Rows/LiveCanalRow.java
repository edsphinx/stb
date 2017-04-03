package com.flynetwifi.netplay.Rows;

import com.flynetwifi.netplay.Cards.LiveCanalCard;
import com.flynetwifi.netplay.Cards.MenuCard;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fonseca on 3/30/17.
 */

public class LiveCanalRow {
    @SerializedName("titulo") private String mTitle;
    @SerializedName("cards") private List<LiveCanalCard> mCards;

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<LiveCanalCard> getmCards() {
        return mCards;
    }

    public void setmCards(List<LiveCanalCard> mCards) {
        this.mCards = mCards;
    }
}

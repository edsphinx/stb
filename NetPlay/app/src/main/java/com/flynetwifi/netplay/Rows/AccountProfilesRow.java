package com.flynetwifi.netplay.Rows;

import com.flynetwifi.netplay.Cards.AccountProfileCard;
import com.google.gson.annotations.SerializedName;

public class AccountProfilesRow {
    @SerializedName("perfiles_usuarios") private AccountProfileCard[] profileCards;

    public AccountProfileCard[] getProfileCards(){ return profileCards;}
}

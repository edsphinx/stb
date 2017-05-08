package com.flynetwifi.nhstream.Rows;

import com.flynetwifi.nhstream.Cards.AccountProfileCard;
import com.google.gson.annotations.SerializedName;

public class AccountProfilesRow {
    @SerializedName("perfiles_usuarios") private AccountProfileCard[] profileCards;

    public AccountProfileCard[] getProfileCards(){ return profileCards;}
}

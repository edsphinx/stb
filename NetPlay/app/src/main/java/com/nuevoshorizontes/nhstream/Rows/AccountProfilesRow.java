package com.nuevoshorizontes.nhstream.Rows;

import com.nuevoshorizontes.nhstream.Cards.AccountProfileCard;
import com.google.gson.annotations.SerializedName;

public class AccountProfilesRow {
    @SerializedName("perfiles_usuarios") private AccountProfileCard[] profileCards;

    public AccountProfileCard[] getProfileCards(){ return profileCards;}
}

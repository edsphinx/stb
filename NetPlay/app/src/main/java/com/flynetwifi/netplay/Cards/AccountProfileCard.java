package com.flynetwifi.netplay.Cards;


import com.google.gson.annotations.SerializedName;

public class AccountProfileCard {
    @SerializedName("id") private Integer mId = null;
    @SerializedName("nombre") private String mTitle = "";
    @SerializedName("servicio") private String mService = "";
    @SerializedName("control") private int mParentalControl = 1;

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmService() {
        return mService;
    }

    public void setmService(String mService) {
        this.mService = mService;
    }

    public int getmParentalControl() {
        return mParentalControl;
    }

    public void setmParentalControl(int mParentalControl) {
        this.mParentalControl = mParentalControl;
    }
}

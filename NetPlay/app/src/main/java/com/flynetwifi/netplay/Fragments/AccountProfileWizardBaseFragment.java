package com.flynetwifi.netplay.Fragments;

import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;

import com.flynetwifi.netplay.AccountProfilePasswordActivity;
import com.flynetwifi.netplay.R;

public class AccountProfileWizardBaseFragment extends GuidedStepFragment {


    @Override
    public int onProvideTheme() {
        return R.style.Theme_Wizard;
    }

    AccountProfilePasswordActivity getWizardActivity() {
        if (!(getActivity() instanceof AccountProfilePasswordActivity)) {
            throw new IllegalStateException(AccountProfilePasswordActivity.class.getName() + " expected.");
        }
        return (AccountProfilePasswordActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //mMovie = (Movie) getArguments().getSerializable("movie");
        super.onCreate(savedInstanceState);
    }
}

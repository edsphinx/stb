package com.flynetwifi.netplay;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;

import com.flynetwifi.netplay.Fragments.AccountProfileWizardFragment;

public class AccountProfilePasswordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.fondo02_top);

        GuidedStepFragment fragment = new AccountProfileWizardFragment();
        fragment.setArguments(getIntent().getExtras());
        GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content);
    }

    @Override
    public void onBackPressed() {
        if (GuidedStepFragment.getCurrentGuidedStepFragment(getFragmentManager())
                instanceof AccountProfileWizardFragment) {
            finish();
        } else super.onBackPressed();
    }
}

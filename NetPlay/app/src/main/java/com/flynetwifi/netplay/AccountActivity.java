package com.flynetwifi.netplay;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;

import com.flynetwifi.netplay.Fragments.AccountFragment;


public class AccountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, newInstance(), AccountFragment.TAG);
        ft1.commit();

    }

    private static AccountFragment newInstance() {
        AccountFragment f = new AccountFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }
}

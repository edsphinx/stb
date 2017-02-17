package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.ProfileFragment;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, newInstance(), ProfileFragment.TAG);
        ft1.commit();

    }

    private static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

}

package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.ProfileFragment;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Fragment fragment = new ProfileFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment)
                .commit();

    }

}

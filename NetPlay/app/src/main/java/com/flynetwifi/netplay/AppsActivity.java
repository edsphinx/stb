package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.AppsFragment;

public class AppsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, newInstance(), AppsFragment.TAG);
        ft1.commit();

    }

    private static AppsFragment newInstance() {
        AppsFragment f = new AppsFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

}

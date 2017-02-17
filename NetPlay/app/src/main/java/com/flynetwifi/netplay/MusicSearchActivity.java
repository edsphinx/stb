package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.MusicSearchFragment;

public class MusicSearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, newInstance(), MusicSearchFragment.TAG);
        ft1.commit();

    }

    private static MusicSearchFragment newInstance() {
        MusicSearchFragment f = new MusicSearchFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }
}

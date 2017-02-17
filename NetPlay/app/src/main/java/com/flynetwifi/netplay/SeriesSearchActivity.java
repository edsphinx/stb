package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.SeriesSearchFragment;

public class SeriesSearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, newInstance(), SeriesSearchFragment.TAG);
        ft1.commit();

    }

    private static SeriesSearchFragment newInstance() {
        SeriesSearchFragment f = new SeriesSearchFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }
}

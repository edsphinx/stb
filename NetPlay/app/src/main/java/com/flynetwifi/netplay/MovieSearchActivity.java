package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.MoviesSearchFragment;

public class MovieSearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, newInstance(), MoviesSearchFragment.TAG);
        ft1.commit();

    }

    private static MoviesSearchFragment newInstance() {
        MoviesSearchFragment f = new MoviesSearchFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }
}

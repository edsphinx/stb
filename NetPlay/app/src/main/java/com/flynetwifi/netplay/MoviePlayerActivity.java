package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.MoviePlayerFragment;
import com.flynetwifi.netplay.Fragments.VideoSurfaceFragment;

public class MoviePlayerActivity extends Activity {
    public static final String TAG = "VideoExampleActivity";

    public static String nombre = "";
    public static String url = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        Bundle extras = getIntent().getExtras();

        nombre = extras.getString("nombre");
        url = extras.getString("url");

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, new VideoSurfaceFragment(), VideoSurfaceFragment.TAG);
        ft1.commit();

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.add(R.id.video_fragment, new MoviePlayerFragment(), MoviePlayerFragment.TAG);
        ft2.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
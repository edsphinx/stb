package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.MusicPlayerFragment;
import com.flynetwifi.netplay.Fragments.VideoSurfaceFragment;

public class MusicPlayerActivity extends Activity {
    public static final String TAG = "MusicaReproductorActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live);

        String tipo = getIntent().getStringExtra("tipo");
        String id = getIntent().getStringExtra("id");


        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, new VideoSurfaceFragment(), VideoSurfaceFragment.TAG);
        ft1.commit();

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.add(R.id.video_fragment, newInstance(tipo, id), MusicPlayerFragment.TAG);
        ft2.commit();
    }

    private static MusicPlayerFragment newInstance(String tipo, String id) {
        MusicPlayerFragment f = new MusicPlayerFragment();
        Bundle args = new Bundle();
        args.putString("tipo", tipo);
        args.putString("id", id);
        f.setArguments(args);
        return f;
    }
}

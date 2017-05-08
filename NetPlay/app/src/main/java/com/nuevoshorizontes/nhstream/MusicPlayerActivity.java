package com.nuevoshorizontes.nhstream;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;

import com.nuevoshorizontes.nhstream.Fragments.MusicPlayerFragment;
import com.nuevoshorizontes.nhstream.Fragments.VideoSurfaceFragment;

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_UP) {
            if (e.getKeyCode() == 82) {
                Intent intent = new Intent(this.getBaseContext(),
                        MainActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                        .toBundle();
                startActivity(intent, bundle);
                this.finish();
            }
        }
        return super.dispatchKeyEvent(e);
    }
}

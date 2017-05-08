package com.flynetwifi.nhstream;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;

import com.flynetwifi.nhstream.Fragments.MoviePlayerFragment;
import com.flynetwifi.nhstream.Fragments.VideoSurfaceFragment;
import com.flynetwifi.netplay.R;

public class MoviePlayerActivity extends Activity {
    public static final String TAG = "VideoExampleActivity";

    public static String id = "";
    public static String nombre = "";
    public static String url = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");
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
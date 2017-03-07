package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;

import com.flynetwifi.netplay.Fragments.SeriesPlayerFragment;
import com.flynetwifi.netplay.Fragments.VideoSurfaceFragment;

public class SeriesPlayerActivity extends Activity {

    public static final String TAG = "SeriesReproductorActivity";
    public static String id, nombre, url, row, posicion;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live);

        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");
        nombre = getIntent().getStringExtra("nombre");
        url = getIntent().getStringExtra("url");
        row = getIntent().getStringExtra("row");
        posicion = getIntent().getStringExtra("posicion");

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, new VideoSurfaceFragment(), VideoSurfaceFragment.TAG);
        ft1.commit();

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.add(R.id.video_fragment, newInstance(id, nombre, url, row, posicion), SeriesPlayerActivity.TAG);
        ft2.commit();
    }


    private static SeriesPlayerFragment newInstance(String id, String nombre, String url, String row, String posicion) {
        SeriesPlayerFragment f = new SeriesPlayerFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("nombre", nombre);
        args.putString("url", url);
        args.putString("row", row);
        args.putString("posicion", posicion);
        f.setArguments(args);
        return f;
    }

    @Override
    protected void onNewIntent(Intent intent){
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

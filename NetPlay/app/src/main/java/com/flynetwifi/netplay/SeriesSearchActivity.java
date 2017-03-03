package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;

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

package com.nuevoshorizontes.nhstream;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.PlaybackFragment;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;

import com.newrelic.agent.android.NewRelic;
import com.nuevoshorizontes.nhstream.Fragments.LiveFragment;


public class LiveActivity extends Activity implements NetplayAplication.IMemoryInfo {

    private PlaybackFragment mPlaybackFragment;
    public static String access_token = "";
    public static String user_profile = "";
    public static String user_type = "";

    protected LiveActivity child;
    public static String TAG = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewRelic.withApplicationToken(
                "AAf8803f5f81f23361659615b315f068ef437b32a5"
        ).start(this.getApplication());

        setContentView(R.layout.activity_base);
        TAG = getString(R.string.live_tag);

        access_token = getIntent().getStringExtra("access_token");
        user_profile = getIntent().getStringExtra("user_profile");
        user_type = getIntent().getStringExtra("user_type");

        Fragment fragment =
                getFragmentManager().findFragmentByTag(getString(R.string.live_tag));
        if (fragment instanceof PlaybackFragment) {
            mPlaybackFragment = (PlaybackFragment) fragment;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_UP) {
            if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
                LiveFragment fragment = (LiveFragment) getFragmentManager().findFragmentByTag(getString(R.string.live_tag));
                if (fragment != null) {
                    fragment.keypress(e);
                }
            } else if (e.getKeyCode() == 82) {
                Intent intent = new Intent();
                intent = new Intent(LiveActivity.this,
                        MainActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                        .toBundle();
                startActivity(intent, bundle);
                this.finish();
            } else if (e.getKeyCode() == 19 || e.getKeyCode() == 20 || e.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || e.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                LiveFragment fragment = (LiveFragment) getFragmentManager().findFragmentByTag(getString(R.string.live_tag));
                if(fragment != null){
                    fragment.upDown(e);
                }
            } else if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || e.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                LiveFragment fragment = (LiveFragment) getFragmentManager().findFragmentByTag(getString(R.string.live_tag));
                if (fragment != null) {
                    fragment.showRows();
                }
            }


        }
        return super.

                dispatchKeyEvent(e);
    }

    @Override
    public void goodTimeToReleaseMemory() {

    }
}

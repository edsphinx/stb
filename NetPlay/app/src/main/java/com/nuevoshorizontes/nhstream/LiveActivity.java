package com.nuevoshorizontes.nhstream;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;

import com.newrelic.agent.android.NewRelic;
import com.nuevoshorizontes.nhstream.Fragments.LiveFragment;
import com.nuevoshorizontes.nhstream.Fragments.VideoSurfaceFragment;

public class LiveActivity extends Activity implements NetplayAplication.IMemoryInfo {

    public static final String TAG = "LiveActivity";

    protected LiveActivity child;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewRelic.withApplicationToken(
                "AAf8803f5f81f23361659615b315f068ef437b32a5"
        ).start(this.getApplication());

        setContentView(R.layout.activity_live);

        String access_token = getIntent().getStringExtra("access_token");
        String user_profile = getIntent().getStringExtra("user_profile");
        String user_type = getIntent().getStringExtra("user_type");

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, new VideoSurfaceFragment(), VideoSurfaceFragment.TAG);
        ft1.commit();

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.add(R.id.video_fragment, newInstance(access_token, user_profile, user_type), LiveFragment.TAG);
        ft2.commit();
    }

    private static LiveFragment newInstance(String access_token, String user_profile, String user_type) {
        LiveFragment f = new LiveFragment();
        Bundle args = new Bundle();
        args.putString("access_token", access_token);
        args.putString("user_profile", user_profile);
        args.putString("user_type", user_type);
        f.setArguments(args);
        return f;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_UP ) {
            if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
                LiveFragment fragment = (LiveFragment) getFragmentManager().findFragmentByTag(LiveFragment.TAG);
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
            }else if (e.getKeyCode() == 19 || e.getKeyCode() ==  20 || e.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || e.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                LiveFragment fragment = (LiveFragment) getFragmentManager().findFragmentByTag(LiveFragment.TAG);
                if (fragment != null) {
                    if(fragment.getIsChannelRowActive()) {
                        fragment.keypress(e);
                    }else{
                        fragment.keypressupodwn(e);
                    }
                }
            }
        }/*
        else if (e.getAction() == KeyEvent.ACTION_DOWN ){
            if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
                LiveFragment_old fragment = (LiveFragment_old) getFragmentManager().findFragmentByTag(LiveFragment_old.TAG);
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
            }else if (e.getKeyCode() == 19 || e.getKeyCode() ==  20 ) {
                LiveFragment_old fragment = (LiveFragment_old) getFragmentManager().findFragmentByTag(LiveFragment_old.TAG);
                if (fragment != null) {
                    fragment.keypress(e);
                }
            }
        }*/
        return super.dispatchKeyEvent(e);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (child != null)
                NetplayAplication.unregisterMemoryListener(child);
        } catch (Exception e) {

        }
    }

    @Override
    public void goodTimeToReleaseMemory() {
        //despues
    }
}

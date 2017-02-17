package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.flynetwifi.netplay.Fragments.LiveFragment;
import com.flynetwifi.netplay.Fragments.VideoSurfaceFragment;

public class LiveActivity extends Activity {

    public static final String TAG = "LiveActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public boolean dispatchKeyEvent(KeyEvent e){

        if((e.getKeyCode() >= 7 && e.getKeyCode() <= 16) && e.getAction() == KeyEvent.ACTION_UP){
            LiveFragment fragment = (LiveFragment) getFragmentManager().findFragmentByTag(LiveFragment.TAG);
            if(fragment != null){
                fragment.keypress(e);
            }
        }
        return super.dispatchKeyEvent(e);
    }


}

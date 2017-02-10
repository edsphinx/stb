package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.flynetwifi.netplay.Fragments.LiveFragment;
import com.flynetwifi.netplay.Fragments.LiveSurfaceFragment;

public class LiveActivity extends Activity {

    public static String TAG = "LiveActivity";
    public static String name = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, new LiveSurfaceFragment(), LiveSurfaceFragment.TAG);
        ft1.commit();

        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
        ft2.add(R.id.video_fragment, new LiveFragment(), LiveFragment.TAG);
        ft2.commit();
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

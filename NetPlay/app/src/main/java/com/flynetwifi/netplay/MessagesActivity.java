package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.flynetwifi.netplay.Fragments.MessagesFragment;

public class MessagesActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);


        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.video_fragment, newInstance(), MessagesFragment.TAG);
        ft1.commit();
    }

    private static MessagesFragment newInstance() {
        MessagesFragment f = new MessagesFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }
}

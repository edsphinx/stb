package com.flynetwifi.netplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v4.app.ActivityOptionsCompat;

import com.flynetwifi.netplay.Fragments.MusicPlaylistWizardFragment;

public class MusicPlaylistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_poster);

        GuidedStepFragment fragment = new MusicPlaylistWizardFragment();
        fragment.setArguments(getIntent().getExtras());
        GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content);
    }

    @Override
    public void onBackPressed() {
        if (GuidedStepFragment.getCurrentGuidedStepFragment(getFragmentManager())
                instanceof MusicPlaylistWizardFragment) {
            Intent intent = null;
            intent = new Intent(this.getBaseContext(),
                    MusicActivity.class);
            intent.putExtra("user_profile", MainActivity.user_profile );
            intent.putExtra("user_type", MainActivity.user_type );
            intent.putExtra("access_token", MainActivity.access_token );
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                    .toBundle();
            startActivity(intent, bundle);
            finish();
        } else super.onBackPressed();
    }
}

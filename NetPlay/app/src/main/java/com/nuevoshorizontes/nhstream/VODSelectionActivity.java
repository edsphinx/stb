package com.nuevoshorizontes.nhstream;

import android.os.Bundle;
import android.app.Activity;
import android.support.v17.leanback.app.GuidedStepFragment;

public class VODSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vodselection);

        if (savedInstanceState == null) {
            GuidedStepFragment fragment = new VODSelectionFrame();
            GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content);
        }
    }

}

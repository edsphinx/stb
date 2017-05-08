package com.flynetwifi.nhstream;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.app.ActivityOptionsCompat;

import com.flynetwifi.netplay.R;

import java.util.List;

public class VODSelectionFrame extends GuidedStepFragment {
    private static final int ACTION_PELICULAS = 1;
    private static final int ACTION_SERIES = ACTION_PELICULAS + 1;

    @Override
    public int onProvideTheme() {
        return R.style.Theme_Wizard;
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        GuidanceStylist.Guidance guidance = new GuidanceStylist.Guidance("Video Online",
                "Disfruta del Mejor Contenido Online",
                "", null);
        return guidance;
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction action = new GuidedAction.Builder()
                .id(ACTION_PELICULAS)
                .title("Peliculas").build();
        actions.add(action);
        action = new GuidedAction.Builder()
                .id(ACTION_SERIES)
                .title("Series").build();
        actions.add(action);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        Intent intent = null;
        if (ACTION_PELICULAS == action.getId()) {
            intent = new Intent(getActivity().getBaseContext(),
                    MovieActivity.class);
        } else if (ACTION_SERIES == action.getId()) {
            intent = new Intent(getActivity().getBaseContext(),
                    SeriesActivity.class);
        }
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                .toBundle();
        startActivity(intent, bundle);
        getActivity().finish();
    }
}

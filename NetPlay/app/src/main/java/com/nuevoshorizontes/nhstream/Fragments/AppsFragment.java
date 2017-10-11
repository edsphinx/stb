package com.nuevoshorizontes.nhstream.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.VerticalGridPresenter;

import com.nuevoshorizontes.nhstream.Cards.AppCard;
import com.nuevoshorizontes.nhstream.Presenters.AppsPresenter;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends VerticalGridFragment {
    public static final String TAG = "AppsFragment";

    private static final int COLUMNS = 5;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;

    private ArrayObjectAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");//setTitle(getString(R.string.title_activity_apps));
        setupRowAdapter();
    }

    private void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        AppsPresenter cardPresenter = new AppsPresenter();
        mAdapter = new ArrayObjectAdapter(cardPresenter);
        setAdapter(mAdapter);

        prepareEntranceTransition();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 1000);
    }

    private void createRows() {

        List<AppCard> list = new ArrayList<>();

        Context mContext = getActivity();
        if(mContext!=null) {
            final PackageManager pm = mContext.getPackageManager();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> packages = pm.queryIntentActivities(i, 0);
            for (ResolveInfo ri : packages) {
                AppCard card = new AppCard();
                card.setmTitulo(String.valueOf(ri.loadLabel(pm)));
                card.setmImagen(ri.activityInfo.loadIcon(pm));
                card.setmPaquete(ri.activityInfo.packageName);
                list.add(card);
            }
            mAdapter.addAll(0, list);
        }
    }
}

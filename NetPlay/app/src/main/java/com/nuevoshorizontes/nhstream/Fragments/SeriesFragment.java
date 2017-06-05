package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nuevoshorizontes.nhstream.Cards.SeriesCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.Presenters.SeriesPresenter;
import com.nuevoshorizontes.nhstream.Presenters.SeriesRowPresenterSelector;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.SeriesListRow;
import com.nuevoshorizontes.nhstream.Rows.SeriesRow;
import com.nuevoshorizontes.nhstream.SeriesSearchActivity;
import com.nuevoshorizontes.nhstream.SeriesSeasonsActivity;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
//import PicassoBackgroundManagerTarget;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
//import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesFragment extends BrowseFragment {

    public static final String TAG = "SeriesFragment";

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private ArrayObjectAdapter mRowsAdapter;
    private BackgroundManager backgroundManager;
    //private PicassoBackgroundManagerTarget mBackgroundTarget;

    public static String id = "0";
    private Map<String, SeriesCard[]> data;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        prepareBackgroundManager();
        setupUIElements();
        setupRowAdapter();
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    private void prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mDefaultBackground =
                new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.background));
        backgroundManager.setColor(ContextCompat.getColor(getActivity(), R.color.background));
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getActivity().getResources().getColor(R.color.transparent_background));

//        backgroundManager = BackgroundManager.getInstance(getActivity());
//        backgroundManager.attach(getActivity().getWindow());
//        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                SeriesCard model = (SeriesCard) item;
                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        SeriesSeasonsActivity.class);
                id = String.valueOf(model.getmId());
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof SeriesCard) {
                    SeriesCard model = (SeriesCard) item;
                    int width = mMetrics.widthPixels;
                    int height = mMetrics.heightPixels;
                    Glide.with(getActivity())
                            .load(model.getmPortada())
                            .asBitmap()
                            .centerCrop()
                            .error(R.drawable.bg_poster)
                            .into(new SimpleTarget<Bitmap>(width, height) {
                                @Override
                                public void onResourceReady(Bitmap resource,
                                                            GlideAnimation<? super Bitmap>
                                                                    glideAnimation) {
                                    backgroundManager.setBitmap(resource);
                                }
                            });
//                    Picasso.with(getActivity()).load(model.getmPortada()).skipMemoryCache()
//                            .into(mBackgroundTarget);
                }
            }
        });
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        SeriesSearchActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });
        prepareEntranceTransition();
    }


    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new SeriesRowPresenterSelector());
        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 500);
    }

    private void createRows() {
        mRowsAdapter.clear();
        final SeriesPresenter presenter = new SeriesPresenter();
        mRowsAdapter.add(new SectionRow(new HeaderItem(getString(R.string.series))));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity().getBaseContext(), Constants.server + Constants.series);

                    Gson gson = new Gson();
                    Type seriesCardType;
                    seriesCardType = new TypeToken<Map<String, SeriesCard[]>>() {

                    }.getType();
                    data = gson.fromJson(response, seriesCardType);
                    for (HashMap.Entry<String, SeriesCard[]> entry : data.entrySet()) {
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
                        List<SeriesCard> listSeriesCard = new ArrayList<>();
                        for (SeriesCard card : entry.getValue()) {
                            listRowAdapter.add(card);
                            listSeriesCard.add(card);
                        }
                        SeriesRow row = new SeriesRow();
                        row.setmTitle(entry.getKey());
                        row.setmCards(listSeriesCard);
                        SeriesListRow listRow = new SeriesListRow(
                                new HeaderItem(entry.getKey()),
                                listRowAdapter,
                                row);
                        mRowsAdapter.add(listRow);

                    }
                } catch (JsonParseException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e2) {

                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}


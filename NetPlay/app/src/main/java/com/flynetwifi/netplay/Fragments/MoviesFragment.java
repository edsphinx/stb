package com.flynetwifi.netplay.Fragments;


import android.content.Intent;
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
import com.flynetwifi.netplay.Cards.MovieCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.Models.CustomHeaderItemModel;
import com.flynetwifi.netplay.MovieDetailActivity;
import com.flynetwifi.netplay.MovieSearchActivity;
import com.flynetwifi.netplay.Presenters.CustomHeaderPresenter;
import com.flynetwifi.netplay.Presenters.MoviePresenter;
import com.flynetwifi.netplay.Presenters.MoviePresenterSelector;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.MoviesListRow;
import com.flynetwifi.netplay.Rows.MoviesRow;
import com.flynetwifi.netplay.Utils.DownloadData;
//import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.flynetwifi.netplay.Utils.GlideBackgroundManagerTarget;
import com.flynetwifi.netplay.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
//import com.squareup.picasso.Picasso;
import android.graphics.Bitmap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MoviesFragment extends BrowseFragment {
    public static final String TAG = "MoviesFragment";
    private ArrayObjectAdapter mRowsAdapter;

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private static int CARD_WIDTH = 140;
    private static int CARD_HEIGHT = 220;

    private BackgroundManager backgroundManager;
//    private PicassoBackgroundManagerTarget mBackgroundTarget;
    //private GlideBackgroundManagerTarget mBackgroundTarget;

    public static String id = "0";
    private Map<String, MovieCard[]> data;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        prepareBackgroundManager();
        setupUIElements();
        setupRowAdapter();
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
        setBrandColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

        //backgroundManager = BackgroundManager.getInstance(getActivity());
        //backgroundManager.attach(getActivity().getWindow());
        //mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        //mBackgroundTarget = new GlideBackgroundManagerTarget(backgroundManager);


        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                MovieCard model = (MovieCard) item;
                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        MovieDetailActivity.class);
                id = String.valueOf(model.getmId());
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof MovieCard) {
                    MovieCard model = (MovieCard) item;
                    int width = mMetrics.widthPixels;
                    int height = mMetrics.heightPixels;
                    Glide.with(getActivity())
                            .load(model.getmBackground())
                            .asBitmap()
                            .centerCrop()
                            //.error(R.drawable.bg_poster)
                            .into(new SimpleTarget<Bitmap>(width, height) {
                                @Override
                                public void onResourceReady(Bitmap resource,
                                                            GlideAnimation<? super Bitmap>
                                                                    glideAnimation) {
                                    backgroundManager.setBitmap(resource);
                                }
                            });

//                    Picasso.with(getActivity()).load(model.getmBackground()).skipMemoryCache()
//                            .into(mBackgroundTarget);
                }
            }
        });
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        MovieSearchActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });
        prepareEntranceTransition();
    }


    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new MoviePresenterSelector());
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
        final MoviePresenter presenter = new MoviePresenter();
        final CustomHeaderItemModel HeaderTitle = new CustomHeaderItemModel("Peliculas");
        final CustomHeaderPresenter customHeaderPresenter = new CustomHeaderPresenter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.movies);

                    Gson gson = new Gson();
                    Type peliculasCardType;
                    peliculasCardType = new TypeToken<Map<String, MovieCard[]>>() {

                    }.getType();
                    data = gson.fromJson(response, peliculasCardType);

                    //mRowsAdapter.add(new SectionRow(new HeaderItem(getString(R.string.movies))));
                    mRowsAdapter.add(new SectionRow(HeaderTitle));
                    //mRowsAdapter.add(new SectionRow(HeaderTitle));
                    for (HashMap.Entry<String, MovieCard[]> entry : data.entrySet()) {

                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
                        List<MovieCard> listPeliculasCard = new ArrayList<>();

                        for (MovieCard card : entry.getValue()) {
                            listRowAdapter.add(card);
                            listPeliculasCard.add(card);
                        }
                        MoviesRow row = new MoviesRow();
                        row.setmTitle(entry.getKey());
                        row.setmCards(listPeliculasCard);
                        MoviesListRow listRow = new MoviesListRow(
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

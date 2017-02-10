package com.flynetwifi.netplay.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewGroup;

import com.flynetwifi.netplay.Cards.MovieDetailCard;
import com.flynetwifi.netplay.Cards.MovieRecommendedCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MoviePlayerActivity;
import com.flynetwifi.netplay.Presenters.MovieDetailPresenter;
import com.flynetwifi.netplay.Presenters.MovieRecommendedPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.flynetwifi.netplay.Utils.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MovieDetailFragment extends DetailsFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener {
    public static final String TRANSITION_NAME = "t_for_transition";
    public static final String EXTRA_CARD = "card";

    public String id;
    private ArrayObjectAdapter mRowsAdapter;
    public MovieDetailCard data = null;

    public BackgroundManager backgroundManager;
    public PicassoBackgroundManagerTarget mBackgroundTarget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();

        id = MoviesFragment.id;
        setupUi();
        setupEventListeners();
    }


    public void setupUi() {
        final FullWidthDetailsOverviewRowPresenter rowPresenter = new FullWidthDetailsOverviewRowPresenter(
                new MovieDetailPresenter(getActivity())) {

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                // Customize Actionbar and Content by using custom colors.
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                View actionsView = viewHolder.view.
                        findViewById(R.id.details_overview_actions_background);
                actionsView.setBackgroundColor(getActivity().getResources().
                        getColor(R.color.colorPrimary));

                View detailsView = viewHolder.view.findViewById(R.id.details_frame);
                detailsView.setBackgroundColor(
                        getResources().getColor(R.color.background));
                return viewHolder;
            }
        };
        data = null;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + "/stb/peliculas/detalles/" + id);
                    data = new Gson().fromJson(response, MovieDetailCard.class);

            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        FullWidthDetailsOverviewSharedElementHelper mHelper = new FullWidthDetailsOverviewSharedElementHelper();
        mHelper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        rowPresenter.setListener(mHelper);
        rowPresenter.setParticipatingEntranceTransition(false);
        prepareEntranceTransition();

        ListRowPresenter shadowDisabledRowPresenter = new ListRowPresenter();
        shadowDisabledRowPresenter.setShadowEnabled(false);


        // Setup PresenterSelector to distinguish between the different rows.
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

        // Setup action and detail row.
        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);

       // detailsOverview.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_tv));

        //Logo de Pelicula
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap poster = Picasso.with(getActivity())
                            .load(data.getmLogo())
                            .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), 140),
                                    Utils.convertDpToPixel(getActivity().getApplicationContext(), 220))
                            .centerCrop()
                            .get();
                    detailsOverview.setImageBitmap(getActivity(), poster);
                } catch (IOException e) {
                }
            }
        });

        thread.start();


//Imagen de Fondo
        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        Picasso.with(getActivity()).load(data.getmBackground()).skipMemoryCache().into(mBackgroundTarget);


        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        actionAdapter.add(new Action(1, "Reproducir"));
        //actionAdapter.add(new Action(2, getString(R.string.action_wishlist)));
        //actionAdapter.add(new Action(2, "Peliculas Recomendadas"));
        detailsOverview.setActionsAdapter(actionAdapter);
        mRowsAdapter.add(detailsOverview);


        // Setup recommended row.
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MovieRecommendedPresenter());
        for (MovieRecommendedCard card : data.getmRecommended()) listRowAdapter.add(card);
        HeaderItem header = new HeaderItem(0, getString(R.string.header_recommended));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));


        setAdapter(mRowsAdapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startEntranceTransition();
            }
        }, 500);
    }


    private void setupEventListeners() {
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (!(item instanceof Action)) return;
        Action action = (Action) item;
        if (action.getId() == 1) {
            Intent intent = null;
            intent = new Intent(getActivity().getBaseContext(),
                    MoviePlayerActivity.class);
            intent.putExtra("nombre", data.getmTitle());
            intent.putExtra("url", data.getmStream());
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                    .toBundle();
            startActivity(intent, bundle);
        } else if (action.getId() == 2) {
            setSelectedPosition(1);
        } else {

        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (mRowsAdapter.indexOf(row) > 0) {

        } else {

        }
    }
}


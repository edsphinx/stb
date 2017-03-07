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
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
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

import com.flynetwifi.netplay.Cards.SeriesCard;
import com.flynetwifi.netplay.Cards.SeriesChapterCard;
import com.flynetwifi.netplay.Cards.SeriesSeasonCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.Presenters.SeriesDetailsPresenter;
import com.flynetwifi.netplay.Presenters.SeriesSeasonPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.SeriesPlayerActivity;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.flynetwifi.netplay.Utils.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class SeriesSeasonsFragment extends DetailsFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener {

    public static final String TRANSITION_NAME = "t_for_transition";
    public static final String EXTRA_CARD = "card";

    private ArrayObjectAdapter mRowsAdapter;
    private SeriesCard data = null;

    private BackgroundManager backgroundManager;
    private PicassoBackgroundManagerTarget mBackgroundTarget;

    public static String url = "";
    public static String id = "";
    public static String nombre = "";
    public static Row rowAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        setupEventListeners();
    }


    private void setupUi() {
        final FullWidthDetailsOverviewRowPresenter rowPresenterBack =
                new FullWidthDetailsOverviewRowPresenter(
                new SeriesDetailsPresenter(getActivity())) {

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

        final DetailsOverviewRowPresenter rowPresenter =
                new DetailsOverviewRowPresenter(
                        new SeriesDetailsPresenter(getActivity())) {
                    @Override
                    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                        RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                        View actionsView = viewHolder.view.
                                findViewById(R.id.details_overview_actions_background);

                        View detailsView = viewHolder.view.findViewById(R.id.details_frame);
                        detailsView.setBackgroundColor(
                                getResources().getColor(R.color.background));


                        return viewHolder;

                    }
                };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.details + SeriesFragment.id);
                    data = new Gson().fromJson(response, SeriesCard.class);


            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ListRowPresenter shadowDisabledRowPresenter = new ListRowPresenter();
        shadowDisabledRowPresenter.setShadowEnabled(false);


        // Setup PresenterSelector to distinguish between the different rows.
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        for(int i = 0; i < data.getmNumeroTemporadas(); i++) {
            rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        }
        mRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

        // Setup action and detail row.
        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);

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
        Picasso.with(getActivity()).load(data.getmPortada()).skipMemoryCache().into(mBackgroundTarget);


        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        //actionAdapter.add(new Action(1, "Reproducir"));
        //actionAdapter.add(new Action(2, getString(R.string.action_wishlist)));
        actionAdapter.add(new Action(2, getString(R.string.seasons)));
        detailsOverview.setActionsAdapter(actionAdapter);
        mRowsAdapter.add(detailsOverview);


        // Setup recommended row.
        ArrayObjectAdapter listRowAdapter;
        int i = 0;
        for (SeriesSeasonCard temporadaCard : data.getmTemporadas()) {
            listRowAdapter = new ArrayObjectAdapter(new SeriesSeasonPresenter());
            for(SeriesChapterCard capituloCard :  temporadaCard.getmCapitulos()){
                listRowAdapter.add(capituloCard);
            }
            HeaderItem header = new HeaderItem(i, temporadaCard.getmNombre());
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
            i++;
        }


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
        if (item instanceof Action){
            Action action = (Action) item;
            if(action.getId() == 1){

                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        SeriesPlayerActivity.class);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
            else if(action.getId() == 2){
                setSelectedPosition(1);
            }

        }
        else if(item instanceof SeriesChapterCard){
            SeriesChapterCard capitulo = (SeriesChapterCard) item;

            url = capitulo.getmStream();
            rowAdapter = row;
            nombre = capitulo.getmNombre();
            id = capitulo.getmId();

            Intent intent = null;
            intent = new Intent(getActivity().getBaseContext(),
                    SeriesPlayerActivity.class);
            intent.putExtra("id", capitulo.getmId());
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                    .toBundle();
            startActivity(intent, bundle);
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



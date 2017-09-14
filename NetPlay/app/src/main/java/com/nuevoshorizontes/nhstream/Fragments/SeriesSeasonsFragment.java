package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nuevoshorizontes.nhstream.Cards.SeriesCard;
import com.nuevoshorizontes.nhstream.Cards.SeriesChapterCard;
import com.nuevoshorizontes.nhstream.Cards.SeriesSeasonCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.Presenters.SeriesDetailsPresenter;
import com.nuevoshorizontes.nhstream.Presenters.SeriesSeasonPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.SeriesPlayerActivity;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
//import PicassoBackgroundManagerTarget;
import com.nuevoshorizontes.nhstream.Utils.Utils;
import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.nuevoshorizontes.nhstream.MainActivity.access_token;

public class SeriesSeasonsFragment extends DetailsFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener {

    public static final String TRANSITION_NAME = "t_for_transition";
    public static final String EXTRA_CARD = "card";

    private ArrayObjectAdapter mRowsAdapter;
    private SeriesCard data = null;

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private BackgroundManager backgroundManager;
    //private PicassoBackgroundManagerTarget mBackgroundTarget;

    public static Row rowAdapter = null;
    public static HashMap<Integer, List<SeriesChapterCard>> dataChapters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareBackgroundManager();
        setupUIElements();
        setupEventListeners();
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
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.details + SeriesFragment.id);
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
//                    Bitmap poster = Picasso.with(getActivity())
//                            .load(data.getmLogo())
//                            .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), 140),
//                                    Utils.convertDpToPixel(getActivity().getApplicationContext(), 220))
//                            .centerCrop()
//                            .get();
                try {
                    Bitmap poster = Glide.with(getActivity())
                            .load(data.getmLogo())
                            .asBitmap()
                            .centerCrop()
                            .into(Utils.convertDpToPixel(getActivity().getApplicationContext(), 140),
                                    Utils.convertDpToPixel(getActivity().getApplicationContext(), 220))
                            .get();
                    detailsOverview.setImageBitmap(getActivity(), poster);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        });

        thread.start();


//Imagen de Fondo
        //backgroundManager = BackgroundManager.getInstance(getActivity());
        //backgroundManager.attach(getActivity().getWindow());
        //mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(data.getmPortada())
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
        //Picasso.with(getActivity()).load(data.getmPortada()).skipMemoryCache().into(mBackgroundTarget);


        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        //actionAdapter.add(new Action(1, "Reproducir"));
        //actionAdapter.add(new Action(2, getString(R.string.action_wishlist)));
        actionAdapter.add(new Action(2, getString(R.string.seasons)));
        detailsOverview.setActionsAdapter(actionAdapter);
        mRowsAdapter.add(detailsOverview);


        // Setup recommended row.
        ArrayObjectAdapter listRowAdapter;
        dataChapters = new HashMap<>();
        int i = 0;
        for (SeriesSeasonCard temporadaCard : data.getmTemporadas()) {
            listRowAdapter = new ArrayObjectAdapter(new SeriesSeasonPresenter());

            List<SeriesChapterCard> seriesChapterCardList = new ArrayList<>();

            int posicion = 0;
            for(SeriesChapterCard capituloCard :  temporadaCard.getmCapitulos()){
                capituloCard.setmPosicion(posicion);
                listRowAdapter.add(capituloCard);
                seriesChapterCardList.add(capituloCard);
                posicion++;
            }

            dataChapters.put(i, seriesChapterCardList);
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
                intent = new Intent(getActivity(),
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

            rowAdapter = row;

            Intent intent = null;
            intent = new Intent(getActivity(),
                    SeriesPlayerActivity.class);
            intent.putExtra("id", capitulo.getmId());
            intent.putExtra("url", capitulo.getmStream());
            intent.putExtra("nombre", capitulo.getmNombre());
            intent.putExtra("row", String.valueOf(row.getId()));
            intent.putExtra("posicion", String.valueOf(capitulo.getmPosicion()));
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



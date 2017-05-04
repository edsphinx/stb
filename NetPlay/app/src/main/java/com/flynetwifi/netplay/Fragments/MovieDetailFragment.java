package com.flynetwifi.netplay.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flynetwifi.netplay.Cards.MovieDetailCard;
import com.flynetwifi.netplay.Cards.MovieRecommendedCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MediaPlayers.MovieExoPlayer;
import com.flynetwifi.netplay.MoviePlayerActivity;
import com.flynetwifi.netplay.Presenters.MovieDetailPresenter;
import com.flynetwifi.netplay.Presenters.MovieRecommendedPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Utils.DownloadData;
//import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.flynetwifi.netplay.Utils.GlideBackgroundManagerTarget;
import com.flynetwifi.netplay.Utils.Utils;
import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MovieDetailFragment extends DetailsFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener {

    public static final String TAG = "MovieDetailFragment";
    private static final String TRANSITION_NAME = "t_for_transition";

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private static int CARD_WIDTH = 140;
    private static int CARD_HEIGHT = 220;

    public String response = null;
    public MovieDetailCard data = null;
    public ArrayObjectAdapter mRowsAdapter;
    private BackgroundManager backgroundManager;
    //private PicassoBackgroundManagerTarget mBackgroundTarget;
    private GlideBackgroundManagerTarget mBackgroundTarget;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();

        String id = MoviesFragment.id;
        prepareBackgroundManager();
        setupUIElements(id);
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

    private void setupUIElements(String id) {
        final DetailsOverviewRowPresenter rowPresenter = setupRowPresenter();
         mRowsAdapter = setupRowAdapter(rowPresenter);

        data = setupData(id);

        // Setup action and detail row.
        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);


        if (data != null) {
            setupLogo(detailsOverview);
            setBackground();
        }

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        actionAdapter.add(new Action(1, getString(R.string.play)));
        detailsOverview.setActionsAdapter(actionAdapter);
        //detailsOverview.setImageDrawable(R.drawable.bg_default);
        mRowsAdapter.add(detailsOverview);


        // Setup recommended row.
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MovieRecommendedPresenter());
        for (MovieRecommendedCard card : data.getmRecommended()) listRowAdapter.add(card);
        HeaderItem header = new HeaderItem(0, getString(R.string.header_recommended));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));


        setAdapter(mRowsAdapter);

        startTransition();


    }

    /**
     * Create ROW Presenter
     */
    private DetailsOverviewRowPresenter setupRowPresenter() {
        /*final FullWidthDetailsOverviewRowPresenter rowPresenterBAck =
                new FullWidthDetailsOverviewRowPresenter(
                        new MovieDetailPresenter(getActivity())) {

                    @Override
                    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
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
                */

        DetailsOverviewRowPresenter rowPresenter =
                new DetailsOverviewRowPresenter(
                        new MovieDetailPresenter(getActivity())) {
                    @Override
                    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                        RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                        View actionsView = viewHolder.view.
                                findViewById(R.id.details_overview_actions_background);

                        View detailsView = viewHolder.view.findViewById(R.id.details_frame);
                        detailsView.setBackgroundColor(
                                getResources().getColor(R.color.transparent_background));


                        return viewHolder;

                    }
                };

        return rowPresenter;
    }

    private ArrayObjectAdapter setupRowAdapter(DetailsOverviewRowPresenter rowPresenter) {
        // Setup PresenterSelector to distinguish between the different rows.
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        return new ArrayObjectAdapter(rowPresenterSelector);
    }

    /**
     * Descripcion: Download Data de Pelicula?
     *
     * @param idMovie
     * @return
     */
    private MovieDetailCard setupData(final String idMovie) {
        response = null;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadData downloadData = new DownloadData();
                response = downloadData.run(Constants.server + Constants.movies_details
                        + idMovie);

            }
        });
        thread.start();

        try {
            thread.join();
            if (response != null) {
                return new Gson().fromJson(response, MovieDetailCard.class);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;


    }

    /**
     * Descripcion: Setup el Background
     */
    private void setBackground() {
        //Imagen de Fondo
        //backgroundManager = BackgroundManager.getInstance(getActivity());
        //backgroundManager.attach(getActivity().getWindow());
        //mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        //mBackgroundTarget = new GlideBackgroundManagerTarget(backgroundManager);
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(data.getmBackground())
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
//        Picasso.with(getActivity())
//                .load(data.getmBackground())
//                .skipMemoryCache()
//                .error(R.drawable.bg_poster)
//                .into(mBackgroundTarget);
    }

    private void setupLogo(final DetailsOverviewRow detailsOverview) {
        //Logo de Pelicula
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap poster = Glide.with(getActivity())
                            .load(data.getmLogo())
                            .asBitmap()
                            .centerCrop()
                            .skipMemoryCache(true)
                            .into(Utils.convertDpToPixel(getActivity().getApplicationContext(), CARD_WIDTH),
                                    Utils.convertDpToPixel(getActivity().getApplicationContext(), CARD_HEIGHT))
                            .get();
                    detailsOverview.setImageBitmap(getActivity(), poster);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
//                    Bitmap poster = Picasso.with(getActivity())
//                            .load(data.getmLogo())
//                            .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), 140),
//                                    Utils.convertDpToPixel(getActivity().getApplicationContext(), 220))
//                            //.centerCrop()
//                            .centerInside()
//                            .get();
                    //detailsOverview.setImageBitmap(getActivity(), poster);
            }
        });

        thread.start();
    }

    private void startTransition() {

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
        try {
            if (item instanceof MovieRecommendedCard) {
                MovieRecommendedCard card = (MovieRecommendedCard) item;
                reloadMovie(String.valueOf(card.getmId()));
            }
            if (item instanceof Action) {
                Action action = (Action) item;
                if (action.getId() == 1) {

//                Intent intent = null;
//                intent = new Intent(getActivity().getBaseContext(),
//                        MoviePlayerActivity.class);
//                intent.putExtra("id", data.getmId());
//                intent.putExtra("nombre", data.getmTitle());
//                intent.putExtra("url", data.getmStream());
//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
//                        .toBundle();
//                startActivity(intent, bundle);

                    if (data != null) {
                        UriExoMedia sample = new UriExoMedia(data.getmTitle(), null, null, null,
                                false, data.getmStream(), null);

                        onExoMediaSelected(sample);
                    }

                } else if (action.getId() == 2) {
                    setSelectedPosition(1);
                }
            }
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }
    }

    private abstract static class ExoMedia {

        public final String name;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;

        public ExoMedia(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
            this.name = name;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;
        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, MovieExoPlayer.class);
            intent.putExtra("movie_name",name);
            intent.putExtra(MovieExoPlayer.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
            if (drmSchemeUuid != null) {
                intent.putExtra(MovieExoPlayer.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
                intent.putExtra(MovieExoPlayer.DRM_LICENSE_URL, drmLicenseUrl);
                intent.putExtra(MovieExoPlayer.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
            }
            return intent;
        }

    }

    private static final class UriExoMedia extends ExoMedia {

        public final String uri;
        public final String extension;

    public UriExoMedia(String name, UUID drmSchemeUuid, String drmLicenseUrl,
        String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,
        String extension) {
        super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
        this.uri = uri;
        this.extension = extension;
    }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra(MovieExoPlayer.EXTENSION_EXTRA, extension)
                    .setAction(MovieExoPlayer.ACTION_VIEW);
        }

    }

    private void onExoMediaSelected(ExoMedia sample) {
            startActivity(sample.buildIntent(getActivity()));
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row){
    }

    private void reloadMovie(String id) {
        //mRowsAdapter.clear();

        data = setupData(id);

        // Setup action and detail row.
        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);


        if (data != null) {
            setupLogo(detailsOverview);
            setBackground();
        }

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        actionAdapter.add(new Action(1, getString(R.string.play)));
        detailsOverview.setActionsAdapter(actionAdapter);
        mRowsAdapter.replace(0, detailsOverview);


        // Setup recommended row.
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MovieRecommendedPresenter());
        for (MovieRecommendedCard card : data.getmRecommended()) listRowAdapter.add(card);
        HeaderItem header = new HeaderItem(0, getString(R.string.header_recommended));
        mRowsAdapter.replace(1, new ListRow(header, listRowAdapter));


        //mRowsAdapter.notifyArrayItemRangeChanged(0, 2);

    }


}


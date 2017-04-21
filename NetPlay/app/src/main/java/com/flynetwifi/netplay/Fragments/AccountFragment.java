package com.flynetwifi.netplay.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flynetwifi.netplay.Cards.AccountCard;
import com.flynetwifi.netplay.Cards.AccountProfileCard;
import com.flynetwifi.netplay.Cards.BillsCard;
import com.flynetwifi.netplay.Cards.MessagesCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MainActivity;
import com.flynetwifi.netplay.Presenters.AccountBillPresenter;
import com.flynetwifi.netplay.Presenters.AccountPresenter;
import com.flynetwifi.netplay.Presenters.AccountProfilesPresenter;
import com.flynetwifi.netplay.Presenters.MessagesPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.AccountProfilesRow;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.Utils.GlideBackgroundManagerTarget;
//import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.bumptech.glide.Glide;
//import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends DetailsFragment implements OnItemViewSelectedListener,
        OnItemViewClickedListener {

    public static final String TAG = "AccountFragment";

    private static final String TRANSITION_NAME = "t_for_transition";

    private ArrayObjectAdapter mRowsAdapter;
    private AccountCard data = null;
    private BillsCard[] dataBills = null;
    private AccountProfilesRow dataProfiles = null;
    private Map<String, MessagesCard> dataMessages = new HashMap<>();

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private BackgroundManager backgroundManager;
    //private PicassoBackgroundManagerTarget mBackgroundTarget;
    private GlideBackgroundManagerTarget mBackgroundTarget;

    private final int DATA_ROW = 0;
    private final int FACTURAS_ROW = 1;
    private final int PROFILES_ROW = 2;
    private final int MESSAGEs_ROW = 3;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
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
        final FullWidthDetailsOverviewRowPresenter rowPresenter =
                new FullWidthDetailsOverviewRowPresenter(
                        new AccountPresenter(getActivity())
                ) {
                    @Override
                    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                        RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                        View actionsView = viewHolder.view
                                .findViewById(R.id.details_overview_actions_background);
                        actionsView.setBackground(getActivity().getResources()
                                .getDrawable(R.drawable.bg_default));


                        View detailsView = viewHolder.view.findViewById(R.id.details_frame);
                        detailsView.setBackgroundColor(
                                getResources().getColor(R.color.background));
                        return viewHolder;
                    }
                };

        // Setup PresenterSelector to distinguish between the different rows.
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);


        data = null;

        //Descargando informacion de Cuenta
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadData downloadData = new DownloadData();
                String response = downloadData.run(Constants.server + Constants.account + MainActivity.access_token);
                data = new Gson().fromJson(response, AccountCard.class);

            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ic_account_box);
        detailsOverview.setImageBitmap(getActivity().getBaseContext(), icon);

        //backgroundManager = BackgroundManager.getInstance(getActivity());
        //backgroundManager.attach(getActivity().getWindow());
        //mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        //mBackgroundTarget = new GlideBackgroundManagerTarget(backgroundManager);

        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(R.drawable.blackwall)
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
//        Picasso.with(getActivity()).load(R.drawable.blackwall).skipMemoryCache()
//                .into(mBackgroundTarget);


        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        actionAdapter.add(new Action(FACTURAS_ROW, getString(R.string.account_bills)));
        actionAdapter.add(new Action(PROFILES_ROW, getString(R.string.accounts)));
        actionAdapter.add(new Action(MESSAGEs_ROW, "Mensajes"));
        detailsOverview.setActionsAdapter(actionAdapter);
        mRowsAdapter.add(DATA_ROW, detailsOverview);

        /**
         * Agregando Facturas de Cliente
         */
        final ArrayObjectAdapter billsAdapter = new ArrayObjectAdapter(new AccountBillPresenter());

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();

                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + "/stb/cuenta/facturas/"
                            + MainActivity.access_token);


                    dataBills = gson.fromJson(response, BillsCard[].class);


                    int i = 0;
                    for (BillsCard card : dataBills) {
                        billsAdapter.add(card);
                    }

                    HeaderItem header = new HeaderItem(FACTURAS_ROW, "Facturas");
                    mRowsAdapter.add(FACTURAS_ROW, new ListRow(header,
                            billsAdapter));


                } catch (JsonParseException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /**
         * Agregando Perfiles de Cliente
         */
        final ArrayObjectAdapter profilesAdapter = new ArrayObjectAdapter(new AccountProfilesPresenter());

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    DownloadData downloadData = new DownloadData();

                    String response = downloadData.run(Constants.server + Constants.profiles + MainActivity.access_token);
                    dataProfiles = new Gson().fromJson(response, AccountProfilesRow.class);

                    int i = 0;
                    for (AccountProfileCard card : dataProfiles.getProfileCards()) {
                        profilesAdapter.add(card);
                    }

                    HeaderItem header = new HeaderItem(PROFILES_ROW, "Perfiles de Cuenta");
                    mRowsAdapter.add(PROFILES_ROW, new ListRow(header,
                            profilesAdapter));


                } catch (JsonParseException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /** Agregando ROW de Mensajes */
        dataMessages = null;
        final ArrayObjectAdapter messagesAdapter = new ArrayObjectAdapter(new MessagesPresenter());
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadData downloadData = new DownloadData();
                String response = downloadData.run(Constants.server + Constants.messages);

                Gson gson = new Gson();
                Type mensajesCardType;
                mensajesCardType = new TypeToken<HashMap<String, MessagesCard>>() {
                }
                        .getType();
                dataMessages = gson.fromJson(response, mensajesCardType);
                for (HashMap.Entry<String, MessagesCard> entry : dataMessages.entrySet()) {
                    MessagesCard card =  entry.getValue();
                    messagesAdapter.add(card);
                }

                HeaderItem header = new HeaderItem(MESSAGEs_ROW, "Mensajes");
                mRowsAdapter.add(MESSAGEs_ROW, new ListRow(header,
                        messagesAdapter));

            }
        });


        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        if (item instanceof Action) {
            if (item instanceof Action) {
                Action action = (Action) item;

                if (action.getId() == FACTURAS_ROW) {
                    setSelectedPosition(FACTURAS_ROW);
                }
                else if(action.getId() == PROFILES_ROW){
                    setSelectedPosition(PROFILES_ROW);
                }
                else if(action.getId() == MESSAGEs_ROW){
                    setSelectedPosition(MESSAGEs_ROW);
                }

            }
        }

    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {

    }
}


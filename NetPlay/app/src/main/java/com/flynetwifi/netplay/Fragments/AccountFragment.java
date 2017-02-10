package com.flynetwifi.netplay.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;
import android.view.ViewGroup;

import com.flynetwifi.netplay.Cards.AccountCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.Presenters.AccountPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

/**
 * Created by mauro on 2/8/17.
 */
public class AccountFragment extends DetailsFragment implements OnItemViewSelectedListener,
        OnItemViewClickedListener {

    public static final String TRANSITION_NAME = "t_for_transition";
    public static final String EXTRA_CARD = "card";

    private ArrayObjectAdapter mRowsAdapter;
    public AccountCard data = null;

    public BackgroundManager backgroundManager;
    public PicassoBackgroundManagerTarget mBackgroundTarget;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);


        setupUi();
        setupEventListeners();
    }

    public void setupUi() {
        final FullWidthDetailsOverviewRowPresenter rowPresenter = new FullWidthDetailsOverviewRowPresenter(
                new AccountPresenter(getActivity())
        ) {
            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                View actionsView = viewHolder.view.findViewById(R.id.details_overview_actions_background);
                actionsView.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_default));


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
                String response = downloadData.run(Constants.server + Constants.account);
                data = new Gson().fromJson(response, AccountCard.class);

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


        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ic_account_box);
        detailsOverview.setImageBitmap(getActivity().getBaseContext(), icon);

        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        Picasso.with(getActivity()).load(R.drawable.bg_poster).skipMemoryCache().into(mBackgroundTarget);


        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        actionAdapter.add(new Action(1, "Facturas"));
        //actionAdapter.add(new Action(2, getString(R.string.action_wishlist)));
        actionAdapter.add(new Action(2, "Cuentas de Usuario"));
        detailsOverview.setActionsAdapter(actionAdapter);
        mRowsAdapter.add(detailsOverview);

        setAdapter(mRowsAdapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startEntranceTransition();
            }
        }, 500);
    }

    public void setupEventListeners() {
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

    }
}


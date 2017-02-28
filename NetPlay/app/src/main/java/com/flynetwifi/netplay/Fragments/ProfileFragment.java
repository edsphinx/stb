package com.flynetwifi.netplay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;

import com.flynetwifi.netplay.AccountProfilePasswordActivity;
import com.flynetwifi.netplay.Cards.AccountProfileCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MainActivity;
import com.flynetwifi.netplay.Presenters.AccountProfilesPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.AccountProfilesRow;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends BrowseFragment {

    public static final String TAG = "ProfileFragment";

    private ArrayObjectAdapter mRowsAdapter;
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private AccountProfilesRow data = null;
    private BackgroundManager backgroundManager;
    private PicassoBackgroundManagerTarget mBackgroundTarget;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity().getBaseContext();
        setupUIElements();
        setupRowAdapter();
        setupEventListeners();
    }

    private void setupUIElements() {
        //setTitle(getString(R.string.app_name));
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(false);
        //setBrandColor(getResources().getColor(R.color.colorPrimary));
        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        Picasso.with(getActivity()).load(R.drawable.fondo02_top).skipMemoryCache()
                .into(mBackgroundTarget);

    }

    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        createRows();
        mRowsAdapter.add(createCardRow(data));
        setAdapter(mRowsAdapter);
    }


    private void createRows() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                DownloadData downloadData = new DownloadData();

                String response = downloadData.run(Constants.server + Constants.profiles + MainActivity.access_token);
                data = new Gson().fromJson(response, AccountProfilesRow.class);


            }
        });


        try {
            thread.start();
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static ListRow createCardRow(AccountProfilesRow cardRow) {

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new AccountProfilesPresenter());
        for (AccountProfileCard card : cardRow.getProfileCards()) {
            listRowAdapter.add(card);
        }
        return new ListRow(listRowAdapter);
    }


    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());

    }

    private void playSound() {
        Thread thread = new Thread() {
            public void run() {
                //mp.start();
            }
        };
        thread.start();
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            AccountProfileCard card = (AccountProfileCard) item;
            if (card.getmParentalControl() == 2) {

                SharedPreferences loginSettings = mContext.getSharedPreferences("loginSettings", 0);
                SharedPreferences.Editor editor = loginSettings.edit();
                editor.putString("user_profile", String.valueOf(card.getmId()));
                editor.putString("user_type", "0");
                editor.commit();

                MainActivity.user_profile = String.valueOf(card.getmId());
                getActivity().finish();
            } else {
                Intent intent = new Intent(mContext,
                        AccountProfilePasswordActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
                getActivity().finish();
            }

        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            playSound();
        }
    }


}

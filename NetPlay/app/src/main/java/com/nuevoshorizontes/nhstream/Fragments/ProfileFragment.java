package com.flynetwifi.nhstream.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flynetwifi.nhstream.AccountProfilePasswordActivity;
import com.flynetwifi.nhstream.Cards.AccountProfileCard;
import com.flynetwifi.nhstream.Constants;
import com.flynetwifi.nhstream.MainActivity;
import com.flynetwifi.nhstream.Presenters.AccountProfilesPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.nhstream.Rows.AccountProfilesRow;
import com.flynetwifi.nhstream.Utils.DownloadData;
//import PicassoBackgroundManagerTarget;
import com.flynetwifi.nhstream.Utils.GlideBackgroundManagerTarget;
import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;

public class ProfileFragment extends BrowseFragment {

    public static final String TAG = "ProfileFragment";

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private ArrayObjectAdapter mRowsAdapter;
    private Context mContext;
    private AccountProfilesRow data = null;
    private BackgroundManager backgroundManager;
//    private PicassoBackgroundManagerTarget mBackgroundTarget;
    private GlideBackgroundManagerTarget mBackgroundTarget;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity().getBaseContext();
        prepareBackgroundManager();
        setupUIElements();
        setupRowAdapter();
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
        //setTitle(getString(R.string.app_name));
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(false);
        //setBrandColor(getResources().getColor(R.color.colorPrimary));
//        backgroundManager = BackgroundManager.getInstance(getActivity());
//        backgroundManager.attach(getActivity().getWindow());
//        mBackgroundTarget = new GlideBackgroundManagerTarget(backgroundManager);
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(R.drawable.bg_poster)
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap resource,
                                                GlideAnimation<? super Bitmap>
                                                        glideAnimation) {
                        backgroundManager.setBitmap(resource);
                    }
                });
//        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
//        Picasso.with(getActivity()).load(R.drawable.blackwall).skipMemoryCache()
//                .into(mBackgroundTarget);
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

        if (data.getProfileCards().length<=1){
            AccountProfileCard card = data.getProfileCards()[0];
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

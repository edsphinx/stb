package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
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
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nuevoshorizontes.nhstream.AccountProfilePasswordActivity;
import com.nuevoshorizontes.nhstream.Cards.AccountProfileCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.LiveActivity;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.Presenters.AccountProfilesPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.AccountProfilesRow;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
//import PicassoBackgroundManagerTarget;
import com.nuevoshorizontes.nhstream.Utils.GlideBackgroundManagerTarget;
import com.google.gson.Gson;

public class ProfileFragment extends BrowseFragment {

    public static final String TAG = "ProfileFragment";

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private ArrayObjectAdapter mRowsAdapter;
    private Context mContext;
    private AccountProfilesRow data = null;
    private BackgroundManager backgroundManager;
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

                //String temp_token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImNkZjJlYzZjNWQzNDg5YWNiMTViZGIxM2I0ZDM1ZWYyYWU0M2NiYzFkNzE3YzNjY2RmMTIzZmJkZDFjNTllNWMwYTA3MDUxMWVhZGYzOTUzIn0.eyJhdWQiOiIyIiwianRpIjoiY2RmMmVjNmM1ZDM0ODlhY2IxNWJkYjEzYjRkMzVlZjJhZTQzY2JjMWQ3MTdjM2NjZGYxMjNmYmRkMWM1OWU1YzBhMDcwNTExZWFkZjM5NTMiLCJpYXQiOjE0OTY2OTkxMjQsIm5iZiI6MTQ5NjY5OTEyNCwiZXhwIjoxNDk2NzQyMzI0LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.G-EwL401lcUVG3Lt-g4GhncsTY-kmd0_-ksA2plAiAT5rTFlIbC68l9RV2XIl9BecuKAD_cZcnyiO4KBqGUmgKISveiAOJi4pk-hn1t2AdGrgXvW4LTGuW4j0adifzBbp7CuCaSuM5noGeFNmtvaPXz-iaY587xwtOUOKT50P1pi6tWhRQq6yg1lYvHtSIw_mD1iBYhCNrRHnvsNMDoarcXiE9coYS_hB4OUpWglRJ7JdmgtlI6H4egx_wkGtz2Uu1jCA_DFAtjLco5Ff6UuTGwO4fbqbhcBarBDvBu8_Ahq9lTQd9mjTAvhrzQgVi4H5si47i4mVNwYnHOKFPJNQCiL8MhzogLLT3AVL8LMHL5h922I2VYseCpU9HhI0TZ4ZkDegacFB7r8wufrhpiRwSYl4v_CVLsTifftp4yMvpeERgZmdtGwe_68MVPEhSRgbgXbkaulWWVC33gukrO3A7zEVa5XxcOSnRQV_Cbe_klIZygORy1SMd8J1HUt6OFdqHuYSRcDsxQOyEQXw3t4bBc8TBBjnmH2klYNStT7xmaWvux8jVzjbugQGByyl5UhJqweSUAsvLFoNC-zxqReqf00Q-1BobAoeUPCJY8OBo7_b8mX_V07G7jhTv3n3Kgipikk-WojATctsjwD34RYDwZ-A_cBtERdtrrRmw3A6is";

                String request = Constants.server + Constants.profiles;
                String response = downloadData.run(getActivity().getBaseContext(),  MainActivity.access_token, true, request);
                try {
                    data = new Gson().fromJson(response, AccountProfilesRow.class);
                }catch (Exception egson){
                    Log.e(TAG, egson.toString());
                }
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
        }else{
            AccountProfileCard card = null;
            for (int i = 0; i < data.getProfileCards().length; i++ ){
                card = data.getProfileCards()[i];
                if(card.getmParentalControl() == 2){
                    SharedPreferences loginSettings = mContext.getSharedPreferences("loginSettings", 0);
                    SharedPreferences.Editor editor = loginSettings.edit();
                    editor.putString("user_profile", String.valueOf(card.getmId()));
                    editor.putString("user_type", "0");
                    editor.commit();
                    MainActivity.user_profile = String.valueOf(card.getmId());
                }
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
            Intent intent = null;
            if (card.getmParentalControl() == 2) {

                SharedPreferences loginSettings = mContext.getSharedPreferences("loginSettings", 0);
                SharedPreferences.Editor editor = loginSettings.edit();
                editor.putString("user_profile", String.valueOf(card.getmId()));
                editor.putString("user_type", "0");
                editor.commit();
                MainActivity.user_profile = String.valueOf(card.getmId());
                if(card.getmInterface() == 0){
                    intent = new Intent(getActivity().getBaseContext(),
                            LiveActivity.class);
                    intent.putExtra("user_profile", MainActivity.user_profile);
                    intent.putExtra("user_type", "0");//MainActivity.user_type);
                    intent.putExtra("access_token", MainActivity.access_token);
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                            .toBundle();
                    startActivity(intent, bundle);

                }else{
                    getActivity().finish();
                }
            } else {
                intent = new Intent(mContext,
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

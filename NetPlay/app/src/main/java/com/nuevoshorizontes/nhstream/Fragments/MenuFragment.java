package com.nuevoshorizontes.nhstream.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nuevoshorizontes.nhstream.AccountActivity;
import com.nuevoshorizontes.nhstream.AppsActivity;
import com.nuevoshorizontes.nhstream.Cards.MenuCard;
import com.nuevoshorizontes.nhstream.LiveActivity;
import com.nuevoshorizontes.nhstream.LiveCategoriesActivity;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.MessagesActivity;
import com.nuevoshorizontes.nhstream.MovieActivity;
import com.nuevoshorizontes.nhstream.MusicActivity;
import com.nuevoshorizontes.nhstream.Presenters.MenuPresenter;
import com.nuevoshorizontes.nhstream.Presenters.MenuPresenterSelector;
import com.nuevoshorizontes.nhstream.ProfileActivity;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.MenuListRow;
import com.nuevoshorizontes.nhstream.Rows.MenuRow;
import com.nuevoshorizontes.nhstream.SeriesActivity;
//import PicassoBackgroundManagerTarget;
import com.nuevoshorizontes.nhstream.Utils.GlideBackgroundManagerTarget;
import com.nuevoshorizontes.nhstream.Utils.Utils;
import com.nuevoshorizontes.nhstream.VODSelectionActivity;
import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends BrowseFragment implements OnItemViewSelectedListener {

    private ArrayObjectAdapter mRowsAdapter;
    private Context mContext;
    private ListRow mainMenu;
    private MenuListRow subMenu;
    private int currentMenu = 0;

    private DisplayMetrics mMetrics;
    private Drawable mDefaultBackground;

    private BackgroundManager backgroundManager;
    //private PicassoBackgroundManagerTarget mBackgroundTarget;
    private GlideBackgroundManagerTarget mBackgroundTarget;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        prepareBackgroundManager();
        setupUIElements();
        setupRowAdapter();
        setupEventListeners();

    }

    @Override
    public void onStart(){
        super.onStart();

        //startLiveTV();
    }

    private void startLiveTV(){
        Intent intent = new Intent(getActivity().getBaseContext(),
                LiveActivity.class);

        if (intent != null) {
            intent.putExtra("user_profile", MainActivity.user_profile);
            intent.putExtra("user_type", MainActivity.user_type);
            intent.putExtra("access_token", MainActivity.access_token);
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                    .toBundle();
            startActivity(intent, bundle);
        }
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
        setBrandColor(getResources().getColor(R.color.colorPrimary));

        //backgroundManager = BackgroundManager.getInstance(getActivity());
        //backgroundManager.attach(getActivity().getWindow());
        //mBackgroundTarget = new GlideBackgroundManagerTarget(backgroundManager);
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
//        Glide.with(getActivity())
//                .load(R.drawable.bg_default)
//                .skipMemoryCache(true)
//                .into(mBackgroundTarget);
//        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
//        Picasso.with(getActivity()).load(R.drawable.bg_default).skipMemoryCache()
//                .into(mBackgroundTarget);

    }

    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new MenuPresenterSelector());

        createRows();
        startEntranceTransition();
        setAdapter(mRowsAdapter);
    }

    private void createRows() {
        currentMenu = 0;
        mRowsAdapter.clear();

        final MenuPresenter presenter = new MenuPresenter();

        String json = Utils.inputStreamToString(getResources().openRawResource(R.raw.menu_data));
        MenuRow[] rows = new Gson().fromJson(json, MenuRow[].class);

        mRowsAdapter.add(new SectionRow(new HeaderItem("Menu")));

        for(MenuRow row : rows){
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
            List<MenuCard> listMenuCard = new ArrayList<>();

            for(MenuCard card : row.getmCards()){
                listRowAdapter.add(card);
                listMenuCard.add(card);
            }

            MenuRow menuRow = new MenuRow();
            menuRow.setmCards(listMenuCard);



            MenuListRow listRow = new MenuListRow(
                    new HeaderItem(""),
                    listRowAdapter,
                    row
            );


            mRowsAdapter.add(0, listRow);

        }



    }

    private ListRow createCardRow(MenuRow cardRow) {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MenuPresenter());
        for (MenuCard card : cardRow.getmCards()) {
            listRowAdapter.add(card);
        }
        return mainMenu = new ListRow(listRowAdapter);
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(this);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof MenuCard) {
                //Evento Select

            }

    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (MainActivity.user_profile == "") {
                Intent intent = new Intent(getActivity().getBaseContext(),
                        ProfileActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            } else {
                Intent intent = null;
                MenuCard card = (MenuCard) item;
                int id = card.getmId();
                switch (id) {
                    case 0: {
                        intent = new Intent(getActivity().getBaseContext(),
                                LiveActivity.class);
                        break;
                    }
                    case 11: {
                        intent = new Intent(getActivity().getBaseContext(),
                                LiveActivity.class);
                        break;
                    }
                    case 12: {
                        intent = new Intent(getActivity().getBaseContext(),
                                LiveCategoriesActivity.class);
                        break;
                    }
                    case 1:
                        intent = new Intent(getActivity().getBaseContext(),
                                VODSelectionActivity.class);
                        break;

                    case 2: {
                        intent = new Intent(getActivity().getBaseContext(),
                                MusicActivity.class);
                        break;
                    }
                    case 3: {
                        intent = new Intent(getActivity().getBaseContext(),
                                AccountActivity.class);
                        break;
                    }
                    case 4: {
                        intent = new Intent(getActivity().getBaseContext(),
                                AppsActivity.class);
                        break;
                    }
                    case 5: {
                        intent = new Intent(getActivity().getBaseContext(),
                                MessagesActivity.class);
                        break;
                    }

                    case 21:{
                        intent = new Intent(getActivity().getBaseContext(),
                                MovieActivity.class);
                        break;
                    }

                    case 22: {
                        intent = new Intent(getActivity().getBaseContext(),
                                SeriesActivity.class);
                        break;
                    }

                    default:
                        break;
                }
                if (intent != null) {
                    intent.putExtra("user_profile", MainActivity.user_profile);
                    intent.putExtra("user_type", MainActivity.user_type);
                    intent.putExtra("access_token", MainActivity.access_token);
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                            .toBundle();
                    startActivity(intent, bundle);
                }
            }
        }
    }



}

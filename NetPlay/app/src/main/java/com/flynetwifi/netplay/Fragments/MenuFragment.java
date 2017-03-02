package com.flynetwifi.netplay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.flynetwifi.netplay.AccountActivity;
import com.flynetwifi.netplay.AppsActivity;
import com.flynetwifi.netplay.Cards.MenuCard;
import com.flynetwifi.netplay.LiveActivity;
import com.flynetwifi.netplay.LiveCategoriesActivity;
import com.flynetwifi.netplay.MainActivity;
import com.flynetwifi.netplay.MessagesActivity;
import com.flynetwifi.netplay.MovieActivity;
import com.flynetwifi.netplay.MusicActivity;
import com.flynetwifi.netplay.Presenters.MenuPresenter;
import com.flynetwifi.netplay.Presenters.MenuPresenterSelector;
import com.flynetwifi.netplay.ProfileActivity;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.MenuListRow;
import com.flynetwifi.netplay.Rows.MenuRow;
import com.flynetwifi.netplay.SeriesActivity;
import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.flynetwifi.netplay.Utils.Utils;
import com.flynetwifi.netplay.VODSelectionActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends BrowseFragment implements OnItemViewSelectedListener {

    private ArrayObjectAdapter mRowsAdapter;
    private Context mContext;
    private ListRow mainMenu;
    private MenuListRow subMenu;
    private int currentMenu = 0;

    private BackgroundManager backgroundManager;
    private PicassoBackgroundManagerTarget mBackgroundTarget;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        setupUIElements();
        setupRowAdapter();
        setupEventListeners();
    }

    private void setupUIElements() {
        //setTitle(getString(R.string.app_name));
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(false);
        setBrandColor(getResources().getColor(R.color.colorPrimary));

        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        Picasso.with(getActivity()).load(R.drawable.bg_default).skipMemoryCache()
                .into(mBackgroundTarget);

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

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
        MenuRow row = new MenuRow();


         subMenu = new MenuListRow(
                new HeaderItem(""),
                listRowAdapter,
                row
        );

        mRowsAdapter.add(1, subMenu);



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
                MenuCard menuCard = (MenuCard) item;
                if(menuCard.getmId() >= 0 && menuCard.getmId() < 6) {
                    currentMenu = menuCard.getmId();
                    mHandler.postDelayed(mRunnable, 500);
                }
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

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            String json = "";
            switch (currentMenu) {
                case 0:
                    json = Utils.inputStreamToString(getResources().openRawResource(R.raw.menu_live));
                    break;
                case 1:
                    json = Utils.inputStreamToString(getResources().openRawResource(R.raw.menu_vod));
                    break;
                default:
                    json = "";

            }

            if (!json.contentEquals("")) {
                MenuRow[] rows = new Gson().fromJson(json, MenuRow[].class);
                MenuPresenter presenter = new MenuPresenter();

                for(MenuRow row : rows){
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
                    List<MenuCard> listMenuCard = new ArrayList<>();

                    for(MenuCard card : row.getmCards()){
                        listRowAdapter.add(card);
                        listMenuCard.add(card);
                    }

                    MenuRow menuRow = new MenuRow();
                    menuRow.setmCards(listMenuCard);

                    subMenu = new MenuListRow(
                            new HeaderItem(""),
                            listRowAdapter,
                            row
                    );

                    mRowsAdapter.replace(1, subMenu);

                }

            } else {
                mRowsAdapter.replace(1, subMenu);
            }
        }
    };

}

package com.flynetwifi.netplay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import com.flynetwifi.netplay.AccountActivity;
import com.flynetwifi.netplay.AppsActivity;
import com.flynetwifi.netplay.Cards.MenuCard;
import com.flynetwifi.netplay.LiveActivity;
import com.flynetwifi.netplay.MainActivity;
import com.flynetwifi.netplay.MessagesActivity;
import com.flynetwifi.netplay.MusicActivity;
import com.flynetwifi.netplay.Presenters.MenuPresenter;
import com.flynetwifi.netplay.ProfileActivity;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.MenuRow;
import com.flynetwifi.netplay.Utils.Utils;
import com.flynetwifi.netplay.VODSelectionActivity;
import com.google.gson.Gson;


public class MenuFragment extends BrowseFragment {

    private ArrayObjectAdapter mRowsAdapter;
    private Context mContext;
    private MediaPlayer mp;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mp = MediaPlayer.create(mContext, R.raw.menu_selection);
        setupUIElements();
        setupRowAdapter();
        setupEventListeners();
    }

    private void setupUIElements() {
        setTitle(getString(R.string.app_name));
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(false);
        setBrandColor(getResources().getColor(R.color.colorPrimary));

    }

    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        createRows();
        setAdapter(mRowsAdapter);
    }

    private void createRows() {
        String json = Utils.inputStreamToString(getResources().openRawResource(R.raw.menu_data));
        MenuRow[] rows = new Gson().fromJson(json, MenuRow[].class);
        for (MenuRow row : rows)
            mRowsAdapter.add(createCardRow(row));
    }

    private ListRow createCardRow(MenuRow cardRow) {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MenuPresenter());
        for (MenuCard card : cardRow.getmCards()) {
            listRowAdapter.add(card);
        }
        return new ListRow(listRowAdapter);
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void playSound() {
        /*Thread thread = new Thread() {
            public void run() {
                mp.start();
            }
        };
        thread.start();*/
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
                    default:
                        break;
                }
                if (intent != null) {
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                            .toBundle();
                    startActivity(intent, bundle);
                }
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

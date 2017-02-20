package com.flynetwifi.netplay.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SearchBar;
import android.support.v17.leanback.widget.SearchEditText;
import android.support.v17.leanback.widget.SearchOrbView;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.flynetwifi.netplay.Cards.SeriesCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.Presenters.SeriesPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.SeriesListRow;
import com.flynetwifi.netplay.Rows.SeriesRow;
import com.flynetwifi.netplay.SeriesSeasonsActivity;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesSearchFragment extends android.support.v17.leanback.app.SearchFragment
        implements SearchFragment.SearchResultProvider, OnItemViewClickedListener {

    public static final String TAG = "SeriesSearchFragment";

    private ArrayObjectAdapter mRowsAdapter;
    private Map<String, SeriesCard[]> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        setSearchResultProvider(this);
        setOnItemViewClickedListener(this);

        setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
            @Override
            public void recognizeSpeech() {

            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchBar searchBar = (SearchBar) view.findViewById(R.id.lb_search_bar);

        SearchOrbView searchOrbView = (SearchOrbView) view.findViewById(R.id.lb_search_bar_speech_orb);
        searchOrbView.setVisibility(View.GONE);
        searchBar.setSearchBarListener(null);

        final SearchEditText searchEditText = (SearchEditText) view.findViewById(R.id.lb_search_text_editor);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    loadRows(charSequence.toString());
                } else {
                    mRowsAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadRows(final String query) {
        mRowsAdapter.clear();
        final SeriesPresenter presenter = new SeriesPresenter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.series + query);

                    Gson gson = new Gson();
                    Type seriesCardType;
                    seriesCardType = new TypeToken<Map<String, SeriesCard[]>>() {

                    }.getType();
                    data = gson.fromJson(response, seriesCardType);

                    for (HashMap.Entry<String, SeriesCard[]> entry : data.entrySet()) {
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
                        List<SeriesCard> listSeriesCard = new ArrayList<>();
                        for (SeriesCard card : entry.getValue()) {
                            listRowAdapter.add(card);
                            listSeriesCard.add(card);
                        }
                        SeriesRow row = new SeriesRow();
                        row.setmTitle(entry.getKey());
                        row.setmCards(listSeriesCard);
                        SeriesListRow listRow = new SeriesListRow(
                                new HeaderItem(entry.getKey()),
                                listRowAdapter,
                                row);
                        mRowsAdapter.add(listRow);

                    }
                } catch (JsonParseException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {
        SeriesCard model = (SeriesCard) item;
        Intent intent  = new Intent(getActivity().getBaseContext(),
                SeriesSeasonsActivity.class);
        SeriesFragment.id = String.valueOf(model.getmId());
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                .toBundle();
        startActivity(intent, bundle);
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        loadRows(newQuery);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadRows(query);
        return false;
    }
}


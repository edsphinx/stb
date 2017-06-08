package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import com.nuevoshorizontes.nhstream.Cards.MovieCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MovieDetailActivity;
import com.nuevoshorizontes.nhstream.Presenters.MoviePresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.MoviesListRow;
import com.nuevoshorizontes.nhstream.Rows.MoviesRow;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nuevoshorizontes.nhstream.MainActivity.access_token;


public class MoviesSearchFragment extends android.support.v17.leanback.app.SearchFragment
        implements android.support.v17.leanback.app.SearchFragment.SearchResultProvider,
        OnItemViewClickedListener {

    public static final String TAG = "MovieSearchFragment";

    private ArrayObjectAdapter mRowsAdapter;
    private Map<String, MovieCard[]> data;

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
        final MoviePresenter presenter = new MoviePresenter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mRowsAdapter.clear();
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity().getBaseContext(), access_token, false, Constants.server + Constants.movies + query);

                    Gson gson = new Gson();
                    Type peliculasCardType;
                    peliculasCardType = new TypeToken<Map<String, MovieCard[]>>() {

                    }.getType();
                    data = gson.fromJson(response, peliculasCardType);

                    for (HashMap.Entry<String, MovieCard[]> entry : data.entrySet()) {
                        String key = entry.getKey();

                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
                        List<MovieCard> listPeliculasCard = new ArrayList<>();

                        for (MovieCard card : entry.getValue()) {
                            listRowAdapter.add(card);
                            listPeliculasCard.add(card);
                        }
                        MoviesRow row = new MoviesRow();
                        row.setmTitle(key);
                        row.setmCards(listPeliculasCard);
                        MoviesListRow listRow = new MoviesListRow(new HeaderItem(key), listRowAdapter, row);
                        mRowsAdapter.add(listRow);

                    }


                }  catch (JsonParseException e1) {
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

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        MovieCard model = (MovieCard) item;
        Intent intent = new Intent(getActivity().getBaseContext(),
                MovieDetailActivity.class);
        MoviesFragment.id = String.valueOf(model.getmId());
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                .toBundle();
        startActivity(intent, bundle);
    }
}

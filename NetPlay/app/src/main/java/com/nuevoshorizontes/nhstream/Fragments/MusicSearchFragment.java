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

import com.nuevoshorizontes.nhstream.Cards.MusicSongCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MusicPlayerActivity;
import com.nuevoshorizontes.nhstream.Presenters.MusicSongPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.MusicSongListRow;
import com.nuevoshorizontes.nhstream.Rows.MusicSongsRow;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nuevoshorizontes.nhstream.MainActivity.access_token;


public class MusicSearchFragment extends android.support.v17.leanback.app.SearchFragment
        implements android.support.v17.leanback.app.SearchFragment.SearchResultProvider,
        OnItemViewClickedListener {

    public static final String TAG = "MusicSearchFragment";

    private ArrayObjectAdapter mRowsAdapter;
    private Map<String, MusicSongCard[]> data;

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
        final MusicSongPresenter presenter = new MusicSongPresenter();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mRowsAdapter.clear();
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.music_song_query + query);

                    Gson gson = new Gson();
                    Type songCardType;
                    songCardType = new TypeToken<Map<String, MusicSongCard[]>>(){

                    }.getType();
                    data = gson.fromJson(response, songCardType);
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
                    for (HashMap.Entry<String, MusicSongCard[]> entry : data.entrySet()) {
                        String key = entry.getKey();


                        List<MusicSongCard> musicSongCards = new ArrayList<>();

                        for (MusicSongCard card : entry.getValue()) {
                            listRowAdapter.add(card);
                            musicSongCards.add(card);
                        }

                        MusicSongsRow row = new MusicSongsRow();
                        row.setmTitle(key);
                        row.setmCards(musicSongCards);
                        MusicSongListRow listRow = new MusicSongListRow(new HeaderItem(key), listRowAdapter, row);
                        mRowsAdapter.add(listRow);

                    }

                }
                catch (Exception e){

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

        Intent intent = null;
        intent = new Intent(getActivity(),
                MusicPlayerActivity.class);
        if(item instanceof MusicSongCard){
            MusicSongCard model = (MusicSongCard) item;
            intent.putExtra("tipo", "3" );
            intent.putExtra("id", model.getId() );
        }
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                .toBundle();
        startActivity(intent, bundle);

    }
}

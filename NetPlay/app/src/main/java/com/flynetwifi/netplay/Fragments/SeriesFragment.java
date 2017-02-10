package com.flynetwifi.netplay.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.flynetwifi.netplay.Cards.SeriesCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.Presenters.SeriesPresenter;
import com.flynetwifi.netplay.Presenters.SeriesRowPresenterSelector;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.SeriesListRow;
import com.flynetwifi.netplay.Rows.SeriesRow;
import com.flynetwifi.netplay.SeriesSearchActivity;
import com.flynetwifi.netplay.SeriesSeasonsActivity;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.Utils.PicassoBackgroundManagerTarget;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesFragment extends BrowseFragment {

    private ArrayObjectAdapter mRowsAdapter;
    public BackgroundManager backgroundManager;
    public PicassoBackgroundManagerTarget mBackgroundTarget;

    public static String id = "0";
    public Map<String, SeriesCard[]> data;

    @Override
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setupUi();
        setupRowAdapter();
    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getActivity().getResources().getColor(R.color.colorPrimary));

        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                SeriesCard model = (SeriesCard) item;
                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        SeriesSeasonsActivity.class);
                id = String.valueOf(model.getmId());
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof SeriesCard) {
                    SeriesCard model = (SeriesCard) item;
                    Picasso.with(getActivity()).load(model.getmPortada()).skipMemoryCache()
                            .into(mBackgroundTarget);
                }
            }
        });
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        SeriesSearchActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });
        prepareEntranceTransition();
    }


    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new SeriesRowPresenterSelector());
        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 500);
    }

    private void createRows() {
        mRowsAdapter.clear();
        final SeriesPresenter presenter = new SeriesPresenter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.series);

                    Gson gson = new Gson();
                    Type seriesCardType;
                    seriesCardType = new TypeToken<Map<String, SeriesCard[]>>(){

                    }.getType();
                    data = gson.fromJson(response, seriesCardType);

                    mRowsAdapter.add(new SectionRow(new HeaderItem("Series")));
                    for(HashMap.Entry<String, SeriesCard[]> entry : data.entrySet()){
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
                        List<SeriesCard> listSeriesCard = new ArrayList<>();
                        for(SeriesCard card: entry.getValue()){
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
                }
                catch (JsonParseException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e2) {

                }
            }
        });
        thread.start();

        try{
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }


    }

}


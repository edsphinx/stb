package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.nuevoshorizontes.nhstream.Cards.MusicCard;
import com.nuevoshorizontes.nhstream.Cards.MusicGendersCard;
import com.nuevoshorizontes.nhstream.Cards.MusicPlaylistCard;
import com.nuevoshorizontes.nhstream.Cards.MusicSingersCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.MusicPlayerActivity;
import com.nuevoshorizontes.nhstream.MusicPlaylistActivity;
import com.nuevoshorizontes.nhstream.MusicSearchActivity;
import com.nuevoshorizontes.nhstream.Presenters.MusicGendersPresenter;
import com.nuevoshorizontes.nhstream.Presenters.MusicPlaylistPresenter;
import com.nuevoshorizontes.nhstream.Presenters.MusicSingersPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.MusicGendersListRow;
import com.nuevoshorizontes.nhstream.Rows.MusicGendersRow;
import com.nuevoshorizontes.nhstream.Rows.MusicPlaylistListRow;
import com.nuevoshorizontes.nhstream.Rows.MusicPlaylistRow;
import com.nuevoshorizontes.nhstream.Rows.MusicSingersListRow;
import com.nuevoshorizontes.nhstream.Rows.MusicSingersRow;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
//import PicassoBackgroundManagerTarget;
import com.nuevoshorizontes.nhstream.Utils.GlideBackgroundManagerTarget;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nuevoshorizontes.nhstream.MainActivity.access_token;

public class MusicFragment extends BrowseFragment {
    public static final String TAG = "MusicFragment";
    private ArrayObjectAdapter mRowsAdapter;
    private BackgroundManager backgroundManager;
//    private PicassoBackgroundManagerTarget mBackgroundTarget;
    private GlideBackgroundManagerTarget mBackgroundTarget;

    private Map<String, MusicCard> data;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        setupRowAdapter();
    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setTitle(getString(R.string.music_on_demand));
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
//        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        mBackgroundTarget = new GlideBackgroundManagerTarget(backgroundManager);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                Intent intent = null;
                intent = new Intent(getActivity(),
                        MusicPlayerActivity.class);
                if(item instanceof MusicSingersCard){
                    MusicSingersCard model = (MusicSingersCard) item;
                    intent.putExtra("tipo", "2" );
                    intent.putExtra("id", model.getmId() );
                }
                if(item instanceof MusicPlaylistCard){
                    MusicPlaylistCard model = (MusicPlaylistCard) item;
                    if(model.getmId().contentEquals("0")){
                        intent = new Intent(getActivity(),
                                MusicPlaylistActivity.class);
                        getActivity().finish();
                    }else {
                        intent.putExtra("tipo", "0");
                        intent.putExtra("id", model.getmId());
                    }
                }
                if(item instanceof MusicGendersCard){
                    MusicGendersCard model = (MusicGendersCard) item;
                    intent.putExtra("tipo", "1" );
                    intent.putExtra("id", model.getmId() );
                }
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

            }
        });

        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(getActivity(),
                        MusicSearchActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
            }
        });

        prepareEntranceTransition();
    }

    private void setupRowAdapter() {

        ListRowPresenter listRowPresenter = new ListRowPresenter();
        listRowPresenter.setNumRows(1);
        listRowPresenter.setShadowEnabled(true);

        mRowsAdapter = new ArrayObjectAdapter(listRowPresenter);
        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadRows();
                startEntranceTransition();
            }
        }, 500);
    }

    private void loadRows() {
        mRowsAdapter.clear();
        mRowsAdapter.add(new SectionRow(new HeaderItem(getString(R.string.music))));

        loadPlaylists();
        loadSingers();
        loadGenders();

    }

    private void loadPlaylists(){
        final MusicPlaylistPresenter playlistPresenter = new MusicPlaylistPresenter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.music_playlist + MainActivity.user_profile);

                    Gson gson = new Gson();
                    Type musicaCardType;
                    musicaCardType = new TypeToken<Map<String, MusicCard>>() {

                    }.getType();
                    data = gson.fromJson(response, musicaCardType);


                    for (HashMap.Entry<String, MusicCard> entry : data.entrySet()) {
                        MusicCard model =  entry.getValue();

                        ArrayObjectAdapter listRowAdapter = null;
                        List<MusicPlaylistCard> listPlaylistModel = new ArrayList<>();
                        //List<MusicSingersCard> listCantantesModel = new ArrayList<>();
                        //List<MusicGendersCard> listGenerosModel = new ArrayList<>();


                        if (model.getmTipo() == 0) {
                            listRowAdapter = new ArrayObjectAdapter(playlistPresenter);
                            MusicPlaylistRow row = new MusicPlaylistRow();

                            MusicPlaylistCard dataModel = new MusicPlaylistCard();
                            dataModel.setmId("0");
                            dataModel.setmNombre(getString(R.string.add_playlist));
                            dataModel.setmNumero("");
                            listRowAdapter.add(dataModel);
                            listPlaylistModel.add(dataModel);


                            for (int i = 0; i < model.getmData().length; i++) {

                                String id = "";
                                String nombre = "";
                                String numero = "";
                                for (Map.Entry<String, String> map : model.getmData()[i].entrySet()) {
                                    if (map.getKey().contentEquals("id")) {
                                        id = map.getValue();
                                    }
                                    if (map.getKey().contentEquals("nombre")) {
                                        nombre = map.getValue();
                                    }
                                    if (map.getKey().contentEquals("numero")) {
                                        numero = map.getValue();
                                    }
                                }
                                dataModel = new MusicPlaylistCard();
                                dataModel.setmId(id);
                                dataModel.setmNombre(nombre);
                                dataModel.setmNumero(numero);
                                listRowAdapter.add(dataModel);
                                listPlaylistModel.add(dataModel);

                            }
                            row.setPlaylists(listPlaylistModel);
                            MusicPlaylistListRow playlistListRow = new MusicPlaylistListRow(
                                    new HeaderItem(entry.getKey()),
                                    listRowAdapter,
                                    row);
                            mRowsAdapter.add(playlistListRow);
                        }
                    }
                }
                catch (JsonParseException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e2) {

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

    private void loadSingers(){
        final MusicSingersPresenter cantantesPresenter = new MusicSingersPresenter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.music_singers);

                    Gson gson = new Gson();
                    Type musicaCardType;
                    musicaCardType = new TypeToken<Map<String, MusicCard>>() {

                    }.getType();
                    data = gson.fromJson(response, musicaCardType);


                    for (HashMap.Entry<String, MusicCard> entry : data.entrySet()) {
                        MusicCard model = entry.getValue();

                        ArrayObjectAdapter listRowAdapter = null;
                        List<MusicSingersCard> listCantantesModel = new ArrayList<>();


                        if (model.getmTipo() == 2) {
                            listRowAdapter = new ArrayObjectAdapter(cantantesPresenter);
                            MusicSingersRow row = new MusicSingersRow();

                            for (int i = 0; i < model.getmData().length; i++) {

                                String id = "";
                                String nombre = "";
                                String imagen = "";
                                for (Map.Entry<String, String> map : model.getmData()[i].entrySet()) {

                                    if (map.getKey().contentEquals("nombre")) {
                                        nombre = map.getValue();

                                    }
                                    if (map.getKey().contentEquals("id")) {
                                        id = map.getValue();

                                    }
                                    if (map.getKey().contentEquals("imagen")) {
                                        imagen = map.getValue();
                                    }
                                }
                                MusicSingersCard cantantesModel = new MusicSingersCard();
                                cantantesModel.setmId(id);
                                cantantesModel.setmNombre(nombre);
                                cantantesModel.setmImagen(imagen);
                                listRowAdapter.add(cantantesModel);
                                listCantantesModel.add(cantantesModel);

                            }
                            row.setCantantes(listCantantesModel);
                            MusicSingersListRow cantantesListRow = new MusicSingersListRow(
                                    new HeaderItem(entry.getKey()),
                                    listRowAdapter,
                                    row);
                            mRowsAdapter.add(cantantesListRow);
                        }
                    }
                }
                catch (JsonParseException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e2) {

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

    private void loadGenders(){

        final MusicGendersPresenter generosPresenter = new MusicGendersPresenter();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.music_genders);

                    Gson gson = new Gson();
                    Type musicaCardType;
                    musicaCardType = new TypeToken<Map<String, MusicCard>>() {

                    }.getType();
                    data = gson.fromJson(response, musicaCardType);


                    for (HashMap.Entry<String, MusicCard> entry : data.entrySet()) {
                        MusicCard model = entry.getValue();

                        ArrayObjectAdapter listRowAdapter = null;
                        List<MusicGendersCard> listGenerosModel = new ArrayList<>();


                        if (model.getmTipo() == 1) {
                            listRowAdapter = new ArrayObjectAdapter(generosPresenter);
                            MusicGendersRow row = new MusicGendersRow();

                            for (int i = 0; i < model.getmData().length; i++) {

                                String id = "";
                                String nombre = "";
                                String imagen = "";
                                for (Map.Entry<String, String> map : model.getmData()[i].entrySet()) {

                                    if (map.getKey().contentEquals("nombre")) {
                                        nombre = map.getValue();
                                    }
                                    if (map.getKey().contentEquals("id")) {
                                        id = map.getValue();

                                    }
                                    if (map.getKey().contentEquals("imagen")) {
                                        imagen = map.getValue();
                                    }
                                }
                                MusicGendersCard generosModel = new MusicGendersCard();
                                generosModel.setmId(id);
                                generosModel.setmNombre(nombre);
                                generosModel.setmImagen(imagen);
                                listRowAdapter.add(generosModel);
                                listGenerosModel.add(generosModel);

                            }
                            row.setGeneros(listGenerosModel);
                            MusicGendersListRow generosListRow = new MusicGendersListRow(
                                    new HeaderItem(entry.getKey()),
                                    listRowAdapter,
                                    row);
                            mRowsAdapter.add(generosListRow);
                        }
                    }
                }
                catch (JsonParseException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e2) {

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


}

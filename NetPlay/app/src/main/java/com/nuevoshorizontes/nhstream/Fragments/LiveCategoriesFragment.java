package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.nuevoshorizontes.nhstream.Cards.LiveCanalCard;
import com.nuevoshorizontes.nhstream.Cards.LiveProgramCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.MediaPlayers.LiveMediaPlayerGlue;
import com.nuevoshorizontes.nhstream.Presenters.LiveCanalPresenter;
import com.nuevoshorizontes.nhstream.Presenters.LiveProgramPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.LiveProgramRow;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.nuevoshorizontes.nhstream.media.MediaMetaData;
import com.nuevoshorizontes.nhstream.media.MediaPlayerGlue;
import com.nuevoshorizontes.nhstream.media.MediaUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class LiveCategoriesFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, OnItemViewSelectedListener,
        MediaPlayerGlue.OnMediaStateChangeListener {


    public static String TAG;

    private Map<String, LiveCanalCard[]> channelsMap = null;
    private Map<String, LiveCanalCard> channelsFavoritesMap = null;
    private ArrayObjectAdapter mainRowsAdapter;
    private ArrayObjectAdapter channelsRowAdapter;
    private ArrayObjectAdapter favoriteChannelsRowAdapter;
    private ArrayObjectAdapter programsRowAdapter;
    private ArrayObjectAdapter myProgramsRowAdapter;

    private PlaybackControlsRowPresenter playbackControlsRowPresenter;

    ClassPresenterSelector rowPresenterSelector;


    private LiveProgramRow programationData;
    private LiveCanalCard selectedChannel;
    private String channelNumber = "";
    private Thread thread;

    private String access_token;
    private String user_type;
    private String user_profile;

    MediaMetaData currentMetaData;


    private final Handler handlerLoadPrograms = new Handler();
    private final Handler handlerUpdateMediaPlayer = new Handler();

    private final Runnable runnableLoadPrograms = new Runnable() {
        @Override
        public void run() {

            loadChannelPrograms();
            handlerLoadPrograms.removeCallbacks(this);
        }
    };



    private LiveMediaPlayerGlue mGlue;
    private final int ROw_PLAYER = 0;
    private final int ROW_CHANNELS = 3;
    private final int ROW_PROGRAMATION = 2;
    private final int ROW_FAVORITE_CHANNELS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = getString(R.string.TAG_LIVE);

        //Variables de Session recibidas de LiveActivity
        Bundle args = getArguments();
        access_token = args.getString("access_token", "");
        user_type = args.getString("user_type", "");
        user_profile = args.getString("user_profile", "");

        /**
        Fondo de MediaPlayer
        PlaybackOverlayFragment.BG_NONE = Sin Fondo
        BG_DARK = Oscuro,
        BG_LIGHT = 50% Transparente
         */
        setBackgroundType(PlaybackOverlayFragment.BG_NONE);
        setUpMediaPlayer();

        /**Inicializando el Adaptador de todas las filas */
        setMainRowsAdapter();

        addChannelsRow();

        setAdapter(mainRowsAdapter);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }


    /**
     * Set up media player.
     */
    private void setUpMediaPlayer(){
        //Inicializar Media PLayer
        mGlue = new LiveMediaPlayerGlue(getActivity(),this) {
            @Override
            protected void onRowChanged(PlaybackControlsRow row) {

            }
        };

        //Fragment de Video
        Fragment videoSurfaceFragment = getFragmentManager()
                .findFragmentByTag(VideoSurfaceFragment.TAG);

        SurfaceView surface = (SurfaceView) videoSurfaceFragment.getView();
        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mGlue.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mGlue.resetPlayer();
                mGlue.releaseMediaSession();
                mGlue.setDisplay(null);
                mGlue.enableProgressUpdating(false);
                ;
            }
        });
    }


    /**
     * Sets main rows adapter.
     */
    public void setMainRowsAdapter() {

        //Selector que permite agregar multiples Presentadores
        rowPresenterSelector = new ClassPresenterSelector();

        //Inicialización y configuración del Presentador del MediaPlayer
        playbackControlsRowPresenter = mGlue.createControlsRowAndPresenter();
        playbackControlsRowPresenter.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        playbackControlsRowPresenter.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        playbackControlsRowPresenter.setSecondaryActionsHidden(false);
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == 0) { //Like Action

                }
                if (action.getId() == 2) {  //Repeat Action
                    final LiveCanalCard card = selectedChannel;
                    MediaMetaData currentMetaData = new MediaMetaData();
                    currentMetaData.setMediaTitle(card.getmTitle());
                    currentMetaData.setMediaSourcePath(card.getmRecord());
                    mGlue.prepareIfNeededAndPlay(currentMetaData);
                }

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mGlue.enableProgressUpdating(mGlue.hasValidMedia() && mGlue.isMediaPlaying());
        mGlue.createMediaSessionIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        if (mGlue.isMediaPlaying()) {
            boolean isVisibleBehind = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isVisibleBehind = getActivity().requestVisibleBehind(true);
            }
            if (!isVisibleBehind) {
                mGlue.pausePlayback();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().requestVisibleBehind(false);
            }
        }
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
        mGlue.enableProgressUpdating(false);
        mGlue.resetPlayer();
        mGlue.releaseMediaSession();
        mGlue.saveUIState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGlue.releaseMediaPlayer();
    }



    private void addChannelsRow() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadData downloadData = new DownloadData();
                String response = downloadData.run(getActivity(), access_token, false, Constants.server + "/stb/live/categorias" + "/"
                        + access_token + "/" + user_type);
                Gson gson = new Gson();


                try {
                    Type canalesCardType = new TypeToken<Map<String, LiveCanalCard[]>>() {
                    }.getType();

                    channelsMap = gson.fromJson(response, canalesCardType);

                } catch (Exception e) {
                    Log.w("EXCEPTION", e.toString());
                }


            }
        });
        thread.start();


        try {
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }




        //Player
        rowPresenterSelector.addClassPresenter(PlaybackControlsRow.class,
                playbackControlsRowPresenter);

        //Mi Favorite Channels
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
//Channel Rows
        int j = ROW_FAVORITE_CHANNELS;
        int posicion = 0;
        for (Map.Entry<String, LiveCanalCard[]> entry : channelsMap.entrySet()) {
            rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
            rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        }


        //Main Object
        mainRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

        mainRowsAdapter.add(ROw_PLAYER, mGlue.getControlsRow());

        //Row de Canales Favoritos
        addFavoriteChannels();

         j = ROW_FAVORITE_CHANNELS + 1;
         posicion = 0;
        for (Map.Entry<String, LiveCanalCard[]> entry : channelsMap.entrySet()) {
            j = j + 1;
            int i = 0;

            channelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
            for (LiveCanalCard card : entry.getValue()) {
                card.setmPosicion(posicion);
                card.setmRow(j);
                channelsRowAdapter.add(card);
                i++;
                posicion++;
            }
            HeaderItem header = new HeaderItem(j, entry.getKey());
            mainRowsAdapter.add(j, new ListRow(header, channelsRowAdapter));
            j++;
            header = new HeaderItem(j, "Programacion");
            mainRowsAdapter.add(j, new ListRow(header, new ArrayObjectAdapter()));


        }


    }


    private void addProgramation() {
        programsRowAdapter = new ArrayObjectAdapter(new LiveProgramPresenter());
        HeaderItem header = new HeaderItem(ROW_PROGRAMATION, getString(R.string.programation));
        mainRowsAdapter.add(ROW_PROGRAMATION, new ListRow(header, programsRowAdapter));
    }

    private void addFavoriteChannels() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                favoriteChannelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.live_favorites
                            + user_profile);


                    Type canalesCardType;
                    canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                    }.getType();


                    channelsFavoritesMap = gson.fromJson(response, canalesCardType);


                    int i = 0;
                    for (HashMap.Entry<String, LiveCanalCard> entry : channelsFavoritesMap.entrySet()) {
                        LiveCanalCard card = entry.getValue();
                        card.setmPosicion(i);
                        card.setmRow(ROW_FAVORITE_CHANNELS);
                        entry.setValue(card);
                        favoriteChannelsRowAdapter.add(entry.getValue());
                        i++;
                    }
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        HeaderItem header = new HeaderItem(ROW_FAVORITE_CHANNELS, getString(R.string.favorite_chanels));
        mainRowsAdapter.add(ROW_FAVORITE_CHANNELS, new ListRow(header, favoriteChannelsRowAdapter));
        header = new HeaderItem(ROW_PROGRAMATION, "Programacion");
        mainRowsAdapter.add(ROW_PROGRAMATION, new ListRow(header, new ArrayObjectAdapter()));
    }


    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {

        if (item instanceof LiveCanalCard) {


            if (row.getId() == ROW_FAVORITE_CHANNELS) {


                final LiveCanalCard card = (LiveCanalCard) item;
                MediaMetaData currentMetaData = new MediaMetaData();

                currentMetaData.setMediaTitle(card.getmTitle());
                currentMetaData.setMediaArtistName(card.getmDescription());
                currentMetaData.setMediaSourcePath(card.getmStream());

                mGlue.prepareIfNeededAndPlay(currentMetaData);

                selectedChannel = card;
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, 700);

            } else if (row.getId() == ROW_CHANNELS) {

                final LiveCanalCard card = (LiveCanalCard) item;
                currentMetaData = new MediaMetaData();

                currentMetaData.setMediaTitle(card.getmTitle());
                currentMetaData.setMediaArtistName(card.getmDescription());
                currentMetaData.setMediaSourcePath(card.getmStream());

                selectedChannel = card;
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, 500);
            }
        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {


        //if (row.getId() == ROW_CHANNELS) {
            if (item instanceof LiveCanalCard) {
                /*if (selectedChannel != null) {
                    selectedChannel.setmEstado(0);
                    channelsRowAdapter.replace(selectedChannel.getmPosicion(), selectedChannel);
                    channelsRowAdapter.notifyArrayItemRangeChanged(selectedChannel.getmPosicion(), 1);
                }
                LiveCanalCard card = (LiveCanalCard) item;
                card.setmEstado(1);
                channelsRowAdapter.replace(card.getmPosicion(), card);
                channelsRowAdapter.notifyArrayItemRangeChanged(card.getmPosicion(), 1);
                selectedChannel = card;*/

                selectedChannel = (LiveCanalCard) item;
                //selectedHandler.postDelayed(selectedRunnable, 500);
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, 700);


            }
        //}

    }


    private void loadChannelPrograms() {

        final LiveCanalCard card = selectedChannel;
        if (thread != null) {
            if (thread.isAlive()) {
                try {
                    thread.destroy();
                } catch (UnsupportedOperationException e) {

                }
            }
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                            new LiveProgramPresenter());
                    programationData = null;
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.programation
                            + String.valueOf(card.getmId()));

                    programationData = new Gson().fromJson(response, LiveProgramRow.class);
                    for (LiveProgramCard card : programationData.getProgramaCards()) {
                        listRowAdapter.add(card);
                    }

                    HeaderItem header = new HeaderItem(selectedChannel.getmRow()+1, getString(R.string.programation_title) + ": "
                            + card.getmTitle());
                    mainRowsAdapter.replace(selectedChannel.getmRow()+1, new ListRow(header, listRowAdapter));

                } catch (UnsupportedOperationException e1) {
                    e1.printStackTrace();
                } catch (IllegalStateException e2) {
                    e2.printStackTrace();
                } catch (JsonParseException e3) {
                    e3.printStackTrace();
                }
            }
        });

        thread.start();
    }




    @Override
    public void onMediaStateChanged(MediaMetaData currentMediaMetaData, int currentMediaState) {
        if (currentMediaState == MediaUtils.MEDIA_STATE_COMPLETED) {
            mGlue.startPlayback();
        }
    }

    /**
     * Keypress.
     *
     * @param e the e
     */
    public void keypress(KeyEvent e) {

        if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
            int numero = e.getKeyCode() - 7;
            channelNumber = channelNumber + String.valueOf(numero);
            cambiarCanalHandler.removeCallbacks(cambiarCanalRunnable);
            cambiarCanalHandler.postDelayed(cambiarCanalRunnable, 1500);
        }
    }

    /**
     * Cambiar canal.
     */
    public void cambiarCanal() {

        if (channelsMap.containsKey(channelNumber)) {
            selectedChannel.setmEstado(0);
            channelsRowAdapter.replace(selectedChannel.getmPosicion(), selectedChannel);
            channelsRowAdapter.notifyArrayItemRangeChanged(selectedChannel.getmPosicion(), 1);

            /*LiveCanalCard card = channelsMap.get(channelNumber);
            getRowsFragment().setSelectedPosition(ROW_CHANNELS, true,
                    new ListRowPresenter.SelectItemViewHolderTask(card.getmPosicion()));
            currentMetaData = new MediaMetaData();

            currentMetaData.setMediaTitle(card.getmTitle());
            currentMetaData.setMediaArtistName(card.getmDescription());
            currentMetaData.setMediaSourcePath(card.getmStream());

            selectedChannel = card;
            handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
            handlerLoadPrograms.postDelayed(runnableLoadPrograms, 500);*/

        }
        channelNumber = "";

    }

    private final Handler cambiarCanalHandler = new Handler();
    private final Runnable cambiarCanalRunnable = new Runnable() {
        @Override
        public void run() {
            cambiarCanal();
        }
    };
}

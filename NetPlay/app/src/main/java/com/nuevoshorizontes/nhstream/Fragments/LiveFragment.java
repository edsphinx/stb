package com.nuevoshorizontes.nhstream.Fragments;


import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nuevoshorizontes.nhstream.Cards.LiveActionCard;
import com.nuevoshorizontes.nhstream.Cards.LiveCanalCard;
import com.nuevoshorizontes.nhstream.Cards.LiveFavoriteCanalCard;
import com.nuevoshorizontes.nhstream.Cards.LiveProgramCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.MediaPlayers.Live.VideoFragment;
import com.nuevoshorizontes.nhstream.MediaPlayers.Live.VideoFragmentGlueHost;
import com.nuevoshorizontes.nhstream.MediaPlayers.Live.VideoPlayerGlue;
import com.nuevoshorizontes.nhstream.Presenters.LiveActionPresenter;
import com.nuevoshorizontes.nhstream.Presenters.LiveCanalPresenter;
import com.nuevoshorizontes.nhstream.Presenters.LiveFavoriteCanalPresenter;
import com.nuevoshorizontes.nhstream.Presenters.LiveProgramPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.LiveActionsListRow;
import com.nuevoshorizontes.nhstream.Rows.LiveActionsRow;
import com.nuevoshorizontes.nhstream.Rows.LiveProgramRow;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.nuevoshorizontes.nhstream.Utils.DownloadEPG;
import com.nuevoshorizontes.nhstream.Utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exoplayer2.ext.leanback.LeanbackPlayerAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LiveFragment extends VideoFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener {

    //UPDATE DELAY
    private static final int UPDATE_DELAY = 16;
    private Map<String, LiveFavoriteCanalCard> dataFavorite = null;
    private static boolean isChannelRowActive;
    //Monitoreo de Canales
    private final Handler monitoreoHandler = new Handler();
    private ArrayObjectAdapter actionsRowAdapter;
    private ArrayObjectAdapter favoriteChannelsRowAdapter;
    private ArrayObjectAdapter programsRowAdapter;
    //Indices de Cada ROW
    private final int ROW_CHANNELS = 0;
    private final Runnable runnableRelease = new Runnable() {
        @Override
        public void run() {
            garbageCollector();
            handlerRelease.removeCallbacks(this);
            handlerRelease.postDelayed(this, delayMediaRelease);
        }
    };
    Handler mHandlerResetTitle = new Handler();
    Runnable mRunnableResetTitle = new Runnable() {
        @Override
        public void run() {
            mHandlerResetTitle.removeCallbacks(this);
            setmTitle("");
        }
    };
    private String user_type;
    private String user_profile;
    Handler mOverlayHiddenHandler = new Handler();
    Runnable mOverlayHiddenRunnable = new Runnable() {
        @Override
        public void run() {
            mOverlayHiddenHandler.removeCallbacks(this);
            hideControlsOverlay(true);
            isChannelRowActive = false;
        }
    };



    Handler mResetChannelHandler = new Handler();
    private int delay = 40000;//1000 * 30;
    private int delayMediaRelease = 10800000; //7200000;
    Handler mHandlerRelaunchChannel = new Handler();
    //Data de Canales y Canales Favoritos
    private Map<String, LiveCanalCard> data = null;
    //Adaptador Principal
    private ArrayObjectAdapter infoRowsAdapter;
    //Adaptadores de Cada Row
    private ArrayObjectAdapter channelsRowAdapter;
    //ROW de Programacion
    private LiveProgramRow programationData;
    //Canal Seleccionado y Canal en Reproduccion
    private LiveCanalCard selectedChannel, currentChannel;
    private final int ROW_ACTIONS = 1;
    private final int ROW_PROGRAMATION = 2;
    // private final int ROw_PLAYER = 3;
    private final int ROW_FAVORITE_CHANNELS = 3;
    //Numero de Canal en String
    private String channelNumber = "";
    private String TOTAL_PRESSED = "";
    private final Handler handlerRelease = new Handler();
    private final Handler handlerLoadPrograms = new Handler();
    private final Handler handlerLoadFavoriteChannels = new Handler();
    private final Handler cambiarCanalHandler = new Handler();
    private Handler selectedHandler = new Handler();
    //Hilo Secundario para Tareas de Descarga
    private Thread thread;

    //Variables de Session
    private String access_token;

    private final Runnable runnableLoadPrograms = new Runnable() {
        @Override
        public void run() {
            loadChannelPrograms();
            handlerLoadPrograms.removeCallbacks(this);
        }
    };

    private final Runnable cambiarCanalRunnable = new Runnable() {
        @Override
        public void run() {
            cambiarCanal();
        }
    };

    private Runnable selectedRunnable = new Runnable() {
        @Override
        public void run() {
            selectedHandler.removeCallbacks(this);
            if (currentChannel != null) {
                currentChannel.setmEstado(0);
                channelsRowAdapter.replace(currentChannel.getmPosicion(), currentChannel);
                channelsRowAdapter.notifyArrayItemRangeChanged(currentChannel.getmPosicion(), 1);
            }
            LiveCanalCard card = selectedChannel;
            card.setmEstado(1);
            channelsRowAdapter.replace(card.getmPosicion(), card);
            channelsRowAdapter.notifyArrayItemRangeChanged(card.getmPosicion(), 1);
            selectedChannel = card;
            currentChannel = card;

        }
    };
    //DELAY para Cargar Programas
    private int LOAD_PROGRAMS_DELAY = 300;
    //Contador de Digitos presionados en numpad
    private int TOTAL_DIGIT = 0;
    //Player
    private VideoPlayerGlue mPlayerGlue;
    private LeanbackPlayerAdapter mPlayerAdapter;
    private SimpleExoPlayer mPlayer;
    private TrackSelector mTrackSelector;
    private Long mPosition = Long.parseLong("0");

    private void setupUI() {
        /**
         * Set Background Color del Objeto Principal de las Filas
         * @Option PlaybackOverlayFragment.BG_NONE
         * @Option PlaybackOverlayFragment.BG_LIGHT
         * @Option PlaybackOverlayFragment.BG_DARK
         *
         */
        setBackgroundType(NHPlaybackOverlayFragment.BG_DARK);
    }

    private int reinitializeCounter = 0;
    private Handler mHandlerSelectedPosition = new Handler();
    private Runnable mRunnableSelectedPosition = new Runnable() {
        @Override
        public void run() {
            // Seleccionar el ultimo canal Reproducido
            mHandlerSelectedPosition.removeCallbacks(this);
            setSelectedPosition(0, false, currentChannel.getmPosicion());
        }
    };
    Runnable mRunnableRelaunchChannel = new Runnable() {
        @Override
        public void run() {
            mHandlerRelaunchChannel.removeCallbacks(this);
            if(reinitializeCounter == 3){
                reinitializeCounter = 0;
                initializePlayer();
            }
            play(currentChannel, false, false, 0);
        }
    };
    Runnable mResetChannelRunnable = new Runnable() {
        @Override
        public void run() {
            mResetChannelHandler.removeCallbacks(this);
            if (mPlayerGlue != null) {
                if (!mPlayerGlue.isPlaying()) {
                    releasePlayer();
                    initializePlayer();
                    play(currentChannel, true, false, 0);
                } else {
                    if (mPlayerGlue.getCurrentPosition() != mPosition) {
                        mPosition = mPlayerGlue.getCurrentPosition();
                        reinitializeCounter++;
                    } else {
                        if(reinitializeCounter == 3){
                            releasePlayer();
                        }

                       mHandlerRelaunchChannel.postDelayed(mRunnableRelaunchChannel, 1000);
                    }
                }
            }

            mResetChannelHandler.postDelayed(this, 2000);
        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isChannelRowActive = false;

        /** Obteniendo data de Session */
        access_token = MainActivity.access_token;
        user_type = MainActivity.user_type;
        user_profile = MainActivity.user_profile;

        // Setup User Interface
        setupUI();
        setMainRowsAdapter();
        // Agregar fila de Canales
        addChannelsRow();
        // Agregar fila de Acciones
        addActionsRow();
        // Agregar Fila de Programacion
        addProgramation();
        // Agregar Canales Favoritos
        addFavoriteChannels(true);
        // Set Objeto Principal
        setAdapter(infoRowsAdapter);

        // Eventos Click y Selected de los Rows
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);

        //Reset cada minuto
        handlerRelease.postDelayed(runnableRelease, delayMediaRelease);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();

            LiveCanalCard card = (LiveCanalCard) data.get(loadPreferences());
            // Verificar que el card no sea nulo
            if (card != null) {
                play(card, true, true, 20000);
                currentChannel = card;
                selectedChannel = card;
                // Cargar Programas
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, 1000);
            }
        }
    }

    /**
     * Pauses the player.
     */
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();

        if (mPlayerGlue != null && mPlayerGlue.isPlaying()) {
            mPlayerGlue.pause();
        }
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void garbageCollector() {
        System.gc();
        System.runFinalization();
    }

    private void initializePlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), mTrackSelector);
        mPlayerAdapter = new LeanbackPlayerAdapter(getActivity(), mPlayer, UPDATE_DELAY);

        mPlayerGlue = new VideoPlayerGlue(getActivity(), mPlayerAdapter);
        mPlayerGlue.setHost(new VideoFragmentGlueHost(this));
        mPlayerGlue.addPlayerCallback(
                new PlaybackGlue.PlayerCallback() {
                    @Override
                    public void onPreparedStateChanged(PlaybackGlue glue) {
                        super.onPreparedStateChanged(glue);
                        if (glue.isPrepared()) {
                            glue.removePlayerCallback(this);
                            glue.play();
                        }
                    }
                });
    }

    /**
     * Description: Inicializando Objeto Principal
     */
    private void setMainRowsAdapter() {

        /** Presenter Selector de Multiples Filas */
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        /**
         * Mantener el mismo orden de los indices de filas
         * Channel Rows
         */
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        /** Channel Programation */
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        /** Player */
        //rowPresenterSelector.addClassPresenter(PlaybackControlsRow.class,
        //        playbackControlsRowPresenter);
        /** Canales Favoritos */
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

        /** Main ObjectAdapter de Rows */
        infoRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mTrackSelector = null;
            mPlayerGlue = null;
            mPlayerAdapter = null;
        }
    }

    private void play(LiveCanalCard video, boolean option, boolean toPosition, int hiddenTime) {
        prepareMediaForPlaying(Uri.parse(video.getmStream()));

        mPlayerGlue.play();
        setmTitle(video.getmNumero() + " ");
        if (option) {
            mOverlayHiddenHandler.postDelayed(mOverlayHiddenRunnable, hiddenTime);
        } else {
            hideControlsOverlay(false);
        }

        if(toPosition){
            mHandlerSelectedPosition.postDelayed(mRunnableSelectedPosition, 2000);
        }
        mHandlerResetTitle.removeCallbacks(mRunnableResetTitle);
        mHandlerResetTitle.postDelayed(mRunnableResetTitle, 2000);

        mResetChannelHandler.postDelayed(mResetChannelRunnable, 5000);
    }

    private void prepareMediaForPlaying(Uri mediaSourceUri) {
        String userAgent = Util.getUserAgent(getActivity(), "VideoPlayerGlue");

   /*     ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();
        MediaSource mediaSource =
                new ExtractorMediaSource(mediaSourceUri,
                        rtmpDataSourceFactory,
                        extractorsFactory, null, null);
*/

         MediaSource mediaSource = new HlsMediaSource(mediaSourceUri, new DefaultDataSourceFactory(getActivity(), userAgent),
                        3,
                        null,
                        null);

        // This is the MediaSource representing the media to be played.
        mPlayer.prepare(mediaSource);
    }


    // Agregar Fila de Canales
    private void addChannelsRow() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadData downloadData = new DownloadData();

                // Obtener JSON de Canales En Vivo
                String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.live + "/"
                        + access_token + "/" + user_type);
                Gson gson = new Gson();
                // Map<String, LiveCanalCard>, el String es el numero del canal
                // Se utiliza para buscar el numero del Canal con el PAD del Control
                Type canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                }.getType();
                // Se inicializa el Adaptador de los canales
                channelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
                try {
                    // Parseo del RESPONSE -> Map<String, LiveCanalCard>
                    data = gson.fromJson(response, canalesCardType);
                    int i = 0;
                    // Recorriendo cada entrada
                    for (HashMap.Entry<String, LiveCanalCard> entry : data.entrySet()) {
                        LiveCanalCard card = entry.getValue();
                        // Set la posicion de cada Canal en el ROW
                        card.setmPosicion(i);
                        entry.setValue(card);
                        // Se agrega la Card al Adaptador de Canales
                        channelsRowAdapter.add(entry.getValue());
                        i++;
                    }
                } catch (Exception e) {
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


        // Creando el header para ROW de Canales
        HeaderItem header = new HeaderItem(getString(R.string.chanels));
        // Agregando ROW de Canales en su indice
        infoRowsAdapter.add(ROW_CHANNELS, new ListRow(header, channelsRowAdapter));

    }

    //Descripcion: Agregar Fila de Acciones
    private void addActionsRow() {
        final LiveActionPresenter presenter = new LiveActionPresenter();
        // Inicializar el Adaptador
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
        List<LiveActionCard> listRowCard = new ArrayList<>();

        // Obtener  JSON DATA
        String json = Utils.inputStreamToString(getResources().openRawResource(R.raw.menu_actions));
        // Parsear DATA a un array de LiveActionsRow[]
        LiveActionsRow[] rows = new Gson().fromJson(json, LiveActionsRow[].class);

        // Leer Cada ROW
        for (LiveActionsRow row : rows) {
            // Cada LiveActionCard se agrega al Adaptador y Lista Princial
            for (LiveActionCard card : row.getmCards()) {
                listRowAdapter.add(card);
                listRowCard.add(card);
            }
            // Setup de la Fila de Acciones
            LiveActionsRow liveActionsRow = new LiveActionsRow();
            liveActionsRow.setmCards(listRowCard);
            // Setup de Lista de Filas
            LiveActionsListRow listRow = new LiveActionsListRow(
                    new HeaderItem(""),
                    listRowAdapter,
                    row
            );
            // Se agrega la lista de ROWS al Adaptador Principal en su indice
            infoRowsAdapter.add(ROW_ACTIONS, listRow);

        }


    }

    // Description: Agregar Fila de Programas Vacia
    private void addProgramation() {
        // Se inicializa el Adaptador de los programas
        programsRowAdapter = new ArrayObjectAdapter(new LiveProgramPresenter());
        // Creando el header para ROW de Programas
        HeaderItem header = new HeaderItem(getString(R.string.programation));
        // Agregando ROW de Programas en su indice
        infoRowsAdapter.add(ROW_PROGRAMATION, new ListRow(header, programsRowAdapter));
    }

    // Descripcion: Agregar Fila de Canales Favoritos
    private void addFavoriteChannels(boolean option) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                // Adaptador de Canales Favoritos
                favoriteChannelsRowAdapter = new ArrayObjectAdapter(new LiveFavoriteCanalPresenter());
                try {
                    DownloadData downloadData = new DownloadData();
                    // Obtener JSON de Canales Favoritos
                    String response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.live_favorites
                            + user_profile);
                    // Map<String, LiveCanalCard>, el String es el numero del canal
                    Type canalesCardType = new TypeToken<Map<String, LiveFavoriteCanalCard>>() {
                    }.getType();
                    // Parseo del RESPONSE -> Map<String, LiveCanalCard>
                    dataFavorite = gson.fromJson(response, canalesCardType);
                    int i = 0;
                    // Recorriendo cada entrada
                    if(dataFavorite != null) {
                        for (HashMap.Entry<String, LiveFavoriteCanalCard> entry : dataFavorite.entrySet()) {
                            LiveFavoriteCanalCard card = entry.getValue();
                            // Set la posicion de cada Canal en el ROW
                            card.setmPosicion(i);
                            entry.setValue(card);
                            // Se agrega la Card al Adaptador de Canales Favoritos
                            favoriteChannelsRowAdapter.add(entry.getValue());
                            i++;
                        }
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

        // Creando el header para ROW de Canales Favoritos
        HeaderItem header = new HeaderItem(getString(R.string.favorite_chanels));
        // Agregando ROW de Canales Favoritos en su indice
        if (option == true) {
            infoRowsAdapter.add(ROW_FAVORITE_CHANNELS, new ListRow(header, favoriteChannelsRowAdapter));
        } else {
            infoRowsAdapter.replace(ROW_FAVORITE_CHANNELS,
                    new ListRow(header, favoriteChannelsRowAdapter));
        }

    }

    // Descripcion: Obtener selectedChannel
    // @return Numero de Canal String
    private String loadPreferences() {
        SharedPreferences mPrefs = getActivity().getPreferences(0);
        String channel = mPrefs.getString("selectedChannel", "711");
        //setmTitle(channel);
        return channel;
    }

    // Descripcion: Cargar Programas segun el Canal
    private void loadChannelPrograms() {

        // Verificar que el thread no está activo
        final LiveCanalCard card = currentChannel;
        try {
            /*getVerticalGridView().setSelectedPosition(ROW_CHANNELS,
                    new ListRowPresenter.ViewHolderTask(card.getmPosicion())
            );*/
            // setSelectedPosition(ROW_CHANNELS, true, 5);
        } catch (Exception e) {
            Log.w("Error", e.toString());
        }


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
                    // Adaptador de Programas
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                            new LiveProgramPresenter());
                    // Se limpia la DATA de Programacion
                    programationData = null;
                    DownloadEPG downloadData = new DownloadEPG();
                    // Obtener JSON de Programas del Canal
                    String response = downloadData.run(Constants.server_epg + Constants.programation_epg
                            + String.valueOf(card.getmNumero()));//+ String.valueOf(card.getmId()));
                    //response = "{ programacion: " + response + " }";
                    if (!response.equalsIgnoreCase("{}") || !response.equalsIgnoreCase("[]")) {
                        // Parse del RESPONE -> LiveProgramRow
                        programationData = new Gson().fromJson(response, LiveProgramRow.class);
                        //Por cada Programa en ProgramationData, se agrega al Adaptador
                        if (programationData != null) {
                            for (LiveProgramCard card : programationData.getProgramaCards()) {
                                listRowAdapter.add(card);
                            }
                            // Creando el header para ROW de Programacion
                            HeaderItem header = new HeaderItem(ROW_PROGRAMATION,
                                    getString(R.string.programation_title) + ": " + card.getmTitle());
                            // Agregando ROW de Programacion en su indice
                            infoRowsAdapter.replace(ROW_PROGRAMATION, new ListRow(header, listRowAdapter));
                        }
                    }
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

    // Descripcion: Agregar Canal a Canales Favoritos y Recargar el ROW
    private void addFavoriteChannel() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Request para guardar canal como Canal Favorito
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder()
                        .add("id_canal", selectedChannel.getmId().toString());
                RequestBody formBody = formBuilder.build();

                Request request = new Request.Builder()
                        .url(Constants.server + "/stb/live/favoritos/guardar/" + user_profile)
                        .addHeader("Accept", "application/json; q=0.5")
                        .addHeader("Authorization", "Bearer " + access_token)
                        .post(formBody)
                        .build();
                try (
                        Response response = client.newCall(request).execute()
                ) {
                    // Recargar el ROW de Canales Favoritos
                    addFavoriteChannels(false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    // Descripcion: Cambiar canal con PAD de Control.
    public void cambiarCanal() {
        // Verificar que el Canal del Numero este en las KEY de DATA

        if (data.containsKey(channelNumber)) {
            // Reset Thumbnail to Logo en Canal
            selectedChannel.setmEstado(0);
            channelsRowAdapter.replace(selectedChannel.getmPosicion(), selectedChannel);
            channelsRowAdapter.notifyArrayItemRangeChanged(selectedChannel.getmPosicion(), 1);

            // Mover el Foco al Canal Seleccionado
            LiveCanalCard card = data.get(channelNumber);
            card.setmEstado(1);
            // Actualizar Canal Seleccionado
            selectedChannel = card;
            currentChannel = selectedChannel;
            // Reproducir
            play(card, true, true, 0);
            setSelectedPosition(0, false, card.getmPosicion());
            // Cargar Programas de Canal
            handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
            handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);

            // Guardar el numero del Canal Seleccionado en SharedPreferences
            SharedPreferences mPrefs = getActivity().getPreferences(0);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("selectedChannel", String.valueOf(selectedChannel.getmNumero()));
            editor.commit();


        }
        // RESET al Numero del Canal
        channelNumber = "";
        TOTAL_DIGIT = 0;
        TOTAL_PRESSED = "";
        setmTitle(TOTAL_PRESSED);
    }

    /**
     * Keypress.
     *
     * @param e
     */
    public void keypress(KeyEvent e) {
        //if (TOTAL_DIGIT < 4) {
        // Si el KeyCode es un digito entre 0 y 9
        if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16 && TOTAL_DIGIT < 4) {
            // Obtengo el digito
            int numero = e.getKeyCode() - 7;
            TOTAL_PRESSED = TOTAL_PRESSED + String.valueOf(numero);
            // Se concatena el numero al guardado
            channelNumber = TOTAL_PRESSED;

            setmTitle(channelNumber);
            // Cambiar el Canal
            cambiarCanalHandler.removeCallbacks(cambiarCanalRunnable);
            cambiarCanalHandler.postDelayed(cambiarCanalRunnable, 1500);
            TOTAL_DIGIT++;
        } else {
            // if(TOTAL_PRESSED.length() > 2) {
            if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
                TOTAL_PRESSED = TOTAL_PRESSED.substring(TOTAL_PRESSED.length() - 3);
                // Obtengo el digito
                int numero = e.getKeyCode() - 7;
                TOTAL_PRESSED = TOTAL_PRESSED + String.valueOf(numero);
                // Se concatena el numero al guardado
                channelNumber = TOTAL_PRESSED;
                setmTitle(channelNumber);
                // Cambiar el Canal
                cambiarCanalHandler.removeCallbacks(cambiarCanalRunnable);
                cambiarCanalHandler.postDelayed(cambiarCanalRunnable, 1500);
            }
        }
    }

    public void upDown(KeyEvent e) {
        if (!isControlsOverlayVisible()) {
            keypressupodwn(e);
        } else {
            keypress(e);
        }
    }


    public void showRows() {
        if (!isControlsOverlayVisible()) {
            showControlsOverlay(true);
            setSelectedPosition(0, false, currentChannel.getmPosicion());
        }
    }

    /**
     * Keypress UP/DOWN.
     *
     * @param e
     */
    public void keypressupodwn(KeyEvent e) {
        try {

            // Si el KeyCode es un digito entre 0 y 9
            if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
                // Obtengo el digito
                int numero = e.getKeyCode() - 7;
                // Se concatena el numero al guardado
                channelNumber = channelNumber + String.valueOf(numero);
                setmTitle(channelNumber + " ");
                // Cambiar el Canal
                cambiarCanalHandler.removeCallbacks(cambiarCanalRunnable);
                cambiarCanalHandler.postDelayed(cambiarCanalRunnable, 1500);
            } else if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || e.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                int position = currentChannel.getmPosicion();
                if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    if (position < channelsRowAdapter.size() - 1) {//jalar del json de canales
                        position++;
                        LiveCanalCard card = (LiveCanalCard) channelsRowAdapter.get(position);
                        if (card != null) {
                            currentChannel = card;
                            // Mover el Foco al Canal Seleccionado
                            card.setmEstado(1);
                            setSelectedPosition(0, false, card.getmPosicion());
                            // Actualizar Canal Seleccionado
                            selectedChannel = card;
                            currentChannel = card;
                            // Seleccionar el ultimo canal Reproducido
                            // Cargar Programas
                            handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                            handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);
                            //setmTitle(String.valueOf(selectedChannel.getmNumero()));
                            play(currentChannel, false, true, 0);

                            // Guardar el numero del Canal Seleccionado en SharedPreferences
                            SharedPreferences mPrefs = getActivity().getPreferences(0);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("selectedChannel", String.valueOf(selectedChannel.getmNumero()));
                            editor.commit();
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (position > 0) {
                        position--;
                        LiveCanalCard card = (LiveCanalCard) channelsRowAdapter.get(position);
                        if (card != null) {
                            currentChannel = card;
                            // Mover el Foco al Canal Seleccionado
                            card.setmEstado(1);
                            //setSelectedPosition(0, false, card.getmPosicion());
                            selectedChannel = card;
                            currentChannel = card;
                            play(currentChannel, false, true, 0);
                            // Cargar Programas
                            handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                            handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);
                            //setmTitle(String.valueOf(selectedChannel.getmNumero()));

                            // Guardar el numero del Canal Seleccionado en SharedPreferences
                            SharedPreferences mPrefs = getActivity().getPreferences(0);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("selectedChannel", String.valueOf(selectedChannel.getmNumero()));
                            editor.commit();
                        }
                    }
                    //infoRowsAdapter.notifyArrayItemRangeChanged(currentChannel.getmPosicion(),1);
                    //setSelectedPosition(0, false, currentChannel.getmPosicion());
                    //Log.w("PRESSED_KEY_LIVEFRAG",e.toString());
                    // setmTitle(currentChannel.getmNumero().toString());
                } else if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    // setmTitle(String.valueOf(currentChannel.getmNumero()));
                    setSelectedPosition(0, false, currentChannel.getmPosicion());
                } else if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    //setmTitle(String.valueOf(currentChannel.getmNumero()));
                    setSelectedPosition(0, false, currentChannel.getmPosicion());
                }
            }
            //setmTitle("");
        } catch (Exception ex) {
            //Log.i(TAG,ex.toString());
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private static String createKillCommandForSystemBin(final String _user, final String _fileName) {
        return "kill $(ps|grep ^" + _user + ".*[/]system[/]bin[/]" + _fileName + "$|tr -s ' '|cut -d ' ' -f2);";
    }

    private static void runKillShellCommand() throws Throwable {
        final Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = null;
        try {
            os = new DataOutputStream(p.getOutputStream());
            //Creating the command for killing the relevant camera processes
            final String command =
                    //Command to kill the process using the file "/system/bin/mediaserver" under the "media" user name
                    createKillCommandForSystemBin("media", "mediaserver");
            os.writeBytes(command + "\n");
        } finally {
            if (os != null) {
                try {
                    os.writeBytes("exit\n");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                try {
                    os.flush();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                try {
                    os.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                try {
                    p.waitFor();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                try {
                    p.destroy();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Descripcion: Evento Selected de los Cards en los ROWS
     *
     * @param itemViewHolder
     * @param item
     * @param rowViewHolder
     * @param row
     */
    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

        mOverlayHiddenHandler.removeCallbacks(mOverlayHiddenRunnable);
        mOverlayHiddenHandler.postDelayed(mOverlayHiddenRunnable, 10000);
        isChannelRowActive = true;
        try {
            /** Si el item es del tipo: LiveCanalCard */
            if (item instanceof LiveCanalCard) {
                /** Si el ROW Seleccionado es Canales */
                if (row.getId() == ROW_CHANNELS) {
                    /** Asignar nuevo canal Seleccionado */
                    selectedChannel = (LiveCanalCard) item;
                    /** Cargar thumbnail de Canal */
                    //selectedHandler.postDelayed(selectedRunnable, 500);
                    /** Cargar Programas de Canal **/
                    handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                    handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);
                }
            } else if (item instanceof LiveActionCard) {
                //Para debug
            } else if (item instanceof LiveProgramCard) {
                //Para debug
            }
        } catch (Exception e) {
        }
    }

    /**
     * Descripcion: Evento Click de los Cards en los ROWS
     *
     * @param itemViewHolder
     * @param item
     * @param rowViewHolder
     * @param row
     */
    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {

        try {
            /** Si el item es del tipo: LiveCanalCard */
            if (item instanceof LiveCanalCard) {
                /** Parsear el item */
                final LiveCanalCard card = (LiveCanalCard) item;
                //Reproducir
                play(card, true, false, 5000);
                /** Asignar nuevo canal Seleccionado */
                card.setmEstado(1);
                selectedChannel = card;
                currentChannel = selectedChannel;
                /** Mover el Foco al Canal Seleccionado */
                setSelectedPosition(0, false, card.getmPosicion());

                /** Cargar Programas de Canal */
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);
                /**Reset cada minuto **/
//                handlerRelease.removeCallbacks(runnableRelease);
//                handlerRelease.postDelayed(runnableRelease, delayMediaRelease);

                /** Si el ID de la ROW es de Canales Favoritos */
                if (row.getId() == ROW_CHANNELS) {
                    /** Guardar el numero del Canal Seleccionado en SharedPreferences */
                    SharedPreferences mPrefs = getActivity().getPreferences(0);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("selectedChannel", String.valueOf(selectedChannel.getmNumero()));
                    editor.commit();
                }
            } else if (item instanceof LiveActionCard) {
                final LiveActionCard card = (LiveActionCard) item;
                /** Add Channel To Favorites */
                if (card.getmId() == 0) {
                    addFavoriteChannel();
                }

            } else if (item instanceof LiveFavoriteCanalCard) {
                final LiveFavoriteCanalCard card = (LiveFavoriteCanalCard) item;
                String ChannelNumberSelected = card.getmNumero().toString();
                final LiveCanalCard cardLive = data.get(ChannelNumberSelected);
                /** Preprar MetaData y Reproducir */
                play(cardLive, true, false, 10000);
                /** Asignar nuevo canal Seleccionado */
                cardLive.setmEstado(1);
                selectedChannel = cardLive;
                currentChannel = selectedChannel;

                /** Cargar Programas de Canal */
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);

                //if (row.getId() == ROW_CHANNELS) {
                /** Guardar el numero del Canal Seleccionado en SharedPreferences */
                SharedPreferences mPrefs = getActivity().getPreferences(0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("selectedChannel", String.valueOf(selectedChannel.getmNumero()));
                editor.commit();
                //}
            }
        } catch (Exception e) {
        }
    }
}
package com.flynetwifi.netplay.Fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
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
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.flynetwifi.netplay.Cards.LiveActionCard;
import com.flynetwifi.netplay.Cards.LiveCanalCard;
import com.flynetwifi.netplay.Cards.LiveProgramCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MediaPlayers.LiveMediaPlayerGlue;
import com.flynetwifi.netplay.Presenters.LiveActionPresenter;
import com.flynetwifi.netplay.Presenters.LiveCanalPresenter;
import com.flynetwifi.netplay.Presenters.LiveProgramPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.LiveActionsListRow;
import com.flynetwifi.netplay.Rows.LiveActionsRow;
import com.flynetwifi.netplay.Rows.LiveProgramRow;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.Utils.Utils;
import com.flynetwifi.netplay.media.MediaMetaData;
import com.flynetwifi.netplay.media.MediaPlayerGlue;
import com.flynetwifi.netplay.media.MediaUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LiveFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, OnItemViewSelectedListener,
        MediaPlayerGlue.OnMediaStateChangeListener {

    public static String TAG = "LiveFragment";

    /**
     * Data de Canales y Canales Favoritos
     */
    private Map<String, LiveCanalCard> data = null;
    private Map<String, LiveCanalCard> dataFavorite = null;

    /**
     * Adaptador Principal
     */

    private ArrayObjectAdapter infoRowsAdapter;

    /**
     * Adaptador de Canales, Acciones, Canales Favoritos y Programas
     */
    private ArrayObjectAdapter channelsRowAdapter;
    private ArrayObjectAdapter actionsRowAdapter;
    private ArrayObjectAdapter favoriteChannelsRowAdapter;
    private ArrayObjectAdapter programsRowAdapter;

    /**
     * MediaPlayer
     * Presentador de MediaPlayer
     */
    private LiveMediaPlayerGlue mGlue;
    private PlaybackControlsRowPresenter playbackControlsRowPresenter;

    /**
     * ROW de Programacion
     */
    private LiveProgramRow programationData;

    /**
     * Card para Canal Seleccionado y Canal en Reproduccion
     */
    private LiveCanalCard selectedChannel, currentChannel;

    /**
     * String de Numero de Canal
     */
    private String channelNumber = "";


    private Thread thread;

    /**
     * Variables de Session
     */
    private String access_token;
    private String user_type;
    private String user_profile;

    /**
     * CONSTANTES
     */
    private int LOAD_PROGRAMS_DELAY = 700;


    /**
     * Primera Reproduccion on Select
     */
    private boolean playOnSelect = true;

    /**
     * MetaData de Canal Actual
     */
    MediaMetaData currentMetaData;

    /**
     * Indices de Cada ROW
     * Deben ser secuenciales e iniciar por <cero>
     */
    private final int ROW_CHANNELS = 0;
    private final int ROW_ACTIONS = 1;
    private final int ROW_PROGRAMATION = 2;
    // private final int ROw_PLAYER = 3;
    private final int ROW_FAVORITE_CHANNELS = 3;


    private final Handler handlerLoadPrograms = new Handler();
    private final Handler handlerLoadFavoriteChannels = new Handler();
    private final Handler cambiarCanalHandler = new Handler();
    private Handler selectedHandler = new Handler();


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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Obteniendo data de Session */
        Bundle args = getArguments();
        access_token = args.getString("access_token", "");
        user_type = args.getString("user_type", "");
        user_profile = args.getString("user_profile", "");


        /** Inicializando MediaPlayer */
        mGlue = new LiveMediaPlayerGlue(getActivity(), this) {
            @Override
            protected void onRowChanged(PlaybackControlsRow row) {
                if (infoRowsAdapter == null) return;
                infoRowsAdapter.notifyArrayItemRangeChanged(0, 1);
            }
        };

        /** Setup VideoSurfaceFragment */
        setupUI();

        /** Inicializando Objeto Principal */
        setMainRowsAdapter();

        /** Agregar Fila de Canales */
        addChannelsRow();

        /** Agregar Fila de Acciones */
        addActionsRow();

        /** Agregar Fila de Programacion */
        addProgramation();

        /** Agregar Fil de Controles */
        //infoRowsAdapter.add(ROw_PLAYER, mGlue.getControlsRow());

        /** Agregar Canales Favoritos */
        addFavoriteChannels(true);

        /** Set Objeto Principal */
        setAdapter(infoRowsAdapter);

        /** Eventos Click y Selected de los Rows */
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);

    }

    /**
     * Setup VideoSurfaceFragment
     */
    private void setupUI() {
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
                //Reset MediaPlayer
                mGlue.resetPlayer();
                mGlue.releaseMediaSession();
                mGlue.setDisplay(null);
                mGlue.enableProgressUpdating(false);
                ;
            }
        });

        /**
         * Set Background Color del Objeto Principal de las Filas
         * @Option PlaybackOverlayFragment.BG_NONE
         * @Option PlaybackOverlayFragment.BG_LIGHT
         * @Option PlaybackOverlayFragment.BG_DARK
         *
         */
        setBackgroundType(PlaybackOverlayFragment.BG_DARK);
    }

    /**
     * Description: Inicializando Objeto Principal
     */
    private void setMainRowsAdapter() {
        /** Inicializando PlayBackControlPresenter */
        playbackControlsRowPresenter = mGlue.createControlsRowAndPresenter();
        playbackControlsRowPresenter.setBackgroundColor(getActivity().getResources().getColor(R.color.program_background));
        playbackControlsRowPresenter.setBackgroundColor(getActivity().getResources().getColor(R.color.program_background));
        playbackControlsRowPresenter.setSecondaryActionsHidden(false);
        /**
         * Evento Click de Acciones Secundarias
         * Los Controles se definen en @LiveMediaPlayerGlue
         */
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (selectedChannel != null) {
                    if (action.getId() == 0) { //Like Action
                        addFavoriteChannel();
                    }
                    if (action.getId() == 2) {  //Repeat Action
                        final LiveCanalCard card = selectedChannel;
                        MediaMetaData currentMetaData = new MediaMetaData();
                        currentMetaData.setMediaTitle(card.getmTitle());
                        currentMetaData.setMediaSourcePath(card.getmRecord());
                        mGlue.prepareIfNeededAndPlay(currentMetaData);
                    }
                }
            }
        });

        /** Presenter Selector de Multiples Filas */
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        /**
         * Mantener el mismo orden de los indices de filas
         * Channel Rows
         */
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

    /**
     * Description: Agregar Fila de Canales
     */
    private void addChannelsRow() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadData downloadData = new DownloadData();
                /** Obtener JSON de Canales En Vivo **/
                String response = downloadData.run(Constants.server + Constants.live + "/"
                        + access_token + "/" + user_type);
                Gson gson = new Gson();
                /**
                 * Map<String, LiveCanalCard>, el String es el numero del canal
                 * Se utiliza para buscar el numero del Canal con el PAD del Control
                 */
                Type canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                }.getType();
                /** Se inicializa el Adaptador de los canales */
                channelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
                try {
                    /** Parseo del RESPONSE -> Map<String, LiveCanalCard> */
                    data = gson.fromJson(response, canalesCardType);
                    int i = 0;
                    /** Recorriendo cada entrada */
                    for (HashMap.Entry<String, LiveCanalCard> entry : data.entrySet()) {
                        LiveCanalCard card = entry.getValue();
                        /** Set la posicion de cada Canal en el ROW */
                        card.setmPosicion(i);
                        entry.setValue(card);
                        /** Se agrega la Card al Adaptador de Canales */
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


        /** Creando el header para ROW de Canales */
        HeaderItem header = new HeaderItem(ROW_CHANNELS, getString(R.string.chanels));
        /** Agregando ROW de Canales en su indice **/
        infoRowsAdapter.add(ROW_CHANNELS, new ListRow(header, channelsRowAdapter));

    }

    /**
     * Descripcion: Agregar Fila de Acciones
     */
    private void addActionsRow(){
        final LiveActionPresenter presenter = new LiveActionPresenter();
        /** Inicializar el Adaptador */
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
        List<LiveActionCard> listRowCard = new ArrayList<>();

        /** Obtener  JSON DATA */
        String json = Utils.inputStreamToString(getResources().openRawResource(R.raw.menu_actions));
        /** Parsear DATA a un array de LiveActionsRow[] */
        LiveActionsRow[] rows = new Gson().fromJson(json, LiveActionsRow[].class);

        /** Leer Cada ROW */
        for(LiveActionsRow row : rows){
            /** Cada LiveActionCard se agrega al Adaptador y Lista Princial */
            for(LiveActionCard card : row.getmCards()){
                listRowAdapter.add(card);
                listRowCard.add(card);
            }
            /** Setup de la Fila de Acciones */
            LiveActionsRow liveActionsRow = new LiveActionsRow();
            liveActionsRow.setmCards(listRowCard);
            /** Setup de Lista de Filas */
            LiveActionsListRow listRow = new LiveActionsListRow(
                    new HeaderItem(""),
                    listRowAdapter,
                    row
            );
            /** Se agrega la lista de ROWS al Adaptador Principal en su indice */
            infoRowsAdapter.add(ROW_ACTIONS, listRow);

        }


    }

    /**
     * Description: Agregar Fila de Programas Vacia
     */
    private void addProgramation() {
        /** Se inicializa el Adaptador de los programas */
        programsRowAdapter = new ArrayObjectAdapter(new LiveProgramPresenter());
        /** Creando el header para ROW de Programas */
        HeaderItem header = new HeaderItem(ROW_PROGRAMATION, getString(R.string.programation));
        /** Agregando ROW de Programas en su indice **/
        infoRowsAdapter.add(ROW_PROGRAMATION, new ListRow(header, programsRowAdapter));
    }

    /**
     * Descripcion: Agregar Fila de Canales Favoritos
     */
    private void addFavoriteChannels(boolean option) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                /** Adaptador de Canales Favoritos */
                favoriteChannelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
                try {
                    DownloadData downloadData = new DownloadData();
                    /** Obtener JSON de Canales Favoritos **/
                    String response = downloadData.run(Constants.server + Constants.live_favorites
                            + user_profile);
                    /**
                     * Map<String, LiveCanalCard>, el String es el numero del canal
                     */
                    Type canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                    }.getType();
                    /** Parseo del RESPONSE -> Map<String, LiveCanalCard> */
                    dataFavorite = gson.fromJson(response, canalesCardType);
                    int i = 0;
                    /** Recorriendo cada entrada */
                    for (HashMap.Entry<String, LiveCanalCard> entry : dataFavorite.entrySet()) {
                        LiveCanalCard card = entry.getValue();
                        /** Set la posicion de cada Canal en el ROW */
                        card.setmPosicion(i);
                        entry.setValue(card);
                        /** Se agrega la Card al Adaptador de Canales Favoritos */
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

        /** Creando el header para ROW de Canales Favoritos */
        HeaderItem header = new HeaderItem(ROW_FAVORITE_CHANNELS,
                getString(R.string.favorite_chanels));
        /** Agregando ROW de Canales Favoritos en su indice **/
        if(option == true) {
            infoRowsAdapter.add(ROW_FAVORITE_CHANNELS,
                    new ListRow(header, favoriteChannelsRowAdapter));
        }else {
            infoRowsAdapter.replace(ROW_FAVORITE_CHANNELS,
                    new ListRow(header, favoriteChannelsRowAdapter));
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

        /** Si el item es del tipo: LiveCanalCard */
        if (item instanceof LiveCanalCard) {
            /** Parsear el item */
            final LiveCanalCard card = (LiveCanalCard) item;
            /** SetUp MetaData */
            currentMetaData = new MediaMetaData();
            currentMetaData.setMediaTitle(card.getmTitle());
            currentMetaData.setMediaArtistName(card.getmDescription());
            currentMetaData.setMediaSourcePath(card.getmStream());
            /** Preprar MetaData y Reproducir */
            mGlue.prepareIfNeededAndPlay(currentMetaData);
            /** Asignar nuevo canal Seleccionado */
            selectedChannel = card;
            /** Cargar Programas de Canal */
            handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
            handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);

            /** Si el ID de la ROW es de Canales Favoritos */
            if (row.getId() == ROW_CHANNELS) {
                /** Guardar el numero del Canal Seleccionado en SharedPreferences */
                SharedPreferences mPrefs = getActivity().getPreferences(0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("selectedChannel", String.valueOf(selectedChannel.getmNumero()));
                editor.commit();
            }
        }
        else if(item instanceof LiveActionCard){
            final LiveActionCard card = (LiveActionCard) item;
            /** Add Channel To Favorites */
            if(card.getmId() == 0){
                addFavoriteChannel();
            }
            /** Live Replay */
            else if(card.getmId() == 1){
                MediaMetaData currentMetaData = new MediaMetaData();
                currentMetaData.setMediaTitle(currentChannel.getmTitle());
                currentMetaData.setMediaSourcePath(currentChannel.getmRecord());
                mGlue.prepareIfNeededAndPlay(currentMetaData);
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
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {
        /** Si el item es del tipo: LiveCanalCard */
        if (item instanceof LiveCanalCard) {
            /** Si el ROW Seleccionado es Canales */
            if (row.getId() == ROW_CHANNELS) {
                /** Asignar nuevo canal Seleccionado */
                selectedChannel = (LiveCanalCard) item;
                /** Cargar thumbnail de Canal */
                ///selectedHandler.postDelayed(selectedRunnable, 500);
                /** Cargar Programas de Canal **/
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);

                /** Reproduccion del Primer Canal */
                if (playOnSelect == true) {
                    /** Obtener ultimo canal reproducido */
                    LiveCanalCard card = (LiveCanalCard) data.get(loadPreferences());
                    /** Verificar que el card no sea nulo */
                    if (card != null) {
                        /** Actualizar MetaData */
                        currentMetaData = new MediaMetaData();
                        currentMetaData.setMediaTitle(card.getmTitle());
                        currentMetaData.setMediaArtistName(card.getmDescription());
                        currentMetaData.setMediaSourcePath(card.getmStream());
                        /** Preprar MetaData y Reproducir */
                        mGlue.prepareIfNeededAndPlay(currentMetaData);
                        /** Seleccionar el ultimo canal Reproducido */
                        getRowsFragment().setSelectedPosition(ROW_PROGRAMATION);
                        getRowsFragment().setSelectedPosition(ROW_CHANNELS, false,
                                new ListRowPresenter.SelectItemViewHolderTask(card.getmPosicion()));
                        /** Cargar Programas */
                        handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                        handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);
                    }
                    /** Los proximos select no reproduciran */
                    playOnSelect = false;
                }

            }
        }

    }

    /**
     * Descripcion: Obtener selectedChannel
     * @return Numero de Canal String
     */
    private String loadPreferences() {
        SharedPreferences mPrefs = getActivity().getPreferences(0);
        String channel = mPrefs.getString("selectedChannel", "711");
        return channel;
    }

    /**
     * Descripcion: Cargar Programas segun el Canal
     */
    private void loadChannelPrograms() {

        /** Verificar que el thread no está activo */
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
                    /** Adaptador de Programas */
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                            new LiveProgramPresenter());
                    /** Se limpia la DATA de Programacion */
                    programationData = null;
                    DownloadData downloadData = new DownloadData();
                    /** Obtener JSON de Programas del Canal **/
                    String response = downloadData.run(Constants.server + Constants.programation
                            + String.valueOf(card.getmId()));
                    /** Parse del RESPONE -> LiveProgramRow */
                    programationData = new Gson().fromJson(response, LiveProgramRow.class);
                    /**
                     * Por cada Programa en ProgramationData, se agrega al Adaptador
                     */
                    for (LiveProgramCard card : programationData.getProgramaCards()) {
                        listRowAdapter.add(card);
                    }
                    /** Creando el header para ROW de Programacion */
                    HeaderItem header = new HeaderItem(ROW_PROGRAMATION,
                            getString(R.string.programation_title) + ": " + card.getmTitle());
                    /** Agregando ROW de Programacion en su indice **/
                    infoRowsAdapter.replace(ROW_PROGRAMATION, new ListRow(header, listRowAdapter));

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

    /**
     * Descripcion: Agregar Canal a Canales Favoritos y Recargar el ROW
     */
    private void addFavoriteChannel() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                /** Request para guardar canal como Canal Favorito */
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
                    /** Recargar el ROW de Canales Favoritos */
                    addFavoriteChannels(false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    /**
     * Descripcion: Cambiar canal con PAD de Control.
     */
    public void cambiarCanal() {
        /** Verificar que el Canal del Numero este en las KEY de DATA */

        if (data.containsKey(channelNumber)) {
            /** Reset Thumbnail to Logo en Canal */
            selectedChannel.setmEstado(0);
            channelsRowAdapter.replace(selectedChannel.getmPosicion(), selectedChannel);
            channelsRowAdapter.notifyArrayItemRangeChanged(selectedChannel.getmPosicion(), 1);

            /** Mover el Foco al Canal Seleccionado */
            LiveCanalCard card = data.get(channelNumber);
            card.setmEstado(1);
            getRowsFragment().setSelectedPosition(ROW_CHANNELS, false,
                    new ListRowPresenter.SelectItemViewHolderTask(card.getmPosicion()));

            /** Actualizar MetaData */
            currentMetaData = new MediaMetaData();
            currentMetaData.setMediaTitle(card.getmTitle());
            currentMetaData.setMediaArtistName(card.getmDescription());
            currentMetaData.setMediaSourcePath(card.getmStream());

            /** Actualizar Canal Seleccionado **/
            selectedChannel = card;
            /** Actualizar MetaData en el Player y Reproducir */
            mGlue.prepareIfNeededAndPlay(currentMetaData);

            /** Cargar Programas de Canal **/
            handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
            handlerLoadPrograms.postDelayed(runnableLoadPrograms, LOAD_PROGRAMS_DELAY);

            /** Guardar el numero del Canal Seleccionado en SharedPreferences */
            SharedPreferences mPrefs = getActivity().getPreferences(0);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("selectedChannel", String.valueOf(selectedChannel.getmNumero()));
            editor.commit();


        }
        /** RESET al Numero del Canal */
        channelNumber = "";
        setTitle("");

    }

    /**
     * Keypress.
     *
     * @param e
     */
    public void keypress(KeyEvent e) {

        /** Si el KeyCode es un digito entre 0 y 9 */
        if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
            /** Obtengo el digito **/
            int numero = e.getKeyCode() - 7;
            /** Se concatena el numero al guardado */
            channelNumber = channelNumber + String.valueOf(numero);
            setTitle(channelNumber);
            /** Cambiar el Canal */
            cambiarCanalHandler.removeCallbacks(cambiarCanalRunnable);
            cambiarCanalHandler.postDelayed(cambiarCanalRunnable, 1500);
        }
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

    @Override
    public void onMediaStateChanged(MediaMetaData currentMediaMetaData, int currentMediaState) {
        if (currentMediaState == MediaUtils.MEDIA_STATE_COMPLETED) {
            mGlue.startPlayback();
        }

    }






}

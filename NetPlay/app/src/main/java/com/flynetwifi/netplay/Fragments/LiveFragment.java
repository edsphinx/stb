package com.flynetwifi.netplay.Fragments;

import android.app.Fragment;
import android.content.Context;
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

import com.flynetwifi.netplay.Cards.LiveCanalCard;
import com.flynetwifi.netplay.Cards.LiveProgramCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MediaPlayers.LiveMediaPlayerGlue;
import com.flynetwifi.netplay.Presenters.LiveCanalPresenter;
import com.flynetwifi.netplay.Presenters.LiveProgramPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.LiveProgramRow;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.media.MediaMetaData;
import com.flynetwifi.netplay.media.MediaPlayerGlue;
import com.flynetwifi.netplay.media.MediaUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Live fragment.
 */
public class LiveFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, OnItemViewSelectedListener,
        MediaPlayerGlue.OnMediaStateChangeListener {


    private Context mContext;
    /**
     * The constant TAG.
     */
    public static String TAG;

    private Map<String, LiveCanalCard> data = null;
    private ArrayObjectAdapter infoRowsAdapter;
    private ArrayObjectAdapter channelsRowAdapter;
    private ArrayObjectAdapter favoriteChannelsRowAdapter;
    private ArrayObjectAdapter programsRowAdapter;
    private ArrayObjectAdapter myProgramsRowAdapter;

    private PlaybackControlsRowPresenter playbackControlsRowPresenter;


    private LiveProgramRow programationData;
    private LiveCanalCard selectedChannel, currentChannel;
    private String channelNumber = "";
    private Thread thread;

    private String access_token;
    private String user_type;
    private String user_profile;

    /**
     * The Current meta data.
     */
    MediaMetaData currentMetaData;


    private final Handler handlerLoadPrograms = new Handler();
    private final Handler handlerUpdateMediaPlayer = new Handler();
    private final Handler handlerLoadFavoriteChannels = new Handler();

    private final Runnable runnableLoadPrograms = new Runnable() {
        @Override
        public void run() {
            mGlue.prepareIfNeededAndPlay(currentMetaData);
            loadChannelPrograms();
            handlerLoadPrograms.removeCallbacks(this);
        }
    };


    private final Runnable runnableFavoriteChannels = new Runnable() {
        @Override
        public void run() {
            loadFavoriteChannels();
            handlerLoadFavoriteChannels.removeCallbacks(this);
        }
    };

    private LiveMediaPlayerGlue mGlue;
    private final int ROw_PLAYER = 0;
    private final int ROW_CHANNELS = 1;
    private final int ROW_PROGRAMATION = 2;
    private final int ROW_FAVORITE_CHANNELS = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = getString(R.string.TAG_LIVE);

        Bundle args = getArguments();
        access_token = args.getString("access_token", "");
        user_type = args.getString("user_type", "");
        user_profile = args.getString("user_profile", "");


        mContext = getActivity();

        mGlue = new LiveMediaPlayerGlue(getActivity(), this) {
            @Override
            protected void onRowChanged(PlaybackControlsRow row) {
                if (infoRowsAdapter == null) return;
                infoRowsAdapter.notifyArrayItemRangeChanged(0, 1);
            }
        };

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
        setBackgroundType(PlaybackOverlayFragment.BG_NONE);


        setMainRowsAdapter();

        infoRowsAdapter.add(ROw_PLAYER, mGlue.getControlsRow());

        addChannelsRow();

        addProgramation();
        addFavoriteChannels();

        setAdapter(infoRowsAdapter);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
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

    private void setMainRowsAdapter() {
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();

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

        //Player
        rowPresenterSelector.addClassPresenter(PlaybackControlsRow.class,
                playbackControlsRowPresenter);
        //Channel Rows
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        //Channel Programation
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        //Mi Favorite Channels
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

        //Main Object
        infoRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

    }

    private void addChannelsRow() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadData downloadData = new DownloadData();
                String response = downloadData.run(Constants.server + Constants.live + "/"
                        + access_token + "/" + user_type);
                Gson gson = new Gson();
                Type canalesCardType;
                canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                }.getType();
                channelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
                try {
                    data = gson.fromJson(response, canalesCardType);
                    int i = 0;
                    for (HashMap.Entry<String, LiveCanalCard> entry : data.entrySet()) {
                        String key = entry.getKey();
                        LiveCanalCard card = entry.getValue();
                        card.setmPosicion(i);
                        entry.setValue(card);
                        channelsRowAdapter.add(entry.getValue());
                        i++;
                    }
                } catch (Exception e) {

                }


            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        HeaderItem header = new HeaderItem(ROW_CHANNELS, getString(R.string.chanels));
        infoRowsAdapter.add(ROW_CHANNELS, new ListRow(header, channelsRowAdapter));
    }


    private void addProgramation() {
        programsRowAdapter = new ArrayObjectAdapter(new LiveProgramPresenter());
        HeaderItem header = new HeaderItem(ROW_PROGRAMATION, getString(R.string.programation));
        infoRowsAdapter.add(ROW_PROGRAMATION, new ListRow(header, programsRowAdapter));
    }

    private void addFavoriteChannels() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                favoriteChannelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.live_favorites
                            + user_profile);


                    Type canalesCardType;
                    canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                    }.getType();


                    data = gson.fromJson(response, canalesCardType);


                    int i = 0;
                    for (HashMap.Entry<String, LiveCanalCard> entry : data.entrySet()) {
                        LiveCanalCard card = entry.getValue();
                        card.setmPosicion(i);
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
        infoRowsAdapter.add(ROW_FAVORITE_CHANNELS, new ListRow(header, favoriteChannelsRowAdapter));
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


        if (row.getId() == ROW_CHANNELS) {
            if (item instanceof LiveCanalCard) {
                selectedChannel = (LiveCanalCard) item;

                selectedHandler.postDelayed(selectedRunnable, 1500);

            }
        }

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
                    String response = downloadData.run(Constants.server + Constants.programation
                            + String.valueOf(card.getmId()));

                    programationData = new Gson().fromJson(response, LiveProgramRow.class);
                    for (LiveProgramCard card : programationData.getProgramaCards()) {
                        listRowAdapter.add(card);
                    }

                    HeaderItem header = new HeaderItem(ROW_PROGRAMATION, getString(R.string.programation_title) + ": "
                            + card.getmTitle());
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


    private void loadFavoriteChannels() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                favoriteChannelsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.live_favorites
                            + user_profile);

                    Type canalesCardType;
                    canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                    }.getType();


                    data = gson.fromJson(response, canalesCardType);
                    int i = 0;
                    for (HashMap.Entry<String, LiveCanalCard> entry : data.entrySet()) {
                        String key = entry.getKey();
                        LiveCanalCard card = entry.getValue();
                        card.setmPosicion(i);
                        entry.setValue(card);
                        favoriteChannelsRowAdapter.add(entry.getValue());
                        i++;
                    }

                    HeaderItem header = new HeaderItem(ROW_FAVORITE_CHANNELS, getString(R.string.favorite_chanels));
                    infoRowsAdapter.replace(ROW_FAVORITE_CHANNELS, new ListRow(header,
                            favoriteChannelsRowAdapter));
                } catch (JsonParseException e) {
                    e.printStackTrace();
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

        if (data.containsKey(channelNumber)) {
            selectedChannel.setmEstado(0);
            channelsRowAdapter.replace(selectedChannel.getmPosicion(), selectedChannel);
            channelsRowAdapter.notifyArrayItemRangeChanged(selectedChannel.getmPosicion(), 1);

            LiveCanalCard card = data.get(channelNumber);
            getRowsFragment().setSelectedPosition(ROW_CHANNELS, true,
                    new ListRowPresenter.SelectItemViewHolderTask(card.getmPosicion()));
            currentMetaData = new MediaMetaData();

            currentMetaData.setMediaTitle(card.getmTitle());
            currentMetaData.setMediaArtistName(card.getmDescription());
            currentMetaData.setMediaSourcePath(card.getmStream());

            selectedChannel = card;
            handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
            handlerLoadPrograms.postDelayed(runnableLoadPrograms, 500);

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


    private Handler selectedHandler = new Handler();
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
}

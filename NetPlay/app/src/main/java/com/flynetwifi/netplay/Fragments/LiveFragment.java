package com.flynetwifi.netplay.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
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

import com.flynetwifi.netplay.Cards.LiveCanalCard;
import com.flynetwifi.netplay.Cards.LiveProgramCard;
import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MainActivity;
import com.flynetwifi.netplay.MediaPlayers.LiveMediaPlayerGlue;
import com.flynetwifi.netplay.MediaPlayers.LiveVideoMediaPlayerGlue;
import com.flynetwifi.netplay.Presenters.LiveCanalPresenter;
import com.flynetwifi.netplay.Presenters.LiveProgramPresenter;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Rows.LiveProgramRow;
import com.flynetwifi.netplay.Utils.DownloadData;
import com.flynetwifi.netplay.media.MediaMetaData;
import com.flynetwifi.netplay.media.MediaUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LiveFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, OnItemViewSelectedListener, LiveMediaPlayerGlue.OnMediaStateChangeListener{


    public static final String TAG = "LiveFragment";

    private Context mContext;

    public Map<String, LiveCanalCard> data = null; //Map of Channels
    private ArrayObjectAdapter channelsRowAdapter;
    private ArrayObjectAdapter programsRowAdapter;
    public LiveProgramRow programationData;
    public LiveCanalCard selectedChannel;
    public String channelNumber = "";


    public Thread thread; //Delete this

    public Handler handlerLoadPrograms = new Handler();

    public Runnable runnableLoadPrograms = new Runnable() {
        @Override
        public void run() {
            loadChannelPrograms();
            handlerLoadPrograms.removeCallbacks(this);
        }
    };

    private LiveVideoMediaPlayerGlue mGlue;

    public final int ROW_CHANNELS = 0;
    public final int ROW_PROGRAMATION = 1;

    private MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mp = MediaPlayer.create(mContext, R.raw.menu_selection);

        mGlue = new LiveVideoMediaPlayerGlue(getActivity(), this) {
            @Override
            protected void onRowChanged(PlaybackControlsRow row) {

            }
        };

        Fragment videoSurfaceFragment = getFragmentManager()
                .findFragmentByTag(LiveSurfaceFragment.TAG);

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
        addCanales();
    }

    private void playSound() {
        Thread thread = new Thread() {
            public void run() {
                mp.start();
            }
        };
        thread.start();
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

    private void addCanales() {
        final PlaybackControlsRowPresenter controlsPresenter = mGlue
                .createControlsRowAndPresenter();
        channelsRowAdapter = new ArrayObjectAdapter(controlsPresenter);
        channelsRowAdapter.add(mGlue.getControlsRow());

        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        channelsRowAdapter = new ArrayObjectAdapter(rowPresenterSelector);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server  + Constants.live + "/"
                            + MainActivity.access_token + "/" + MainActivity.user_type);
                    Gson gson = new Gson();
                    Type canalesCardType;
                    canalesCardType = new TypeToken<Map<String, LiveCanalCard>>() {
                    }.getType();

                    data = gson.fromJson(response, canalesCardType);

            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        programsRowAdapter = new ArrayObjectAdapter(new LiveCanalPresenter());

        int i = 0;
        for (HashMap.Entry<String, LiveCanalCard> entry : data.entrySet()) {
            String key = entry.getKey();
            LiveCanalCard card = entry.getValue();
            card.setmPosicion(i);
            entry.setValue(card);
            programsRowAdapter.add(entry.getValue());
            i++;
        }
        HeaderItem header = new HeaderItem(ROW_CHANNELS, getString(R.string.chanels));
        channelsRowAdapter.add(ROW_CHANNELS, new ListRow(header, programsRowAdapter));


        programsRowAdapter = new ArrayObjectAdapter(new LiveProgramPresenter());
        programationData = null;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.programation + "1");
                    programationData = new Gson().fromJson(response, LiveProgramRow.class);
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (LiveProgramCard card : programationData.getProgramaCards()) {
            programsRowAdapter.add(card);

        }

        header = new HeaderItem(ROW_PROGRAMATION, getString(R.string.programation));

        channelsRowAdapter.add(ROW_PROGRAMATION, new ListRow(header, programsRowAdapter));


        setAdapter(channelsRowAdapter);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {

        return;
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {

        if (row.getId() == ROW_CHANNELS) {
            if (item instanceof LiveCanalCard) {

                final LiveCanalCard card = (LiveCanalCard) item;

                MediaMetaData currentMetaData = new MediaMetaData();

                currentMetaData.setMediaTitle(card.getmTitle());
                currentMetaData.setMediaArtistName("");
                currentMetaData.setMediaSourcePath(card.getmStream());

                mGlue.prepareIfNeededAndPlay(currentMetaData);

                selectedChannel = card;
                handlerLoadPrograms.removeCallbacks(runnableLoadPrograms);
                handlerLoadPrograms.postDelayed(runnableLoadPrograms, 400);
                playSound();

            }
        }

    }

    public void loadChannelPrograms() {
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
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new LiveProgramPresenter());
                    programationData = null;
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + Constants.programation + String.valueOf(card.getmId()));
                    programationData = new Gson().fromJson(response, LiveProgramRow.class);
                    for (LiveProgramCard card : programationData.getProgramaCards()) {
                        listRowAdapter.add(card);
                    }

                    HeaderItem header = new HeaderItem(ROW_PROGRAMATION, getString(R.string.programation_title) +": " +  card.getmTitle());
                    channelsRowAdapter.replace(ROW_PROGRAMATION, new ListRow(header, listRowAdapter));

                }  catch (UnsupportedOperationException e1) {
                } catch (IllegalStateException e2) {
                } catch (JsonParseException e3) {
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

    public void keypress(KeyEvent e) {

        if (e.getKeyCode() >= 7 && e.getKeyCode() <= 16) {
            int numero = e.getKeyCode() - 7;
            channelNumber = channelNumber + String.valueOf(numero) ;
            cambiarCanalHandler.removeCallbacks(cambiarCanalRunnable);
            cambiarCanalHandler.postDelayed(cambiarCanalRunnable, 1500);
        }
    }

    private void cambiarCanal() {


        if (data.containsKey(channelNumber)) {
            LiveCanalCard card = (LiveCanalCard) data.get(channelNumber);
            getRowsFragment().setSelectedPosition(ROW_CHANNELS, true,
                    new ListRowPresenter.SelectItemViewHolderTask(card.getmPosicion()));

        }
        channelNumber ="";

    }

    private Handler cambiarCanalHandler = new Handler();
    private Runnable cambiarCanalRunnable = new Runnable() {
        @Override
        public void run() {
            cambiarCanal();
        }
    };

}

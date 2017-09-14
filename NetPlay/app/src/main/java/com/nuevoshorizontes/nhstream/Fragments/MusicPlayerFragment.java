package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.nuevoshorizontes.nhstream.Cards.MusicSongCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MediaPlayers.MusicMediaPlayerGlue;
import com.nuevoshorizontes.nhstream.Presenters.MusicSongPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Rows.MusicSongsRow;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.nuevoshorizontes.nhstream.media.MediaMetaData;
import com.nuevoshorizontes.nhstream.media.MediaPlayerGlue;
import com.nuevoshorizontes.nhstream.media.MediaUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import static com.nuevoshorizontes.nhstream.MainActivity.access_token;

public class MusicPlayerFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, MediaPlayerGlue.OnMediaStateChangeListener {


    public static final String TAG = "MusicPlayerFragment";
    private ArrayObjectAdapter mRowsAdapter;
    private MusicMediaPlayerGlue mGlue;

    private MusicSongCard cancionesModel = null;

    private String tipo = "";
    private String id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        tipo = args.getString("tipo", "");
        id = args.getString("id", "");


        mGlue = new MusicMediaPlayerGlue(getActivity(), this) {

            @Override
            protected void onRowChanged(PlaybackControlsRow row) {
                if (mRowsAdapter == null) return;
                mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
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
                // Nothing to do
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
        addPlaybackControlsRow();
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

    private void addPlaybackControlsRow() {


        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter = mGlue.createControlsRowAndPresenter();
        playbackControlsRowPresenter.setBackgroundColor(getActivity().getResources().getColor(R.color.background));


        rowPresenterSelector.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

        mRowsAdapter.add(mGlue.getControlsRow());


        playlistsRow();

        setAdapter(mRowsAdapter);
        setOnItemViewClickedListener(this);
    }

    private void playlistsRow() {
        Thread thread = new Thread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String response = "";
                    if (tipo.contentEquals("0")) {
                        response = downloadData.run(getActivity(), access_token, false, Constants.server  + Constants.songs_playlist + id);
                        MusicSongsRow rows = new Gson().fromJson(response, MusicSongsRow.class);
                        mRowsAdapter.add(1, createCardRowCantantes(rows));
                    } else if (tipo.contentEquals("1")) {
                        response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.songs_genders + id);
                        MusicSongsRow rows = new Gson().fromJson(response, MusicSongsRow.class);
                        mRowsAdapter.add(1, createCardRowCantantes(rows));
                    } else if (tipo.contentEquals("2")) {
                        response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.songs_singers + id);
                        MusicSongsRow rows = new Gson().fromJson(response, MusicSongsRow.class);
                        mRowsAdapter.add(1, createCardRowCantantes(rows));
                    }
                    else if (tipo.contentEquals("3")) {
                        response = downloadData.run(getActivity(), access_token, false, Constants.server + Constants.songs_songs + id);
                        MusicSongsRow rows = new Gson().fromJson(response, MusicSongsRow.class);
                        mRowsAdapter.add(1, createCardRowCantantes(rows));
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


        if (cancionesModel != null) {
            MediaMetaData currentMetaData = new MediaMetaData();

            currentMetaData.setMediaTitle(cancionesModel.getNombre());
            currentMetaData.setMediaArtistName(cancionesModel.getCantante());
            currentMetaData.setMediaSourcePath(cancionesModel.getStream());

            mGlue.prepareIfNeededAndPlay(currentMetaData);
        }
    }

    private Row createCardRowCantantes(final MusicSongsRow cardRow) {

        MusicSongPresenter presenter = new MusicSongPresenter();
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenter);
        int i = 0;
        for (MusicSongCard card : cardRow.getCanciones()) {
            listRowAdapter.add(card);
            if (i == 0) {
                cancionesModel = card;
                i = 1;
            }
        }

        return new ListRow(new HeaderItem(getString(R.string.songs)), listRowAdapter);

    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {

        if (item instanceof MusicSongCard) {
            MusicSongCard cancion = (MusicSongCard) item;

            MediaMetaData currentMetaData = new MediaMetaData();

            currentMetaData.setMediaTitle(cancion.getNombre());
            currentMetaData.setMediaArtistName(cancion.getCantante());
            currentMetaData.setMediaSourcePath(cancion.getStream());

            mGlue.prepareIfNeededAndPlay(currentMetaData);

        } else if (row.getId() == -1) {
            if (!(item instanceof Action)) return;
            mGlue.onActionClicked((Action) item);
        }
    }


    @Override
    public void onMediaStateChanged(MediaMetaData currentMediaMetaData, int currentMediaState) {
        if (currentMediaState == MediaUtils.MEDIA_STATE_COMPLETED) {
            mGlue.startPlayback();
        }
    }
}


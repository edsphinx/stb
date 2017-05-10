package com.nuevoshorizontes.nhstream.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nuevoshorizontes.nhstream.Cards.SeriesChapterCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.MediaPlayers.SeriesMediaPlayerGlue;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.SeriesPlayerActivity;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.nuevoshorizontes.nhstream.media.MediaMetaData;
import com.nuevoshorizontes.nhstream.media.MediaPlayerGlue;
import com.nuevoshorizontes.nhstream.media.MediaUtils;

import java.util.HashMap;
import java.util.List;

public class SeriesPlayerFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, MediaPlayerGlue.OnMediaStateChangeListener {


    public static final String TAG = "SeriesPlayerFragment";
    public int TIME = 0;
    public String id, nombre, url, row, posicion;

    private ArrayObjectAdapter mRowsAdapter;
    private List<SeriesChapterCard> listPosiciones;
    private HashMap<Integer, List<SeriesChapterCard>> dataChapters;
    private int currentPosition = 0;

    private SeriesMediaPlayerGlue mGlue;
    private Handler mSeekHandler = new Handler();
    private Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            mSeekHandler.removeCallbacks(this);
            try {
                if (mGlue.isMediaPlaying()) {
                    TIME = mGlue.getCurrentPosition();
                    int tracking = TIME / 1000;
                    if (tracking % 60 == 0) {
                        updateProgress();
                    }

                }
                mSeekHandler.postDelayed(this, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }




        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        id = args.getString("id", "");
        nombre = args.getString("nombre", "");
        url = args.getString("url", "");
        row = args.getString("row", "");
        posicion = args.getString("posicion", "");

        mGlue = new SeriesMediaPlayerGlue(getActivity(), this) {

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
            }
        });
        setBackgroundType(PlaybackOverlayFragment.BG_NONE);
        addPlaybackControlsRow();


        dataChapters = SeriesSeasonsFragment.dataChapters;
        listPosiciones = dataChapters.get(Integer.parseInt(row));

    }

    @Override
    public void onStart() {
        super.onStart();
        mGlue.enableProgressUpdating(mGlue.hasValidMedia() && mGlue.isMediaPlaying());
        mGlue.createMediaSessionIfNeeded();
        mSeekHandler.postDelayed(mSeekRunnable, 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaMetaData intentMetaData = getActivity().getIntent().getParcelableExtra(SeriesPlayerActivity.TAG);
        MediaMetaData currentMetaData = new MediaMetaData();
        if (intentMetaData != null) {
            currentMetaData.setMediaTitle(intentMetaData.getMediaTitle());
            currentMetaData.setMediaArtistName(intentMetaData.getMediaArtistName());
            currentMetaData.setMediaSourcePath(intentMetaData.getMediaSourcePath());
            currentMetaData.setMediaAlbumArtUrl(intentMetaData.getMediaAlbumArtUrl());
        } else {
            currentMetaData.setMediaTitle(nombre);
            currentMetaData.setMediaArtistName("");
            currentMetaData.setMediaSourcePath(url);
            currentMetaData.setmPosition(getTime());
            //currentMetaData.setmPosition(2785000);
        }
        mGlue.setOnMediaFileFinishedPlayingListener(this);
        mGlue.prepareIfNeededAndPlay(currentMetaData);
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
    public void onStop() {
        super.onStop();
        updateProgress();
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
        playbackControlsRowPresenter.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent_background));
        rowPresenterSelector.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

        mRowsAdapter.add(mGlue.getControlsRow());


        mRowsAdapter.add(1, SeriesSeasonsFragment.rowAdapter);

        setAdapter(mRowsAdapter);
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (row.getId() == 0) {
            if (item instanceof SeriesChapterCard) {
                updateProgress();
                SeriesChapterCard capitulo = (SeriesChapterCard) item;
                id = capitulo.getmId();
                MediaMetaData currentMetaData = new MediaMetaData();

                currentMetaData.setMediaTitle(capitulo.getmNombre());
                currentMetaData.setMediaArtistName("");
                currentMetaData.setMediaSourcePath(capitulo.getmStream());
                currentMetaData.setmPosition(getTime());

                mGlue.prepareIfNeededAndPlay(currentMetaData);
            }
        } else if (row.getId() == -1) {
            if (!(item instanceof Action)) return;
            mGlue.onActionClicked((Action) item);
        }
    }


    @Override
    public void onMediaStateChanged(MediaMetaData currentMediaMetaData, int currentMediaState) {
        if (currentMediaState == MediaUtils.MEDIA_STATE_COMPLETED) {
            //mGlue.startPlayback();
            for(SeriesChapterCard card : listPosiciones ){
                if(card.getmPosicion() == Integer.parseInt(posicion)){

                    SeriesChapterCard capitulo = (SeriesChapterCard) card;
                    capitulo = listPosiciones.get(capitulo.getmPosicion() + 1);
                    id = capitulo.getmId();
                    MediaMetaData currentMetaData = new MediaMetaData();

                    currentMetaData.setMediaTitle(capitulo.getmNombre());
                    currentMetaData.setMediaArtistName("");
                    currentMetaData.setMediaSourcePath(capitulo.getmStream());
                    currentMetaData.setmPosition(getTime());

                    mGlue.prepareIfNeededAndPlay(currentMetaData);
                }
            }
        }
    }

    public void updateProgress() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String user_profile = MainActivity.user_profile;
                    Log.w("REQUEST", Constants.server
                            + "/stb/tracking/"
                            + id + "/"
                            + user_profile + "/"
                            + String.valueOf(TIME) + "/"
                            + "1");
                    DownloadData downloadData = new DownloadData();

                    String response = downloadData.run(Constants.server
                            + "/stb/tracking/"
                            + id + "/"
                            + user_profile + "/"
                            + String.valueOf(TIME) + "/"
                            + "1"
                    );
                    Log.w("RESPONSE", response);

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

    public int getTime() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String user_profile = MainActivity.user_profile;
                    String response = downloadData.run(Constants.server
                            + "/stb/tracking/listado/"
                            + id + "/"
                            + user_profile + "/"
                            + "1"
                    );

                    TIME = Integer.parseInt(response);

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
        return TIME;
    }
}
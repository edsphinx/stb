package com.nuevoshorizontes.nhstream.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.MediaPlayers.MovieMediaPlayerGlue;
import com.nuevoshorizontes.nhstream.MoviePlayerActivity;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.nuevoshorizontes.nhstream.media.MediaMetaData;
import com.nuevoshorizontes.nhstream.media.MediaPlayerGlue;
import com.nuevoshorizontes.nhstream.media.MediaUtils;

import static com.nuevoshorizontes.nhstream.MainActivity.access_token;

public class MoviePlayerFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, MediaPlayerGlue.OnMediaStateChangeListener {

    public static final String TAG = "MoviePlayerFragment";
    private ArrayObjectAdapter mRowsAdapter;
    private MovieMediaPlayerGlue mGlue;
    public String nombre;
    public int TIME = 0;
    private Handler mSeekHandler = new Handler();
    private Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            mSeekHandler.removeCallbacks(this);
            try{
                if (mGlue.isMediaPlaying()) {
                    TIME = mGlue.getCurrentPosition();
                    int tracking = TIME / 1000;
                    if (tracking % 60 == 0) {
                        updateProgress();
                    }
                }
                mSeekHandler.postDelayed(this, 1000);
            }
            catch(Exception e){
                e.printStackTrace();
            }



        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGlue = new MovieMediaPlayerGlue(getActivity(), this) {

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
                mGlue.enableProgressUpdating(false);;
            }
        });
        setBackgroundType(PlaybackOverlayFragment.BG_DARK);
        addPlaybackControlsRow();
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
        MediaMetaData intentMetaData = getActivity().getIntent().getParcelableExtra(MoviePlayerActivity.TAG);
        MediaMetaData currentMetaData = new MediaMetaData();
        if (intentMetaData != null) {
            currentMetaData.setMediaTitle(intentMetaData.getMediaTitle());
            currentMetaData.setMediaArtistName(intentMetaData.getMediaArtistName());
            currentMetaData.setMediaSourcePath(intentMetaData.getMediaSourcePath());
            currentMetaData.setMediaAlbumArtUrl(intentMetaData.getMediaAlbumArtUrl());
        } else {
            currentMetaData.setMediaTitle(MoviePlayerActivity.nombre);
            currentMetaData.setMediaArtistName("");
            currentMetaData.setMediaSourcePath(MoviePlayerActivity.url);
            currentMetaData.setmPosition(getTime());
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
            if (!isVisibleBehind ) {
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

    private void addPlaybackControlsRow() {
        final PlaybackControlsRowPresenter controlsPresenter = mGlue
                .createControlsRowAndPresenter();
        mRowsAdapter = new ArrayObjectAdapter(controlsPresenter);
        mRowsAdapter.add(mGlue.getControlsRow());
        setAdapter(mRowsAdapter);
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (!(item instanceof Action)) return;
        mGlue.onActionClicked((Action) item);
    }


    @Override
    public void onMediaStateChanged(MediaMetaData currentMediaMetaData, int currentMediaState) {

        if (currentMediaState == MediaUtils.MEDIA_STATE_COMPLETED) {
            mGlue.startPlayback();
        }

    }


    public void updateProgress(){


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String user_profile = MainActivity.user_profile;
                    String response = downloadData.run(getActivity().getBaseContext(), access_token, false, Constants.server
                            + "/stb/tracking/"
                            + MoviePlayerActivity.id + "/"
                            + user_profile + "/"
                            + String.valueOf(TIME) + "/"
                            + "0"
                    );

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

    public int getTime(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadData downloadData = new DownloadData();
                    String user_profile = MainActivity.user_profile;
                    String response = downloadData.run(getActivity().getBaseContext(), access_token, false, Constants.server
                            + "/stb/tracking/listado/"
                            + MoviePlayerActivity.id + "/"
                            + user_profile + "/"
                            + "0"
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

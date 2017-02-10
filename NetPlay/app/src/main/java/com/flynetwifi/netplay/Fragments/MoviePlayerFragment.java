package com.flynetwifi.netplay.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
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

import com.flynetwifi.netplay.MediaPlayers.MovieMediaPlayerGlue;
import com.flynetwifi.netplay.MoviePlayerActivity;
import com.flynetwifi.netplay.media.MediaMetaData;
import com.flynetwifi.netplay.media.MediaPlayerGlue;
import com.flynetwifi.netplay.media.MediaUtils;

public class MoviePlayerFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, MediaPlayerGlue.OnMediaStateChangeListener {

    public static final String TAG = "LiveFragment";
    private ArrayObjectAdapter mRowsAdapter;
    private MovieMediaPlayerGlue mGlue;
    public String nombre;


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
}

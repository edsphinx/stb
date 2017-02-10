package com.flynetwifi.netplay.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.flynetwifi.netplay.Cards.SeriesChapterCard;
import com.flynetwifi.netplay.MediaPlayers.SeriesMediaPlayerGlue;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.SeriesPlayerActivity;
import com.flynetwifi.netplay.media.MediaMetaData;
import com.flynetwifi.netplay.media.MediaPlayerGlue;
import com.flynetwifi.netplay.media.MediaUtils;

public class SeriesPlayerFragment extends PlaybackOverlayFragment implements
        OnItemViewClickedListener, MediaPlayerGlue.OnMediaStateChangeListener {


    public static final String TAG = "LiveFragment";
    private ArrayObjectAdapter mRowsAdapter;
    private SeriesMediaPlayerGlue mGlue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        MediaMetaData intentMetaData = getActivity().getIntent().getParcelableExtra(SeriesPlayerActivity.TAG);
        MediaMetaData currentMetaData = new MediaMetaData();
        if (intentMetaData != null) {
            currentMetaData.setMediaTitle(intentMetaData.getMediaTitle());
            currentMetaData.setMediaArtistName(intentMetaData.getMediaArtistName());
            currentMetaData.setMediaSourcePath(intentMetaData.getMediaSourcePath());
            currentMetaData.setMediaAlbumArtUrl(intentMetaData.getMediaAlbumArtUrl());
        } else {
            currentMetaData.setMediaTitle(SeriesSeasonsFragment.nombre);
            currentMetaData.setMediaArtistName("");
            /*if(SeriesSeasonsFragment.url == "") {
                currentMetaData.setMediaSourcePath(Constants.server + "/multimedia/peliculas/Deadpool.m3u8");
            }
            else{*/
                currentMetaData.setMediaSourcePath(SeriesSeasonsFragment.url);
            //}
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


        final PlaybackControlsRowPresenter controlsPresenter = mGlue
                .createControlsRowAndPresenter();
        mRowsAdapter = new ArrayObjectAdapter(controlsPresenter);
        mRowsAdapter.add(mGlue.getControlsRow());

        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter = mGlue.createControlsRowAndPresenter();
        playbackControlsRowPresenter.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
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
        if(row.getId() == 0){
            if(item instanceof SeriesChapterCard){
                SeriesChapterCard capitulo = (SeriesChapterCard) item;
                MediaMetaData currentMetaData = new MediaMetaData();

                currentMetaData.setMediaTitle(capitulo.getmNombre());
                currentMetaData.setMediaArtistName("");
                currentMetaData.setMediaSourcePath(capitulo.getmStream());

                mGlue.prepareIfNeededAndPlay(currentMetaData);
            }
        }
        else if(row.getId() == -1) {
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
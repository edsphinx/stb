/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.flynetwifi.netplay.MediaPlayers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.app.PlaybackControlGlue;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.view.KeyEvent;
import android.view.View;

import com.flynetwifi.netplay.media.MediaMetaData;


public abstract class LiveMediaPlayerGlue extends PlaybackControlGlue {

    private static final String TAG = "MusicMediaPlayerGlue";
    private final Context mContext;
    protected PlaybackControlsRow mControlsRow;


    protected boolean mInitialized = false; // true when the MediaPlayer is prepared/initialized
    protected OnMediaStateChangeListener mMediaFileStateChangeListener;

    protected MediaMetaData mMediaMetaData = null;

    public LiveMediaPlayerGlue(Context context, PlaybackOverlayFragment fragment) {
        super(context, fragment, new int[]{1});
        mContext = context;


    }


    public void setOnMediaFileFinishedPlayingListener(OnMediaStateChangeListener listener) {
        mMediaFileStateChangeListener = listener;
    }


    @Override
    public PlaybackControlsRowPresenter createControlsRowAndPresenter() {
        PlaybackControlsRowPresenter presenter = super.createControlsRowAndPresenter();
        mControlsRow = getControlsRow();

        // Add secondary actions and change the control row color.
        ArrayObjectAdapter secondaryActions = new ArrayObjectAdapter(
                new ControlButtonPresenterSelector());
        mControlsRow.setSecondaryActionsAdapter(secondaryActions);


        return presenter;
    }



    @Override
    public void onActionClicked(Action action) {
        // If either 'Shuffle' or 'Repeat' has been clicked we need to make sure the acitons index
        // is incremented and the UI updated such that we can display the new state.
        super.onActionClicked(action);

        onMetadataChanged();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return super.onKey(v, keyCode, event);
    }

    @Override
    public boolean hasValidMedia() {
        return mMediaMetaData != null;
    }


    @Override
    public CharSequence getMediaTitle() {
        return hasValidMedia() ? mMediaMetaData.getMediaTitle() : "N/a";
    }

    @Override
    public CharSequence getMediaSubtitle() {
        return hasValidMedia() ? mMediaMetaData.getMediaArtistName() : "N/a";
    }

    @Override
    public Drawable getMediaArt() {
        return (hasValidMedia() && mMediaMetaData.getMediaAlbumArtResId() != 0) ?
                //getContext().getResources().
                //        getDrawable(mMediaMetaData.getMediaAlbumArtResId(), null)
                null
                : null;
    }

    @Override
    public long getSupportedActions() {
        return PlaybackControlGlue.ACTION_PLAY_PAUSE | PlaybackControlGlue.ACTION_FAST_FORWARD
                | PlaybackControlGlue.ACTION_REWIND;
    }

    @Override
    public int getCurrentSpeedId() {
        // 0 = Pause, 1 = Normal Playback Speed
        return isMediaPlaying() ? 1 : 0;
    }

    @Override
    protected void skipToNext() {
        // Not supported.
    }

    @Override
    protected void skipToPrevious() {
        // Not supported.
    }


    public void startPlayback() throws IllegalStateException {
        startPlayback(1);
    }


    public MediaMetaData getMediaMetaData() {
        return mMediaMetaData;
    }

    public void setMediaMetaData(MediaMetaData mediaMetaData) {
        mMediaMetaData = mediaMetaData;
        onMetadataChanged();
    }


    /**
     * A listener which will be called whenever a media item's playback status changes.
     */
    public interface OnMediaStateChangeListener {

        void onMediaStateChanged(MediaMetaData currentMediaMetaData, int currentMediaState);

        ;
    }
}

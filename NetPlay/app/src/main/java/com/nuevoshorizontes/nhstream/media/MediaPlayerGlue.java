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

package com.nuevoshorizontes.nhstream.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.app.PlaybackControlGlue;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.nuevoshorizontes.nhstream.R;

public abstract class MediaPlayerGlue extends PlaybackControlGlue implements
        OnItemViewSelectedListener {

    private static final int FAST_FORWARD_REWIND_STEP = 10 * 1000; // in milliseconds
    private static final int FAST_FORWARD_REWIND_REPEAT_DELAY = 200; // in milliseconds
    private static final String TAG = "MusicMediaPlayerGlue";
    private final Context mContext;
    private final PlaybackControlsRow.ThumbsDownAction mThumbsDownAction;
    private final PlaybackControlsRow.ThumbsUpAction mThumbsUpAction;
    private final PlaybackControlsRow.RepeatAction mRepeatAction;
    private final PlaybackControlsRow.ShuffleAction mShuffleAction;
    private final PlaybackControlsRow.SkipNextAction mSkipNextAction;
    protected PlaybackControlsRow mControlsRow;
    private static final int REFRESH_PROGRESS = 1;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    updateProgress();
                    queueNextRefresh();
            }
        }
    };

    protected boolean mInitialized = false; // true when the MediaPlayer is prepared/initialized
    protected OnMediaStateChangeListener mMediaFileStateChangeListener;
    private Action mSelectedAction; // the action which is currently selected by the user
    private long mLastKeyDownEvent = 0L; // timestamp when the last DPAD_CENTER KEY_DOWN occurred

    protected MediaMetaData mMediaMetaData = null;

    protected MediaPlayerGlue(Context context, PlaybackOverlayFragment fragment) {
        super(context, fragment, new int[]{1});
        mContext = context;
        // Instantiate secondary actions
        mShuffleAction = new PlaybackControlsRow.ShuffleAction(mContext);
        mRepeatAction = new PlaybackControlsRow.RepeatAction(mContext);
        mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(mContext);
        mThumbsUpAction = new PlaybackControlsRow.ThumbsUpAction(mContext);
        mThumbsDownAction.setIndex(PlaybackControlsRow.ThumbsAction.OUTLINE);
        mThumbsUpAction.setIndex(PlaybackControlsRow.ThumbsAction.OUTLINE);

        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(mContext);


        // Register selected listener such that we know what action the user currently has focused.
        fragment.setOnItemViewSelectedListener(this);

    }



    public void setOnMediaFileFinishedPlayingListener(OnMediaStateChangeListener listener) {
        mMediaFileStateChangeListener = listener;
    }

    /**
     * Override this method in case you need to add different secondary actions.
     *
     * @param secondaryActionsAdapter The adapter you need to add the {@link Action}s to.
     */
    protected void addSecondaryActions(ArrayObjectAdapter secondaryActionsAdapter) {
        secondaryActionsAdapter.add(mShuffleAction);
        secondaryActionsAdapter.add(mRepeatAction);
        secondaryActionsAdapter.add(mThumbsDownAction);
        secondaryActionsAdapter.add(mThumbsUpAction);
        secondaryActionsAdapter.add(mSkipNextAction);
    }


    protected void setupControlsRowPresenter(PlaybackControlsRowPresenter presenter) {
        // TODO: hahnr@ move into resources
        presenter.setProgressColor(getContext().getResources().getColor(
                R.color.colorPrimary));
        presenter.setBackgroundColor(getContext().getResources().getColor(
                R.color.colorAccent));
    }

    @Override public PlaybackControlsRowPresenter createControlsRowAndPresenter() {
        PlaybackControlsRowPresenter presenter = super.createControlsRowAndPresenter();
        mControlsRow = getControlsRow();

        // Add secondary actions and change the control row color.
        ArrayObjectAdapter secondaryActions = new ArrayObjectAdapter(
                new ControlButtonPresenterSelector());
        mControlsRow.setSecondaryActionsAdapter(secondaryActions);
        addSecondaryActions(secondaryActions);
        setupControlsRowPresenter(presenter);

        return presenter;
    }

    @Override public void enableProgressUpdating(final boolean enabled) {
        Log.d(TAG, "enableProgressUpdating: " + enabled);
        if (!enabled) {
            mHandler.removeMessages(REFRESH_PROGRESS);
            return;
        }
        queueNextRefresh();
    }

    private void queueNextRefresh() {
        Message refreshMsg = mHandler.obtainMessage(REFRESH_PROGRESS);
        mHandler.removeMessages(REFRESH_PROGRESS);
        mHandler.sendMessageDelayed(refreshMsg, getUpdatePeriod());
    }

    @Override public void onActionClicked(Action action) {
        // If either 'Shuffle' or 'Repeat' has been clicked we need to make sure the acitons index
        // is incremented and the UI updated such that we can display the new state.
        super.onActionClicked(action);
        if (action instanceof PlaybackControlsRow.ShuffleAction) {
            mShuffleAction.nextIndex();
        } else if (action instanceof PlaybackControlsRow.RepeatAction) {
            mRepeatAction.nextIndex();
        } else if (action instanceof PlaybackControlsRow.ThumbsUpAction) {
            if (mThumbsUpAction.getIndex() == PlaybackControlsRow.ThumbsAction.SOLID) {
                mThumbsUpAction.setIndex(PlaybackControlsRow.ThumbsAction.OUTLINE);
            } else {
                mThumbsUpAction.setIndex(PlaybackControlsRow.ThumbsAction.SOLID);
                mThumbsDownAction.setIndex(PlaybackControlsRow.ThumbsAction.OUTLINE);
            }
        } else if (action instanceof PlaybackControlsRow.ThumbsDownAction) {
            if (mThumbsDownAction.getIndex() == PlaybackControlsRow.ThumbsAction.SOLID) {
                mThumbsDownAction.setIndex(PlaybackControlsRow.ThumbsAction.OUTLINE);
            } else {
                mThumbsDownAction.setIndex(PlaybackControlsRow.ThumbsAction.SOLID);
                mThumbsUpAction.setIndex(PlaybackControlsRow.ThumbsAction.OUTLINE);
            }
        }
        onMetadataChanged();
    }

    @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
        // This method is overridden in order to make implement fast forwarding and rewinding when
        // the user keeps the corresponding action pressed.
        // We only consume DPAD_CENTER Action_DOWN events on the Fast-Forward and Rewind action and
        // only if it has not been pressed in the last X milliseconds.
        boolean consume = mSelectedAction instanceof PlaybackControlsRow.RewindAction;
        consume = consume || mSelectedAction instanceof PlaybackControlsRow.FastForwardAction;
        consume = consume && mInitialized;
        consume = consume && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER;
        consume = consume && event.getAction() == KeyEvent.ACTION_DOWN;
        consume = consume && System
                .currentTimeMillis() - mLastKeyDownEvent > FAST_FORWARD_REWIND_REPEAT_DELAY;
        if (consume) {
            mLastKeyDownEvent = System.currentTimeMillis();
            int newPosition = getCurrentPosition() + FAST_FORWARD_REWIND_STEP;
            if (mSelectedAction instanceof PlaybackControlsRow.RewindAction) {
                newPosition = getCurrentPosition() - FAST_FORWARD_REWIND_STEP;
            }
            // Make sure the new calculated duration is in the range 0 >= X >= MediaDuration
            if (newPosition < 0) newPosition = 0;
            if (newPosition > getMediaDuration()) newPosition = getMediaDuration();
            seekTo(newPosition);
            return true;
        }
        return super.onKey(v, keyCode, event);
    }

    @Override public boolean hasValidMedia() {
        return mMediaMetaData != null;
    }


    @Override public CharSequence getMediaTitle() {
        return hasValidMedia() ? mMediaMetaData.getMediaTitle() : "N/a";
    }

    @Override public CharSequence getMediaSubtitle() {
        return hasValidMedia() ? mMediaMetaData.getMediaArtistName() : "N/a";
    }

    @Override public Drawable getMediaArt() {
        return (hasValidMedia() && mMediaMetaData.getMediaAlbumArtResId() != 0) ?
                //getContext().getResources().
                //        getDrawable(mMediaMetaData.getMediaAlbumArtResId(), null)
                null
                : null;
    }

    @Override public long getSupportedActions() {
        return PlaybackControlGlue.ACTION_PLAY_PAUSE | PlaybackControlGlue.ACTION_FAST_FORWARD
                | PlaybackControlGlue.ACTION_REWIND;
    }

    @Override public int getCurrentSpeedId() {
        // 0 = Pause, 1 = Normal Playback Speed
        return isMediaPlaying() ? 1 : 0;
    }

    @Override protected void skipToNext() {
        // Not supported.
    }

    @Override protected void skipToPrevious() {
        // Not supported.
    }

    protected abstract void seekTo(int newPosition);


    /**
     * Call to <code>startPlayback(1)</code>.
     *
     * @throws IllegalStateException See {@link MediaPlayer} for further information about it's
     * different states when setting a data source and preparing it to be played.
     */
    public void startPlayback() throws IllegalStateException {
        startPlayback(1);
    }




    /**
     * @return Returns <code>true</code> iff 'Shuffle' is <code>ON</code>.
     */
    public boolean useShuffle() {
        return mShuffleAction.getIndex() == PlaybackControlsRow.ShuffleAction.ON;
    }

    /**
     * @return Returns <code>true</code> iff 'Repeat-One' is <code>ON</code>.
     */
    public boolean repeatOne() {
        return mRepeatAction.getIndex() == PlaybackControlsRow.RepeatAction.ONE;
    }

    /**
     * @return Returns <code>true</code> iff 'Repeat-All' is <code>ON</code>.
     */
    public boolean repeatAll() {
        return mRepeatAction.getIndex() == PlaybackControlsRow.RepeatAction.ALL;
    }

    public MediaMetaData getMediaMetaData() {
        return mMediaMetaData;
    }

    public void setMediaMetaData(MediaMetaData mediaMetaData) {
        mMediaMetaData = mediaMetaData;
        onMetadataChanged();
    }


    @Override public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                         RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Action) {
            mSelectedAction = (Action) item;
        } else {
            mSelectedAction = null;
        }
    }


    public interface OnMediaStateChangeListener {

        void onMediaStateChanged(MediaMetaData currentMediaMetaData, int currentMediaState);;
    }
}
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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

import com.flynetwifi.netplay.LiveActivity;
import com.flynetwifi.netplay.media.MediaMetaData;
import com.flynetwifi.netplay.media.MediaUtils;

import java.io.IOException;



public abstract class LiveVideoMediaPlayerGlue extends LiveMediaPlayerGlue {

    private MediaPlayer mPlayer;

    private MediaSession mVideoSession;
    private static final String TAG = "VideoMediaPlayerGlue";

    public LiveVideoMediaPlayerGlue(Context context, PlaybackOverlayFragment fragment) {
        super(context, fragment);

        createMediaPlayerIfNeeded();
        setFadingEnabled(true);
    }


    private void updateVideoSessionPlayState(int playbackState) {
        if (mVideoSession == null) {
            // MediaSession has already been released, no need to update PlaybackState
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PlaybackState.Builder playbackStateBuilder = null;

            playbackStateBuilder = new PlaybackState.Builder();

            long currentPosition = getCurrentPosition();
            playbackStateBuilder.setState(playbackState, currentPosition, (float) 1.0).setActions(
                    getPlaybackStateActions()
            );
            mVideoSession.setPlaybackState(playbackStateBuilder.build());
        }
    }

    private long getPlaybackStateActions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE |
                    PlaybackState.ACTION_FAST_FORWARD | PlaybackState.ACTION_REWIND |
                    PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS;
        }
        return 0;
    }


    private void updateMediaSessionIntent() {
        if (mVideoSession == null) {
            return;
        }
        Intent nowPlayIntent = new Intent(getContext(), LiveActivity.class);
        nowPlayIntent.putExtra(LiveActivity.TAG, mMediaMetaData);
        nowPlayIntent.setData(Uri.parse(mMediaMetaData.getMediaSourcePath()));
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, nowPlayIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

    }


    public void setDisplay(SurfaceHolder surfaceHolder) {
        mPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public boolean isMediaPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getMediaDuration() {
        return mInitialized ? mPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mInitialized ? mPlayer.getCurrentPosition() : 0;
    }

    @Override
    protected void startPlayback(int speed) throws IllegalStateException {

        prepareIfNeededAndPlay(mMediaMetaData);
    }

    @Override
    protected void pausePlayback() {
        if (isMediaPlaying()) {
            mPlayer.pause();
            updateVideoSessionPlayState(PlaybackState.STATE_PAUSED);
        }
    }


    public void prepareIfNeededAndPlay(MediaMetaData mediaMetaData) {
        if (mediaMetaData == null) {
            throw new RuntimeException("Provided metadata is null!");
        }

        createMediaPlayerIfNeeded();
        createMediaSessionIfNeeded();
        if (mInitialized && isMediaItemsTheSame(mMediaMetaData, mediaMetaData)) {
            if (!isMediaPlaying()) {
                Log.d(TAG, "mPlayer is started (meta data is the same)");
                mPlayer.start();
                updateVideoSessionPlayState(PlaybackState.STATE_PLAYING);
                onStateChanged();
            }
        } else {
            prepareNewMedia(mediaMetaData);
        }
    }

    public void saveUIState() {
        //onMetadataChanged();
        onStateChanged();
    }

    private boolean isMediaItemsTheSame(MediaMetaData currentMediaMetaData,
                                        MediaMetaData newMediaMetaData) {
        if (currentMediaMetaData == newMediaMetaData) {
            return true;
        }
        if (currentMediaMetaData == null || newMediaMetaData == null) {
            return false;
        }
        if (newMediaMetaData.getMediaSourceUri() != null) {
            return currentMediaMetaData.getMediaSourceUri().equals(
                    newMediaMetaData.getMediaSourceUri()
            );
        }
        if (newMediaMetaData.getMediaSourcePath() != null) {
            return currentMediaMetaData.getMediaSourcePath().equals(
                    newMediaMetaData.getMediaSourcePath()
            );
        }
        return false;
    }

    private void prepareNewMedia(final MediaMetaData mediaMetaData) {
        resetPlayer();

        mMediaMetaData = mediaMetaData;
        updateMediaSessionIntent();
        try {
            if (mediaMetaData.getMediaSourceUri() != null) {
                mPlayer.setDataSource(getContext(), mediaMetaData.getMediaSourceUri());
            } else mPlayer.setDataSource(mediaMetaData.getMediaSourcePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mInitialized = true;
                mPlayer.start();
                updateVideoSessionPlayState(PlaybackState.STATE_PLAYING);
                //onMetadataChanged();
                onStateChanged();

            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mInitialized && mMediaFileStateChangeListener != null)
                    mMediaFileStateChangeListener.onMediaStateChanged(mediaMetaData,
                            MediaUtils.MEDIA_STATE_COMPLETED);
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                Log.e(TAG, "MediaPlayer had error " + what + " extra " + extra);
                return true;
            }
        });
        mPlayer.prepareAsync();
        updateVideoSessionPlayState(PlaybackState.STATE_BUFFERING);
        onStateChanged();

    }

    /**
     * Resets the {@link MediaPlayer} such that a new media file can be played.
     */
    public void resetPlayer() {
        mInitialized = false;
        if (mPlayer != null) {
            mPlayer.reset();
            updateVideoSessionPlayState(PlaybackState.STATE_PAUSED);
        }
    }

    public void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
    }

    /**
     * Releases the {@link MediaPlayer}. The media player is released when the fragment exits
     * (onDestroy() is called on the playback fragment).
     */
    public void releaseMediaPlayer() {
        resetPlayer();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        /*if (!abandonAudioFocus()) {
            Log.e(TAG, "Video player could not abandon audio focus in releaseResources");
        }*/
    }

    public void createMediaSessionIfNeeded() {
        if (mVideoSession == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mVideoSession = new MediaSession(this.getContext(), "VideoPlayer Session");
                mVideoSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
                mVideoSession.setCallback(new VideoSessionCallback());
                mVideoSession.setActive(true);
                updateVideoSessionPlayState(PlaybackState.STATE_NONE);
                getFragment().getActivity().setMediaController(
                        new MediaController(getContext(), mVideoSession.getSessionToken()));
            }

        }
    }

    /**
     * Releases the {@link MediaSession}. The media session is released when the playback fragment
     * is no longer visible. This can happen in onStop() or surfaceDestroyed() (The ordering of these
     * two is not deterministic, and that's why it's important to release the session in both callbacks).
     * Since the media session can be released without the fragment being destroyed,
     * {@link #createMediaSessionIfNeeded()} should be called when the playback becomes active again.
     */
    public void releaseMediaSession() {
        Log.d(TAG, "Media session being released!");
        if (mVideoSession != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mVideoSession.release();
            }
            mVideoSession = null;
        }
        /*if (!abandonAudioFocus()) {
            Log.e(TAG, "Video player could not abandon audio focus in releaseResources");
        }*/
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class VideoSessionCallback extends MediaSession.Callback {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            KeyEvent keyEvent = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Log.d(TAG, "onMediaButtonEvent in VideoSessionCallback called with event: " + keyEvent);
            // No need to handle KEYCODE_MEDIA_PLAY_PAUSE separately, as the super class delegates
            // it to the correct onPlay() or onPause() methods.
            return super.onMediaButtonEvent(mediaButtonIntent);
        }

        @Override
        public void onPlay() {
            Log.d(TAG, "onPlay in VideoSessionCallback called");
            startPlayback();
        }

        @Override
        public void onPause() {
            Log.d(TAG, "onPause in VideoSessionCallback called");
            pausePlayback();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
        }

        @Override
        public void onRewind() {
            super.onRewind();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }
    }
}

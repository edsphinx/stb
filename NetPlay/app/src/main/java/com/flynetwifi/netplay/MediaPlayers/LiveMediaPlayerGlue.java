package com.flynetwifi.netplay.MediaPlayers;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.support.v17.leanback.app.PlaybackFragment;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flynetwifi.netplay.Fragments.LiveFragment;
import com.flynetwifi.netplay.LiveActivity;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.media.MediaMetaData;
import com.flynetwifi.netplay.media.MediaPlayerGlue;
import com.flynetwifi.netplay.media.MediaUtils;

import java.io.IOException;

public abstract class LiveMediaPlayerGlue extends MediaPlayerGlue implements
        AudioManager.OnAudioFocusChangeListener{

    private final PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction;
    private final PlaybackControlsRow.PictureInPictureAction mPipAction;
    private MediaPlayer mPlayer;
    private MediaSession mVideoSession;
    private AudioManager mAudioManager;
    private int mAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
    private static final String TAG = "VideoMediaPlayerGlue";

    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private PlaybackControlsRow.RepeatAction mRepeatAction;
    private PlaybackControlsRow.ThumbsUpAction mThumbsUpAction;
    private PlaybackControlsRow.ThumbsDownAction mThumbsDownAction;
    private PlaybackControlsRow.ShuffleAction mShuffleAction;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private PlaybackControlsRow.HighQualityAction mHighQualityAction;
    private PlaybackControlsRow.MoreActions mMoreActions;

    public LiveMediaPlayerGlue(Context context, PlaybackFragment fragment) {
        super(context, fragment);

        createMediaPlayerIfNeeded();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        // Instantiate secondary actions
        mClosedCaptioningAction = new PlaybackControlsRow.ClosedCaptioningAction(context);
        mPipAction = new PlaybackControlsRow.PictureInPictureAction(context);
        setFadingEnabled(true);
    }

    @Override protected void addSecondaryActions(ArrayObjectAdapter secondaryActionsAdapter) {
        /*mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(getContext());
        mRepeatAction = new PlaybackControlsRow.RepeatAction(getContext());
        mThumbsUpAction = new PlaybackControlsRow.ThumbsUpAction(getContext());
        mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(getContext());
        mShuffleAction = new PlaybackControlsRow.ShuffleAction(getContext());
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getContext());
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getContext());
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getContext());
        mRewindAction = new PlaybackControlsRow.RewindAction(getContext());
        mHighQualityAction = new PlaybackControlsRow.HighQualityAction(getContext());
        mMoreActions = new PlaybackControlsRow.MoreActions(getContext());


        mThumbsUpAction.setId(0);
        secondaryActionsAdapter.add(mThumbsUpAction);
        //secondaryActionsAdapter.add(mThumbsDownAction);
        mRepeatAction.setId(2);
        secondaryActionsAdapter.add(mRepeatAction);
        //secondaryActionsAdapter.add(mShuffleAction);
        //secondaryActionsAdapter.add(mHighQualityAction);
        //secondaryActionsAdapter.add(mMoreActions);*/
    }

    @Override public void onActionClicked(Action action) {
        /*super.onActionClicked(action);
        if (action == mClosedCaptioningAction) {
            mClosedCaptioningAction.nextIndex();
        } else if (action == mPipAction) {
        }*/
    }

    public void setupControlsRowPresenter(PlaybackControlsRowPresenter presenter) {
        presenter.setProgressColor(getContext().getResources().getColor(R.color.colorPrimary));
        presenter.setBackgroundColor(getContext().getResources().getColor(R.color.background));
    }

    private boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private boolean abandonAudioFocus() {
        return mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateVideoSessionMetaData() {
        if (mMediaMetaData == null) {
            throw new IllegalArgumentException(
                    "mCurrentMediaItem is null in updateMediaSessionMetaData!");
        }
        final MediaMetadata.Builder metaDataBuilder = new MediaMetadata.Builder();
        if (mMediaMetaData.getMediaTitle() != null) {
            metaDataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE,
                    mMediaMetaData.getMediaTitle());
        }
        if (mMediaMetaData.getMediaAlbumName() != null) {
            metaDataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM,
                    mMediaMetaData.getMediaAlbumName());
        }
        if (mMediaMetaData.getMediaArtistName() != null) {
            metaDataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST,
                    mMediaMetaData.getMediaArtistName());
        }
        Resources res = getContext().getResources();
        int cardWidth = res.getDimensionPixelSize(R.dimen.playback_now_playing_card_width);
        int cardHeight = res.getDimensionPixelSize(R.dimen.playback_now_playing_card_height);
        if (mMediaMetaData.getMediaAlbumArtUrl() != null) {
            Glide.with(getContext())
                    .load(mMediaMetaData.getMediaAlbumArtUrl())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(cardWidth, cardHeight) {
                              @Override
                              public void onResourceReady(
                                      Bitmap resource,
                                      GlideAnimation<? super Bitmap> glideAnimation) {
                                  metaDataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART,
                                          resource);
                                  mVideoSession.setMetadata(metaDataBuilder.build());
                              }
                          }
                    );
        }

    }

    private void updateVideoSessionPlayState(int playbackState) {
        if (mVideoSession == null) {
            // MediaSession has already been released, no need to update PlaybackState
            return;
        }
        PlaybackState.Builder playbackStateBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playbackStateBuilder = new PlaybackState.Builder();

            long currentPosition = getCurrentPosition();
            playbackStateBuilder.setState(playbackState, currentPosition, (float) 1.0).setActions(
                    PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE |
                            PlaybackState.ACTION_FAST_FORWARD | PlaybackState.ACTION_REWIND |
                            PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS
            );
            mVideoSession.setPlaybackState(playbackStateBuilder.build());
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoSession.setSessionActivity(pi);
        }
    }

    private long getPlaybackStateActions() {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE |
                    PlaybackState.ACTION_FAST_FORWARD | PlaybackState.ACTION_REWIND |
                    PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS;
//        }
//
//        return 0;
    }

    public void setDisplay(SurfaceHolder surfaceHolder) {
        /*int width = 1920;
        int height = 1280;
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dpWidth = width / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpHeight = height / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        surfaceHolder.setFixedSize(Math.round(dpWidth), Math.round(dpHeight));*/

        mPlayer.setDisplay(surfaceHolder);
    }

    @Override public boolean isMediaPlaying() {
        return mPlayer.isPlaying();
    }

    @Override public int getMediaDuration() {
        return mInitialized ? mPlayer.getDuration() : 0;
    }

    @Override public int getCurrentPosition() {
        return mInitialized ? mPlayer.getCurrentPosition() : 0;
    }

//    protected void startPlayback(int speed) throws IllegalStateException {
//        if (requestAudioFocus()) {
//            mAudioFocus = AudioManager.AUDIOFOCUS_GAIN;
//        } else {
//            Log.e(TAG, "Video player could not obtain audio focus in startPlayback");
//            return;
//        }
//        prepareIfNeededAndPlay(mMediaMetaData);
//    }

    public void pausePlayback() {
        if (isMediaPlaying()) {
            mPlayer.pause();
            updateVideoSessionPlayState(PlaybackState.STATE_PAUSED);
        }
    }

    @Override
    protected void seekTo(int newPosition) {
        if (mInitialized) {
            mPlayer.seekTo(newPosition);
        }
    }

    public void prepareIfNeededAndPlay(MediaMetaData mediaMetaData, LiveFragment view) {
        if (mediaMetaData == null) {
            throw new RuntimeException("Provided metadata is null!");
        }
        if (requestAudioFocus()) {
            mAudioFocus = AudioManager.AUDIOFOCUS_GAIN;
        } else {
            Log.e(TAG, "Video player could not obtain audio focus in prepareIfNeededAndPlay");
            return;
        }
        createMediaPlayerIfNeeded();
        createMediaSessionIfNeeded(view);
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
        onMetadataChanged();
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
            }
            else mPlayer.setDataSource(mediaMetaData.getMediaSourcePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                mInitialized = true;
                mPlayer.start();
                updateVideoSessionPlayState(PlaybackState.STATE_PLAYING);
                updateVideoSessionMetaData();
                //onMetadataChanged();
                onStateChanged();
                updateProgress();
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override public void onCompletion(MediaPlayer mp) {
                if (mInitialized && mMediaFileStateChangeListener != null)
                    mMediaFileStateChangeListener.onMediaStateChanged(mediaMetaData,
                            MediaUtils.MEDIA_STATE_COMPLETED);
                mPlayer.start();
            }
        });
        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (mInitialized) {
                    mControlsRow.setBufferedProgress((int) (mp.getDuration() * (percent / 100f)));
                }
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                Log.e(TAG, "MediaPlayer had error " + what  + " extra " + extra);
                mPlayer.pause();
                mPlayer.start();;
                return true;
            }
        });

        mPlayer.prepareAsync();
        updateVideoSessionPlayState(PlaybackState.STATE_BUFFERING);
        onStateChanged();
    }


    public void resetPlayer() {
        mInitialized = false;
        if (mPlayer != null) {
            mPlayer.reset();
            updateVideoSessionPlayState(PlaybackState.STATE_PAUSED);
        }
    }

    private void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
    }


    public void releaseMediaPlayer() {
        resetPlayer();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (!abandonAudioFocus() ) {
            Log.e(TAG, "Video player could not abandon audio focus in releaseResources");
        }
    }

    public void createMediaSessionIfNeeded(LiveFragment view) {
        if (mVideoSession == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mVideoSession = new MediaSession(this.getContext(), "VideoPlayer Session");
                mVideoSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
                mVideoSession.setCallback(new VideoSessionCallback());
                mVideoSession.setActive(true);
                updateVideoSessionPlayState(PlaybackState.STATE_NONE);

                view.getActivity().setMediaController(
                        new MediaController(getContext(), mVideoSession.getSessionToken()));
            }

        }
    }

    public void releaseMediaSession() {
        Log.d(TAG, "Media session being released!");
        if (mVideoSession != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mVideoSession.release();
            }
            mVideoSession = null;
        }
        if (!abandonAudioFocus() ) {
            Log.e(TAG, "Video player could not abandon audio focus in releaseResources");
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "AudioFocus loss transient.");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "AudioFocus loss transient can duck.");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                abandonAudioFocus();
                Log.d(TAG, "AudioFocus loss");
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "AudioFocus Gained");
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class VideoSessionCallback extends MediaSession.Callback {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            KeyEvent keyEvent = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Log.d(TAG, "onMediaButtonEvent in VideoSessionCallback called with event: " + keyEvent);
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

package com.nuevoshorizontes.nhstream.MediaPlayers.Live;

import android.content.Context;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;

import java.util.concurrent.TimeUnit;

import exoplayer2.ext.leanback.LeanbackPlayerAdapter;


public class VideoPlayerGlue extends PlaybackTransportControlGlue<LeanbackPlayerAdapter> {

    /**
     * Constructor for the glue.
     *
     * @param context
     * @param playerAdapter  Implementation to underlying media player.
     */
    public VideoPlayerGlue(
            Context context,
            LeanbackPlayerAdapter playerAdapter) {
        super(context, playerAdapter);
    }
}

package com.flynetwifi.netplay.MediaPlayers;

/**
 * Created by fonseca on 4/12/17.
 */

public class NHLiveMediaPlayerGlue {
    private static final NHLiveMediaPlayerGlue ourInstance = new NHLiveMediaPlayerGlue();

    public static NHLiveMediaPlayerGlue getInstance() {
        return ourInstance;
    }

    private NHLiveMediaPlayerGlue() {
    }
}

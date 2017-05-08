package com.flynetwifi.nhstream.Rows;

import com.flynetwifi.nhstream.Cards.MusicPlaylistCard;

import java.util.List;

public class MusicPlaylistRow {

    private List<MusicPlaylistCard> playlists = null;

    public void setPlaylists(List<MusicPlaylistCard> playlists) {
        this.playlists = playlists;
    }

    public List<MusicPlaylistCard> getPlaylists() {
        return playlists;
    }
}

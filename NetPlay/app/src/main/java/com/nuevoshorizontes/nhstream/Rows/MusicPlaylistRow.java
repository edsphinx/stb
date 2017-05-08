package com.nuevoshorizontes.nhstream.Rows;

import com.nuevoshorizontes.nhstream.Cards.MusicPlaylistCard;

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

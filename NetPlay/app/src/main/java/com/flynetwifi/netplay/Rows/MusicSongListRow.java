package com.flynetwifi.netplay.Rows;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;


public class MusicSongListRow extends ListRow {
    private MusicSongsRow mCardRow;

    public MusicSongListRow(HeaderItem header, ObjectAdapter adapter, MusicSongsRow musicSongsRow) {
        super(header, adapter);
        setMusicSongRow(musicSongsRow);
    }

    public MusicSongsRow getCardRow() {
        return mCardRow;
    }

    private void setMusicSongRow(MusicSongsRow cardRow) {
        this.mCardRow = cardRow;
    }
}

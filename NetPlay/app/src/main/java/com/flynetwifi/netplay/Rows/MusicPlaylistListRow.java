package com.flynetwifi.netplay.Rows;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

/**
 * Created by mauro on 2/8/17.
 */
public class MusicPlaylistListRow extends ListRow {

    private MusicPlaylistRow mCardRow;

    public MusicPlaylistListRow(HeaderItem header, ObjectAdapter adapter, MusicPlaylistRow playlistRow) {
        super(header, adapter);
        setPlaylistRow(playlistRow);
    }

    public MusicPlaylistRow getCardRow() {
        return mCardRow;
    }

    public void setPlaylistRow(MusicPlaylistRow cardRow) {
        this.mCardRow = cardRow;
    }
}

package com.flynetwifi.nhstream.Rows;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

/**
 * Created by mauro on 2/8/17.
 */
public class MusicSingersListRow extends ListRow {

    private MusicSingersRow mCardRow;

    public MusicSingersListRow(HeaderItem header, ObjectAdapter adapter, MusicSingersRow cantantesRow) {
        super(header, adapter);
        setCantantesRow(cantantesRow);
    }

    public MusicSingersRow getCardRow() {
        return mCardRow;
    }

    private void setCantantesRow(MusicSingersRow cardRow) {
        this.mCardRow = cardRow;
    }
}

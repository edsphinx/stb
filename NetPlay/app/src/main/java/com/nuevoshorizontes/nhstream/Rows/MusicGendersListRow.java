package com.nuevoshorizontes.nhstream.Rows;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

public class MusicGendersListRow extends ListRow {

    private MusicGendersRow mCardRow;

    public MusicGendersListRow(HeaderItem header, ObjectAdapter adapter, MusicGendersRow generosRow) {
        super(header, adapter);
        setGenerosRow(generosRow);
    }

    public MusicGendersRow getCardRow() {
        return mCardRow;
    }

    private void setGenerosRow(MusicGendersRow cardRow) {
        this.mCardRow = cardRow;
    }
}

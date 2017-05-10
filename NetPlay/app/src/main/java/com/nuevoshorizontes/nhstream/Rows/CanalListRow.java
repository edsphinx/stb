package com.nuevoshorizontes.nhstream.Rows;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

/**
 * Created by fonseca on 3/29/17.
 */

public class CanalListRow extends ListRow {

    private LiveCanalRow mCardRow;

    public CanalListRow(HeaderItem header, ObjectAdapter adapter, LiveCanalRow canalRow) {
        super(header, adapter);
        setLiveCanalRow(canalRow);
    }

    public LiveCanalRow getCardRow() {
        return mCardRow;
    }

    private void setLiveCanalRow(LiveCanalRow cardRow) {
        this.mCardRow = cardRow;
    }
}

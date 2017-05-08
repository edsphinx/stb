package com.flynetwifi.nhstream.Presenters;

import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import com.flynetwifi.nhstream.Rows.CanalListRow;
import com.flynetwifi.nhstream.Rows.LiveCanalRow;

/**
 * Created by fonseca on 3/28/17.
 */

public class LiveCanalPresenterSelector extends PresenterSelector {

    private ListRowPresenter mShadowEnabledRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL);
    private ListRowPresenter mShadowDisabledRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);

    public LiveCanalPresenterSelector() {
        mShadowEnabledRowPresenter.setNumRows(1);
        mShadowDisabledRowPresenter.setShadowEnabled(false);
        mShadowDisabledRowPresenter.setSelectEffectEnabled(false);
        mShadowDisabledRowPresenter.setKeepChildForeground(false);

    }

    @Override public Presenter getPresenter(Object item) {
        CanalListRow listRow = (CanalListRow) item;
        LiveCanalRow row = listRow.getCardRow();

        //if (row.useShadow()) return mShadowEnabledRowPresenter;
        return mShadowDisabledRowPresenter;
    }

    @Override
    public Presenter[] getPresenters() {
        return new Presenter[] {
                mShadowDisabledRowPresenter,
                mShadowEnabledRowPresenter
        };
    }
}

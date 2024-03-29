/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.nuevoshorizontes.nhstream.Presenters;

import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import com.nuevoshorizontes.nhstream.Rows.MenuListRow;
import com.nuevoshorizontes.nhstream.Rows.MenuRow;


public class MenuPresenterSelector extends PresenterSelector {

    private ListRowPresenter mShadowEnabledRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL);
    private ListRowPresenter mShadowDisabledRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);

    public MenuPresenterSelector() {
        mShadowEnabledRowPresenter.setNumRows(1);
        mShadowDisabledRowPresenter.setShadowEnabled(false);
        mShadowDisabledRowPresenter.setSelectEffectEnabled(false);
        mShadowDisabledRowPresenter.setKeepChildForeground(false);

    }

    @Override public Presenter getPresenter(Object item) {
        MenuListRow listRow = (MenuListRow) item;
        MenuRow row = listRow.getCardRow();

//        if (row.useShadow()) return mShadowEnabledRowPresenter;
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

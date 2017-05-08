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

package com.nuevoshorizontes.nhstream.Rows;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

public class LiveActionsListRow extends ListRow {

    private LiveActionsRow mCardRow;

    public LiveActionsListRow(HeaderItem header, ObjectAdapter adapter, LiveActionsRow liveActionsRow) {
        super(header, adapter);
        setLiveActionsRow(liveActionsRow);
    }

    public LiveActionsRow getCardRow() {
        return mCardRow;
    }

    private void setLiveActionsRow(LiveActionsRow cardRow) {
        this.mCardRow = cardRow;
    }
}

/*
 * Copyright (c) 2015 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */

package com.flynetwifi.nhstream.Rows;

import com.flynetwifi.nhstream.Cards.SeriesCard;

import java.util.List;


public class SeriesRow {

    private static final int TYPE_DEFAULT = 0;

    private int mType = TYPE_DEFAULT;
    private boolean mShadow = true;
    private String mTitle;
    private List<SeriesCard> mCards;

    public static int getTypeDefault() {
        return TYPE_DEFAULT;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public boolean ismShadow() {
        return mShadow;
    }

    public void setmShadow(boolean mShadow) {
        this.mShadow = mShadow;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<SeriesCard> getmCards() {
        return mCards;
    }

    public void setmCards(List<SeriesCard> mCards) {
        this.mCards = mCards;
    }
}

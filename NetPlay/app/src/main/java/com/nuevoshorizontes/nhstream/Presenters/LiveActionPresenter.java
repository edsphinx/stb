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

package com.flynetwifi.nhstream.Presenters;

import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flynetwifi.nhstream.Cards.LiveActionCard;
import com.flynetwifi.netplay.R;


public class LiveActionPresenter extends Presenter {


    static class ViewHolder extends Presenter.ViewHolder {

        public final TextView nombre;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.live_action_card, parent, false);
        itemView.setFocusable(true);
        itemView.setFocusableInTouchMode(true);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final LiveActionCard card = (LiveActionCard) item;

        ((ViewHolder) viewHolder).nombre.setText(card.getmTitulo());


    }


    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TO DO
    }



}
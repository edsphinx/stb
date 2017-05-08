package com.flynetwifi.nhstream.Presenters;


import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flynetwifi.nhstream.Cards.LiveCanalCard;
import com.flynetwifi.netplay.R;

public class LiveChannelInfoPresenter extends Presenter {

    private static final String TAG = Presenter.class.getSimpleName();

    private static Context mContext;


    static class ViewHolder extends Presenter.ViewHolder {

        public TextView nombre;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre);

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.live_channel_info, parent, false);
        itemView.setFocusable(false);
        itemView.setFocusableInTouchMode(false);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final LiveCanalCard card = (LiveCanalCard) item;


        ((ViewHolder) viewHolder).nombre.setText(card.getmTitle());

    }


    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TO DO
    }


}

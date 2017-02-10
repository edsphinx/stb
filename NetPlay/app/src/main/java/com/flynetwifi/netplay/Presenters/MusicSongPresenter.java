package com.flynetwifi.netplay.Presenters;


import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flynetwifi.netplay.Cards.MusicSongCard;
import com.flynetwifi.netplay.R;

public class MusicSongPresenter extends Presenter {

    private static final String TAG = Presenter.class.getSimpleName();

    private static Context mContext;
    private static int CARD_WIDTH = 313;
    private static int CARD_HEIGHT = 176;

    static class ViewHolder extends Presenter.ViewHolder {

        public TextView nombre, numero;
        public ImageView logo;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre);
            numero = (TextView) view.findViewById(R.id.numero);
        }



    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_playlist_card, parent, false);
        itemView.setFocusable(true);
        itemView.setFocusableInTouchMode(true);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final MusicSongCard cancion = (MusicSongCard) item;

        ((ViewHolder) viewHolder).nombre.setText(cancion.getNombre());
        //((ViewHolder) viewHolder).numero.setText(cancion.getmNumero());

//       ((ViewHolder) viewHolder).updateCardViewImage(cancion.getmThumbnail());

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

}
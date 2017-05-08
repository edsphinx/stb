package com.flynetwifi.nhstream.Presenters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flynetwifi.nhstream.Cards.AppCard;
import com.flynetwifi.netplay.R;

public class AppsPresenter extends android.support.v17.leanback.widget.Presenter {

    private static Context mContext;

    static class ViewHolder extends android.support.v17.leanback.widget.Presenter.ViewHolder {

        public final TextView nombre;
        public final ImageView logo;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre);
            logo = (ImageView) view.findViewById(R.id.logo);
            //imagen = (RelativeLayout) view.findViewById(R.id.imagen_menu);
        }

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_card, parent, false);
        itemView.setFocusable(true);
        itemView.setFocusableInTouchMode(true);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(android.support.v17.leanback.widget.Presenter.ViewHolder viewHolder, Object item) {
        final AppCard appsCard = (AppCard) item;



        if(appsCard.getmTitulo().equalsIgnoreCase("netflix")) {
            ((ViewHolder) viewHolder).nombre.setText("");
            ((ViewHolder) viewHolder).logo.getLayoutParams().width = 190;
            ((ViewHolder) viewHolder).logo.getLayoutParams().height = 100;
            ((ViewHolder) viewHolder).logo.setImageResource(R.drawable.netflix);
            //((ViewHolder) viewHolder).imagen.setBackgroundResource(R.drawable.live_tv);
        }else{
            ((ViewHolder) viewHolder).nombre.setText(appsCard.getmTitulo());
            ((ViewHolder) viewHolder).logo.setImageDrawable(appsCard.getmImagen());
        }

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager manager;
                manager =   mContext.getPackageManager();
                Intent i = manager.getLaunchIntentForPackage(appsCard.getmPaquete());
                mContext.startActivity(i);
            }
        });

    }


    @Override
    public void onUnbindViewHolder(android.support.v17.leanback.widget.Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(android.support.v17.leanback.widget.Presenter.ViewHolder viewHolder) {
        // TO DO
    }
}
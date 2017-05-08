package com.flynetwifi.nhstream.Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flynetwifi.nhstream.Cards.LiveFavoriteCanalCard;
import com.flynetwifi.netplay.R;

/**
 * Created by fonseca on 5/3/17.
 */

public class LiveFavoriteCanalPresenter extends Presenter {


    private static Context mContext;
    private static final int CARD_WIDTH = 150;
    private static final int CARD_HEIGHT = 150;

    static class ViewHolder extends Presenter.ViewHolder {

        public final TextView name;
        public final TextView number;
        public final ImageView logo;

        //public final PicassoImageCardViewTarget mImageCardViewTarget;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.nombre);
            number = (TextView) view.findViewById(R.id.numero);
            logo = (ImageView) view.findViewById(R.id.thumbnail);
            //mImageCardViewTarget = new PicassoImageCardViewTarget(logo);
        }

        public ImageView getLogo() {
            return logo;
        }

        void updateCardViewImage(String uri) {
//            int resourceId = mContext.getResources()
//                    .getIdentifier("live_placeholder",
//                            "drawable", mContext.getPackageName());

//            Glide.with(mContext)
//                    .load(resourceId)
//                    .into(logo);
//            Picasso.with(mContext)
//                    .load(resourceId)
//                    .into(mImageCardViewTarget);

            Glide.with(mContext)
                    .load(uri)
                    .placeholder(R.drawable.placeholder)
                    .skipMemoryCache(true)
                    .into(logo);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.live_canal_card, parent, false);
        itemView.setFocusable(true);
        itemView.setFocusableInTouchMode(true);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final LiveFavoriteCanalCard card = (LiveFavoriteCanalCard) item;


        ((ViewHolder) viewHolder).name.setText(card.getmTitle());
        ((ViewHolder) viewHolder).number.setText(String.valueOf(card.getmNumero()));

        if (card.getmEstado() == 0) {
            ((ViewHolder) viewHolder).updateCardViewImage(card.getmLogo());
        } else {
            //((ViewHolder) viewHolder).updateCardViewImage(card.getmThumbnail());
            ((ViewHolder) viewHolder).updateCardViewImage(card.getmLogo());
        }

    }


    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TO DO
    }

}
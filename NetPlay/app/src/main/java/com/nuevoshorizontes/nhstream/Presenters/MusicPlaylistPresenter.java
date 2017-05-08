package com.nuevoshorizontes.nhstream.Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nuevoshorizontes.nhstream.Cards.MusicPlaylistCard;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Utils.Utils;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;

/**
 * Created by mauro on 2/8/17.
 */
public class MusicPlaylistPresenter  extends Presenter {

    private static final String TAG = Presenter.class.getSimpleName();

    private static Context mContext;
    private static int CARD_WIDTH = 313;
    private static int CARD_HEIGHT = 176;

    static class ViewHolder extends Presenter.ViewHolder {

        public TextView nombre, numero;
        public ImageView logo;
        //public PicassoImageCardViewTarget mImageCardViewTarget;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre);
            numero = (TextView) view.findViewById(R.id.numero);
           // mImageCardViewTarget = new PicassoImageCardViewTarget(logo);
        }

        protected void updateCardViewImage(String uri) {
            Glide
                    .with(mContext)
                    .load(uri)
                    .override(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                            Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                    .placeholder(R.drawable.bg_poster)
                    .error(R.drawable.bg_poster)
                    .into(logo);
//            Picasso.with(mContext)
//                    .load(uri)
//                    .resize(Utils.convertDpToPixel(mContext, CARD_WIDTH),
//                            Utils.convertDpToPixel(mContext, CARD_HEIGHT))
//                    .placeholder(R.drawable.bg_poster)
//                    .error(R.drawable.bg_poster)
//                    .into(mImageCardViewTarget);
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
        final MusicPlaylistCard cancion = (MusicPlaylistCard) item;

        ((ViewHolder) viewHolder).nombre.setText(cancion.getmNombre());
        //((ViewHolder) viewHolder).numero.setText(cancion.getmNumero());
        ((ViewHolder) viewHolder).numero.setText(cancion.getmNumero());

        // ((ViewHolder) viewHolder).updateCardViewImage(cancion);

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

//    public static class PicassoImageCardViewTarget implements Target {
//        private ImageView mImageCardView;
//
//        public PicassoImageCardViewTarget(ImageView imageCardView) {
//            mImageCardView = imageCardView;
//        }
//
//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
//            mImageCardView.setImageBitmap(bitmap);
//        }
//
//        @Override
//        public void onBitmapFailed(Drawable drawable) {
//            mImageCardView.setImageDrawable(null);
//        }
//
//        @Override
//        public void onPrepareLoad(Drawable drawable) {
//            // Do nothing, default_background manager has its own transitions
//        }
//    }

}
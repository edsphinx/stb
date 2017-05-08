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

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nuevoshorizontes.nhstream.Cards.MovieRecommendedCard;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Utils.Utils;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;



public class MovieRecommendedPresenter extends Presenter {

    private static final String TAG = Presenter.class.getSimpleName();

    private static Context mContext;
    private static int CARD_WIDTH = 140;
    private static int CARD_HEIGHT = 220;

    static class ViewHolder extends Presenter.ViewHolder {

        public TextView nombre;
        public ImageView logo;

        //public PicassoImageCardViewTarget mImageCardViewTarget;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre);
            logo = (ImageView) view.findViewById(R.id.thumbnail);
            /// mImageCardViewTarget = new PicassoImageCardViewTarget(logo); */
        }

        public ImageView getLogo(){
            return logo;
        }

        void updateCardViewImage(String uri) {
                Glide
                        .with(mContext)
                        .load(uri)
                        .centerCrop()
                        .override(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                                Utils.convertDpToPixel(mContext, CARD_HEIGHT))
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
                .inflate(R.layout.movie_card, parent, false);
        itemView.setFocusable(true);
        itemView.setFocusableInTouchMode(true);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final MovieRecommendedCard card = (MovieRecommendedCard) item;


        ((ViewHolder) viewHolder).nombre.setText(card.getmTitle());

        ((ViewHolder) viewHolder).updateCardViewImage(card.getmLogo());

    }


    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TO DO
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
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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.nuevoshorizontes.nhstream.Cards.MovieCard;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Utils.Utils;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;
import com.bumptech.glide.request.target.Target;



public class MoviePresenter extends android.support.v17.leanback.widget.Presenter {

    private static final String TAG = Presenter.class.getSimpleName();

    private static Context mContext;
    private static int CARD_WIDTH = 313;
    private static int CARD_HEIGHT = 176;
//    private static int CARD_WIDTH = 140;
//    private static int CARD_HEIGHT = 220;

    static class ViewHolder extends android.support.v17.leanback.widget.Presenter.ViewHolder {

        public TextView nombre;
        public ImageView logo;

//        public PicassoImageCardViewTarget mImageCardViewTarget;
        public GlideImageCardViewTarget mImageCardViewTarget;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.nombre);
            logo = (ImageView) view.findViewById(R.id.thumbnail);
//            mImageCardViewTarget = new PicassoImageCardViewTarget(logo);
            //mImageCardViewTarget = new GlideImageCardViewTarget(logo);
        }

        public ImageView getLogo(){
            return logo;
        }

        void updateCardViewImage(String uri) {
            Glide
                    .with(mContext)
                    .load(uri)
                    .override(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                            Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                    //.placeholder(R.drawable.bg_poster)
                    .error(R.drawable.bg_poster)
                    .skipMemoryCache(false)
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
    public void onBindViewHolder(android.support.v17.leanback.widget.Presenter.ViewHolder viewHolder, Object item) {
        final MovieCard pelicula = (MovieCard) item;

        ((ViewHolder) viewHolder).nombre.setText(pelicula.getmNombre());

       ((ViewHolder) viewHolder).updateCardViewImage(pelicula.getmThumbnail());

    }


    @Override
    public void onUnbindViewHolder(android.support.v17.leanback.widget.Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(android.support.v17.leanback.widget.Presenter.ViewHolder viewHolder) {
        // TO DO
    }

    public static class GlideImageCardViewTarget implements Target {
        private ImageView mImageCardView;

        public GlideImageCardViewTarget(ImageView imageCardView) {
            mImageCardView = imageCardView;
        }

        public void onBitmapLoaded(Bitmap bitmap, Glide loadedFrom) {
            mImageCardView.setImageBitmap(bitmap);
        }

        public void onBitmapFailed(Drawable drawable) {
            mImageCardView.setImageDrawable(null);
        }

        public void onPrepareLoad(Drawable drawable) {
            // Do nothing, default_background manager has its own transitions
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {

        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onResourceReady(Object resource, GlideAnimation glideAnimation) {

        }

        @Override
        public void onLoadCleared(Drawable placeholder) {

        }

        @Override
        public void getSize(SizeReadyCallback cb) {

        }

        @Override
        public void setRequest(Request request) {

        }

        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onDestroy() {

        }
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
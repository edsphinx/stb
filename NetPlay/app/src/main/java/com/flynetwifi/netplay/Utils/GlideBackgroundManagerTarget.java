package com.flynetwifi.netplay.Utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.app.BackgroundManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import static android.R.attr.bitmap;

/**
 * Created by fonseca on 4/18/17.
 */

public class GlideBackgroundManagerTarget implements Target {

    private BackgroundManager mBackgroundManager;

    public GlideBackgroundManagerTarget(BackgroundManager backgroundManager) {
        this.mBackgroundManager = backgroundManager;
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {

    }

    public void onBitmapLoaded(Bitmap bitmap, GlideDrawable loadedFrom) {
        this.mBackgroundManager.setBitmap(bitmap);
    }

    public void onBitmapLoaded(Bitmap bitmap) {
        this.mBackgroundManager.setBitmap(bitmap);
    }

    public void onBitmapFailed(Drawable drawable) {
        this.mBackgroundManager.setDrawable(drawable);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlideBackgroundManagerTarget that = (GlideBackgroundManagerTarget) o;

        if (!mBackgroundManager.equals(that.mBackgroundManager)) return false;

        return true;
    }

    public int hashCode() {
        return mBackgroundManager.hashCode();
    }

//    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//        this.mBackgroundManager.setBitmap(resource);
//    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        this.mBackgroundManager.setDrawable(errorDrawable);
    }

    @Override
    public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
        this.mBackgroundManager.setBitmap((Bitmap)resource);
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

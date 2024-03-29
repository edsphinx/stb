package com.nuevoshorizontes.nhstream.Fragments;

/**
 * Created by fonseca on 4/01/17.
 */

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.animation.LogAccelerateInterpolator;
import android.support.v17.leanback.animation.LogDecelerateInterpolator;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;

/**
 * A fragment for displaying playback controls and related content.
 * <p>
 * A NHPlaybackOverlayFragment renders the elements of its {@link ObjectAdapter} as a set
 * of rows in a vertical list.  The Adapter's {@link PresenterSelector} must maintain subclasses
 * of {@link RowPresenter}.
 * </p>
 * <p>
 *     Este es un link {@link android.support.v17.leanback.app.PlaybackOverlaySupportFragment}
 * An instance of {@link android.support.v17.leanback.widget.PlaybackControlsRow} is expected to be
 * at position 0 in the adapter.
 * </p>
 */
public class NHPlaybackOverlayFragment extends NHDetailsFragment {

    /**
     * No background.
     */
    public static final int BG_NONE = 0;

    /**
     * A dark translucent background.
     */
    public static final int BG_DARK = 1;

    /**
     * A light translucent background.
     */
    public static final int BG_LIGHT = 2;

    /**
     * Listener allowing the application to receive notification of fade in and/or fade out
     * completion events.
     */
    public static class OnFadeCompleteListener {
        public void onFadeInComplete() {
        }
        public void onFadeOutComplete() {
        }
    }

    /**
     * Interface allowing the application to handle input events.
     */
    public interface InputEventHandler {
        /**
         * Called when an {@link InputEvent} is received.
         *
         * @return If the event should be consumed, return true. To allow the event to
         * continue on to the next handler, return false.
         */
        public boolean handleInputEvent(InputEvent event);
    }

    private static final String TAG = "PlaybackOverlayFragment";
    private static final boolean DEBUG = false;
    private static final int ANIMATION_MULTIPLIER = 1;

    private static int START_FADE_OUT = 1;

    // Fading status
    private static final int IDLE = 0;
    private static final int IN = 1;
    private static final int OUT = 2;

    private int mPadPos;
    private int mPaddingTop;
    private int mPaddingBottom;
    private View mRootView;
    private int mBackgroundType = BG_DARK;
    private int mBgDarkColor;
    private int mBgLightColor;
    private int mShowTimeMs;
    private int mIddleTimeMs;
    private int mMajorFadeTranslateY, mMinorFadeTranslateY;
    private int mAnimationTranslateY;
    private OnFadeCompleteListener mFadeCompleteListener;
    private InputEventHandler mInputEventHandler;
    private boolean mFadingEnabled = true;
    private int mFadingStatus = IDLE;
    private int mBgAlpha;
    private ValueAnimator mBgFadeInAnimator, mBgFadeOutAnimator;
    private ValueAnimator mControlRowFadeInAnimator, mControlRowFadeOutAnimator;
    private ValueAnimator mDescriptionFadeInAnimator, mDescriptionFadeOutAnimator;
    private ValueAnimator mOtherRowFadeInAnimator, mOtherRowFadeOutAnimator;
    private boolean mTranslateAnimationEnabled;
    private boolean mResetControlsToPrimaryActionsPending;
    private RecyclerView.ItemAnimator mItemAnimator;

    private final Animator.AnimatorListener mFadeListener =
            new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    enableVerticalGridAnimations(false);
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (DEBUG) Log.v(TAG, "onAnimationEnd " + mBgAlpha);
                    if (mBgAlpha > 0) {
                        enableVerticalGridAnimations(true);
                        startFadeTimer(mPadPos);
                        if (mFadeCompleteListener != null) {
                            mFadeCompleteListener.onFadeInComplete();
                        }
                    } else {
                        VerticalGridView verticalView = getVerticalGridView();
                        // reset focus to the primary actions only if the selected row was the controls row
                        if (verticalView != null && verticalView.getSelectedPosition() == 0) {
                            resetControlsToPrimaryActions(null);
                        }
                        if (mFadeCompleteListener != null) {
                            mFadeCompleteListener.onFadeOutComplete();
                        }
                    }
                    mFadingStatus = IDLE;
                }
            };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == START_FADE_OUT && mFadingEnabled) {
                fade(false);
            }
        }
    };

    private final VerticalGridView.OnTouchInterceptListener mOnTouchInterceptListener =
            new VerticalGridView.OnTouchInterceptListener() {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent event) {
                    return onInterceptInputEvent(event);
                }
            };

    private final VerticalGridView.OnKeyInterceptListener mOnKeyInterceptListener =
            new VerticalGridView.OnKeyInterceptListener() {
                @Override
                public boolean onInterceptKeyEvent(KeyEvent event) {
                    return onInterceptInputEvent(event);
                }
            };

    private void setBgAlpha(int alpha) {
        mBgAlpha = alpha;
        if (mRootView != null) {
            mRootView.getBackground().setAlpha(alpha);
        }
    }

    private void enableVerticalGridAnimations(boolean enable) {
        if (getVerticalGridView() != null) {
            getVerticalGridView().setAnimateChildLayout(enable);
        }
    }

    private void resetControlsToPrimaryActions(ItemBridgeAdapter.ViewHolder vh) {
        if (vh == null && getVerticalGridView() != null) {
            vh = (ItemBridgeAdapter.ViewHolder) getVerticalGridView().findViewHolderForPosition(0);
        }
        if (vh == null) {
            mResetControlsToPrimaryActionsPending = true;
        } else if (vh.getPresenter() instanceof PlaybackControlsRowPresenter) {
            mResetControlsToPrimaryActionsPending = false;
            ((PlaybackControlsRowPresenter) vh.getPresenter()).showPrimaryActions(
                    (PlaybackControlsRowPresenter.ViewHolder) vh.getViewHolder());
        }
    }

    /**
     * Enables or disables view fading.  If enabled,
     * the view will be faded in when the fragment starts,
     * and will fade out after a time period.  The timeout
     * period is reset each time {@link #tickle} is called.
     *
     */
    public void setFadingEnabled(boolean enabled) {
        if (DEBUG) Log.v(TAG, "setFadingEnabled " + enabled);
        if (enabled != mFadingEnabled) {
            mFadingEnabled = enabled;
            if (mFadingEnabled) {
                if (isResumed() && mFadingStatus == IDLE
                        && !mHandler.hasMessages(START_FADE_OUT)) {
                    startFadeTimer(mPadPos);
                }
            } else {
                // Ensure fully opaque
                if(mPadPos<2) {
                    mHandler.removeMessages(START_FADE_OUT);
                    fade(true);
                    //startFadeTimer();
                }
            }
        }
    }

    /**
     * Returns true if view fading is enabled.
     */
    public boolean isFadingEnabled() {
        return mFadingEnabled;
    }

    /**
     * Sets the listener to be called when fade in or out has completed.
     */
    public void setFadeCompleteListener(OnFadeCompleteListener listener) {
        mFadeCompleteListener = listener;
        mPadPos = 0;
    }

    /**
     * Returns the listener to be called when fade in or out has completed.
     */
    public OnFadeCompleteListener getFadeCompleteListener() {
        return mFadeCompleteListener;
    }

    /**
     * Sets the input event handler.
     */
    public final void setInputEventHandler(InputEventHandler handler) {
        mInputEventHandler = handler;
    }

    /**
     * Returns the input event handler.
     */
    public final InputEventHandler getInputEventHandler() {
        return mInputEventHandler;
    }

    /**
     * Tickles the playback controls.  Fades in the view if it was faded out,
     * otherwise resets the fade out timer.  Tickling on input events is handled
     * by the fragment.
     */
    public void tickle() {
        if (DEBUG) Log.v(TAG, "tickle enabled " + mFadingEnabled + " isResumed " + isResumed());
        if (!mFadingEnabled || !isResumed()) {
            return;
        }
        if (mHandler.hasMessages(START_FADE_OUT)) {
            // Restart the timer
            startFadeTimer(mPadPos);
        } else {
            fade(true);
        }
    }

    /**
     * Fades out the playback overlay immediately.
     */
    public void fadeOut() {
        mHandler.removeMessages(START_FADE_OUT);
        fade(false);
    }

    public void fadeShitOut(){
        mPadPos = 2;
        mHandler.removeMessages(START_FADE_OUT);
        mFadingStatus = IDLE;
        mBgAlpha = 0;
        fade(false);
    }

    private boolean areControlsHidden() {
        return mFadingStatus == IDLE && mBgAlpha == 0;
    }

    private boolean onInterceptInputEvent(InputEvent event) {
        final boolean controlsHidden = areControlsHidden();
        if (DEBUG) Log.v(TAG, "onInterceptInputEvent hidden " + controlsHidden + " " + event);
        boolean consumeEvent = false;
        int keyCode = KeyEvent.KEYCODE_UNKNOWN;

        if (mInputEventHandler != null) {
            consumeEvent = mInputEventHandler.handleInputEvent(event);
        }
        if (event instanceof KeyEvent) {
            keyCode = ((KeyEvent) event).getKeyCode();
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mPadPos=1;
//            case KeyEvent.KEYCODE_DPAD_DOWN:
//            case KeyEvent.KEYCODE_DPAD_UP:
                // Event may be consumed; regardless, if controls are hidden then these keys will
                // bring up the controls.
                if (controlsHidden) {
                    consumeEvent = true;
                }
                tickle();
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                mPadPos = 0;
                // If fading enabled and controls are not hidden, back will be consumed to fade
                // them out (even if the key was consumed by the handler).
                if (mFadingEnabled && !controlsHidden) {
                    consumeEvent = true;
                    mHandler.removeMessages(START_FADE_OUT);
                    if(mFadingStatus == OUT){
                        mFadingStatus = IDLE;
                    }
                    if (mBgAlpha == 0){
                        mBgAlpha = 255;
                    }
                    fade(false);
                } else if (consumeEvent) {
                    tickle();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                mPadPos = 2;
                // Event may be consumed; regardless,
                if(controlsHidden){
                    consumeEvent = true;
                }else {
                      if (consumeEvent) {
                          fadeOut();
                      }else{
                          tickle();
                      }
                }

//                if (controlsHidden) {
//                    consumeEvent = true;
//                    tickle();
//
//                }else{
//                    consumeEvent = true;
//                    mHandler.removeMessages(START_FADE_OUT);
//                    fade(false);
//
//                }
                break;
            default:
                mPadPos = 0;
                if (consumeEvent) {
                    tickle();
                }
        }
        return consumeEvent;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFadingEnabled) {
            setBgAlpha(0);
            fade(true);
        }
        getVerticalGridView().setOnTouchInterceptListener(mOnTouchInterceptListener);
        getVerticalGridView().setOnKeyInterceptListener(mOnKeyInterceptListener);
    }

    private void startFadeTimer(int mPos) {
        final boolean controlsHidden = areControlsHidden();
        if (mHandler != null) {
            mHandler.removeMessages(START_FADE_OUT);
            if(mPos < 2)
                mHandler.sendEmptyMessageDelayed(START_FADE_OUT, mShowTimeMs);
            else
                mHandler.sendEmptyMessageDelayed(START_FADE_OUT, mIddleTimeMs);
        }
    }

    private static ValueAnimator loadAnimator(Context context, int resId) {
        ValueAnimator animator = (ValueAnimator) AnimatorInflater.loadAnimator(context, resId);
        animator.setDuration(animator.getDuration() * ANIMATION_MULTIPLIER);
        return animator;
    }

    private void loadBgAnimator() {
        ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                setBgAlpha((Integer) arg0.getAnimatedValue());
            }
        };

        mBgFadeInAnimator = loadAnimator(getActivity(), android.support.v17.leanback.R.animator.lb_playback_bg_fade_in);
        mBgFadeInAnimator.addUpdateListener(listener);
        mBgFadeInAnimator.addListener(mFadeListener);

        mBgFadeOutAnimator = loadAnimator(getActivity(), android.support.v17.leanback.R.animator.lb_playback_bg_fade_out);
        mBgFadeOutAnimator.addUpdateListener(listener);
        mBgFadeOutAnimator.addListener(mFadeListener);
    }

    private TimeInterpolator mLogDecelerateInterpolator = new LogDecelerateInterpolator(100,0);
    private TimeInterpolator mLogAccelerateInterpolator = new LogAccelerateInterpolator(100,0);

    private View getControlRowView() {
        if (getVerticalGridView() == null) {
            return null;
        }
        RecyclerView.ViewHolder vh = getVerticalGridView().findViewHolderForPosition(0);
        if (vh == null) {
            return null;
        }
        return vh.itemView;
    }

    private void loadControlRowAnimator() {
        final NHPlaybackOverlayFragment.AnimatorListener listener = new NHPlaybackOverlayFragment.AnimatorListener() {
            @Override
            void getViews(ArrayList<View> views) {
                View view = getControlRowView();
                if (view != null) {
                    views.add(view);
                }
            }
        };
        final ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                View view = getControlRowView();
                if (view != null) {
                    final float fraction = (Float) arg0.getAnimatedValue();
                    if (DEBUG) Log.v(TAG, "fraction " + fraction);
                    view.setAlpha(fraction);
                    view.setTranslationY((float) mAnimationTranslateY * (1f - fraction));
                }
            }
        };

        mControlRowFadeInAnimator = loadAnimator(
                getActivity(), android.support.v17.leanback.R.animator.lb_playback_controls_fade_in);
        mControlRowFadeInAnimator.addUpdateListener(updateListener);
        mControlRowFadeInAnimator.addListener(listener);
        mControlRowFadeInAnimator.setInterpolator(mLogDecelerateInterpolator);

        mControlRowFadeOutAnimator = loadAnimator(
                getActivity(), android.support.v17.leanback.R.animator.lb_playback_controls_fade_out);
        mControlRowFadeOutAnimator.addUpdateListener(updateListener);
        mControlRowFadeOutAnimator.addListener(listener);
        mControlRowFadeOutAnimator.setInterpolator(mLogAccelerateInterpolator);
    }

    private void loadOtherRowAnimator() {
        final NHPlaybackOverlayFragment.AnimatorListener listener = new NHPlaybackOverlayFragment.AnimatorListener() {
            @Override
            void getViews(ArrayList<View> views) {
                if (getVerticalGridView() == null) {
                    return;
                }
                final int count = getVerticalGridView().getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = getVerticalGridView().getChildAt(i);
                    if (view != null) {
                        views.add(view);
                    }
                }
            }
        };
        final ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                if (getVerticalGridView() == null) {
                    return;
                }
                final float fraction = (Float) arg0.getAnimatedValue();
                for (View view : listener.mViews) {
                    if (getVerticalGridView().getChildPosition(view) > 0) {
                        view.setAlpha(fraction);
                        view.setTranslationY((float) mAnimationTranslateY * (1f - fraction));
                    }
                }
            }
        };

        mOtherRowFadeInAnimator = loadAnimator(
                getActivity(), android.support.v17.leanback.R.animator.lb_playback_controls_fade_in);
        mOtherRowFadeInAnimator.addListener(listener);
        mOtherRowFadeInAnimator.addUpdateListener(updateListener);
        mOtherRowFadeInAnimator.setInterpolator(mLogDecelerateInterpolator);

        mOtherRowFadeOutAnimator = loadAnimator(
                getActivity(), android.support.v17.leanback.R.animator.lb_playback_controls_fade_out);
        mOtherRowFadeOutAnimator.addListener(listener);
        mOtherRowFadeOutAnimator.addUpdateListener(updateListener);
        mOtherRowFadeOutAnimator.setInterpolator(new AccelerateInterpolator());
    }

    private void loadDescriptionAnimator() {
        ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                if (getVerticalGridView() == null) {
                    return;
                }
                ItemBridgeAdapter.ViewHolder adapterVh = (ItemBridgeAdapter.ViewHolder)
                        getVerticalGridView().findViewHolderForPosition(0);
                if (adapterVh != null && adapterVh.getViewHolder()
                        instanceof PlaybackControlsRowPresenter.ViewHolder) {
                    final Presenter.ViewHolder vh = ((PlaybackControlsRowPresenter.ViewHolder)
                            adapterVh.getViewHolder()).mDescriptionViewHolder;
                    if (vh != null) {
                        vh.view.setAlpha((Float) arg0.getAnimatedValue());
                    }
                }
            }
        };

        mDescriptionFadeInAnimator = loadAnimator(
                getActivity(), android.support.v17.leanback.R.animator.lb_playback_description_fade_in);
        mDescriptionFadeInAnimator.addUpdateListener(listener);
        mDescriptionFadeInAnimator.setInterpolator(mLogDecelerateInterpolator);

        mDescriptionFadeOutAnimator = loadAnimator(
                getActivity(), android.support.v17.leanback.R.animator.lb_playback_description_fade_out);
        mDescriptionFadeOutAnimator.addUpdateListener(listener);
    }

    private void fade(boolean fadeIn) {
        if (DEBUG) Log.v(TAG, "fade " + fadeIn);
        if (getView() == null) {
            return;
        }
        if ((fadeIn && mFadingStatus == IN) || (!fadeIn && mFadingStatus == OUT)) {
            if (DEBUG) Log.v(TAG, "requested fade in progress");
            return;
        }
        if ((fadeIn && mBgAlpha == 255) || (!fadeIn && mBgAlpha == 0)) {
            if (DEBUG) Log.v(TAG, "fade is no-op");
            return;
        }

        mAnimationTranslateY = getVerticalGridView().getSelectedPosition() == 0 ?
                mMajorFadeTranslateY : mMinorFadeTranslateY;

        if( mPadPos < 2) {
            if (mFadingStatus == IDLE) {
                if (fadeIn) {
                    mBgFadeInAnimator.start();
                    mControlRowFadeInAnimator.start();
                    mOtherRowFadeInAnimator.start();
                    mDescriptionFadeInAnimator.start();
                } else {
                    mBgFadeOutAnimator.start();
                    mControlRowFadeOutAnimator.start();
                    mOtherRowFadeOutAnimator.start();
                    mDescriptionFadeOutAnimator.start();
                }
            } else {
                if (fadeIn) {
                    mBgFadeOutAnimator.reverse();
                    mControlRowFadeOutAnimator.reverse();
                    mOtherRowFadeOutAnimator.reverse();
                    mDescriptionFadeOutAnimator.reverse();
                } else {
                    mBgFadeInAnimator.reverse();
                    mControlRowFadeInAnimator.reverse();
                    mOtherRowFadeInAnimator.reverse();
                    mDescriptionFadeInAnimator.reverse();
                }
            }
        }else{
            mFadingStatus = IDLE;
            fadeIn = true;
            mBgAlpha = 255;
            startFadeTimer(mPadPos);
        }

        // If fading in while control row is focused, set initial translationY so
        // views slide in from below.
        if (fadeIn && mFadingStatus == IDLE && mPadPos < 2) {
            final int count = getVerticalGridView().getChildCount();
            for (int i = 0; i < count; i++) {
                getVerticalGridView().getChildAt(i).setTranslationY(mAnimationTranslateY);
            }
        }

        if(mPadPos < 2) {
            mFadingStatus = fadeIn ? IN : OUT;
        }else{
            mPadPos = 0;
        }
    }

    /**
     * Sets the list of rows for the fragment.
     */
    @Override
    public void setAdapter(ObjectAdapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterObserver(mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerObserver(mObserver);
        }
    }

    @Override
    void setVerticalGridViewLayout(VerticalGridView listview) {
        if (listview == null) {
            return;
        }
        // Padding affects alignment when last row is focused
        // (last is first when there's only one row).
        setPadding(listview, mPaddingTop, mPaddingBottom);

        // Item alignment affects focused row that isn't the last.
        listview.setItemAlignmentOffset(0);
        listview.setItemAlignmentOffsetPercent(50);

        // Push rows to the bottom.
        listview.setWindowAlignmentOffset(0);
        listview.setWindowAlignmentOffsetPercent(50);
        listview.setWindowAlignment(VerticalGridView.WINDOW_ALIGN_BOTH_EDGE);
    }

    private static void setPadding(View view, int paddingTop, int paddingBottom) {
        view.setPadding(view.getPaddingLeft(), paddingTop,
                view.getPaddingRight(), paddingBottom);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPaddingTop =
                getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_playback_controls_padding_top);
        mPaddingBottom =
                getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_playback_controls_padding_bottom);
        mBgDarkColor =
                getResources().getColor(android.support.v17.leanback.R.color.lb_playback_controls_background_dark);
        mBgLightColor =
                getResources().getColor(android.support.v17.leanback.R.color.lb_playback_controls_background_light);
        mShowTimeMs = 6000;//getResources().getInteger(android.support.v17.leanback.R.integer.lb_playback_controls_show_time_ms);
        mIddleTimeMs =
                500;
        mMajorFadeTranslateY =
                getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_playback_major_fade_translate_y);
        mMinorFadeTranslateY =
                getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_playback_minor_fade_translate_y);

        loadBgAnimator();
        loadControlRowAnimator();
        loadOtherRowAnimator();
        loadDescriptionAnimator();
    }

    /**
     * Sets the background type.
     *
     * @param type One of BG_LIGHT, BG_DARK, or BG_NONE.
     */
    public void setBackgroundType(int type) {
        switch (type) {
            case BG_LIGHT:
            case BG_DARK:
            case BG_NONE:
                if (type != mBackgroundType) {
                    mBackgroundType = type;
                    updateBackground();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid background type");
        }
    }

    /**
     * Returns the background type.
     */
    public int getBackgroundType() {
        return mBackgroundType;
    }

    private void updateBackground() {
        if (mRootView != null) {
            int color = mBgDarkColor;
            switch (mBackgroundType) {
                case BG_DARK: break;
                case BG_LIGHT: color = mBgLightColor; break;
                case BG_NONE: color = Color.TRANSPARENT; break;
            }
            mRootView.setBackground(new ColorDrawable(color));
        }
    }

    private void updateControlsBottomSpace(ItemBridgeAdapter.ViewHolder vh) {
        // Add extra space between rows 0 and 1
        if (vh == null && getVerticalGridView() != null) {
            vh = (ItemBridgeAdapter.ViewHolder)
                    getVerticalGridView().findViewHolderForPosition(0);
        }
        if (vh != null && vh.getPresenter() instanceof PlaybackControlsRowPresenter) {
            final int adapterSize = getAdapter() == null ? 0 : getAdapter().size();
            ((PlaybackControlsRowPresenter) vh.getPresenter()).showBottomSpace(
                    (PlaybackControlsRowPresenter.ViewHolder) vh.getViewHolder(),
                    adapterSize > 1);
        }
    }

    private final ItemBridgeAdapter.AdapterListener mAdapterListener =
            new ItemBridgeAdapter.AdapterListener() {
                @Override
                public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder vh) {
                    if (DEBUG) Log.v(TAG, "onAttachedToWindow " + vh.getViewHolder().view);
                    if ((mFadingStatus == IDLE && mBgAlpha == 0) || mFadingStatus == OUT) {
                        if (DEBUG) Log.v(TAG, "setting alpha to 0");
                        vh.getViewHolder().view.setAlpha(0);
                    }
                    if (vh.getPosition() == 0 && mResetControlsToPrimaryActionsPending) {
                        resetControlsToPrimaryActions(vh);
                    }
                }
                @Override
                public void onDetachedFromWindow(ItemBridgeAdapter.ViewHolder vh) {
                    if (DEBUG) Log.v(TAG, "onDetachedFromWindow " + vh.getViewHolder().view);
                    // Reset animation state
                    vh.getViewHolder().view.setAlpha(1f);
                    vh.getViewHolder().view.setTranslationY(0);
                    if (vh.getViewHolder() instanceof PlaybackControlsRowPresenter.ViewHolder) {
                        Presenter.ViewHolder descriptionVh = ((PlaybackControlsRowPresenter.ViewHolder)
                                vh.getViewHolder()).mDescriptionViewHolder;
                        if (descriptionVh != null) {
                            descriptionVh.view.setAlpha(1f);
                        }
                    }
                }
                @Override
                public void onBind(ItemBridgeAdapter.ViewHolder vh) {
                    if (vh.getPosition() == 0) {
                        updateControlsBottomSpace(vh);
                    }
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        mBgAlpha = 255;
        updateBackground();
        getRowsFragment().setExternalAdapterListener(mAdapterListener);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        mRootView = null;
        super.onDestroyView();
    }

    private final ObjectAdapter.DataObserver mObserver = new ObjectAdapter.DataObserver() {
        @Override
        public void onChanged() {
            updateControlsBottomSpace(null);
        }
    };

    static abstract class AnimatorListener implements Animator.AnimatorListener {
        ArrayList<View> mViews = new ArrayList<View>();
        ArrayList<Integer> mLayerType = new ArrayList<Integer>();

        @Override
        public void onAnimationCancel(Animator animation) {
        }
        @Override
        public void onAnimationRepeat(Animator animation) {
        }
        @Override
        public void onAnimationStart(Animator animation) {
            getViews(mViews);
            for (View view : mViews) {
                mLayerType.add(view.getLayerType());
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            for (int i = 0; i < mViews.size(); i++) {
                mViews.get(i).setLayerType(mLayerType.get(i), null);
            }
            mLayerType.clear();
            mViews.clear();
        }
        abstract void getViews(ArrayList<View> views);
    };
}

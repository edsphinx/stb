package com.nuevoshorizontes.nhstream.Models;

/**
 * Created by fonseca on 3/18/17.
 */

import android.support.v17.leanback.widget.HeaderItem;

import com.nuevoshorizontes.nhstream.Presenters.CustomHeaderPresenter;

/**
 * Subclass of {@link HeaderItem} to hold icon resource id
 * to show icon on header with {@link CustomHeaderPresenter}
 */
public class CustomHeaderItemModel extends HeaderItem {


    private static final String TAG = CustomHeaderItemModel.class.getSimpleName();
    public static final int ICON_NONE = -1;

    /** Hold an icon resource id */
    private int mIconResId = ICON_NONE;

    public CustomHeaderItemModel(long id, String name, int iconResId) {
        super(id, name);
        mIconResId = iconResId;
    }

    public CustomHeaderItemModel(long id, String name) {
        this(id, name, ICON_NONE);
    }

    public CustomHeaderItemModel(String name) {
        super(name);
    }

    public int getIconResId() {
        return mIconResId;
    }

    public void setIconResId(int iconResId) {
        this.mIconResId = iconResId;
    }
}


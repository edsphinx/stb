package com.nuevoshorizontes.nhstream.MediaPlayers.Live;

import android.support.annotation.RequiresApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.support.v17.leanback.app.BrowseSupportFragment;

class FragmentUtil {

    @RequiresApi(23)
    private static Context getContextNew(Fragment fragment) {
        return fragment.getContext();
    }

    public static Context getContext(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            return getContextNew(fragment);
        } else {
            return fragment.getActivity();
        }
    }

    public static Context getContext(BrowseSupportFragment fragment) {

        return fragment.getActivity();
    }
}
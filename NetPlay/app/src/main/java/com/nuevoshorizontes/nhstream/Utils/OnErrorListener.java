package com.nuevoshorizontes.nhstream.Utils;

/**
 * Created by fonseca on 11/8/17.
 */

public interface OnErrorListener {

    public void onError(final String msg);

    public void onError(final Exception exc);

    public void onNotRoot();
}

package com.nuevoshorizontes.nhstream.Utils;

import android.support.v7.widget.AppCompatTextView;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Typeface;

//import android.content.

/**
 * Created by fonseca on 3/28/17.
 */

public class NHRegularTextView extends AppCompatTextView {

    public NHRegularTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public NHRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public NHRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/RobotoCondensed-Regular.ttf", context);
        //Typeface customFont = FontCache.getTypeface("fonts/testfont.ttf", context);
        //Typeface customFont = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "testfont.ttf");

        setTypeface(customFont);
    }
}

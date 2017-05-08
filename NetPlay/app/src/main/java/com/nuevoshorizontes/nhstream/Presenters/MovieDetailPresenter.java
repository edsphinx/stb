package com.flynetwifi.nhstream.Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flynetwifi.nhstream.Cards.MovieDetailCard;
import com.flynetwifi.netplay.R;
import com.flynetwifi.nhstream.Utils.ResourceCache;

public class MovieDetailPresenter extends Presenter {

    private ResourceCache mResourceCache = new ResourceCache();
    private Context mContext;

    public MovieDetailPresenter(Context context) {
        mContext = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_detail, null);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        TextView primaryText = mResourceCache.getViewById(viewHolder.view, R.id.primary_text);
        TextView sndText1 = mResourceCache.getViewById(viewHolder.view, R.id.secondary_text_first);
        //TextView sndText2 = mResourceCache.getViewById(viewHolder.view, R.id.secondary_text_second);
        TextView extraText = mResourceCache.getViewById(viewHolder.view, R.id.extra_text);

        MovieDetailCard card = (MovieDetailCard) item;
        primaryText.setText(card.getmTitle());
        //sndText1.setText(card.getmDescription());
        sndText1.setText(card.getmYear() + "");
        extraText.setText(card.getmText());
    }

    @Override public void onUnbindViewHolder(ViewHolder viewHolder) {
        // Nothing to do here.
    }
}

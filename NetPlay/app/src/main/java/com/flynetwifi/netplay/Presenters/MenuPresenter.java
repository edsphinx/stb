package com.flynetwifi.netplay.Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flynetwifi.netplay.Cards.MenuCard;
import com.flynetwifi.netplay.R;

public class MenuPresenter extends Presenter {

    private static Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_card, parent, false);

        itemView.setFocusableInTouchMode(true);
        itemView.setFocusable(true);
        itemView.setClickable(true);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final MenuCard menuCard = (MenuCard) item;

        //((ViewHolder) viewHolder).name.setText(menuCard.getmTitulo());
        ((ViewHolder) viewHolder).logo.setImageDrawable(mContext.getResources().getDrawable(menuCard.getLocalImageResourceId(mContext)));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ((ViewHolder) viewHolder).logo.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.md_white_1000)));
//        }

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    static class ViewHolder extends android.support.v17.leanback.widget.Presenter.ViewHolder {

        //public final TextView name;
        public final ImageView logo;

        public ViewHolder(View view) {
            super(view);
            //name = (TextView) view.findViewById(R.id.name);
            logo = (ImageView) view.findViewById(R.id.logo);
        }

    }
}

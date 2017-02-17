package com.flynetwifi.netplay.Presenters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flynetwifi.netplay.Cards.AccountProfileCard;
import com.flynetwifi.netplay.R;

public class AccountProfilesPresenter extends Presenter {

    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_profile_card, parent, false);

        itemView.setFocusableInTouchMode(true);
        itemView.setFocusable(true);
        itemView.setClickable(true);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final AccountProfileCard menuCard = (AccountProfileCard) item;

        ((ViewHolder) viewHolder).name.setText(menuCard.getmTitle());
        ((ViewHolder) viewHolder).logo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_account_box));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((ViewHolder) viewHolder).logo.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.md_white_1000)));
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    static class ViewHolder extends Presenter.ViewHolder {

        public final TextView name;
        public final ImageView logo;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            logo = (ImageView) view.findViewById(R.id.logo);
        }

    }
}

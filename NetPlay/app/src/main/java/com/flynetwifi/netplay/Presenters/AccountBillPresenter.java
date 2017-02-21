package com.flynetwifi.netplay.Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flynetwifi.netplay.Cards.BillsCard;
import com.flynetwifi.netplay.R;

public class AccountBillPresenter extends Presenter {

    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_bill_card, parent, false);

        itemView.setFocusableInTouchMode(true);
        itemView.setFocusable(true);
        itemView.setClickable(true);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        final BillsCard card = (BillsCard) item;

        ((ViewHolder) viewHolder).bill_id.setText(card.getTotal());

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    static class ViewHolder extends Presenter.ViewHolder {

        public final TextView bill_id;

        public ViewHolder(View view) {
            super(view);
            bill_id = (TextView) view.findViewById(R.id.bill_id);
        }

    }
}

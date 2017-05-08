package com.flynetwifi.nhstream.Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flynetwifi.nhstream.Cards.BillsCard;
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

        ((ViewHolder) viewHolder).bill_id.setText(card.getId());
        ((ViewHolder) viewHolder).bill_fecha_emision.setText(card.getFecha_emision());
        ((ViewHolder) viewHolder).bill_fecha_vencimiento.setText(card.getFecha_vencimiento());
        ((ViewHolder) viewHolder).bill_fecha_inicio_periodo.setText(card.getFecha_inicio_periodo());
        ((ViewHolder) viewHolder).bill_fecha_fin_periodo.setText(card.getFecha_fin_periodo());
        ((ViewHolder) viewHolder).bill_impuesto.setText(card.getImpuesto());
        ((ViewHolder) viewHolder).bill_subtotal.setText(card.getSubtotal());
        ((ViewHolder) viewHolder).bill_total.setText(card.getTotal());
        if(card.getEstado() == false){
            ((ViewHolder) viewHolder).bill_estado.setText("Pendiente");
        }
        else{
            ((ViewHolder) viewHolder).bill_estado.setText("Pagada");
        }


    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    static class ViewHolder extends Presenter.ViewHolder {

        public final TextView bill_id;
        public final TextView bill_estado;
        public final TextView bill_fecha_emision;
        public final TextView bill_fecha_vencimiento;
        public final TextView bill_fecha_inicio_periodo;
        public final TextView bill_fecha_fin_periodo;
        public final TextView bill_total;
        public final TextView bill_subtotal;
        public final TextView bill_impuesto;

        public ViewHolder(View view) {
            super(view);
            bill_id = (TextView) view.findViewById(R.id.bill_id);
            bill_estado = (TextView) view.findViewById(R.id.bill_estado);
            bill_fecha_emision = (TextView) view.findViewById(R.id.bill_fecha_emision);
            bill_fecha_vencimiento = (TextView) view.findViewById(R.id.bill_fecha_vencimiento);
            bill_fecha_inicio_periodo = (TextView) view.findViewById(R.id.bill_inicio_periodo);
            bill_fecha_fin_periodo = (TextView) view.findViewById(R.id.bill_fin_periodo);
            bill_subtotal = (TextView) view.findViewById(R.id.bill_subtotal);
            bill_impuesto = (TextView) view.findViewById(R.id.bill_impuesto);
            bill_total = (TextView) view.findViewById(R.id.bill_total);
         }

    }
}

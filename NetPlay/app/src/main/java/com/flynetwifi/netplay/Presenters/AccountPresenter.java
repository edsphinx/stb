package com.flynetwifi.netplay.Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flynetwifi.netplay.Cards.AccountCard;
import com.flynetwifi.netplay.R;
import com.flynetwifi.netplay.Utils.ResourceCache;

public class AccountPresenter extends Presenter {

    private final ResourceCache mResourceCache = new ResourceCache();
    private final Context mContext;

    public AccountPresenter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.account_detail, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        TextView id = mResourceCache.getViewById(viewHolder.view, R.id.id_cuenta);
        TextView nombre = mResourceCache.getViewById(viewHolder.view, R.id.nombre);
        TextView correo = mResourceCache.getViewById(viewHolder.view, R.id.cuenta_correo);
        TextView telefono = mResourceCache.getViewById(viewHolder.view, R.id.cuenta_telefono);
        TextView direccion = mResourceCache.getViewById(viewHolder.view, R.id.cuenta_direccion);
        TextView nacionalidad = mResourceCache.getViewById(viewHolder.view, R.id.cuenta_nacionalidad);
        TextView fecha_instalacion = mResourceCache.getViewById(viewHolder.view, R.id.cuenta_fecha_instalacion);
        TextView inicio_contrato = mResourceCache.getViewById(viewHolder.view, R.id.cuenta_inicio_contrato);
        TextView duracion_contrato = mResourceCache.getViewById(viewHolder.view, R.id.cuenta_duracion);


        AccountCard card = (AccountCard) item;
        nombre.setText(card.getmNombre());
        id.setText(card.getmId());
        correo.setText(card.getmCorreo());
        telefono.setText(card.getmTelefono());
        direccion.setText(card.getmDireccion());
        nacionalidad.setText(card.getmNacionalidad());
        fecha_instalacion.setText(card.getmFechaInstalacion());
        inicio_contrato.setText(card.getmContratoInicio());
        duracion_contrato.setText(card.getmContratoDuracion());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        // Nothing to do here.
    }
}

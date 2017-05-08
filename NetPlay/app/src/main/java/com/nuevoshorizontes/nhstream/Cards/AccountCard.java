package com.flynetwifi.nhstream.Cards;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mauro on 2/8/17.
 */
public class AccountCard  {
    @SerializedName("id") private String mId ;
    @SerializedName("nombre") private String mNombre ;
    @SerializedName("direccion") private String mDireccion ;
    @SerializedName("fecha_instalacion") private String mFechaInstalacion ;
    @SerializedName("telefono") private String mTelefono ;
    @SerializedName("correo") private String mCorreo ;
    @SerializedName("contrato_inicio") private String mContratoInicio ;
    @SerializedName("contrato_duracion") private String mContratoDuracion ;
    @SerializedName("internet") private String mInternet ;
    @SerializedName("iptv") private String mIptv ;
    @SerializedName("nacionalidad") private String mNacionalidad ;

    public String getmId() {
        return mId;
    }

    public String getmNombre() {
        return mNombre;
    }

    public String getmDireccion() {
        return mDireccion;
    }

    public String getmFechaInstalacion() {
        return mFechaInstalacion;
    }

    public String getmTelefono() {
        return mTelefono;
    }

    public String getmCorreo() {
        return mCorreo;
    }

    public String getmContratoInicio() {
        return mContratoInicio;
    }

    public String getmContratoDuracion() {
        return mContratoDuracion;
    }


    public String getmNacionalidad() {
        return mNacionalidad;
    }
}

package com.flynetwifi.netplay.Cards;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mauro on 2/8/17.
 */
public class AccountCard  {
    @SerializedName("id") private String mId = "x";
    @SerializedName("nombre") private String mNombre = "x";
    @SerializedName("direccion") private String mDireccion = "x";
    @SerializedName("fecha_instalacion") private String mFechaInstalacion = "x";
    @SerializedName("telefono") private String mTelefono = "x";
    @SerializedName("correo") private String mCorreo = "x";
    @SerializedName("contrato_inicio") private String mContratoInicio = "x";
    @SerializedName("contrato_duracion") private String mContratoDuracion = "x";
    @SerializedName("internet") private String mInternet = "";
    @SerializedName("iptv") private String mIptv = "";
    @SerializedName("nacionalidad") private String mNacionalidad = "x";

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

    public String getmInternet() {
        return mInternet;
    }

    public String getmIptv() {
        return mIptv;
    }

    public String getmNacionalidad() {
        return mNacionalidad;
    }
}

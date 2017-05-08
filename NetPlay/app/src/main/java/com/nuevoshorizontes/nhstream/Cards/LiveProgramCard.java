package com.nuevoshorizontes.nhstream.Cards;


import com.google.gson.annotations.SerializedName;

public class LiveProgramCard {

    @SerializedName("id") private Integer mId = null;
    @SerializedName("nombre") private String mNombre = "";
    @SerializedName("descripcion") private String mDescripcion = "";
    @SerializedName("fecha") private String mFecha = "";
    @SerializedName("hora_inicio") private String mHora_inicio = "";
    @SerializedName("hora_fin") private String mHora_fin = "";

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public String getmNombre() {
        return mNombre;
    }

    public String getmDescripcion() {
        return mDescripcion;
    }


    public String getmFecha() {
        return mFecha;
    }


    public String getmHora_inicio() {
        return mHora_inicio;
    }


    public String getmHora_fin() {
        return mHora_fin;
    }

}

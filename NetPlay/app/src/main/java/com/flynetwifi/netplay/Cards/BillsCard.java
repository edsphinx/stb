package com.flynetwifi.netplay.Cards;


import com.google.gson.annotations.SerializedName;

public class BillsCard {

    @SerializedName("id") String id;
    @SerializedName("fecha_emision") String fecha_emision;
    @SerializedName("fecha_fin_periodo") String fecha_fin_periodo;
    @SerializedName("estado") Boolean estado;
    @SerializedName("total") String total;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha_emision() {
        return fecha_emision;
    }

    public void setFecha_emision(String fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    public String getFecha_fin_periodo() {
        return fecha_fin_periodo;
    }

    public void setFecha_fin_periodo(String fecha_fin_periodo) {
        this.fecha_fin_periodo = fecha_fin_periodo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

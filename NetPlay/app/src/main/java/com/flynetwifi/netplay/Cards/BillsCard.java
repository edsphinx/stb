package com.flynetwifi.netplay.Cards;


import com.google.gson.annotations.SerializedName;

public class BillsCard {

    @SerializedName("id") String id;
    @SerializedName("fecha_emision") String fecha_emision;
    @SerializedName("fecha_inicio_periodo") String fecha_inicio_periodo;
    @SerializedName("fecha_fin_periodo") String fecha_fin_periodo;
    @SerializedName("fecha_vencimiento") String fecha_vencimiento;
    @SerializedName("estado") Boolean estado;
    @SerializedName("impuesto") String impuesto;
    @SerializedName("subtotal") String subtotal;
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

    public String getFecha_inicio_periodo() {
        return fecha_inicio_periodo;
    }

    public void setFecha_inicio_periodo(String fecha_inicio_periodo) {
        this.fecha_inicio_periodo = fecha_inicio_periodo;
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

    public String getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(String fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }

    public String getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(String impuesto) {
        this.impuesto = impuesto;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}

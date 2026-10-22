package com.universidad.gestorfinanzas.dto;

import com.universidad.gestorfinanzas.modelo.Transaccion;

public class RespuestaTransaccionDto {

    private Transaccion transaccion;
    private boolean esPicoInusual;
    private String mensajeAlertaPico;
    private boolean superaPresupuesto;
    private String mensajeAlertaPresupuesto;
    private boolean variacionServicioBasico;
    private String mensajeVariacionServicioBasico;

    // Campos para la alerta de desviación de meta de ahorro
    private boolean alertaDesviacionMeta;
    private String mensajeDesviacionMeta;
    private Long idMetaAfectada;
    private String nombreMetaAfectada;

    public RespuestaTransaccionDto() {
    }

    public RespuestaTransaccionDto(Transaccion transaccion, boolean esPicoInusual, String mensajeAlertaPico,
                                  boolean superaPresupuesto, String mensajeAlertaPresupuesto,
                                  boolean variacionServicioBasico, String mensajeVariacionServicioBasico) {
        this.transaccion = transaccion;
        this.esPicoInusual = esPicoInusual;
        this.mensajeAlertaPico = mensajeAlertaPico;
        this.superaPresupuesto = superaPresupuesto;
        this.mensajeAlertaPresupuesto = mensajeAlertaPresupuesto;
        this.variacionServicioBasico = variacionServicioBasico;
        this.mensajeVariacionServicioBasico = mensajeVariacionServicioBasico;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public boolean isEsPicoInusual() {
        return esPicoInusual;
    }

    public void setEsPicoInusual(boolean esPicoInusual) {
        this.esPicoInusual = esPicoInusual;
    }

    public String getMensajeAlertaPico() {
        return mensajeAlertaPico;
    }

    public void setMensajeAlertaPico(String mensajeAlertaPico) {
        this.mensajeAlertaPico = mensajeAlertaPico;
    }

    public boolean isSuperaPresupuesto() {
        return superaPresupuesto;
    }

    public void setSuperaPresupuesto(boolean superaPresupuesto) {
        this.superaPresupuesto = superaPresupuesto;
    }

    public String getMensajeAlertaPresupuesto() {
        return mensajeAlertaPresupuesto;
    }

    public void setMensajeAlertaPresupuesto(String mensajeAlertaPresupuesto) {
        this.mensajeAlertaPresupuesto = mensajeAlertaPresupuesto;
    }

    public boolean isVariacionServicioBasico() {
        return variacionServicioBasico;
    }

    public void setVariacionServicioBasico(boolean variacionServicioBasico) {
        this.variacionServicioBasico = variacionServicioBasico;
    }

    public String getMensajeVariacionServicioBasico() {
        return mensajeVariacionServicioBasico;
    }

    public void setMensajeVariacionServicioBasico(String mensajeVariacionServicioBasico) {
        this.mensajeVariacionServicioBasico = mensajeVariacionServicioBasico;
    }

    public boolean isAlertaDesviacionMeta() {
        return alertaDesviacionMeta;
    }

    public void setAlertaDesviacionMeta(boolean alertaDesviacionMeta) {
        this.alertaDesviacionMeta = alertaDesviacionMeta;
    }

    public String getMensajeDesviacionMeta() {
        return mensajeDesviacionMeta;
    }

    public void setMensajeDesviacionMeta(String mensajeDesviacionMeta) {
        this.mensajeDesviacionMeta = mensajeDesviacionMeta;
    }

    public Long getIdMetaAfectada() {
        return idMetaAfectada;
    }

    public void setIdMetaAfectada(Long idMetaAfectada) {
        this.idMetaAfectada = idMetaAfectada;
    }

    public String getNombreMetaAfectada() {
        return nombreMetaAfectada;
    }

    public void setNombreMetaAfectada(String nombreMetaAfectada) {
        this.nombreMetaAfectada = nombreMetaAfectada;
    }
}
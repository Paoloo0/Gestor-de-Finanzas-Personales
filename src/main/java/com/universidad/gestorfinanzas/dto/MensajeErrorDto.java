package com.universidad.gestorfinanzas.dto;

import java.time.LocalDateTime;

public class MensajeErrorDto {

    private LocalDateTime fechaHora;
    private int codigoEstado;
    private String error;
    private String mensaje;
    private String ruta;

    public MensajeErrorDto() {
        this.fechaHora = LocalDateTime.now();
    }

    public MensajeErrorDto(int codigoEstado, String error, String mensaje, String ruta) {
        this.fechaHora = LocalDateTime.now();
        this.codigoEstado = codigoEstado;
        this.error = error;
        this.mensaje = mensaje;
        this.ruta = ruta;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(int codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
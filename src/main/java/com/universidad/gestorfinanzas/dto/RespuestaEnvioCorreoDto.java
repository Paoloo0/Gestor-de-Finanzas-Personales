package com.universidad.gestorfinanzas.dto;

public class RespuestaEnvioCorreoDto {

    private boolean exito;
    private String correoDestino;
    private String asunto;
    private String mensaje;
    private String cuerpoReporte;

    public RespuestaEnvioCorreoDto() {
    }

    public RespuestaEnvioCorreoDto(boolean exito, String correoDestino, String asunto, String mensaje, String cuerpoReporte) {
        this.exito = exito;
        this.correoDestino = correoDestino;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.cuerpoReporte = cuerpoReporte;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getCorreoDestino() {
        return correoDestino;
    }

    public void setCorreoDestino(String correoDestino) {
        this.correoDestino = correoDestino;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCuerpoReporte() {
        return cuerpoReporte;
    }

    public void setCuerpoReporte(String cuerpoReporte) {
        this.cuerpoReporte = cuerpoReporte;
    }
}

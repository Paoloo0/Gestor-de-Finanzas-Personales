package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;

public class SolicitudMetaAhorroDto {

    private Long idUsuario;
    private String nombreMeta;
    private BigDecimal montoNecesario;
    private int plazoMeses; // 1 a 12

    public SolicitudMetaAhorroDto() {
    }

    public SolicitudMetaAhorroDto(Long idUsuario, String nombreMeta, BigDecimal montoNecesario, int plazoMeses) {
        this.idUsuario = idUsuario;
        this.nombreMeta = nombreMeta;
        this.montoNecesario = montoNecesario;
        this.plazoMeses = plazoMeses;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreMeta() {
        return nombreMeta;
    }

    public void setNombreMeta(String nombreMeta) {
        this.nombreMeta = nombreMeta;
    }

    public BigDecimal getMontoNecesario() {
        return montoNecesario;
    }

    public void setMontoNecesario(BigDecimal montoNecesario) {
        this.montoNecesario = montoNecesario;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }
}
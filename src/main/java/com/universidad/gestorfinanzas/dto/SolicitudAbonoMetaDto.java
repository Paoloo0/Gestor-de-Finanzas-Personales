package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la solicitud de abonos o depósitos destinados a una meta de ahorro específica.
 */
public class SolicitudAbonoMetaDto {

    private Long idUsuario;
    private BigDecimal montoAbono;
    private LocalDate fecha;
    private String descripcion;

    public SolicitudAbonoMetaDto() {
    }

    public SolicitudAbonoMetaDto(Long idUsuario, BigDecimal montoAbono, LocalDate fecha, String descripcion) {
        this.idUsuario = idUsuario;
        this.montoAbono = montoAbono;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.descripcion = descripcion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getMontoAbono() {
        return montoAbono;
    }

    public void setMontoAbono(BigDecimal montoAbono) {
        this.montoAbono = montoAbono;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

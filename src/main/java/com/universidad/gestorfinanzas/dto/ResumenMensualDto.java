package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;
import java.util.List;

public class ResumenMensualDto {

    private Long idUsuario;
    private String nombreUsuario;
    private int mes;
    private int anio;
    private BigDecimal ingresoMensualBase;
    private BigDecimal totalGastado;
    private BigDecimal saldoDisponible;
    private List<EstadoCategoriaDto> desgloseCategorias;

    public ResumenMensualDto() {
    }

    public ResumenMensualDto(Long idUsuario, String nombreUsuario, int mes, int anio,
                             BigDecimal ingresoMensualBase, BigDecimal totalGastado,
                             BigDecimal saldoDisponible, List<EstadoCategoriaDto> desgloseCategorias) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.mes = mes;
        this.anio = anio;
        this.ingresoMensualBase = ingresoMensualBase;
        this.totalGastado = totalGastado;
        this.saldoDisponible = saldoDisponible;
        this.desgloseCategorias = desgloseCategorias;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public BigDecimal getIngresoMensualBase() {
        return ingresoMensualBase;
    }

    public void setIngresoMensualBase(BigDecimal ingresoMensualBase) {
        this.ingresoMensualBase = ingresoMensualBase;
    }

    public BigDecimal getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(BigDecimal totalGastado) {
        this.totalGastado = totalGastado;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public List<EstadoCategoriaDto> getDesgloseCategorias() {
        return desgloseCategorias;
    }

    public void setDesgloseCategorias(List<EstadoCategoriaDto> desgloseCategorias) {
        this.desgloseCategorias = desgloseCategorias;
    }
}
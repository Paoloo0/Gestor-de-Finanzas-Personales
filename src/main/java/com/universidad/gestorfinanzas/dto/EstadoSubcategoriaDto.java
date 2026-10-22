package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;

public class EstadoSubcategoriaDto {

    private Long idSubcategoria;
    private String nombreSubcategoria;
    private BigDecimal gastoAcumulado;

    public EstadoSubcategoriaDto() {
    }

    public EstadoSubcategoriaDto(Long idSubcategoria, String nombreSubcategoria, BigDecimal gastoAcumulado) {
        this.idSubcategoria = idSubcategoria;
        this.nombreSubcategoria = nombreSubcategoria;
        this.gastoAcumulado = gastoAcumulado;
    }

    public Long getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(Long idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }

    public String getNombreSubcategoria() {
        return nombreSubcategoria;
    }

    public void setNombreSubcategoria(String nombreSubcategoria) {
        this.nombreSubcategoria = nombreSubcategoria;
    }

    public BigDecimal getGastoAcumulado() {
        return gastoAcumulado;
    }

    public void setGastoAcumulado(BigDecimal gastoAcumulado) {
        this.gastoAcumulado = gastoAcumulado;
    }
}
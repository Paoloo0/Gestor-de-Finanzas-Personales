package com.universidad.gestorfinanzas.dto;

import java.math.BigDecimal;
import java.util.List;

public class EstadoCategoriaDto {

    private Long idCategoria;
    private String nombreCategoria;
    private BigDecimal porcentajeMeta;
    private BigDecimal montoMaximoPermitido;
    private BigDecimal gastoAcumulado;
    private BigDecimal saldoRestante;
    private BigDecimal porcentajeConsumido;
    private boolean superaPresupuesto;
    private List<EstadoSubcategoriaDto> subcategorias;

    public EstadoCategoriaDto() {
    }

    public EstadoCategoriaDto(Long idCategoria, String nombreCategoria, BigDecimal porcentajeMeta,
                              BigDecimal montoMaximoPermitido, BigDecimal gastoAcumulado,
                              BigDecimal saldoRestante, BigDecimal porcentajeConsumido,
                              boolean superaPresupuesto, List<EstadoSubcategoriaDto> subcategorias) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.porcentajeMeta = porcentajeMeta;
        this.montoMaximoPermitido = montoMaximoPermitido;
        this.gastoAcumulado = gastoAcumulado;
        this.saldoRestante = saldoRestante;
        this.porcentajeConsumido = porcentajeConsumido;
        this.superaPresupuesto = superaPresupuesto;
        this.subcategorias = subcategorias;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public BigDecimal getPorcentajeMeta() {
        return porcentajeMeta;
    }

    public void setPorcentajeMeta(BigDecimal porcentajeMeta) {
        this.porcentajeMeta = porcentajeMeta;
    }

    public BigDecimal getMontoMaximoPermitido() {
        return montoMaximoPermitido;
    }

    public void setMontoMaximoPermitido(BigDecimal montoMaximoPermitido) {
        this.montoMaximoPermitido = montoMaximoPermitido;
    }

    public BigDecimal getGastoAcumulado() {
        return gastoAcumulado;
    }

    public void setGastoAcumulado(BigDecimal gastoAcumulado) {
        this.gastoAcumulado = gastoAcumulado;
    }

    public BigDecimal getSaldoRestante() {
        return saldoRestante;
    }

    public void setSaldoRestante(BigDecimal saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

    public BigDecimal getPorcentajeConsumido() {
        return porcentajeConsumido;
    }

    public void setPorcentajeConsumido(BigDecimal porcentajeConsumido) {
        this.porcentajeConsumido = porcentajeConsumido;
    }

    public boolean isSuperaPresupuesto() {
        return superaPresupuesto;
    }

    public void setSuperaPresupuesto(boolean superaPresupuesto) {
        this.superaPresupuesto = superaPresupuesto;
    }

    public List<EstadoSubcategoriaDto> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<EstadoSubcategoriaDto> subcategorias) {
        this.subcategorias = subcategorias;
    }
}
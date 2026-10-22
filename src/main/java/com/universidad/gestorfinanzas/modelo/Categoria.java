package com.universidad.gestorfinanzas.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un bloque de la regla presupuestaria 70-30-20
 * (70% Necesidades, 30% Ocio y gustos, 20% Ahorro e Inversión).
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "porcentaje_meta", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeMeta;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Subcategoria> subcategorias = new ArrayList<>();

    public Categoria() {
    }

    public Categoria(String nombre, String descripcion, BigDecimal porcentajeMeta) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.porcentajeMeta = porcentajeMeta;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcentajeMeta() {
        return porcentajeMeta;
    }

    public void setPorcentajeMeta(BigDecimal porcentajeMeta) {
        this.porcentajeMeta = porcentajeMeta;
    }

    public List<Subcategoria> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<Subcategoria> subcategorias) {
        this.subcategorias = subcategorias;
    }
}
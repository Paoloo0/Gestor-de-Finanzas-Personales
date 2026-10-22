package com.universidad.gestorfinanzas.repositorio;

import com.universidad.gestorfinanzas.modelo.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransaccionRepositorio extends JpaRepository<Transaccion, Long> {

    // Transacciones del usuario en un rango de fechas (ej: un mes)
    List<Transaccion> findByUsuarioIdUsuarioAndFechaBetween(Long idUsuario, LocalDate fechaInicio, LocalDate fechaFin);

    // Historial de gastos por subcategoría (para cálculo del promedio histórico)
    List<Transaccion> findByUsuarioIdUsuarioAndSubcategoriaIdSubcategoriaAndTipo(Long idUsuario, Long idSubcategoria, String tipo);

    // Listado cronológico inverso para el frontend
    List<Transaccion> findByUsuarioIdUsuarioOrderByFechaDesc(Long idUsuario);

    // Buscar transacciones por nombre de subcategoría en un período (para comparar Servicios Básicos con mes anterior)
    List<Transaccion> findByUsuarioIdUsuarioAndSubcategoriaNombreIgnoreCaseAndTipoAndFechaBetween(
            Long idUsuario, String nombreSubcategoria, String tipo, LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar transacciones asociadas a una subcategoría específica
    List<Transaccion> findBySubcategoriaIdSubcategoria(Long idSubcategoria);
}
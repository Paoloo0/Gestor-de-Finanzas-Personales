package com.universidad.gestorfinanzas.repositorio;

import com.universidad.gestorfinanzas.modelo.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoriaRepositorio extends JpaRepository<Subcategoria, Long> {
    List<Subcategoria> findByCategoriaIdCategoria(Long idCategoria);
    Optional<Subcategoria> findByNombreIgnoreCase(String nombre);
}
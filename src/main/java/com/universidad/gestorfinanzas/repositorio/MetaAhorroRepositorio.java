package com.universidad.gestorfinanzas.repositorio;

import com.universidad.gestorfinanzas.modelo.MetaAhorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetaAhorroRepositorio extends JpaRepository<MetaAhorro, Long> {
    List<MetaAhorro> findByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);
    Optional<MetaAhorro> findFirstByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);
}
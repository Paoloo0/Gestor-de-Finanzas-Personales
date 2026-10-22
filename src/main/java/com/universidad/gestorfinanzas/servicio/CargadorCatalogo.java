package com.universidad.gestorfinanzas.servicio;

import com.universidad.gestorfinanzas.modelo.Categoria;
import com.universidad.gestorfinanzas.modelo.Subcategoria;
import com.universidad.gestorfinanzas.modelo.Transaccion;
import com.universidad.gestorfinanzas.repositorio.CategoriaRepositorio;
import com.universidad.gestorfinanzas.repositorio.SubcategoriaRepositorio;
import com.universidad.gestorfinanzas.repositorio.TransaccionRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Sincroniza y mantiene actualizado el catálogo maestro de categorías y subcategorías
 * bajo la Regla 70-30-20.
 * Inicia la aplicación limpia, sin datos precargados de usuarios ni transacciones de prueba.
 */
@Component
public class CargadorCatalogo implements CommandLineRunner {

    private final CategoriaRepositorio categoriaRepositorio;
    private final SubcategoriaRepositorio subcategoriaRepositorio;
    private final TransaccionRepositorio transaccionRepositorio;

    public CargadorCatalogo(CategoriaRepositorio categoriaRepositorio,
                            SubcategoriaRepositorio subcategoriaRepositorio,
                            TransaccionRepositorio transaccionRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
        this.subcategoriaRepositorio = subcategoriaRepositorio;
        this.transaccionRepositorio = transaccionRepositorio;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Bloque 70% Necesidades
        Categoria necesidades = obtenerOCrearCategoria("Necesidades", "Gastos fijos indispensables", new BigDecimal("70.00"));
        sincronizarSubcategoria("Vivienda", "Alquiler, Mantenimiento", necesidades);
        sincronizarSubcategoriaConAlias("Servicios básicos", "Servicios Básicos", "Luz, Agua, Gas, Teléfono, Internet", necesidades);
        sincronizarSubcategoria("Alimentación", "Viáticos", necesidades);
        sincronizarSubcategoria("Salud", "Medicamentos, Seguros médicos", necesidades);
        sincronizarSubcategoria("Educación", "Mensualidad de estudio", necesidades);
        sincronizarSubcategoria("Transporte", "Pasajes de transporte, Combustible", necesidades);
        sincronizarSubcategoria("Préstamos", "Deuda", necesidades);

        // 2. Bloque 30% Ocio y gustos
        Categoria ocio = obtenerOCrearCategoria("Ocio y gustos", "Gastos prescindibles y recreación", new BigDecimal("30.00"));
        sincronizarSubcategoria("Entretenimiento", "Restaurantes, bares, cine, conciertos y reuniones con amigos", ocio);
        sincronizarSubcategoria("Compras personales", "Ropa, calzado, tecnología y accesorios", ocio);
        sincronizarSubcategoriaConAlias("Suscripciones de entretenimiento", "Suscripciones", "Plataformas de streaming", ocio);
        sincronizarSubcategoriaConAlias("Viajes de placer", "Viajes", "Vacaciones y viajes", ocio);

        // 3. Bloque 20% Ahorro e Inversión
        Categoria ahorro = obtenerOCrearCategoria("Ahorro e Inversión", "Ahorro y crecimiento patrimonial", new BigDecimal("20.00"));
        Subcategoria fondoEmergencia = sincronizarSubcategoria("Fondo de emergencia", "Dinero apartado para emergencias", ahorro);
        sincronizarSubcategoria("Inversiones", "Aportes a fondos mutuos, depósitos a plazo fijo", ahorro);

        // Desvincular 'Ahorro para metas' del catálogo de subcategorías (ahora es módulo independiente)
        desvincularAhorroParaMetas(fondoEmergencia);

        System.out.println(">>> [CATÁLOGO 70-30-20]: Catálogo jerárquico sincronizado y actualizado exitosamente.");
    }

    private Categoria obtenerOCrearCategoria(String nombre, String descripcion, BigDecimal porcentajeMeta) {
        List<Categoria> categorias = categoriaRepositorio.findAll();
        for (Categoria c : categorias) {
            if (c.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
                c.setPorcentajeMeta(porcentajeMeta);
                return categoriaRepositorio.save(c);
            }
        }
        Categoria nueva = new Categoria(nombre, descripcion, porcentajeMeta);
        return categoriaRepositorio.save(nueva);
    }

    private Subcategoria sincronizarSubcategoria(String nombre, String descripcion, Categoria categoria) {
        List<Subcategoria> subcats = subcategoriaRepositorio.findByCategoriaIdCategoria(categoria.getIdCategoria());
        for (Subcategoria s : subcats) {
            if (s.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
                s.setDescripcion(descripcion);
                s.setCategoria(categoria);
                return subcategoriaRepositorio.save(s);
            }
        }
        Subcategoria nueva = new Subcategoria(nombre, descripcion, categoria);
        return subcategoriaRepositorio.save(nueva);
    }

    private void sincronizarSubcategoriaConAlias(String nombreOficial, String aliasAnterior, String descripcion, Categoria categoria) {
        List<Subcategoria> subcats = subcategoriaRepositorio.findByCategoriaIdCategoria(categoria.getIdCategoria());
        for (Subcategoria s : subcats) {
            if (s.getNombre().trim().equalsIgnoreCase(nombreOficial) || s.getNombre().trim().equalsIgnoreCase(aliasAnterior)) {
                s.setNombre(nombreOficial);
                s.setDescripcion(descripcion);
                s.setCategoria(categoria);
                subcategoriaRepositorio.save(s);
                return;
            }
        }
        subcategoriaRepositorio.save(new Subcategoria(nombreOficial, descripcion, categoria));
    }

    private void desvincularAhorroParaMetas(Subcategoria subcategoriaDestino) {
        List<Subcategoria> todas = subcategoriaRepositorio.findAll();
        for (Subcategoria s : todas) {
            if ("Ahorro para metas".equalsIgnoreCase(s.getNombre().trim())) {
                List<Transaccion> txs = transaccionRepositorio.findBySubcategoriaIdSubcategoria(s.getIdSubcategoria());
                if (txs != null && !txs.isEmpty() && subcategoriaDestino != null) {
                    for (Transaccion t : txs) {
                        t.setSubcategoria(subcategoriaDestino);
                        transaccionRepositorio.save(t);
                    }
                }
                subcategoriaRepositorio.delete(s);
            }
        }
    }
}
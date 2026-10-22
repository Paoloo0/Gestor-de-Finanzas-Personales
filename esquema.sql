-- ====================================================================
-- Script SQL: Gestor de Finanzas Personales con Subcategorías (Regla 70-30-20)
-- Motor: PostgreSQL
-- Proyecto Académico: Ingeniería de Software (5to Ciclo)
-- ====================================================================

-- 1. Limpieza de tablas (en orden inverso de dependencias)
DROP TABLE IF EXISTS metas_ahorro;
DROP TABLE IF EXISTS transacciones;
DROP TABLE IF EXISTS subcategorias;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS usuarios;

-- 2. Tabla de Usuarios
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    ingreso_mensual NUMERIC(12, 2) NOT NULL CHECK (ingreso_mensual >= 0)
);

-- 3. Tabla de Categorías Principales (Regla 70-30-20)
CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    porcentaje_meta NUMERIC(5, 2) NOT NULL CHECK (porcentaje_meta > 0 AND porcentaje_meta <= 100)
);

-- 4. Tabla de Subcategorías en Cascada
CREATE TABLE subcategorias (
    id_subcategoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    id_categoria INT NOT NULL,
    CONSTRAINT fk_subcategoria_categoria FOREIGN KEY (id_categoria) 
        REFERENCES categorias(id_categoria) ON DELETE CASCADE
);

-- 5. Tabla de Transacciones
CREATE TABLE transacciones (
    id_transaccion SERIAL PRIMARY KEY,
    monto NUMERIC(12, 2) NOT NULL CHECK (monto > 0),
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('GASTO', 'INGRESO')),
    descripcion VARCHAR(255) NOT NULL,
    id_subcategoria INT NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_transaccion_subcategoria FOREIGN KEY (id_subcategoria) 
        REFERENCES subcategorias(id_subcategoria) ON DELETE RESTRICT,
    CONSTRAINT fk_transaccion_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- 6. Tabla de Metas de Ahorro
CREATE TABLE metas_ahorro (
    id_meta SERIAL PRIMARY KEY,
    nombre_meta VARCHAR(100) NOT NULL,
    monto_necesario NUMERIC(12, 2) NOT NULL CHECK (monto_necesario > 0),
    plazo_meses INT NOT NULL CHECK (plazo_meses BETWEEN 1 AND 12),
    cuota_mensual NUMERIC(12, 2) NOT NULL,
    monto_acumulado NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_meta_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- ====================================================================
-- Inserción ÚNICAMENTE del Catálogo de Categorías y Subcategorías
-- (Sin datos de usuarios ni transacciones precargadas: base limpia)
-- ====================================================================

-- 1. Categorías fijas de la regla 70-30-20
INSERT INTO categorias (id_categoria, nombre, descripcion, porcentaje_meta) VALUES
(1, 'Necesidades', 'Gastos esenciales indispensables para vivir y trabajar', 70.00),
(2, 'Ocio y gustos', 'Gastos prescindibles para estilo de vida y disfrute personal', 30.00),
(3, 'Ahorro e Inversión', 'Recursos orientados a seguridad futura y crecimiento patrimonial', 20.00);

-- 2. Subcategorías para 70% — Necesidades
INSERT INTO subcategorias (nombre, descripcion, id_categoria) VALUES
('Vivienda', 'Alquiler, hipoteca, mantenimiento del hogar', 1),
('Servicios básicos', 'Luz, agua, gas, internet y telefonía fija', 1),
('Alimentación', 'Supermercado, abarrotes, mercados locales', 1),
('Salud', 'Medicamentos, citas médicas, seguros de salud', 1),
('Educación', 'Pensiones universitarias, libros, cursos y materiales', 1),
('Transporte', 'Pasajes, combustible, mantenimiento vehicular', 1),
('Préstamos', 'Cuotas de deudas bancarias o créditos financieros', 1);

-- 3. Subcategorías para 30% — Ocio y gustos
INSERT INTO subcategorias (nombre, descripcion, id_categoria) VALUES
('Entretenimiento', 'Cine, salidas de fin de semana, eventos y conciertos', 2),
('Compras personales', 'Ropa, tecnología, accesorios y regalos', 2),
('Suscripciones de entretenimiento', 'Streaming (Netflix, Spotify), apps y servicios digitales', 2),
('Viajes de placer', 'Paseos, boletos de avión, hospedajes y turismo', 2);

-- 4. Subcategorías para 20% — Ahorro e Inversión
INSERT INTO subcategorias (nombre, descripcion, id_categoria) VALUES
('Fondo de emergencia', 'Respaldo para imprevistos (3 a 6 meses de gastos)', 3),
('Inversiones', 'Depósitos a plazo, fondos mutuos o acciones', 3);
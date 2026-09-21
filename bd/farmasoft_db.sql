/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS `farmasoft_db`;
CREATE DATABASE IF NOT EXISTS `farmasoft_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `farmasoft_db`;

CREATE TABLE IF NOT EXISTS `categorias` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `categorias` (`id_categoria`, `nombre`, `descripcion`) VALUES
	(1, 'Analgésicos y Antiinflamatorios', 'Medicamentos para aliviar el dolor y reducir la inflamación'),
	(2, 'Antibióticos', 'Fármacos de prescripción para combatir infecciones bacterianas'),
	(3, 'Antialérgicos y Antihistamínicos', 'Tratamiento de alergias y congestión'),
	(4, 'Cuidado Personal e Higiene', 'Productos para el aseo diario, jabones y cuidado corporal'),
	(5, 'Cuidado Infantil y Bebés', 'Leches de fórmula, pañales y cremas para bebés'),
	(6, 'Dermocosmética', 'Cuidado avanzado de la piel y protección solar'),
	(7, 'Dispositivos Médicos y Primeros Auxilios', 'Termómetros, jeringas, curas, gasas y tensiómetros'),
	(8, 'prueba', 'peiaadas');

CREATE TABLE IF NOT EXISTS `clientes` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_documento` enum('CC','CE','PASAPORTE','TI','NIT') COLLATE utf8mb4_unicode_ci DEFAULT 'CC',
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `eps` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alergias` text COLLATE utf8mb4_unicode_ci,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `numero_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `clientes` (`id_cliente`, `nombre_completo`, `tipo_documento`, `numero_documento`, `telefono`, `direccion`, `correo`, `fecha_nacimiento`, `eps`, `alergias`, `fecha_registro`) VALUES
	(1, 'Carlos Alberto Gómez', 'CC', '1114829301', '3157894512', 'Calle 25 # 14-30', 'carlos.gomez@gmail.com', '1988-04-12', 'Sura', 'Penicilina', '2026-09-12 17:51:21'),
	(2, 'María Fernanda Rojas', 'CC', '1085293841', '3124567890', 'Carrera 10 # 5-12', 'mafe.rojas@hotmail.com', '1995-08-23', 'Sanitas', 'Ninguna', '2026-09-12 17:51:21'),
	(3, 'Jorge Enrique Linares', 'CC', '14239841', '3189012345', 'Av. Pasoancho # 56-11', 'jorge.linares@yahoo.com', '1975-11-02', 'Nueva EPS', 'Aspirina, Ibuprofeno', '2026-09-12 17:51:21'),
	(4, 'Diana Marcela Ruiz', 'CC', '1113840291', '3001234567', 'Calle 5 # 38-20', 'diana.ruiz@gmail.com', '2001-02-15', 'Salud Total', 'Sulfa', '2026-09-12 17:51:21'),
	(5, 'Andrés Felipe Valencia', 'CC', '1098231456', '3168901234', 'Cra 8 # 12-45', 'andres.valencia@outlook.com', '1992-06-30', 'Compensar', 'Ninguna', '2026-09-12 17:51:21');

CREATE TABLE IF NOT EXISTS `compras` (
  `id_compra` int NOT NULL AUTO_INCREMENT,
  `id_sucursal` int NOT NULL,
  `numero_factura_proveedor` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_proveedor` int NOT NULL,
  `id_empleado` int NOT NULL,
  `fecha_compra` datetime DEFAULT CURRENT_TIMESTAMP,
  `subtotal` decimal(12,2) NOT NULL,
  `impuestos` decimal(12,2) NOT NULL DEFAULT '0.00',
  `total` decimal(12,2) NOT NULL,
  `forma_pago` enum('EFECTIVO','TRANSFERENCIA','CREDITO_PROVEEDOR') COLLATE utf8mb4_unicode_ci DEFAULT 'CREDITO_PROVEEDOR',
  `estado` enum('PENDIENTE','RECIBIDA','PARCIALMENTE_RECIBIDA','CANCELADA','DEVUELTA') COLLATE utf8mb4_unicode_ci DEFAULT 'RECIBIDA',
  PRIMARY KEY (`id_compra`),
  KEY `id_sucursal` (`id_sucursal`),
  KEY `id_proveedor` (`id_proveedor`),
  KEY `id_empleado` (`id_empleado`),
  CONSTRAINT `compras_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`),
  CONSTRAINT `compras_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedores` (`id_proveedor`),
  CONSTRAINT `compras_ibfk_3` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `compras` (`id_compra`, `id_sucursal`, `numero_factura_proveedor`, `id_proveedor`, `id_empleado`, `fecha_compra`, `subtotal`, `impuestos`, `total`, `forma_pago`, `estado`) VALUES
	(1, 1, 'FAC-TQ-90812', 3, 1, '2026-07-01 10:30:00', 1625000.00, 308750.00, 1933750.00, 'CREDITO_PROVEEDOR', 'RECIBIDA'),
	(2, 1, 'FAC-GEN-11204', 2, 2, '2026-07-15 14:15:00', 960000.00, 182400.00, 1142400.00, 'TRANSFERENCIA', 'RECIBIDA'),
	(3, 1, 'PED-20260913112851', 1, 1, '2026-09-13 11:30:07', 3000.00, 570.00, 3570.00, 'EFECTIVO', 'PENDIENTE'),
	(4, 1, 'PED-20260913114513', 1, 1, '2026-09-13 11:45:46', 10.00, 1.90, 11.90, 'CREDITO_PROVEEDOR', 'PENDIENTE'),
	(5, 1, 'PED-20260913122734', 1, 2, '2026-09-13 12:28:03', 548000.00, 104120.00, 652120.00, 'EFECTIVO', 'PENDIENTE');

CREATE TABLE IF NOT EXISTS `configuracion_global` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_farmacia` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nit` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `iva_general` decimal(5,2) NOT NULL DEFAULT '19.00',
  `moneda` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT 'COP',
  `tiempo_alerta_vencimiento_dias` int DEFAULT '60',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `configuracion_global` (`id`, `nombre_farmacia`, `nit`, `telefono`, `direccion`, `iva_general`, `moneda`, `tiempo_alerta_vencimiento_dias`) VALUES
	(1, 'FarmaSoft Plus S.A.S.', '900123456-7', '6022345678', 'Calle Principal # 10-20', 19.00, 'COP', 60);

CREATE TABLE IF NOT EXISTS `detalles_compras` (
  `id_detalle_compra` int NOT NULL AUTO_INCREMENT,
  `id_compra` int NOT NULL,
  `id_producto` int NOT NULL,
  `tipo_presentacion` enum('CAJA','SELLO','UNIDAD') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CAJA',
  `factor_conversion` int NOT NULL DEFAULT '1',
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_compra`),
  KEY `id_compra` (`id_compra`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `detalles_compras_ibfk_1` FOREIGN KEY (`id_compra`) REFERENCES `compras` (`id_compra`) ON DELETE CASCADE,
  CONSTRAINT `detalles_compras_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `detalles_compras` (`id_detalle_compra`, `id_compra`, `id_producto`, `tipo_presentacion`, `factor_conversion`, `cantidad`, `precio_unitario`, `subtotal`) VALUES
	(1, 1, 1, 'CAJA', 1, 150, 4500.00, 675000.00),
	(2, 1, 5, 'CAJA', 1, 25, 38000.00, 950000.00),
	(3, 2, 2, 'CAJA', 1, 80, 12000.00, 960000.00),
	(4, 3, 4, 'CAJA', 1, 1, 3000.00, 3000.00),
	(5, 4, 15, 'CAJA', 1, 1, 10.00, 10.00),
	(6, 5, 4, 'CAJA', 10, 1, 28000.00, 28000.00),
	(7, 5, 6, 'CAJA', 5, 1, 520000.00, 520000.00);

CREATE TABLE IF NOT EXISTS `detalles_formulas` (
  `id_detalle_formula` int NOT NULL AUTO_INCREMENT,
  `id_formula` int NOT NULL,
  `id_producto` int NOT NULL,
  `dosis` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `frecuencia` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `duracion_tratamiento` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_detalle_formula`),
  KEY `id_formula` (`id_formula`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `detalles_formulas_ibfk_1` FOREIGN KEY (`id_formula`) REFERENCES `formulas_medicas` (`id_formula`) ON DELETE CASCADE,
  CONSTRAINT `detalles_formulas_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `detalles_formulas` (`id_detalle_formula`, `id_formula`, `id_producto`, `dosis`, `frecuencia`, `duracion_tratamiento`) VALUES
	(1, 1, 2, '1 Cápsula 500mg', 'Cada 8 horas', '7 días');

CREATE TABLE IF NOT EXISTS `detalles_ventas` (
  `id_detalle_venta` int NOT NULL AUTO_INCREMENT,
  `id_venta` int NOT NULL,
  `id_producto` int NOT NULL,
  `id_formula` int DEFAULT NULL,
  `id_lote` int NOT NULL,
  `tipo_venta` enum('UNIDAD','SELLO','CAJA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNIDAD',
  `cantidad` int NOT NULL,
  `unidades_descontadas` int NOT NULL DEFAULT '1',
  `precio_unitario` decimal(12,2) NOT NULL,
  `porcentaje_iva` decimal(5,2) NOT NULL DEFAULT '0.00',
  `subtotal` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_venta`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`),
  KEY `id_lote` (`id_lote`),
  KEY `fk_detalle_venta_formula` (`id_formula`),
  CONSTRAINT `detalles_ventas_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`) ON DELETE CASCADE,
  CONSTRAINT `detalles_ventas_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `detalles_ventas_ibfk_3` FOREIGN KEY (`id_lote`) REFERENCES `lotes` (`id_lote`),
  CONSTRAINT `fk_detalle_venta_formula` FOREIGN KEY (`id_formula`) REFERENCES `formulas_medicas` (`id_formula`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `detalles_ventas` (`id_detalle_venta`, `id_venta`, `id_producto`, `id_formula`, `id_lote`, `tipo_venta`, `cantidad`, `unidades_descontadas`, `precio_unitario`, `porcentaje_iva`, `subtotal`) VALUES
	(1, 1, 1, NULL, 1, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(2, 1, 3, NULL, 4, 'UNIDAD', 1, 1, 26000.00, 19.00, 26000.00),
	(3, 2, 2, NULL, 3, 'UNIDAD', 1, 1, 22000.00, 19.00, 22000.00),
	(4, 3, 1, NULL, 1, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(5, 4, 1, NULL, 1, 'SELLO', 1, 12, 102000.00, 19.00, 102000.00),
	(6, 4, 2, NULL, 3, 'UNIDAD', 1, 1, 22000.00, 19.00, 22000.00),
	(7, 4, 4, NULL, 5, 'SELLO', 4, 40, 70000.00, 19.00, 280000.00),
	(8, 5, 1, NULL, 1, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(9, 6, 1, NULL, 13, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(10, 7, 1, NULL, 11, 'SELLO', 2, 24, 102000.00, 19.00, 204000.00),
	(11, 8, 1, NULL, 11, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(12, 9, 1, NULL, 11, 'SELLO', 1, 12, 102000.00, 19.00, 102000.00),
	(13, 10, 1, NULL, 11, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(14, 11, 1, NULL, 11, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(15, 12, 1, NULL, 8, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(16, 13, 3, NULL, 12, 'UNIDAD', 1, 1, 26000.00, 19.00, 26000.00),
	(17, 14, 1, NULL, 8, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(18, 15, 1, NULL, 8, 'UNIDAD', 1, 1, 8500.00, 19.00, 8500.00),
	(19, 16, 17, NULL, 27, 'UNIDAD', 1, 1, 1190.00, 19.00, 1190.00),
	(20, 17, 2, NULL, 3, 'UNIDAD', 2, 2, 26180.00, 19.00, 52360.00),
	(21, 18, 3, NULL, 4, 'UNIDAD', 3, 3, 30940.00, 19.00, 92820.00),
	(22, 19, 4, NULL, 5, 'UNIDAD', 1, 1, 8330.00, 19.00, 8330.00),
	(23, 20, 5, NULL, 6, 'UNIDAD', 2, 2, 73780.00, 19.00, 147560.00),
	(24, 21, 6, NULL, 7, 'UNIDAD', 1, 1, 196350.00, 19.00, 196350.00),
	(25, 22, 16, NULL, 26, 'UNIDAD', 1, 1, 1190.00, 0.00, 1190.00),
	(26, 23, 17, NULL, 27, 'UNIDAD', 2, 2, 1190.00, 19.00, 2380.00),
	(27, 24, 1, NULL, 1, 'UNIDAD', 3, 3, 10115.00, 19.00, 30345.00),
	(28, 25, 2, NULL, 3, 'UNIDAD', 1, 1, 26180.00, 19.00, 26180.00),
	(29, 26, 3, NULL, 4, 'UNIDAD', 2, 2, 30940.00, 19.00, 61880.00),
	(30, 27, 4, NULL, 5, 'UNIDAD', 3, 3, 8330.00, 19.00, 24990.00),
	(31, 28, 5, NULL, 6, 'UNIDAD', 1, 1, 73780.00, 19.00, 73780.00),
	(32, 29, 6, NULL, 7, 'UNIDAD', 1, 1, 196350.00, 19.00, 196350.00),
	(33, 30, 16, NULL, 26, 'UNIDAD', 3, 3, 1190.00, 0.00, 3570.00),
	(34, 31, 17, NULL, 27, 'UNIDAD', 1, 1, 1190.00, 19.00, 1190.00),
	(35, 32, 1, NULL, 1, 'UNIDAD', 2, 2, 10115.00, 19.00, 20230.00),
	(36, 33, 2, NULL, 3, 'UNIDAD', 3, 3, 26180.00, 19.00, 78540.00),
	(37, 34, 3, NULL, 4, 'UNIDAD', 1, 1, 30940.00, 19.00, 30940.00),
	(38, 35, 4, NULL, 5, 'UNIDAD', 2, 2, 8330.00, 19.00, 16660.00),
	(39, 36, 5, NULL, 6, 'UNIDAD', 3, 3, 73780.00, 19.00, 221340.00),
	(40, 37, 6, NULL, 7, 'UNIDAD', 1, 1, 196350.00, 19.00, 196350.00),
	(41, 38, 16, NULL, 26, 'UNIDAD', 2, 2, 1190.00, 0.00, 2380.00),
	(42, 39, 17, NULL, 27, 'UNIDAD', 3, 3, 1190.00, 19.00, 3570.00),
	(43, 40, 1, NULL, 1, 'UNIDAD', 1, 1, 10115.00, 19.00, 10115.00),
	(44, 41, 2, NULL, 3, 'UNIDAD', 2, 2, 26180.00, 19.00, 52360.00),
	(45, 42, 3, NULL, 4, 'UNIDAD', 3, 3, 30940.00, 19.00, 92820.00),
	(46, 43, 4, NULL, 5, 'UNIDAD', 1, 1, 8330.00, 19.00, 8330.00),
	(47, 44, 5, NULL, 6, 'UNIDAD', 2, 2, 73780.00, 19.00, 147560.00),
	(48, 45, 6, NULL, 7, 'UNIDAD', 1, 1, 196350.00, 19.00, 196350.00),
	(49, 46, 16, NULL, 26, 'UNIDAD', 1, 1, 1190.00, 0.00, 1190.00),
	(50, 47, 17, NULL, 27, 'UNIDAD', 2, 2, 1190.00, 19.00, 2380.00),
	(51, 48, 1, NULL, 1, 'UNIDAD', 3, 3, 10115.00, 19.00, 30345.00),
	(52, 49, 2, NULL, 3, 'UNIDAD', 1, 1, 26180.00, 19.00, 26180.00),
	(53, 50, 3, NULL, 4, 'UNIDAD', 2, 2, 30940.00, 19.00, 61880.00),
	(54, 51, 4, NULL, 5, 'UNIDAD', 3, 3, 8330.00, 19.00, 24990.00),
	(55, 52, 5, NULL, 6, 'UNIDAD', 1, 1, 73780.00, 19.00, 73780.00),
	(56, 53, 6, NULL, 7, 'UNIDAD', 1, 1, 196350.00, 19.00, 196350.00),
	(57, 54, 16, NULL, 26, 'UNIDAD', 3, 3, 1190.00, 0.00, 3570.00),
	(58, 55, 17, NULL, 27, 'UNIDAD', 1, 1, 1190.00, 19.00, 1190.00),
	(59, 56, 1, NULL, 1, 'UNIDAD', 2, 2, 10115.00, 19.00, 20230.00),
	(60, 57, 2, NULL, 3, 'UNIDAD', 3, 3, 26180.00, 19.00, 78540.00),
	(61, 58, 3, NULL, 4, 'UNIDAD', 1, 1, 30940.00, 19.00, 30940.00),
	(62, 59, 4, NULL, 5, 'UNIDAD', 2, 2, 8330.00, 19.00, 16660.00),
	(63, 60, 5, NULL, 6, 'UNIDAD', 3, 3, 73780.00, 19.00, 221340.00),
	(64, 61, 6, NULL, 7, 'UNIDAD', 1, 1, 196350.00, 19.00, 196350.00),
	(65, 62, 16, NULL, 26, 'UNIDAD', 2, 2, 1190.00, 0.00, 2380.00),
	(66, 63, 17, NULL, 27, 'UNIDAD', 3, 3, 1190.00, 19.00, 3570.00),
	(67, 64, 1, NULL, 1, 'UNIDAD', 1, 1, 10115.00, 19.00, 10115.00);

CREATE TABLE IF NOT EXISTS `devoluciones` (
  `id_devolucion` int NOT NULL AUTO_INCREMENT,
  `id_sucursal` int NOT NULL,
  `tipo_devolucion` enum('CLIENTE','PROVEEDOR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_venta` int DEFAULT NULL,
  `id_compra` int DEFAULT NULL,
  `id_producto` int NOT NULL,
  `id_lote` int DEFAULT NULL,
  `cantidad` int NOT NULL,
  `motivo` enum('PRODUCTO_DEFECTUOSO','ERROR_ENTREGA','PROXIMO_A_VENCER','VENCIDO','RETIRO_MERCADO','EMPAQUE_DANADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado_producto` enum('APTO_PARA_REINGRESO','DESECHADO','DEVUELTO_A_FABRICA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_devolucion` datetime DEFAULT CURRENT_TIMESTAMP,
  `id_usuario_regente` int NOT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `evidencia_devolucion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_devolucion`),
  KEY `id_sucursal` (`id_sucursal`),
  KEY `id_venta` (`id_venta`),
  KEY `id_compra` (`id_compra`),
  KEY `id_producto` (`id_producto`),
  KEY `id_usuario_regente` (`id_usuario_regente`),
  KEY `idx_devoluciones_lote` (`id_lote`),
  CONSTRAINT `devoluciones_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`),
  CONSTRAINT `devoluciones_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `devoluciones_ibfk_3` FOREIGN KEY (`id_compra`) REFERENCES `compras` (`id_compra`),
  CONSTRAINT `devoluciones_ibfk_4` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `devoluciones_ibfk_5` FOREIGN KEY (`id_usuario_regente`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_devoluciones_lote` FOREIGN KEY (`id_lote`) REFERENCES `lotes` (`id_lote`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `devoluciones` (`id_devolucion`, `id_sucursal`, `tipo_devolucion`, `id_venta`, `id_compra`, `id_producto`, `id_lote`, `cantidad`, `motivo`, `estado_producto`, `fecha_devolucion`, `id_usuario_regente`, `observaciones`, `evidencia_devolucion`) VALUES
	(1, 1, 'CLIENTE', 10, NULL, 1, 11, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-09-13 17:55:34', 1, 'El cleinte nunca salio', NULL),
	(2, 1, 'CLIENTE', 23, NULL, 17, 27, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-07-23 23:31:00', 1, 'Devolución parcial de prueba sin evidencia', NULL),
	(3, 1, 'CLIENTE', 29, NULL, 6, 7, 1, 'PRODUCTO_DEFECTUOSO', 'DESECHADO', '2026-07-31 11:47:00', 1, 'Devolución parcial de prueba sin evidencia', NULL),
	(4, 1, 'CLIENTE', 35, NULL, 4, 5, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-08-08 00:03:00', 1, 'Devolución parcial de prueba sin evidencia', NULL),
	(5, 1, 'CLIENTE', 41, NULL, 2, 3, 1, 'PRODUCTO_DEFECTUOSO', 'DESECHADO', '2026-08-15 11:29:00', 1, 'Devolución parcial de prueba sin evidencia', NULL),
	(6, 1, 'CLIENTE', 47, NULL, 17, 27, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-08-22 23:45:00', 1, 'Devolución parcial de prueba sin evidencia', NULL),
	(7, 1, 'CLIENTE', 53, NULL, 6, 7, 1, 'PRODUCTO_DEFECTUOSO', 'DESECHADO', '2026-08-30 12:01:00', 1, 'Devolución parcial de prueba sin evidencia', NULL),
	(8, 1, 'CLIENTE', 59, NULL, 4, 5, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-09-06 23:27:00', 1, 'Devolución parcial de prueba sin evidencia', NULL),
	(9, 1, 'CLIENTE', 26, NULL, 3, 4, 2, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-07-28 17:04:00', 1, 'Devolución total de prueba sin evidencia', NULL),
	(10, 1, 'CLIENTE', 38, NULL, 16, 26, 2, 'ERROR_ENTREGA', 'DESECHADO', '2026-08-12 16:46:00', 1, 'Devolución total de prueba sin evidencia', NULL),
	(11, 1, 'CLIENTE', 50, NULL, 3, 4, 2, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-08-27 16:28:00', 1, 'Devolución total de prueba sin evidencia', NULL),
	(12, 1, 'CLIENTE', 62, NULL, 16, 26, 2, 'ERROR_ENTREGA', 'DESECHADO', '2026-09-11 17:00:00', 1, 'Devolución total de prueba sin evidencia', NULL),
	(13, 1, 'PROVEEDOR', NULL, 1, 1, 1, 2, 'PRODUCTO_DEFECTUOSO', 'DEVUELTO_A_FABRICA', '2026-08-20 15:30:00', 1, 'Devolución a proveedor de prueba sin evidencia', NULL),
	(14, 1, 'CLIENTE', 63, NULL, 17, 27, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-09-14 21:28:27', 1, 'ninguna', '/uploads/devoluciones/devolucion_28_0dd45ffe-df94-420e-b986-6509d9fee827.jpeg'),
	(15, 1, 'CLIENTE', 63, NULL, 17, 27, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-09-14 21:36:06', 1, NULL, '/uploads/devoluciones/devolucion_28_45eb8fe0-3713-455f-bbb7-d14f7f405437.png'),
	(16, 1, 'CLIENTE', 63, NULL, 17, 27, 1, 'ERROR_ENTREGA', 'APTO_PARA_REINGRESO', '2026-09-14 21:47:27', 1, 'ninguna', '/uploads/devoluciones/devolucion_28_e025150a-0b23-47b5-aa27-7102d7fcc900.jpeg');

CREATE TABLE IF NOT EXISTS `domicilios` (
  `id_domicilio` int NOT NULL AUTO_INCREMENT,
  `id_venta` int NOT NULL,
  `id_cliente` int NOT NULL,
  `direccion_entrega` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono_contacto` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_domiciliario` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `costo_domicilio` decimal(10,2) DEFAULT '0.00',
  `pagado` tinyint(1) NOT NULL DEFAULT '0',
  `fecha_pago` datetime DEFAULT NULL,
  `estado` enum('PENDIENTE','EN_PREPARACION','EN_CAMINO','ENTREGADO','CANCELADO') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `fecha_hora_salida` datetime DEFAULT NULL,
  `fecha_hora_entrega` datetime DEFAULT NULL,
  `evidencia_entrega` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observaciones_entrega` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_domicilio`),
  UNIQUE KEY `id_venta` (`id_venta`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `domicilios_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `domicilios_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `domicilios` (`id_domicilio`, `id_venta`, `id_cliente`, `direccion_entrega`, `telefono_contacto`, `nombre_domiciliario`, `costo_domicilio`, `pagado`, `fecha_pago`, `estado`, `fecha_hora_salida`, `fecha_hora_entrega`, `evidencia_entrega`, `observaciones_entrega`) VALUES
	(1, 1, 1, 'Calle 25 # 14-30 Apt 302', '3157894512', 'Marmato Motos Express', 4000.00, 0, NULL, 'ENTREGADO', NULL, NULL, NULL, NULL),
	(2, 5, 1, 'Calle 25 # 14-30', '3157894512', 'Javier', 2500.00, 1, '2026-09-13 17:38:44', 'ENTREGADO', '2026-09-13 17:23:45', '2026-09-13 17:38:44', '/uploads/domicilios/domicilio_2_89feafde-43f7-462c-a776-750e17ad6215.png', NULL),
	(3, 6, 1, 'Calle 25 # 14-30', '3157894512', NULL, 0.00, 0, NULL, 'CANCELADO', '2026-09-13 17:23:50', NULL, '/uploads/domicilios/domicilio_3_98b69d3c-9c83-47da-a674-2550258c0e4d.png', NULL),
	(4, 7, 1, 'Calle 25 # 14-30', '3157894512', 'Diego', 2000.00, 1, '2026-09-13 09:45:40', 'ENTREGADO', NULL, '2026-09-13 09:45:40', '/uploads/domicilios/domicilio_4_1789310740416.png', 'blablabla'),
	(5, 8, 1, 'Calle 25 # 14-30', '3157894512', 'Digo', 2000.00, 0, NULL, 'ENTREGADO', '2026-09-13 17:08:58', '2026-09-13 17:09:07', NULL, NULL),
	(6, 9, 2, 'Carrera 10 # 5-12', '3124567890', 'Diego', 3000.00, 0, NULL, 'CANCELADO', NULL, NULL, NULL, NULL),
	(7, 10, 1, 'Calle 25 # 14-30', '3157894512', 'Javier', 3000.00, 0, NULL, 'CANCELADO', '2026-09-13 17:54:57', NULL, NULL, 'Domicilio devuelto completamente'),
	(8, 11, 2, 'Carrera 10 # 5-12', '3124567890', 'Diego', 3000.00, 0, NULL, 'EN_CAMINO', '2026-09-13 20:19:19', NULL, NULL, NULL),
	(9, 13, 1, 'Calle 25 # 14-30', '3157894512', 'GO', 3000.00, 0, NULL, 'EN_CAMINO', '2026-09-14 09:04:37', NULL, NULL, NULL),
	(10, 14, 2, 'Carrera 10 # 5-12', '3124567890', 'Milton', 10000.00, 0, NULL, 'EN_CAMINO', '2026-09-14 20:32:06', NULL, NULL, NULL),
	(11, 15, 1, 'Calle 25 # 14-30', '3157894512', 'Diego', 2000.00, 0, NULL, 'EN_CAMINO', '2026-09-14 11:29:40', NULL, NULL, NULL),
	(12, 16, 2, 'Carrera 10 # 5-12', '3124567890', 'Diego', 3000.00, 0, NULL, 'PENDIENTE', NULL, NULL, NULL, NULL),
	(13, 18, 3, 'Dirección prueba #1', '3000000001', NULL, 4000.00, 1, '2026-07-16 19:36:00', 'ENTREGADO', '2026-07-16 18:51:00', '2026-07-16 19:26:00', NULL, 'Registro de prueba para reportes'),
	(14, 21, 1, 'Dirección prueba #2', '3000000002', NULL, 5000.00, 1, '2026-07-20 14:09:00', 'ENTREGADO', '2026-07-20 13:24:00', '2026-07-20 13:59:00', NULL, 'Registro de prueba para reportes'),
	(15, 24, 3, 'Dirección prueba #3', '3000000003', NULL, 6000.00, 0, NULL, 'PENDIENTE', NULL, NULL, NULL, 'Registro de prueba para reportes'),
	(16, 27, 1, 'Dirección prueba #4', '3000000004', NULL, 3000.00, 0, NULL, 'EN_PREPARACION', NULL, NULL, NULL, 'Registro de prueba para reportes'),
	(17, 30, 3, 'Dirección prueba #5', '3000000005', NULL, 4000.00, 0, NULL, 'EN_CAMINO', '2026-07-31 19:23:00', NULL, NULL, 'Registro de prueba para reportes'),
	(18, 33, 1, 'Dirección prueba #6', '3000000006', NULL, 5000.00, 0, NULL, 'ENTREGADO', '2026-08-04 13:06:00', '2026-08-04 13:41:00', NULL, 'Registro de prueba para reportes'),
	(19, 36, 3, 'Dirección prueba #7', '3000000007', NULL, 6000.00, 0, NULL, 'CANCELADO', '2026-08-08 06:49:00', NULL, NULL, 'Registro de prueba para reportes'),
	(20, 39, 1, 'Dirección prueba #8', '3000000008', NULL, 3000.00, 1, '2026-08-12 02:07:00', 'ENTREGADO', '2026-08-12 01:22:00', '2026-08-12 01:57:00', NULL, 'Registro de prueba para reportes'),
	(21, 42, 3, 'Dirección prueba #9', '3000000009', NULL, 4000.00, 0, NULL, 'EN_CAMINO', '2026-08-15 19:05:00', NULL, NULL, 'Registro de prueba para reportes'),
	(22, 45, 1, 'Dirección prueba #10', '3000000010', NULL, 5000.00, 1, '2026-08-19 13:33:00', 'ENTREGADO', '2026-08-19 12:48:00', '2026-08-19 13:23:00', NULL, 'Registro de prueba para reportes'),
	(23, 48, 3, 'Dirección prueba #11', '3000000011', NULL, 6000.00, 0, NULL, 'PENDIENTE', NULL, NULL, NULL, 'Registro de prueba para reportes'),
	(24, 51, 1, 'Dirección prueba #12', '3000000012', NULL, 3000.00, 0, NULL, 'ENTREGADO', '2026-08-27 01:04:00', '2026-08-27 01:39:00', NULL, 'Registro de prueba para reportes'),
	(25, 54, 3, 'Dirección prueba #13', '3000000013', NULL, 4000.00, 0, NULL, 'CANCELADO', '2026-08-30 18:47:00', NULL, NULL, 'Registro de prueba para reportes'),
	(26, 57, 1, 'Dirección prueba #14', '3000000014', NULL, 5000.00, 0, NULL, 'EN_PREPARACION', NULL, NULL, NULL, 'Registro de prueba para reportes'),
	(27, 60, 3, 'Dirección prueba #15', '3000000015', NULL, 6000.00, 0, NULL, 'ENTREGADO', '2026-09-07 07:03:00', '2026-09-07 07:38:00', NULL, 'Registro de prueba para reportes'),
	(28, 63, 1, 'Dirección prueba #16', '3000000016', NULL, 3000.00, 0, NULL, 'CANCELADO', '2026-09-11 00:46:00', NULL, NULL, 'Registro de prueba para reportes | Domicilio devuelto completamente');

CREATE TABLE IF NOT EXISTS `empleados` (
  `id_empleado` int NOT NULL AUTO_INCREMENT,
  `id_sucursal` int NOT NULL,
  `nombre_completo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_documento` enum('CC','CE','PASAPORTE') COLLATE utf8mb4_unicode_ci DEFAULT 'CC',
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cargo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `salario` decimal(12,2) NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_empleado`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `uk_empleados_correo` (`correo`),
  KEY `id_sucursal` (`id_sucursal`),
  CONSTRAINT `empleados_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `empleados` (`id_empleado`, `id_sucursal`, `nombre_completo`, `tipo_documento`, `numero_documento`, `telefono`, `direccion`, `correo`, `cargo`, `salario`, `estado`) VALUES
	(1, 1, 'Luz Marina Bermúdez', 'CC', '31892014', '3115678901', 'Calle 12 # 4-15', 'luz.bermudez@farmasoft.com', 'Administrador General', 4500000.00, 'ACTIVO'),
	(2, 1, 'Jonathan Smith Pérez', 'CC', '1115938201', '3174561230', 'Cra 15 # 22-08', 'jonathan.perez@farmasoft.com', 'Regente de Farmacia', 3200000.00, 'ACTIVO'),
	(3, 1, 'Sandra Patricia Osorio', 'CC', '66982014', '3148901234', 'Calle 40 # 18-90', 'sandra.osorio@farmasoft.com', 'Auxiliar de Farmacia', 1800000.00, 'ACTIVO'),
	(4, 1, 'Kevin Alexis Quintero', 'CC', '1112938471', '3201237894', 'Cra 27 # 8-33', 'kevin.quintero@farmasoft.com', 'Vendedor / Cajero', 1600000.00, 'ACTIVO'),
	(5, 1, 'Diego Alejandro Olaya Gonzalez', 'CC', '1117352098', '3228991339', 'Carrera 27a # 10 a 04', 'diealeolagon@gmail.com', 'Jefe', 10000000.00, 'ACTIVO'),
	(6, 1, 'Javier Millei', 'CC', '1232134', '3228991339', 'Carrera 27a # 10 a 04', 'a@gmail.com', 'domiciliario', 2000000.00, 'ACTIVO');

CREATE TABLE IF NOT EXISTS `formulas_medicas` (
  `id_formula` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int NOT NULL,
  `nombre_medico` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tarjeta_profesional` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entidad_salud` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_expedicion` date NOT NULL,
  `vigencia_dias` int DEFAULT '30',
  `archivo_adjunto_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_formula`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `formulas_medicas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `formulas_medicas` (`id_formula`, `id_cliente`, `nombre_medico`, `tarjeta_profesional`, `entidad_salud`, `fecha_expedicion`, `vigencia_dias`, `archivo_adjunto_url`, `observaciones`) VALUES
	(1, 2, 'Dr. Fernando Arango', 'RM-45892-VALLE', 'Clínica Imbanaco', '2026-07-26', 15, NULL, 'Tomar Amoxicilina cada 8 horas por 7 días');

CREATE TABLE IF NOT EXISTS `lotes` (
  `id_lote` int NOT NULL AUTO_INCREMENT,
  `id_producto` int NOT NULL,
  `numero_lote` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_fabricacion` date DEFAULT NULL,
  `fecha_vencimiento` date NOT NULL,
  `cantidad_inicial` int NOT NULL,
  `cantidad_actual` int NOT NULL,
  `estado` enum('DISPONIBLE','PROXIMO_A_VENCER','VENCIDO','RETIRADO','DEVUELTO') COLLATE utf8mb4_unicode_ci DEFAULT 'DISPONIBLE',
  PRIMARY KEY (`id_lote`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `lotes_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `lotes` (`id_lote`, `id_producto`, `numero_lote`, `fecha_fabricacion`, `fecha_vencimiento`, `cantidad_inicial`, `cantidad_actual`, `estado`) VALUES
	(1, 1, 'LOTE-DOL-2026-A', '2026-09-01', '2027-09-30', 100, 86, 'DISPONIBLE'),
	(2, 1, 'LOTE-DOL-2026-B', '2026-09-01', '2027-10-31', 50, 50, 'DISPONIBLE'),
	(3, 2, 'LOTE-AMX-2026-A', '2026-09-01', '2027-11-30', 80, 79, 'DISPONIBLE'),
	(4, 3, 'LOTE-APR-2026-A', '2026-09-01', '2027-12-31', 45, 45, 'DISPONIBLE'),
	(5, 4, 'LOTE-LOR-2026-A', '2026-09-01', '2027-12-31', 120, 80, 'DISPONIBLE'),
	(6, 5, 'LOTE-UMB-2026-A', '2026-09-01', '2027-12-31', 25, 25, 'DISPONIBLE'),
	(7, 6, 'LOTE-INS-2026-A', '2026-09-01', '2027-12-31', 12, 12, 'DISPONIBLE'),
	(8, 1, '3', '2026-09-25', '2027-09-18', 100, 97, 'DISPONIBLE'),
	(9, 8, 'diego', '2026-09-30', '2027-06-11', 100, 100, 'DISPONIBLE'),
	(10, 14, 'lote b', '2026-09-03', '2026-10-28', 4000, 4000, 'PROXIMO_A_VENCER'),
	(11, 1, 'lote 1', '2026-09-12', '2026-09-13', 2400, 2362, 'VENCIDO'),
	(12, 3, 'lote 3', '2026-09-12', '2026-09-25', 100, 99, 'PROXIMO_A_VENCER'),
	(13, 1, 'lote c', '2026-09-12', '2026-09-12', 600, 599, 'VENCIDO'),
	(24, 8, 'lote 10', '2026-09-13', '2026-09-30', 8280, 8280, 'PROXIMO_A_VENCER'),
	(25, 15, 'lote 0111', '2026-09-13', '2026-09-30', 4000, 4000, 'PROXIMO_A_VENCER'),
	(26, 16, 'lote Clonacepan 2', '2026-09-14', '2026-11-06', 16000, 16000, 'PROXIMO_A_VENCER'),
	(27, 17, 'Lote Viagra', '2026-09-14', '2027-05-14', 1200, 1202, 'DISPONIBLE');

CREATE TABLE IF NOT EXISTS `movimientos_inventario` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `id_sucursal` int NOT NULL,
  `tipo_movimiento` enum('ENTRADA_COMPRA','SALIDA_VENTA','AJUSTE_PERDIDA','AJUSTE_DANIO','DEVOLUCION_CLIENTE','DEVOLUCION_PROVEEDOR','TRASLADO_ENTRADA','TRASLADO_SALIDA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_producto` int NOT NULL,
  `id_lote` int DEFAULT NULL,
  `cantidad` int NOT NULL,
  `existencia_anterior` int NOT NULL,
  `nueva_existencia` int NOT NULL,
  `fecha_movimiento` datetime DEFAULT CURRENT_TIMESTAMP,
  `id_usuario` int NOT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `id_sucursal` (`id_sucursal`),
  KEY `id_producto` (`id_producto`),
  KEY `id_lote` (`id_lote`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `movimientos_inventario_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`),
  CONSTRAINT `movimientos_inventario_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `movimientos_inventario_ibfk_3` FOREIGN KEY (`id_lote`) REFERENCES `lotes` (`id_lote`),
  CONSTRAINT `movimientos_inventario_ibfk_4` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `movimientos_inventario` (`id_movimiento`, `id_sucursal`, `tipo_movimiento`, `id_producto`, `id_lote`, `cantidad`, `existencia_anterior`, `nueva_existencia`, `fecha_movimiento`, `id_usuario`, `motivo`) VALUES
	(1, 1, 'ENTRADA_COMPRA', 1, 1, 100, 0, 100, '2026-09-12 17:51:21', 1, 'Carga inicial por compra FAC-TQ-90812'),
	(2, 1, 'SALIDA_VENTA', 1, 1, 1, 100, 99, '2026-09-12 17:51:21', 4, 'Venta según Factura FARM-00001'),
	(3, 1, 'DEVOLUCION_CLIENTE', 1, 11, 1, 2362, 2363, '2026-09-13 17:55:33', 1, 'Reingreso por devolución de domicilio #7 / devolución #1'),
	(4, 1, 'DEVOLUCION_CLIENTE', 17, 27, 1, 1199, 1200, '2026-09-14 21:28:26', 1, 'Reingreso por devolución de domicilio #28 / devolución #14'),
	(5, 1, 'DEVOLUCION_CLIENTE', 17, 27, 1, 1200, 1201, '2026-09-14 21:36:06', 1, 'Reingreso por devolución de domicilio #28 / devolución #15'),
	(6, 1, 'DEVOLUCION_CLIENTE', 17, 27, 1, 1201, 1202, '2026-09-14 21:47:27', 1, 'Reingreso por devolución de domicilio #28 / devolución #16');

CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id_token` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `token_hash` char(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_expiracion` datetime NOT NULL,
  `usado` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_token`),
  UNIQUE KEY `uk_password_reset_token_hash` (`token_hash`),
  KEY `idx_password_reset_usuario` (`id_usuario`),
  KEY `idx_password_reset_expiracion` (`fecha_expiracion`),
  CONSTRAINT `fk_password_reset_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `password_reset_tokens` (`id_token`, `id_usuario`, `token_hash`, `fecha_creacion`, `fecha_expiracion`, `usado`) VALUES
	(1, 5, 'adca9e8974769536afd12ee09cf8b428d8961e5cc5554122eb487d65a1b6ba31', '2026-09-14 08:43:38', '2026-09-14 09:13:38', 1),
	(2, 5, 'fb605567c7cb367aedb57688af47bad6f205cdc8287aaf23b7a75009b3f0c8c3', '2026-09-14 08:44:21', '2026-09-14 09:14:21', 1),
	(3, 5, 'c3c7b62e7c2b7778cfa7e8880b82effe3a5c31d3291ff1b69f4c32754538efb7', '2026-09-14 08:44:47', '2026-09-14 09:14:47', 1),
	(4, 5, 'f468300fb15523adfe6f1457c486953480159e3793bb239870ae942da82d3b3e', '2026-09-14 08:45:02', '2026-09-14 09:15:02', 0);

CREATE TABLE IF NOT EXISTS `permisos` (
  `id_permiso` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `modulo` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `orden` int NOT NULL DEFAULT '0',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_permiso`),
  UNIQUE KEY `uk_permiso_codigo` (`codigo`),
  KEY `idx_permiso_modulo` (`modulo`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`, `orden`, `activo`) VALUES
	(1, 'DASHBOARD_VER', 'Ver dashboard', 'Dashboard', 'Consultar indicadores y resumen principal.', 10, 1),
	(2, 'PRODUCTOS_VER', 'Ver productos', 'Catálogo y Stock', 'Consultar catálogo, precios y datos de productos.', 20, 1),
	(3, 'PRODUCTOS_CREAR', 'Crear productos', 'Catálogo y Stock', 'Registrar nuevos productos o medicamentos.', 21, 1),
	(4, 'PRODUCTOS_EDITAR', 'Editar productos', 'Catálogo y Stock', 'Modificar información de productos.', 22, 1),
	(5, 'PRODUCTOS_ELIMINAR', 'Eliminar productos', 'Catálogo y Stock', 'Eliminar productos cuando las reglas del sistema lo permitan.', 23, 1),
	(6, 'CATEGORIAS_VER', 'Ver categorías', 'Catálogo y Stock', 'Consultar categorías de productos.', 30, 1),
	(7, 'CATEGORIAS_GESTIONAR', 'Gestionar categorías', 'Catálogo y Stock', 'Crear, editar o eliminar categorías.', 31, 1),
	(8, 'LOTES_VER', 'Ver lotes e inventario', 'Inventario', 'Consultar lotes, existencias y vencimientos.', 40, 1),
	(9, 'LOTES_CREAR', 'Registrar lotes', 'Inventario', 'Registrar nuevos lotes de inventario.', 41, 1),
	(10, 'LOTES_RETIRAR', 'Retirar o reactivar lotes', 'Inventario', 'Cambiar disponibilidad de lotes.', 42, 1),
	(11, 'ALERTAS_VER', 'Ver alertas de vencimiento', 'Inventario', 'Consultar productos próximos a vencer o vencidos.', 43, 1),
	(12, 'VENTAS_VER', 'Ver historial de ventas', 'Dispensación / POS', 'Consultar ventas y su detalle.', 50, 1),
	(13, 'VENTAS_CREAR', 'Crear ventas', 'Dispensación / POS', 'Registrar nuevas ventas y facturación.', 51, 1),
	(14, 'VENTAS_ANULAR', 'Anular ventas', 'Dispensación / POS', 'Anular ventas según las reglas del sistema.', 52, 1),
	(15, 'FORMULAS_VER', 'Ver fórmulas médicas', 'Fórmulas Médicas', 'Consultar fórmulas registradas.', 60, 1),
	(16, 'FORMULAS_CREAR', 'Registrar fórmulas', 'Fórmulas Médicas', 'Crear fórmulas médicas para clientes.', 61, 1),
	(17, 'FORMULAS_EDITAR', 'Editar fórmulas', 'Fórmulas Médicas', 'Actualizar datos de fórmulas médicas.', 62, 1),
	(18, 'PROVEEDORES_VER', 'Ver proveedores', 'Proveedores y Compras', 'Consultar directorio de proveedores.', 70, 1),
	(19, 'PROVEEDORES_CREAR', 'Crear proveedores', 'Proveedores y Compras', 'Registrar nuevos proveedores.', 71, 1),
	(20, 'PROVEEDORES_EDITAR', 'Editar proveedores', 'Proveedores y Compras', 'Actualizar información de proveedores.', 72, 1),
	(21, 'PROVEEDORES_INACTIVAR', 'Inactivar proveedores', 'Proveedores y Compras', 'Inactivar proveedores cuando no puedan eliminarse.', 73, 1),
	(22, 'COMPRAS_VER', 'Ver órdenes de compra', 'Proveedores y Compras', 'Consultar pedidos y compras a proveedores.', 80, 1),
	(23, 'COMPRAS_CREAR', 'Crear órdenes de compra', 'Proveedores y Compras', 'Crear pedidos a proveedores.', 81, 1),
	(24, 'COMPRAS_EDITAR', 'Editar órdenes de compra', 'Proveedores y Compras', 'Editar pedidos dentro del periodo permitido.', 82, 1),
	(25, 'COMPRAS_RECIBIR', 'Recibir mercancía', 'Proveedores y Compras', 'Registrar recepción y entrada de mercancía.', 83, 1),
	(26, 'COMPRAS_CANCELAR', 'Cancelar órdenes', 'Proveedores y Compras', 'Cancelar pedidos pendientes.', 84, 1),
	(27, 'CLIENTES_VER', 'Ver clientes', 'Pacientes / Clientes', 'Consultar clientes y su historial.', 90, 1),
	(28, 'CLIENTES_CREAR', 'Crear clientes', 'Pacientes / Clientes', 'Registrar nuevos clientes.', 91, 1),
	(29, 'CLIENTES_EDITAR', 'Editar clientes', 'Pacientes / Clientes', 'Actualizar información de clientes.', 92, 1),
	(30, 'DOMICILIOS_VER', 'Ver domicilios', 'Domicilios', 'Consultar domicilios registrados.', 100, 1),
	(31, 'DOMICILIOS_GESTIONAR', 'Gestionar domicilios', 'Domicilios', 'Asignar, actualizar estado y registrar entregas.', 101, 1),
	(32, 'DEVOLUCIONES_VER', 'Ver devoluciones', 'Devoluciones', 'Consultar devoluciones de cliente o proveedor.', 110, 1),
	(33, 'DEVOLUCIONES_GESTIONAR', 'Gestionar devoluciones', 'Devoluciones', 'Registrar y autorizar devoluciones.', 111, 1),
	(34, 'EMPLEADOS_VER', 'Ver empleados', 'Personal y Accesos', 'Consultar personal y cuentas de acceso.', 120, 1),
	(35, 'EMPLEADOS_GESTIONAR', 'Gestionar empleados y accesos', 'Personal y Accesos', 'Crear o editar empleados, usuarios y roles asignados.', 121, 1),
	(36, 'ROLES_VER', 'Ver roles y permisos', 'Personal y Accesos', 'Consultar roles y permisos disponibles.', 130, 1),
	(37, 'ROLES_GESTIONAR', 'Gestionar roles y permisos', 'Personal y Accesos', 'Crear, editar o eliminar roles y configurar permisos.', 131, 1),
	(38, 'REPORTES_VER', 'Ver reportes', 'Reportes', 'Consultar reportes del sistema.', 140, 1),
	(39, 'SUCURSALES_VER', 'Ver sucursales', 'Configuración', 'Consultar sucursales.', 150, 1),
	(40, 'SUCURSALES_GESTIONAR', 'Gestionar sucursales', 'Configuración', 'Crear o modificar sucursales.', 151, 1),
	(41, 'CONFIGURACION_VER', 'Ver configuración', 'Configuración', 'Consultar parámetros generales.', 152, 1),
	(42, 'CONFIGURACION_EDITAR', 'Editar configuración', 'Configuración', 'Modificar parámetros generales de PillOne.', 153, 1);

CREATE TABLE IF NOT EXISTS `productos` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `codigo_interno` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_barras` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_comercial` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_generico` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `presentacion` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `concentracion` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `laboratorio` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `registro_invima` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_categoria` int NOT NULL,
  `id_proveedor` int DEFAULT NULL,
  `precio_compra` decimal(12,2) NOT NULL,
  `precio_venta` decimal(12,2) NOT NULL,
  `porcentaje_iva` decimal(5,2) NOT NULL DEFAULT '19.00',
  `precio_venta_tableta` decimal(12,2) DEFAULT NULL,
  `unidades_por_empaque` int DEFAULT '1' COMMENT 'Cantidad total de unidades que trae la presentación mayor',
  `sellos_por_caja` int DEFAULT '1' COMMENT 'Cantidad de sellos o blísters que trae la caja principal',
  `unidades_por_sello` int DEFAULT '1' COMMENT 'Cantidad de unidades o pastillas que trae cada sello o blíster',
  `precio_compra_empaque` decimal(12,2) DEFAULT NULL COMMENT 'Precio total de compra del empaque completo',
  `precio_venta_empaque` decimal(12,2) DEFAULT NULL COMMENT 'Precio total de venta del empaque completo',
  `stock_total` int DEFAULT '0',
  `stock_minimo` int DEFAULT '10',
  `ubicacion_estante` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `requiere_formula` tinyint(1) DEFAULT '0',
  `es_venta_libre` tinyint(1) DEFAULT '1',
  `es_controlado` tinyint(1) DEFAULT '0',
  `requiere_refrigeracion` tinyint(1) DEFAULT '0',
  `restricciones_venta` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','DESCONTINUADO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo_interno` (`codigo_interno`),
  UNIQUE KEY `codigo_barras` (`codigo_barras`),
  KEY `id_categoria` (`id_categoria`),
  KEY `id_proveedor` (`id_proveedor`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`),
  CONSTRAINT `productos_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedores` (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `productos` (`id_producto`, `codigo_interno`, `codigo_barras`, `nombre_comercial`, `nombre_generico`, `descripcion`, `presentacion`, `concentracion`, `laboratorio`, `registro_invima`, `id_categoria`, `id_proveedor`, `precio_compra`, `precio_venta`, `porcentaje_iva`, `precio_venta_tableta`, `unidades_por_empaque`, `sellos_por_caja`, `unidades_por_sello`, `precio_compra_empaque`, `precio_venta_empaque`, `stock_total`, `stock_minimo`, `ubicacion_estante`, `requiere_formula`, `es_venta_libre`, `es_controlado`, `requiere_refrigeracion`, `restricciones_venta`, `estado`) VALUES
	(1, 'PROD-001', '770200100101', 'Dolex Forte', 'Acetaminofén + Cafeína', 'Analgésico y antipirético para dolores intensos', 'Caja x 12 Tabletas', '500mg / 65mg', 'GSK / TQ', 'INVIMA 2018M-0001234', 1, 3, 4500.00, 10115.00, 19.00, NULL, 12, 1, 12, 50000.00, 113050.00, 233, 20, 'Estante A1', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(2, 'PROD-002', '770200100102', 'Amoxicilina Genfar', 'Amoxicilina', 'Antibiótico bactericida de amplio espectro', 'Caja x 50 Cápsulas', '500 mg', 'Genfar', 'INVIMA 2020M-0015678', 2, 2, 12000.00, 26180.00, 19.00, NULL, 50, 1, 50, 550000.00, 1190000.00, 79, 15, 'Estante B2 (Restringido)', 1, 0, 0, 0, NULL, 'ACTIVO'),
	(3, 'PROD-003', '770200100103', 'Apronax', 'Naproxeno Sódico', 'Antiinflamatorio no esteroideo de alivio prolongado', 'Caja x 10 Tabletas', '550 mg', 'Bayer', 'INVIMA 2019M-0009876', 1, 4, 15000.00, 30940.00, 19.00, NULL, 10, 1, 10, 140000.00, 285600.00, 144, 10, 'Estante A2', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(4, 'PROD-004', '770200100104', 'Loratadina Procaps', 'Loratadina', 'Antihistamínico no sedante', 'Caja x 10 Tabletas', '10 mg', 'Procaps', 'INVIMA 2021M-0011223', 3, 1, 3000.00, 8330.00, 19.00, NULL, 10, 1, 10, 28000.00, 77350.00, 80, 15, 'Estante C1', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(5, 'PROD-005', '770200100105', 'Bloqueador Umbrela Urban', 'Protector Solar FPS 50+', 'Protección dermatológica contra rayos UV y luz azul', 'Frasco x 50 ml', 'FPS 50+', 'Medihealth / TQ', 'NSOC12345-22CO', 6, 3, 38000.00, 73780.00, 19.00, NULL, 1, 1, 1, 38000.00, 73780.00, 25, 5, 'Vitrina Dermocosmética', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(6, 'PROD-006', '770200100106', 'Insulina Lantus', 'Insulina Glargina', 'Insulina de acción prolongada', 'Caja x 5 Plumas soloSTAR 3ml', '100 UI/ml', 'Sanofi', 'INVIMA 2017M-0004512', 2, 1, 110000.00, 196350.00, 19.00, NULL, 5, 1, 5, 520000.00, 952000.00, 12, 5, 'Nevera Principal (2-8°C)', 1, 0, 0, 1, NULL, 'ACTIVO'),
	(8, '001', '1272877382', 'amoxicilina', 'amoxicilina', 'm', 'caja por 200', '500mg', 'genfar', '11877823', 6, 4, 1.00, 2.38, 19.00, NULL, 276, 12, 23, 12.00, 44.03, 8380, 10, 'pacillo 1', 0, 1, 0, 0, 'a', 'ACTIVO'),
	(14, '00101', '12728773823', 'amoxicilina', 'amoxicilina', '1', 'caja por 200', '500mg', 'genfar', '118778231', 7, 4, 10.00, 23.80, 19.00, NULL, 200, 10, 20, 300.00, 476.00, 4000, 10, 'pacillo 1', 0, 1, 0, 0, '', 'ACTIVO'),
	(15, 'codigo1', 'codigoDeBarras', 'Clonacepam', 'Clonacepam', 'aaaaaaaa', 'caja 10 tabletas', '500mg', 'Genfar', '199132registro', 7, 1, 10.00, 23.80, 0.00, 119.00, 100, 10, 10, 200.00, 357.00, 4000, 50, 'estante 2', 0, 1, 0, 0, 'ninguna', 'ACTIVO'),
	(16, '0001111', 'codigoDeBarras2', 'Clonacepam2', 'Clonacepam2', 'diego', 'caja 10 tabletas', '500mg', 'Genfar', '199132registro22', 1, 1, 500.00, 1190.00, 0.00, 14280.00, 400, 20, 20, 30000.00, 53550.00, 16000, 10, 'estante 24', 1, 0, 0, 0, 'solo con formula', 'ACTIVO'),
	(17, 'PROD12', '121737278127821', 'Sildenafil', 'viagra', 'vaso dilatador', 'caja 10 tabletas', '500mg', 'Genfar', '3029919221NV', 4, NULL, 500.00, 1190.00, 19.00, 13090.00, 120, 10, 12, 60000.00, 119000.00, 1202, 10, 'Estante 2B', 0, 1, 0, 0, 'Menores de Edad', 'ACTIVO');

CREATE TABLE IF NOT EXISTS `proveedores` (
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `razon_social` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nit` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_contacto` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_productos` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_proveedor`),
  UNIQUE KEY `nit` (`nit`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `proveedores` (`id_proveedor`, `razon_social`, `nit`, `direccion`, `telefono`, `correo`, `nombre_contacto`, `tipo_productos`, `estado`) VALUES
	(1, 'Laboratorios Procaps S.A.', '890200123-1', 'Calle 80 # 78B-11, Barranquilla', '6053718000', 'ventas@procaps.com.co', 'Lina Marcela Hoyos', 'Medicamentos Genéricos y Marca', 'ACTIVO'),
	(2, 'Genfar S.A.', '860005432-8', 'Transversal 23 # 97-73, Bogotá', '6016345000', 'pedidos@genfar.com.co', 'Roberto Gómez', 'Medicamentos Genéricos y Venta Libre', 'ACTIVO'),
	(3, 'Tecnoquímicas S.A. (TQ)', '890300220-4', 'Calle 23 N° 7-39, Cali', '6028825000', 'contacto@tq.com.co', 'Beatriz Elena Restrepo', 'Cuidado Personal, Bebés y Fármacos', 'ACTIVO'),
	(4, 'Bayer S.A. Colombia', '860001200-5', 'Carrera 58 # 11-84, Bogotá', '6014234000', 'servicio.cliente@bayer.com', 'Carlos Mario Silva', 'Especialidades Farmacéuticas', 'ACTIVO');

CREATE TABLE IF NOT EXISTS `roles` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `roles` (`id_rol`, `nombre`) VALUES
	(1, 'ADMINISTRADOR'),
	(2, 'FARMACEUTICO_REGENTE'),
	(3, 'VENDEDOR'),
	(4, 'AUXILIAR_FARMACIA'),
	(5, 'DOMICILIARIO');

CREATE TABLE IF NOT EXISTS `roles_permisos` (
  `id_rol` int NOT NULL,
  `id_permiso` int NOT NULL,
  PRIMARY KEY (`id_rol`,`id_permiso`),
  KEY `idx_roles_permisos_permiso` (`id_permiso`),
  CONSTRAINT `fk_roles_permisos_permiso` FOREIGN KEY (`id_permiso`) REFERENCES `permisos` (`id_permiso`) ON DELETE CASCADE,
  CONSTRAINT `fk_roles_permisos_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `roles_permisos` (`id_rol`, `id_permiso`) VALUES
	(1, 1),
	(1, 2),
	(1, 3),
	(1, 4),
	(1, 5),
	(1, 6),
	(1, 7),
	(1, 8),
	(1, 9),
	(1, 10),
	(1, 11),
	(1, 12),
	(1, 13),
	(1, 14),
	(1, 15),
	(1, 16),
	(1, 17),
	(1, 18),
	(1, 19),
	(1, 20),
	(1, 21),
	(1, 22),
	(1, 23),
	(1, 24),
	(1, 25),
	(1, 26),
	(1, 27),
	(1, 28),
	(1, 29),
	(1, 30),
	(1, 31),
	(1, 32),
	(1, 33),
	(1, 34),
	(1, 35),
	(1, 36),
	(1, 37),
	(1, 38),
	(1, 39),
	(1, 40),
	(1, 41),
	(1, 42),
	(2, 1),
	(2, 2),
	(2, 3),
	(2, 4),
	(2, 6),
	(2, 7),
	(2, 8),
	(2, 9),
	(2, 10),
	(2, 11),
	(2, 12),
	(2, 13),
	(2, 14),
	(2, 15),
	(2, 16),
	(2, 17),
	(2, 18),
	(2, 19),
	(2, 20),
	(2, 21),
	(2, 22),
	(2, 23),
	(2, 24),
	(2, 25),
	(2, 26),
	(2, 27),
	(2, 28),
	(2, 29),
	(2, 30),
	(2, 31),
	(2, 32),
	(2, 33),
	(2, 38),
	(3, 1),
	(3, 2),
	(3, 8),
	(3, 11),
	(3, 12),
	(3, 13),
	(3, 15),
	(3, 16),
	(3, 27),
	(3, 28),
	(3, 29),
	(3, 30),
	(3, 31),
	(4, 1),
	(4, 2),
	(4, 6),
	(4, 8),
	(4, 9),
	(4, 11),
	(4, 12),
	(4, 13),
	(4, 15),
	(4, 16),
	(4, 18),
	(4, 22),
	(4, 25),
	(4, 27),
	(4, 28),
	(4, 29),
	(4, 30),
	(4, 31),
	(5, 30),
	(5, 31),
	(5, 38);

CREATE TABLE IF NOT EXISTS `sucursales` (
  `id_sucursal` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_sucursal` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ciudad` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT 'Tuluá',
  `departamento` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT 'Valle del Cauca',
  `estado` enum('ACTIVA','INACTIVA') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVA',
  PRIMARY KEY (`id_sucursal`),
  UNIQUE KEY `codigo_sucursal` (`codigo_sucursal`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sucursales` (`id_sucursal`, `nombre`, `codigo_sucursal`, `direccion`, `telefono`, `ciudad`, `departamento`, `estado`) VALUES
	(1, 'FarmaSoft Principal Tuluá', 'TUL-01', 'Calle 25 # 22-10', '6022345678', 'Tuluá', 'Valle del Cauca', 'ACTIVA'),
	(2, 'FarmaSoft Sucursal Centro', 'TUL-02', 'Carrera 28 # 26-45', '6022345679', 'Tuluá', 'Valle del Cauca', 'ACTIVA'),
	(3, 'FarmaSoft Norte', 'BUG-01', 'Calle 18 # 12-30', '6022289012', 'Buga', 'Valle del Cauca', 'ACTIVA');

CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `id_empleado` int NOT NULL,
  `id_rol` int NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` enum('ACTIVO','BLOQUEADO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `id_empleado` (`id_empleado`),
  UNIQUE KEY `username` (`username`),
  KEY `id_rol` (`id_rol`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`) ON DELETE CASCADE,
  CONSTRAINT `usuarios_ibfk_2` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `usuarios` (`id_usuario`, `id_empleado`, `id_rol`, `username`, `password_hash`, `estado`) VALUES
	(1, 1, 1, 'admin', 'PBKDF2$210000$cGlsbG9uZS1hZG1pbi0wMQ==$OU5app0HXnrka1v3tTRWYCqX4UefNIgtF1sUtYk+AfU=', 'ACTIVO'),
	(2, 2, 2, 'regente01', 'PBKDF2$210000$cGlsbG9uZS1yZWdlbnRlMDE=$6jUD8jCb+Rh/mO4VOpx1qRmSTBqmFcS0KRou1Frj7Pw=', 'ACTIVO'),
	(3, 3, 4, 'sandra.aux', 'PBKDF2$210000$IpBB1VHQM+701ngizi33dw==$9+oodkzDV3R+4nMOz1lx0JWHd4/8nI6SHOUtTzgLAec=', 'ACTIVO'),
	(4, 4, 3, 'kevin.vendedor', 'PBKDF2$210000$cGlsbG9uZS1rZXZpbjAwMQ==$oc+rhAuxqrAaEr67ACT7s2wr80fclgkYxzTubJXxlqw=', 'ACTIVO'),
	(5, 5, 3, 'diegod', 'PBKDF2$210000$3Au7wx9HVree6N/0Yok39Q==$Sg56dTzol+1gwuWw6bHzMYpVkeRuGoQO0yspIftkaTY=', 'ACTIVO'),
	(6, 6, 5, 'domiciliario', 'PBKDF2$210000$mTq+/VXewXo+IAxX4GUBsw==$xQtzBoW/CLK2vS4LIqTRVwDukTUbceAY7ENC3Wxz8r4=', 'ACTIVO');

CREATE TABLE IF NOT EXISTS `ventas` (
  `id_venta` int NOT NULL AUTO_INCREMENT,
  `id_sucursal` int NOT NULL,
  `numero_factura` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_cliente` int DEFAULT NULL,
  `id_empleado` int NOT NULL,
  `id_usuario` int NOT NULL,
  `fecha_venta` datetime DEFAULT CURRENT_TIMESTAMP,
  `subtotal` decimal(12,2) NOT NULL,
  `descuento` decimal(12,2) DEFAULT '0.00',
  `aplica_iva` tinyint(1) NOT NULL DEFAULT '1',
  `porcentaje_iva` decimal(5,2) NOT NULL DEFAULT '19.00',
  `impuesto_iva` decimal(12,2) DEFAULT '0.00',
  `total` decimal(12,2) NOT NULL,
  `metodo_pago` enum('EFECTIVO','TARJETA_DEBITO','TARJETA_CREDITO','TRANSFERENCIA','NEQUI_DAVIPLATA','PAGO_MIXTO') COLLATE utf8mb4_unicode_ci DEFAULT 'EFECTIVO',
  `estado` enum('PENDIENTE','PAGADA','ANULADA','DEVUELTA') COLLATE utf8mb4_unicode_ci DEFAULT 'PAGADA',
  PRIMARY KEY (`id_venta`),
  UNIQUE KEY `numero_factura` (`numero_factura`),
  KEY `id_sucursal` (`id_sucursal`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_empleado` (`id_empleado`),
  KEY `idx_ventas_usuario` (`id_usuario`),
  CONSTRAINT `fk_ventas_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`),
  CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `ventas_ibfk_3` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `ventas` (`id_venta`, `id_sucursal`, `numero_factura`, `id_cliente`, `id_empleado`, `id_usuario`, `fecha_venta`, `subtotal`, `descuento`, `aplica_iva`, `porcentaje_iva`, `impuesto_iva`, `total`, `metodo_pago`, `estado`) VALUES
	(1, 1, 'FARM-00001', 1, 4, 4, '2026-07-27 09:10:00', 34500.00, 0.00, 1, 19.00, 6555.00, 41055.00, 'NEQUI_DAVIPLATA', 'PAGADA'),
	(2, 1, 'FARM-00002', 2, 4, 4, '2026-07-27 10:25:00', 22000.00, 0.00, 0, 19.00, 0.00, 22000.00, 'EFECTIVO', 'PAGADA'),
	(3, 1, 'FARM-20260912185350961', 1, 1, 1, '2026-09-12 18:53:51', 8500.00, 0.00, 1, 19.00, 1615.00, 10115.00, 'TARJETA_DEBITO', 'PAGADA'),
	(4, 2, 'FARM-20260912190844651', 1, 1, 1, '2026-09-12 19:08:45', 404000.00, 0.00, 1, 19.00, 76760.00, 480760.00, 'PAGO_MIXTO', 'PAGADA'),
	(5, 1, 'FARM-20260912193623389', 1, 1, 1, '2026-09-12 19:36:23', 8500.00, 0.00, 1, 19.00, 1615.00, 12615.00, 'EFECTIVO', 'PAGADA'),
	(6, 1, 'FARM-20260912220613571', 1, 1, 1, '2026-09-12 22:06:14', 8500.00, 0.00, 1, 19.00, 1615.00, 10115.00, 'EFECTIVO', 'PAGADA'),
	(7, 1, 'FARM-20260913094428841', 1, 1, 1, '2026-09-13 09:44:29', 204000.00, 0.00, 1, 19.00, 38760.00, 244760.00, 'EFECTIVO', 'PAGADA'),
	(8, 1, 'FARM-20260913113118724', 1, 2, 2, '2026-09-13 11:31:19', 8500.00, 0.00, 1, 19.00, 1615.00, 12115.00, 'EFECTIVO', 'PAGADA'),
	(9, 1, 'FARM-20260913141716144', 2, 1, 1, '2026-09-13 14:17:16', 102000.00, 0.00, 1, 19.00, 19380.00, 124380.00, 'TARJETA_DEBITO', 'PAGADA'),
	(10, 1, 'FARM-20260913175444814', 1, 1, 1, '2026-09-13 17:54:45', 8500.00, 0.00, 1, 19.00, 1615.00, 13115.00, 'EFECTIVO', 'PAGADA'),
	(11, 3, 'FARM-20260913201856708', 2, 1, 1, '2026-09-13 20:18:57', 8500.00, 0.00, 1, 19.00, 1615.00, 13115.00, 'EFECTIVO', 'PAGADA'),
	(12, 3, 'FARM-20260914090251966', NULL, 1, 1, '2026-09-14 09:02:52', 8500.00, 0.00, 1, 19.00, 1615.00, 10115.00, 'EFECTIVO', 'PAGADA'),
	(13, 3, 'FARM-20260914090416303', 1, 1, 1, '2026-09-14 09:04:16', 26000.00, 0.00, 1, 19.00, 4940.00, 33940.00, 'EFECTIVO', 'PAGADA'),
	(14, 1, 'FARM-20260914104228653', 2, 1, 1, '2026-09-14 10:42:29', 8500.00, 0.00, 1, 19.00, 1615.00, 20115.00, 'EFECTIVO', 'PAGADA'),
	(15, 3, 'FARM-20260914112911883', 1, 1, 1, '2026-09-14 11:29:12', 8500.00, 0.00, 1, 19.00, 1615.00, 12115.00, 'EFECTIVO', 'PAGADA'),
	(16, 3, 'FARM-20260914203532441', 2, 5, 5, '2026-09-14 20:35:32', 1190.00, 0.00, 1, 0.00, 190.00, 4190.00, 'EFECTIVO', 'PAGADA'),
	(17, 1, 'TEST-RPT-0001', 2, 2, 2, '2026-07-15 09:15:00', 52360.00, 0.00, 1, 19.00, 9948.40, 62308.40, 'TARJETA_DEBITO', 'PAGADA'),
	(18, 1, 'TEST-RPT-0002', 3, 3, 3, '2026-07-16 18:26:00', 92820.00, 0.00, 1, 19.00, 17635.80, 110455.80, 'TARJETA_CREDITO', 'PAGADA'),
	(19, 1, 'TEST-RPT-0003', 4, 4, 4, '2026-07-18 03:37:00', 8330.00, 0.00, 1, 19.00, 1582.70, 9912.70, 'TRANSFERENCIA', 'PAGADA'),
	(20, 1, 'TEST-RPT-0004', 5, 1, 1, '2026-07-19 03:48:00', 147560.00, 0.00, 1, 19.00, 28036.40, 175596.40, 'NEQUI_DAVIPLATA', 'PAGADA'),
	(21, 1, 'TEST-RPT-0005', 1, 2, 2, '2026-07-20 12:59:00', 196350.00, 0.00, 1, 19.00, 37306.50, 233656.50, 'PAGO_MIXTO', 'PAGADA'),
	(22, 1, 'TEST-RPT-0006', 1, 3, 3, '2026-07-21 21:20:00', 1190.00, 0.00, 0, 0.00, 0.00, 1190.00, 'EFECTIVO', 'PAGADA'),
	(23, 1, 'TEST-RPT-0007', 2, 4, 4, '2026-07-22 21:31:00', 2380.00, 0.00, 1, 19.00, 452.20, 2832.20, 'TARJETA_DEBITO', 'PAGADA'),
	(24, 1, 'TEST-RPT-0008', 3, 1, 1, '2026-07-24 06:42:00', 30345.00, 0.00, 1, 19.00, 5765.55, 36110.55, 'TARJETA_CREDITO', 'PAGADA'),
	(25, 1, 'TEST-RPT-0009', 4, 2, 2, '2026-07-25 15:53:00', 26180.00, 1309.00, 1, 19.00, 4974.20, 29845.20, 'TRANSFERENCIA', 'PAGADA'),
	(26, 1, 'TEST-RPT-0010', 5, 3, 3, '2026-07-26 16:04:00', 61880.00, 0.00, 1, 19.00, 11757.20, 73637.20, 'NEQUI_DAVIPLATA', 'DEVUELTA'),
	(27, 1, 'TEST-RPT-0011', 1, 4, 4, '2026-07-28 00:25:00', 24990.00, 0.00, 1, 19.00, 4748.10, 29738.10, 'PAGO_MIXTO', 'PAGADA'),
	(28, 1, 'TEST-RPT-0012', 1, 1, 1, '2026-07-29 09:36:00', 73780.00, 0.00, 1, 19.00, 14018.20, 87798.20, 'EFECTIVO', 'PAGADA'),
	(29, 1, 'TEST-RPT-0013', 2, 2, 2, '2026-07-30 09:47:00', 196350.00, 0.00, 1, 19.00, 37306.50, 233656.50, 'TARJETA_DEBITO', 'PAGADA'),
	(30, 1, 'TEST-RPT-0014', 3, 3, 3, '2026-07-31 18:58:00', 3570.00, 0.00, 0, 0.00, 0.00, 3570.00, 'TARJETA_CREDITO', 'PAGADA'),
	(31, 1, 'TEST-RPT-0015', 4, 4, 4, '2026-08-02 03:19:00', 1190.00, 0.00, 1, 19.00, 226.10, 1416.10, 'TRANSFERENCIA', 'PAGADA'),
	(32, 1, 'TEST-RPT-0016', 5, 1, 1, '2026-08-03 03:30:00', 20230.00, 0.00, 1, 19.00, 3843.70, 24073.70, 'NEQUI_DAVIPLATA', 'PAGADA'),
	(33, 1, 'TEST-RPT-0017', 1, 2, 2, '2026-08-04 12:41:00', 78540.00, 0.00, 1, 19.00, 14922.60, 93462.60, 'PAGO_MIXTO', 'PAGADA'),
	(34, 1, 'TEST-RPT-0018', 1, 3, 3, '2026-08-05 21:52:00', 30940.00, 1547.00, 1, 19.00, 5878.60, 35271.60, 'EFECTIVO', 'PAGADA'),
	(35, 1, 'TEST-RPT-0019', 2, 4, 4, '2026-08-06 22:03:00', 16660.00, 0.00, 1, 19.00, 3165.40, 19825.40, 'TARJETA_DEBITO', 'PAGADA'),
	(36, 1, 'TEST-RPT-0020', 3, 1, 1, '2026-08-08 06:24:00', 221340.00, 0.00, 1, 19.00, 42054.60, 263394.60, 'TARJETA_CREDITO', 'PAGADA'),
	(37, 1, 'TEST-RPT-0021', 4, 2, 2, '2026-08-09 15:35:00', 196350.00, 0.00, 1, 19.00, 37306.50, 233656.50, 'TRANSFERENCIA', 'PAGADA'),
	(38, 1, 'TEST-RPT-0022', 5, 3, 3, '2026-08-10 15:46:00', 2380.00, 0.00, 0, 0.00, 0.00, 2380.00, 'NEQUI_DAVIPLATA', 'DEVUELTA'),
	(39, 1, 'TEST-RPT-0023', 1, 4, 4, '2026-08-12 00:57:00', 3570.00, 0.00, 1, 19.00, 678.30, 4248.30, 'PAGO_MIXTO', 'PAGADA'),
	(40, 1, 'TEST-RPT-0024', 1, 1, 1, '2026-08-13 09:18:00', 10115.00, 0.00, 1, 19.00, 1921.85, 12036.85, 'EFECTIVO', 'PAGADA'),
	(41, 1, 'TEST-RPT-0025', 2, 2, 2, '2026-08-14 09:29:00', 52360.00, 0.00, 1, 19.00, 9948.40, 62308.40, 'TARJETA_DEBITO', 'PAGADA'),
	(42, 1, 'TEST-RPT-0026', 3, 3, 3, '2026-08-15 18:40:00', 92820.00, 0.00, 1, 19.00, 17635.80, 110455.80, 'TARJETA_CREDITO', 'PAGADA'),
	(43, 1, 'TEST-RPT-0027', 4, 4, 4, '2026-08-17 03:51:00', 8330.00, 416.50, 1, 19.00, 1582.70, 9496.20, 'TRANSFERENCIA', 'PAGADA'),
	(44, 1, 'TEST-RPT-0028', 5, 1, 1, '2026-08-18 04:02:00', 147560.00, 0.00, 1, 19.00, 28036.40, 175596.40, 'NEQUI_DAVIPLATA', 'PAGADA'),
	(45, 1, 'TEST-RPT-0029', 1, 2, 2, '2026-08-19 12:23:00', 196350.00, 0.00, 1, 19.00, 37306.50, 233656.50, 'PAGO_MIXTO', 'PAGADA'),
	(46, 1, 'TEST-RPT-0030', 1, 3, 3, '2026-08-20 21:34:00', 1190.00, 0.00, 0, 0.00, 0.00, 1190.00, 'EFECTIVO', 'PAGADA'),
	(47, 1, 'TEST-RPT-0031', 2, 4, 4, '2026-08-21 21:45:00', 2380.00, 0.00, 1, 19.00, 452.20, 2832.20, 'TARJETA_DEBITO', 'PAGADA'),
	(48, 1, 'TEST-RPT-0032', 3, 1, 1, '2026-08-23 06:56:00', 30345.00, 0.00, 1, 19.00, 5765.55, 36110.55, 'TARJETA_CREDITO', 'PAGADA'),
	(49, 1, 'TEST-RPT-0033', 4, 2, 2, '2026-08-24 15:17:00', 26180.00, 0.00, 1, 19.00, 4974.20, 31154.20, 'TRANSFERENCIA', 'PAGADA'),
	(50, 1, 'TEST-RPT-0034', 5, 3, 3, '2026-08-25 15:28:00', 61880.00, 0.00, 1, 19.00, 11757.20, 73637.20, 'NEQUI_DAVIPLATA', 'DEVUELTA'),
	(51, 1, 'TEST-RPT-0035', 1, 4, 4, '2026-08-27 00:39:00', 24990.00, 0.00, 1, 19.00, 4748.10, 29738.10, 'PAGO_MIXTO', 'PAGADA'),
	(52, 1, 'TEST-RPT-0036', 1, 1, 1, '2026-08-28 09:50:00', 73780.00, 3689.00, 1, 19.00, 14018.20, 84109.20, 'EFECTIVO', 'PAGADA'),
	(53, 1, 'TEST-RPT-0037', 2, 2, 2, '2026-08-29 10:01:00', 196350.00, 0.00, 1, 19.00, 37306.50, 233656.50, 'TARJETA_DEBITO', 'PAGADA'),
	(54, 1, 'TEST-RPT-0038', 3, 3, 3, '2026-08-30 18:22:00', 3570.00, 0.00, 0, 0.00, 0.00, 3570.00, 'TARJETA_CREDITO', 'PAGADA'),
	(55, 1, 'TEST-RPT-0039', 4, 4, 4, '2026-09-01 03:33:00', 1190.00, 0.00, 1, 19.00, 226.10, 1416.10, 'TRANSFERENCIA', 'PAGADA'),
	(56, 1, 'TEST-RPT-0040', 5, 1, 1, '2026-09-02 03:44:00', 20230.00, 0.00, 1, 19.00, 3843.70, 24073.70, 'NEQUI_DAVIPLATA', 'PAGADA'),
	(57, 1, 'TEST-RPT-0041', 1, 2, 2, '2026-09-03 12:55:00', 78540.00, 0.00, 1, 19.00, 14922.60, 93462.60, 'PAGO_MIXTO', 'PAGADA'),
	(58, 1, 'TEST-RPT-0042', 1, 3, 3, '2026-09-04 21:16:00', 30940.00, 0.00, 1, 19.00, 5878.60, 36818.60, 'EFECTIVO', 'PAGADA'),
	(59, 1, 'TEST-RPT-0043', 2, 4, 4, '2026-09-05 21:27:00', 16660.00, 0.00, 1, 19.00, 3165.40, 19825.40, 'TARJETA_DEBITO', 'PAGADA'),
	(60, 1, 'TEST-RPT-0044', 3, 1, 1, '2026-09-07 06:38:00', 221340.00, 0.00, 1, 19.00, 42054.60, 263394.60, 'TARJETA_CREDITO', 'PAGADA'),
	(61, 1, 'TEST-RPT-0045', 4, 2, 2, '2026-09-08 15:49:00', 196350.00, 9817.50, 1, 19.00, 37306.50, 223839.00, 'TRANSFERENCIA', 'PAGADA'),
	(62, 1, 'TEST-RPT-0046', 5, 3, 3, '2026-09-09 16:00:00', 2380.00, 0.00, 0, 0.00, 0.00, 2380.00, 'NEQUI_DAVIPLATA', 'DEVUELTA'),
	(63, 1, 'TEST-RPT-0047', 1, 4, 4, '2026-09-11 00:21:00', 3570.00, 0.00, 1, 19.00, 678.30, 4248.30, 'PAGO_MIXTO', 'DEVUELTA'),
	(64, 1, 'TEST-RPT-0048', 1, 1, 1, '2026-09-12 09:32:00', 10115.00, 0.00, 1, 19.00, 1921.85, 12036.85, 'EFECTIVO', 'PAGADA');

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_asignar_proveedor_habitual_detalle_compra` AFTER INSERT ON `detalles_compras` FOR EACH ROW BEGIN
    UPDATE productos p
    JOIN compras c ON c.id_compra=NEW.id_compra
    SET p.id_proveedor=c.id_proveedor
    WHERE p.id_producto=NEW.id_producto
      AND p.id_proveedor IS NULL
      AND c.estado<>'CANCELADA';
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_productos_codigo_interno_auto` BEFORE INSERT ON `productos` FOR EACH ROW BEGIN
    IF NEW.codigo_interno IS NULL OR TRIM(NEW.codigo_interno)='' THEN
        SET NEW.codigo_interno=CONCAT('PROD-',UPPER(SUBSTRING(REPLACE(UUID(),'-',''),1,12)));
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;


-- =====================================================================
-- DATOS DE DEMOSTRACION / PRUEBAS FUNCIONALES
-- NO MODIFICA EMPLEADOS, USUARIOS, ROLES, PERMISOS NI ACCESOS.
-- Las fechas se calculan respecto al día en que se ejecuta este script.
-- Evidencias de prueba = NULL para no depender de archivos físicos.
-- =====================================================================

START TRANSACTION;

-- Clientes adicionales para búsquedas y ventas de prueba
INSERT INTO `clientes`
(`id_cliente`,`nombre_completo`,`tipo_documento`,`numero_documento`,`telefono`,`direccion`,`correo`,`fecha_nacimiento`,`eps`,`alergias`,`fecha_registro`)
VALUES
(101,'Valentina Torres Mendoza','CC','TEST100001','3005550101','Cra 30 # 20-15','valentina.test@pillone.local','1997-05-11','Sura','Ninguna',NOW()),
(102,'Miguel Ángel Castro','CC','TEST100002','3005550102','Calle 18 # 11-42','miguel.test@pillone.local','1984-10-02','Nueva EPS','Ninguna',NOW()),
(103,'Laura Sofía Ramírez','CC','TEST100003','3005550103','Calle 27 # 24-08','laura.test@pillone.local','1992-01-20','Sanitas','Penicilina',NOW());

-- ---------------------------------------------------------------------
-- VENTAS: hoy, 1 día, 3 días, 7 días, 15 días, 30 días y 45 días atrás.
-- Los subtotales son antes de IVA; impuesto_iva contiene el IVA.
-- Cuando hay domicilio, el costo del domicilio está incluido en total.
-- ---------------------------------------------------------------------
INSERT INTO `ventas`
(`id_venta`,`id_sucursal`,`numero_factura`,`id_cliente`,`id_empleado`,`id_usuario`,
 `fecha_venta`,`subtotal`,`descuento`,`aplica_iva`,`porcentaje_iva`,`impuesto_iva`,
 `total`,`metodo_pago`,`estado`)
VALUES
-- HOY
(1001,1,'DEMO-HOY-001',101,1,1,DATE_ADD(CURDATE(),INTERVAL 8 HOUR),25500.00,0.00,1,19.00,4845.00,30345.00,'EFECTIVO','PAGADA'),
(1002,1,'DEMO-HOY-002',102,2,2,DATE_ADD(CURDATE(),INTERVAL 9 HOUR),44000.00,0.00,1,19.00,8360.00,56360.00,'TARJETA_DEBITO','PAGADA'),
(1003,1,'DEMO-HOY-003',103,1,1,DATE_ADD(CURDATE(),INTERVAL 10 HOUR),78000.00,0.00,1,19.00,14820.00,97820.00,'NEQUI_DAVIPLATA','PAGADA'),
(1004,1,'DEMO-HOY-004',101,2,2,DATE_ADD(CURDATE(),INTERVAL 11 HOUR),21000.00,0.00,1,19.00,3990.00,28990.00,'EFECTIVO','PAGADA'),
(1005,1,'DEMO-HOY-005',102,1,1,DATE_ADD(CURDATE(),INTERVAL 12 HOUR),62000.00,0.00,1,19.00,11780.00,77780.00,'TRANSFERENCIA','DEVUELTA'),
(1006,1,'DEMO-HOY-006',103,1,1,DATE_ADD(CURDATE(),INTERVAL 13 HOUR),165000.00,5000.00,1,19.00,31350.00,191350.00,'PAGO_MIXTO','PAGADA'),
-- AYER
(1007,1,'DEMO-AYER-001',101,2,2,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 1 DAY),INTERVAL 10 HOUR),52000.00,0.00,1,19.00,9880.00,61880.00,'TARJETA_CREDITO','PAGADA'),
(1008,1,'DEMO-AYER-002',102,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 1 DAY),INTERVAL 16 HOUR),14000.00,0.00,1,19.00,2660.00,19660.00,'EFECTIVO','PAGADA'),
-- HACE 3 DÍAS
(1009,1,'DEMO-3D-001',103,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 9 HOUR),85000.00,0.00,1,19.00,16150.00,101150.00,'NEQUI_DAVIPLATA','PAGADA'),
(1010,1,'DEMO-3D-002',101,2,2,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 14 HOUR),22000.00,0.00,1,19.00,4180.00,30180.00,'EFECTIVO','PAGADA'),
(1011,1,'DEMO-3D-003',102,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 18 HOUR),124000.00,6200.00,1,19.00,23560.00,141360.00,'TRANSFERENCIA','PAGADA'),
-- HACE 7 DÍAS
(1012,1,'DEMO-7D-001',101,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 7 DAY),INTERVAL 11 HOUR),330000.00,0.00,1,19.00,62700.00,392700.00,'TARJETA_CREDITO','PAGADA'),
(1013,1,'DEMO-7D-002',103,2,2,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 7 DAY),INTERVAL 15 HOUR),26000.00,0.00,1,19.00,4940.00,34940.00,'EFECTIVO','PAGADA'),
-- HACE 15 DÍAS
(1014,1,'DEMO-15D-001',102,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 10 HOUR),186000.00,0.00,1,19.00,35340.00,221340.00,'PAGO_MIXTO','PAGADA'),
(1015,1,'DEMO-15D-002',101,2,2,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 17 HOUR),42000.00,0.00,1,19.00,7980.00,49980.00,'TARJETA_DEBITO','DEVUELTA'),
-- HACE 30 DÍAS
(1016,1,'DEMO-30D-001',103,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 30 DAY),INTERVAL 9 HOUR),110000.00,0.00,1,19.00,20900.00,130900.00,'TRANSFERENCIA','PAGADA'),
(1017,1,'DEMO-30D-002',102,2,2,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 30 DAY),INTERVAL 13 HOUR),68000.00,3400.00,1,19.00,12920.00,77520.00,'NEQUI_DAVIPLATA','PAGADA'),
-- HACE 45 DÍAS
(1018,1,'DEMO-45D-001',101,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 45 DAY),INTERVAL 10 HOUR),248000.00,0.00,1,19.00,47120.00,295120.00,'EFECTIVO','PAGADA'),
(1019,1,'DEMO-45D-002',103,2,2,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 45 DAY),INTERVAL 16 HOUR),28000.00,0.00,1,19.00,5320.00,33320.00,'TARJETA_DEBITO','PAGADA'),
-- Estados que no deben sumar como venta efectiva
(1020,1,'DEMO-PENDIENTE-001',102,1,1,DATE_ADD(CURDATE(),INTERVAL 14 HOUR),7000.00,0.00,1,19.00,1330.00,8330.00,'EFECTIVO','PENDIENTE'),
(1021,1,'DEMO-ANULADA-001',101,1,1,DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 12 HOUR),26000.00,0.00,1,19.00,4940.00,30940.00,'EFECTIVO','ANULADA');

-- Detalles de las ventas anteriores
INSERT INTO `detalles_ventas`
(`id_detalle_venta`,`id_venta`,`id_producto`,`id_formula`,`id_lote`,`tipo_venta`,
 `cantidad`,`unidades_descontadas`,`precio_unitario`,`porcentaje_iva`,`subtotal`)
VALUES
(2001,1001,1,NULL,1,'UNIDAD',3,3,8500.00,19.00,25500.00),
(2002,1002,2,NULL,3,'UNIDAD',2,2,22000.00,19.00,44000.00),
(2003,1003,3,NULL,4,'UNIDAD',3,3,26000.00,19.00,78000.00),
(2004,1004,4,NULL,5,'UNIDAD',3,3,7000.00,19.00,21000.00),
(2005,1005,5,NULL,6,'UNIDAD',1,1,62000.00,19.00,62000.00),
(2006,1006,6,NULL,7,'UNIDAD',1,1,165000.00,19.00,165000.00),
(2007,1007,3,NULL,4,'UNIDAD',2,2,26000.00,19.00,52000.00),
(2008,1008,4,NULL,5,'UNIDAD',2,2,7000.00,19.00,14000.00),
(2009,1009,1,NULL,1,'UNIDAD',10,10,8500.00,19.00,85000.00),
(2010,1010,2,NULL,3,'UNIDAD',1,1,22000.00,19.00,22000.00),
(2011,1011,5,NULL,6,'UNIDAD',2,2,62000.00,19.00,124000.00),
(2012,1012,6,NULL,7,'UNIDAD',2,2,165000.00,19.00,330000.00),
(2013,1013,3,NULL,4,'UNIDAD',1,1,26000.00,19.00,26000.00),
(2014,1014,5,NULL,6,'UNIDAD',3,3,62000.00,19.00,186000.00),
(2015,1015,4,NULL,5,'UNIDAD',6,6,7000.00,19.00,42000.00),
(2016,1016,2,NULL,3,'UNIDAD',5,5,22000.00,19.00,110000.00),
(2017,1017,1,NULL,1,'UNIDAD',8,8,8500.00,19.00,68000.00),
(2018,1018,5,NULL,6,'UNIDAD',4,4,62000.00,19.00,248000.00),
(2019,1019,4,NULL,5,'UNIDAD',4,4,7000.00,19.00,28000.00),
(2020,1020,4,NULL,5,'UNIDAD',1,1,7000.00,19.00,7000.00),
(2021,1021,3,NULL,4,'UNIDAD',1,1,26000.00,19.00,26000.00);

-- ---------------------------------------------------------------------
-- DOMICILIOS: mezcla de todos los estados para probar el módulo.
-- ---------------------------------------------------------------------
INSERT INTO `domicilios`
(`id_domicilio`,`id_venta`,`id_cliente`,`direccion_entrega`,`telefono_contacto`,
 `nombre_domiciliario`,`costo_domicilio`,`pagado`,`fecha_pago`,`estado`,
 `fecha_hora_salida`,`fecha_hora_entrega`,`evidencia_entrega`,`observaciones_entrega`)
VALUES
(101,1002,102,'Cra 30 # 20-15','3005550102','Domiciliario Demo',4000.00,1,
 DATE_ADD(CURDATE(),INTERVAL 10 HOUR),'ENTREGADO',
 DATE_ADD(CURDATE(),INTERVAL 9 HOUR),DATE_ADD(CURDATE(),INTERVAL 10 HOUR),NULL,
 'Entrega de prueba completada'),
(102,1004,101,'Calle 18 # 11-42','3005550101','Domiciliario Demo',4000.00,0,
 NULL,'EN_CAMINO',DATE_ADD(CURDATE(),INTERVAL 11 HOUR),NULL,NULL,
 'Tiene devolución parcial: aún queda mercancía pendiente'),
(103,1005,102,'Calle 27 # 24-08','3005550102','Domiciliario Demo',4000.00,0,
 NULL,'CANCELADO',DATE_ADD(CURDATE(),INTERVAL 12 HOUR),NULL,NULL,
 'Domicilio devuelto completamente'),
(104,1010,101,'Cra 30 # 20-15','3005550101',NULL,4000.00,0,
 NULL,'PENDIENTE',NULL,NULL,NULL,'Domicilio pendiente de prueba'),
(105,1013,103,'Calle 27 # 24-08','3005550103','Domiciliario Demo',4000.00,0,
 NULL,'EN_PREPARACION',NULL,NULL,NULL,'Domicilio en preparación'),
(106,1014,102,'Calle 18 # 11-42','3005550102','Domiciliario Demo',0.00,1,
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 12 HOUR),'ENTREGADO',
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 11 HOUR),
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 12 HOUR),NULL,
 'Entrega histórica de prueba'),
(107,1015,101,'Cra 30 # 20-15','3005550101','Domiciliario Demo',0.00,0,
 NULL,'CANCELADO',
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 17 HOUR),NULL,NULL,
 'Domicilio histórico devuelto completamente'),
(108,1017,102,'Calle 18 # 11-42','3005550102','Domiciliario Demo',0.00,1,
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 30 DAY),INTERVAL 15 HOUR),'ENTREGADO',
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 30 DAY),INTERVAL 14 HOUR),
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 30 DAY),INTERVAL 15 HOUR),NULL,
 'Entrega de hace un mes');

-- ---------------------------------------------------------------------
-- DEVOLUCIONES:
--  - parcial hoy: venta 1004 sigue PAGADA / domicilio EN_CAMINO
--  - total hoy: venta 1005 DEVUELTA / domicilio CANCELADO
--  - parcial hace 3 días
--  - total hace 15 días
--  - parcial hace 30 días
--  - devolución a proveedor, que NO debe descontar ventas de clientes
-- ---------------------------------------------------------------------
INSERT INTO `devoluciones`
(`id_devolucion`,`id_sucursal`,`tipo_devolucion`,`id_venta`,`id_compra`,
 `id_producto`,`id_lote`,`cantidad`,`motivo`,`estado_producto`,`fecha_devolucion`,
 `id_usuario_regente`,`observaciones`,`evidencia_devolucion`)
VALUES
(101,1,'CLIENTE',1004,NULL,4,5,1,'ERROR_ENTREGA','APTO_PARA_REINGRESO',
 DATE_ADD(CURDATE(),INTERVAL 12 HOUR),1,
 'PRUEBA: devolución parcial de 1 de 3 unidades; el domicilio debe verse como DEVOLUCIÓN PARCIAL',NULL),
(102,1,'CLIENTE',1005,NULL,5,6,1,'ERROR_ENTREGA','APTO_PARA_REINGRESO',
 DATE_ADD(CURDATE(),INTERVAL 13 HOUR),1,
 'PRUEBA: devolución total; venta DEVUELTA y domicilio CANCELADO',NULL),
(103,1,'CLIENTE',1009,NULL,1,1,2,'PRODUCTO_DEFECTUOSO','DESECHADO',
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 16 HOUR),1,
 'PRUEBA: devolución parcial histórica; producto desechado',NULL),
(104,1,'CLIENTE',1015,NULL,4,5,6,'ERROR_ENTREGA','APTO_PARA_REINGRESO',
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 18 HOUR),1,
 'PRUEBA: devolución total histórica',NULL),
(105,1,'CLIENTE',1016,NULL,2,3,1,'PRODUCTO_DEFECTUOSO','DESECHADO',
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 30 DAY),INTERVAL 15 HOUR),1,
 'PRUEBA: devolución parcial de hace un mes',NULL),
(106,1,'PROVEEDOR',NULL,1,1,1,3,'PRODUCTO_DEFECTUOSO','DEVUELTO_A_FABRICA',
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 7 DAY),INTERVAL 14 HOUR),1,
 'PRUEBA: devolución a proveedor; no debe disminuir ventas de clientes',NULL);

-- Movimientos de inventario correspondientes a las devoluciones aptas de prueba.
-- Son históricos/informativos y no cambian usuarios ni accesos.
INSERT INTO `movimientos_inventario`
(`id_movimiento`,`id_sucursal`,`tipo_movimiento`,`id_producto`,`id_lote`,`cantidad`,
 `existencia_anterior`,`nueva_existencia`,`fecha_movimiento`,`id_usuario`,`motivo`)
VALUES
(101,1,'DEVOLUCION_CLIENTE',4,5,1,80,81,
 DATE_ADD(CURDATE(),INTERVAL 12 HOUR),1,'PRUEBA: reingreso devolución parcial venta DEMO-HOY-004'),
(102,1,'DEVOLUCION_CLIENTE',5,6,1,25,26,
 DATE_ADD(CURDATE(),INTERVAL 13 HOUR),1,'PRUEBA: reingreso devolución total venta DEMO-HOY-005'),
(103,1,'DEVOLUCION_CLIENTE',4,5,6,74,80,
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 15 DAY),INTERVAL 18 HOUR),1,'PRUEBA: reingreso devolución total histórica'),
(104,1,'DEVOLUCION_PROVEEDOR',1,1,3,89,86,
 DATE_ADD(DATE_SUB(CURDATE(),INTERVAL 7 DAY),INTERVAL 14 HOUR),1,'PRUEBA: devolución a proveedor');

COMMIT;

-- Ajustar AUTO_INCREMENT después de los IDs reservados para demostración.
ALTER TABLE `clientes` AUTO_INCREMENT=104;
ALTER TABLE `ventas` AUTO_INCREMENT=1022;
ALTER TABLE `detalles_ventas` AUTO_INCREMENT=2022;
ALTER TABLE `domicilios` AUTO_INCREMENT=109;
ALTER TABLE `devoluciones` AUTO_INCREMENT=107;
ALTER TABLE `movimientos_inventario` AUTO_INCREMENT=105;

-- Verificaciones rápidas para HeidiSQL
SELECT 'VENTAS DEMO' AS bloque, COUNT(*) AS registros
FROM ventas WHERE numero_factura LIKE 'DEMO-%';

SELECT 'DEVOLUCIONES DEMO' AS bloque, COUNT(*) AS registros,
       SUM(tipo_devolucion='CLIENTE') AS cliente,
       SUM(tipo_devolucion='PROVEEDOR') AS proveedor
FROM devoluciones WHERE id_devolucion BETWEEN 101 AND 106;

SELECT 'DOMICILIOS DEMO' AS bloque, estado, COUNT(*) AS cantidad
FROM domicilios WHERE id_domicilio BETWEEN 101 AND 108
GROUP BY estado
ORDER BY estado;


/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
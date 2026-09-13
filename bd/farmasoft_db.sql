/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_compra`),
  KEY `id_compra` (`id_compra`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `detalles_compras_ibfk_1` FOREIGN KEY (`id_compra`) REFERENCES `compras` (`id_compra`) ON DELETE CASCADE,
  CONSTRAINT `detalles_compras_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
  `id_lote` int NOT NULL,
  `tipo_venta` enum('UNIDAD','SELLO','CAJA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNIDAD',
  `cantidad` int NOT NULL,
  `unidades_descontadas` int NOT NULL DEFAULT '1',
  `precio_unitario` decimal(12,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_venta`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`),
  KEY `id_lote` (`id_lote`),
  CONSTRAINT `detalles_ventas_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`) ON DELETE CASCADE,
  CONSTRAINT `detalles_ventas_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `detalles_ventas_ibfk_3` FOREIGN KEY (`id_lote`) REFERENCES `lotes` (`id_lote`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `detalles_ventas` (`id_detalle_venta`, `id_venta`, `id_producto`, `id_lote`, `tipo_venta`, `cantidad`, `unidades_descontadas`, `precio_unitario`, `subtotal`) VALUES
	(1, 1, 1, 1, 'UNIDAD', 1, 1, 8500.00, 8500.00),
	(2, 1, 3, 4, 'UNIDAD', 1, 1, 26000.00, 26000.00),
	(3, 2, 2, 3, 'UNIDAD', 1, 1, 22000.00, 22000.00),
	(4, 3, 1, 1, 'UNIDAD', 1, 1, 8500.00, 8500.00),
	(5, 4, 1, 1, 'SELLO', 1, 12, 102000.00, 102000.00),
	(6, 4, 2, 3, 'UNIDAD', 1, 1, 22000.00, 22000.00),
	(7, 4, 4, 5, 'SELLO', 4, 40, 70000.00, 280000.00),
	(8, 5, 1, 1, 'UNIDAD', 1, 1, 8500.00, 8500.00),
	(9, 6, 1, 13, 'UNIDAD', 1, 1, 8500.00, 8500.00),
	(10, 7, 1, 11, 'SELLO', 2, 24, 102000.00, 204000.00),
	(11, 8, 1, 11, 'UNIDAD', 1, 1, 8500.00, 8500.00);

CREATE TABLE IF NOT EXISTS `devoluciones` (
  `id_devolucion` int NOT NULL AUTO_INCREMENT,
  `id_sucursal` int NOT NULL,
  `tipo_devolucion` enum('CLIENTE','PROVEEDOR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_venta` int DEFAULT NULL,
  `id_compra` int DEFAULT NULL,
  `id_producto` int NOT NULL,
  `cantidad` int NOT NULL,
  `motivo` enum('PRODUCTO_DEFECTUOSO','ERROR_ENTREGA','PROXIMO_A_VENCER','VENCIDO','RETIRO_MERCADO','EMPAQUE_DANADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado_producto` enum('APTO_PARA_REINGRESO','DESECHADO','DEVUELTO_A_FABRICA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_devolucion` datetime DEFAULT CURRENT_TIMESTAMP,
  `id_usuario_regente` int NOT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_devolucion`),
  KEY `id_sucursal` (`id_sucursal`),
  KEY `id_venta` (`id_venta`),
  KEY `id_compra` (`id_compra`),
  KEY `id_producto` (`id_producto`),
  KEY `id_usuario_regente` (`id_usuario_regente`),
  CONSTRAINT `devoluciones_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`),
  CONSTRAINT `devoluciones_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `devoluciones_ibfk_3` FOREIGN KEY (`id_compra`) REFERENCES `compras` (`id_compra`),
  CONSTRAINT `devoluciones_ibfk_4` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `devoluciones_ibfk_5` FOREIGN KEY (`id_usuario_regente`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `domicilios` (`id_domicilio`, `id_venta`, `id_cliente`, `direccion_entrega`, `telefono_contacto`, `nombre_domiciliario`, `costo_domicilio`, `pagado`, `fecha_pago`, `estado`, `fecha_hora_salida`, `fecha_hora_entrega`, `evidencia_entrega`, `observaciones_entrega`) VALUES
	(1, 1, 1, 'Calle 25 # 14-30 Apt 302', '3157894512', 'Marmato Motos Express', 4000.00, 0, NULL, 'ENTREGADO', NULL, NULL, NULL, NULL),
	(2, 5, 1, 'Calle 25 # 14-30', '3157894512', 'Javier', 2500.00, 0, NULL, 'PENDIENTE', NULL, NULL, NULL, NULL),
	(3, 6, 1, 'Calle 25 # 14-30', '3157894512', NULL, 0.00, 0, NULL, 'PENDIENTE', NULL, NULL, NULL, NULL),
	(4, 7, 1, 'Calle 25 # 14-30', '3157894512', 'Diego', 2000.00, 1, '2026-09-13 09:45:40', 'ENTREGADO', NULL, '2026-09-13 09:45:40', '/uploads/domicilios/domicilio_4_1789310740416.png', 'blablabla'),
	(5, 8, 1, 'Calle 25 # 14-30', '3157894512', 'Digo', 2000.00, 0, NULL, 'PENDIENTE', NULL, NULL, NULL, NULL);

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
  KEY `id_sucursal` (`id_sucursal`),
  CONSTRAINT `empleados_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `empleados` (`id_empleado`, `id_sucursal`, `nombre_completo`, `tipo_documento`, `numero_documento`, `telefono`, `direccion`, `correo`, `cargo`, `salario`, `estado`) VALUES
	(1, 1, 'Luz Marina Bermúdez', 'CC', '31892014', '3115678901', 'Calle 12 # 4-15', 'luz.bermudez@farmasoft.com', 'Administrador General', 4500000.00, 'ACTIVO'),
	(2, 1, 'Jonathan Smith Pérez', 'CC', '1115938201', '3174561230', 'Cra 15 # 22-08', 'jonathan.perez@farmasoft.com', 'Regente de Farmacia', 3200000.00, 'ACTIVO'),
	(3, 1, 'Sandra Patricia Osorio', 'CC', '66982014', '3148901234', 'Calle 40 # 18-90', 'sandra.osorio@farmasoft.com', 'Auxiliar de Farmacia', 1800000.00, 'ACTIVO'),
	(4, 1, 'Kevin Alexis Quintero', 'CC', '1112938471', '3201237894', 'Cra 27 # 8-33', 'kevin.quintero@farmasoft.com', 'Vendedor / Cajero', 1600000.00, 'ACTIVO'),
	(5, 1, 'Diego Alejandro Olaya Gonzalez', 'CC', '1117352098', '3228991339', 'Carrera 27a # 10 a 04', 'diealeolagon@gmail.com', 'Jefe', 10000000.00, 'ACTIVO');

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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `lotes` (`id_lote`, `id_producto`, `numero_lote`, `fecha_fabricacion`, `fecha_vencimiento`, `cantidad_inicial`, `cantidad_actual`, `estado`) VALUES
	(1, 1, 'LOTE-DOL-2026-A', '2026-09-01', '2027-09-30', 100, 86, 'DISPONIBLE'),
	(2, 1, 'LOTE-DOL-2026-B', '2026-09-01', '2027-10-31', 50, 50, 'DISPONIBLE'),
	(3, 2, 'LOTE-AMX-2026-A', '2026-09-01', '2027-11-30', 80, 79, 'DISPONIBLE'),
	(4, 3, 'LOTE-APR-2026-A', '2026-09-01', '2027-12-31', 45, 45, 'DISPONIBLE'),
	(5, 4, 'LOTE-LOR-2026-A', '2026-09-01', '2027-12-31', 120, 80, 'DISPONIBLE'),
	(6, 5, 'LOTE-UMB-2026-A', '2026-09-01', '2027-12-31', 25, 25, 'DISPONIBLE'),
	(7, 6, 'LOTE-INS-2026-A', '2026-09-01', '2027-12-31', 12, 12, 'DISPONIBLE'),
	(8, 1, '3', '2026-09-25', '2027-09-18', 100, 100, 'DISPONIBLE'),
	(9, 8, 'diego', '2026-09-30', '2027-06-11', 100, 100, 'DISPONIBLE'),
	(10, 14, 'lote b', '2026-09-03', '2026-10-28', 4000, 4000, 'PROXIMO_A_VENCER'),
	(11, 1, 'lote 1', '2026-09-12', '2026-09-13', 2400, 2375, 'PROXIMO_A_VENCER'),
	(12, 3, 'lote 3', '2026-09-12', '2026-09-25', 100, 100, 'PROXIMO_A_VENCER'),
	(13, 1, 'lote c', '2026-09-12', '2026-09-12', 600, 599, 'VENCIDO'),
	(23, 5, 'TEST-VENCIDO-005', '2025-04-10', '2026-04-10', 60, 40, 'VENCIDO'),
	(24, 8, 'lote 10', '2026-09-13', '2026-09-30', 8280, 8280, 'PROXIMO_A_VENCER'),
	(25, 15, 'lote 0111', '2026-09-13', '2026-09-30', 4000, 4000, 'PROXIMO_A_VENCER');

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `movimientos_inventario` (`id_movimiento`, `id_sucursal`, `tipo_movimiento`, `id_producto`, `id_lote`, `cantidad`, `existencia_anterior`, `nueva_existencia`, `fecha_movimiento`, `id_usuario`, `motivo`) VALUES
	(1, 1, 'ENTRADA_COMPRA', 1, 1, 100, 0, 100, '2026-09-12 17:51:21', 1, 'Carga inicial por compra FAC-TQ-90812'),
	(2, 1, 'SALIDA_VENTA', 1, 1, 1, 100, 99, '2026-09-12 17:51:21', 4, 'Venta según Factura FARM-00001');

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
  `id_proveedor` int NOT NULL,
  `precio_compra` decimal(12,2) NOT NULL,
  `precio_venta` decimal(12,2) NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `productos` (`id_producto`, `codigo_interno`, `codigo_barras`, `nombre_comercial`, `nombre_generico`, `descripcion`, `presentacion`, `concentracion`, `laboratorio`, `registro_invima`, `id_categoria`, `id_proveedor`, `precio_compra`, `precio_venta`, `precio_venta_tableta`, `unidades_por_empaque`, `sellos_por_caja`, `unidades_por_sello`, `precio_compra_empaque`, `precio_venta_empaque`, `stock_total`, `stock_minimo`, `ubicacion_estante`, `requiere_formula`, `es_venta_libre`, `es_controlado`, `requiere_refrigeracion`, `restricciones_venta`, `estado`) VALUES
	(1, 'PROD-001', '770200100101', 'Dolex Forte', 'Acetaminofén + Cafeína', 'Analgésico y antipirético para dolores intensos', 'Caja x 12 Tabletas', '500mg / 65mg', 'GSK / TQ', 'INVIMA 2018M-0001234', 1, 3, 4500.00, 8500.00, NULL, 12, 1, 12, 50000.00, 95000.00, 2611, 20, 'Estante A1', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(2, 'PROD-002', '770200100102', 'Amoxicilina Genfar', 'Amoxicilina', 'Antibiótico bactericida de amplio espectro', 'Caja x 50 Cápsulas', '500 mg', 'Genfar', 'INVIMA 2020M-0015678', 2, 2, 12000.00, 22000.00, NULL, 50, 1, 50, 550000.00, 1000000.00, 79, 15, 'Estante B2 (Restringido)', 1, 0, 0, 0, NULL, 'ACTIVO'),
	(3, 'PROD-003', '770200100103', 'Apronax', 'Naproxeno Sódico', 'Antiinflamatorio no esteroideo de alivio prolongado', 'Caja x 10 Tabletas', '550 mg', 'Bayer', 'INVIMA 2019M-0009876', 1, 4, 15000.00, 26000.00, NULL, 10, 1, 10, 140000.00, 240000.00, 145, 10, 'Estante A2', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(4, 'PROD-004', '770200100104', 'Loratadina Procaps', 'Loratadina', 'Antihistamínico no sedante', 'Caja x 10 Tabletas', '10 mg', 'Procaps', 'INVIMA 2021M-0011223', 3, 1, 3000.00, 7000.00, NULL, 10, 1, 10, 28000.00, 65000.00, 80, 15, 'Estante C1', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(5, 'PROD-005', '770200100105', 'Bloqueador Umbrela Urban', 'Protector Solar FPS 50+', 'Protección dermatológica contra rayos UV y luz azul', 'Frasco x 50 ml', 'FPS 50+', 'Medihealth / TQ', 'NSOC12345-22CO', 6, 3, 38000.00, 62000.00, NULL, 1, 1, 1, 38000.00, 62000.00, 25, 5, 'Vitrina Dermocosmética', 0, 1, 0, 0, NULL, 'ACTIVO'),
	(6, 'PROD-006', '770200100106', 'Insulina Lantus', 'Insulina Glargina', 'Insulina de acción prolongada', 'Caja x 5 Plumas soloSTAR 3ml', '100 UI/ml', 'Sanofi', 'INVIMA 2017M-0004512', 2, 1, 110000.00, 165000.00, NULL, 5, 1, 5, 520000.00, 800000.00, 12, 5, 'Nevera Principal (2-8°C)', 1, 0, 0, 1, NULL, 'ACTIVO'),
	(8, '001', '1272877382', 'amoxicilina', 'amoxicilina', 'm', 'caja por 200', '500mg', 'genfar', '11877823', 6, 4, 1.00, 2.00, NULL, 276, 12, 23, 12.00, 37.00, 8380, 10, 'pacillo 1', 0, 1, 0, 0, 'a', 'ACTIVO'),
	(14, '00101', '12728773823', 'amoxicilina', 'amoxicilina', '1', 'caja por 200', '500mg', 'genfar', '118778231', 7, 4, 10.00, 20.00, NULL, 200, 10, 20, 300.00, 400.00, 4000, 10, 'pacillo 1', 0, 1, 0, 0, '', 'ACTIVO'),
	(15, 'codigo1', 'codigoDeBarras', 'Clonacepam', 'Clonacepam', 'aaaaaaaa', 'caja 10 tabletas', '500mg', 'Genfar', '199132registro', 7, 1, 10.00, 20.00, 100.00, 100, 10, 10, 200.00, 300.00, 4000, 50, 'estante 2', 0, 1, 0, 0, 'ninguna', 'ACTIVO');

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `roles` (`id_rol`, `nombre`) VALUES
	(1, 'ADMINISTRADOR'),
	(2, 'FARMACEUTICO_REGENTE'),
	(3, 'VENDEDOR'),
	(4, 'AUXILIAR_FARMACIA');

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `usuarios` (`id_usuario`, `id_empleado`, `id_rol`, `username`, `password_hash`, `estado`) VALUES
	(1, 1, 1, 'admin', '$2a$10$wT3yKkXG1z/9W8Z3E9s2.e.yG9v5uL7Qf1u1u1u1u1u1u1u1u1u1u', 'ACTIVO'),
	(2, 2, 2, 'regente01', '$2a$10$wT3yKkXG1z/9W8Z3E9s2.e.yG9v5uL7Qf1u1u1u1u1u1u1u1u1u1u', 'ACTIVO'),
	(3, 3, 4, 'sandra.aux', '$2a$10$wT3yKkXG1z/9W8Z3E9s2.e.yG9v5uL7Qf1u1u1u1u1u1u1u1u1u1u', 'ACTIVO'),
	(4, 4, 3, 'kevin.vendedor', '$2a$10$wT3yKkXG1z/9W8Z3E9s2.e.yG9v5uL7Qf1u1u1u1u1u1u1u1u1u1u', 'ACTIVO'),
	(5, 5, 1, 'Diego_Olaya', 'PBKDF2$210000$n5oldGBLtj0InehPnCA98w==$eJTaQEx5PiNfw/fDc1kbGZvnI+r6wE9EBA0GbZC8+ng=', 'ACTIVO');

CREATE TABLE IF NOT EXISTS `ventas` (
  `id_venta` int NOT NULL AUTO_INCREMENT,
  `id_sucursal` int NOT NULL,
  `numero_factura` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_cliente` int DEFAULT NULL,
  `id_empleado` int NOT NULL,
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
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`),
  CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `ventas_ibfk_3` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `ventas` (`id_venta`, `id_sucursal`, `numero_factura`, `id_cliente`, `id_empleado`, `fecha_venta`, `subtotal`, `descuento`, `aplica_iva`, `porcentaje_iva`, `impuesto_iva`, `total`, `metodo_pago`, `estado`) VALUES
	(1, 1, 'FARM-00001', 1, 4, '2026-07-27 09:10:00', 34500.00, 0.00, 1, 19.00, 6555.00, 41055.00, 'NEQUI_DAVIPLATA', 'PAGADA'),
	(2, 1, 'FARM-00002', 2, 4, '2026-07-27 10:25:00', 22000.00, 0.00, 0, 19.00, 0.00, 22000.00, 'EFECTIVO', 'PAGADA'),
	(3, 1, 'FARM-20260912185350961', 1, 1, '2026-09-12 18:53:51', 8500.00, 0.00, 1, 19.00, 1615.00, 10115.00, 'TARJETA_DEBITO', 'PAGADA'),
	(4, 2, 'FARM-20260912190844651', 1, 1, '2026-09-12 19:08:45', 404000.00, 0.00, 1, 19.00, 76760.00, 480760.00, 'PAGO_MIXTO', 'PAGADA'),
	(5, 1, 'FARM-20260912193623389', 1, 1, '2026-09-12 19:36:23', 8500.00, 0.00, 1, 19.00, 1615.00, 12615.00, 'EFECTIVO', 'PAGADA'),
	(6, 1, 'FARM-20260912220613571', 1, 1, '2026-09-12 22:06:14', 8500.00, 0.00, 1, 19.00, 1615.00, 10115.00, 'EFECTIVO', 'PAGADA'),
	(7, 1, 'FARM-20260913094428841', 1, 1, '2026-09-13 09:44:29', 204000.00, 0.00, 1, 19.00, 38760.00, 244760.00, 'EFECTIVO', 'PAGADA'),
	(8, 1, 'FARM-20260913113118724', 1, 2, '2026-09-13 11:31:19', 8500.00, 0.00, 1, 19.00, 1615.00, 12115.00, 'EFECTIVO', 'PAGADA');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

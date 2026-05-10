-- Usuarios
INSERT INTO users (username, password_hash, role, created_at) VALUES ('admin', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'ADMIN', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp1', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp2', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp3', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp4', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp5', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp6', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp7', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp8', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
INSERT INTO users (username, password_hash, role, created_at) VALUES ('emp9', '$2a$10$X.f/X8LzX.N2h5rRj/X8LzX.N2h5rRj', 'USER', CURRENT_TIMESTAMP);
--Inserts de clientes
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('12345678A', 'Juan Pérez', '600111222', 'juan.perez@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('23456789B', 'María García', '600222333', 'maria.garcia@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('34567890C', 'Carlos Rodríguez', '600333444', 'carlos.rod@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('45678901D', 'Ana Martínez', '600444555', 'ana.mtz@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('56789012E', 'Luis López', '600555666', 'luis.lopez@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('67890123F', 'Elena Sánchez', '600666777', 'elena.sanchez@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('78901234G', 'Javier Gómez', '600777888', 'javier.gomez@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('89012345H', 'Lucía Díaz', '600888999', 'lucia.diaz@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('90123456I', 'Diego Torres', '600999000', 'diego.torres@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('01234567J', 'Marta Ruiz', '611111222', 'marta.ruiz@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('11223344K', 'Sergio Hernández', '611222333', 'sergio.h@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('22334455L', 'Paula Jiménez', '611333444', 'paula.j@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('33445566M', 'Andrés Moreno', '611444555', 'andres.moreno@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('44556677N', 'Sara Muñoz', '611555666', 'sara.munoz@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('55667788Ñ', 'Fernando Romero', '611666777', 'fer.romero@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('66778899O', 'Raquel Alonso', '611777888', 'raquel.alonso@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('77889900P', 'Ricardo Navarro', '611888999', 'ricardo.nav@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('88990011Q', 'Silvia Ramos', '611999000', 'silvia.ramos@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('99001122R', 'Alberto Castro', '622111222', 'alberto.castro@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('00112233S', 'Irene Ortega', '622222333', 'irene.ortega@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('12345678A', 'Juan Pérez', '600111222', 'juan.perez@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('23456789B', 'María García', '600222333', 'maria.garcia@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('34567890C', 'Carlos Rodríguez', '600333444', 'carlos.rod@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('45678901D', 'Ana Martínez', '600444555', 'ana.mtz@email.com');
INSERT INTO clients (dni, nombre, telefono, correo) VALUES ('56789012E', 'Luis López', '600555666', 'luis.lopez@email.com');

--Inserts de habitaciones de prueba
-- PLANTA 1: INDIVIDUALES (101-140)
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (101, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (102, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (103, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (104, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (105, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (106, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (107, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (108, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (109, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (110, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (111, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (112, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (113, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (114, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (115, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (116, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (117, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (118, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (119, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (120, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (121, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (122, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (123, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (124, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (125, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (126, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (127, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (128, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (129, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (130, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (131, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (132, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (133, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (134, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (135, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (136, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (137, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (138, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (139, 'Individual', 'Disponible', 45.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (140, 'Individual', 'Disponible', 45.0);

-- PLANTA 2: DOBLES (201-240)
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (201, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (202, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (203, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (204, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (205, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (206, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (207, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (208, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (209, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (210, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (211, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (212, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (213, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (214, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (215, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (216, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (217, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (218, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (219, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (220, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (221, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (222, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (223, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (224, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (225, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (226, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (227, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (228, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (229, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (230, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (231, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (232, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (233, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (234, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (235, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (236, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (237, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (238, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (239, 'Doble', 'Disponible', 75.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (240, 'Doble', 'Disponible', 75.0);

-- Interts Invoices
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Estancia Habitación Doble', 3, 80.00, 240.00, 50.40, 290.40, b'1', 1);
INSERT INTO invoices(concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Suite Nupcial', 2, 150.00, 300.00, 63.00, 363.00, b'1', 2);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Habitación Individual', 5, 55.00, 275.00, 57.75, 332.75, b'0', 3);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Apartamento Turístico', 7, 120.00, 840.00, 176.40, 1016.40, b'1', 4);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Estancia Fin de Semana', 2, 95.00, 190.00, 39.90, 229.90, b'0', 5);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Habitación Triple', 4, 110.00, 440.00, 92.40, 532.40, b'1', 6);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Bungalow Familiar', 10, 200.00, 2000.00, 420.00, 2420.00, b'1', 7);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Estancia Económica', 1, 45.00, 45.00, 9.45, 54.45, b'0', 8);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Habitación Vistas Mar', 3, 135.50, 406.50, 85.37, 491.87, b'1', 9);
INSERT INTO invoices (concepto, noches, precio, subtotal, iva, total, pagada, cliente_id) VALUES ('Pack Relax Todo Incluido', 4, 180.00, 720.00, 151.20, 871.20, b'0', 10);

-- PLANTA 3: SUITES (301-320)
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (301, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (302, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (303, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (304, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (305, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (306, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (307, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (308, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (309, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (310, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (311, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (312, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (313, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (314, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (315, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (316, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (317, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (318, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (319, 'Suite', 'Disponible', 120.0);
INSERT INTO habitacion (numero, tipo, estado, precio_por_noche) VALUES (320, 'Suite', 'Disponible', 120.0);

--Inserts de actividades de prueba
INSERT INTO activities (descripcion, precio, fecha_comienzo, fecha_fin, max_participantes) VALUES ('Excursión en 4x4', 45.00, '2026-05-19 10:00:00', '2026-05-19 12:00:00', 10);
INSERT INTO activities (descripcion, precio, fecha_comienzo, fecha_fin, max_participantes) VALUES ('Kayak en la costa', 30.00, '2026-05-19 12:30:00', '2026-05-19 14:30:00', 8);
INSERT INTO activities (descripcion, precio, fecha_comienzo, fecha_fin, max_participantes) VALUES ('Ruta de senderismo', 20.00, '2026-05-19 15:00:00', '2026-05-19 17:00:00', 15);
INSERT INTO activities (descripcion, precio, fecha_comienzo, fecha_fin, max_participantes) VALUES ('Paseo en barco', 60.00, '2026-05-20 09:30:00', '2026-05-20 11:30:00', 12);
INSERT INTO activities (descripcion, precio, fecha_comienzo, fecha_fin, max_participantes) VALUES ('Buceo básico', 80.00, '2026-05-20 12:00:00', '2026-05-20 14:00:00', 6);

--Inserts tabla actividades-clientes
INSERT INTO activities_clients (activity_id, client_id) VALUES (1, 1);
INSERT INTO activities_clients (activity_id, client_id) VALUES (1, 2);
INSERT INTO activities_clients (activity_id, client_id) VALUES (2, 3);
INSERT INTO activities_clients (activity_id, client_id) VALUES (3, 4);
INSERT INTO activities_clients (activity_id, client_id) VALUES (3, 5);
INSERT INTO activities_clients (activity_id, client_id) VALUES (4, 1);
INSERT INTO activities_clients (activity_id, client_id) VALUES (4, 2);
INSERT INTO activities_clients (activity_id, client_id) VALUES (4, 3);
INSERT INTO activities_clients (activity_id, client_id) VALUES (4, 4);
INSERT INTO activities_clients (activity_id, client_id) VALUES (4, 5);
INSERT INTO activities_clients (activity_id, client_id) VALUES (5, 1);

-- Inerts de descuentos
INSERT INTO discounts (concepto, porcentaje, fecha_caducidad)
VALUES ('Descuento Fidelidad', 10, '2026-12-31');

INSERT INTO discounts (concepto, porcentaje, fecha_caducidad)
VALUES ('Descuento VIP', 15, '2026-10-31');

INSERT INTO discounts (concepto, porcentaje, fecha_caducidad)
VALUES ('Promoción Verano', 5, '2026-08-31');

-- Empleados
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Juan', 'Pérez', 'Recepción', 1);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Ana', 'García', 'Recepción', 2);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Carlos', 'Sánchez', 'Recepción', 3);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Marta', 'López', 'Limpieza', 4);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Luis', 'Gómez', 'Limpieza', 5);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Elena', 'Rivas', 'Limpieza', 6);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Pedro', 'Torres', 'Seguridad', 7);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Sofía', 'Castro', 'Seguridad', 8);
INSERT INTO employees (nombre, apellido, cargo, user_id) VALUES ('Diego', 'Ruiz', 'Seguridad', 9);
-- Horarios (Solo los 3 turnos principales)
INSERT INTO schedules (nombre_turno, hora_entrada, hora_salida) VALUES ('Turno Mañana', '08:00:00', '15:00:00');
INSERT INTO schedules (nombre_turno, hora_entrada, hora_salida) VALUES ('Turno Tarde', '15:00:00', '22:00:00');
INSERT INTO schedules (nombre_turno, hora_entrada, hora_salida) VALUES ('Turno Noche', '22:00:00', '08:00:00');
-- Shifts (Lunes 27 de Abril al Domingo 3 de Mayo, 2026)
-- Lunes 27
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 1, 1, 'Equipo A');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 2, 1, 'Equipo A');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 3, 1, 'Equipo A');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 4, 2, 'Equipo B');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 5, 2, 'Equipo B');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 6, 2, 'Equipo B');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 7, 3, 'Equipo C');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 8, 3, 'Equipo C');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-27', 9, 3, 'Equipo C');
-- Martes 28
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 1, 1, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 2, 1, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 3, 1, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 4, 2, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 5, 2, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 6, 2, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 7, 3, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 8, 3, 'Sin cambios');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-28', 9, 3, 'Sin cambios');
-- Miércoles 29 (Rotación de ejemplo)
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-29', 7, 1, 'Rotación');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-29', 8, 1, 'Rotación');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-29', 9, 1, 'Rotación');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-29', 1, 2, 'Rotación');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-29', 2, 2, 'Rotación');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-29', 3, 2, 'Rotación');
-- Jueves 30
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-30', 4, 1, 'Día completo');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-30', 5, 1, 'Día completo');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-04-30', 6, 1, 'Día completo');
-- Viernes 01 (Mayo)
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-01', 1, 3, 'Festivo');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-01', 2, 3, 'Festivo');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-01', 3, 3, 'Festivo');
-- Sábado 02
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-02', 4, 2, 'Fin de semana');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-02', 5, 2, 'Fin de semana');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-02', 6, 2, 'Fin de semana');
-- Domingo 03
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-03', 7, 1, 'Fin de semana');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-03', 8, 1, 'Fin de semana');
INSERT INTO shifts (fecha, employee_id, schedule_id, observaciones) VALUES ('2026-05-03', 9, 1, 'Fin de semana');
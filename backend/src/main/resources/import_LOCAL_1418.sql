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
INSERT INTO room (number, type, price, available) VALUES (101, 'Individual', 60.00, 1);
INSERT INTO room (number, type, price, available) VALUES (102, 'Individual', 60.00, 1);
INSERT INTO room (number, type, price, available) VALUES (202, 'Doble', 90.00, 0);

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
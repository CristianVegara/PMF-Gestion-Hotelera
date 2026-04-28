--Inserts de usuarios
INSERT INTO users (username, password_hash, role, created_at) VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8q6OuVGkqCYAdVqKz3T.Y6B5V7Bw.T.Y6B5', 'ROLE_ADMIN', '2024-01-01 10:00:00');
INSERT INTO users (username, password_hash, role, created_at) VALUES ('jdoe', '$2a$10$vI8BvWZpC8ZpX9.Z7H6O.uM8vI8BvWZpC8ZpX9.Z7H6O.uM8vI8B', 'ROLE_USER', '2024-01-15 14:30:00');
INSERT INTO users (username, password_hash, role, created_at) VALUES ('mgarcia', '$2a$10$L2k.XyZ1.234567890abcdL2k.XyZ1.234567890abcdL2k.XyZ', 'ROLE_USER', '2024-02-01 09:15:00');
INSERT INTO users (username, password_hash, role, created_at) VALUES ('moderator_01', '$2a$10$PqR.StU1.vWxYz2.345678PqR.StU1.vWxYz2.345678PqR.St', 'ROLE_MODERATOR', '2024-02-05 18:45:00');
INSERT INTO users (username, password_hash, role, created_at) VALUES ('guest_user', '$2a$10$AbC.DeF1.gHiJkL3.456789AbC.DeF1.gHiJkL3.456789AbC.De', 'ROLE_GUEST', CURRENT_TIMESTAMP);
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

-- Inerts de descuentos
INSERT INTO discounts (concepto, porcentaje, fecha_caducidad)
VALUES ('Descuento Fidelidad', 10, '2026-12-31');

INSERT INTO discounts (concepto, porcentaje, fecha_caducidad)
VALUES ('Descuento VIP', 15, '2026-10-31');

INSERT INTO discounts (concepto, porcentaje, fecha_caducidad)
VALUES ('Promoción Verano', 5, '2026-08-31');
-- Inserts de las Comunidades Autónomas, ignora si se produce un error en la inserción
INSERT IGNORE INTO regions (id, code, name) VALUES
(1, '01', 'ANDALUCÍA'),
(2, '02', 'ARAGÓN'),
(3, '03', 'ASTURIAS'),
(4, '04', 'BALEARES'),
(5, '05', 'CANARIAS'),
(6, '06', 'CANTABRIA'),
(7, '07', 'CASTILLA Y LEÓN'),
(8, '08', 'CASTILLA-LA MANCHA'),
(9, '09', 'CATALUÑA'),
(10, '10', 'COMUNIDAD VALENCIANA'),
(11, '11', 'EXTREMADURA'),
(12, '12', 'GALICIA'),
(13, '13', 'MADRID'),
(14, '14', 'MURCIA'),
(15, '15', 'NAVARRA'),
(16, '16', 'PAÍS VASCO'),
(17, '17', 'LA RIOJA'),
(18, '18', 'CEUTA Y MELILLA');

-- Inserta si no existe (ignora duplicados por UNIQUE username o id)
INSERT IGNORE INTO users (
   id, email, password_hash, active, account_non_locked,
   last_password_change, password_expires_at, failed_login_attempts,
   email_verified, must_change_password
) VALUES
(1, 'admin@app.local',        '$2a$12$HnF3pSI.kpCNujmMgcQDA.fbGt2TFPbmMDH.rT4wUKLvOKEzsvlTC',  TRUE,  TRUE,  NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE,  FALSE),
(2, 'jdoe@app.local',         '$2a$12$HnF3pSI.kpCNujmMgcQDA.fbGt2TFPbmMDH.rT4wUKLvOKEzsvlTC',      TRUE,  TRUE,  NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 1, FALSE, FALSE),
(3, 'maria@app.local',        '$2a$12$HnF3pSI.kpCNujmMgcQDA.fbGt2TFPbmMDH.rT4wUKLvOKEzsvlTC',  TRUE,  TRUE,  NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE,  TRUE ),
(4, 'blockeduser@app.local',  '$2a$12$HnF3pSI.kpCNujmMgcQDA.fbGt2TFPbmMDH.rT4wUKLvOKEzsvlTC',    FALSE, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 5, FALSE, FALSE);
-- Insertar datos de las provincias españolas con los códigos correctos

INSERT IGNORE INTO provinces (code, name, region_id) VALUES
('01', 'Araba/Álava', 16),
('02', 'Albacete', 8),
('03', 'Alicante/Alacant', 10),
('04', 'Almería', 1),
('05', 'Ávila', 7),
('06', 'Badajoz', 11),
('07', 'Balears, Illes', 4),
('08', 'Barcelona', 9),
('09', 'Burgos', 7),
('10', 'Cáceres', 11),
('11', 'Cádiz', 1),
('12', 'Castellón/Castelló', 10),
('13', 'Ciudad Real', 8),
('14', 'Córdoba', 1),
('15', 'Coruña, A', 12),
('16', 'Cuenca', 8),
('17', 'Girona', 9),
('18', 'Granada', 1),
('19', 'Guadalajara', 8),
('20', 'Gipuzkoa', 16),
('21', 'Huelva', 1),
('22', 'Huesca', 2),
('23', 'Jaén', 1),
('24', 'León', 7),
('25', 'Lleida', 9),
('26', 'Rioja, La', 17),
('27', 'Lugo', 12),
('28', 'Madrid', 13),
('29', 'Málaga', 1),
('30', 'Murcia', 14),
('31', 'Navarra', 15),
('32', 'Ourense', 12),
('33', 'Asturias', 3),
('34', 'Palencia', 7),
('35', 'Palmas, Las', 5),
('36', 'Pontevedra', 12),
('37', 'Salamanca', 7),
('38', 'Santa Cruz de Tenerife', 5),
('39', 'Cantabria', 6),
('40', 'Segovia', 7),
('41', 'Sevilla', 1),
('42', 'Soria', 7),
('43', 'Tarragona', 9),
('44', 'Teruel', 2),
('45', 'Toledo', 8),
('46', 'Valencia/València', 10),
('47', 'Valladolid', 7),
('48', 'Bizkaia', 16),
('49', 'Zamora', 7),
('50', 'Zaragoza', 2),
('51', 'Ceuta', 18),
('52', 'Melilla', 18);

-- Insertar los roles
INSERT IGNORE INTO roles (id, name, display_name, description) VALUES
(1, 'ROLE_ADMIN', 'Administrator', 'Acceso total a todas las funcionalidades del sistema'),
(2, 'ROLE_USER', 'User', 'Usuario estándar'),
(3, 'ROLE_MANAGER', 'Manager', 'Usuario gestor de la aplicación tiene acceso a las funcionalidades de gestión de datos');


INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
-- Usuario 1: admin completo
(1, 1),  -- ROLE_ADMIN
(1, 2),  -- ROLE_USER
-- Usuario 2: usuario estándar
(2, 2),  -- ROLE_USER
-- Usuario 3: manager con permisos de usuario
(3, 3),  -- ROLE_MANAGER
(3, 2);  -- ROLE_USER
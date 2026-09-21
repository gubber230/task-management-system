INSERT INTO users (id, username, password, email, first_name, last_name, is_deleted) VALUES
  (201, 'proj_owner', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'proj_owner@test.com', 'Proj', 'Owner', 0),
  (202, 'proj_member', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'proj_member@test.com', 'Proj', 'Member', 0),
  (203, 'proj_outsider', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'proj_outsider@test.com', 'Proj', 'Outsider', 0);

INSERT INTO users_roles (user_id, role_id) SELECT 201, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 202, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 203, id FROM roles WHERE role = 'USER';

INSERT INTO projects (id, name, description, start_date, end_date, status, owner_id) VALUES
  (201, 'Integration Project', 'Integration Description', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'IN_PROGRESS', 201);

INSERT INTO projects_users (projects_id, users_id) VALUES (201, 202);

INSERT INTO users (id, username, password, email, first_name, last_name, is_deleted) VALUES
  (701, 'john_doe', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'john@example.com', 'John', 'Doe', 0),
  (702, 'login_user_test', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'login_user_test@example.com', 'Login', 'User', 0);

INSERT INTO users_roles (user_id, role_id) SELECT 701, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 702, id FROM roles WHERE role = 'USER';

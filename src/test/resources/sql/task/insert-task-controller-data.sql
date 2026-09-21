INSERT INTO users (id, username, password, email, first_name, last_name, is_deleted) VALUES
  (301, 'task_owner', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'task_owner@test.com', 'Task', 'Owner', 0),
  (302, 'task_assignee', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'task_assignee@test.com', 'Task', 'Assignee', 0),
  (303, 'task_outsider', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'task_outsider@test.com', 'Task', 'Outsider', 0);

INSERT INTO users_roles (user_id, role_id) SELECT 301, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 302, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 303, id FROM roles WHERE role = 'USER';

INSERT INTO projects (id, name, description, start_date, end_date, status, owner_id) VALUES
  (301, 'Task Test Project', 'Description', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'IN_PROGRESS', 301);

INSERT INTO projects_users (projects_id, users_id) VALUES (301, 302);

INSERT INTO tasks (id, name, description, priority, status, due_date, project_id, assignee_id) VALUES
  (301, 'Integration Task', 'Integration Description', 'HIGH', 'IN_PROGRESS',
   DATE_ADD(CURDATE(), INTERVAL 5 DAY), 301, 302);

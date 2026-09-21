INSERT INTO users (id, username, password, email, is_deleted) VALUES
  (111, 'assignee_ttest', 'x', 'assignee_ttest@test.com', 0);

INSERT INTO projects (id, name, description, start_date, end_date, status, owner_id) VALUES
  (111, 'Core API', 'Backend', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'INITIATED', 111);

INSERT INTO tasks (id, name, description, priority, status, due_date, project_id, assignee_id) VALUES
  (111, 'Create endpoints', 'Write controllers', 'HIGH', 'NOT_STARTED',
   DATE_ADD(CURDATE(), INTERVAL 5 DAY), 111, 111);

INSERT INTO users (id, username, password, email, is_deleted) VALUES
  (501, 'comment_owner', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'comment_owner@test.com', 0),
  (502, 'comment_member', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'comment_member@test.com', 0),
  (503, 'comment_outsider', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'comment_outsider@test.com', 0);

INSERT INTO users_roles (user_id, role_id) SELECT 501, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 502, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 503, id FROM roles WHERE role = 'USER';

INSERT INTO projects (id, name, description, start_date, end_date, status, owner_id) VALUES
  (501, 'Comment Test Project', 'Description', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'IN_PROGRESS', 501);

INSERT INTO projects_users (projects_id, users_id) VALUES (501, 502);

INSERT INTO tasks (id, name, description, priority, status, due_date, project_id, assignee_id) VALUES
  (501, 'Comment Task', 'Description', 'MEDIUM', 'IN_PROGRESS', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 501, 502);

INSERT INTO comments (id, task_id, user_id, text, time_stamp) VALUES
  (501, 501, 502, 'Existing comment', NOW());

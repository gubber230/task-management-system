INSERT INTO users (id, username, password, email, is_deleted) VALUES
  (601, 'attachment_owner', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'attachment_owner@test.com', 0),
  (602, 'attachment_outsider', '$2b$10$Xx.mfmSq0UY9l344V.8GO.Jy2w3jZboLsnbesZGpREfbvr0SIuxbi', 'attachment_outsider@test.com', 0);

INSERT INTO users_roles (user_id, role_id) SELECT 601, id FROM roles WHERE role = 'USER';
INSERT INTO users_roles (user_id, role_id) SELECT 602, id FROM roles WHERE role = 'USER';

INSERT INTO projects (id, name, description, start_date, end_date, status, owner_id) VALUES
  (601, 'Attachment Test Project', 'Description', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'IN_PROGRESS', 601);

INSERT INTO tasks (id, name, description, priority, status, due_date, project_id, assignee_id) VALUES
  (601, 'Attachment Task', 'Description', 'MEDIUM', 'IN_PROGRESS', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 601, 601),
  (602, 'Attachment Task Without File', 'Description', 'MEDIUM', 'IN_PROGRESS', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 601, 601);

INSERT INTO attachments (id, task_id, dropbox_file_id, file_name, upload_date) VALUES
  (601, 601, 'dbx-601', 'doc.txt', NOW());

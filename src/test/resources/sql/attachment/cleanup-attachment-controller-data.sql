DELETE FROM attachments WHERE task_id IN (601, 602);
DELETE FROM tasks WHERE project_id = 601;
DELETE FROM projects WHERE id = 601;
DELETE FROM users_roles WHERE user_id IN (601, 602);
DELETE FROM users WHERE id IN (601, 602);

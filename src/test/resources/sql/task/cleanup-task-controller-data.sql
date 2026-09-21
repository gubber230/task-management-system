DELETE FROM tasks_labels WHERE task_id = 301;
DELETE FROM comments WHERE task_id = 301;
DELETE FROM attachments WHERE task_id = 301;
DELETE FROM tasks WHERE project_id = 301;
DELETE FROM projects_users WHERE projects_id = 301;
DELETE FROM projects WHERE id = 301;
DELETE FROM users_roles WHERE user_id IN (301, 302, 303);
DELETE FROM users WHERE id IN (301, 302, 303);

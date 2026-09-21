DELETE FROM comments WHERE task_id = 501;
DELETE FROM tasks WHERE id = 501;
DELETE FROM projects_users WHERE projects_id = 501;
DELETE FROM projects WHERE id = 501;
DELETE FROM users_roles WHERE user_id IN (501, 502, 503);
DELETE FROM users WHERE id IN (501, 502, 503);

DELETE FROM projects_users WHERE projects_id = 101;
DELETE FROM projects WHERE id = 101;
DELETE FROM users WHERE id IN (101, 102, 103);

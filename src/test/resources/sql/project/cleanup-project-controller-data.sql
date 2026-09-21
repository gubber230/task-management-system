DELETE FROM projects_users
  WHERE projects_id = 201
     OR projects_id IN (SELECT id FROM projects WHERE name LIKE 'Integration Project - New%');
DELETE FROM tasks WHERE project_id = 201;
DELETE FROM projects WHERE id = 201 OR name LIKE 'Integration Project - New%';
DELETE FROM users_roles WHERE user_id IN (201, 202, 203);
DELETE FROM users WHERE id IN (201, 202, 203);

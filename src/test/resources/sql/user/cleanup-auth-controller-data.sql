DELETE FROM users_roles WHERE user_id IN (701, 702)
  OR user_id IN (SELECT id FROM users WHERE username = 'new_reg_user');
DELETE FROM users WHERE id IN (701, 702) OR username = 'new_reg_user';

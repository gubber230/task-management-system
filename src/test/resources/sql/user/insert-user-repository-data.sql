INSERT INTO users (id, username, email, password, is_deleted) VALUES
  (121, 'john_doe_rtest', 'john_rtest@example.com', 'secret123', 0);

INSERT INTO users_roles (user_id, role_id)
SELECT 121, id FROM roles WHERE role = 'USER';

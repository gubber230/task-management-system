INSERT INTO users (id, username, password, email, is_deleted) VALUES
  (101, 'owner_ptest', 'x', 'owner_ptest@test.com', 0),
  (102, 'member_ptest', 'x', 'member_ptest@test.com', 0),
  (103, 'outsider_ptest', 'x', 'outsider_ptest@test.com', 0);

INSERT INTO projects (id, name, description, start_date, end_date, status, owner_id) VALUES
  (101, 'App Development', 'Main project', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'INITIATED', 101);

-- owner додається і як member — це замінює ручний entityManager.persist() з оригінального теста
INSERT INTO projects_users (projects_id, users_id) VALUES
  (101, 101),
  (101, 102);

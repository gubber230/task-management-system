DELETE FROM tasks_labels WHERE label_id = 401;
DELETE FROM labels WHERE id = 401 OR name IN ('Enhancement', 'Updated Label');

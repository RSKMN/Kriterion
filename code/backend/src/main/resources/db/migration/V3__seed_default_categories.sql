-- Seed system-wide default categories.
-- user_id NULL + is_default TRUE denotes system-provided categories.

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Food', 'EXPENSE', 'utensils', '#ef4444', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Food' AND type = 'EXPENSE'
);

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Transport', 'EXPENSE', 'bus', '#f97316', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Transport' AND type = 'EXPENSE'
);

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Shopping', 'EXPENSE', 'shopping-bag', '#ec4899', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Shopping' AND type = 'EXPENSE'
);

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Bills', 'EXPENSE', 'receipt', '#eab308', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Bills' AND type = 'EXPENSE'
);

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Salary', 'INCOME', 'wallet', '#22c55e', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Salary' AND type = 'INCOME'
);

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Freelance', 'INCOME', 'briefcase', '#14b8a6', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Freelance' AND type = 'INCOME'
);

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Entertainment', 'EXPENSE', 'film', '#8b5cf6', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Entertainment' AND type = 'EXPENSE'
);

INSERT INTO categories (user_id, name, type, icon, color, is_default)
SELECT NULL, 'Health', 'EXPENSE', 'heart-pulse', '#06b6d4', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE user_id IS NULL AND name = 'Health' AND type = 'EXPENSE'
);

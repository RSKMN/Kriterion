-- Indexes for query performance and high-cardinality access paths.

CREATE INDEX idx_categories_user_id ON categories(user_id);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_transaction_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);

CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_month_year ON budgets(month, year);
CREATE INDEX idx_budgets_user_month_year ON budgets(user_id, month, year);

CREATE INDEX idx_recurring_transactions_user_id ON recurring_transactions(user_id);
CREATE INDEX idx_recurring_transactions_next_run_date ON recurring_transactions(next_run_date);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_user_read_status ON notifications(user_id, is_read);

CREATE INDEX idx_receipts_user_id ON receipts(user_id);

CREATE INDEX idx_ai_categorizations_transaction_id ON ai_categorizations(transaction_id);
CREATE INDEX idx_ai_categorizations_predicted_category_id ON ai_categorizations(predicted_category_id);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

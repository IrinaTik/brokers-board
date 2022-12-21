INSERT INTO currencies (id, name, code, org) VALUES (1, 'US Dollar', 'USD', 'America');
INSERT INTO currencies (id, name, code, org) VALUES (2, 'Euro', 'EUR', 'Europe');
INSERT INTO currencies (id, name, code, org) VALUES (3, 'Rubl', 'RUB', 'Russia');

INSERT INTO exchange_rates (id, first_currency_id, second_currency_id, first_currency_value) VALUES (1, 2, 1, 1);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (1, 10.8, '2021-09-17', 1);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (2, 11.26, '2021-08-17', 1);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (3, 12.6, '2022-02-17', 1);

INSERT INTO exchange_rates (id, first_currency_id, second_currency_id, first_currency_value) VALUES (2, 1, 3, 1);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (4, 70.46, '2021-09-17', 2);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (5, 73.98, '2021-08-17', 2);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (6, 76.56, '2022-02-17', 2);

INSERT INTO exchange_rates (id, first_currency_id, second_currency_id, first_currency_value) VALUES (3, 2, 3, 1);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (7, 76.56, '2021-09-17', 3);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (8, 80.34, '2021-08-17', 3);
INSERT INTO exchange_rate_history (id, currency_value, date_time, rate_id) VALUES (9, 82.4, '2022-02-17', 3);
INSERT INTO cars (model, brand, type, inventory, daily_fee) VALUES ('X5', 'BMW', 'SEDAN', 5, 1000);
INSERT INTO rentals (car_id, user_id, rental_date, return_date, actual_return_date)
VALUES (1, 1, '2024-04-10', '2024-04-30', '2024-04-25');
INSERT INTO payments (amount_to_pay, rental_id, session_url, session_id, status, type)
VALUES (1000, 1, 'http://localhost:8080/testUrl', 24323482934, 'PENDING', 'PAYMENT');
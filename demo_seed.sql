-- Demo-only seed data for a fresh resource_manager database.
-- Additive and re-runnable: existing matching records are left unchanged.
-- Run schema.sql only against a fresh database; it drops and recreates tables.

USE `resource_manager`;
START TRANSACTION;

-- Demo login accounts (passwords are BCrypt hashes).
INSERT INTO `USER` (login_id, password, name, role)
SELECT 'demo_user', '$2a$12$Xw3ZulbEPSu8ImvYfkueI.dA2Y/U97es221p8SDvTh0bA6Vt07vpi', '시연 사용자', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE login_id = 'demo_user');

INSERT INTO `USER` (login_id, password, name, role)
SELECT 'demo_admin', '$2a$12$IBjyRv/n1OSzfsXLda9aSeIqiP/ulbbgnYMfkeIo0xgR9/RpPDMsy', '시연 관리자', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE login_id = 'demo_admin');

-- Demo categories.
INSERT INTO CATEGORY (name)
SELECT '시연_컴퓨터' WHERE NOT EXISTS (SELECT 1 FROM CATEGORY WHERE name = '시연_컴퓨터');
INSERT INTO CATEGORY (name)
SELECT '시연_영상장비' WHERE NOT EXISTS (SELECT 1 FROM CATEGORY WHERE name = '시연_영상장비');
INSERT INTO CATEGORY (name)
SELECT '시연_음향장비' WHERE NOT EXISTS (SELECT 1 FROM CATEGORY WHERE name = '시연_음향장비');

-- Demo items. Available items remain available while their loan is only REQUESTED.
INSERT INTO ITEM (category_id, item_name, serial_no, description, status)
SELECT c.category_id, '시연용 노트북', 'DEMO-ITEM-001', '통합 시연용 대여 가능 물품', 'AVAILABLE'
FROM CATEGORY c WHERE c.name = '시연_컴퓨터'
  AND NOT EXISTS (SELECT 1 FROM ITEM WHERE serial_no = 'DEMO-ITEM-001');
INSERT INTO ITEM (category_id, item_name, serial_no, description, status)
SELECT c.category_id, '시연용 카메라', 'DEMO-ITEM-002', '통합 시연용 정상 대여 물품', 'BORROWED'
FROM CATEGORY c WHERE c.name = '시연_영상장비'
  AND NOT EXISTS (SELECT 1 FROM ITEM WHERE serial_no = 'DEMO-ITEM-002');
INSERT INTO ITEM (category_id, item_name, serial_no, description, status)
SELECT c.category_id, '시연용 태블릿', 'DEMO-ITEM-003', '통합 시연용 연체 물품', 'BORROWED'
FROM CATEGORY c WHERE c.name = '시연_컴퓨터'
  AND NOT EXISTS (SELECT 1 FROM ITEM WHERE serial_no = 'DEMO-ITEM-003');
INSERT INTO ITEM (category_id, item_name, serial_no, description, status)
SELECT c.category_id, '시연용 프로젝터', 'DEMO-ITEM-004', '통합 시연용 승인 대기 물품', 'AVAILABLE'
FROM CATEGORY c WHERE c.name = '시연_영상장비'
  AND NOT EXISTS (SELECT 1 FROM ITEM WHERE serial_no = 'DEMO-ITEM-004');
INSERT INTO ITEM (category_id, item_name, serial_no, description, status)
SELECT c.category_id, '시연용 마이크', 'DEMO-ITEM-005', '통합 시연용 반납 확인 대기 물품', 'BORROWED'
FROM CATEGORY c WHERE c.name = '시연_음향장비'
  AND NOT EXISTS (SELECT 1 FROM ITEM WHERE serial_no = 'DEMO-ITEM-005');
INSERT INTO ITEM (category_id, item_name, serial_no, description, status)
SELECT c.category_id, '시연용 스피커', 'DEMO-ITEM-006', '통합 시연용 반납 완료 이력 물품', 'AVAILABLE'
FROM CATEGORY c WHERE c.name = '시연_음향장비'
  AND NOT EXISTS (SELECT 1 FROM ITEM WHERE serial_no = 'DEMO-ITEM-006');

-- Loan rows cover the main lifecycle and each relevant status.
INSERT INTO LOAN (user_id, item_id, start_date, due_date, return_date, purpose, status)
SELECT u.user_id, i.item_id, CURDATE() - INTERVAL 20 DAY, CURDATE() - INTERVAL 13 DAY,
       CURDATE() - INTERVAL 12 DAY, 'DEMO_SEED:RETURNED', 'RETURNED'
FROM `USER` u JOIN ITEM i ON i.serial_no = 'DEMO-ITEM-006'
WHERE u.login_id = 'demo_user'
  AND NOT EXISTS (SELECT 1 FROM LOAN WHERE purpose = 'DEMO_SEED:RETURNED');

INSERT INTO LOAN (user_id, item_id, start_date, due_date, return_date, purpose, status)
SELECT u.user_id, i.item_id, CURDATE() + INTERVAL 1 DAY, CURDATE() + INTERVAL 8 DAY,
       NULL, 'DEMO_SEED:REQUESTED', 'REQUESTED'
FROM `USER` u JOIN ITEM i ON i.serial_no = 'DEMO-ITEM-004'
WHERE u.login_id = 'demo_user'
  AND NOT EXISTS (SELECT 1 FROM LOAN WHERE purpose = 'DEMO_SEED:REQUESTED');

INSERT INTO LOAN (user_id, item_id, start_date, due_date, return_date, purpose, status)
SELECT u.user_id, i.item_id, CURDATE() - INTERVAL 2 DAY, CURDATE() + INTERVAL 5 DAY,
       NULL, 'DEMO_SEED:BORROWED', 'BORROWED'
FROM `USER` u JOIN ITEM i ON i.serial_no = 'DEMO-ITEM-002'
WHERE u.login_id = 'demo_user'
  AND NOT EXISTS (SELECT 1 FROM LOAN WHERE purpose = 'DEMO_SEED:BORROWED');

INSERT INTO LOAN (user_id, item_id, start_date, due_date, return_date, purpose, status)
SELECT u.user_id, i.item_id, CURDATE() - INTERVAL 14 DAY, CURDATE() - INTERVAL 3 DAY,
       NULL, 'DEMO_SEED:OVERDUE', 'OVERDUE'
FROM `USER` u JOIN ITEM i ON i.serial_no = 'DEMO-ITEM-003'
WHERE u.login_id = 'demo_user'
  AND NOT EXISTS (SELECT 1 FROM LOAN WHERE purpose = 'DEMO_SEED:OVERDUE');

INSERT INTO LOAN (user_id, item_id, start_date, due_date, return_date, purpose, status)
SELECT u.user_id, i.item_id, CURDATE() - INTERVAL 9 DAY, CURDATE() - INTERVAL 2 DAY,
       NULL, 'DEMO_SEED:RETURN_REQUESTED', 'RETURN_REQUESTED'
FROM `USER` u JOIN ITEM i ON i.serial_no = 'DEMO-ITEM-005'
WHERE u.login_id = 'demo_user'
  AND NOT EXISTS (SELECT 1 FROM LOAN WHERE purpose = 'DEMO_SEED:RETURN_REQUESTED');

COMMIT;

--Account data insertion
INSERT INTO account(
    id, account_number, balance, is_active, person_id, open_date)
VALUES (101, 8776672, 1000, true, 1, '2022/11/11');

INSERT INTO account(
    id, account_number, balance, is_active, person_id, open_date)
VALUES (102, 8753345, 2410, true, 2, '2023/01/10');

INSERT INTO account(
    id, account_number, balance, is_active, person_id, open_date)
VALUES (103, 17253417, 1400, true, 3, '2022/01/10');



-- CARD DATA INSERTION
INSERT INTO public.card(
    id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count, issue_date)
VALUES (201, 6280231451904303, 199, '2023/11/11', true, 1233, 101, 0, '2021/11/11');
INSERT INTO public.card(
    id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count, issue_date)
VALUES (202, 628023145234765, 342, '2023/11/11', false, 1111, 102, 0, '2021/11/11');

INSERT INTO public.card(
    id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count, issue_date)
VALUES (203, 628023145412347, 202, '2020/11/11', true, 7654, 101, 0, '2018/11/11');

INSERT INTO public.card(
    id, card_number, cvv2, expire_date, is_active, pin, account_id, incorrect_pin_count, issue_date)
VALUES (204, 6280231476123412, 239, '2022/11/11', true, 1233, 101, 0, '2020/11/11');
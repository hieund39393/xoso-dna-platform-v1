INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSCHANLE', 'ຫວຍລາວ', 'Xổ số chẵn lẻ','หวยลาว','Xổ số Chẵn lẻ');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('CHANLE', 'ຄູ່-ຄີກ', 'Chẵn lẻ','หวยลาว','Chẵn lẻ');
INSERT INTO lottery_category(name, code, active , created_date, modified_date) VALUES('XSCHANLE', 'XSCHANLE',true, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('CHANLE', 'CHANLE',10000,19000, Now(), Now());

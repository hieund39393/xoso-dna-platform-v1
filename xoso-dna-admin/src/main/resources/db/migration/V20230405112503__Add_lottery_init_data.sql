--translated string--
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSN', 'ຫວຍໄວ', 'Xổ số nhanh','หวยเร็ว','Xổ số nhanh');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSN15', 'ຄວາມໄວ 15 ວິນາທີ', 'Xổ số nhanh 15s','ความเร็ว 15 วินาที','Xổ số nhanh 15s');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSN45', 'ຄວາມໄວ 45 ວິນາທີ', 'Xổ số nhanh 45s','ความเร็ว 45 วินาที','Xổ số nhanh 45s');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSN60', 'ຄວາມໄວ 1 ນາທີ', 'Xổ số nhanh 1p','ความเร็ว 1 นาที','Xổ số nhanh 1p');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSVIE', 'ຫວຍຫວຽດນາມ', 'Xổ số Việt Nam','หวยเวียดนาม','Xổ số Việt Nam');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSLAO', 'ຫວຍລາວ', 'Xổ số Lào','หวยลาว','Xổ số Lào');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSTHAI', 'ຫວຍໄທ', 'Xổ số Thái','หวยไทย','Xổ số Thái');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSCAM', 'ຫວຍກໍາປູເຈຍ', 'Xổ số Cam','หวยกัมพูชา','Xổ số Cam');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSLAO18', 'ຫວຍລາວ 18h00', 'Xổ số Lào 18h','หวยลาว 18h00','Xổ số Lào 18h');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSLAO20', 'ຫວຍລາວ 20h00', 'Xổ số Lào 20h','หวยลาว 20h00','Xổ số Lào 20h');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSLAO22', 'ຫວຍລາວ 22h00', 'Xổ số Lào 22h','หวยลาว 22h00','Xổ số Lào 22h');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSVIE1830', 'ຫວຍຫວຽດນາມ 18h30', 'Xổ số Việt Nam 18h30','หวยเวียดนาม 18h30','Xổ số Việt Nam 18h30');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSVIE2030', 'ຫວຍຫວຽດນາມ 20h30', 'Xổ số Việt Nam 20h30','หวยเวียดนาม 20h30','Xổ số Việt Nam 20h30');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('XSVIE2230', 'ຫວຍຫວຽດນາມ 22h30', 'Xổ số Việt Nam 22h30','หวยเวียดนาม 22h30','Xổ số Việt Nam 22h30');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('DDB', 'ລາງວັນພິເສດ', 'Đề đặc biệt','รางวัลพิเศษ','Đề đặc biệt');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('DGN', 'ລາງວັນທີ 1', 'Đề giải nhất','รางวัลที่ 1','Đề giải nhất');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('BCDB', 'ເລກ 3 ຕົວທ້າຍຂອງລາງວັນພິເສດ', 'Ba càng đặc biệt','เลข 3 ท้ายของรางวัลพิเศษ','Ba càng đặc biệt');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('BCGN', 'ເລກ 3 ຕົວທ້າຍຂອງລາງວັນທີ 1', 'Ba càng giải nhất','เลข 3 ท้ายของรางวัลที่ 1','Ba càng giải nhất');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('LO', 'ເລກທ້າຍ', 'Lô','เลขท้าย','Lô');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('LX2', 'ເລກທ້າຍ 2 ໂຕ', 'Lô xiên 2','เลขท้าย 2 ตัว','Lô xiên 2');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('LX3', 'ເລກທ້າຍ 3 ໂຕ', 'Lô xiên 3','เลขท้าย 3 ตัว','Lô xiên 3');
INSERT INTO translated_string(code, lao, viet, thai, cam) VALUES ('LX4', 'ເລກທ້າຍ 4 ໂຕ', 'Lô xiên 4','เลขท้าย 4 ตัว','Lô xiên 4');

--lottery category-----
INSERT INTO lottery_category(name, code, active , created_date, modified_date) VALUES('XSN','XSN',true, Now(), Now());
INSERT INTO lottery_category(name, code,active , created_date, modified_date) VALUES('XSVIE','XSVIE',true, Now(), Now());
INSERT INTO lottery_category(name, code, active , created_date, modified_date) VALUES('XSLAO','XSLAO',true, Now(), Now());
INSERT INTO lottery_category(name, code, active , created_date, modified_date) VALUES('XSTHAI','XSTHAI',true, Now(), Now());
INSERT INTO lottery_category(name, code, active , created_date, modified_date) VALUES('XSCAM','XSCAM',true, Now(), Now());

--lottery mode--
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('DDB','DDB',10000,990000, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('DGN','DGN',10000,990000, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('BCDB','BCDB',10000,990000, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('BCGN','BCGN',10000,990000, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('LO','LO',10000,990000, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('LX2','LX2',10000,990000, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('LX3','LX3',10000,990000, Now(), Now());
INSERT INTO lottery_mode(code, name,price, prize_money, created_date, modified_date) VALUES('LX4','LX4',10000,990000, Now(), Now());

--bank--
insert into bank(code, name, description, enabled, created_date, modified_date) VALUES('BCEL','ທະນາຄານລາວ','Banque pour le Commerce Extérieur Lao',true,Now(),Now());
insert into bank(code, name, description, enabled, created_date, modified_date) VALUES('LDB','ກະຊວງທະນາຄານລາວ','Lao Development Bank',true,Now(),Now());
insert into bank(code, name, description, enabled, created_date, modified_date) VALUES('LVB','ທະນາຄານລາວປະຕິວັດ','Lao-Viet Bank',true, Now(),Now());
insert into bank(code, name, description, enabled, created_date, modified_date) VALUES('BFL','ທະນາຄານບັດປະຊາຊົນລາວ','Banque Franco-Lao',true, Now(),Now());
insert into bank(code, name, description, enabled, created_date, modified_date) VALUES('APB','ທະນາຄານລາວແບບຂອງອາເມຣິກາ','Agricultural Promotion Bank',true, Now(),Now());
insert into bank(code, name, description, enabled, created_date, modified_date) VALUES('LCB','ທະນາຄານລາວຂາດ','Lao-China Bank',true, Now(),Now());
insert into bank(code, name, description, enabled, created_date, modified_date) VALUES('LXB','ທະນາຄານທາງດ້ານການຂາຍສິນຄ້າລາວ','Lao Securities Exchange Bank',true, Now(),Now());


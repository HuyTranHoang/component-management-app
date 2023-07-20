--Create Table Section
CREATE TABLE customers
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone   VARCHAR(10)  NOT NULL UNIQUE,
    email   VARCHAR(255),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE UNIQUE INDEX idx_unique_email
    ON customers (email)
    WHERE email IS NOT NULL AND email <> '';

CREATE TABLE departments
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE

);

CREATE TABLE positions
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE suppliers
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255),
    website VARCHAR(255),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE products
(
    id                SERIAL PRIMARY KEY,
    product_code      VARCHAR(255),
    name              VARCHAR(255)     NOT NULL,
    price             DOUBLE PRECISION NOT NULL,
    stock_quantity    INTEGER          NOT NULL,
    month_of_warranty INTEGER,
    note              VARCHAR(255),
    supplier_id       BIGINT REFERENCES suppliers (id) ON DELETE CASCADE,
    category_id       BIGINT REFERENCES categories (id) ON DELETE CASCADE,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_supplier_category
    ON products (supplier_id, category_id);

CREATE TABLE products_storage
(
    id              SERIAL PRIMARY KEY,
    import_quantity INTEGER NOT NULL,
    export_quantity INTEGER NOT NULL,
    product_id      BIGINT REFERENCES products (id) ON DELETE CASCADE,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_product
    ON products_storage (product_id);

CREATE TABLE employees
(
    id                     SERIAL PRIMARY KEY,
    name                   VARCHAR(255)     NOT NULL,
    address                VARCHAR(255)     NOT NULL,
    phone                  VARCHAR(10)      NOT NULL UNIQUE,
    email                  VARCHAR(255)     NOT NULL UNIQUE,
    password               VARCHAR(255)     NOT NULL,
    salary                 DOUBLE PRECISION NOT NULL,
    image                  VARCHAR(255)     NOT NULL,
    citizen_identification VARCHAR(12)      NOT NULL UNIQUE,
    date_of_birth          DATE             NOT NULL,
    date_of_hire           DATE             NOT NULL,
    department_id          BIGINT REFERENCES departments (id),
    position_id            BIGINT REFERENCES positions (id),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_department_position
    ON employees (department_id, position_id);

CREATE TABLE orders
(
    id                SERIAL PRIMARY KEY,
    order_date        TIMESTAMP        NOT NULL,
    delivery_date     TIMESTAMP        NOT NULL,
    receive_date     TIMESTAMP        NOT NULL,
    delivery_location VARCHAR(255)     NOT NULL,
    total_amount      DOUBLE PRECISION NOT NULL,
    note              VARCHAR(255),
    customer_id       BIGINT REFERENCES customers (id) ON DELETE CASCADE,
    employee_id       BIGINT REFERENCES employees (id),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_customer_employee
    ON orders (customer_id, employee_id);

CREATE TABLE order_detail
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(255)     NOT NULL,
    price        DOUBLE PRECISION NOT NULL,
    quantity     INTEGER          NOT NULL,
    discount     INTEGER          NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    order_id     BIGINT REFERENCES orders (id) ON DELETE CASCADE,
    product_id   BIGINT REFERENCES products (id) ON DELETE CASCADE,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_order_product
    ON order_detail (order_id, product_id);


-- Trigger Section
-- -- Trigger khi ProductStorage(import_quantity) tăng thì sẽ tăng Product(stock_quantity) lên
-- CREATE OR REPLACE FUNCTION increase_stock_quantity()
--     RETURNS TRIGGER AS
-- $$
-- BEGIN
--     UPDATE products
--     SET stock_quantity = stock_quantity + NEW.import_quantity
--     WHERE id = NEW.product_id;
--
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER increase_stock_trigger
--     AFTER INSERT
--     ON products_storage
--     FOR EACH ROW
--     WHEN (NEW.import_quantity > 0) -- Nếu ImportQuantity > 0 thì chạy func
-- EXECUTE FUNCTION increase_stock_quantity();
--
-- -- Trigger khi ProductStorage(export_quantity) tăng thì sẽ giảm Product(stock_quantity) xuống
-- CREATE OR REPLACE FUNCTION decrease_stock_quantity()
--     RETURNS TRIGGER AS
-- $$
-- BEGIN
--     UPDATE products
--     SET stock_quantity = stock_quantity - NEW.export_quantity
--     WHERE id = NEW.product_id;
--
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER decrease_stock_trigger
--     AFTER INSERT
--     ON products_storage
--     FOR EACH ROW
--     WHEN (NEW.export_quantity > 0) -- Nếu ExportQuantity > 0 thì chạy func
-- EXECUTE FUNCTION decrease_stock_quantity();
-- -- Trigger khi sửa stock_quantity thì sẽ tự động thêm cột mới vào trong bảng products storage
-- CREATE OR REPLACE FUNCTION update_products_storage()
--     RETURNS TRIGGER AS
-- $$
-- BEGIN
--     IF NEW.stock_quantity > OLD.stock_quantity THEN
--         INSERT INTO products_storage (import_quantity, export_quantity, date_of_storage, product_id)
--         VALUES (NEW.stock_quantity - OLD.stock_quantity, 0, NOW(), NEW.id);
--     ELSIF NEW.stock_quantity < OLD.stock_quantity THEN
--         INSERT INTO products_storage (import_quantity, export_quantity, date_of_storage, product_id)
--         VALUES (0, OLD.stock_quantity - NEW.stock_quantity, NOW(), NEW.id);
--     END IF;
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;

--Triggier khi thêm 1 product mới thì sẽ tự động thêm record mới trong products storage
CREATE OR REPLACE FUNCTION insert_products_storage()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO products_storage (import_quantity, export_quantity, product_id)
    VALUES (NEW.stock_quantity, 0, NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER insert_stock_trigger
    AFTER INSERT
    ON products
    FOR EACH ROW
EXECUTE FUNCTION insert_products_storage();

-- Trigger khi sửa stock_quantity thì sẽ tự động thêm cột mới vào trong bảng products storage
CREATE OR REPLACE FUNCTION update_products_storage()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.stock_quantity > OLD.stock_quantity THEN
        INSERT INTO products_storage (import_quantity, export_quantity, product_id)
        VALUES (NEW.stock_quantity - OLD.stock_quantity, 0, NEW.id);
    ELSIF NEW.stock_quantity < OLD.stock_quantity THEN
        INSERT INTO products_storage (import_quantity, export_quantity, product_id)
        VALUES (0, OLD.stock_quantity - NEW.stock_quantity, NEW.id);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_stock_trigger
    BEFORE UPDATE OF stock_quantity
    ON products
    FOR EACH ROW
EXECUTE FUNCTION update_products_storage();


-- Insert Section
-- Customer
INSERT INTO customers (name, address, phone, email)
VALUES ('Trần Hoàng Huy', 'Bến Lức, Long An', '070333444', 'hoanghuy@gmail.com'),
       ('Huỳnh Huỳnh Bảo', 'Cầu Kho, Hồ Chí Minh', '090444555', 'huynhbao@gmail.com'),
       ('Trần Phát Đạt', 'Tân Bình, Hồ Chí Minh', '091555666', 'phatdat@gmail.com'),
       ('Nguyễn Văn Giỏi', 'Quận 1, Hồ Chí Minh', '0901234567', 'vangioi@gmail.com'),
       ('Trần Anh Phúc', 'Quận 2, Hồ Chí Minh', '0902345678', 'anhphuc@gmail.com'),
       ('Lê Văn Cuờng', 'Quận 3, Hồ Chí Minh', '0903456789', 'vancuong@gmail.com'),
       ('Phạm Thị Dung', 'Quận 4, Hồ Chí Minh', '0904567890', 'thidung@gmail.com'),
       ('Hoàng Văn Võ', 'Quận 5, Hồ Chí Minh', '0905678901', 'vanvo@gmail.com'),
       ('Nguyễn Thị Lan Anh', 'Quận 6, Hồ Chí Minh', '0906789012', 'lananh@gmail.com'),
       ('Trần Văn Hưng', 'Quận 7, Hồ Chí Minh', '0907890123', 'vanhung@gmail.com'),
       ('Lê Thị Thu Hường', 'Quận 8, Hồ Chí Minh', '0908901234', 'thuhuong@gmail.com'),
       ('Phạm Gia Hưng', 'Quận 9, Hồ Chí Minh', '0909012345', 'giahung@gmail.com'),
       ('Hoàng Thị Thắm', 'Quận 10, Hồ Chí Minh', '0900123456', 'thitham@gmail.com');

-- Department
INSERT INTO departments (name, description)
VALUES ('Nhân sự', 'Quản lý nhân viên trong công ty'),
       ('Bán hàng', 'Quản lý các loại sản phẩm, số lượng tồn kho, tư vấn cho khách hàng');

-- Position
INSERT INTO positions (name, description)
VALUES ('Trưởng phòng', ''),
       ('Quản lý', ''),
       ('Nhân viên', '');

-- Employee ( Mật khẩu default = 1234@abc
INSERT INTO employees (name, address, phone, email,
                       password, salary, image,
                       citizen_identification, date_of_birth, date_of_hire, department_id, position_id)
VALUES ('Thái Bảo', 'Bình Thạnh, Hồ Chí Minh', '093444555', 'thaibao@gmail.com',
        '44fddd8b9d8755a392567bc5a7f54a5dcee0a82c2f19fa1c3afc808bca6cc841', 5000000, 'defaultImg.jpg',
        0123456789123, '1994-01-01', '2023-06-06', 1, 2),
       ('Lê Vương', 'Gò Đen, Hồ Chí Minh', '0973334444', 'levuong@gmail.com',
        '44fddd8b9d8755a392567bc5a7f54a5dcee0a82c2f19fa1c3afc808bca6cc841', 7000000, 'defaultImg.jpg',
        987654321123, '2001-01-01', '2023-07-07', 2, 2);

-- Category
INSERT INTO categories (name, description)
VALUES ('CPU - Bộ vi xử lý', ''),
       ('SSD/HHD - Ổ cứng', ''),
       ('Case - Thùng máy', ''),
       ('Ram - Bộ nhớ', ''),
       ('Mainboard - Bo mạch chủ', ''),
       ('PSU - Nguồn máy tính', ''),
       ('Fan - Quạt tản nhiệt', '');

-- Supplier
INSERT INTO suppliers (name, email, website)
VALUES ('ASUS', 'asus@gmail.com', 'https://www.asus.com/'),
       ('CORSAIR', 'corsair@gmail.com', 'https://www.cosair.com/'),
       ('Cooler Master', 'coolermaster@gmail.com', 'https://www.coolermaster.com/'),
       ('GIGABYTE', 'gigabyte@gmail.com', 'https://www.gigabyte.com/'),
       ('INTEL', 'intel@gmail.com', 'https://www.intel.com/'),
       ('KINGSTON', 'kingston@gmail.com', 'https://www.kingston.com/'),
       ('MSI', 'msi@gmail.com', 'https://www.msi.com/'),
       ('SAMSUNG', 'samsung@gmail.com', 'https://www.samsung.com/'),
       ('Western Digital', 'wd@gmail.com', 'https://www.wd.com/'),
       ('AMD', 'amd@gmail.com', 'https://www.amd.com/');

-- Product
-- CPU
INSERT INTO products (product_code, name, price, stock_quantity, month_of_warranty, note,
                      supplier_id, category_id)
VALUES ('Ci512400', 'CPU INTEL Core i5-12400 (6C/12T, 2.50 GHz - 4.40 GHz, 18MB', 4390000, 10, 12, 'note',
        5, 1),
       ('CG6405', 'CPU INTEL Pentium Gold G6405 (2C/4T, 4.10 GHz, 4MB) - 1200', 1599000, 5, 12, 'note',
        5, 1),
       ('Ci912900K', 'CPU INTEL Core i9-12900K (16C/24T, 3.20 GHz - 5.20 GHz, 30MB) - 1700', 10499000, 20, 24,
        'note', 5, 1),
       ('Ci712700', 'CPU INTEL Core i7-12700 (12C/20T, 4.90 GHz, 25MB) - 1700', 8399000, 22, 24, 'note',
        5, 1),
       ('R53600', 'CPU AMD Ryzen 5 3600 (6C/12T, 3.6 GHz - 4.2 GHz, 35MB)', 3699000, 15, 12, 'note',
        10, 1),
       ('R93900X', 'CPU AMD Ryzen 9 3900X (12C/24T, 3.8 GHz - 4.6 GHz, 70MB)', 12999000, 11, 24, 'note',
        10, 1),
       ('R72600X', 'CPU AMD Ryzen 7 2600X (6C/12T, 3.6 GHz - 4.2 GHz, 19MB)', 5799000, 5, 24, 'note',
        10, 1),
       ('R73700X', 'CPU AMD Ryzen 7 3700X (8C/16T, 3.6 GHz - 4.4 GHz, 36MB)', 7799000, 7, 24, 'note',
        10, 1),
       ('R91900X', 'CPU AMD Ryzen 9 1900X (8C/16T, 3.8 GHz - 4.2 GHz, 20MB)', 9399000, 9, 24, 'note',
        10, 1),
       ('RA53500X', 'CPU AMD Ryzen Threadripper 5350X (16C/32T, 3.0 GHz - 4.2 GHz, 144MB)', 24999000, 11, 24, 'note',
        10, 1),
       ('R73600X', 'CPU AMD Ryzen 7 3600X (6C/12T, 3.8 GHz - 4.4 GHz, 35MB)', 6799000, 2, 24, 'note',
        10, 1),
       ('R51600', 'CPU AMD Ryzen 5 1600 (6C/12T, 3.2 GHz - 3.6 GHz, 16MB)', 3299000, 9, 12, 'note',
        10, 1),
       ('R73700', 'CPU AMD Ryzen 7 3700 (8C/16T, 3.6 GHz - 4.4 GHz, 36MB)', 7399000, 11, 24, 'note',
        10, 1),
       ('RA72500X', 'CPU AMD Ryzen Threadripper 7250X (24C/48T, 3.2 GHz - 4.2 GHz, 144MB)', 48999000, 2, 24, 'note',
        10, 1);

-- SSD/HDD
INSERT INTO products (product_code, name, price, stock_quantity, month_of_warranty, note,
                      supplier_id, category_id)
VALUES ('SNV2', 'Ổ cứng gắn trong/ SSD Kingston NV2 1000GB M.2 2280 PCIe Gen 4.0 NVMe', 1459000, 9, 12, 'note',
        6, 2),
       ('SA400', 'Ổ cứng SSD Kingston A400 480GB Sata 3 (SA400S37/480G)', 749000, 12, 12, 'note',
        6, 2),
       ('SA2000', 'Ổ cứng SSD Kingston A2000 500GB M.2 NVMe', 1499000, 22, 12, 'note',
        6, 2),
       ('SUV500', 'Ổ cứng SSD Kingston UV500 1TB Sata 3', 2999000, 12, 24, 'note',
        6, 2),
       ('SKC600', 'Ổ cứng HDD Kingston KC600 2TB 2.5" SATA 3', 2599000, 43, 36, 'note',
        6, 2),
       ('SSS870', 'Ổ cứng SSD Samsung 870 EVO 4TB SATA III 2.5 inch (MZ-77E4T0BW)', 9999000, 50, 24, 'note',
        8, 2),
       ('SSS970', 'Ổ cứng SSD Samsung 970 EVO Plus 500GB M.2 NVMe', 1599000, 12, 12, 'note',
        8, 2),
       ('SSS860', 'Ổ cứng SSD Samsung 860 EVO 1TB Sata 3', 2899000, 32, 36, 'note',
        8, 2),
       ('SSS980', 'Ổ cứng SSD Samsung 980 Pro 2TB M.2 NVMe PCIe 4.0', 6799000, 19, 24, 'note',
        8, 2),
       ('WDB001', 'Ổ cứng HDD Western Digital Blue 1TB 3.5" SATA 3 - WD10EZEX', 955000, 28, 24, 'note',
        9, 2),
       ('SSN550', 'Ổ cứng SSD Western Digital WD Blue SN550 1TB M.2 NVMe', 1999000, 37, 12, 'note',
        9, 2),
       ('SUBWAY', 'Ổ cứng di động Western Digital My Passport 2TB USB 3.0', 1599000, 43, 24, 'note',
        9, 4);


-- Case
INSERT INTO products (product_code, name, price, stock_quantity, month_of_warranty, note,
                      supplier_id, category_id)
VALUES ('MB520', 'Thùng máy/ Case CM MasterBox MB520 ARGB', 1999000, 15, 12, 'note', 3, 3),
       ('MB05W', 'Case máy tính Cooler Master MasterBox 5 White', 1539000, 35, 12, 'note', 3, 3),
       ('M100A', 'Thùng máy/ Case MSI MAG FORCE M100A (4 Fan RGB)', 829000, 42, 24, 'note', 7, 3),
       ('M110R', 'Thùng máy/ Case MSI MPG GUNGNIR 110R WHITE (306-7G10W21-W57)', 2330000, 77, 24, 'note',
        7, 3),
       ('GC300', 'Thùng máy/ Case GIGABYTE C300 Glass RGB (ATX/mATX/Mini-ITX)', 2399000, 13, 12, 'note',
        4, 3),
       ('AORUSAC300G', 'Thùng máy/ Case GIGABYTE AORUS AC300G (ATX/mATX/Mini-ITX)', 2999000, 32, 24, 'note',
        4, 3),
       ('C200G', 'Thùng máy/ Case GIGABYTE C200G (ATX/mATX/Mini-ITX)', 1799000, 12, 12, 'note',
        4, 3),
       ('ASU100', 'Thùng máy/ Case ASUS TUF Gaming GT301 (ATX/mATX/Mini-ITX)', 1999000, 15, 12, 'note',
        1, 3),
       ('ASU200', 'Thùng máy/ Case ASUS ROG Strix Helios (ATX/EATX)', 8999000, 16, 24, 'note',
        1, 3),
       ('ASU300', 'Thùng máy/ Case ASUS ROG Strix Helios GX601 (ATX/EATX)', 10999000, 17, 24, 'note',
        1, 3);

-- RAM
INSERT INTO products (product_code, name, price, stock_quantity, month_of_warranty, note,
                      supplier_id, category_id)
VALUES ('RCMK235', 'RAM desktop CORSAIR Vengeance LPX Black Heat spreader (1 x 16GB) DDR4 3200MHz', 1249000, 17,
        12, 'note', 2, 4),
       ('RS13Z55', 'Bộ nhớ ram Gigabyte AORUS RGB 16GB (2x8GB) DDR4 3600 (2x Demo kit)', 3549000, 27, 12,
        'note', 4, 4),
       ('RKFB432', 'RAM desktop KINGSTON Fury Beast RGB 16GB (2 x 8GB) DDR4 3200MHz (KF432C16BBAK2/16)', 1699000, 7, 24,
        'note', 6, 4),
       ('RS16G33', 'RAM desktop GIGABYTE Aorus RGB 16GB DDR4-3333 (2 x 8GB) DDR4 2666MHz', 2950000, 9, 24,
        'note', 4, 4),
       ('SAM16G3200', 'RAM desktop Samsung DDR4 16GB (1x16GB) 3200MHz', 1399000, 6, 12, 'note',
        8, 4),
       ('SAM32G3600', 'RAM desktop Samsung DDR4 32GB (2x16GB) 3600MHz', 2999000, 5, 24, 'note',
        8, 4),
       ('SAM8G2400', 'RAM desktop Samsung DDR4 8GB (1x8GB) 2400MHz', 699000, 4, 12, 'note',
        8, 4),
       ('CORS8G2666', 'RAM desktop Corsair Vengeance LPX 8GB (1x8GB) DDR4 2666MHz', 799000, 3, 12, 'note',
        2, 4),
       ('CORS16G3200', 'RAM desktop Corsair Vengeance RGB Pro 16GB (2x8GB) DDR4 3200MHz', 2499000, 2, 12,
        'note', 2, 4),
       ('CORS32G3600', 'RAM desktop Corsair Dominator Platinum RGB 32GB (2x16GB) DDR4 3600MHz', 5599000, 7, 24,
        'note', 2, 4);

-- Mainboard
INSERT INTO products (product_code, name, price, stock_quantity, month_of_warranty, note,
                      supplier_id, category_id)
VALUES ('H510MK', 'Mainboard ASUS PRIME H510M-K', 1689000, 9, 12, 'note', 1, 5),
       ('PRMH510', 'Mainboard ASUS PRIME H510M', 1899000, 4, 12, 'note', 1, 5),
       ('TUFZ590', 'Mainboard ASUS TUF GAMING Z590-PLUS (WI-FI 6)', 6599000, 1, 24, 'note', 1, 5),
       ('ROGX570', 'Mainboard ASUS ROG CROSSHAIR VIII FORMULA', 13399000, 3, 24, 'note', 1, 5),
       ('H510MH', 'Mainboard GIGABYTE H510M-H', 1749000, 5, 12, 'note', 4, 5),
       ('BMH410', 'Mainboard GIGABYTE B450M H', 2499000, 11, 12, 'note', 4, 5),
       ('AZ490', 'Mainboard GIGABYTE AORUS Z490 MASTER', 10999000, 51, 24, 'note', 4, 5),
       ('X570G', 'Mainboard GIGABYTE X570 AORUS ELITE WIFI', 6499000, 61, 24, 'note', 4, 5),
       ('B660MA', 'Mainboard MSI PRO B660M-A WIFI DDR4', 3390000, 12, 24, 'note', 7, 5),
       ('BMAGB460', 'Mainboard MSI MAG B460 TOMAHAWK', 3799000, 23, 12, 'note', 7, 5),
       ('MPGB550', 'Mainboard MSI MPG B550 GAMING CARBON WIFI', 5699000, 42, 24, 'note', 7, 5),
       ('X570MG', 'Mainboard MSI MEG X570 GODLIKE', 24999000, 32, 24, 'note', 7, 5);

-- PSU
INSERT INTO products (product_code, name, price, stock_quantity, month_of_warranty, note,
                      supplier_id, category_id)
VALUES ('P80S', 'Nguồn máy tính Cooler Master MWE V2 - 650W - 80 Plus Bronze', 1549000, 12, 12, 'note',
        3, 6),
       ('PSP000157', 'Nguồn máy tính ASUS Asus Rog Thor 1200P - 1200W - 80 Plus Platinum - Full Modular (SP000157)',
        10900000, 25, 12, 'note', 1, 6),
       ('P9020231', 'Nguồn máy tính CORSAIR CP-9020231-NA - 750W - 80 Plus Gold - Full Modular (CP-9020231-NA)',
        2990000, 32, 24, 'note', 2, 6),
       ('PCX550F',
        'Nguồn máy tính CORSAIR CX550F RGB Black 80 Plus Bronze - 550W - 80 Plus Bronze - Full Modular (CP-9020216-NA)',
        1290000, 12, 24, 'note', 2, 6);

-- FAN
INSERT INTO products (product_code, name, price, stock_quantity, month_of_warranty, note,
                      supplier_id, category_id)
VALUES ('F0E3', 'Quạt case MSI RGB 12cm Fan (OE3-7G20001-809)', 89000, 14, 12, 'note', 7, 7),
       ('FML120', 'Quạt CORSAIR ML120 RGB', 2619000, 15, 12, 'note', 2, 7),
       ('FCM212', 'Quạt CPU CM Hyper 212 ARGB', 699000, 17, 24, 'note', 3, 7),
       ('FSP005549', 'Tản nhiệt nước AIO ASUS ROG STRIX LC II 240 ARGB (SP005549)', 4900000, 43, 24, 'note',
        1, 7);

-- Order
INSERT INTO orders (order_date, delivery_date, receive_date, delivery_location, total_amount, note, customer_id,
                    employee_id)
VALUES ('2023/07/13', '2023/07/13', '2023/07/15', 'Bến Lức Long An', 0, '', 1, 1),
       ('2023/06/13', '2023/06/13', '2023/06/15', 'Quận 1 Hồ Chí Minh', 0, '', 2, 1),
       ('2023/05/13', '2023/05/13', '2023/05/15', 'Quận 1 Hồ Chí Minh', 0, '', 2, 2),
       ('2023/04/13', '2023/04/13', '2023/04/15', 'Tân Bình Hồ Chí Minh', 0, '', 3, 2),
       ('2023/03/13', '2023/03/13', '2023/03/15', 'Bến Lức Long An', 0, '', 4, 1);
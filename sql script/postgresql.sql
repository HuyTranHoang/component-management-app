--Create Table Section
CREATE TABLE customers
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone   VARCHAR(10)  NOT NULL,
    email   VARCHAR(255) UNIQUE
);

CREATE TABLE departments
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE positions
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE suppliers
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255),
    website VARCHAR(255)
);

CREATE TABLE categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE products
(
    id                SERIAL PRIMARY KEY,
    product_code      VARCHAR(255),
    name              VARCHAR(255)     NOT NULL,
    price             DOUBLE PRECISION NOT NULL,
    minimum_price     DOUBLE PRECISION NOT NULL,
    stock_quantity    INTEGER          NOT NULL,
    month_of_warranty INTEGER,
    note              VARCHAR(255),
    description       VARCHAR(512),
    supplier_id       BIGINT REFERENCES suppliers (id),
    category_id       BIGINT REFERENCES categories (id)
);

CREATE INDEX idx_supplier_category
    ON products (supplier_id, category_id);

CREATE TABLE products_storage
(
    id              SERIAL PRIMARY KEY,
    import_quantity INTEGER NOT NULL,
    export_quantity INTEGER NOT NULL,
    date_of_storage DATE    NOT NULL,
    product_id      BIGINT REFERENCES products (id)
);

CREATE INDEX idx_product
    ON products_storage (product_id);

CREATE TABLE employees
(
    id                     SERIAL PRIMARY KEY,
    name                   VARCHAR(255)     NOT NULL,
    address                VARCHAR(255)     NOT NULL,
    phone                  VARCHAR(10)      NOT NULL,
    email                  VARCHAR(255)     NOT NULL UNIQUE,
    password               VARCHAR(255)     NOT NULL,
    salary                 DOUBLE PRECISION NOT NULL,
    image                  VARCHAR(255)     NOT NULL,
    citizen_identification VARCHAR(12)      NOT NULL UNIQUE,
    date_of_birth          DATE             NOT NULL,
    date_of_hire           DATE             NOT NULL,
    department_id          BIGINT REFERENCES departments (id),
    position_id            BIGINT REFERENCES positions (id)
);

CREATE INDEX idx_department_position
    ON employees (department_id, position_id);

CREATE TABLE orders
(
    id                SERIAL PRIMARY KEY,
    order_date        TIMESTAMP        NOT NULL,
    delivery_date     TIMESTAMP        NOT NULL,
    shipment_date     TIMESTAMP        NOT NULL,
    delivery_location VARCHAR(255)     NOT NULL,
    total_amount      DOUBLE PRECISION NOT NULL,
    note              varchar(255),
    customer_id       BIGINT REFERENCES customers (id),
    employee_id       BIGINT REFERENCES employees (id)
);

CREATE INDEX idx_customer_employee
    ON orders (customer_id, employee_id);

CREATE TABLE OrderDetail
(
    id           SERIAL PRIMARY KEY,
    quantity     INTEGER          NOT NULL,
    discount     DOUBLE PRECISION NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    order_id     BIGINT REFERENCES orders (id),
    product_id   BIGINT REFERENCES products (id)
);

CREATE INDEX idx_order_product
    ON OrderDetail (order_id, product_id);


-- Trigger Section
-- Trigger khi ProductStorage(import_quantity) tăng thì sẽ tăng Product(stock_quantity) lên
CREATE OR REPLACE FUNCTION increase_stock_quantity()
    RETURNS TRIGGER AS
$$
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity + NEW.import_quantity
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER increase_stock_trigger
    AFTER INSERT
    ON products_storage
    FOR EACH ROW
    WHEN (NEW.import_quantity > 0) -- Nếu ImportQuantity > 0 thì chạy func
EXECUTE FUNCTION increase_stock_quantity();

-- Trigger khi ProductStorage(export_quantity) tăng thì sẽ giảm Product(stock_quantity) xuống
CREATE OR REPLACE FUNCTION decrease_stock_quantity()
    RETURNS TRIGGER AS
$$
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity - NEW.export_quantity
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER decrease_stock_trigger
    AFTER INSERT
    ON products_storage
    FOR EACH ROW
    WHEN (NEW.export_quantity > 0) -- Nếu ExportQuantity > 0 thì chạy func
EXECUTE FUNCTION decrease_stock_quantity();

-- Insert Section
-- Customer
INSERT INTO customers (name, address, phone, email)
VALUES ('Hoàng Huy', 'Bến Lức, Long An', '070333444', 'hoanghuy@gmail.com'),
       ('Huỳnh Bảo', 'Cầu Kho, Hồ Chí Minh', '090444555', 'huynhbao@gmail.com'),
       ('Phát Đạt', 'Tân Bình, Hồ Chí Minh', '091555666', 'phatdat@gmail.com');

-- Department
INSERT INTO departments (name, description)
VALUES ('Nhân sự', 'Quản lý nhân viên trong công ty'),
       ('Bán hàng', 'Quản lý các loại sản phẩm, số lượng tồn kho, tư vấn cho khách hàng');

-- Position
INSERT INTO positions (name, description)
VALUES ('Truưởng phòng', ''),
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
        987654321123, '2001-01-01', '2023-07-07', 1, 2);

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
       ('Western Digital', 'wd@gmail.com', 'https://www.wd.com/');

-- Product
-- CPU
INSERT INTO products (product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, description,
                      supplier_id, category_id)
VALUES ('Ci512400', 'CPU INTEL Core i5-12400 (6C/12T, 2.50 GHz - 4.40 GHz, 18MB', 4390000, 3390000, 0, 12, 'note',
        'description', 5, 1),
       ('CG6405', 'CPU INTEL Pentium Gold G6405 (2C/4T, 4.10 GHz, 4MB) - 1200', 1599000, 1399000, 0, 12, 'note',
        'description', 5, 1),
       ('Ci912900K', 'CPU INTEL Core i9-12900K (16C/24T, 3.20 GHz - 5.20 GHz, 30MB) - 1700', 11499000, 10499000, 0, 24,
        'note', 'description', 5, 1),
       ('Ci712700', 'CPU INTEL Core i7-12700 (12C/20T, 4.90 GHz, 25MB) - 1700', 8399000, 7399000, 0, 24, 'note',
        'description', 5, 1);

-- SSD/HDD
INSERT INTO products (product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, description,
                      supplier_id, category_id)
VALUES ('SNV2', 'Ổ cứng gắn trong/ SSD Kingston NV2 1000GB M.2 2280 PCIe Gen 4.0 NVMe', 1459000, 1159000, 0, 12, 'note',
        'description', 6, 2),
       ('SA400', 'Ổ cứng SSD Kingston A400 480GB Sata 3 (SA400S37/480G)', 749000, 649000, 0, 12, 'note', 'description',
        6, 2),
       ('SSS870', 'Ổ cứng SSD Samsung 870 EVO 4TB SATA III 2.5 inch (MZ-77E4T0BW)', 9999000, 8999000, 0, 24, 'note',
        'description', 8, 2),
       ('SWDB1', 'Ổ cứng HDD Western Digital Blue 1TB 3.5" SATA 3 - WD10EZEX', 955000, 755000, 0, 24, 'note',
        'description', 9, 2);

-- Case
INSERT INTO products (product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, description,
                      supplier_id, category_id)
VALUES ('MB520', 'Thùng máy/ Case CM MasterBox MB520 ARGB', 1999000, 1599000, 0, 12, 'note', 'description', 3, 3),
       ('MB05W', 'Case máy tính Cooler Master MasterBox 5 White', 1539000, 1139000, 0, 12, 'note', 'description', 3, 3),
       ('M100A', 'Thùng máy/ Case MSI MAG FORCE M100A (4 Fan RGB)', 829000, 729000, 0, 24, 'note', 'description', 7, 3),
       ('M110R', 'Thùng máy/ Case MSI MPG GUNGNIR 110R WHITE (306-7G10W21-W57)', 2630000, 2330000, 0, 24, 'note',
        'description', 7, 3);

-- RAM
INSERT INTO products (product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, description,
                      supplier_id, category_id)
VALUES ('RCMK235', 'RAM desktop CORSAIR Vengeance LPX Black Heat spreader (1 x 16GB) DDR4 3200MHz', 1249000, 1149000, 0,
        12, 'note', 'description', 2, 4),
       ('RS13Z55', 'Bộ nhớ ram Gigabyte AORUS RGB 16GB (2x8GB) DDR4 3600 (2x Demo kit)', 3549000, 3349000, 0, 12,
        'note', 'description', 4, 4),
       ('RKFB432', 'RAM desktop KINGSTON Fury Beast RGB 16GB (2 x 8GB) DDR4 3200MHz (KF432C16BBAK2/16)', 1699000,
        1499000, 0, 24, 'note', 'description', 6, 4),
       ('RS16G33', 'RAM desktop GIGABYTE Aorus RGB 16GB DDR4-3333 (2 x 8GB) DDR4 2666MHz', 2950000, 2650000, 0, 24,
        'note', 'description', 4, 4);

-- Mainboard
INSERT INTO products (product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, description,
                      supplier_id, category_id)
VALUES ('H510MK', 'Mainboard ASUS PRIME H510M-K', 1689000, 1489000, 0, 12, 'note', 'description', 1, 5),
       ('H510MH', 'Mainboard GIGABYTE H510M-H', 1749000, 1549000, 0, 12, 'note', 'description', 4, 5),
       ('B660MA', 'Mainboard MSI PRO B660M-A WIFI DDR4', 3390000, 3090000, 0, 24, 'note', 'description', 7, 5),
       ('X670E', 'Mainboard ASUS ROG CROSSHAIR X670E EXTREME', 26690000, 24690000, 0, 24, 'note', 'description', 1, 5);

-- PSU
INSERT INTO products (product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, description,
                      supplier_id, category_id)
VALUES ('P80S', 'Nguồn máy tính Cooler Master MWE V2 - 650W - 80 Plus Bronze', 1549000, 1249000, 0, 12, 'note',
        'description', 3, 6),
       ('PSP000157', 'Nguồn máy tính ASUS Asus Rog Thor 1200P - 1200W - 80 Plus Platinum - Full Modular (SP000157)',
        10900000, 9900000, 0, 12, 'note', 'description', 1, 6),
       ('P9020231', 'Nguồn máy tính CORSAIR CP-9020231-NA - 750W - 80 Plus Gold - Full Modular (CP-9020231-NA)',
        2990000, 2790000, 0, 24, 'note', 'description', 2, 6),
       ('PCX550F',
        'Nguồn máy tính CORSAIR CX550F RGB Black 80 Plus Bronze - 550W - 80 Plus Bronze - Full Modular (CP-9020216-NA)',
        1290000, 1090000, 0, 24, 'note', 'description', 2, 6);

-- FAN
INSERT INTO products (product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, description,
                      supplier_id, category_id)
VALUES ('F0E3', 'Quạt case MSI RGB 12cm Fan (OE3-7G20001-809)', 89000, 79000, 0, 12, 'note', 'description', 7, 7),
       ('FML120', 'Quạt CORSAIR ML120 RGB', 2619000, 2419000, 0, 12, 'note', 'description', 2, 7),
       ('FCM212', 'Quạt CPU CM Hyper 212 ARGB', 699000, 599000, 0, 24, 'note', 'description', 3, 7),
       ('FSP005549', 'Tản nhiệt nước AIO ASUS ROG STRIX LC II 240 ARGB (SP005549)', 4900000, 4500000, 0, 24, 'note',
        'description', 1, 7);
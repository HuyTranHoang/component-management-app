
--Create Table Section
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           phone VARCHAR(10) NOT NULL,
                           email VARCHAR(255) UNIQUE
);

CREATE TABLE departments (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             description VARCHAR(255)
);

CREATE TABLE positions (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           description VARCHAR(255)
);

CREATE TABLE suppliers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255),
                           website VARCHAR(255)
);

CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description VARCHAR(255)
);

CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          product_code VARCHAR(255),
                          name VARCHAR(255) NOT NULL,
                          price DOUBLE PRECISION NOT NULL,
                          minimum_price DOUBLE PRECISION NOT NULL,
                          stock_quantity INTEGER NOT NULL,
                          month_of_warranty INTEGER,
                          note VARCHAR(255),
                          description VARCHAR(512),
                          supplier_id BIGINT REFERENCES suppliers(id),
                          category_id BIGINT REFERENCES categories(id)
);

CREATE INDEX idx_supplier_category
    ON products (supplier_id, category_id);

CREATE TABLE products_storage (
                                  id SERIAL PRIMARY KEY,
                                  import_quantity INTEGER NOT NULL,
                                  export_quantity INTEGER NOT NULL,
                                  date_of_storage DATE NOT NULL,
                                  product_id BIGINT REFERENCES products(id)
);

CREATE INDEX idx_product
    ON products_storage(product_id);

CREATE TABLE employees (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           phone VARCHAR(10) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           password VARCHAR(255) NOT NULL,
                           salary DOUBLE PRECISION NOT NULL,
                           image VARCHAR(255) NOT NULL,
                           citizen_identification VARCHAR(12) NOT NULL UNIQUE,
                           date_of_birth DATE NOT NULL,
                           date_of_hire DATE NOT NULL,
                           department_id BIGINT REFERENCES departments(id),
                           position_id BIGINT REFERENCES positions(id)
);

CREATE INDEX idx_department_position
    ON employees(department_id, position_id);

CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        order_date TIMESTAMP NOT NULL,
                        delivery_date TIMESTAMP NOT NULL,
                        shipment_date TIMESTAMP NOT NULL,
                        delivery_location VARCHAR(255) NOT NULL,
                        total_amount DOUBLE PRECISION NOT NULL,
                        note varchar(255),
                        customer_id BIGINT REFERENCES customers(id),
                        employee_id BIGINT REFERENCES employees(id)
);

CREATE INDEX idx_customer_employee
    ON orders (customer_id, employee_id);

CREATE TABLE OrderDetail (
                             id SERIAL PRIMARY KEY,
                             quantity INTEGER NOT NULL,
                             discount DOUBLE PRECISION NOT NULL,
                             total_amount DOUBLE PRECISION NOT NULL,
                             order_id BIGINT REFERENCES orders(id),
                             product_id BIGINT REFERENCES products(id)
);

CREATE INDEX idx_order_product
    ON OrderDetail (order_id, product_id);


-- Trigger Section
-- Trigger khi ProductStorage(import_quantity) tăng thì sẽ tăng Product(stock_quantity) lên
CREATE OR REPLACE FUNCTION increase_stock_quantity()
    RETURNS TRIGGER AS $$
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity + NEW.import_quantity
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER increase_stock_trigger
    AFTER INSERT ON products_storage
    FOR EACH ROW
    WHEN (NEW.import_quantity > 0)  -- Nếu ImportQuantity > 0 thì chạy func
EXECUTE FUNCTION increase_stock_quantity();

-- Trigger khi ProductStorage(export_quantity) tăng thì sẽ giảm Product(stock_quantity) xuống
CREATE OR REPLACE FUNCTION decrease_stock_quantity()
    RETURNS TRIGGER AS $$
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity - NEW.export_quantity
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER decrease_stock_trigger
    AFTER INSERT ON products_storage
    FOR EACH ROW
    WHEN (NEW.export_quantity > 0)  -- Nếu ExportQuantity > 0 thì chạy func
EXECUTE FUNCTION decrease_stock_quantity();

-- Insert Section
-- Customer
INSERT INTO customers (name, address, phone, email)
VALUES
    ('Hoàng Huy', 'Bến Lức, Long An', '070333444', 'hoanghuy@gmail.com'),
    ('Huỳnh Bảo', 'Cầu Kho, Hồ Chí Minh', '090444555', 'huynhbao@gmail.com'),
    ('Phát Đạt', 'Tân Bình, Hồ Chí Minh', '091555666', 'phatdat@gmail.com');

-- Department
INSERT INTO departments (name, description)
VALUES
    ('Nhân sự', 'Quản lý nhân viên trong công ty'),
    ('Bán hàng', 'Quản lý các loại sản phẩm, số lượng tồn kho, tư vấn cho khách hàng');

-- Position
INSERT INTO positions (name, description)
VALUES
    ('Truưởng phòng', ''),
    ('Quản lý', ''),
    ('Nhân viên', '');

-- Employee ( Mật khẩu default = 1234@abc
INSERT INTO employees (name, address, phone, email,
                       password, salary, image,
                       citizen_identification, date_of_birth, date_of_hire, department_id, position_id)
VALUES
    ('Thái Bảo', 'Bình Thạnh, Hồ Chí Minh', '093444555', 'thaibao@gmail.com',
     '44fddd8b9d8755a392567bc5a7f54a5dcee0a82c2f19fa1c3afc808bca6cc841', 5000000, 'defaultImg.jpg',
     0123456789123, '1994-01-01', '2023-06-06', 1, 2),
    ('Lê Vương', 'Gò Đen, Hồ Chí Minh', '0973334444', 'levuong@gmail.com',
     '44fddd8b9d8755a392567bc5a7f54a5dcee0a82c2f19fa1c3afc808bca6cc841', 7000000, 'defaultImg.jpg',
     987654321123, '2001-01-01', '2023-07-07', 1, 2);

-- Category
INSERT INTO categories (name, description)
VALUES
    ('CPU - Bộ vi xử lý', ''),
    ('SSD/HHD - Ổ cứng', ''),
    ('Case - Thùng máy', ''),
    ('Ram - Bộ nhớ', ''),
    ('Mainboard - Bo mạch chủ', ''),
    ('PSU - Nguồn máy tính', ''),
    ('Fan - Quạt tản nhiệt', '');

-- Supplier
INSERT INTO suppliers (name, email, website)
VALUES
    ('ASUS','asus@gmail.com', 'https://www.asus.com/'),
    ('CORSAIR','corsair@gmail.com', 'https://www.cosair.com/'),
    ('Cooler Master','coolermaster@gmail.com', 'https://www.coolermaster.com/'),
    ('GIGABYTE','gigabyte@gmail.com', 'https://www.gigabyte.com/'),
    ('INTEL','intel@gmail.com', 'https://www.intel.com/'),
    ('KINGSTON','kingston@gmail.com', 'https://www.kingston.com/'),
    ('MSI','msi@gmail.com', 'https://www.msi.com/'),
    ('SAMSUNG','samsung@gmail.com', 'https://www.samsung.com/'),
    ('Western Digital','wd@gmail.com', 'https://www.wd.com/');
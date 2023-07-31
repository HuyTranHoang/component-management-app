--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

-- Started on 2023-07-26 20:39:38

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 3453 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS '';


--
-- TOC entry 234 (class 1255 OID 18516)
-- Name: insert_products_storage(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.insert_products_storage() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO products_storage (import_quantity, export_quantity, product_id)
    VALUES (NEW.stock_quantity, 0, NEW.id);
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.insert_products_storage() OWNER TO postgres;

--
-- TOC entry 235 (class 1255 OID 18517)
-- Name: update_products_storage(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_products_storage() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.update_products_storage() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 214 (class 1259 OID 18518)
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 18524)
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.categories_id_seq OWNER TO postgres;

--
-- TOC entry 3455 (class 0 OID 0)
-- Dependencies: 215
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;


--
-- TOC entry 216 (class 1259 OID 18525)
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    address character varying(255) NOT NULL,
    phone character varying(10) NOT NULL,
    email character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 18531)
-- Name: customers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.customers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customers_id_seq OWNER TO postgres;

--
-- TOC entry 3456 (class 0 OID 0)
-- Dependencies: 217
-- Name: customers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.customers_id_seq OWNED BY public.customers.id;


--
-- TOC entry 218 (class 1259 OID 18532)
-- Name: departments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.departments (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.departments OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 18538)
-- Name: departments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.departments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.departments_id_seq OWNER TO postgres;

--
-- TOC entry 3457 (class 0 OID 0)
-- Dependencies: 219
-- Name: departments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.departments_id_seq OWNED BY public.departments.id;


--
-- TOC entry 220 (class 1259 OID 18539)
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employees (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    address character varying(255) NOT NULL,
    phone character varying(10) NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    salary double precision NOT NULL,
    image character varying(255) NOT NULL,
    citizen_identification character varying(12) NOT NULL,
    date_of_birth date NOT NULL,
    date_of_hire date NOT NULL,
    department_id bigint,
    position_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.employees OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 18545)
-- Name: employees_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.employees_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.employees_id_seq OWNER TO postgres;

--
-- TOC entry 3458 (class 0 OID 0)
-- Dependencies: 221
-- Name: employees_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.employees_id_seq OWNED BY public.employees.id;


--
-- TOC entry 222 (class 1259 OID 18546)
-- Name: order_detail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_detail (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    price double precision NOT NULL,
    quantity integer NOT NULL,
    discount integer NOT NULL,
    total_amount double precision NOT NULL,
    order_id bigint,
    product_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.order_detail OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 18550)
-- Name: order_detail_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_detail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.order_detail_id_seq OWNER TO postgres;

--
-- TOC entry 3459 (class 0 OID 0)
-- Dependencies: 223
-- Name: order_detail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.order_detail_id_seq OWNED BY public.order_detail.id;


--
-- TOC entry 224 (class 1259 OID 18551)
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders (
    id integer NOT NULL,
    order_date timestamp without time zone NOT NULL,
    delivery_date timestamp without time zone NOT NULL,
    receive_date timestamp without time zone NOT NULL,
    delivery_location character varying(255) NOT NULL,
    total_amount double precision NOT NULL,
    note character varying(255),
    is_cancelled boolean DEFAULT false,
    customer_id bigint ,
    employee_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.orders OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 18558)
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_id_seq OWNER TO postgres;

--
-- TOC entry 3460 (class 0 OID 0)
-- Dependencies: 225
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


--
-- TOC entry 226 (class 1259 OID 18559)
-- Name: positions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.positions (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.positions OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 18565)
-- Name: positions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.positions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.positions_id_seq OWNER TO postgres;

--
-- TOC entry 3461 (class 0 OID 0)
-- Dependencies: 227
-- Name: positions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.positions_id_seq OWNED BY public.positions.id;


--
-- TOC entry 228 (class 1259 OID 18566)
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
    id integer NOT NULL,
    product_code character varying(255) NOT NULL UNIQUE,
    name character varying(255) NOT NULL,
    price double precision NOT NULL,
    stock_quantity integer NOT NULL,
    month_of_warranty integer,
    note character varying(255),
    supplier_id bigint,
    category_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.products OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 18572)
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.products_id_seq OWNER TO postgres;

--
-- TOC entry 3462 (class 0 OID 0)
-- Dependencies: 229
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;


--
-- TOC entry 230 (class 1259 OID 18573)
-- Name: products_storage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products_storage (
    id integer NOT NULL,
    import_quantity integer NOT NULL,
    export_quantity integer NOT NULL,
    product_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.products_storage OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 18577)
-- Name: products_storage_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.products_storage_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.products_storage_id_seq OWNER TO postgres;

--
-- TOC entry 3463 (class 0 OID 0)
-- Dependencies: 231
-- Name: products_storage_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.products_storage_id_seq OWNED BY public.products_storage.id;


--
-- TOC entry 232 (class 1259 OID 18578)
-- Name: suppliers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.suppliers (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    email character varying(255),
    website character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);


ALTER TABLE public.suppliers OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 18584)
-- Name: suppliers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.suppliers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.suppliers_id_seq OWNER TO postgres;

--
-- TOC entry 3464 (class 0 OID 0)
-- Dependencies: 233
-- Name: suppliers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.suppliers_id_seq OWNED BY public.suppliers.id;


--
-- TOC entry 3220 (class 2604 OID 18585)
-- Name: categories id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);


--
-- TOC entry 3222 (class 2604 OID 18586)
-- Name: customers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers ALTER COLUMN id SET DEFAULT nextval('public.customers_id_seq'::regclass);


--
-- TOC entry 3224 (class 2604 OID 18587)
-- Name: departments id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.departments ALTER COLUMN id SET DEFAULT nextval('public.departments_id_seq'::regclass);


--
-- TOC entry 3226 (class 2604 OID 18588)
-- Name: employees id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees ALTER COLUMN id SET DEFAULT nextval('public.employees_id_seq'::regclass);


--
-- TOC entry 3228 (class 2604 OID 18589)
-- Name: order_detail id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_detail ALTER COLUMN id SET DEFAULT nextval('public.order_detail_id_seq'::regclass);


--
-- TOC entry 3230 (class 2604 OID 18590)
-- Name: orders id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- TOC entry 3233 (class 2604 OID 18591)
-- Name: positions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.positions ALTER COLUMN id SET DEFAULT nextval('public.positions_id_seq'::regclass);


--
-- TOC entry 3235 (class 2604 OID 18592)
-- Name: products id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);


--
-- TOC entry 3237 (class 2604 OID 18593)
-- Name: products_storage id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_storage ALTER COLUMN id SET DEFAULT nextval('public.products_storage_id_seq'::regclass);


--
-- TOC entry 3239 (class 2604 OID 18594)
-- Name: suppliers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suppliers ALTER COLUMN id SET DEFAULT nextval('public.suppliers_id_seq'::regclass);


--
-- TOC entry 3428 (class 0 OID 18518)
-- Dependencies: 214
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.categories VALUES (1, 'CPU - Bộ vi xử lý', '', '2023-07-24');
INSERT INTO public.categories VALUES (2, 'SSD/HHD - Ổ cứng', '', '2023-07-24');
INSERT INTO public.categories VALUES (3, 'Case - Thùng máy', '', '2023-07-24');
INSERT INTO public.categories VALUES (4, 'Ram - Bộ nhớ', '', '2023-07-24');
INSERT INTO public.categories VALUES (5, 'Mainboard - Bo mạch chủ', '', '2023-07-24');
INSERT INTO public.categories VALUES (6, 'PSU - Nguồn máy tính', '', '2023-07-24');
INSERT INTO public.categories VALUES (7, 'Fan - Quạt tản nhiệt', '', '2023-07-24');


--
-- TOC entry 3430 (class 0 OID 18525)
-- Dependencies: 216
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.customers VALUES (1, 'Trần Hoàng Huy', 'Bến Lức, Long An', '070333444', 'hoanghuy@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (2, 'Huỳnh Huỳnh Bảo', 'Cầu Kho, Hồ Chí Minh', '090444555', 'huynhbao@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (3, 'Trần Phát Đạt', 'Tân Bình, Hồ Chí Minh', '091555666', 'phatdat@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (4, 'Nguyễn Văn Giỏi', 'Quận 1, Hồ Chí Minh', '0901234567', 'vangioi@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (5, 'Trần Anh Phúc', 'Quận 2, Hồ Chí Minh', '0902345678', 'anhphuc@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (6, 'Lê Văn Cuờng', 'Quận 3, Hồ Chí Minh', '0903456789', 'vancuong@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (7, 'Phạm Thị Dung', 'Quận 4, Hồ Chí Minh', '0904567890', 'thidung@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (8, 'Hoàng Văn Võ', 'Quận 5, Hồ Chí Minh', '0905678901', 'vanvo@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (9, 'Nguyễn Thị Lan Anh', 'Quận 6, Hồ Chí Minh', '0906789012', 'lananh@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (10, 'Trần Văn Hưng', 'Quận 7, Hồ Chí Minh', '0907890123', 'vanhung@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (11, 'Lê Thị Thu Hường', 'Quận 8, Hồ Chí Minh', '0908901234', 'thuhuong@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (12, 'Phạm Gia Hưng', 'Quận 9, Hồ Chí Minh', '0909012345', 'giahung@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (13, 'Hoàng Thị Thắm', 'Quận 10, Hồ Chí Minh', '0900123456', 'thitham@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (14, 'Ngô Bá Khá', 'Quận 11, Hồ Chí Minh', '0932187645', 'khabanh@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (15, 'Phan Anh', 'Quận 12, Hồ Chí Minh', '0911223445', 'anhphan@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (16, 'Trần Ngọc Long', 'Bình Thạnh, Hồ Chí Minh', '0423534645', 'ngoclong@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (17, 'Phan Tấn Trung', 'Gò Vấp, Hồ Chí Minh', '0333333333', 'tantrung@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (18, 'Nguyễn Hoàng Long', 'Thủ Đức, Hồ Chí Minh', '0123234345', 'hoanglong@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (19, 'Trần Hào Nhật', 'Tân Phú, Hồ Chí Minh', '0912321123', 'haonhat@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (20, 'Nguyễn Quốc Duy', 'Bình Tân, Hồ Chí Minh', '0965687274', 'quocduy@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (21, 'Lê Hưng Thạnh', 'Phan Thiết, Bình Thuận', '0986727940', 'hungthanh@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (22, 'Nguyễn Anh Linh', 'Bảo Lộc, Lâm Đồng', '0949123324', 'anhlinh@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (23, 'Phan Yến Nhi', 'Phan Rang, Ninh Thuận', '0985123321', 'yennhi@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (24, 'Lâm Đình Khoa', 'Cao Lãnh, Đồng Tháp', '0966999999', 'dinhkhoa@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (26, 'Nguyễn Văn Đức', 'Yên Thành, Nghệ An', '0937123123', 'vanduc@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (25, 'Nguyễn Tuấn Anh', 'Hàm Thuận Nam, Bình Thuận', '0986345456', 'tuananh@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (27, 'Trần Mạnh Tiến', 'Bảo Lộc, Lâm Đồng', '0949987123', 'manhtien@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (28, 'Hồ Bình Nguyên', 'Phan Thiết, Bình Thuận', '0986321456', 'binhnguyen@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (29, 'Trần Thái Linh', 'Quận 1, Hồ Chí Minh', '0929929929', 'thailinh@gmail.com', '2023-07-24');
INSERT INTO public.customers VALUES (30, 'Mai Nam Hải', 'Quận 7, Hồ Chí Minh', '0929292292', 'namhai@gmail.com', '2023-07-24');


--
-- TOC entry 3432 (class 0 OID 18532)
-- Dependencies: 218
-- Data for Name: departments; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.departments VALUES (1, 'Nhân sự', 'Quản lý nhân viên trong công ty', '2023-07-24');
INSERT INTO public.departments VALUES (2, 'Bán hàng', 'Quản lý các loại sản phẩm, số lượng tồn kho, tư vấn cho khách hàng', '2023-07-24');


--
-- TOC entry 3434 (class 0 OID 18539)
-- Dependencies: 220
-- Data for Name: employees; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.employees VALUES (2, 'Lê Vương', 'Gò Đen, Hồ Chí Minh', '0973334444', 'levuong@gmail.com', '44fddd8b9d8755a392567bc5a7f54a5dcee0a82c2f19fa1c3afc808bca6cc841', 7000000, 'defaultImg.jpg', '987654321123', '2001-01-01', '2023-07-07', 2, 2, '2023-07-24');
INSERT INTO public.employees VALUES (3, 'Ngọc Sơn', 'Bình Thạnh, Hồ Chí Minh', '0123123120', 'ngocson@gmail.com', 'db44f649425f98656b5de78b633204e7d30c3d01d8f96d98527eefc9a1422ba1', 9000000, '1fbdd01d-3349-4394-83cb-4b554e3fe1de.jpg', '123123789789', '1998-07-09', '2023-07-27', 1, 1, '2023-07-26');
INSERT INTO public.employees VALUES (1, 'Thái Bảo', 'Bình Thạnh, Hồ Chí Minh', '0934445555', 'thaibao@gmail.com', '44fddd8b9d8755a392567bc5a7f54a5dcee0a82c2f19fa1c3afc808bca6cc841', 5000000, 'defaultImg.jpg', '123456789123', '1994-01-01', '2023-07-26', 1, 2, '2023-07-24');
INSERT INTO public.employees VALUES (4, 'Minh Thân', 'Bình Thạnh, Hồ Chí Minh', '0972727727', 'minhthan@gmail.com', 'db44f649425f98656b5de78b633204e7d30c3d01d8f96d98527eefc9a1422ba1', 11000000, '7afdfa98-ee82-4024-8fd2-2b2e3ca713e5.jpg', '098123765432', '1996-07-01', '2023-07-01', 2, 3, '2023-07-26');
INSERT INTO public.employees VALUES (5, 'Chí Bảo', 'Quận 1, Hồ Chí Minh', '0966696696', 'chibao@gmail.com', 'db44f649425f98656b5de78b633204e7d30c3d01d8f96d98527eefc9a1422ba1', 5500000, '2f7cb920-15d6-445e-880e-8aa5d366e999.jpg', '098766666632', '2003-07-01', '2023-07-01', 1, 3, '2023-07-26');


--
-- TOC entry 3436 (class 0 OID 18546)
-- Dependencies: 222
-- Data for Name: order_detail; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.order_detail VALUES (1, 'CPU INTEL Core i7-12700 (12C/20T, 4.90 GHz, 25MB) - 1700', 8399000, 1, 0, 8399000, 6, 4, '2023-07-24');
INSERT INTO public.order_detail VALUES (2, 'Ổ cứng SSD Kingston A2000 500GB M.2 NVMe', 1499000, 10, 15, 12741500, 7, 17, '2023-07-24');
INSERT INTO public.order_detail VALUES (3, 'Ổ cứng SSD Samsung 870 EVO 4TB SATA III 2.5 inch (MZ-77E4T0BW)', 9999000, 3, 3, 29097090, 8, 20, '2023-07-31');
INSERT INTO public.order_detail VALUES (4, 'CPU AMD Ryzen Threadripper 7250X (24C/48T, 3.2 GHz - 4.2 GHz, 144MB)', 48999000, 1, 0, 48999000, 9, 14, '2023-07-31');
INSERT INTO public.order_detail VALUES (5, 'Ổ cứng HDD Western Digital Blue 1TB 3.5" SATA 3 - WD10EZEX', 955000, 3, 2, 2807700, 10, 24, '2023-07-31');
INSERT INTO public.order_detail VALUES (6, 'CPU AMD Ryzen 7 3700X (8C/16T, 3.6 GHz - 4.4 GHz, 36MB)', 7799000, 2, 0, 15598000, 11, 8, '2023-07-31');
INSERT INTO public.order_detail VALUES (7, 'Ổ cứng SSD Kingston A400 480GB Sata 3 (SA400S37/480G)', 749000, 1, 0, 749000, 11, 16, '2023-07-31');
INSERT INTO public.order_detail VALUES (8, 'Ổ cứng HDD Western Digital Blue 1TB 3.5" SATA 3 - WD10EZEX', 955000, 5, 5, 4536250, 12, 24, '2023-07-31');
INSERT INTO public.order_detail VALUES (9, 'Ổ cứng SSD Samsung 860 EVO 1TB Sata 3', 2899000, 3, 0, 8697000, 13, 22, '2023-07-31');
INSERT INTO public.order_detail VALUES (10, 'Ổ cứng SSD Samsung 870 EVO 4TB SATA III 2.5 inch (MZ-77E4T0BW)', 9999000, 5, 5, 47495250, 14, 20, '2023-07-31');
INSERT INTO public.order_detail VALUES (11, 'Ổ cứng HDD Kingston KC600 2TB 2.5" SATA 3', 2599000, 1, 0, 2599000, 15, 19, '2023-07-31');
INSERT INTO public.order_detail VALUES (12, 'CPU AMD Ryzen Threadripper 5350X (16C/32T, 3.0 GHz - 4.2 GHz, 144MB)', 24999000, 1, 0, 24999000, 16, 10, '2023-07-31');
INSERT INTO public.order_detail VALUES (13, 'Ổ cứng HDD Kingston KC600 2TB 2.5" SATA 3', 2599000, 2, 0, 5198000, 17, 19, '2023-07-31');
INSERT INTO public.order_detail VALUES (14, 'Ổ cứng SSD Samsung 980 Pro 2TB M.2 NVMe PCIe 4.0', 6799000, 1, 0, 6799000, 18, 23, '2023-07-31');
INSERT INTO public.order_detail VALUES (15, 'CPU INTEL Core i7-12700 (12C/20T, 4.90 GHz, 25MB) - 1700', 8399000, 1, 0, 8399000, 19, 4, '2023-07-31');
INSERT INTO public.order_detail VALUES (16, 'Ổ cứng di động Western Digital My Passport 2TB USB 3.0', 1599000, 3, 0, 4797000, 20, 26, '2023-07-31');
INSERT INTO public.order_detail VALUES (17, 'CPU INTEL Core i5-12400 (6C/12T, 2.50 GHz - 4.40 GHz, 18MB', 4390000, 1, 0, 4390000, 21, 1, '2023-07-31');
INSERT INTO public.order_detail VALUES (18, 'Ổ cứng SSD Samsung 870 EVO 4TB SATA III 2.5 inch (MZ-77E4T0BW)', 9999000, 1, 5, 9499050, 21, 20, '2023-07-31');
INSERT INTO public.order_detail VALUES (19, 'CPU AMD Ryzen Threadripper 5350X (16C/32T, 3.0 GHz - 4.2 GHz, 144MB)', 24999000, 1, 5, 23749050, 21, 10, '2023-07-31');
INSERT INTO public.order_detail VALUES (20, 'CPU INTEL Pentium Gold G6405 (2C/4T, 4.10 GHz, 4MB) - 1200', 1599000, 2, 0, 3198000, 22, 2, '2023-07-31');
INSERT INTO public.order_detail VALUES (21, 'Ổ cứng gắn trong/ SSD Kingston NV2 1000GB M.2 2280 PCIe Gen 4.0 NVMe', 1459000, 2, 0, 2918000, 22, 15, '2023-07-31');
INSERT INTO public.order_detail VALUES (22, 'Ổ cứng SSD Kingston A400 480GB Sata 3 (SA400S37/480G)', 749000, 1, 0, 749000, 23, 16, '2023-07-31');
INSERT INTO public.order_detail VALUES (23, 'Ổ cứng HDD Western Digital Blue 1TB 3.5" SATA 3 - WD10EZEX', 955000, 1, 0, 955000, 23, 24, '2023-07-31');
INSERT INTO public.order_detail VALUES (24, 'Ổ cứng gắn trong/ SSD Kingston NV2 1000GB M.2 2280 PCIe Gen 4.0 NVMe', 1459000, 1, 0, 1459000, 23, 15, '2023-07-31');
INSERT INTO public.order_detail VALUES (25, 'Ổ cứng SSD Kingston A2000 500GB M.2 NVMe', 1499000, 1, 0, 1499000, 23, 17, '2023-07-31');
INSERT INTO public.order_detail VALUES (26, 'CPU INTEL Pentium Gold G6405 (2C/4T, 4.10 GHz, 4MB) - 1200', 1599000, 1, 0, 1599000, 23, 2, '2023-07-31');
INSERT INTO public.order_detail VALUES (27, 'Thùng máy/ Case CM MasterBox MB520 ARGB', 1999000, 1, 0, 1999000, 24, 27, '2023-07-31');
INSERT INTO public.order_detail VALUES (28, 'RAM desktop CORSAIR Vengeance LPX Black Heat spreader (1 x 16GB) DDR4 3200MHz', 1249000, 1, 0, 1249000, 24, 37, '2023-07-31');
INSERT INTO public.order_detail VALUES (29, 'Mainboard ASUS PRIME H510M-K', 1689000, 1, 0, 1689000, 24, 47, '2023-07-31');
INSERT INTO public.order_detail VALUES (30, 'Nguồn máy tính Cooler Master MWE V2 - 650W - 80 Plus Bronze', 1549000, 1, 0, 1549000, 24, 59, '2023-07-31');
INSERT INTO public.order_detail VALUES (31, 'Quạt CORSAIR ML120 RGB', 2619000, 1, 0, 2619000, 24, 64, '2023-07-31');
INSERT INTO public.order_detail VALUES (32, 'Thùng máy/ Case ASUS ROG Strix Helios GX601 (ATX/EATX)', 10999000, 1, 0, 10999000, 25, 36, '2023-07-31');
INSERT INTO public.order_detail VALUES (33, 'CPU AMD Ryzen Threadripper 7250X (24C/48T, 3.2 GHz - 4.2 GHz, 144MB)', 48999000, 1, 0, 48999000, 25, 14, '2023-07-31');
INSERT INTO public.order_detail VALUES (34, 'Ổ cứng SSD Samsung 980 Pro 2TB M.2 NVMe PCIe 4.0', 6799000, 1, 0, 6799000, 25, 23, '2023-07-31');
INSERT INTO public.order_detail VALUES (35, 'RAM desktop GIGABYTE Aorus RGB 16GB DDR4-3333 (2 x 8GB) DDR4 2666MHz', 2950000, 2, 0, 5900000, 25, 40, '2023-07-31');
INSERT INTO public.order_detail VALUES (36, 'Mainboard MSI MEG X570 GODLIKE', 24999000, 1, 0, 24999000, 25, 58, '2023-07-31');
INSERT INTO public.order_detail VALUES (37, 'Quạt case MSI RGB 12cm Fan (OE3-7G20001-809)', 89000, 4, 0, 356000, 26, 63, '2023-07-31');
INSERT INTO public.order_detail VALUES (38, 'RAM desktop Samsung DDR4 8GB (1x8GB) 2400MHz', 699000, 2, 0, 1398000, 27, 43, '2023-07-31');
INSERT INTO public.order_detail VALUES (39, 'Nguồn máy tính ASUS Asus Rog Thor 1200P - 1200W - 80 Plus Platinum - Full Modular (SP000157)', 10900000, 1, 0, 10900000, 28, 60, '2023-07-31');
INSERT INTO public.order_detail VALUES (40, 'Tản nhiệt nước AIO ASUS ROG STRIX LC II 240 ARGB (SP005549)', 4900000, 3, 0, 14700000, 28, 66, '2023-07-31');
INSERT INTO public.order_detail VALUES (41, 'CPU AMD Ryzen 9 3900X (12C/24T, 3.8 GHz - 4.6 GHz, 70MB)', 12999000, 1, 0, 12999000, 29, 6, '2023-07-31');
INSERT INTO public.order_detail VALUES (42, 'RAM desktop Samsung DDR4 32GB (2x16GB) 3600MHz', 2999000, 3, 5, 8547150, 29, 42, '2023-07-31');
INSERT INTO public.order_detail VALUES (43, 'Thùng máy/ Case CM MasterBox MB520 ARGB', 1999000, 4, 0, 7996000, 30, 27, '2023-07-31');
INSERT INTO public.order_detail VALUES (44, 'CPU INTEL Core i5-12400 (6C/12T, 2.50 GHz - 4.40 GHz, 18MB', 4390000, 4, 0, 17560000, 30, 1, '2023-07-31');
INSERT INTO public.order_detail VALUES (45, 'Thùng máy/ Case ASUS ROG Strix Helios GX601 (ATX/EATX)', 10999000, 1, 15, 9349150, 31, 36, '2023-07-31');
INSERT INTO public.order_detail VALUES (46, 'Nguồn máy tính CORSAIR CP-9020231-NA - 750W - 80 Plus Gold - Full Modular (CP-9020231-NA)', 2990000, 1, 0, 2990000, 32, 61, '2023-07-31');
INSERT INTO public.order_detail VALUES (47, 'Thùng máy/ Case ASUS TUF Gaming GT301 (ATX/mATX/Mini-ITX)', 1999000, 1, 0, 1999000, 32, 34, '2023-07-31');
INSERT INTO public.order_detail VALUES (48, 'Tản nhiệt nước AIO ASUS ROG STRIX LC II 240 ARGB (SP005549)', 4900000, 4, 0, 19600000, 33, 66, '2023-07-31');
INSERT INTO public.order_detail VALUES (49, 'Quạt CPU CM Hyper 212 ARGB', 699000, 4, 0, 2796000, 33, 65, '2023-07-31');
INSERT INTO public.order_detail VALUES (50, 'Quạt CORSAIR ML120 RGB', 2619000, 4, 0, 10476000, 33, 64, '2023-07-31');
INSERT INTO public.order_detail VALUES (51, 'Quạt case MSI RGB 12cm Fan (OE3-7G20001-809)', 89000, 4, 0, 356000, 33, 63, '2023-07-31');
INSERT INTO public.order_detail VALUES (52, 'Ổ cứng HDD Western Digital Blue 1TB 3.5" SATA 3 - WD10EZEX', 955000, 19, 15, 15423250, 34, 24, '2023-07-31');
INSERT INTO public.order_detail VALUES (53, 'Thùng máy/ Case MSI MAG FORCE M100A (4 Fan RGB)', 829000, 19, 15, 13388350, 34, 29, '2023-07-31');


--
-- TOC entry 3438 (class 0 OID 18551)
-- Dependencies: 224
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.orders VALUES (21, '2023-07-31 20:57:29.962384', '2023-07-31 20:57:29.962384', '2023-07-31 20:57:29.962384', 'Bình Thạnh, Hồ Chí Minh', 37638100, '', false, 3, 1, '2023-07-31');
INSERT INTO public.orders VALUES (26, '2023-08-04 21:07:50.631773', '2023-08-04 21:07:50.631773', '2023-08-04 21:07:50.631773', 'Quận 1, Hồ Chí Minh', 356000, '', false, 10, 1, '2023-07-31');
INSERT INTO public.orders VALUES (31, '2023-08-03 21:11:37.545233', '2023-08-03 21:11:37.545233', '2023-08-03 21:11:37.545233', 'Đống Đa, Hà Nội', 9349150, '', false, 30, 1, '2023-07-31');
INSERT INTO public.orders VALUES (7, '2023-07-31 20:38:44.594394', '2023-07-31 20:38:44.594394', '2023-07-31 20:38:44.594394', 'Quận 7, Hồ Chí Minh', 12741500, '', false, 28, 2, '2023-07-24');
INSERT INTO public.orders VALUES (8, '2023-08-01 20:42:40.045285', '2023-08-01 20:42:40.046276', '2023-08-01 20:42:40.046276', 'Từ Sơn, Bắc Ninh', 29097090, '', false, 14, 3, '2023-07-31');
INSERT INTO public.orders VALUES (9, '2023-08-02 20:44:10.404443', '2023-08-02 20:44:10.404443', '2023-08-02 20:44:10.404443', 'Gò Vấp, Hồ Chí Minh', 48999000, '', false, 17, 4, '2023-07-31');
INSERT INTO public.orders VALUES (10, '2023-08-03 20:45:06.670043', '2023-08-03 20:45:06.670043', '2023-08-03 20:45:06.670043', 'Cao Lãnh, Đồng Tháp', 2807700, '', false, 24, 5, '2023-07-31');
INSERT INTO public.orders VALUES (12, '2023-08-05 20:48:34.957202', '2023-08-05 20:48:34.957202', '2023-08-05 20:48:34.957202', 'Hàm Thuận Nam, Bình Thuận', 4536250, '', false, 25, 2, '2023-07-31');
INSERT INTO public.orders VALUES (13, '2023-08-06 20:49:29.245255', '2023-08-06 20:49:29.245255', '2023-08-06 20:49:29.245255', 'Bảo Lộc, Lâm Đồng', 8697000, '', false, 22, 3, '2023-07-31');
INSERT INTO public.orders VALUES (14, '2023-07-31 20:50:13.85416', '2023-07-31 20:50:13.85416', '2023-07-31 20:50:13.85416', 'Bình Thạnh, Hồ Chí Minh', 47495250, '', false, 24, 4, '2023-07-31');
INSERT INTO public.orders VALUES (15, '2023-08-01 20:51:01.933316', '2023-08-01 20:51:01.933316', '2023-08-01 20:51:01.933316', 'Phan Rang, Ninh Thuận', 2599000, '', false, 23, 5, '2023-07-31');
INSERT INTO public.orders VALUES (16, '2023-08-02 20:52:23.327737', '2023-08-02 20:52:23.327737', '2023-08-02 20:52:23.327737', 'Bình Thạnh, Hồ Chí Minh', 24999000, '', false, 26, 1, '2023-07-31');
INSERT INTO public.orders VALUES (17, '2023-08-03 20:54:37.536044', '2023-08-03 20:54:37.536044', '2023-08-03 20:54:37.536044', 'Aptech Bình Thạnh', 5198000, '', false, 9, 2, '2023-07-31');
INSERT INTO public.orders VALUES (18, '2023-08-04 20:55:31.823089', '2023-08-04 20:55:31.823089', '2023-08-04 20:55:31.823089', 'Quận 1, Hồ Chí Minh', 6799000, '', false, 18, 3, '2023-07-31');
INSERT INTO public.orders VALUES (19, '2023-08-05 20:56:18.07864', '2023-08-05 20:56:18.07864', '2023-08-05 20:56:18.07864', 'Thủ Đức, Hồ Chí Minh', 8399000, '', false, 19, 4, '2023-07-31');
INSERT INTO public.orders VALUES (20, '2023-08-06 20:56:51.877669', '2023-08-06 20:56:51.877669', '2023-08-06 20:56:51.877669', 'Quận 4, Hồ Chí Minh', 4797000, '', false, 8, 5, '2023-07-31');
INSERT INTO public.orders VALUES (22, '2023-08-01 21:00:33.706677', '2023-08-01 21:00:33.706677', '2023-08-01 21:00:33.706677', 'Phan Thiết, Bình Thuận', 6116000, '', false, 6, 2, '2023-07-31');
INSERT INTO public.orders VALUES (23, '2023-08-02 21:01:18.909315', '2023-08-02 21:01:18.909315', '2023-08-02 21:01:18.909315', 'Tân Bình, Hồ Chí Minh', 6261000, '', false, 4, 3, '2023-07-31');
INSERT INTO public.orders VALUES (24, '2023-08-06 21:03:22.633337', '2023-08-06 21:03:22.633337', '2023-08-06 21:03:22.633337', 'Tân Phú, Hồ Chí Minh', 9105000, '', false, 8, 4, '2023-07-31');
INSERT INTO public.orders VALUES (25, '2023-08-03 21:05:31.52696', '2023-08-03 21:05:31.52696', '2023-08-03 21:05:31.52696', 'Phan Thiết, Bình Thuận', 97696000, '', false, 28, 5, '2023-07-31');
INSERT INTO public.orders VALUES (27, '2023-08-05 21:08:21.255339', '2023-08-05 21:08:21.255339', '2023-08-05 21:08:21.255339', 'Quận 2, Hồ Chí Minh', 1398000, '', false, 11, 2, '2023-07-31');
INSERT INTO public.orders VALUES (28, '2023-07-31 21:08:58.969302', '2023-07-31 21:08:58.969302', '2023-07-31 21:08:58.969302', 'Quận 3, Hồ Chí Minh', 25600000, '', false, 7, 3, '2023-07-31');
INSERT INTO public.orders VALUES (29, '2023-08-01 21:09:41.537867', '2023-08-01 21:09:41.537867', '2023-08-01 21:09:41.537867', 'Bến Lức, Long An', 21546150, '', false, 1, 4, '2023-07-31');
INSERT INTO public.orders VALUES (30, '2023-08-02 21:10:29.758852', '2023-08-02 21:10:29.758852', '2023-08-02 21:10:29.758852', 'Bình Tân, Hồ Chí Minh', 25556000, '', false, 20, 5, '2023-07-31');
INSERT INTO public.orders VALUES (32, '2023-08-04 21:12:26.214607', '2023-08-04 21:12:26.214607', '2023-08-04 21:12:26.214607', 'Quận 11, Hồ Chí Minh', 4989000, '', false, 13, 2, '2023-07-31');
INSERT INTO public.orders VALUES (33, '2023-08-05 21:13:03.23913', '2023-08-05 21:13:03.23913', '2023-08-05 21:13:03.23913', 'Quận 12, Hồ Chí Minh', 33228000, '', false, 14, 3, '2023-07-31');
INSERT INTO public.orders VALUES (34, '2023-08-06 21:13:57.687072', '2023-08-06 21:13:57.687072', '2023-08-06 21:13:57.687072', 'Quận 3, Hồ Chí Minh', 28811600, '', false, 15, 4, '2023-07-31');
INSERT INTO public.orders VALUES (6, '2023-07-24 20:09:05.090888', '2023-07-24 20:09:05.090888', '2023-07-24 20:09:05.090888', 'Quận 1, Hồ Chí Minh', 8399000, '', true, 1, 1, '2023-07-24');
INSERT INTO public.orders VALUES (11, '2023-08-04 20:47:00.159333', '2023-08-04 20:47:00.159333', '2023-08-04 20:47:00.159333', 'Quận 7, Hồ Chí Minh', 16347000, NULL, true, 30, 1, '2023-07-31');


--
-- TOC entry 3440 (class 0 OID 18559)
-- Dependencies: 226
-- Data for Name: positions; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.positions VALUES (1, 'Trưởng phòng', '', '2023-07-24');
INSERT INTO public.positions VALUES (2, 'Quản lý', '', '2023-07-24');
INSERT INTO public.positions VALUES (3, 'Nhân viên', '', '2023-07-24');


--
-- TOC entry 3442 (class 0 OID 18566)
-- Dependencies: 228
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.products VALUES (3, 'Ci912900K', 'CPU INTEL Core i9-12900K (16C/24T, 3.20 GHz - 5.20 GHz, 30MB) - 1700', 10499000, 20, 24, 'note', 5, 1, '2023-07-24');
INSERT INTO public.products VALUES (5, 'R53600', 'CPU AMD Ryzen 5 3600 (6C/12T, 3.6 GHz - 4.2 GHz, 35MB)', 3699000, 15, 12, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (7, 'R72600X', 'CPU AMD Ryzen 7 2600X (6C/12T, 3.6 GHz - 4.2 GHz, 19MB)', 5799000, 5, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (9, 'R91900X', 'CPU AMD Ryzen 9 1900X (8C/16T, 3.8 GHz - 4.2 GHz, 20MB)', 9399000, 9, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (11, 'R73600X', 'CPU AMD Ryzen 7 3600X (6C/12T, 3.8 GHz - 4.4 GHz, 35MB)', 6799000, 2, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (12, 'R51600', 'CPU AMD Ryzen 5 1600 (6C/12T, 3.2 GHz - 3.6 GHz, 16MB)', 3299000, 9, 12, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (13, 'R73700', 'CPU AMD Ryzen 7 3700 (8C/16T, 3.6 GHz - 4.4 GHz, 36MB)', 7399000, 11, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (18, 'SUV500', 'Ổ cứng SSD Kingston UV500 1TB Sata 3', 2999000, 12, 24, 'note', 6, 2, '2023-07-24');
INSERT INTO public.products VALUES (21, 'SSS970', 'Ổ cứng SSD Samsung 970 EVO Plus 500GB M.2 NVMe', 1599000, 12, 12, 'note', 8, 2, '2023-07-24');
INSERT INTO public.products VALUES (25, 'SSN550', 'Ổ cứng SSD Western Digital WD Blue SN550 1TB M.2 NVMe', 1999000, 37, 12, 'note', 9, 2, '2023-07-24');
INSERT INTO public.products VALUES (28, 'MB05W', 'Case máy tính Cooler Master MasterBox 5 White', 1539000, 35, 12, 'note', 3, 3, '2023-07-24');
INSERT INTO public.products VALUES (30, 'M110R', 'Thùng máy/ Case MSI MPG GUNGNIR 110R WHITE (306-7G10W21-W57)', 2330000, 77, 24, 'note', 7, 3, '2023-07-24');
INSERT INTO public.products VALUES (31, 'GC300', 'Thùng máy/ Case GIGABYTE C300 Glass RGB (ATX/mATX/Mini-ITX)', 2399000, 13, 12, 'note', 4, 3, '2023-07-24');
INSERT INTO public.products VALUES (32, 'AORUSAC300G', 'Thùng máy/ Case GIGABYTE AORUS AC300G (ATX/mATX/Mini-ITX)', 2999000, 32, 24, 'note', 4, 3, '2023-07-24');
INSERT INTO public.products VALUES (33, 'C200G', 'Thùng máy/ Case GIGABYTE C200G (ATX/mATX/Mini-ITX)', 1799000, 12, 12, 'note', 4, 3, '2023-07-24');
INSERT INTO public.products VALUES (35, 'ASU200', 'Thùng máy/ Case ASUS ROG Strix Helios (ATX/EATX)', 8999000, 16, 24, 'note', 1, 3, '2023-07-24');
INSERT INTO public.products VALUES (38, 'RS13Z55', 'Bộ nhớ ram Gigabyte AORUS RGB 16GB (2x8GB) DDR4 3600 (2x Demo kit)', 3549000, 27, 12, 'note', 4, 4, '2023-07-24');
INSERT INTO public.products VALUES (39, 'RKFB432', 'RAM desktop KINGSTON Fury Beast RGB 16GB (2 x 8GB) DDR4 3200MHz (KF432C16BBAK2/16)', 1699000, 7, 24, 'note', 6, 4, '2023-07-24');
INSERT INTO public.products VALUES (41, 'SAM16G3200', 'RAM desktop Samsung DDR4 16GB (1x16GB) 3200MHz', 1399000, 6, 12, 'note', 8, 4, '2023-07-24');
INSERT INTO public.products VALUES (44, 'CORS8G2666', 'RAM desktop Corsair Vengeance LPX 8GB (1x8GB) DDR4 2666MHz', 799000, 3, 12, 'note', 2, 4, '2023-07-24');
INSERT INTO public.products VALUES (45, 'CORS16G3200', 'RAM desktop Corsair Vengeance RGB Pro 16GB (2x8GB) DDR4 3200MHz', 2499000, 2, 12, 'note', 2, 4, '2023-07-24');
INSERT INTO public.products VALUES (46, 'CORS32G3600', 'RAM desktop Corsair Dominator Platinum RGB 32GB (2x16GB) DDR4 3600MHz', 5599000, 7, 24, 'note', 2, 4, '2023-07-24');
INSERT INTO public.products VALUES (48, 'PRMH510', 'Mainboard ASUS PRIME H510M', 1899000, 4, 12, 'note', 1, 5, '2023-07-24');
INSERT INTO public.products VALUES (49, 'TUFZ590', 'Mainboard ASUS TUF GAMING Z590-PLUS (WI-FI 6)', 6599000, 1, 24, 'note', 1, 5, '2023-07-24');
INSERT INTO public.products VALUES (50, 'ROGX570', 'Mainboard ASUS ROG CROSSHAIR VIII FORMULA', 13399000, 3, 24, 'note', 1, 5, '2023-07-24');
INSERT INTO public.products VALUES (51, 'H510MH', 'Mainboard GIGABYTE H510M-H', 1749000, 5, 12, 'note', 4, 5, '2023-07-24');
INSERT INTO public.products VALUES (52, 'BMH410', 'Mainboard GIGABYTE B450M H', 2499000, 11, 12, 'note', 4, 5, '2023-07-24');
INSERT INTO public.products VALUES (53, 'AZ490', 'Mainboard GIGABYTE AORUS Z490 MASTER', 10999000, 51, 24, 'note', 4, 5, '2023-07-24');
INSERT INTO public.products VALUES (54, 'X570G', 'Mainboard GIGABYTE X570 AORUS ELITE WIFI', 6499000, 61, 24, 'note', 4, 5, '2023-07-24');
INSERT INTO public.products VALUES (55, 'B660MA', 'Mainboard MSI PRO B660M-A WIFI DDR4', 3390000, 12, 24, 'note', 7, 5, '2023-07-24');
INSERT INTO public.products VALUES (56, 'BMAGB460', 'Mainboard MSI MAG B460 TOMAHAWK', 3799000, 23, 12, 'note', 7, 5, '2023-07-24');
INSERT INTO public.products VALUES (57, 'MPGB550', 'Mainboard MSI MPG B550 GAMING CARBON WIFI', 5699000, 42, 24, 'note', 7, 5, '2023-07-24');
INSERT INTO public.products VALUES (40, 'RS16G33', 'RAM desktop GIGABYTE Aorus RGB 16GB DDR4-3333 (2 x 8GB) DDR4 2666MHz', 2950000, 7, 24, 'note', 4, 4, '2023-07-24');
INSERT INTO public.products VALUES (23, 'SSS980', 'Ổ cứng SSD Samsung 980 Pro 2TB M.2 NVMe PCIe 4.0', 6799000, 17, 24, 'note', 8, 2, '2023-07-24');
INSERT INTO public.products VALUES (22, 'SSS860', 'Ổ cứng SSD Samsung 860 EVO 1TB Sata 3', 2899000, 29, 36, 'note', 8, 2, '2023-07-24');
INSERT INTO public.products VALUES (8, 'R73700X', 'CPU AMD Ryzen 7 3700X (8C/16T, 3.6 GHz - 4.4 GHz, 36MB)', 7799000, 5, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (29, 'M100A', 'Thùng máy/ Case MSI MAG FORCE M100A (4 Fan RGB)', 829000, 23, 24, 'note', 7, 3, '2023-07-24');
INSERT INTO public.products VALUES (15, 'SNV2', 'Ổ cứng gắn trong/ SSD Kingston NV2 1000GB M.2 2280 PCIe Gen 4.0 NVMe', 1459000, 6, 12, 'note', 6, 2, '2023-07-24');
INSERT INTO public.products VALUES (10, 'RA53500X', 'CPU AMD Ryzen Threadripper 5350X (16C/32T, 3.0 GHz - 4.2 GHz, 144MB)', 24999000, 9, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (1, 'Ci512400', 'CPU INTEL Core i5-12400 (6C/12T, 2.50 GHz - 4.40 GHz, 18MB', 4390000, 5, 12, 'note', 5, 1, '2023-07-24');
INSERT INTO public.products VALUES (19, 'SKC600', 'Ổ cứng HDD Kingston KC600 2TB 2.5" SATA 3', 2599000, 40, 36, 'note', 6, 2, '2023-07-24');
INSERT INTO public.products VALUES (26, 'SUBWAY', 'Ổ cứng di động Western Digital My Passport 2TB USB 3.0', 1599000, 40, 24, 'note', 9, 4, '2023-07-24');
INSERT INTO public.products VALUES (36, 'ASU300', 'Thùng máy/ Case ASUS ROG Strix Helios GX601 (ATX/EATX)', 10999000, 15, 24, 'note', 1, 3, '2023-07-24');
INSERT INTO public.products VALUES (2, 'CG6405', 'CPU INTEL Pentium Gold G6405 (2C/4T, 4.10 GHz, 4MB) - 1200', 1599000, 2, 12, 'note', 5, 1, '2023-07-24');
INSERT INTO public.products VALUES (16, 'SA400', 'Ổ cứng SSD Kingston A400 480GB Sata 3 (SA400S37/480G)', 749000, 10, 12, 'note', 6, 2, '2023-07-24');
INSERT INTO public.products VALUES (37, 'RCMK235', 'RAM desktop CORSAIR Vengeance LPX Black Heat spreader (1 x 16GB) DDR4 3200MHz', 1249000, 16, 12, 'note', 2, 4, '2023-07-24');
INSERT INTO public.products VALUES (47, 'H510MK', 'Mainboard ASUS PRIME H510M-K', 1689000, 8, 12, 'note', 1, 5, '2023-07-24');
INSERT INTO public.products VALUES (34, 'ASU100', 'Thùng máy/ Case ASUS TUF Gaming GT301 (ATX/mATX/Mini-ITX)', 1999000, 14, 12, 'note', 1, 3, '2023-07-24');
INSERT INTO public.products VALUES (14, 'RA72500X', 'CPU AMD Ryzen Threadripper 7250X (24C/48T, 3.2 GHz - 4.2 GHz, 144MB)', 48999000, 0, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (43, 'SAM8G2400', 'RAM desktop Samsung DDR4 8GB (1x8GB) 2400MHz', 699000, 2, 12, 'note', 8, 4, '2023-07-24');
INSERT INTO public.products VALUES (6, 'R93900X', 'CPU AMD Ryzen 9 3900X (12C/24T, 3.8 GHz - 4.6 GHz, 70MB)', 12999000, 10, 24, 'note', 10, 1, '2023-07-24');
INSERT INTO public.products VALUES (42, 'SAM32G3600', 'RAM desktop Samsung DDR4 32GB (2x16GB) 3600MHz', 2999000, 2, 24, 'note', 8, 4, '2023-07-24');
INSERT INTO public.products VALUES (27, 'MB520', 'Thùng máy/ Case CM MasterBox MB520 ARGB', 1999000, 10, 12, 'note', 3, 3, '2023-07-24');
INSERT INTO public.products VALUES (24, 'WDB001', 'Ổ cứng HDD Western Digital Blue 1TB 3.5" SATA 3 - WD10EZEX', 955000, 0, 24, 'note', 9, 2, '2023-07-24');
INSERT INTO public.products VALUES (62, 'PCX550F', 'Nguồn máy tính CORSAIR CX550F RGB Black 80 Plus Bronze - 550W - 80 Plus Bronze - Full Modular (CP-9020216-NA)', 1290000, 12, 24, 'note', 2, 6, '2023-07-24');
INSERT INTO public.products VALUES (4, 'Ci712700', 'CPU INTEL Core i7-12700 (12C/20T, 4.90 GHz, 25MB) - 1700', 8399000, 20, 24, 'note', 5, 1, '2023-07-24');
INSERT INTO public.products VALUES (20, 'SSS870', 'Ổ cứng SSD Samsung 870 EVO 4TB SATA III 2.5 inch (MZ-77E4T0BW)', 9999000, 41, 24, 'note', 8, 2, '2023-07-24');
INSERT INTO public.products VALUES (17, 'SA2000', 'Ổ cứng SSD Kingston A2000 500GB M.2 NVMe', 1499000, 11, 12, 'note', 6, 2, '2023-07-24');
INSERT INTO public.products VALUES (59, 'P80S', 'Nguồn máy tính Cooler Master MWE V2 - 650W - 80 Plus Bronze', 1549000, 11, 12, 'note', 3, 6, '2023-07-24');
INSERT INTO public.products VALUES (58, 'X570MG', 'Mainboard MSI MEG X570 GODLIKE', 24999000, 31, 24, 'note', 7, 5, '2023-07-24');
INSERT INTO public.products VALUES (60, 'PSP000157', 'Nguồn máy tính ASUS Asus Rog Thor 1200P - 1200W - 80 Plus Platinum - Full Modular (SP000157)', 10900000, 24, 12, 'note', 1, 6, '2023-07-24');
INSERT INTO public.products VALUES (61, 'P9020231', 'Nguồn máy tính CORSAIR CP-9020231-NA - 750W - 80 Plus Gold - Full Modular (CP-9020231-NA)', 2990000, 31, 24, 'note', 2, 6, '2023-07-24');
INSERT INTO public.products VALUES (66, 'FSP005549', 'Tản nhiệt nước AIO ASUS ROG STRIX LC II 240 ARGB (SP005549)', 4900000, 36, 24, 'note', 1, 7, '2023-07-24');
INSERT INTO public.products VALUES (65, 'FCM212', 'Quạt CPU CM Hyper 212 ARGB', 699000, 13, 24, 'note', 3, 7, '2023-07-24');
INSERT INTO public.products VALUES (64, 'FML120', 'Quạt CORSAIR ML120 RGB', 2619000, 10, 12, 'note', 2, 7, '2023-07-24');
INSERT INTO public.products VALUES (63, 'F0E3', 'Quạt case MSI RGB 12cm Fan (OE3-7G20001-809)', 89000, 6, 12, 'note', 7, 7, '2023-07-24');


--
-- TOC entry 3444 (class 0 OID 18573)
-- Dependencies: 230
-- Data for Name: products_storage; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.products_storage VALUES (1, 10, 0, 1, '2023-07-24');
INSERT INTO public.products_storage VALUES (2, 5, 0, 2, '2023-07-24');
INSERT INTO public.products_storage VALUES (3, 20, 0, 3, '2023-07-24');
INSERT INTO public.products_storage VALUES (4, 22, 0, 4, '2023-07-24');
INSERT INTO public.products_storage VALUES (5, 15, 0, 5, '2023-07-24');
INSERT INTO public.products_storage VALUES (6, 11, 0, 6, '2023-07-24');
INSERT INTO public.products_storage VALUES (7, 5, 0, 7, '2023-07-24');
INSERT INTO public.products_storage VALUES (8, 7, 0, 8, '2023-07-24');
INSERT INTO public.products_storage VALUES (9, 9, 0, 9, '2023-07-24');
INSERT INTO public.products_storage VALUES (10, 11, 0, 10, '2023-07-24');
INSERT INTO public.products_storage VALUES (11, 2, 0, 11, '2023-07-24');
INSERT INTO public.products_storage VALUES (12, 9, 0, 12, '2023-07-24');
INSERT INTO public.products_storage VALUES (13, 11, 0, 13, '2023-07-24');
INSERT INTO public.products_storage VALUES (14, 2, 0, 14, '2023-07-24');
INSERT INTO public.products_storage VALUES (15, 9, 0, 15, '2023-07-24');
INSERT INTO public.products_storage VALUES (16, 12, 0, 16, '2023-07-24');
INSERT INTO public.products_storage VALUES (17, 22, 0, 17, '2023-07-24');
INSERT INTO public.products_storage VALUES (18, 12, 0, 18, '2023-07-24');
INSERT INTO public.products_storage VALUES (19, 43, 0, 19, '2023-07-24');
INSERT INTO public.products_storage VALUES (20, 50, 0, 20, '2023-07-24');
INSERT INTO public.products_storage VALUES (21, 12, 0, 21, '2023-07-24');
INSERT INTO public.products_storage VALUES (22, 32, 0, 22, '2023-07-24');
INSERT INTO public.products_storage VALUES (23, 19, 0, 23, '2023-07-24');
INSERT INTO public.products_storage VALUES (24, 28, 0, 24, '2023-07-24');
INSERT INTO public.products_storage VALUES (25, 37, 0, 25, '2023-07-24');
INSERT INTO public.products_storage VALUES (26, 43, 0, 26, '2023-07-24');
INSERT INTO public.products_storage VALUES (27, 15, 0, 27, '2023-07-24');
INSERT INTO public.products_storage VALUES (28, 35, 0, 28, '2023-07-24');
INSERT INTO public.products_storage VALUES (29, 42, 0, 29, '2023-07-24');
INSERT INTO public.products_storage VALUES (30, 77, 0, 30, '2023-07-24');
INSERT INTO public.products_storage VALUES (31, 13, 0, 31, '2023-07-24');
INSERT INTO public.products_storage VALUES (32, 32, 0, 32, '2023-07-24');
INSERT INTO public.products_storage VALUES (33, 12, 0, 33, '2023-07-24');
INSERT INTO public.products_storage VALUES (34, 15, 0, 34, '2023-07-24');
INSERT INTO public.products_storage VALUES (35, 16, 0, 35, '2023-07-24');
INSERT INTO public.products_storage VALUES (36, 17, 0, 36, '2023-07-24');
INSERT INTO public.products_storage VALUES (37, 17, 0, 37, '2023-07-24');
INSERT INTO public.products_storage VALUES (38, 27, 0, 38, '2023-07-24');
INSERT INTO public.products_storage VALUES (39, 7, 0, 39, '2023-07-24');
INSERT INTO public.products_storage VALUES (40, 9, 0, 40, '2023-07-24');
INSERT INTO public.products_storage VALUES (41, 6, 0, 41, '2023-07-24');
INSERT INTO public.products_storage VALUES (42, 5, 0, 42, '2023-07-24');
INSERT INTO public.products_storage VALUES (43, 4, 0, 43, '2023-07-24');
INSERT INTO public.products_storage VALUES (44, 3, 0, 44, '2023-07-24');
INSERT INTO public.products_storage VALUES (45, 2, 0, 45, '2023-07-24');
INSERT INTO public.products_storage VALUES (46, 7, 0, 46, '2023-07-24');
INSERT INTO public.products_storage VALUES (47, 9, 0, 47, '2023-07-24');
INSERT INTO public.products_storage VALUES (48, 4, 0, 48, '2023-07-24');
INSERT INTO public.products_storage VALUES (49, 1, 0, 49, '2023-07-24');
INSERT INTO public.products_storage VALUES (50, 3, 0, 50, '2023-07-24');
INSERT INTO public.products_storage VALUES (51, 5, 0, 51, '2023-07-24');
INSERT INTO public.products_storage VALUES (52, 11, 0, 52, '2023-07-24');
INSERT INTO public.products_storage VALUES (53, 51, 0, 53, '2023-07-24');
INSERT INTO public.products_storage VALUES (54, 61, 0, 54, '2023-07-24');
INSERT INTO public.products_storage VALUES (55, 12, 0, 55, '2023-07-24');
INSERT INTO public.products_storage VALUES (56, 23, 0, 56, '2023-07-24');
INSERT INTO public.products_storage VALUES (57, 42, 0, 57, '2023-07-24');
INSERT INTO public.products_storage VALUES (58, 32, 0, 58, '2023-07-24');
INSERT INTO public.products_storage VALUES (59, 12, 0, 59, '2023-07-24');
INSERT INTO public.products_storage VALUES (60, 25, 0, 60, '2023-07-24');
INSERT INTO public.products_storage VALUES (61, 32, 0, 61, '2023-07-24');
INSERT INTO public.products_storage VALUES (62, 12, 0, 62, '2023-07-24');
INSERT INTO public.products_storage VALUES (63, 14, 0, 63, '2023-07-24');
INSERT INTO public.products_storage VALUES (64, 15, 0, 64, '2023-07-24');
INSERT INTO public.products_storage VALUES (65, 17, 0, 65, '2023-07-24');
INSERT INTO public.products_storage VALUES (66, 43, 0, 66, '2023-07-24');
INSERT INTO public.products_storage VALUES (67, 0, 1, 4, '2023-07-24');
INSERT INTO public.products_storage VALUES (68, 0, 10, 17, '2023-07-24');
INSERT INTO public.products_storage VALUES (69, 0, 3, 20, '2023-07-31');
INSERT INTO public.products_storage VALUES (70, 0, 1, 14, '2023-07-31');
INSERT INTO public.products_storage VALUES (71, 0, 3, 24, '2023-07-31');
INSERT INTO public.products_storage VALUES (72, 0, 2, 8, '2023-07-31');
INSERT INTO public.products_storage VALUES (73, 0, 1, 16, '2023-07-31');
INSERT INTO public.products_storage VALUES (74, 0, 5, 24, '2023-07-31');
INSERT INTO public.products_storage VALUES (75, 0, 3, 22, '2023-07-31');
INSERT INTO public.products_storage VALUES (76, 0, 5, 20, '2023-07-31');
INSERT INTO public.products_storage VALUES (77, 0, 1, 19, '2023-07-31');
INSERT INTO public.products_storage VALUES (78, 0, 1, 10, '2023-07-31');
INSERT INTO public.products_storage VALUES (79, 0, 2, 19, '2023-07-31');
INSERT INTO public.products_storage VALUES (80, 0, 1, 23, '2023-07-31');
INSERT INTO public.products_storage VALUES (81, 0, 1, 4, '2023-07-31');
INSERT INTO public.products_storage VALUES (82, 0, 3, 26, '2023-07-31');
INSERT INTO public.products_storage VALUES (83, 0, 1, 1, '2023-07-31');
INSERT INTO public.products_storage VALUES (84, 0, 1, 20, '2023-07-31');
INSERT INTO public.products_storage VALUES (85, 0, 1, 10, '2023-07-31');
INSERT INTO public.products_storage VALUES (86, 0, 2, 2, '2023-07-31');
INSERT INTO public.products_storage VALUES (87, 0, 2, 15, '2023-07-31');
INSERT INTO public.products_storage VALUES (88, 0, 1, 16, '2023-07-31');
INSERT INTO public.products_storage VALUES (89, 0, 1, 24, '2023-07-31');
INSERT INTO public.products_storage VALUES (90, 0, 1, 15, '2023-07-31');
INSERT INTO public.products_storage VALUES (91, 0, 1, 17, '2023-07-31');
INSERT INTO public.products_storage VALUES (92, 0, 1, 2, '2023-07-31');
INSERT INTO public.products_storage VALUES (93, 0, 1, 27, '2023-07-31');
INSERT INTO public.products_storage VALUES (94, 0, 1, 37, '2023-07-31');
INSERT INTO public.products_storage VALUES (95, 0, 1, 47, '2023-07-31');
INSERT INTO public.products_storage VALUES (96, 0, 1, 59, '2023-07-31');
INSERT INTO public.products_storage VALUES (97, 0, 1, 64, '2023-07-31');
INSERT INTO public.products_storage VALUES (98, 0, 1, 36, '2023-07-31');
INSERT INTO public.products_storage VALUES (99, 0, 1, 14, '2023-07-31');
INSERT INTO public.products_storage VALUES (100, 0, 1, 23, '2023-07-31');
INSERT INTO public.products_storage VALUES (101, 0, 2, 40, '2023-07-31');
INSERT INTO public.products_storage VALUES (102, 0, 1, 58, '2023-07-31');
INSERT INTO public.products_storage VALUES (103, 0, 4, 63, '2023-07-31');
INSERT INTO public.products_storage VALUES (104, 0, 2, 43, '2023-07-31');
INSERT INTO public.products_storage VALUES (105, 0, 1, 60, '2023-07-31');
INSERT INTO public.products_storage VALUES (106, 0, 3, 66, '2023-07-31');
INSERT INTO public.products_storage VALUES (107, 0, 1, 6, '2023-07-31');
INSERT INTO public.products_storage VALUES (108, 0, 3, 42, '2023-07-31');
INSERT INTO public.products_storage VALUES (109, 0, 4, 27, '2023-07-31');
INSERT INTO public.products_storage VALUES (110, 0, 4, 1, '2023-07-31');
INSERT INTO public.products_storage VALUES (111, 0, 1, 36, '2023-07-31');
INSERT INTO public.products_storage VALUES (112, 0, 1, 61, '2023-07-31');
INSERT INTO public.products_storage VALUES (113, 0, 1, 34, '2023-07-31');
INSERT INTO public.products_storage VALUES (114, 0, 4, 66, '2023-07-31');
INSERT INTO public.products_storage VALUES (115, 0, 4, 65, '2023-07-31');
INSERT INTO public.products_storage VALUES (116, 0, 4, 64, '2023-07-31');
INSERT INTO public.products_storage VALUES (117, 0, 4, 63, '2023-07-31');
INSERT INTO public.products_storage VALUES (118, 0, 19, 24, '2023-07-31');
INSERT INTO public.products_storage VALUES (119, 0, 19, 29, '2023-07-31');


--
-- TOC entry 3446 (class 0 OID 18578)
-- Dependencies: 232
-- Data for Name: suppliers; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.suppliers VALUES (1, 'ASUS', 'asus@gmail.com', 'https://www.asus.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (2, 'CORSAIR', 'corsair@gmail.com', 'https://www.cosair.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (3, 'Cooler Master', 'coolermaster@gmail.com', 'https://www.coolermaster.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (4, 'GIGABYTE', 'gigabyte@gmail.com', 'https://www.gigabyte.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (5, 'INTEL', 'intel@gmail.com', 'https://www.intel.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (6, 'KINGSTON', 'kingston@gmail.com', 'https://www.kingston.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (7, 'MSI', 'msi@gmail.com', 'https://www.msi.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (8, 'SAMSUNG', 'samsung@gmail.com', 'https://www.samsung.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (9, 'Western Digital', 'wd@gmail.com', 'https://www.wd.com/', '2023-07-24');
INSERT INTO public.suppliers VALUES (10, 'AMD', 'amd@gmail.com', 'https://www.amd.com/', '2023-07-24');


--
-- TOC entry 3465 (class 0 OID 0)
-- Dependencies: 215
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categories_id_seq', 8, true);


--
-- TOC entry 3466 (class 0 OID 0)
-- Dependencies: 217
-- Name: customers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.customers_id_seq', 32, true);


--
-- TOC entry 3467 (class 0 OID 0)
-- Dependencies: 219
-- Name: departments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.departments_id_seq', 2, true);


--
-- TOC entry 3468 (class 0 OID 0)
-- Dependencies: 221
-- Name: employees_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.employees_id_seq', 6, true);


--
-- TOC entry 3469 (class 0 OID 0)
-- Dependencies: 223
-- Name: order_detail_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_detail_id_seq', 53, true);


--
-- TOC entry 3470 (class 0 OID 0)
-- Dependencies: 225
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.orders_id_seq', 34, true);


--
-- TOC entry 3471 (class 0 OID 0)
-- Dependencies: 227
-- Name: positions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.positions_id_seq', 3, true);


--
-- TOC entry 3472 (class 0 OID 0)
-- Dependencies: 229
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.products_id_seq', 66, true);


--
-- TOC entry 3473 (class 0 OID 0)
-- Dependencies: 231
-- Name: products_storage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.products_storage_id_seq', 119, true);


--
-- TOC entry 3474 (class 0 OID 0)
-- Dependencies: 233
-- Name: suppliers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.suppliers_id_seq', 10, true);


--
-- TOC entry 3242 (class 2606 OID 18596)
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- TOC entry 3244 (class 2606 OID 18598)
-- Name: customers customers_phone_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_phone_key UNIQUE (phone);


--
-- TOC entry 3246 (class 2606 OID 18600)
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);


--
-- TOC entry 3249 (class 2606 OID 18602)
-- Name: departments departments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


--
-- TOC entry 3251 (class 2606 OID 18604)
-- Name: employees employees_citizen_identification_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_citizen_identification_key UNIQUE (citizen_identification);


--
-- TOC entry 3253 (class 2606 OID 18606)
-- Name: employees employees_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_email_key UNIQUE (email);


--
-- TOC entry 3255 (class 2606 OID 18608)
-- Name: employees employees_phone_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_phone_key UNIQUE (phone);


--
-- TOC entry 3257 (class 2606 OID 18610)
-- Name: employees employees_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);


--
-- TOC entry 3261 (class 2606 OID 18612)
-- Name: order_detail order_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_pkey PRIMARY KEY (id);


--
-- TOC entry 3264 (class 2606 OID 18614)
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 3266 (class 2606 OID 18616)
-- Name: positions positions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_pkey PRIMARY KEY (id);


--
-- TOC entry 3269 (class 2606 OID 18618)
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- TOC entry 3272 (class 2606 OID 18620)
-- Name: products_storage products_storage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_storage
    ADD CONSTRAINT products_storage_pkey PRIMARY KEY (id);


--
-- TOC entry 3274 (class 2606 OID 18622)
-- Name: suppliers suppliers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suppliers
    ADD CONSTRAINT suppliers_pkey PRIMARY KEY (id);


--
-- TOC entry 3262 (class 1259 OID 18623)
-- Name: idx_customer_employee; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_customer_employee ON public.orders USING btree (customer_id, employee_id);


--
-- TOC entry 3258 (class 1259 OID 18624)
-- Name: idx_department_position; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_department_position ON public.employees USING btree (department_id, position_id);


--
-- TOC entry 3259 (class 1259 OID 18625)
-- Name: idx_order_product; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_order_product ON public.order_detail USING btree (order_id, product_id);


--
-- TOC entry 3270 (class 1259 OID 18626)
-- Name: idx_product; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_product ON public.products_storage USING btree (product_id);


--
-- TOC entry 3267 (class 1259 OID 18627)
-- Name: idx_supplier_category; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_supplier_category ON public.products USING btree (supplier_id, category_id);


--
-- TOC entry 3247 (class 1259 OID 18628)
-- Name: idx_unique_email; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_unique_email ON public.customers USING btree (email) WHERE ((email IS NOT NULL) AND ((email)::text <> ''::text));


--
-- TOC entry 3284 (class 2620 OID 18629)
-- Name: products insert_stock_trigger; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER insert_stock_trigger AFTER INSERT ON public.products FOR EACH ROW EXECUTE FUNCTION public.insert_products_storage();


--
-- TOC entry 3285 (class 2620 OID 18630)
-- Name: products update_stock_trigger; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER update_stock_trigger BEFORE UPDATE OF stock_quantity ON public.products FOR EACH ROW EXECUTE FUNCTION public.update_products_storage();


--
-- TOC entry 3275 (class 2606 OID 18631)
-- Name: employees employees_department_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_department_id_fkey FOREIGN KEY (department_id) REFERENCES public.departments(id);


--
-- TOC entry 3276 (class 2606 OID 18636)
-- Name: employees employees_position_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_position_id_fkey FOREIGN KEY (position_id) REFERENCES public.positions(id);


--
-- TOC entry 3277 (class 2606 OID 18641)
-- Name: order_detail order_detail_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;


--
-- TOC entry 3278 (class 2606 OID 18646)
-- Name: order_detail order_detail_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE CASCADE;


--
-- TOC entry 3279 (class 2606 OID 18651)
-- Name: orders orders_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(id) ON DELETE CASCADE;


--
-- TOC entry 3280 (class 2606 OID 18656)
-- Name: orders orders_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES public.employees(id) ON DELETE CASCADE ;


--
-- TOC entry 3281 (class 2606 OID 18661)
-- Name: products products_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE CASCADE;


--
-- TOC entry 3283 (class 2606 OID 18666)
-- Name: products_storage products_storage_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_storage
    ADD CONSTRAINT products_storage_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE CASCADE;


--
-- TOC entry 3282 (class 2606 OID 18671)
-- Name: products products_supplier_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_supplier_id_fkey FOREIGN KEY (supplier_id) REFERENCES public.suppliers(id) ON DELETE CASCADE;


--
-- TOC entry 3454 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;


-- Completed on 2023-07-26 20:39:38

--
-- PostgreSQL database dump complete
--


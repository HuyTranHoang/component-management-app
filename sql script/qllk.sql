PGDMP                         {            QLLK    15.3    15.3 d    z           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            {           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            |           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            }           1262    18515    QLLK    DATABASE     �   CREATE DATABASE "QLLK" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE "QLLK";
                postgres    false                        2615    2200    public    SCHEMA     2   -- *not* creating schema, since initdb creates it
 2   -- *not* dropping schema, since initdb creates it
                postgres    false            ~           0    0    SCHEMA public    COMMENT         COMMENT ON SCHEMA public IS '';
                   postgres    false    5                       0    0    SCHEMA public    ACL     +   REVOKE USAGE ON SCHEMA public FROM PUBLIC;
                   postgres    false    5            �            1255    18516    insert_products_storage()    FUNCTION       CREATE FUNCTION public.insert_products_storage() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO products_storage (import_quantity, export_quantity, product_id)
    VALUES (NEW.stock_quantity, 0, NEW.id);
    RETURN NEW;
END;
$$;
 0   DROP FUNCTION public.insert_products_storage();
       public          postgres    false    5            �            1255    18517    update_products_storage()    FUNCTION     3  CREATE FUNCTION public.update_products_storage() RETURNS trigger
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
 0   DROP FUNCTION public.update_products_storage();
       public          postgres    false    5            �            1259    18518 
   categories    TABLE     �   CREATE TABLE public.categories (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.categories;
       public         heap    postgres    false    5            �            1259    18524    categories_id_seq    SEQUENCE     �   CREATE SEQUENCE public.categories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.categories_id_seq;
       public          postgres    false    5    214            �           0    0    categories_id_seq    SEQUENCE OWNED BY     G   ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;
          public          postgres    false    215            �            1259    18525 	   customers    TABLE       CREATE TABLE public.customers (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    address character varying(255) NOT NULL,
    phone character varying(10) NOT NULL,
    email character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.customers;
       public         heap    postgres    false    5            �            1259    18531    customers_id_seq    SEQUENCE     �   CREATE SEQUENCE public.customers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.customers_id_seq;
       public          postgres    false    5    216            �           0    0    customers_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.customers_id_seq OWNED BY public.customers.id;
          public          postgres    false    217            �            1259    18532    departments    TABLE     �   CREATE TABLE public.departments (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.departments;
       public         heap    postgres    false    5            �            1259    18538    departments_id_seq    SEQUENCE     �   CREATE SEQUENCE public.departments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.departments_id_seq;
       public          postgres    false    5    218            �           0    0    departments_id_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.departments_id_seq OWNED BY public.departments.id;
          public          postgres    false    219            �            1259    18539 	   employees    TABLE     G  CREATE TABLE public.employees (
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
    DROP TABLE public.employees;
       public         heap    postgres    false    5            �            1259    18545    employees_id_seq    SEQUENCE     �   CREATE SEQUENCE public.employees_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.employees_id_seq;
       public          postgres    false    5    220            �           0    0    employees_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.employees_id_seq OWNED BY public.employees.id;
          public          postgres    false    221            �            1259    18546    order_detail    TABLE     V  CREATE TABLE public.order_detail (
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
     DROP TABLE public.order_detail;
       public         heap    postgres    false    5            �            1259    18550    order_detail_id_seq    SEQUENCE     �   CREATE SEQUENCE public.order_detail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE public.order_detail_id_seq;
       public          postgres    false    222    5            �           0    0    order_detail_id_seq    SEQUENCE OWNED BY     K   ALTER SEQUENCE public.order_detail_id_seq OWNED BY public.order_detail.id;
          public          postgres    false    223            �            1259    18551    orders    TABLE     �  CREATE TABLE public.orders (
    id integer NOT NULL,
    order_date timestamp without time zone NOT NULL,
    delivery_date timestamp without time zone NOT NULL,
    receive_date timestamp without time zone NOT NULL,
    delivery_location character varying(255) NOT NULL,
    total_amount double precision NOT NULL,
    note character varying(255),
    is_cancelled boolean DEFAULT false,
    customer_id bigint,
    employee_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.orders;
       public         heap    postgres    false    5            �            1259    18558    orders_id_seq    SEQUENCE     �   CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.orders_id_seq;
       public          postgres    false    224    5            �           0    0    orders_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;
          public          postgres    false    225            �            1259    18559 	   positions    TABLE     �   CREATE TABLE public.positions (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.positions;
       public         heap    postgres    false    5            �            1259    18565    positions_id_seq    SEQUENCE     �   CREATE SEQUENCE public.positions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.positions_id_seq;
       public          postgres    false    226    5            �           0    0    positions_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.positions_id_seq OWNED BY public.positions.id;
          public          postgres    false    227            �            1259    18566    products    TABLE     z  CREATE TABLE public.products (
    id integer NOT NULL,
    product_code character varying(255),
    name character varying(255) NOT NULL,
    price double precision NOT NULL,
    stock_quantity integer NOT NULL,
    month_of_warranty integer,
    note character varying(255),
    supplier_id bigint,
    category_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.products;
       public         heap    postgres    false    5            �            1259    18572    products_id_seq    SEQUENCE     �   CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.products_id_seq;
       public          postgres    false    5    228            �           0    0    products_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;
          public          postgres    false    229            �            1259    18573    products_storage    TABLE     �   CREATE TABLE public.products_storage (
    id integer NOT NULL,
    import_quantity integer NOT NULL,
    export_quantity integer NOT NULL,
    product_id bigint,
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
 $   DROP TABLE public.products_storage;
       public         heap    postgres    false    5            �            1259    18577    products_storage_id_seq    SEQUENCE     �   CREATE SEQUENCE public.products_storage_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.products_storage_id_seq;
       public          postgres    false    230    5            �           0    0    products_storage_id_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.products_storage_id_seq OWNED BY public.products_storage.id;
          public          postgres    false    231            �            1259    18578 	   suppliers    TABLE     �   CREATE TABLE public.suppliers (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    email character varying(255),
    website character varying(255),
    created_at date DEFAULT CURRENT_DATE NOT NULL
);
    DROP TABLE public.suppliers;
       public         heap    postgres    false    5            �            1259    18584    suppliers_id_seq    SEQUENCE     �   CREATE SEQUENCE public.suppliers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.suppliers_id_seq;
       public          postgres    false    232    5            �           0    0    suppliers_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.suppliers_id_seq OWNED BY public.suppliers.id;
          public          postgres    false    233            �           2604    18585    categories id    DEFAULT     n   ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);
 <   ALTER TABLE public.categories ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    215    214            �           2604    18586    customers id    DEFAULT     l   ALTER TABLE ONLY public.customers ALTER COLUMN id SET DEFAULT nextval('public.customers_id_seq'::regclass);
 ;   ALTER TABLE public.customers ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    217    216            �           2604    18587    departments id    DEFAULT     p   ALTER TABLE ONLY public.departments ALTER COLUMN id SET DEFAULT nextval('public.departments_id_seq'::regclass);
 =   ALTER TABLE public.departments ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    219    218            �           2604    18588    employees id    DEFAULT     l   ALTER TABLE ONLY public.employees ALTER COLUMN id SET DEFAULT nextval('public.employees_id_seq'::regclass);
 ;   ALTER TABLE public.employees ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    221    220            �           2604    18589    order_detail id    DEFAULT     r   ALTER TABLE ONLY public.order_detail ALTER COLUMN id SET DEFAULT nextval('public.order_detail_id_seq'::regclass);
 >   ALTER TABLE public.order_detail ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    223    222            �           2604    18590 	   orders id    DEFAULT     f   ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);
 8   ALTER TABLE public.orders ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    225    224            �           2604    18591    positions id    DEFAULT     l   ALTER TABLE ONLY public.positions ALTER COLUMN id SET DEFAULT nextval('public.positions_id_seq'::regclass);
 ;   ALTER TABLE public.positions ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    227    226            �           2604    18592    products id    DEFAULT     j   ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);
 :   ALTER TABLE public.products ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    229    228            �           2604    18593    products_storage id    DEFAULT     z   ALTER TABLE ONLY public.products_storage ALTER COLUMN id SET DEFAULT nextval('public.products_storage_id_seq'::regclass);
 B   ALTER TABLE public.products_storage ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    231    230            �           2604    18594    suppliers id    DEFAULT     l   ALTER TABLE ONLY public.suppliers ALTER COLUMN id SET DEFAULT nextval('public.suppliers_id_seq'::regclass);
 ;   ALTER TABLE public.suppliers ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    233    232            d          0    18518 
   categories 
   TABLE DATA           G   COPY public.categories (id, name, description, created_at) FROM stdin;
    public          postgres    false    214   {       f          0    18525 	   customers 
   TABLE DATA           P   COPY public.customers (id, name, address, phone, email, created_at) FROM stdin;
    public          postgres    false    216   N|       h          0    18532    departments 
   TABLE DATA           H   COPY public.departments (id, name, description, created_at) FROM stdin;
    public          postgres    false    218   �       j          0    18539 	   employees 
   TABLE DATA           �   COPY public.employees (id, name, address, phone, email, password, salary, image, citizen_identification, date_of_birth, date_of_hire, department_id, position_id, created_at) FROM stdin;
    public          postgres    false    220   ��       l          0    18546    order_detail 
   TABLE DATA           {   COPY public.order_detail (id, name, price, quantity, discount, total_amount, order_id, product_id, created_at) FROM stdin;
    public          postgres    false    222          n          0    18551    orders 
   TABLE DATA           �   COPY public.orders (id, order_date, delivery_date, receive_date, delivery_location, total_amount, note, is_cancelled, customer_id, employee_id, created_at) FROM stdin;
    public          postgres    false    224   h�       p          0    18559 	   positions 
   TABLE DATA           F   COPY public.positions (id, name, description, created_at) FROM stdin;
    public          postgres    false    226   7�       r          0    18566    products 
   TABLE DATA           �   COPY public.products (id, product_code, name, price, stock_quantity, month_of_warranty, note, supplier_id, category_id, created_at) FROM stdin;
    public          postgres    false    228   ��       t          0    18573    products_storage 
   TABLE DATA           h   COPY public.products_storage (id, import_quantity, export_quantity, product_id, created_at) FROM stdin;
    public          postgres    false    230   ѕ       v          0    18578 	   suppliers 
   TABLE DATA           I   COPY public.suppliers (id, name, email, website, created_at) FROM stdin;
    public          postgres    false    232   O�       �           0    0    categories_id_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.categories_id_seq', 7, true);
          public          postgres    false    215            �           0    0    customers_id_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.customers_id_seq', 32, true);
          public          postgres    false    217            �           0    0    departments_id_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('public.departments_id_seq', 2, true);
          public          postgres    false    219            �           0    0    employees_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.employees_id_seq', 2, true);
          public          postgres    false    221            �           0    0    order_detail_id_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.order_detail_id_seq', 53, true);
          public          postgres    false    223            �           0    0    orders_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.orders_id_seq', 34, true);
          public          postgres    false    225            �           0    0    positions_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.positions_id_seq', 3, true);
          public          postgres    false    227            �           0    0    products_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.products_id_seq', 66, true);
          public          postgres    false    229            �           0    0    products_storage_id_seq    SEQUENCE SET     G   SELECT pg_catalog.setval('public.products_storage_id_seq', 119, true);
          public          postgres    false    231            �           0    0    suppliers_id_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.suppliers_id_seq', 10, true);
          public          postgres    false    233            �           2606    18596    categories categories_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);
 D   ALTER TABLE ONLY public.categories DROP CONSTRAINT categories_pkey;
       public            postgres    false    214            �           2606    18598    customers customers_phone_key 
   CONSTRAINT     Y   ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_phone_key UNIQUE (phone);
 G   ALTER TABLE ONLY public.customers DROP CONSTRAINT customers_phone_key;
       public            postgres    false    216            �           2606    18600    customers customers_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY public.customers DROP CONSTRAINT customers_pkey;
       public            postgres    false    216            �           2606    18602    departments departments_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.departments DROP CONSTRAINT departments_pkey;
       public            postgres    false    218            �           2606    18604 .   employees employees_citizen_identification_key 
   CONSTRAINT     {   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_citizen_identification_key UNIQUE (citizen_identification);
 X   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_citizen_identification_key;
       public            postgres    false    220            �           2606    18606    employees employees_email_key 
   CONSTRAINT     Y   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_email_key UNIQUE (email);
 G   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_email_key;
       public            postgres    false    220            �           2606    18608    employees employees_phone_key 
   CONSTRAINT     Y   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_phone_key UNIQUE (phone);
 G   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_phone_key;
       public            postgres    false    220            �           2606    18610    employees employees_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_pkey;
       public            postgres    false    220            �           2606    18612    order_detail order_detail_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.order_detail DROP CONSTRAINT order_detail_pkey;
       public            postgres    false    222            �           2606    18614    orders orders_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
       public            postgres    false    224            �           2606    18616    positions positions_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY public.positions
    ADD CONSTRAINT positions_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY public.positions DROP CONSTRAINT positions_pkey;
       public            postgres    false    226            �           2606    18618    products products_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.products DROP CONSTRAINT products_pkey;
       public            postgres    false    228            �           2606    18620 &   products_storage products_storage_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.products_storage
    ADD CONSTRAINT products_storage_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.products_storage DROP CONSTRAINT products_storage_pkey;
       public            postgres    false    230            �           2606    18622    suppliers suppliers_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY public.suppliers
    ADD CONSTRAINT suppliers_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY public.suppliers DROP CONSTRAINT suppliers_pkey;
       public            postgres    false    232            �           1259    18623    idx_customer_employee    INDEX     \   CREATE INDEX idx_customer_employee ON public.orders USING btree (customer_id, employee_id);
 )   DROP INDEX public.idx_customer_employee;
       public            postgres    false    224    224            �           1259    18624    idx_department_position    INDEX     c   CREATE INDEX idx_department_position ON public.employees USING btree (department_id, position_id);
 +   DROP INDEX public.idx_department_position;
       public            postgres    false    220    220            �           1259    18625    idx_order_product    INDEX     Z   CREATE INDEX idx_order_product ON public.order_detail USING btree (order_id, product_id);
 %   DROP INDEX public.idx_order_product;
       public            postgres    false    222    222            �           1259    18626    idx_product    INDEX     N   CREATE INDEX idx_product ON public.products_storage USING btree (product_id);
    DROP INDEX public.idx_product;
       public            postgres    false    230            �           1259    18627    idx_supplier_category    INDEX     ^   CREATE INDEX idx_supplier_category ON public.products USING btree (supplier_id, category_id);
 )   DROP INDEX public.idx_supplier_category;
       public            postgres    false    228    228            �           1259    18628    idx_unique_email    INDEX     �   CREATE UNIQUE INDEX idx_unique_email ON public.customers USING btree (email) WHERE ((email IS NOT NULL) AND ((email)::text <> ''::text));
 $   DROP INDEX public.idx_unique_email;
       public            postgres    false    216    216            �           2620    18629    products insert_stock_trigger    TRIGGER     �   CREATE TRIGGER insert_stock_trigger AFTER INSERT ON public.products FOR EACH ROW EXECUTE FUNCTION public.insert_products_storage();
 6   DROP TRIGGER insert_stock_trigger ON public.products;
       public          postgres    false    228    234            �           2620    18630    products update_stock_trigger    TRIGGER     �   CREATE TRIGGER update_stock_trigger BEFORE UPDATE OF stock_quantity ON public.products FOR EACH ROW EXECUTE FUNCTION public.update_products_storage();
 6   DROP TRIGGER update_stock_trigger ON public.products;
       public          postgres    false    235    228    228            �           2606    18631 &   employees employees_department_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_department_id_fkey FOREIGN KEY (department_id) REFERENCES public.departments(id);
 P   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_department_id_fkey;
       public          postgres    false    220    218    3249            �           2606    18636 $   employees employees_position_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_position_id_fkey FOREIGN KEY (position_id) REFERENCES public.positions(id);
 N   ALTER TABLE ONLY public.employees DROP CONSTRAINT employees_position_id_fkey;
       public          postgres    false    226    220    3266            �           2606    18641 '   order_detail order_detail_order_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;
 Q   ALTER TABLE ONLY public.order_detail DROP CONSTRAINT order_detail_order_id_fkey;
       public          postgres    false    3264    222    224            �           2606    18646 )   order_detail order_detail_product_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE CASCADE;
 S   ALTER TABLE ONLY public.order_detail DROP CONSTRAINT order_detail_product_id_fkey;
       public          postgres    false    228    3269    222            �           2606    18651    orders orders_customer_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(id) ON DELETE CASCADE;
 H   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_customer_id_fkey;
       public          postgres    false    216    224    3246            �           2606    18656    orders orders_employee_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES public.employees(id);
 H   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_employee_id_fkey;
       public          postgres    false    3257    224    220            �           2606    18661 "   products products_category_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE CASCADE;
 L   ALTER TABLE ONLY public.products DROP CONSTRAINT products_category_id_fkey;
       public          postgres    false    214    228    3242            �           2606    18666 1   products_storage products_storage_product_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.products_storage
    ADD CONSTRAINT products_storage_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE CASCADE;
 [   ALTER TABLE ONLY public.products_storage DROP CONSTRAINT products_storage_product_id_fkey;
       public          postgres    false    228    230    3269            �           2606    18671 "   products products_supplier_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_supplier_id_fkey FOREIGN KEY (supplier_id) REFERENCES public.suppliers(id) ON DELETE CASCADE;
 L   ALTER TABLE ONLY public.products DROP CONSTRAINT products_supplier_id_fkey;
       public          postgres    false    232    3274    228            d   �   x�3�tU�Upz�{�BY�B���kr���4202�50�52�2�v���p*|�{�B���+��Q�s:'��C2��KW�=��E�	gPb.̢����g�H�r�&f�%�'���+�>ܵ09C!�r9�J3΀`����K�I���ڼe�n�y@e��@�JJ�Z��7�����1z\\\ ��S      f   �  x���OkA���O1�-�3�'{S+4`����e�����l-�B�"DT<y�ED�(��$+~��&�3���:��������y��M�&'���ê}#<lf�Z��)�[~�7�O�J��4�"�+&�̮%G�yU"��[$� h�t�o�c�
��
��U|�W�x�-_�ޞ�=!9
� �q#�J~�*7�Z��������n1�Ѥ}+����p�$I�1g���nn��E3떏$>��P�]�-�	t��g�.�!�Q���TBT�͍�ޫPм�-���9@L�c��nf�F�G#s��Z�I]L�̔μ#���s�e�'�[>�כ5r������6>� ���b����*�<�g+���ӥ����5X�������a`��R����Ο���I�X�=j���	o��aڐ#(m�����z�v�(8sQ���B�5��u�j����6\�Tj�JX5X��t���ϑ�֢$�	h��١߲
p����TaH@ �tS1�%6 �[>���Cz��*�]�%"4��R*�*?�{���n��OTs��_��?�K��L�'~��v�mӪ�w��l���2�z�(�!�Fuv��7C��Y�&��ԀUWpVI$X��/r73[k�;�I�R�F�~S�S��1������W�Xh�&���� x� 6�����K!+�j���.��Ԓ|��d���E	Y��AI�bw$�Xj����v̅y{���cqQj��!�ݕ�{\��
���"�S��^P����k���µ���=���~4�B�ۏ��=`���c�?!�)h���Է�H��8oto�f�����x�J�i_=s�53��6�{:	x�2 ��Z6P�Q	�Z��L�ME�S��B�7i��}���/���K�Dm�?��L`�y�F�Ǆ*( �b�z#�$Y	P7������kZ��      h   �   x�3���8�(O�����w-�S�9�W!,Z�yxU�BIQ~^�B��-@�������X��\�Ȅˈ����<����ґu'^�����p��L�� т���V��왨�sl��݋A�=�=9O!;#_G���������)$g���&d@F�0F��� �LQ      j     x���=N�0�zr
�D���- A���q�8J�]΁�	":�\ʠ�Gn��B�bKF���<�#��آռ{`5�ƀ�aލ1�����	���]�1 ֌s.��M0me�Ӧ7mWءνsNU�))�a��RVV�7�ٺ�FQK=��ˌ�
�ʚ�Z�	|8p�7�ns�7��}�2���N�y�I
PLY��H�ʜ����n���16p>}���:�H���~���O�<��J��3J�ʯF�,����"˲oe�z�      l   �  x��XMO�V]�_q�U�&�}��K��L u���C�@�U���ͪ���vXN�U�D�I�{vl��*�#x�̽��{ν/P��;��ɰs��<���S��e�#�W |���+`2j�C(>�x��	��Nyv�0�x��:{�Y?� ��ǟ�1mx=Iǋ�,���� 		�089���<"���!(>�(��jT�u�,Vx����)�a�0�n��@�$���Z���TGI�b���,�/�_a`�"�{°Dm��?$)��I|5���&sPL�Ԙh5��4q��,!=";�*��/�*^����LVa��p�,��<��d<Y�ShNW	P���@��.ڔt�vF�/e��9�#JeL2+���H����jPנp"C�]B��l���H�3�����o��~O�{/c��6���j�1�(8��C�jxoGI����A��E���r3y���y�"��"�U�۬��C��ڑ i�[�Eʶ(*:��b�b%9��4�3�Ϸ��OArc��	�g��D���贒���֦��4�6�\��7����a�V7A 8�TYsqF�s+��� ��-Q"G��&��w�����z�bq;�/���6��i�T�2�j�X�BζQHD��[��R�]�l���P/j:������-le�v��^��Ґ&Ӗ���Vb��l��٢��%w�$]NV7ΦW��H\�'�hN��5�H��`�+XO+�����ï),�tܰG��9J��ʘ��Vs�$��7�V�O7Q�Ք���/��{��8��]'ɏژ�w�����|�7��	�A��݅�L
Y�������߱䛏��Њ	�"�bݔ����d�~��U	��5�v͞�"�J�,g��:��nΓt���eǽ�8����x	�[�Q�g��P7D �v_ G����RU�3f�vV߉�I��,�_A08@�ߍ:p$)��Q�^$?ca��9�֏ߧ�X~�%�Ʃ8�by#]t USW���3]-���z���J��(VZ{�S��*����`':Ǝ�昹�ܗ����9{�ku�4��r>�Cr���K(Ԃ���o�8ê]ܼ@�r˧�Λ0����|���ɭUͥ%ɰ�/�fs��ndgTW�����B��u]�D�KR\:̶3Ʉ���R�ѠQ'���]�����;�܉0��n�(�7���wK���աL����j�^W�.����d-�B����:�̈́m��%�E������O <�Q
W���5F��B�;�xfs3�z�p�Z�7���'[WS��̮V� ��IeDKHE�9���ڟވ3\x�Bz=Y?~�����֏?]B�=��c�����!�ٖ%��X�l��Ѭ�\�A�2��4�o¸�+\�f�PD����7/p������L���y�ݬ]���+O
E��*������#_�
�eT����b�H��*#\�úE
����;���׼��'�p�����n��>�O��I� �=��R�֯�;��;��7g�����l��!�o�V�o ��o�$�Ի�+spk�
o�61}3�й��M���A����(���b*�ư�Vحݗ�
"T��K�߳TǴ�d��������O��Wv�O�l� ���~��b�	�a�̯����xۺ�|u����7����      n   �  x��V�nG��_q@-vfvwv��]ą#8��*!'�
�H���Y6� H� )�����I��#oN��*񉣷3ｙP�E:�|��D[�TYol�1�Lhq}��\\�)"�d�-���g����ߧX9g|r�\&��ϛ����y�rs�k�b�|*���� ;�-��
�c�w��UtX9k���( ������_�w������f��yyڒ�dKZ�H�Ҿv�\�8뜣L���������I7\L�P�����פ���l0��=�wO�^,.�W��z9/��*���l��^�(,s?��}�N^�+k�DD��qyr�6���j���YE��}W8V�L�,gB/��+iĖ��b%bi�����
�)��v�+:�]�Ta2(��>�~�9m�~;�˸>�v�*bH��Q�~��2�;YȮ��m��\v��z���ꡲ`�Ƀv�c���E]~����;�A?I�ʔ+$C�L�	=�t{;cPlXӻ�؈�$$2�gW7?�/���C�=��(D�I�+�lL�P�d��"�>٢⑔}� �1�,D�~�Gg�?�'D��'DTC�*JQ/Oc!eB}C�=������m�m��'�~$��P�EؒQ�A�P<(����3�μgˋ����� ��(��x��iЎ\v7&̓���C�%0,艨<C7���
�I�ôx�y�l�	�?�?
��9�cH!yh.��/�=�ZK$�"A���yHHfBG�����S y�	R0VFV(Qʄz8�Lh�U �#���U�"��h�;�
�<h�c�vl����6a��̚$$'�|$$��纽E�d}u)�3Y��]���W� ���M%�1z̄za�c���JJ� ��#�(jw�/҄/�¥y_�ʕvQ$r�W	��A�`\��	��25�KqXv*���H"� %�Cv,�^!B̢t�JI�&D����q��ح�����0���-6�R      p   K   x�3�):�����y�
�7�sr����pq�>ܵ8O!��^	cN��Ë��2��C������ 7��      r   /	  x��Y�n��^O��+����$%Q�LK%e�7�F�l!6H2`��.�MqWw�E�n/St՗��̐��$A�̌�7��;���q��)	�0�X�!��ְ������q��=X8�h�� �q���{\a��Q��(��G�l{XI��[}���x'I"-�(@?@��u���=�b\c����$�HĲs�ry��h�!��Qڵ��^}�y
D:9�<jabx$�w��^�����pM�LF<+��:1CE�VE,S�
Dԭe��n�ama�CxQXmek��R�.0K��@����Le�h�!��Th�N5\�^^I<���W�y}�{vi:��&����]`� ��a�{�R+���P�P�H���s"4]=��g܂�����{@�Q 1���*�ʣ��0�!^��)O�/���z��`s�9��a9���q��V�B)���#�g��$�\�p�_���_��������~��x����`���_�m������2���5�%1c4!����ƻ+�g�q�t�<���"��$���r<Y�gQ��D�.9�/��pˢ
�q��p$F�� E��~B�j���~	:��/���_�&��'�kT��:�$ N�Yr��JZ��>��v��5U|�`	�`�V��9	0S�$~z�[͆)$���n����f��q����aWIg�wI�2룔$x}�dw�/��n� ��v���a]I����'�ɍ�]�(
>���a�7�%R��ʩ�$�Xx��@�Q?�O�����L'Q��]��q�����
OP�E��Xfz��	���c	B��ũU������(U	iܠ,	"�eO��}	�Q�K�k�)]�p��n�m�nHǞ�_mvp��n׫�f��k��B�TS�SHEdTQ� ����8ERv��m,�e��ĳ5��#����ۇM�:lwXLW�M�����+4��1n����LT�̓x,�WīM��v�����<��CP;1an.FT�K����,.G�w"�!�c�=P���9.����hMv�j	Ԑ��3�Z:'�Ub��,M�>ϫ�d�Y_��EV��.��h��5��z�w�c���8U�5�p�c�0�QJ $�a|�<v�.1�?
�UJ^�.�~�&�֋�#Y�h6
�,�	T����5��hB��v�L�`�Mc�&Tu�y2����(���}�AC����@�Ԑ����a1�����b�.���VS�C���(°���2C?	�j�eQ�D�%%y	�	Yyg�{�WUT�޷�YOˢ�=I��h�n�P�)��N�TW^c80'k$���eY`*� \� �4_���"����E�T�`�-,�1�~��8��1���bR5����)�	�2��Ӽ�Rzq��6�}����mv{�d�x�d��+�(D�1z�`�B��`�Lyi�S�-�v�է���$���u>aZ��AO2!�	U��Ѹ�Ӭd\sF}��}�^M3����D1�27�(����� �<�NC�9i�5�]��;H������ve��ㆍ3L���K�������h��3̑�~���2������p�`Q�"��|��F�6euzx��Lv�CN��T��1_g���h{�e[mb5	�����
?��E��J��4�/ޘ��&WEt����_H-�L-cz材Ѯh��*	�)G>ݠ.Hit{n���~u�Ͷ:����u��ÊY�0%#���]���{��
ӟ� Wr�H�%��Y��5�Y!��ha��6G���u��,6M��%�:�����m�.��/�9�v&��)G�,���u��N*��fp�I�g���X�+�Ƿ���xq�3;>3�� W
���M.\��R>�u�f�E�H���0������T*?Ӯ�����RfVA�H��o�����F���ē��]����d^���}|}�K�x�(R0?��J��m�G� {�׵z��t�'��{���O��+��p��(�3��k�L���Ps6�q���8}�^	�%�+J��WS��ѣ)�(b���QBec51`��Mvs��c�q�bA��{����G!�J}N�*�j����t0
���ܥi�'��Q�! ���6�
�D��~����Ȥ��(����|2V��b]D��<���L���~�{E���X�p�U�k^�������x�|�X�:^/��p���0�;��E0[���-u�J ���E�	`�d�./���_���Av�y}���������'��X$�k8A���M�������ު?;9DIFa��6x|���T�a%?�΂�y+����Ŗ!G�Q|�B9���v8�M/�ZB���:�!ѡU^�d�zl�7���fC��D*gXߥ�z\k�g�)��޽{�q[      t   n  x�m�[r� E��d�z{���1zP)���R}XG�i�3�A���g��~xX ��ď`r3���f6(���|��zļъ��X7�c�7�h��{ӈ�nȵ�@�����.aR�A�<
:4;F��Z��=T��W��2�^L}c}xHQ0c�#����J
jlC2�n��Ϯ)�[*E��í"7�J��܄�A�M��`�I�A�M�#p���8O�wS���HBSp���A�Mv5���>�#
fJ#PӾU
^�nM��JG�E�J�:TAJ�BRp��!���~�y�쩧U�F�}��LrYPp2^��uY�̳�����n-/[�.b�Ϲ`���1�@-ªܜ�7�A��O:��uk9��s��|�������g
�A���	~M���+�弆l2��P{��n�#��y����p5ĕ5�^+�y%	@z%)wD�:B���� ڗ������ ������9�>����u��&���u�� lE���>�a�����_z��o��*M� m'[Hk��7���	/�^�:���}��3���*#�����R��O�Wj�_���(���X�}G[sC�Qԛ�N��2F�|J���>��u��;      v   �   x�u�AN�0E�?����i��
�V�I�:BbcR�X�1�]Yܞ��bay9���,@ؑ�۳}���.m�Ƨs��!˼�鄦e�"/��|=/VI����=�5��j�ȭ�`�/�1�ì�։a�L�����I�[��%yzkv�J�'"�+7�u�{���"��-�i]�f_�K��:�G�+kT�B[G8w`�bǺ��ڞ{q�h���UL��g[%���)���^� �\ǜ��{�$�/p[�Z     
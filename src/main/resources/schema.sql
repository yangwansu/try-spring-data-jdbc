CREATE TABLE IF NOT EXISTS product (id INTEGER IDENTITY PRIMARY KEY , name varchar(100), price BIGINT, createdAt bigint)

CREATE TABLE IF NOT EXISTS category_set (PRODUCT_ID INTEGER, name varchar(100))

CREATE TABLE IF NOT EXISTS category_map (PRODUCT_ID INTEGER, PRODUCT_KEY varchar (100), name varchar(100))

CREATE TABLE IF NOT EXISTS category_list (PRODUCT_ID INTEGER, PRODUCT_KEY varchar (100), name varchar(100))

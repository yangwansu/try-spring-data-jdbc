CREATE TABLE IF NOT EXISTS product (id INTEGER IDENTITY PRIMARY KEY , name varchar(100), price BIGINT, createdAt bigint)
CREATE TABLE IF NOT EXISTS category (PRODUCT_ID INTEGER  , name varchar(100))

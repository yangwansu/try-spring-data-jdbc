CREATE TABLE IF NOT EXISTS TEST_TABLE (id integer primary key identity, key1 bigint, key2  varchar(100), UNIQUE (key1, key2))
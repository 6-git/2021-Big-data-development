--drop table if exists data_yys.store;
--
--create table data_yys.store
--(
--  store_id VARCHAR(10) PRIMARY KEY
--);
--

--drop table if exists data_yys.customer;
--create table data_yys.customer
--(
--  customer_id VARCHAR(10) PRIMARY KEY,
--  customer_name VARCHAR(20),
--  phone_number   VARCHAR(11),
--  mail VARCHAR(50),
--  customer_age INT,
--  sex INT,
--  regist_date TIMESTAMP
--);
--

--drop table if exists data_yys.goods;
--create table data_yys.goods
--(
--  goods_id VARCHAR(10) PRIMARY KEY,
--  kind VARCHAR(20),
--  single_price INT
--);
--
--drop table if exists data_yys.sell;
create table data_yys.sell
(
  sell_id VARCHAR(10) PRIMARY KEY,
  sell_city VARCHAR(20),
  customer_id VARCHAR(10),
  goods_id VARCHAR(10),
  goods_num INT,
  buy_time TIMESTAMP,
  total_price INT,
  foreign key(goods_id) references data_yys.goods(goods_id),
  foreign key(customer_id) references data_yys.customer(customer_id)
);
--
--

--按月查看总体销售额
--select to_char(buy_time , 'YYYY-MM'), sum(total_price)  as total_price
--from data_yys.sell
--group by to_char(buy_time , 'YYYY-MM')
--
--按月查看新增注册人数
--select to_char(regist_date, 'YYYY-MM'), count(customer_id) as new_customers
--from data_yys.customer
--group by to_char(regist_date, 'YYYY-MM')
--
--按月查看城市的销售额
--select to_char(buy_time , 'YYYY-MM'), sell_city, sum(total_price) from data_yys.sell
--group by to_char(buy_time , 'YYYY-MM'), sell_city 
--
--按月查看城市、商品类别的销售额
--select to_char(data_yys.sell.buy_time , 'YYYY-MM'), data_yys.sell.sell_city,data_yys.goods.kind , sum(data_yys.sell.total_price) as total_price
--from data_yys.sell left join data_yys.goods on data_yys.sell.goods_id = data_yys.goods.goods_id 
--group by to_char(data_yys.sell.buy_time , 'YYYY-MM'), data_yys.sell.sell_city, data_yys.goods.kind 
--
--按月查看性别、商品类别的销售额
select to_char(data_yys.sell.buy_time , 'YYYY-MM') as bought_time, data_yys.customer.sex , data_yys.goods.kind , sum(data_yys.sell.total_price) as total_price
from data_yys.sell 
left join data_yys.goods on data_yys.sell.goods_id = data_yys.goods.goods_id 
left join data_yys.customer on data_yys.sell.customer_id = data_yys.customer.customer_id
group by to_char(data_yys.sell.buy_time , 'YYYY-MM'), data_yys.customer.sex, data_yys.goods.kind



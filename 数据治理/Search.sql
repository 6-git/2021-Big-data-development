--���²鿴�������۶�
--select to_char(buy_time , 'YYYY-MM'), sum(total_price)  as total_price
--from data_yys.sell
--group by to_char(buy_time , 'YYYY-MM')
--
--���²鿴����ע������
--select to_char(regist_date, 'YYYY-MM'), count(customer_id) as new_customers
--from data_yys.customer
--group by to_char(regist_date, 'YYYY-MM')
--
--���²鿴���е����۶�
--select to_char(buy_time , 'YYYY-MM'), sell_city, sum(total_price) from data_yys.sell
--group by to_char(buy_time , 'YYYY-MM'), sell_city 
--
--���²鿴���С���Ʒ�������۶�
--select to_char(data_yys.sell.buy_time , 'YYYY-MM'), data_yys.sell.sell_city,data_yys.goods.kind , sum(data_yys.sell.total_price) as total_price
--from data_yys.sell left join data_yys.goods on data_yys.sell.goods_id = data_yys.goods.goods_id 
--group by to_char(data_yys.sell.buy_time , 'YYYY-MM'), data_yys.sell.sell_city, data_yys.goods.kind 
--
--���²鿴�Ա���Ʒ�������۶�
select to_char(data_yys.sell.buy_time , 'YYYY-MM') as bought_time, data_yys.customer.sex , data_yys.goods.kind , sum(data_yys.sell.total_price) as total_price
from data_yys.sell 
left join data_yys.goods on data_yys.sell.goods_id = data_yys.goods.goods_id 
left join data_yys.customer on data_yys.sell.customer_id = data_yys.customer.customer_id
group by to_char(data_yys.sell.buy_time , 'YYYY-MM'), data_yys.customer.sex, data_yys.goods.kind



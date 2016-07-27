--数据库初始化脚本

--创建数据库
CREATE DATABASE seckill;

use seckill;
create table seckill(
	`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	`name` varchar(120) NOT NULL COMMENT '商品名称',
	`number` int not null comment '库存数量',
	`start_time` timestamp not null comment '秒杀开始时间',
	`end_time` timestamp not null comment '秒杀结束时间',
	create_time timestamp not null default current_timestamp comment '创建时间',
 	primary key (seckill_id),
 	key idx_start_time(start_time),
 	key idx_end_time(end_time),
 	key idx_create_time(create_time)
))ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

insert into seckill(name,number,start_time,end_time)
values('100元秒杀小米4c',10,'2016-07-27 00:00:00','2016-07-30 00:00:00'),
('800元秒杀Iphone',10,'2016-06-28 00:00:00','2016-06-30 00:00:00'),
('700元秒杀Ipad2',10,'2016-07-28 00:00:00','2016-07-30 00:00:00'),
('100元秒杀红米note',10,'2016-07-29 00:00:00','2016-0-30 00:00:007');

create table success_killed(
	`seckill_id` bigint not null auto_increment comment '商品库存id',
	`user_phone` bigint comment '用户电话',
	`state` tinyint not null default -1 comment '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
	create_time timestamp not null default current_timestamp comment '创建时间',
 	primary key (seckill_id,user_phone),/*联合主键*/
 	key idx_create_time(create_time)
)Engine=InnoDB auto_increment=1000 default charset=utf8 comment='秒杀成功明细表';


--为什么手写DDL
--记录每次上线的DDL修改 
--上线V1.1
ALTER TABLE SECKILL
DROP INDEX IDX_CREATE_TIME
ADD INDEX IDX_C_S(SECKILL_ID,USER_PHONE)
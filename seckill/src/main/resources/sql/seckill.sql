-- 存储过程
-- 存储过程优化的是事务行级锁持有的时间
-- 不要过度依赖存储过程, 简单的逻辑可以应用存储过程
-- QPS:一个秒杀单6000/qps

--执行秒杀存储过程
DELIMITER $$
CREATE procedure `seckill`.`execute_seckill`(
	in v_seckill_id bigint,in v_phone bigint,
	in v_kill_time timestamp, out r_result int
)

begin
	 declare insert_count int default 0;
	 start transaction;
	 insert ignore into success_killed (seckill_id,user_phone,create_time)
	 values(v_seckill_id,v_phone,v_kill_time);
	 
	 select row_count() into insert_count ;
	 
	 IF(insert_count=0) then
	 	ROLLBACK;
	 	set r_result=-1;
	 ELSEIF (insert_count < 0) then
	 	rollback;
	 	set r_result=-2;
	 ELSE
	 	update seckill
		 set number=number-1 
		 where seckill_id=V_seckill_id
		 	and end_time>v_kill_time
		 	and start_time <v_kill_time
		 	and number>0;
		 	
		 	select row_count() into insert_count;
		  IF(insert_count=0) then
	 		ROLLBACK;
	 		set r_result=0;
	 	  ELSEIF (insert_count<0) then
	 		rollback;
	 		set r_result=-2;
	 	  else
	 	  	commit;
	 	  	set r_result=1;
	 	  end IF;
	 end IF;
end 
$$


delimiter ;
set @r_result=-3;
call execute_seckill(1000,18649739856,now(),@r_result);

select @r_result;
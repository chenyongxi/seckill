package com.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessKilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessKilled;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;

//@Component
@Service
public class SeckillServiceImpl implements SeckillService {

	private static final Logger logger = LoggerFactory
			.getLogger(SeckillServiceImpl.class);

	// 注入service 依赖
	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	@Autowired
	private RedisDao redisDao;

	// md5 盐值字符串，用于混淆MD5
	private final String salt = "123456yyyy#$%(((kkkk123";

	private String getMD5(String seckillId) {
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(String seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(String seckillId) {
		
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			} else {
				redisDao.putSeckill(seckill);
			}
		}

		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime()
				|| nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(),
					startTime.getTime(), endTime.getTime());
		}
		// 转换特定的字符串
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	/**
	 * 使用注解事务控制的优点： 1.开发团队达成一致约定，明确标注事务方法的编程风格。
	 * 2：保证事务方法的执行时间尽可能断，不要穿插其他网络操作，RPC/HTTP 请求/如果还是需要，尽可能剥离到事务方法外部。
	 * 3：不是所有的方法都需要事务，如只有一条修改操作，制度操作不需要事务控制， 可以去看行级锁。
	 */
	@Override
	@Transactional
	public SeckillExecution executeSeckill(String seckillId, long userPhone,
			String md5) {

		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		// 执行秒杀逻辑，减库存
		Date nowTime = new Date();

		try {
			int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
			if (updateCount <= 0) {
				// 没有更新到数据库，秒杀结束
				throw new SeckillCloseException("seckill is close");
			} else {
				// 记录购买行为
				int insertCount = successKilledDao.insertSuccessKilled(
						seckillId, userPhone);
				if (insertCount <= 0) {
					// 重复秒杀
					throw new RepeatKillException("Seckill  repeated");
				} else {
					SuccessKilled successKilled = successKilledDao
							.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,
							SeckillStatEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SeckillException("seckill inner error:" + e.getMessage());
		}
	}

	@Override
	public SeckillExecution executeSeckillProcedure(String seckillId,
			long userPhone, String md5) {
		if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException(
					SeckillStatEnum.DATA_REWRITE.getStateInfo());
		}

		Date killTime = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		// 执行存储过程 result被赋值
		try {
			seckillDao.killByProcedure(map);
			// 获取result
			int result = MapUtils.getInteger(map, "result", -2);
			if (result == 1) {
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(
						seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,
						sk);
			} else {
				return new SeckillExecution(seckillId,
						SeckillStatEnum.stateOf(result));
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
	}

}

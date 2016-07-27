package com.seckill.dao.cache;

import com.seckill.entity.Seckill;
import com.seckill.utils.SerializableHelper;

import org.slf4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 关于缓存的数据进行处理的DAO 层 数据操作层
 * @author lenovo
 *
 */
public class RedisDao {
	
	private final Logger logger=null;
	private final JedisPool jedisPool;

	public RedisDao(String ip, int port) {
		jedisPool = new JedisPool(ip, port);
	}

	private SerializableHelper<Seckill> serizableHelper=new SerializableHelper<Seckill>(Seckill.class);
	/**
	 * 如何使用方法就可以了。
	 * @param seckillId
	 * @return
	 */
	public Seckill getSeckill(String seckillId) {
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				byte[] bytes = jedis.get(key.getBytes());
				if (bytes != null) {
					Seckill seckill = serizableHelper.deserialize(bytes);
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}

	/**
	 * 
	 * @param seckill
	 * @return
	 */
	public String putSeckill(Seckill seckill) {
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				byte[] bytes =serizableHelper.serialize(seckill);
				int timeout = 60 * 60;
				String result = jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}

}

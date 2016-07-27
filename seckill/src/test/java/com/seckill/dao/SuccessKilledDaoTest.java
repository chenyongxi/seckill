package com.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seckill.entity.SuccessKilled;

/**
 * Created by wchb7 on 16-5-9.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception {
        String id = "1000";
        long phone = 15811112222L;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount: " + insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {

    	String id = "1000";
        long phone = 15811112222L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        if (successKilled != null) {
            System.out.println(successKilled.getSeckill());
        }
    }
}
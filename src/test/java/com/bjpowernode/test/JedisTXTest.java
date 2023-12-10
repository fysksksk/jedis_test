package com.bjpowernode.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.resps.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JedisTXTest {

    private JedisPool jedisPool = new JedisPool("192.168.170.128", 6379);

    @Test
    public void test01() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("name", "张三");
            jedis.mset("age", "23");

            Transaction tx = jedis.multi();
            try {
                tx.set("name", "李四");
                // 构造出一个Java异常
                int i = 3 / 0;
                tx.set("age", "24");
                tx.exec();
            } catch (Exception e) {
                // 发生异常全部回滚
                tx.discard();
            } finally {
                System.out.println("name = " + jedis.get("name"));
                System.out.println("age = " + jedis.get("age"));
            }
        }
    }

    @Test
    public void test02() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("name", "张三");
            jedis.mset("age", "23");

            Transaction tx = jedis.multi();
            try {
                tx.set("name", "李四");
                // 构造出一个Redis异常
                tx.incr("name");
                tx.set("age", "24");
                tx.exec();
            } catch (Exception e) {
                // 发生异常全部回滚
                tx.discard();
            } finally {
                System.out.println("name = " + jedis.get("name"));
                System.out.println("age = " + jedis.get("age"));
            }
        }
    }

    @Test
    public void test03() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.mset("age", "23");
            System.out.println("age增一前的值 = " + jedis.get("age"));

            jedis.watch("age");
            Transaction tx = jedis.multi();
            try {
                tx.incr("age");
                tx.exec();
            } catch (Exception e) {
                tx.discard();
            } finally {
                System.out.println("age增一后的值 = " + jedis.get("age"));
            }
        }
    }
}

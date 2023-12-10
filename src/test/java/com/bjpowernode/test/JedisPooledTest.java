package com.bjpowernode.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JedisPooledTest {

    private JedisPooled jedis = new JedisPooled("192.168.170.128", 6379);

    // value为String的情况
    @Test
    public void test01() {
        jedis.set("name", "张三");
        jedis.mset("age", "23", "depart", "市场部");

        System.out.println("name = " + jedis.get("name"));
        System.out.println("age = " + jedis.get("age"));
        System.out.println("depart = " + jedis.get("depart"));
    }

    // value为Hash的情况
    @Test
    public void test02() {
        jedis.hset("emp", "name", "Tom");

        Map<String, String> map = new HashMap<>();
        map.put("age", "24");
        map.put("depart", "行政部");
        jedis.hset("emp", map);

        System.out.println("emp.name = " + jedis.hget("emp", "name"));
        List<String> emp = jedis.hmget("emp", "name", "age", "depart");
        System.out.println("emp = " + emp);
    }

    // value为List的情况
    @Test
    public void test03() {
        jedis.rpush("cities", "北京", "上海", "广州");
        List<String> cities = jedis.lrange("cities", 0, -1);
        System.out.println("cities = " + cities);
    }

    // value为Set的情况
    @Test
    public void test04() {
        jedis.rpush("cities", "北京", "上海", "广州");
        List<String> cities = jedis.lrange("cities", 0, -1);
        System.out.println("cities = " + cities);
    }

    // value为ZSet的情况
    @Test
    public void test05() {
        jedis.zadd("sales", 50, "Benz");
        jedis.zadd("sales", 40, "BMW");
        jedis.zadd("sales", 30, "Audi");
        jedis.zadd("sales", 80, "Tesla");
        jedis.zadd("sales", 120, "BYD");

        List<String> top3 = jedis.zrevrange("sales", 0, 2);
        System.out.println("top3 = " + top3);

        List<Tuple> sales = jedis.zrevrangeWithScores("sales", 0, -1);
        for (Tuple sale : sales) {
            System.out.println(sale.getScore() + ":" + sale.getElement());
        }
    }

}

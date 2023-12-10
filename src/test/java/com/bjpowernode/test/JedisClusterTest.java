package com.bjpowernode.test;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

public class JedisClusterTest {

    private JedisCluster jedisCluster;

    {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.170.128", 6380));
        nodes.add(new HostAndPort("192.168.170.128", 6381));
        nodes.add(new HostAndPort("192.168.170.128", 6382));
        nodes.add(new HostAndPort("192.168.170.128", 6383));
        nodes.add(new HostAndPort("192.168.170.128", 6384));
        nodes.add(new HostAndPort("192.168.170.128", 6385));
        System.out.println("Hello World");
        jedisCluster = new JedisCluster(nodes);
    }

    // value为String的情况
    @Test
    public void test01() {
        jedisCluster.set("name", "张三");
        // 跨槽
        jedisCluster.mset("age{emp}", "23", "depart{emp}", "市场部");

        System.out.println("name = " + jedisCluster.get("name"));
        System.out.println("age = " + jedisCluster.get("age{emp}"));
        System.out.println("depart = " + jedisCluster.get("depart{emp}"));
    }

    // value为Hash的情况
    @Test
    public void test02() {
        jedisCluster.hset("emp", "name", "Tom");

        Map<String, String> map = new HashMap<>();
        map.put("age", "24");
        map.put("depart", "行政部");
        jedisCluster.hset("emp", map);

        System.out.println("emp.name = " + jedisCluster.hget("emp", "name"));
        List<String> emp = jedisCluster.hmget("emp", "name", "age", "depart");
        System.out.println("emp = " + emp);
    }

    // value为List的情况
    @Test
    public void test03() {
        jedisCluster.rpush("cities", "北京", "上海", "广州");
        List<String> cities = jedisCluster.lrange("cities", 0, -1);
        System.out.println("cities = " + cities);
    }

    // value为Set的情况
    @Test
    public void test04() {
        jedisCluster.rpush("cities", "北京", "上海", "广州");
        List<String> cities = jedisCluster.lrange("cities", 0, -1);
        System.out.println("cities = " + cities);
    }

    // value为ZSet的情况
    @Test
    public void test05() {
        jedisCluster.zadd("sales", 50, "Benz");
        jedisCluster.zadd("sales", 40, "BMW");
        jedisCluster.zadd("sales", 30, "Audi");
        jedisCluster.zadd("sales", 80, "Tesla");
        jedisCluster.zadd("sales", 120, "BYD");

        List<String> top3 = jedisCluster.zrevrange("sales", 0, 2);
        System.out.println("top3 = " + top3);

        List<Tuple> sales = jedisCluster.zrevrangeWithScores("sales", 0, -1);
        for (Tuple sale : sales) {
            System.out.println(sale.getScore() + ":" + sale.getElement());
        }
    }

}

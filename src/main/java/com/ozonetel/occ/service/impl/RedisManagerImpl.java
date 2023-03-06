/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.RedisDao;
import com.ozonetel.occ.service.RedisManager;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.resps.Tuple;

/**
 *
 * @author pavanj
 * @param <T> model type
 */
public class RedisManagerImpl<T> implements RedisManager<T> {

    protected RedisDao<T> redisDao;

    public void setRedisDao(RedisDao<T> redisDao) {
        this.redisDao = redisDao;
    }

    public RedisManagerImpl(RedisDao<T> redisDao) {
        this.redisDao = redisDao;
    }

    @Override
    public String saveAsJson(String key, T obj) {
        key = StringUtils.lowerCase(key);
        return redisDao.saveAsJson(key, obj);
    }

    @Override
    public String saveex(String key, int seconds, T obj) {
        return redisDao.saveex(StringUtils.lowerCase(key), seconds, obj);
    }

    @Override
    public Long addToSet(String set, String member) {
        set = StringUtils.lowerCase(set);
        return redisDao.addToSet(set, member);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String setString(String key, String value) {
        key = StringUtils.lowerCase(key);
        return redisDao.setString(key, value);
    }

    @Override
    public String getString(String key) {
        key = StringUtils.lowerCase(key);
        return redisDao.getString(key);
    }

    @Override
    public T getFromJson(String key) {
        key = StringUtils.lowerCase(key);
        return redisDao.getFromJson(key);
    }

    @Override
    public Long deleteFromSet(String set, String member) {
        set = StringUtils.lowerCase(set);
        return redisDao.deleteFromSet(set, member);
//        return redisDao.addToSet(setString, member);
    }

    @Override
    public Long moveSet(String originSet, String destinationSet, String member) {
        originSet = StringUtils.lowerCase(originSet);
        destinationSet = StringUtils.lowerCase(destinationSet);
        return redisDao.moveSet(originSet, destinationSet, member);
    }

    @Override
    public Long zadd(String key, double score, String member) {
        key = StringUtils.lowerCase(key);
        return redisDao.zadd(key, score, member);
    }

    @Override
    public Double zscore(String key, String member) {
        key = StringUtils.lowerCase(key);
        return redisDao.zscore(key, member);
    }

    @Override
    public Double zincby(String key, Double incrementScore, String member) {
        key = StringUtils.lowerCase(key);
        return redisDao.zinc(key, incrementScore, member);
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        key = StringUtils.lowerCase(key);
        return redisDao.setrange(key, offset, value);
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        key = StringUtils.lowerCase(key);
        return redisDao.getrange(key, startOffset, endOffset);
    }

    @Override
    public Boolean exists(String key) {
        key = StringUtils.lowerCase(key);
        return redisDao.exists(key);
    }

    @Override
    public Long del(String... keys) {
        String[] lowerCaseKeys = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            lowerCaseKeys[i] = StringUtils.lowerCase(keys[i]);
        }
        return redisDao.del(keys);
    }

    @Override
    public Long del(String key) {
        key = StringUtils.lowerCase(key);
        return redisDao.del(key);
    }

    @Override
    public Long zrem(String key, String... members) {
        key = StringUtils.lowerCase(key);
        return redisDao.zrem(key, members);
    }

    @Override
    public Boolean sismember(String key, final String member) {
        key = StringUtils.lowerCase(key);
        return redisDao.sismember(key, member);
    }

    @Override
    public Long zrank(String key, String member) {
        key = StringUtils.lowerCase(key);
        return redisDao.zrank(key, member);
    }

    @Override
    public Long lpush(String key, int noOfElementToKeep, String... strings) {
        return redisDao.lpush(StringUtils.lowerCase(key), noOfElementToKeep, strings);
    }

    @Override
    public Long rpush(String key, String... strings) {
        return redisDao.rpush(StringUtils.lowerCase(key), strings);
    }

    @Override
    public List lrange(String key, int start, int end) {
        return redisDao.lrange(StringUtils.lowerCase(key), start, end);
    }

    @Override
    public Set<String> smembers(String key) {
        return redisDao.smembers(StringUtils.lowerCase(key));
    }

    public Double zinc(String key, Double incrementScore, String member) {
        return redisDao.zinc(key, incrementScore, member);
    }

    @Override
    public Long zunionstore(String dstkey, String... sets) {
        return redisDao.zunionstore(dstkey, sets);
    }

    @Override
    public List<Tuple> zrangeWithScores(String key, long start, long end) {
        return redisDao.zrangeWithScores(StringUtils.lowerCase(key), start, end);
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return redisDao.setex(StringUtils.lowerCase(key), seconds, value);
    }

    @Override
    public List<String> zrangeByScore(String key, double min, double max) {
        return redisDao.zrangeByScore(StringUtils.lowerCase(key), min, max);
    }

    @Override
    public String toJson(T obj) {
        return redisDao.toJson(obj);
    }

    @Override
    public Long expire(String key, int seconds) {
        return redisDao.expire(StringUtils.lowerCase(key), seconds);
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        return redisDao.expireAt(StringUtils.lowerCase(key), unixTime);
    }

    @Override
    public List<T> getList(String key, long start, long end) {
        return redisDao.getList(StringUtils.lowerCase(key), start, end);
    }

    @Override
    public Set<String> keys(String pattern) {
        return redisDao.keys(pattern);
    }

    @Override
    public String save(String key, String field, T obj) {
        return redisDao.save(StringUtils.lowerCase(key), StringUtils.lowerCase(field), obj);
    }

    @Override
    public T get(String key, String field) {
        return redisDao.get(StringUtils.lowerCase(key), StringUtils.lowerCase(field));
    }

    @Override
    public Long zcard(final String key) {
        return redisDao.zcard(StringUtils.lowerCase(key));
    }

    @Override
    public Long hset(String key, String field, String value) {
        return redisDao.hset(StringUtils.lowerCase(key), StringUtils.lowerCase(field), value);
    }

    @Override
    public String hget(String key, String field) {
        return redisDao.hget(StringUtils.lowerCase(key), StringUtils.lowerCase(field));
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
//-----------------------------        
        return redisDao.hincrBy(StringUtils.lowerCase(key), StringUtils.lowerCase(field), value);
    }

    @Override
    public Long hdel(String key, String field) {
//-----------------------------        
        return redisDao.hdel(StringUtils.lowerCase(key), StringUtils.lowerCase(field));
    }

    @Override
    public Long sadd(String key, String... members) {
        return redisDao.sadd(StringUtils.lowerCase(key), members);
    }

    @Override
    public Long srem(String key, String... members) {
        return redisDao.srem(StringUtils.lowerCase(key), members);
    }

    @Override
    public List<String> hvals(String key) {
        return redisDao.hvals(StringUtils.lowerCase(key));
    }

    @Override
    public Boolean hexists(String key, String field) {
        return redisDao.hexists(StringUtils.lowerCase(key), StringUtils.lowerCase(field));
    }

    @Override
    public Long scard(String key) {
        return redisDao.scard(StringUtils.lowerCase(key));
    }

}

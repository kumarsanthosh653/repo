/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao;

import java.util.List;
import java.util.Set;
import redis.clients.jedis.resps.Tuple;

/**
 *
 * @author pavanj
 */
public interface RedisDao<T> {

    public T convertFromJson(String jsonString);

    public String convertToJson(T obj);

//    public String saveAsJsonInHash(String key, String field, T obj);

    public T getFromJsonInHash(String key, String field);

    public String save(String key, String field, T obj);

    public T get(String key, String field);

    public String saveAsJson(String key, T obj);

    public T getFromJson(String key);

    public String saveex(String key, int seconds, T obj);

    public String toJson(T obj);

    public Long addToSet(String set, String member);

    public Long deleteFromSet(String set, String member);

    public Long moveSet(String originSet, String destinationSet, String member);

    public String setex(final String key, final int seconds, final String value);

    public List<T> getList(String key, final long start,
            final long end);

    public String setString(final String key, String value);

    public String getString(String key);

    public Long zadd(final String key, final double score, final String member);

    public Double zscore(final String key, final String member);

    public Double zinc(final String key, final Double incrementScore, final String member);

    public Long setrange(String key, long offset, String value);

    public String getrange(String key, long startOffset, long endOffset);

    /**
     * Test if the specified key exists. The command returns "1" if the key
     * exists, otherwise "0" is returned. Note that even keys setString with an
     * empty string as value will return "1".
     *
     * Time complexity: O(1)
     *
     * @param key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public Boolean exists(final String key);

    public Long del(String key);

    public Long del(final String... keys);

    public Long zrem(final String key, final String... members);

    /**
     * Return 1 if member is a member of the setString stored at key, otherwise
     * 0 is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the element is a member of the
     * setString 0 if the element is not a member of the setString OR if the key
     * does not exist
     */
    public Boolean sismember(final String key, final String member);

    /**
     * Return the rank (or index) or member in the sorted setString at key, with
     * scores being ordered from low to high.
     * <p>
     * When the given member does not exist in the sorted setString, the special
     * value 'nil' is returned. The returned rank (or index) of the member is
     * 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     *
     * @see #zrevrank(String, String)
     *
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the
     * element as an integer reply if the element exists. A nil bulk reply if
     * there is no such element.
     */
    public Long zrank(final String key, final String member);

    public Long zunionstore(final String dstkey, final String... sets);

    public Long lpush(final String key, int noOfElementToKeep, final String... strings);

    public Long rpush(final String key, final String... strings);

    public List lrange(String key, int start, int end);

    public Set<String> smembers(final String key);

    public List<Tuple> zrangeWithScores(String key, long start, long end);

    public List<String> zrangeByScore(final String key, final double min,
            final double max);

    public Long expire(final String key, final int seconds);

    public Long expireAt(final String key, final long unixTime);

    public Set<String> keys(final String pattern);

    public Long zcard(final String key);

    public Long hset(String key, String field, String value);

    public String hget(String key, String field);

    public Boolean hexists(String key, final String field);

    public Long hdel(final String key, final String... fields);

    public Long hincrBy(final String key, final String field, final long value);

    public Long sadd(String key, String... members);

    public Long srem(String key, String... members);

    public List<String> hvals(String key);

    public Long scard(String key);

    public int getCurrentRedisDb();
}

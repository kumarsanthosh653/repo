package com.ozonetel.occ.dao.redis;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozonetel.occ.dao.RedisDao;
import com.ozonetel.occ.model.Address;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Skill;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.resps.Tuple;

/**
 *
 * @author pavanj
 */
public class RedisDaoImpl<T> implements RedisDao<T> {

    private Logger logger = Logger.getLogger(this.getClass());
    private JedisPool pool;
    private Class<T> persistentClass;

    public RedisDaoImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public void setPool(JedisPool pool) {
        this.pool = pool;
    }

//    @Override
//    public String saveAsJsonInHash(String key, String field, T obj) {
//        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
//        hashOps.put(key, field, convertToJson(obj));
//        return "Success";
//    }

    @Override
    public T getFromJsonInHash(String key, String field) {
//        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
//        return convertFromJson(hashOps.get(key, field));

        return convertFromJson(hget(key, field)); 
    }

    public T convertFromJson(String jsonString) {
        Gson gson = new Gson();
        return (T) gson.fromJson(jsonString, persistentClass);
    }

    public String convertToJson(T obj) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setExclusionStrategies(new MyExclusionStrategy(Set.class))
                    .setExclusionStrategies(new MyExclusionStrategy(List.class))
                    .setExclusionStrategies(new MyExclusionStrategy(Address.class));

//
//            if (!(obj instanceof User)) {
//                gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(User.class));
//            }
            if (!(obj instanceof Agent)) {
                gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(Agent.class));
            }

            if (!(obj instanceof Campaign)) {
                gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(Campaign.class));
            }
            if (!(obj instanceof Skill)) {
                gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(Skill.class));
            }

            if (!(obj instanceof FwpNumber) && !(obj instanceof Agent)) {
                gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(FwpNumber.class));
            }
// Commenting dont know what this code is doing..REVISIT
//            if (obj instanceof Agent) {
//                Agent agent = (Agent) obj;
//                if (agent.getFwpNumber() != null) {
//                    agent.setFwpNumber((FwpNumber) agent.getFwpNumber().clone());//modifying copy of the object.
//                    agent.getFwpNumber().setAgent(null);
//                    agent.getFwpNumber().setUser(null);
//                }
//            }
            return gsonBuilder.create().toJson(obj);
        } catch (Exception ce) {
            logger.error(ce.getMessage(), ce);
            return null;
        }

    }

    @Override
    public String save(String key, String field, T obj) {
        hset(key, field, obj.toString());
        return "Success";
    }

    @Override
    public T get(String key, String field) {
        try {
            return (T) hget(key, field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private GsonBuilder getGsonBuilder(T obj) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .setExclusionStrategies(new MyExclusionStrategy(Set.class))
                .setExclusionStrategies(new MyExclusionStrategy(List.class))
                .setExclusionStrategies(new MyExclusionStrategy(Address.class));

//            if (!(obj instanceof User)) {
//                gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(User.class));
//            }
        if (!(obj instanceof Agent)) {
            gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(Agent.class));
        }

        if (!(obj instanceof Campaign)) {
            gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(Campaign.class));
        }
        if (!(obj instanceof Skill)) {
            gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(Skill.class));
        }

        if (!(obj instanceof FwpNumber) && !(obj instanceof Agent)) {
            gsonBuilder.setExclusionStrategies(new MyExclusionStrategy(FwpNumber.class));
        }

        return gsonBuilder;

    }

    @Override
    public List<T> getList(String key, final long start, final long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            List<String> list = jedis.lrange(key, start, end);
            if (list != null && !list.isEmpty()) {
                Gson gson = new Gson();
                List<T> objList = new ArrayList<>(list.size());
                for (String s : list) {
                    objList.add((T) gson.fromJson(s, persistentClass));
                }
                return objList;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public String toJson(T obj) {
        return getGsonBuilder(obj).create().toJson(obj);
    }

    @Override
    public String saveAsJson(String key, T obj) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();

            try {
//                if (obj instanceof Agent) {
//                    Agent agent = (Agent) obj;
//                    if (agent.getFwpNumber() != null) {
//                        agent.setFwpNumber((FwpNumber) agent.getFwpNumber().clone());//modifying copy of the object.
//                        agent.getFwpNumber().setAgent(null);
//                        agent.getFwpNumber().setUser(null);
//                    }
//                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return jedis.set(key, getGsonBuilder(obj).create().toJson(obj));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public String saveex(String key, int seconds, T obj) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setex(key, seconds, getGsonBuilder(obj).create().toJson(obj));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public String setString(final String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    /**
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    @Override
    public String setex(final String key, final int seconds, final String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setex(key, seconds, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public String getString(String key) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Double zinc(final String key, final Double incrementScore, final String member) {

        Jedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zincrby(key, incrementScore, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public T getFromJson(String key) {
        Jedis jedis = null;
        Gson gson = new Gson();
        try {
            jedis = pool.getResource();
            return (T) gson.fromJson(jedis.get(key), persistentClass);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long addToSet(String set, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(set, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long deleteFromSet(String set, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(set, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long moveSet(String originSet, String destinationSet, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.smove(originSet, destinationSet, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long zadd(final String key, final double score, final String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Double zscore(final String key, final String member) {

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setrange(key, offset, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Boolean exists(final String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long del(final String... keys) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(keys);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Boolean sismember(final String key, final String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long zrem(final String key, final String... members) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, members);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;

    }

    @Override
    public Long zrank(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrank(key, member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long lpush(String key, int noOfElementToKeep, String... strings) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Long count = jedis.lpush(key, strings);
            if (noOfElementToKeep != 0) {
                jedis.ltrim(key, 0, noOfElementToKeep - 1);
            }
            return count;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long rpush(String key, String... strings) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.rpush(key, strings);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    public List lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long zunionstore(String dstkey, String... sets) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zunionstore(dstkey, sets);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Set<String> smembers(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.smembers(key);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public List<Tuple> zrangeWithScores(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeWithScores(key, start, end);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public List<String> zrangeByScore(String key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long expire(final String key, final int seconds) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long expireAt(final String key, final long unixTime) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expireAt(key, unixTime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Set<String> keys(String pattern) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.keys(pattern);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long zcard(final String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long hdel(String key, final String... fields) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Boolean hexists(String key, final String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hexists(key, field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, members);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    public Long srem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, members);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public List<String> hvals(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hvals(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    @Override
    public Long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return 0l;
    }

    public int getCurrentRedisDb() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.getDB();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            pool.returnResource(jedis);
        }
        return 0;
    }

    private class MyExclusionStrategy implements ExclusionStrategy {

        private final Class<?> typeToSkip;

        private MyExclusionStrategy(Class<?> typeToSkip) {
            this.typeToSkip = typeToSkip;
        }

        public boolean shouldSkipClass(Class<?> clazz) {
            return (clazz == typeToSkip);
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return false;
        }
    }
}

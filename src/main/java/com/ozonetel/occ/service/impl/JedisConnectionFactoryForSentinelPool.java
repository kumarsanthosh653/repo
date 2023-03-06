//package com.ozonetel.occ.service.impl;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.data.redis.RedisConnectionFailureException;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.jedis.JedisConnection;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.util.StringUtils;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisSentinelPool;
//import redis.clients.jedis.JedisShardInfo;
//
///**
// *
// * @author pavanj
// */
//public class JedisConnectionFactoryForSentinelPool extends JedisConnectionFactory {
//
//    /**
//     * Returns a Jedis instance to be used as a Redis connection. The instance
//     * can be newly created or retrieved from a pool.
//     *
//     * @return Jedis instance ready for wrapping into a {@link RedisConnection}.
//     */
//    @Override
//    protected Jedis fetchJedisConnector() {
//        try {
//            if (getUsePool() && jedisSentinelPool != null) {
//                return jedisSentinelPool.getResource();
//            }
//            Jedis jedis = new Jedis(getShardInfo());
//            // force initialization (see Jedis issue #82)
//            jedis.connect();
//            return jedis;
//        } catch (Exception ex) {
//            throw new RedisConnectionFailureException("Cannot get Jedis connection", ex);
//        }
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        if (getShardInfo() == null) {
//            setShardInfo(new JedisShardInfo(getHostName(), getPort()));
//
//            if (StringUtils.hasLength(getPassword())) {
//                getShardInfo().setPassword(getPassword());
//            }
//
//            if (getTimeout() > 0) {
//                //getShardInfo().setTimeout(getTimeout());
//                getShardInfo().setConnectionTimeout(getTimeout());
//            }
//        }
//
//    }
//
//    @Override
//    public void destroy() {
//        super.destroy();
//        if (getUsePool() && jedisSentinelPool != null) {
//            try {
//                jedisSentinelPool.destroy();
//            } catch (Exception ex) {
//                log.warn("Cannot properly close Jedis pool", ex);
//            }
//            jedisSentinelPool = null;
//        }
//    }
//
//    @Override
//    public JedisConnection getConnection() {
//        Jedis jedis = fetchJedisConnector();
//        JedisConnection connection = (getUsePool() ? new JedisConnection(jedis, jedisSentinelPool, getDatabase())
//                : new JedisConnection(jedis, null, getDatabase()));
//        connection.setConvertPipelineAndTxResults(getConvertPipelineAndTxResults());
//        return postProcessConnection(connection);
//    }
//
//    public JedisPool getJedisSentinelPool() {
//        return jedisSentinelPool;
//    }
//
//    public void setJedisSentinelPool(JedisPool jedisSentinelPool) {
//        this.jedisSentinelPool = jedisSentinelPool;
//    }
//
//    private JedisPool jedisSentinelPool = null;
//    private final static Log log = LogFactory.getLog(JedisConnectionFactoryForSentinelPool.class);
//}

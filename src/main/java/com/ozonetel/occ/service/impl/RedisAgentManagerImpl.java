package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.dao.RedisDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.RedisManager;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author pavanj
 */
public class RedisAgentManagerImpl extends RedisManagerImpl<Agent> implements RedisAgentManager, ApplicationContextAware, BeanNameAware {

    private static Logger logger = Logger.getLogger(RedisAgentManagerImpl.class);

    public RedisAgentManagerImpl(RedisDao<Agent> redisAgentDao) {
        super(redisAgentDao);
        this.redisAgentDao = redisAgentDao;

    }

//    @Override
//    public Agent get(String key, String field) {
////------------------------------
//        return redisAgentDao.getFromJsonInHash(StringUtils.lowerCase(key), StringUtils.lowerCase(field));
//    }

//    @Override
//    public synchronized String save(String key, String field, Agent dbAgent) {
//        logger.debug("Saving:" + dbAgent.toInfoString());
////        String username = dbAgent.getUser().getUsername();
//// REVISIT commente above as we are not using agent.user 
//    String username = dbAgent.getUserId().toString();
//        // ---- > If agent is inactvie(deleted) delete from Redis also.
//        if (!dbAgent.isActive()) {
//            return "" + ((RedisManager) applicationContext.getBean(beanName)).del(key);
//        }
//
//      Agent redisAgent = ((RedisManager<Agent>) applicationContext.getBean(beanName)).get(username + ":agents", key);
//        long stateMoved = 0;
//        long modeMoved = 0;
//        Agent.State destState = dbAgent.getState();
//        Agent.Mode destinationMode = dbAgent.getMode();
//
//        if (redisAgent != null) {
//            Agent.State origState = redisAgent.getState();
//
//            Agent.Mode originMode = redisAgent.getMode();
//            // -----> Moving to new state.
//            stateMoved = ((RedisManager) applicationContext.getBean(beanName)).moveSet(username + ":state:" + origState, username + ":state:" + destState, dbAgent.getAgentId());
//            logger.debug("Moved from(" + origState + ") to (" + destState + ") state set:" + stateMoved);
//
//            // ----->Moving to new mode.
//            modeMoved = ((RedisManager) applicationContext.getBean(beanName)).moveSet(username + ":mode:" + originMode, username + ":mode:" + destinationMode, dbAgent.getAgentId());
//        }
//        //----- > If element is not moved try removing from all the possible sets and 
//        // put it in destination setString.
//        if (stateMoved == 0) {
//            Set<String> stateKeys = new LinkedHashSet<>(10);
//
//            for (Agent.State state : Agent.State.values()) {
//                stateKeys.add(username + ":state:" + state);
//            }
//
//            for (String stateSet : stateKeys) {
//                ((RedisManager) applicationContext.getBean(beanName)).deleteFromSet(stateSet, dbAgent.getAgentId());
//            }
//
//            // ----> adding to right setString(desired to state setString).
//            ((RedisManager) applicationContext.getBean(beanName)).addToSet(username + ":state:" + destState, dbAgent.getAgentId());
//        }
//
//        if (modeMoved == 0) {
//            Set<String> modeKeys = new LinkedHashSet<>(10);
//            for (Agent.Mode mode : Agent.Mode.values()) {
//                modeKeys.add(username + ":mode:" + mode);
//            }
//
//            for (String modeSet : modeKeys) {
//                ((RedisManager) applicationContext.getBean(beanName)).deleteFromSet(modeSet, dbAgent.getAgentId());
//            }
//
//            // ----> adding to right setString(desired to mode setString).
//            ((RedisManager) applicationContext.getBean(beanName)).addToSet(username + ":mode:" + destinationMode, dbAgent.getAgentId());
//        }
//
//        return redisAgentDao.saveAsJsonInHash(StringUtils.lowerCase(key), StringUtils.lowerCase(field), dbAgent);
//    }

    @Override
    public Long saveAgentWsIdToRedis(String user, String agentId, String websocketId) {
        return redisAgentDao.hset(RedisKeys.AGENT_CLIENTID_MAP, StringUtils.lowerCase(user + ":" + agentId), websocketId);
    }

    @Override
    public Long delAgentWsIdFromRedis(String user, String agentId, String websocketId) {
        return redisAgentDao.hdel(RedisKeys.AGENT_CLIENTID_MAP, StringUtils.lowerCase(user + ":" + agentId));
    }

    @Override
    public String getAgentWsId(String user, String agentId) {
        return redisAgentDao.hget(RedisKeys.AGENT_CLIENTID_MAP, StringUtils.lowerCase(user + ":" + agentId));
    }

    private final RedisDao<Agent> redisAgentDao;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    private ApplicationContext applicationContext;
    private String beanName;
}

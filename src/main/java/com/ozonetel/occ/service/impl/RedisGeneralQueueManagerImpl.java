package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.CallQueue;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.RedisGeneralQueueManager;
import com.ozonetel.occ.service.RedisManager;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 *
 * @author pavanj
 */
public class RedisGeneralQueueManagerImpl implements RedisGeneralQueueManager {

    @Override
    public Long getQueueSize(String did, String user, String agentMonitorUcid, String callerId, Skill skill) {
        return redisCallQueueManager.zcard("queue:" + user + ":" + skill.getSkillName());
    }

    @Override
    public Long checkQueue(String did, String user, String agentMonitorUcid, String callerId, Skill skill, long callStartTime) {

        //
        // ----- > Get the actual call start time from redis.
        boolean isNew = false;
        if (redisCallQueueManager.exists("calltimestamp:" + agentMonitorUcid)) {
            redisCallQueueManager.expire("calltimestamp:" + agentMonitorUcid, 1 * 60 * 60);
            logger.trace("&&&&Not new..");
        } else {
            redisCallQueueManager.setex("calltimestamp:" + agentMonitorUcid, 1 * 60 * 60, "" + callStartTime);
            isNew = true;
        }

//        logger.trace("Delete below score:"+((System.currentTimeMillis() - 60_000) * skill.getPriority()) / (isNew ? 1 : 2));
        //
        // ----- > Get all the elements in queue which have stayed more than 16 seconds in queue 
        //except current monitorUcid and remove from queue.
        //TODO move below code to seperate process.
        List<String> monitorUcidsToExpire = redisCallQueueManager.zrangeByScore("queue:" + user + ":" + skill.getSkillName(), 0, ((System.currentTimeMillis() - 16_000) * skill.getPriority()));
        monitorUcidsToExpire.remove(agentMonitorUcid);

        if (!monitorUcidsToExpire.isEmpty()) {
            logger.debug("`````````Queue ucids to remove:" + monitorUcidsToExpire);
            redisCallQueueManager.zrem("queue:" + user + ":" + skill.getSkillName(), monitorUcidsToExpire.toArray(new String[monitorUcidsToExpire.size()]));
        }

        //String callStartTime = redisCallQueueManager.getString("calltimestamp:" + agentMonitorUcid);
        Date date = new Date(Long.valueOf(redisCallQueueManager.getString("calltimestamp:" + agentMonitorUcid)));

        Long rank = 0l;
        synchronized (this) {
           
            redisCallQueueManager.zadd("queue:" + user + ":" + skill.getSkillName(), date.getTime()  * skill.getPriority(), agentMonitorUcid);
            rank = redisCallQueueManager.zrank("queue:" + user + ":" + skill.getSkillName(), agentMonitorUcid);
        }

        //
        // ----- > Delete entire queue if queue is not updated for 10 minutes.
        redisCallQueueManager.expire("queue:" + user + ":" + skill.getSkillName(), 10 * 60);

        if (!redisCallQueueManager.exists("callqueue:" + agentMonitorUcid)) {//----> saveAsJson queue details  only  if it doesn't exists
            CallQueue callQueue = new CallQueue(callerId, did, skill.getSkillName(), date, Long.valueOf(agentMonitorUcid),skill.getUser().getId());
            redisCallQueueManager.saveex("callqueue:" + agentMonitorUcid, 1 * 60 * 60, callQueue);
        }
        return rank;
    }

    @Override
    public Long deleteFromQueue(String did, String user, String agentMonitorUcid, String callerId, String skillName) {
        redisCallQueueManager.del("callqueue:" + agentMonitorUcid);
        return redisCallQueueManager.zrem("queue:" + user + ":" + skillName, agentMonitorUcid);

    }

    public void setRedisCallQueueManager(RedisManager<CallQueue> redisCallQueueManager) {
        this.redisCallQueueManager = redisCallQueueManager;
    }
    private RedisManager<CallQueue> redisCallQueueManager;
    private static Logger logger = Logger.getLogger(RedisGeneralQueueManagerImpl.class);
}

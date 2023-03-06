package com.ozonetel.occ.service.impl;

import com.google.gson.GsonBuilder;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.service.RedisManager;
import com.ozonetel.occ.util.LockServiceImpl;
import java.util.Date;

/**
 *
 * @author pavanj
 */
public class EventManagerBeanstalk {

    public void logEvent(String eventName, Long userId, String userName, Long agentUniqId, String agentLoginId, Agent.Mode currentAgentMode, Date startTime, Long ucid, String eventMessage, String miscDetails) {
        String lockKey = "event_lock_" + agentUniqId;
        try {
            lockService.lock(lockKey);
            Event event = new Event();
            event.setAgentId(agentUniqId);
            event.setUserId(userId);
            event.setEvent(eventName);
            event.setMode(Event.AgentMode.valueOf(currentAgentMode.name()));
            event.setStartTime(new Date());
            event.setUcid(ucid);
            event.setEventData(eventMessage);
            event.setMiscDetails(miscDetails);

            redisAgentManager.lpush("ca:agent-events:" + userName, 0, new GsonBuilder().serializeNulls().create().toJson(event));

        } finally {
            lockService.unlock(lockKey);
        }

    }

    public void setLockService(LockServiceImpl lockService) {
        this.lockService = lockService;
    }

    public void setRedisAgentManager(RedisManager<Agent> redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    private LockServiceImpl lockService;
    private RedisManager<Agent> redisAgentManager;

}

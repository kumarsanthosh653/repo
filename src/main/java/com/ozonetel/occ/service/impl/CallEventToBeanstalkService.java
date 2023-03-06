package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.model.CallEvent;
import com.ozonetel.occ.service.BeanstalkService;
import com.ozonetel.occ.service.RedisAgentManager;

import com.ozonetel.occ.util.CallListenerAdapter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pavanj
 */
public class CallEventToBeanstalkService extends CallListenerAdapter {

    @Override
    public void callCompleted(CallEvent callEvent) {

        if (redisAgentManager.hexists("ca:disposition:callqueue", callEvent.getUcid())) {
            Map<String, Object> eventMap = new LinkedHashMap<>();
            eventMap.put("monitorUcid", callEvent.getUcid()); //labelling ucid as monitorucid because the script is fetching as monitorUcid
            eventMap.put("user", callEvent.getUser().getUsername());
            eventMap.put("addTime", new Date());
            long jobId = beanstalkService.addCallCompletedEvent(new Gson().toJson(eventMap));
            logger.debug("Inserted call end event:" + jobId + " -> " + eventMap);
        }

    }

    public void setBeanstalkService(BeanstalkService beanstalkService) {
        this.beanstalkService = beanstalkService;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    private BeanstalkService beanstalkService;
    private RedisAgentManager redisAgentManager;
    private static Logger logger = LoggerFactory.getLogger(BeanstalkServiceImpl.class);
}

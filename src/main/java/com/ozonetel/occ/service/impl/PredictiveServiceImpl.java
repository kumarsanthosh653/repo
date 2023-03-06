/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.model.PredictiveCallMonitor;
import com.ozonetel.occ.service.RedisAgentManager;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class PredictiveServiceImpl {

    private static Logger logger = Logger.getLogger(PredictiveServiceImpl.class);
    private RedisAgentManager redisAgentManager;
    private final static String REDIS_SET_CALLMONITOR = "CALLMONITOR";
    private final static String redisDefaultKey = "predictivev2";

    public RedisAgentManager getRedisAgentManager() {
        return redisAgentManager;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    
    private String getRedisKey(Long campaignId, String Key) {
        String finalKey = redisDefaultKey + ":" + campaignId + ":" + Key;
        logger.debug("predictive redis key to return " + finalKey);
        return finalKey;
    }

    private PredictiveCallMonitor convertJsonToPredictiveCallMonitor(String jsonDetails) {
        return new Gson().fromJson(jsonDetails, PredictiveCallMonitor.class);
    }

    private String convertPredictiveCallMonitorToJSON(PredictiveCallMonitor predictiveCallMonitor) {
        return new Gson().toJson(predictiveCallMonitor);
    }

    public void savePredictiveCallMonitorToRedis(Long campaignId, String ucid, PredictiveCallMonitor predictiveCallMonitor) {
        logger.debug("saving predictive call monitor : " + predictiveCallMonitor);
        logger.debug("" + redisAgentManager.hset(getRedisKey(campaignId, REDIS_SET_CALLMONITOR), ucid, convertPredictiveCallMonitorToJSON(predictiveCallMonitor)));

    }

    public PredictiveCallMonitor getPredictiveCallMonitor(Long campaignId, String ucid) {
        return convertJsonToPredictiveCallMonitor(redisAgentManager.hget(getRedisKey(campaignId, REDIS_SET_CALLMONITOR), ucid));
    }

    public void delPredictiveCallMonitor(Long campaignId, String ucid) {
        redisAgentManager.hdel(getRedisKey(campaignId, REDIS_SET_CALLMONITOR), ucid);
    }

}

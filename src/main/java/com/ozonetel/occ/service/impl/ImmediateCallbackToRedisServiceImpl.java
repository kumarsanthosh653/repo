package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallCompletedEvent;
import com.ozonetel.occ.model.CallEvent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.ImmediateCallbacksExecutorService;
import com.ozonetel.occ.service.RedisManager;
import com.ozonetel.occ.util.CallListenerAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class ImmediateCallbackToRedisServiceImpl extends CallListenerAdapter implements ImmediateCallbacksExecutorService {

    @Override
    public void callCompleted(CallEvent callEvent) {

        try {
            logger.debug("stating callCompleted :: " + callEvent.toString());

            if (redisAgentManager.sismember(RedisKeys.CUSTOMER_CALLBACK_OLD_ARCHITECTURE, StringUtils.lowerCase(callEvent.getUser().getUsername()))) {
                if (callEvent.getUser() != null && StringUtils.isNotBlank(callEvent.getUser().getCallBackUrl())) {
                    try {// --> Retrying allowed no.of tries for the customer callbacks.
                        boolean sendCallbackImmediately = false;
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("user_id", callEvent.getUser().getId());
                        params.put("param_name", "IMMEDIATE_CALLBACK");
                        List settings = campaignManager.executeProcedure("call Get_UserParamter(?,?)", params);
                        logger.debug("settings : " + settings);
                        if (settings != null && !settings.isEmpty()) {
                            Map item = (Map) settings.get(0);
                            sendCallbackImmediately = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                        }

                        logger.debug(callEvent.getMonitorUcid() + " sendCallbackImmediately : " + sendCallbackImmediately);
                        if (sendCallbackImmediately) {
                            Long count = redisAgentManager.addToSet(RedisKeys.CUSTOMER_CALLBACKS_SET, callEvent.getMonitorUcid());
                            logger.debug("Adding ucid:" + callEvent.getMonitorUcid() + " for immediate callback old set : " + count);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

            } else {

                Campaign campaign = callEvent.getCampaign();
                boolean inDisposeSet = false;
                if (campaign != null && StringUtils.isNotBlank(campaign.getDispositionType())) {

                    String agentId = callEvent.getAgentId();
                    if (StringUtils.equalsIgnoreCase(campaign.getDispositionType(), "-1") || agentId == null || agentId.equalsIgnoreCase("0")) {
                    } else {

                        if (!campaign.isOffLineMode() && callEvent.getCallStatus() != null && (StringUtils.equalsIgnoreCase(callEvent.getCallStatus(), "success")
                                || (!StringUtils.equalsIgnoreCase(callEvent.getCallStatus(), "success") && campaign.getAcwNac() != null && campaign.getAcwNac()))) {
                            inDisposeSet = true;
                        }
                    }
                }

                if (inDisposeSet) {
                    Integer sla = campaign.getSla();
                    if (sla == null || sla == 0 || sla.equals(0))
                        sla = (30 * 60);
                    Long count = redisAgentManager.zadd(RedisKeys.CUSTOMER_CALLBACKSV3_TODISPOSE_SET, System.currentTimeMillis() + (sla * 1000), callEvent.getMonitorUcid());
                    logger.debug("Adding ucid:" + callEvent.getMonitorUcid() + " for dispose set : "+count);
                } else {
                    Long count = redisAgentManager.addToSet(RedisKeys.CUSTOMER_CALLBACKSV3_SET, callEvent.getMonitorUcid());
                    logger.debug("Adding ucid:" + callEvent.getMonitorUcid() + " for immediate callback : " + count);
                }

                Map triesMap = new HashMap();
                triesMap.put("maxTries", callEvent.getCallBackTries());
                logger.debug("Adding maxtries:" + callEvent.getCallBackTries() + " ucid:" + callEvent.getMonitorUcid() + " for immediate callback.");
                redisAgentManager.hset(RedisKeys.CUSTOMER_CALLBACKSV3_TRIES, callEvent.getMonitorUcid(), new Gson().toJson(triesMap));

            }
            CallCompletedEvent cce = new CallCompletedEvent();
            cce.setDateAdded(new Date());
            cce.setMonitorUcid(callEvent.getMonitorUcid());
            cce.setCampaignId(callEvent.getCampaign().getCampaignId());
            cce.setUser(callEvent.getUser().getUsername());

            Map<String, Object> map = new HashMap<>();
            map.put("callBackTries", callEvent.getCallBackTries());
            map.put("campaignCallbackUrl", callEvent.getCampaign().getCallbackUrl());
            map.put("userCallbackUrl", callEvent.getUser().getCallBackUrl());
            List<Map<String, Object>> callbackActions = new ArrayList<>();
            callbackActions.add(map);
            cce.setCallbackActions(callbackActions);
            
            logger.debug("Adding call complete event to redis : "+cce+" -> "+redisAgentManager.sadd("ca:call_completed", new Gson().toJson(cce)));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void setRedisAgentManager(RedisManager<Agent> redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    private RedisManager<Agent> redisAgentManager;
    private CampaignManager campaignManager;
    private static final Logger logger = Logger.getLogger(ImmediateCallbackToRedisServiceImpl.class);

}

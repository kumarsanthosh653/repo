package com.ozonetel.occ.service;

/**
 *
 * @author pavanj
 */
public interface RedisGetNextFreeAgentService {

    public String getNextFreeAgent(String user,String campaignId, String did, String callerId, String ucid, String agentMonitorUcid, String skillName, String agentId, String agentPh);
}

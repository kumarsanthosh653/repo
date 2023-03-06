package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Agent;

/**
 *
 * @author pavanj
 */
public interface RedisAgentManager extends RedisManager<Agent> {

    public Long saveAgentWsIdToRedis(String user, String agentId, String websocketId);

    public Long delAgentWsIdFromRedis(String user, String agentId, String websocketId);

    public String getAgentWsId(String user, String agentId);
}

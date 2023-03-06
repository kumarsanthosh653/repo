package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Skill;

/**
 *
 * @author pavanj
 */
public interface RedisGeneralQueueManager {

    public Long checkQueue(String did, String user, String agentMonitorUcid, String callerId, Skill skill, long callStartTime);

    public Long deleteFromQueue(String did, String user, String agentMonitorUcid, String callerId, String skillName);

    public Long getQueueSize(String did, String user, String agentMonitorUcid, String callerId, Skill skill);
}

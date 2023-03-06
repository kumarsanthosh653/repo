package com.ozonetel.occ.webapp.action;

import com.ozonetel.occ.service.RedisGetNextFreeAgentService;

/**
 *
 * @author pavanj
 */
public class RedisGetNextFreeAgentAction extends BaseAction {

    @Override
    public String execute() throws Exception {

        status = redisGetNextFreeAgentService.getNextFreeAgent(user,campaignId, did, callerId, ucid, agentMonitorUcid, skillName, agentId, agentPh);
        return SUCCESS;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the did
     */
    public String getDid() {
        return did;
    }

    /**
     * @param did the did to setString
     */
    public void setDid(String did) {
        this.did = did;
    }

    /**
     * @return the skillName
     */
    public String getSkillName() {
        return skillName;
    }

    /**
     * @param skillName the skillName to setString
     */
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setAgentMonitorUcid(String agentMonitorUcid) {
        this.agentMonitorUcid = agentMonitorUcid;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setAgentPh(String agentPh) {
        this.agentPh = agentPh;
    }

    public void setRedisGetNextFreeAgentService(RedisGetNextFreeAgentService redisGetNextFreeAgentService) {
        this.redisGetNextFreeAgentService = redisGetNextFreeAgentService;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    
    
    private String user;
    
    //
    // ----- > Redis managers
    private RedisGetNextFreeAgentService redisGetNextFreeAgentService;
    //
    // ----- > Properties
    private String campaignId;
    private String did;
    private String callerId;
    private String dataId;
    private String ucid;
    private String agentMonitorUcid;
    private String skillName;
    private String agentId;
    private String agentPh;
    private String status;
    private String type;
}

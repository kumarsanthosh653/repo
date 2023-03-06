package com.ozonetel.occ.webapp.action;

import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DispositionManager;

/**
 *
 * @author pavanj
 */
public class DispositionAPI extends BaseAction {

    public String releasePhoneNumber() {

        return SUCCESS;
    }

    @Override
    public String execute() {
        
        return SUCCESS;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setPauseAfterDispose(boolean pauseAfterDispose) {
        this.pauseAfterDispose = pauseAfterDispose;
    }

    public void setPauseReason(String pauseReason) {
        this.pauseReason = pauseReason;
    }

    private DispositionManager dispositionManager;
    private CampaignManager campaignManager;
    private AgentManager agentManager;
    private String status;
    private String dataID;
    private String disposition;
    private String callBackTime;
    private String ucid;
    private String did;
    private String callerID;
    private String skillName;
    private String agentID;
    private String customer;
    private String comments;
    private String action;
    private String campaignType;
    private String campaignID;
    private String apiKey;
    private String responseType;
    private boolean pauseAfterDispose;
    private String pauseReason;

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public void setCallBackTime(String callBackTime) {
        this.callBackTime = callBackTime;
    }

    public void setCallerID(String callerID) {
        this.callerID = callerID;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

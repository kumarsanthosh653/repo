package com.ozonetel.occ.service;

import com.ozonetel.occ.model.IvrFlow;
import com.ozonetel.occ.model.PauseReason;
import com.ozonetel.occ.model.TransferNumber;
import java.util.List;

import java.util.Map;

/**
 * @author Administrator
 *
 */
public interface OCCManager {

    public void initialize();

    public int getConnectedCallCount();

    public void setConnectedCallCount(int connectedCallCount);

    public int getDialedCallCount();

    public void setDialedCallCount(int dialedCallCount);

    public String loginAgent(String agentId, String username);

    public String releaseAgentByAdmin(String username, String agentUniqueId, String whoDidIt);

//    public String logoffAgentByAdmin(String username, Ştring agentId, String whoDidIt);
    
    public String logoffAgentByAdmin(String username, Long agentUniqueId, String whoDidIt);
    

//    public String logoutAgent(String agentId, String customerId, String username);

    public String getNextFreeAgent(String did, String callerId, String dataId, String ucid, String agentMonitorUcid, String skillName, String cId, String agentId, String agentPh);

    public String getUsernameForDid(String did);

    public boolean isValidAgent(String customer, String agentId, String phoneNumber, String pin);

    /**
     * @param agentId
     * @return
     */
    public String releaseAllAgents();

    public String getCallBackList(String agentId);

    //For WebSocket
    public String getCallBackListByAgent(String agentId);

    //This Method Update the agents Previous Event EndTime
    public void updatePreviousEvent(Long agentId);

    public void updateLastLoginEvent(Long agentId, String eventName);

    public String isAgent(String did, String agentId);

    //This Method is to Return the campId Based on the agent Id
    //which in turn used to pass the campId to InformDialler
//    public String getCampaignByAgent(String userName, String agentId);

//    public Long getCampaignIDByAgent(String userName, String agentId);

//    public void updateCallStatus_new(String ucid, String agentMonitorUcid, String did, String agentId, String callerId, String callStatus, String audioFile, String skillName, String hangUpBy, String uui, String isCompleted, String fallBackRule, String type, int transferType, String dialStatus, boolean callCompleted,String dataId,String customerStatus,String agentStatus,String campaignId,Long priId, String startTime, String endTime, String tta,Long dd);
//    public void updateCallStatus(String ucid, String agentMonitorUcid, String did, String agentId, String callerId, String callStatus, String audioFile, String skillName, String hangUpBy, String uui, String isCompleted, String fallBackRule, String type, int transferType, String dialStatus, boolean callCompleted,String dataId,String customerStatus,String agentStatus,String campaignId,Long priId,Long dd);
    public void resetAgentFlag(String ucid);

    public List<PauseReason> getPauseReasons(String username);

    public int isCallInQueue(String callerId, String skillName, String did, String agentMonitorUcid);
//this method returns if the callerId is an Existing agent for the user or did 
//If it is an Agent it returns 1    

    public String checkAgent(String did, String callerId, String ucid, String agentMonitorUcid, Map params);

    public List<String> getAgentTransferList(String username, String did, String agentId);

    public List<String> getSkillTransferList(String username, String did, String agentId);

    public List<TransferNumber> getPhoneTransferList(String username);

    public List getFwpNumberList(String username);

    public String setTransfer(String ucid, String did, String username, String transferType, String transferId, String transferNumber, int blindTransfer);

    public String transferCheck(String ucid, String did);

    public String transferFailed(String did, String agentId);

    public UserManager getUserManager();
    
    public String releaseChatByAdmin(String sessionId, String whoDidIt);
    
    public List<IvrFlow> getFeedbackIVRList(String username);
    }

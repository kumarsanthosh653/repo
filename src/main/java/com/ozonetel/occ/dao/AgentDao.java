package com.ozonetel.occ.dao;

import java.util.List;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.StatusMessage;
import java.util.Map;

/**
 * An interface that provides a data management interface to the Agent table.
 */
public interface AgentDao extends GenericDao<Agent, Long> {

    List<Agent> getAgents();

    List<Agent> getAgentsByUser(String userName);

    List<Agent> getIdleAgentsByUser(String userName);

    List<Agent> getAgentsByCampaign(Long campaignId);

    List<Agent> getAgentsBySkill(Long skillId);

    List<Agent> getAgentsBySkill(Long skillId, Agent.Mode mode);

    List<Agent> getHuntingAgentsBySkill(Long skillId);

//    List<Agent> getAgentsBySkillandUser(String skillName, String username);
//    List<Agent> getAgentByAgentId(String user, String agent);
    List<Agent> getAgentByAgentIdV2(Long userId, String agent);

    List<Campaign> getCampaignByAgentId(Long id);

    List<Agent> getIdleAgents();

    List<Agent> getIdleAgentsByCampaign(Long campaignId);

    List<Agent> getBusyAgents();

    List<Agent> getLoggedInAgents();

    List<Campaign> getCampaignByAgentId(String agentId);

    public List<Agent> getTransferAgentList(Long userId, Long agentUniqueId);

    public int releaseAgentLockWithDialStatus(Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentException);

    public List getAgentPerformance(Long user, Long agent);

    public List<Map<String, Object>> getAgentSummary(Long agentId);

    public int decrementChatSessionCount(Long agentUniqId);

    public int updatePassword(String user, String agentId, String oldPwd, String newPwd);

    public int saveReleasedAgent(Long id, Agent.Mode mode);

    public int savePauseAgent(Long id, String reason);

    public int saveAgentMode(Long id, Agent.Mode mode, boolean reconnect);

    public int saveAgentReconnect(Long id, String clientId);

    public int lockAgent(Long id, String customerNumber);

    public int lockIfAgentAvailable(Long id, String customerNumber);

    public int saveAgentLogin(Long id, FwpNumber fwpNumber, String clientId, String phoneNumber, boolean reconnect);

    public int saveAgentLogout(Long id);

    public boolean makeAgentBusy(Long agentUniqId, String contact, String channelName, String callType, Long monitorUcid);

    public boolean releaseAgentFromCall(Long agentUniqId, Agent.State state);

    public boolean releaseAgentNextFlag(Long agentUniqId);

    public boolean agentCallStarted(Long agentUniqId, String contact, String type, String callStatus, String skill, String campName, Long agentMonitorUcid, boolean updateTimeStamp);

    public boolean updateHoldStartTime(Long agentId, boolean start);

    public boolean isAgentLoggedin(Long agentId);

    public Object getLoggedInAgentsCountByUser(Long UserId);

    public List<Map<String, Object>> getLoggedInAgentsForChat(Long UserId);

    public boolean isValidAgent(Long agentId, String PhoneNo);

    public int pauseChatSessions(Long agentUniqId);

    public List<Map<String, Object>> getChatTransferAgentList(String did, Long agentUniqueId);

    public int releaseAgentLockFromUpdateCallStatus(Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentWithException, Long ucid);
    }

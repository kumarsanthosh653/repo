package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import org.springframework.transaction.annotation.Transactional;

@WebService
@Transactional("transactionManager2")
public interface AgentManager extends GenericManager<Agent, Long> {

    @Override
    public Agent get(Long id);

    @Override
    public Agent save(Agent object);

    public List<Agent> getAgents();

    public List<Agent> getAgentsByUser(String userName);

    public List<Agent> getIdleAgentsByUser(String userName);

    public List<Agent> getAgentsByCampaign(Long campaignId);

    public List<Agent> getAgentsBySkill(Long skillId);

    public List<Agent> getAgentsBySkill(Long skillId, Agent.Mode mode);

    public List<Agent> getHuntingAgentsBySkill(Long skillId);

//    public List<Agent> getAgentsBySkillandUser(String skillName, String username);
//    public Agent getAgentByAgentId(String userName, String agent);
    public Agent getAgentByAgentIdV2(String userName, String agent);

    public Agent getAgentByAgentIdUserId(Long userId, String agent);

    public List<Agent> getIdleAgents();

    public List<Agent> getIdleAgentsByCampaign(Long campaignId);

    public List<Agent> getBusyAgents();

    public List<Agent> getLoggedInAgents();

    public Campaign getCampaignByAgentId(String agentId);

    public String getRedisKey(Agent agent);

    public StatusMessage isAgentLoginByPhoneNumber(Long FwpId, Long userId);

    public String isAgentLoggedin(Long agentId);

    public StatusMessage delAgent(Long agentSeqId);

    public StatusMessage isValidAgent(Long agentId, Long fwpId, Long userId);

    /**
     *
     * @param username
     * @param agentId
     * @param phoneNumber
     * @param usId client connect id
     * @param reconnect
     * @param mode
     * @return
     */
    public String loginAgent(String username, String agentId, String phoneNumber, String usId, boolean reconnect, Event.AgentMode mode);

    /**
     * logs out agent.
     *
     * @param username
     * @param agentId
     * @param agentPhoneNumber
     * @param mode
     * @param logoffMessage
     * @return
     */
    public StatusMessage logoutAgent(String username, String agentId, String agentPhoneNumber, Event.AgentMode mode, String logoffMessage);

    /**
     * this will logs out agent and informs the agent Toolbar to logout if agent
     * is connected.
     *
     * @param username
     * @param agentId
     * @param whoDidIt
     * @return
     */
//    public String logoutAgentByAdmin(String username, String agentId, String whoDidIt);
//    public String saveInRedis(Agent agent);
    /**
     * puts the agent in pause mode
     *
     * @param username
     * @param agentId
     * @param pauseReason
     * @param mode
     * @return
     */
    public StatusMessage pauseAgent(String username, String agentId, String pauseReason, Event.AgentMode mode);

    public String sendPauseAlertToAgent(String username, Long agentUniqId, String agentId, String pauseReason, String clientId);

    public StatusMessage releaseAgent(String username, String agentId, Event.AgentMode mode, String releaseMessage);

    /**
     * returns agent list containing id and agent login ID, other properties
     * will be null.
     *
     * @param username
     * @return
     */
    public List<Agent> getTransferAgentList(String username, Long agentUniqueId);

    /**
     * Locks the agent only if the agent is not already locked and IDLE in
     * state. Sets next flag to 1 and sets the number agent is talking to.
     *
     * @param username
     * @param agentId
     * @param customerNumber customer number the agent is assigned to
     * @return <code>true</code> if locked successfully otherwise
     * <code>false</code> returns <code>null</code>.
     */
    public boolean lockIfAgentAvailable(String username, String agentId, String customerNumber);

    public int lockAgent(Long agentUniqueId, String customerNumber);

    /**
     *
     * @param username
     * @param agentId
     * @param mode
     * @param state
     * @return
     */
    public StatusMessage setAgentMode(String username, String agentId, String mode, String state);

    /**
     *
     * @param user
     * @param agentId
     * @return
     */
    public List<Report> getAgentCallHistory(String user, String agentId);

    public int releaseAgentLockWithDialStatus(String user, Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentWithException, boolean dropcall);

    public Map<String, Object> getAgentPerformance(String user, Long agentUniqueId);

    public String sendScreenBargeRequest(String user, Long agentUniqueId, String agentId, String requesetPeerId);

    public int decrementAgentChatSessionsInDB(Long agentUniqId);

    public StatusMessage updatePassword(String user, String agentLoginId, Long agentUniqId, String oldPwd, String newPwd);

    public String performCallEvent(String event, String agentId, String apiKey, String ucid);

//    public AgentFreeStatus getAgentFreeStatus(Long id);
    public int saveAgentLogin(Long id, boolean reconnect, String phoneNumber, FwpNumber fwp, String clientId);

    public int saveAgentLogout(Long id);

    public int saveReleasedAgent(Long uniqId, Agent.Mode mode);

    public int savePauseAgent(Long uniqId, String reason);

    public int saveAgentMode(Long uniqId, Agent.Mode mode, boolean reconnect);

    public int saveAgentReconnect(Long agentUniqId, String clientId);

//    public int releaseAgentLock(Long id, boolean unlock);
    public boolean makeAgentBusy(Long agentUniqId, String contact, String channelName, String callType, Long monitorUcid);

    public boolean releaseAgentFromCall(Long agentUniqId, Agent.State state);

    /**
     * sets <code>nextFlag</code> in <code>agent</code> table to 0 and
     * <code>is_locked</code> flag to 0 in <code>AgentFreeStatus</code> table.
     *
     * @param agentUniqId
     * @return
     */
    public boolean releaseOnlyAgentFlag(Long agentUniqId);

    public boolean releaseFromConference(Long agentUniqId);

    public boolean agentCallStarted(Long agentUniqId, String contact, String type, String callStatus, String skill, String campName, Long agentMonitorUcid, boolean updateTimeStamp);

    public void syncToLdb(Long agentUniqId);

    public boolean updateHoldStartTime(Long agentId, boolean start);

    public List<Map<String, Object>> getLoggedInAgentsForUser(Long userId, Long subUserId);

    public List<Map<String, Object>> getLoggedInAgentsForUserAndCampaign(Long userId, Long subUserId, Long campId);

    public List<Map<String, Object>> getLoggedInAgentsForChat(Long userId);

    public List<Map<String, Object>> getInboundAgentStatusSkillWise(Long userId, String skillId);

    public int pauseChatSessions(Long agentUniqId);

    public List<Map<String, Object>> getChatTransferAgentList(String did, Long agentUniqueId);

    public int releaseAgentLockFromUpdateCallStatus(String user, Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentWithException, boolean dropcall, Long ucid);
}

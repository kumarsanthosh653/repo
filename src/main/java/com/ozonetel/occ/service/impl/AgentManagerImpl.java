package com.ozonetel.occ.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.dao.AgentDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;

import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentTokenManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.RedisManager;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.agentManager;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.userManager;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.HttpUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

@WebService(serviceName = "AgentService", endpointInterface = "com.ozonetel.occ.service.AgentManager")
public class AgentManagerImpl extends GenericManagerImpl<Agent, Long> implements AgentManager, MessageSourceAware {

    AgentDao agentDao;
//    AgentFreeStatusDao agentFreeStatusDao;

    public AgentManagerImpl(AgentDao agentDao) {
        super(agentDao);
        this.agentDao = agentDao;
//        this.agentFreeStatusDao = agentFreeStatusDao;
    }

    public List<Agent> getAgents() {
        return agentDao.getAgents();
    }

    public List<Agent> getAgentsByUser(String userName) {
        return agentDao.getAgentsByUser(userName);
    }

    public List<Agent> getIdleAgentsByUser(String userName) {
        return agentDao.getIdleAgentsByUser(userName);
    }

    public synchronized List<Agent> getAgentsByCampaign(Long campaignId) {
        // TODO Auto-generated method stub
        return agentDao.getAgentsByCampaign(campaignId);
    }

    public Agent getAgentByAgentIdV2(String user, String agent) {
        log.debug("REVISIT HERE..This code should be replaced with AGENT UNIQUE ID...");
        User u = userManager.getUserByUsername(user);
        List<Agent> agentsList = agentDao.getAgentByAgentIdV2(u.getId(), agent);
        if (agentsList.size() > 0) {
            return agentsList.get(0);
        }
        return null;
    }

    public Agent getAgentByAgentIdUserId(Long userId, String agent) {
        List<Agent> agentsList = agentDao.getAgentByAgentIdV2(userId, agent);
        if (agentsList.size() > 0) {
            return agentsList.get(0);
        }
        return null;
    }

    public List<Campaign> getCampaignByAgentId(Long id) {
        return agentDao.getCampaignByAgentId(id);
    }

    public Campaign getCampaignByAgentId(String agentId) {
        List<Agent> agents = getAll();
        for (Agent agent : agents) {
            if (agent.getAgentId().equalsIgnoreCase(agentId)) {
//                return agent.getCampaign();
                log.debug("REVISIT CODE HERE SOME THING NEEDS to be CHANGED ...");
                return null;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatusMessage logoutAgent(String username, String agentId, String agentPhoneNumber, Event.AgentMode mode, String logoffMessage) {
        /* not using anywhere so commenting
        Agent a = getAgentByAgentId(username, agentId);

        if (null != a && a.isLoggedIn()) {
            Agent.Mode agentMode = a.getMode();
            Agent.State agentState = a.getState();
            a.setState(Agent.State.AUX);
            a.setStateReason(null);
            a.setIdleTime(System.currentTimeMillis());
            a.setClientId(null);
            a.setPhoneNumber(null);

            if (a.getFwpNumber() != null) {
                fwpNumberManager.resetFwpNumber(a.getFwpNumber());
            }

            a.setFwpNumber(null);
            a.setIdleTime(System.currentTimeMillis());
            save(a);

            log.info("@@@@@Removing from sets in logout action:");
            redisAgentManager.deleteFromSet(username + ":mode:" + agentMode, a.getAgentId());
            redisAgentManager.deleteFromSet(username + ":state:" + agentState, a.getAgentId());
            redisAgentManager.deleteFromSet(username + ":loggedin", a.getAgentId());
            //
            // ----- > Log the event.
            eventManager.logEvent(Constants.EVENT_LOGOUT, a, new Date(), null, logoffMessage, null);
            log.debug("Success : [" + username + "][" + agentId + "] is logged out with #" + agentPhoneNumber);
            return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.logout", new Object[]{agentId, username}, Locale.getDefault()));
        } else {
            log.debug("Error: [" + username + "][" + agentId + "] is NULL");
            log.debug("Error: tbAgentLogin");

            return new StatusMessage(Status.ERROR, messageSource.getMessage("error.agent.notfound", new Object[]{agentId, username}, Locale.getDefault()));
        }*/
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String loginAgent(String username, String agentId, String phoneNumber, String usId, boolean reconnect, Event.AgentMode mode) {
        /* not using anywhere so commenting
        Agent agent = getAgentByAgentId(username, agentId);
        if (null != agent && agent.isLoggedIn()) {

            Event agentLastEvent = eventManager.getLastEventForAgent(username, agent);
            log.debug("Agent last event:" + agentLastEvent);
            //
            // ----> If agent is logged in or has not logged out properly logout forcefully.
            if (!reconnect && (agent.getClientId() != null || (agentLastEvent != null && StringUtils.equalsIgnoreCase("logout", agentLastEvent.getEvent())))) {
                logoutAgentByAdmin(username, agentId, "admin:from_login_request");
            }

            agent.setIdleTime(System.currentTimeMillis());
            agent.setStateReason(null);
            agent.setPhoneNumber(phoneNumber);
            if (!reconnect) {
                agent.setMode(Agent.Mode.INBOUND);
            }
            FwpNumber fwp = fwpNumberManager.getFwpNumberByPhone(phoneNumber, username);
            fwp.setAgent(agent);
            fwp.setLastSelected(System.currentTimeMillis());
            fwpNumberManager.save(fwp);
            agent.setFwpNumber(fwp);
            if (usId != null && !usId.isEmpty()) {
                agent.setClientId(usId);
            }
            agent.setState(Agent.State.AUX);
            agent.setLastSelected(System.currentTimeMillis());
            save(agent);

            log.info(">>>>>Going to save in redis...");
            try {
//                    redisAgentManager.saveAsJson(username + ":" + agentId, a);
                log.info("Saved agent");
                redisAgentManager.addToSet(agent.getUser().getUsername() + ":mode:" + agent.getMode(), agent.getAgentId());
                log.info("Added to mode set");
                redisAgentManager.addToSet(agent.getUser().getUsername() + ":state:" + agent.getState(), agent.getAgentId());
                log.info("Added to state set");
                redisAgentManager.addToSet(agent.getUser().getUsername() + ":loggedin", agent.getAgentId());
                log.info("Added to logged in set");
                redisAgentManager.zadd(agent.getUser().getUsername() + ":agent:scores", System.currentTimeMillis() * agent.getPriority(), agent.getAgentId());
            } catch (StackOverflowError error) {
                log.error(error.getMessage(), error);
            }

            //
            // ----- > Log the event
            eventManager.logEvent(reconnect ? Constants.EVENT_RECONNECT : Constants.EVENT_LOGIN, agent, new Date(), null, null, null);

        }

        return "Success";*/
        return null;
    }

    @Override
    public String sendPauseAlertToAgent(String username, Long agentUniqId, String agentId, String pauseReason, String clientId) {

        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setType("pauseFromApi");
        tokenResponse.setString("reason", pauseReason);

//        toolBarManager.tbAgentPause(username, agentId, pauseReason, Event.AgentMode.valueOf(agent.getMode().toString()));
//        return "SUCCESS";
        tokenServer = getTokenServer();
        if (StringUtils.isBlank(clientId)) {
            clientId = agentDao.get(agentUniqId).getClientId();
        }
        if (tokenServer.getConnector(clientId) != null) {
            tokenServer.sendToken(tokenServer.getConnector(clientId), tokenResponse);
            return "Sent pause alert";
        } else {
            log.debug("[" + agentId + "]Agent unable to send to Client [" + clientId + "]" + " so will not be released.");
            return "Failed to connect to agent.";
        }
    }

    @Override
    public StatusMessage pauseAgent(String username, String agentId, String pauseReason, Event.AgentMode mode) {
        /* not using anywhere so commenting
        StatusMessage statusMessage;
        Agent a = getAgentByAgentId(username, agentId);
        if (null != a && a.isLoggedIn()) {
            a.setState(Agent.State.AUX);
            a.setIdleTime(System.currentTimeMillis());
            a.setStateReason(pauseReason);
            save(a);
            statusMessage = new StatusMessage(Status.SUCCESS, "Agent paused.");
            eventManager.logEvent(username, a, new Date(), null, pauseReason, null);

        } else {
            statusMessage = new StatusMessage(Status.ERROR, "AgentPause Fail,Reason:" + (a == null ? "No agent found with the agent id '" + agentId + "'" : "Agent is not logged in with this id:" + agentId));
        }

        return statusMessage;*/
        return null;
    }

    @Override
    public StatusMessage releaseAgent(String username, String agentId, Event.AgentMode mode, String releaseMessage) {
        /* not using anywhere so commenting
        Agent agent = getAgentByAgentId(username, agentId);

        if (null != agent && agent.isLoggedIn()) {
            agent.setState(Agent.State.IDLE);
            agent.setIdleSince(0);
            agent.setIdleTime(System.currentTimeMillis());
            agent.setNextFlag(new Long(0));
            agent.setContact("");
            agent.setCallExceptions(new Long(0));
            agent.setDirectCallCount(agent.getDirectCallCount() - 1);
            agent.setMode(Agent.Mode.valueOf(mode.toString()));
            agent.setStateReason(null);
            agent = save(agent);

            //If agent is released in PROGRESSIVE/BLENDED inform the dialer.
            if ((agent.getMode() == Agent.Mode.PROGRESSIVE) || (agent.getMode() == Agent.Mode.BLENDED)) {
                previewDialerManager.informDialer(username, agent.getAgentId(), null);
            }

            eventManager.logEvent(Constants.EVENT_RELEASE, agent, new Date(), null, releaseMessage, null);

            log.debug("Success: [" + username + "][" + agentId + "] is released with " + agent.getMode());
            return new StatusMessage(Status.SUCCESS, "Agent released in " + agent.getMode());

        } else {
            log.error("Release error : [" + username + "][" + agentId + "] is NULL/Not logged in :" + agent);
            return new StatusMessage(Status.ERROR, "Agent is not released.");
        }*/
        return null;
    }

    @Override
    public boolean lockIfAgentAvailable(String username, String agentId, String customerNumber) {
        log.debug("Locking if agent available : " + agentId + " for user: " + username + " customer : " + customerNumber);
        boolean locked = false;
        Long agentUniqueId = null;

        String cachedAgentUniqId = redisAgentManager.getString(username + ":agent:" + agentId);
        if (!StringUtils.isBlank(cachedAgentUniqId)) {
            agentUniqueId = Long.valueOf(cachedAgentUniqId);
        } else {
            agentUniqueId = agentManager.getAgentByAgentIdV2(username, agentId).getId();
        }
        //check if agent is availabe for call in agentfreestatus table for optimal users only.
//        if (redisAgentManager.sismember("optimal:users", StringUtils.lowerCase(username))) {
//            if ((locked = agentFreeStatusDao.lockAgentFreeStatusIfAvailable(agentUniqueId))) {
//                log.debug("Locked agentfreestatus with id:" + agentUniqueId + " for user:" + username + " agent loginid:" + agentId);
//                agentDao.lockAgent(agentUniqueId, customerNumber);//system monitor purpose->idletime,calling..
//            }
//        } else {
//            locked = agentDao.lockAgent(agentUniqueId, customerNumber) > 0;//system monitor purpose->idletime,calling..
//        }
        locked = agentDao.lockIfAgentAvailable(agentUniqueId, customerNumber) > 0;//system monitor purpose->idletime,calling..
        log.debug("Locked if agent available : " + agentId + " for user: " + username + " customer : " + customerNumber + " ? " + locked);

        return locked;

    }

    public int lockAgent(Long agentUniqueId, String customerNumber) {
        return agentDao.lockAgent(agentUniqueId, customerNumber);//system monitor purpose->idletime,calling..
    }

    @Override
    public StatusMessage setAgentMode(String username, String agentId, String mode, String state) {
        /* not using anywhere so commenting
        Agent agent = getAgentByAgentId(username, agentId);
        log.debug("SettingAgentMode :[" + username + "][" + agentId + "][" + mode + "]");

        if (null != agent && agent.isLoggedIn()) {
            Agent.Mode agentMode = Agent.Mode.INBOUND;
            try {
                agentMode = Agent.Mode.valueOf(mode.toUpperCase());
            } catch (Exception ignore) {

            }

            agent.setMode(agentMode);
            save(agent);

            String eventName, eventData = agent.getStateReason();

            if (StringUtils.equalsIgnoreCase(state, Constants.READY)) {
                eventName = Constants.EVENT_RELEASE;
            } else if (StringUtils.equalsIgnoreCase(state, Constants.PAUSED)) {
                eventName = Constants.EVENT_PAUSE;
            } else {
                eventName = Constants.EVENT_PAUSE;
                eventData = "NotReady";
            }

            if (StringUtils.isEmpty(eventData)) {
                eventData = "changeMode";
            }

            eventManager.logEvent(eventName, agent, new Date(), null, eventData, null);

            log.debug("Success: [" + username + "][" + agentId + "] mode changed to [" + mode + "]");

            return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.agent.modechange", new Object[]{agent.getAgentName(), "" + agent.getMode()}, Locale.getDefault()));
        } else {
            log.debug("Error: [" + username + "][" + agentId + "][" + mode + "]");
            return new StatusMessage(Status.SUCCESS, messageSource.getMessage("error.changemode", null, Locale.getDefault()));
        }
         */
        return null;
    }

    @Override
    public int releaseAgentLockWithDialStatus(String user, Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentWithException, boolean dropcall) {

//        agentFreeStatusDao.releaseAgentLock(id, dropcall);
        return agentDao.releaseAgentLockWithDialStatus(id, dialStatus, isProgressive, isException, exceptionCount, lockAgentWithException);
    }

    @Override
    public int releaseAgentLockFromUpdateCallStatus(String user, Long id, String dialStatus, boolean isProgressive, boolean isException, int exceptionCount, boolean lockAgentWithException, boolean dropcall, Long ucid) {
        log.debug("Releasing agent from update call status : " + user + " | " + id + " | " + ucid);
        int released = 0;
        released = agentDao.releaseAgentLockFromUpdateCallStatus(id, dialStatus, isProgressive, isException, exceptionCount, lockAgentWithException, ucid);
        log.debug("Released agent from update call status ? " + released +" | "+user + " | " + id + " | " + ucid);
        return released;
    }

//    @Override
//    public int releaseAgentLock(Long id, boolean unlock) {
//        return agentFreeStatusDao.releaseAgentLock(id, unlock);
//    }
    public void syncToLdb(Long agentUniqId) {
        log.debug("Syncing agent to local DB | agentId : " + agentUniqId);
        Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
        queryParams.put("agentId", agentUniqId);
        this.executeProcedure("{Call Login_Agent(?)}", queryParams);
    }

    @Override
    public String sendScreenBargeRequest(String user, Long agentUniqueId, String agentId, String requesetPeerId) {
        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setType("shareYourScreen");
        tokenResponse.setString("peerId", requesetPeerId);
        tokenResponse.setString("agentId", agentId);
        tokenResponse.setString("user", user);
        return "" + agentTokenManager.sendTokenToAgent(this.get(agentUniqueId), tokenResponse);
    }

    @Override
    public List<Report> getAgentCallHistory(String user, String agentId) {

        if (userManager.hasRole(user, Constants.AGENT_CALL_HISTORY_ROLE)) {
            return redisReportManager.getList(user + ":agentcallhistory:" + agentId, 0, 20);
        }

        return null;
    }

    @Override
    public String performCallEvent(String event, String agentId, String apiKey, String ucid) {
        try {
            Agent a = this.getAgentByAgentIdV2(userManager.getUserByApiKey(apiKey).getUsername(), agentId);
//        log.debug(a);
            Token tokenResponse = TokenFactory.createToken();
            tokenResponse.setType("performCallEvent");
            tokenResponse.setString("event", event);
            return "" + agentTokenManager.sendTokenToAgent(a, tokenResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "ERROR";

    }

//    @Override
//    public AgentFreeStatus getAgentFreeStatus(Long id) {
//        return agentFreeStatusDao.get(id);
//    }
    @Override
    public int saveAgentLogin(Long id, boolean reconnect, String phoneNumber, FwpNumber fwp, String clientId) {
//        redisAgentManager.save(a.getUser().getUsername() + ":agents", getRedisKey(a), a);
//        agentFreeStatusDao.saveAgentLogin(id, reconnect, phoneNumber, fwp, clientId);
        return agentDao.saveAgentLogin(id, fwp, clientId, phoneNumber, reconnect);
    }

    @Override
    public int saveAgentLogout(Long id) {
//        redisAgentManager.save(a.getUser().getUsername() + ":agents", getRedisKey(a), a);
//        agentFreeStatusDao.saveAgentLogout(id);
        return agentDao.saveAgentLogout(id);
    }

    @Override
    public int saveReleasedAgent(Long uniqId, Agent.Mode mode) {
        //redisAgentManager.save(a.getUser().getUsername() + ":agents", getRedisKey(a), a);
//        int freUpdate = agentFreeStatusDao.saveReleasedAgent(uniqId);
//        log.debug("Updated agent free status for agent:" + uniqId + " | cout:" + freUpdate);
        return agentDao.saveReleasedAgent(uniqId, mode);
    }

    @Override
    public int savePauseAgent(Long uniqId, String reason) {
//        redisAgentManager.save(a.getUser().getUsername() + ":agents", getRedisKey(a), a);
//        agentFreeStatusDao.savePauseAgent(uniqId);
        return agentDao.savePauseAgent(uniqId, reason);
    }

    @Override
    public int saveAgentMode(Long uniqId, Agent.Mode mode, boolean reconnect) {
//        redisAgentManager.save(a.getUser().getUsername() + ":agents", getRedisKey(a), a);
//        agentFreeStatusDao.saveAgentMode(uniqId, mode, reconnect);
        return agentDao.saveAgentMode(uniqId, mode, reconnect);
    }

    @Override
    public int saveAgentReconnect(Long agentUniqId, String clientId) {
//        redisAgentManager.save(a.getUser().getUsername() + ":agents", getRedisKey(a), a);
//        agentFreeStatusDao.saveAgentReconnect(agentUniqId, clientId);
        return agentDao.saveAgentReconnect(agentUniqId, clientId);
    }

    @Override
    public boolean makeAgentBusy(Long agentUniqId, String contact, String channelName, String callType, Long monitorUcid) {
        return agentDao.makeAgentBusy(agentUniqId, contact, channelName, callType, monitorUcid);
    }

    @Override
    public boolean releaseAgentFromCall(Long agentUniqId, Agent.State state) {
        return agentDao.releaseAgentFromCall(agentUniqId, state);
    }

    @Override
    public boolean releaseOnlyAgentFlag(Long agentUniqId) {
        log.debug("Releasing agent nextflag to 0: for agent uniqID" + agentUniqId);
        //        if (agentDao.releaseAgentNextFlag(agentUniqId)) {
//            return agentFreeStatusDao.releaseAgentLockFlag(agentUniqId) > 0;
//        }
        return agentDao.releaseAgentNextFlag(agentUniqId);
    }

    public boolean releaseFromConference(Long agentUniqId) {
        log.debug("Releasing agent:" + agentUniqId + " from conference..");
        return agentDao.releaseAgentFromCall(agentUniqId, Agent.State.IDLE);
//        return agentFreeStatusDao.saveReleasedAgent(agentUniqId) > 0;
    }

    @Override
    public boolean agentCallStarted(Long agentUniqId, String contact, String type, String callStatus, String skill, String campName, Long agentMonitorUcid, boolean updateTimeStamp) {
        log.debug("Agent Call Started now : " + agentUniqId + " ucid : " + agentMonitorUcid + " conatct : " + contact + " type: " + type + " callStatus : " + callStatus + " skill : " + skill + " campaname: " + campName + " updatetimestamp : " + updateTimeStamp);
        return agentDao.agentCallStarted(agentUniqId, contact, type, callStatus, skill, campName, agentMonitorUcid, updateTimeStamp);
    }

    public boolean updateHoldStartTime(Long agentId, boolean start) {
        log.debug("Updating hold start time for agent : " + agentId + " is hold start? " + start);
        return agentDao.updateHoldStartTime(agentId, start);
    }

    @Override
    public List<Agent> getTransferAgentList(String username, Long agentUniqueId) {
        return agentDao.getTransferAgentList(userManager.getUserByUsername(username).getId(), agentUniqueId);
    }

    @Override
    public List<Map<String, Object>> getChatTransferAgentList(String did, Long agentUniqueId) {
        return agentDao.getChatTransferAgentList(did, agentUniqueId);
    }

    @Override
    public String getRedisKey(Agent agent) {
        return "agent:" + agent.getId();
    }

    public List<Agent> getIdleAgents() {
        // TODO Auto-generated method stub
        return agentDao.getIdleAgents();
    }

    public List<Agent> getAgentsBySkill(Long skillId) {
        return agentDao.getAgentsBySkill(skillId);
    }

    public List<Agent> getAgentsBySkill(Long skillId, Agent.Mode mode) {
        return agentDao.getAgentsBySkill(skillId, mode);
    }

    public List<Agent> getHuntingAgentsBySkill(Long skillId) {
        return agentDao.getHuntingAgentsBySkill(skillId);
    }

//    public List<Agent> getAgentsBySkillandUser(String skillName, String username) {
//        return agentDao.getAgentsBySkillandUser(skillName, username);
//    }
    public List<Agent> getIdleAgentsByCampaign(Long campaignId) {
        // TODO Auto-generated method stub
        return agentDao.getIdleAgentsByCampaign(campaignId);
    }

    public List<Agent> getBusyAgents() {
        // TODO Auto-generated method stub
        return agentDao.getBusyAgents();
    }

    public List<Agent> getLoggedInAgents() {
        // TODO Auto-generated method stub
        return agentDao.getLoggedInAgents();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setFwpNumberManager(FwpNumberManager fwpNumberManager) {
        this.fwpNumberManager = fwpNumberManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setAgentTokenManager(AgentTokenManager agentTokenManager) {
        this.agentTokenManager = agentTokenManager;
    }

    public void setRedisReportManager(RedisManager<Report> redisReportManager) {
        this.redisReportManager = redisReportManager;
    }

    public void setPreviewDialerManager(PreviewDialerManager previewDialerManager) {
        this.previewDialerManager = previewDialerManager;
    }

//    public AgentFreeStatusDao getAgentFreeStatusDao() {
//        return agentFreeStatusDao;
//    }
//
//    public void setAgentFreeStatusDao(AgentFreeStatusDao agentFreeStatusDao) {
//        this.agentFreeStatusDao = agentFreeStatusDao;
//    }
    private TokenServerLocalImpl getTokenServer() {
        if (tokenServer == null) {
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }
        return tokenServer;

    }

    @Override
    public int decrementAgentChatSessionsInDB(Long agentUniqId) {
//        agentFreeStatusDao.decrementChatSessionCount(agentUniqId);
        return agentDao.decrementChatSessionCount(agentUniqId);
    }

    @Override
    public int pauseChatSessions(Long agentUniqId) {
        return agentDao.pauseChatSessions(agentUniqId);
    }

    @Override
    public StatusMessage updatePassword(String user, String agentLoginId, Long agentUniqId, String oldPwd, String newPwd) {
//-----------------------------
        /*Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", agentUniqId);
        params.put("oldPwd", oldPwd);
        params.put("newPwd", newPwd);*/

        //currentpwd=Test1234&newpwd=Test123&confirmpwd=Test123
        try {
            log.debug("Update password request for user:" + user + "| agent:" + agentLoginId);

            URIBuilder passwordChangeUri = new URIBuilder(appProperty.getAdminPortalUrl() + "passwordChange.html");
            passwordChangeUri.addParameter("username", user);
            passwordChangeUri.addParameter("agentId", agentLoginId);
            passwordChangeUri.addParameter("currentpwd", oldPwd);
            passwordChangeUri.addParameter("newpwd", newPwd);
            passwordChangeUri.addParameter("confirmpwd", newPwd);
            passwordChangeUri.addParameter("apiKey", userManager.getUserByUsername(user).getApiKey());
            log.debug("Password change request:" + passwordChangeUri.build().toString());
            HttpResponseDetails response = HttpUtils.doGet(passwordChangeUri.build().toString());
            log.debug(passwordChangeUri.build().toString() + " | " + response.toLongString());
            JsonElement jelement = new JsonParser().parse(response.getResponseBody());
            return new StatusMessage(Status.valueOf(jelement.getAsJsonObject().get("status").getAsString().trim().toUpperCase()),
                    jelement.getAsJsonObject().get("message").getAsString());
        } catch (IOException | URISyntaxException ioe) {
            log.error(ioe.getMessage(), ioe);
        }

        /*  List<Map<String, Object>> result = eventManager.executeProcedure("CALL Reset_Agent_Password(?,?,?)", params);//we should use other than agentmanager. Agentmanager points to local db, but this procedure exists in main DB.
        log.debug("Password update result:" + result);
        if (result == null || result.isEmpty()) {
            return new StatusMessage(Status.ERROR, "Check your current password!");
        }

        if (StringUtils.equalsIgnoreCase((String) result.get(0).get("Status"), "success")) {
            return new StatusMessage(Status.SUCCESS, "Password updated successfully!");
        }*/
        return new StatusMessage(Status.ERROR, "Check your current password!");
    }

    @Override
    public Map<String, Object> getAgentPerformance(String user, Long agentUniqueId) {
        Agent agentObj = this.get(agentUniqueId);
        Map<String, Object> agentPerformance = null;
        List list = agentDao.getAgentPerformance(agentObj.getUserId(), agentObj.getId());
        if (list.size() > 0) {
            agentPerformance = (Map<String, Object>) list.get(0);
        }
        list = agentDao.getAgentSummary(agentObj.getId());
        if (list.size() > 0) {
            agentPerformance.putAll((Map<String, Object>) list.get(0));
        }
        return agentPerformance;
    }

    @Override
    public StatusMessage isAgentLoginByPhoneNumber(Long FwpId, Long userId) {
        Map<String, Object> reportParams = new HashMap<String, Object>();
        reportParams.put("FwpId", FwpId);
        reportParams.put("userId", userId);
        List<Agent> agents = agentManager.findByNamedQuery("isAgentLoginByPhoneNumberId", reportParams);
        log.debug("agents Size = " + agents.size());
        if (agents.size() > 0) {
            Agent agent2 = agents.get(0);
            log.debug("Agent " + agent2.getAgentName() + " logged in with this number");
            return new StatusMessage(Status.ERROR, agent2.getAgentId() + " logged in with the number(" + agent2.getFwpNumber().getPhoneNumber() + ")");
        } else {
            log.debug("Phone number is free:" + FwpId);
            return new StatusMessage(Status.SUCCESS, "Phone number is free");
        }
    }

    @Override
    public String isAgentLoggedin(Long agentId) {
        return "" + agentDao.isAgentLoggedin(agentId);
    }

    public StatusMessage delAgent(Long agentSeqId) {
        this.log.debug((Object) ("Trying to Delete the Agent from Local DB for the ID : " + agentSeqId));
        log.debug("The Response : " + agentDao.isAgentLoggedin(agentSeqId));
        /* try{
            log.debug("Thread went to sleep");
        Thread.currentThread().sleep(10000);
        }catch(InterruptedException e){
            log.debug("Thread got interrupted");
            log.error(e.getMessage(), e);
        } */
        if (agentDao.isAgentLoggedin(agentSeqId)) {
            log.debug("Agent " + agentSeqId + " already Logged in hence not allowing to Delete");
            return new StatusMessage(Status.ERROR, "Agent already Logged in");
        } else {
            boolean isDeleted = false;
            try {
                Agent a = (Agent) this.get(agentSeqId);
                log.debug((Object) ("AgentName : " + a.getAgentName() + " AgentID : " + a.getAgentId() + " ID : " + a.getId()));
                a.setActive(false);
                for (int i = 1; i < 4; ++i) {
                    try {
                        this.save(a);
                        isDeleted = true;
                        log.debug((Object) ("Successfully Deleted the Agent for the ID : " + agentSeqId + " in " + i + " try"));
                        break;
                    } catch (Exception e) {
                        isDeleted = false;
                        log.error("Problem in Deletion of the Agent with id : " + agentSeqId, e);
                    }
                }
                if (isDeleted) {
                    return new StatusMessage(Status.SUCCESS, "Agent Deleted Successfully");
                }
                return new StatusMessage(Status.ERROR, "Agent not deleted due to some technical issue");
            } catch (NullPointerException e) {
//            log.debug("Not able to fetch the Agent details as Agent not yet logged in with the Id : " + agentId);
                log.error("Not able to fetch the Agent details as Agent not yet logged in with the Id : " + agentSeqId, e);
                return new StatusMessage(Status.SUCCESS, "Agent not yet synced to Local DB");
            } catch (Exception e) {
//            log.debug("Not able to delete as some exception raised for the AgentID : " + agentId);
                log.error("Not able to delete the Agent with id " + agentSeqId + " due to some Exception", e);
                return new StatusMessage(Status.ERROR, "Not able to delete the AgentID : " + agentSeqId + " as some exception raised");
            }
        }
    }

    @Override
    public StatusMessage isValidAgent(Long agentId, Long fwpId, Long userId) {
        String loggedin = isAgentLoggedin(agentId);//Used to check if agent is already logged in or not
        if (StringUtils.equalsIgnoreCase("true", loggedin)) {
            log.debug("Already Agent Logged in:" + agentId);
            return new StatusMessage(Status.ERROR, "Agent has already logged in");
        } else {
//            return isAgentLoginByPhoneNumber(fwpId, userId);
            StatusMessage status = isAgentLoginByPhoneNumber(fwpId, userId);
            //Checking Agent License
            if (status.getStatus().toString().equals("SUCCESS")) {
                int loggedInAgentCount = Integer.parseInt(agentDao.getLoggedInAgentsCountByUser(userId).toString());
                //Getting the count of Logged in Agents
                Map<String, Object> params = new LinkedHashMap();
                params.put("user_id", userId);
                params.put("isAdmin", 1);
                params.put("param_code", "AGENT_LOGIN_LIMIT");
                List loginLimitParameter = eventManager.executeProcedure("call Get_UserParamterV2(?,?,?)", params);
//                    log.debug("User agent limit setting:" + loginLimitParameter);
                if (loginLimitParameter != null && !loginLimitParameter.isEmpty()) {
//                        log.trace(loginLimitParameter.get(0));
                    String value = ((Map<String, Object>) loginLimitParameter.get(0)).get("ParameterValue") == null ? ((Map<String, Object>) loginLimitParameter.get(0)).get("DefaultValue").toString() : ((Map<String, Object>) loginLimitParameter.get(0)).get("ParameterValue").toString();
//                        log.debug("Agent Login Limit value = " + value);
                    if (!StringUtils.isBlank(value)) {
                        log.debug("Agent License is : " + value + " No of Agents currently Logged in : " + loggedInAgentCount);
                        if (value.equals("-1") || loggedInAgentCount < Integer.parseInt(value)) {
                            return new StatusMessage(Status.SUCCESS, "Valid Agent");
                        } else {
                            return new StatusMessage(Status.ERROR, "Agent Login Limit Exceeded");
                        }

                    } else {
                        return new StatusMessage(Status.ERROR, "Agent Login Limit not Set");
                    }
                } else {
                    return new StatusMessage(Status.ERROR, "Not able to login, Please contact Admin");
                }
//                return new StatusMessage(Status.SUCCESS, agentDao.getLoggedInAgentsCountByUser(userId).toString());
            } else {
                return status;
            }
        }

    }

    @Override
    public List<Map<String, Object>> getLoggedInAgentsForUser(Long userId, Long subUserId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserID", userId);
        params.put("SubUserID", subUserId);
        return agentManager.executeProcedure("{call Get_MONAgentStatusV31(?,?)}", params);
    }

    @Override
    public List<Map<String, Object>> getLoggedInAgentsForUserAndCampaign(Long userId, Long subUserId, Long campId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserID", userId);
        params.put("SubUserID", subUserId);
        params.put("campaignId", campId);
        return agentManager.executeProcedure("{call Get_MONAgentStatusV41(?,?,?)}", params);
    }

    @Override
    public List<Map<String, Object>> getInboundAgentStatusSkillWise(Long userId, String skillId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("pUserID", userId);
        params.put("pSkillID", skillId);
        return agentManager.executeProcedure("{call Get_InboundAgentStatusSkillWise(?,?)}", params);
    }

    public List<Map<String, Object>> getLoggedInAgentsForChat(Long userId) {
        return agentDao.getLoggedInAgentsForChat(userId);
    }

    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    public TokenServerLocalImpl tokenServer;
    private RedisAgentManager redisAgentManager;
    private FwpNumberManager fwpNumberManager;
    private EventManager eventManager;
    private AgentTokenManager agentTokenManager;
    private MessageSource messageSource;
    private RedisManager<Report> redisReportManager;
    private PreviewDialerManager previewDialerManager;
    private AppProperty appProperty;

}

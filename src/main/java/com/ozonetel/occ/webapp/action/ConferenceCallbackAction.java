package com.ozonetel.occ.webapp.action;

import com.google.gson.Gson;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallConfDetail;
import com.ozonetel.occ.model.CallHoldDetail;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.HoldDetailManager;
import com.ozonetel.occ.service.RedisManager;
import com.ozonetel.occ.service.impl.Status;
import com.ozonetel.occ.service.impl.TokenServerLocalImpl;
import com.ozonetel.occ.webapp.util.RequestUtil;
import java.math.BigInteger;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 *
 * @author pavanj
 */
public class ConferenceCallbackAction extends BaseAction {

    @Override
    public String execute() throws Exception {
        try {
            statusMessage = new StatusMessage(Status.ERROR, "");
            log.trace("Income parameters are:" + RequestUtil.getRequestParams(getRequest()));
            if (StringUtils.endsWithIgnoreCase(callStatus, "conferenceEnd")) {
                Date endDate = new Date();
                if (StringUtils.isNotEmpty(confAgentId)) {
                    try {
                        Agent participatedAgent = agentManager.get(confAgentUniqId);
                        agentManager.releaseFromConference(participatedAgent.getId());
                        log.debug("Unlocked participated agent after conference end " + participatedAgent);

                        Token tokenResponse = TokenFactory.createToken();
                        tokenResponse.setType("agentReleaseWOACW");
                        tokenResponse.setString("custNumber", holdNumber);
                        tokenResponse.setString("callStatus", "Success");
                        tokenResponse.setString("agentMonitorUcid", monitorUcid);
                        tokenResponse.setString("agentStatus", "ACW");
                        if (participatedAgent.getClientId() != null && tokenServer.getConnector(participatedAgent.getClientId()) != null) {
                            tokenServer.sendToken(tokenServer.getConnector(participatedAgent.getClientId()), tokenResponse);
                            log.debug("Sent conferenceEnd event to participant agent :[" + confAgentId + "]");
                        } else {
                            log.fatal("Unable to sent UnHold event to Client [" + confAgentId + "]" + participatedAgent);
                        }
                        redisAgentManager.hset(user + ":agent:events", confAgentId, new Gson().toJson(tokenResponse.getMap()));
                        eventManager.logEvent(Agent.State.IDLE.name(), participatedAgent.getUserId(), user, participatedAgent.getId(), participatedAgent.getAgentId(), participatedAgent.getMode(), new Date(), Long.valueOf(ucid), "CONFERENCE", agentId);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                CallConfDetail confDetail = (CallConfDetail) callConfDetailManager.get(id);
                Agent a = agentManager.getAgentByAgentIdV2(user, agentId);
                if (confDetail != null) {
                    Token tokenResponse = TokenFactory.createToken();
                    tokenResponse.setType("conferenceEnd");
                    tokenResponse.setString("custNumber", phoneNumber);
                    if (a.getClientId() != null && tokenServer.getConnector(a.getClientId()) != null) {
                        tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                        log.debug("Sent conferenceEnd event to ScreenPop :[" + agentId + "]");
                    } else {
                        log.fatal("Unable to sent UnHold event to Client [" + agentId + "]");
                    }

                    confDetail.setEndTime(endDate);
                    confDetail.setExitStatus(dialStatus);
                    confDetail.setDialStatus(dialStatus.equals("answered") ? "SUCCESS" : "FAIL");
                    confDetail.setAduioFile(audioFile);
                    callConfDetailManager.save(confDetail);
//                    getRequest().setAttribute("status", "success");
                    statusMessage = new StatusMessage(Status.SUCCESS, "Graceful");
                } else {
                    //getRequest().setAttribute("status", "Fail:No record found");
                    statusMessage = new StatusMessage(Status.ERROR, "No record found");

                }

                CallHoldDetail callHoldDetail = holdDetailManager.getCallHoldDetailByUCID(new BigInteger(ucid), phoneNumber);
                if (callHoldDetail != null) {
                    callHoldDetail.setEndTime(endDate);
                    holdDetailManager.save(callHoldDetail);
                    agentManager.updateHoldStartTime(a.getId(), false);
                } else {
                    log.error("Got CallHoldDetail as null for ucid:" + ucid + " | Caller id:" + phoneNumber + " | monitor ucid:" + monitorUcid);
                }
            } else if (StringUtils.endsWithIgnoreCase(callStatus, "conferenceStart")) {
                if (StringUtils.isNotEmpty(confAgentId)) {
                    Agent participatedAgent = null;
                    if (confAgentUniqId != null) {
                        participatedAgent = agentManager.get(confAgentUniqId);
                    } else {
                        participatedAgent = agentManager.getAgentByAgentIdV2(user, confAgentId);
                    }

                    Token tokenResponse = TokenFactory.createToken();
                    tokenResponse.setType("conferenceStart");
                    tokenResponse.setString("custNumber", holdNumber);
                    tokenResponse.setString("callType", "Conference Call");
                    tokenResponse.setString("callerId", agentId);
                    tokenResponse.setString("ucid", ucid);
                    tokenResponse.setString("monitorUcid", monitorUcid);
                    tokenResponse.setString("did", did);
                    tokenResponse.setString("uui", holdNumber);

                    if (participatedAgent.getClientId() != null && tokenServer.getConnector(participatedAgent.getClientId()) != null) {
                        tokenServer.sendToken(tokenServer.getConnector(participatedAgent.getClientId()), tokenResponse);
                        log.debug("Sent conferenceStart event to participant agent :[" + confAgentId + "]");
                    } else {
                        log.fatal("Unable to send event to participant agent [" + confAgentId + "]");
                    }
                    agentManager.makeAgentBusy(participatedAgent.getId(), holdNumber, "", "Conference Call", new Long(monitorUcid));
                    eventManager.logEvent("incall",  participatedAgent.getUserId(), user, participatedAgent.getId(), participatedAgent.getAgentId(), participatedAgent.getMode(), new Date(), Long.valueOf(ucid), "CONFERENCE", agentId);
                    redisAgentManager.hset(user + ":agent:events", confAgentId, new Gson().toJson(tokenResponse.getMap()));

                    //eventManager.logEvent(Constants.CALLING, participatedAgent.getUserId(), user, participatedAgent.getId(), participatedAgent.getAgentId(), participatedAgent.getMode(), new Date(), Long.valueOf(ucid), "CONFERENCE", agentId);
                    //redisAgentManager.hset(user + ":agent:events", confAgentId, new Gson().toJson(tokenResponse.getMap()));
                }
            }
        } catch (Exception e) {
//            getRequest().setAttribute("status", "exception:" + e.getMessage());
            statusMessage = new StatusMessage(Status.EXCEPTION, e.getMessage());
            log.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setCallConfDetailManager(GenericManager callConfDetailManager) {
        this.callConfDetailManager = callConfDetailManager;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setConfAgentId(String confAgentId) {
        this.confAgentId = confAgentId;
    }

    public void setMonitorUcid(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    public void setHoldDetailManager(HoldDetailManager holdDetailManager) {
        this.holdDetailManager = holdDetailManager;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getDialStatus() {
        return dialStatus;
    }

    public void setDialStatus(String dialStatus) {
        this.dialStatus = dialStatus;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public RedisManager<Agent> getRedisAgentManager() {
        return redisAgentManager;
    }

    public void setRedisAgentManager(RedisManager<Agent> redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public String getHoldNumber() {
        return holdNumber;
    }

    public void setHoldNumber(String holdNumber) {
        this.holdNumber = holdNumber;
    }

    public void setConfAgentUniqId(Long confAgentUniqId) {
        this.confAgentUniqId = confAgentUniqId;
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setTokenServer(TokenServerLocalImpl tokenServer) {
        this.tokenServer = tokenServer;
    }

    private StatusMessage statusMessage;
    private GenericManager callConfDetailManager;
    private String callStatus;
    private String dialStatus;
    private String ucid;
    private String monitorUcid;
    private String sid;
    private String did;
    private String agentId;
    private String confAgentId;
    private Long confAgentUniqId;
    private Long id;
    private String phoneNumber;
    private String holdNumber;
    private AgentManager agentManager;
    private EventManager eventManager;
    private RedisManager<Agent> redisAgentManager;
    private String user;
    private TokenServerLocalImpl tokenServer ;
    private HoldDetailManager holdDetailManager;
    private String audioFile;
}

package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.jamesmurty.utils.XMLBuilder;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.RedisManager;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.agentManager;
import com.ozonetel.occ.util.AppContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 *
 * @author rajeshchary
 */
public class ReleaseAgent extends OCCManagerImpl implements Command {

    private String agentId;
    private String ucid;
    private String did;
    private String type;//check whether the call is Manual Dial by fwp or inbound
    private String transType;
    private int transferType = 0;
    private String campaignParam;
    private String campaignId;
    private String callStatus;
    private RedisManager<Agent> redisAgentManager;
    private PreviewDialerManager previewDialerManager;
    private String agentMonitorUcid;
    private boolean informDialer = false;
    private Campaign c = null;
    private final Date eventTime;

    private void init() {
        redisAgentManager = (RedisManager<Agent>) AppContext.getApplicationContext().getBean("redisAgentManager");
        previewDialerManager = (PreviewDialerManager) AppContext.getApplicationContext().getBean("previewDialerManager");
    }

    public ReleaseAgent(HttpServletRequest request, Date _eventTime) {
        super.initialize();
        this.eventTime = _eventTime;
        this.agentId = (String) request.getParameter("agentId");
        this.ucid = (String) request.getParameter("ucid");
        this.did = (String) request.getParameter("did");
        this.type = (String) request.getParameter("type");
        type = (type.isEmpty() ? "Inbound" : type);
        this.transType = (String) request.getParameter("transferType");
        this.agentMonitorUcid = request.getParameter("agentMonitorUcid");
        transferType = (!transType.isEmpty() ? Integer.parseInt(transType) : transferType);
        this.campaignParam = (String) request.getParameter("campaignId");
        this.campaignId = (campaignParam != null && !campaignParam.isEmpty()) ? campaignParam : null;
        this.callStatus = (String) request.getParameter("callStatus");
        init();
    }

    public ReleaseAgent(String agentId, String ucid, String did, String type, String transType, String campaignParam, String campaignId, String callStatus, String agentMonitorUcid, Date eventTime) {
        super.initialize();
        init();
        this.agentId = agentId;
        this.ucid = ucid;
        this.did = did;
        this.type = type;
        this.transType = transType;
        this.campaignParam = campaignParam;
        this.campaignId = campaignId;
        this.callStatus = callStatus;
        this.agentMonitorUcid = agentMonitorUcid;
        this.eventTime = eventTime;
    }

    public String execute() {
        return setAgentIdle(did, agentId, ucid, type, transferType, campaignId);
    }

    public String setAgentIdle(String did, String agentId, String ucid, String type, int transferType, final String campaignId) {
        try {

            XMLBuilder resp = getXMLBuilder("releaseAgent");

            Token tokenResponse = TokenFactory.createToken();
            if (campaignId != null && !campaignId.isEmpty()) {
                c = campaignManager.get(new Long(campaignId));
            } else if (type.equalsIgnoreCase("manual") || type.equalsIgnoreCase("toolbarmanual")) {
                c = campaignManager.getCampaignsByDid(did);
            } else {
                c = campaignManager.getCampaignsByDid(did, type);
            }
            String username = c != null ? c.getUser().getUsername() : getUsernameForDid(did);
            final Agent a;
            String cachedAgentUniqId = redisAgentManager.getString(username + ":agent:" + agentId);
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                a = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                a = agentManager.getAgentByAgentIdV2(username, agentId);
            }

            FwpNumber fwpNumber = null;
            if ((c != null && c.isOffLineMode()) || transferType == 3) {
                if (type.equalsIgnoreCase("manual")) {
                    if (agentId != null) {
//                        fwpNumber = fwpNumberManager.getFwpNumberByPhone(a.getPhoneNumber(), getUsernameForDid(did));
                        fwpNumber = fwpNumberManager.get(new Long(agentId));
                    }
                } else {
                    try {
                        fwpNumber = fwpNumberManager.get(new Long(agentId));
                    } catch (NumberFormatException e) {
                        log.debug("agentId [" + agentId + "] got number format exception");
                    }
                }
                if (fwpNumber != null) {
                    fwpNumber.setState(Agent.State.IDLE);
                    fwpNumber.setNextFlag(new Long(0));
                    fwpNumber.setContact("");
                    fwpNumber.setDirectCallCount((fwpNumber.getDirectCallCount() == null || fwpNumber.getDirectCallCount() < 0) ? 0L : (fwpNumber.getDirectCallCount() - 1));
                    fwpNumberManager.save(fwpNumber);
                    log.debug("Ucid : "+ ucid +" | Released FWP Number + "+fwpNumber);
                    return resp.e("status").t("1").asString();
                }

            }

            log.debug("ACW=" + c.getAcwNac());
            if (a != null && (agentMonitorUcid != null) && !a.getUcid().equals(Long.valueOf(agentMonitorUcid))) { // check if agent's current UCID and release event UCID are different.
                log.debug("Wrong sequence{ Agent UCID = " + a.getUcid() + ", Got UCID:" + agentMonitorUcid + " }");
                resp.e("status").t("Wrong sequence{ Agent UCID = " + a.getUcid() + ", Got UCID:" + ucid + " } ");
                return resp.toString();
            }
            log.debug("Agent in Release agent : " + a);
            if (null != a && (a.getState() == Agent.State.BUSY || ((a.getNextFlag() == 1 && a.getState() == Agent.State.IDLE) && (StringUtils.equalsIgnoreCase(callStatus, "success") || (c.getAcwNac() != null && c.getAcwNac() && !type.equalsIgnoreCase("inbound")))))) {// Forcinfg the agent ot ACW only when he is in  Busy
                Agent.State state = null;
                fwpNumber = a.getFwpNumber();
                if (fwpNumber != null) {
                    fwpNumber.setContact(null);
                    fwpNumber.setState(Agent.State.IDLE);
                    fwpNumber.setLastSelected(((System.currentTimeMillis() / 1000) * 1000));
                    fwpNumberManager.save(fwpNumber);
                }

                String eventName = null;

                if ((c != null && c.isOffLineMode()) || type.equalsIgnoreCase("manual") || c.getSla() == -1) {
//                    agentManager.releaseAgentLock(a.getId(), true);
                    state = Agent.State.IDLE;
                    eventName = "IDLE";
                    tokenResponse.setString("agentStatus", "Ready");
                    tokenResponse.setType("agentReleaseWOACW");// if wrapuptime =-1 not need of making to ACW state
                    if (a.getMode() == Agent.Mode.PROGRESSIVE || a.getMode() == Agent.Mode.BLENDED) {
                        informDialer = true;
                    }
                } else {
                    state = Agent.State.ACW;
                    eventName = "ACW";
                    tokenResponse.setType("agentRelease");
                    tokenResponse.setString("dispositionType", c.getDispositionType() != null ? c.getDispositionType() : "1");
                    tokenResponse.setString("callStatus", callStatus);
                    tokenResponse.setString("agentMonitorUcid", agentMonitorUcid);
                    tokenResponse.setString("agentStatus", "ACW");
                }

                eventManager.logEvent(eventName, a.getUserId(), c.getUser().getUsername(), a.getId(), a.getAgentId(), a.getMode(), eventTime, StringUtils.isBlank(ucid) ? null : Long.valueOf(ucid), null, null);
                //agentManager.save(a);
                agentManager.releaseAgentFromCall(a.getId(), state);

                if (informDialer) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            log.debug("Informing the Dialer for the user ");
                            previewDialerManager.informDialer(c.getUser().getUsername(), a.getAgentId(), StringUtils.isBlank(campaignId) ? null : Long.valueOf(campaignId));
                        }
                    }).start();

                }
                //Informing to Cloud Agent if the campaign is not in off Line Mode
                if (!c.isOffLineMode() && !type.equalsIgnoreCase("manual")) {
                    tokenResponse.setInteger("ACW", 30);
                    tokenResponse.setString("wrapUpTime", "" + c.getSla());
                    if (tokenServer.getConnector(a.getClientId()) != null) {
                        tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                        log.debug("[" + ucid + "] sent Agent[" + a.getAgentId() + "] to ACW");
                    } else {
                        log.debug("[" + ucid + "] Agent[" + a.getAgentId() + "] ACW Unable to sent to Client [" + a.getClientId() + "]");
                    }

                    String existingString = redisAgentManager.hget(c.getUser().getUsername() + ":agent:events", a.getAgentId());
                    Map releaseMap = new Gson().fromJson(existingString, LinkedHashMap.class);

                    for (Map.Entry entry : (Set<Map.Entry>) tokenResponse.getMap().entrySet()) {
                        releaseMap.put(entry.getKey(), entry.getValue());
                    }

                    releaseMap.put("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(eventTime));
                    redisAgentManager.hset(c.getUser().getUsername() + ":agent:events", a.getAgentId(), new Gson().toJson(releaseMap));
                }
                log.debug("Success :[" + ucid + "] Resp=" + resp.asString());
                return resp.e("status").t("1").asString();
            } else {
                String reason = "Unknown";
                try {
                    reason = (a == null ? "Agent is null" : a.toInfoString());
                } catch (Exception ignore) {
                }
                log.error("ReleaseError :[" + ucid + "] Resp=" + resp.asString() + " | Reason:" + reason);
                return resp.e("status").t("0").asString();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "ERROR";
        }
    }
}

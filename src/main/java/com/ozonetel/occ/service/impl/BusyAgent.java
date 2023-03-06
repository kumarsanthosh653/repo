package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.service.Command;
import javax.servlet.http.HttpServletRequest;
import com.jamesmurty.utils.XMLBuilder;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.PredictiveCallMonitor;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.UserIntegration;
import com.ozonetel.occ.util.DateUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ozonetel.occ.util.SecurityUtil;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.ScreenPopService;
import com.ozonetel.occ.service.UserIntegrationManager;
import com.ozonetel.occ.util.AppContext;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rajeshchary
 */
public class BusyAgent extends OCCManagerImpl implements Command {

    private String dataId;
    private String agentId;
    private String contact;
    private String channelName;
    private String callData;
    private String ucid;
    private String agentMonitorUcid;
    private String did;
    private String uui;
    private String skillName;
    private String type;
    private String transType;
    private int transferType = 0;
    private String campaignId;
    private Date aat;
    private static Logger logger = Logger.getLogger(BusyAgent.class);
    private HttpServletRequest request;
    private RedisAgentManager redisAgentManager;
    private final Date eventTime;
    private AppProperty appProperty;
    private UserIntegrationManager userIntegrationManager;
    private PredictiveServiceImpl predictiveService;
    private String audioFile = "";
    private ScreenPopService screenPopService;
    private PreviewDataManager previewDataManager;

    public BusyAgent(HttpServletRequest request, Date _eventTime) {
        this.eventTime = _eventTime;
        this.dataId = (String) request.getParameter("data_id");
        this.agentId = (String) request.getParameter("agentId");
        this.contact = (String) request.getParameter("callerId");
        this.channelName = (String) request.getParameter("channelName");
        this.callData = (String) request.getParameter("callData");
        this.ucid = (String) request.getParameter("ucid");
        this.agentMonitorUcid = (String) request.getParameter("agentMonitorUcid");
        this.did = (String) request.getParameter("did");
        this.uui = (String) request.getParameter("uui");//used for inbound to display in the screenpop
        this.skillName = (String) request.getParameter("skillName");//used for inbound to display in the screenpop
        this.type = (String) request.getParameter("type");//check whether the call is Manual Dial by fwp or inbound
        type = (type.isEmpty() ? "Inbound" : type);
        this.transType = (String) request.getParameter("transferType");
        transferType = (!transType.isEmpty() ? Integer.parseInt(transType) : transferType);
        String campaignParam = (String) request.getParameter("campaignId");
        this.campaignId = (campaignParam != null && !campaignParam.isEmpty()) ? campaignParam : null;
        initManagers();
        String aat = (String) request.getParameter("aat");
        String cat = (String) request.getParameter("cat");
        try {
            this.aat = aat != null ? DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", aat) : new Date();
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        this.audioFile = (String) request.getParameter("audioFile");
        this.request = request;
        initManagers();
    }

    private void initManagers() {
        tokenServer = (TokenServerLocalImpl) AppContext.getApplicationContext().getBean("tokenServer");
        redisAgentManager = (RedisAgentManager) AppContext.getApplicationContext().getBean("redisAgentManager");
        appProperty = (AppProperty) AppContext.getApplicationContext().getBean("appProperty");
        eventManager = (EventManager) AppContext.getApplicationContext().getBean("eventManager");
        userIntegrationManager = (UserIntegrationManager) AppContext.getApplicationContext().getBean("userIntegrationManager");
        predictiveService = (PredictiveServiceImpl) AppContext.getApplicationContext().getBean("predictiveServiceImpl");
        screenPopService = (ScreenPopService) AppContext.getApplicationContext().getBean("screenPopService");
        previewDataManager = (PreviewDataManager) AppContext.getApplicationContext().getBean("previewDataManager");
    }

    public BusyAgent(String dataId, String agentId, String contact, String channelName, String callData, String ucid, String agentMonitorUcid, String did, String uui, String skillName, String type, String transType, String campaignId, Date aat, Date eventTime) {
        this.dataId = dataId;
        this.agentId = agentId;
        this.contact = contact;
        this.channelName = channelName;
        this.callData = callData;
        this.ucid = ucid;
        this.agentMonitorUcid = agentMonitorUcid;
        this.did = did;
        this.uui = uui;
        this.skillName = skillName;
        this.type = type;
        this.transType = transType;
        this.campaignId = campaignId;
        this.aat = aat;
        this.eventTime = eventTime;
        initManagers();
    }

    public String execute() {
        return setAgentBusy(did, agentId, dataId, contact, channelName, callData, ucid, agentMonitorUcid, uui, skillName, type, transferType, campaignId, aat);
    }

    public String setAgentBusy(String did, String agentId, String dataId, String callerId, String channelName, String callData, String ucid, String agentMonitorUcid, String uui, String skillName, String type, int transferType, String campaignId, Date aat) {
        try {
            XMLBuilder resp = getXMLBuilder("busyAgent");
            Campaign c = null;
            if (campaignId != null && !campaignId.isEmpty()) {
                c = campaignManager.get(new Long(campaignId));
            } else if (type.equalsIgnoreCase("Manual") || type.equalsIgnoreCase("ToolBarManual")) {
                c = campaignManager.getCampaignsByDid(did);
            } else {
                c = campaignManager.getCampaignsByDid(did, type);
            }
            Agent a = null;

            FwpNumber fwpNumber = null;
            String username = (c != null) ? c.getUser().getUsername() : getUsernameForDid(did);
            Map<String, Object> reportParams = new HashMap<String, Object>();
            reportParams.put("ucid", new Long(ucid));
            reportParams.put("did", did);
            List<Report> reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);
            if (reports.size() > 0) {
                Report r = reports.get(0);
                log.debug("got report : " + r);
                if (r.getTimeToAnswer() == null && aat != null) {
                    r.setTimeToAnswer(aat);
                }
                
                if (aat!=null && !r.isCallCompleted()) { //when there is race condition update report might already have updated end time.
                    r.setEndTime(new Date(aat.getTime() + 1000));//add one second for end time bcz this is answered and may be short call.
                    log.debug(agentMonitorUcid+" Set end time in busy agent:"+r.getEndTime());
                }

                if (aat != null && type.equalsIgnoreCase("Predictive")) {
                    r.setAgentAnswerTime(aat);
                    log.debug("updating time to answer:" + aat);
                }
                r.setStatus("Success");//Asuming if got busy agent then the call is answered
                r.setCustomerStatus("answered");
                r.setAgentStatus("answered");
                r.setDialStatus("answered");
                r.setAudioFile(audioFile);
                reportManager.save(r);
                log.debug("updated report : " + r.toLongString());
                if (dataId == null && r.getData_id() != null) {
                    dataId = r.getData_id().toString();
                }
            }
            
            log.debug("campaign : "+c+" | dataId : "+dataId);
            if (c != null && c.getCampaignType() != null && c.getCampaignType().equalsIgnoreCase("Predictive")) {
                
                // this code has to be revived start
                if (dataId == null && agentMonitorUcid != null) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("monitorUcid", new Long(agentMonitorUcid));
                    params.put("did", did);
                    List<Report> reportList = reportManager.findByNamedQuery("getReportByMonitorUcid", params);
                    if (reportList != null) {
                        for (Report r : reportList) {
                            if (r.getData_id() != null) {
                                dataId = r.getData_id().toString();
                                break;
                            }
                        }
                    }
                }
                // end
                
                log.debug("dataId : "+dataId);
                if (dataId != null) {
                    PreviewData pData = previewDataManager.get(new Long(dataId));
                    log.debug("preview Data in busy agent : " + pData);
                    if (pData != null) {
                        pData.setStatus("Success");
                        pData.setDateUpdated(new Date());
                        previewDataManager.save(pData);
                        log.debug("after make data Busy in busy agent : "+pData);
                    }
                }
            }
            
            if (c != null && !c.isOffLineMode() && transferType != 3) {
                String cachedAgentUniqId = redisAgentManager.getString(username + ":agent:" + agentId);
                if (!StringUtils.isBlank(cachedAgentUniqId)) {
                    a = agentManager.get(Long.valueOf(cachedAgentUniqId));
                } else {
                    a = agentManager.getAgentByAgentIdV2(username, agentId);
                }

                logger.debug("[" + did + "][" + ucid + "] Making Agent[" + agentId + "] Busy ");
            } else {// for Hunting Mode
                logger.debug("[" + did + "][" + ucid + "] Making FWPNumber[" + agentId + "] Busy ");
                fwpNumber = fwpNumberManager.get(new Long(agentId));

                if (fwpNumber.getNextFlag() == 1) {//Assuming if fwpNumber is in Calling State
                    fwpNumberManager.makeFwpBusy(fwpNumber.getId(), callerId, true);

                } else {
                    logger.debug("⚉ Fwpnumber next flag is not 1:" + fwpNumber.getId() + " | " + fwpNumber.getPhoneName() + ", So not making busy.");
                }
                try {

                    if (c.getPopUrlAt() == Campaign.POPAT.PLUGIN) {
                        request.setAttribute("agentPhoneNumber", "" + fwpNumber.getPhoneNumber());
                        request.setAttribute("username", "" + username);
                        request.setAttribute("apiKey", "" + c.getUser().getApiKey());
                        UserIntegration userIntegration = userIntegrationManager.getUserIntegrationById(fwpNumber.getUserId(), Long.valueOf(c.getScreenPopUrl()));

                        //Command plugin = new PluginManager(userIntegration, appProperty.getPluginUrl(), c.getScreenPopUrl(), request);
                        Command integration = new PluginManager(userIntegration, redisAgentManager, appProperty.getPluginUrl(), agentMonitorUcid, ucid, type, fwpNumber.getPhoneNumber(),
                                did, callerId, "", "busyAgent", "Success", c.getUser().getApiKey(),
                                username, c.isOffLineMode() ? fwpNumber.getPhoneName() : agentId, Boolean.FALSE, Boolean.FALSE, aat, eventTime, uui, "answered", "answered", c.isOffLineMode(), "busyAgent");
                        integration.execute();
                    }

                    if (c.getPopUrlAt() == Campaign.POPAT.SERVERBUSY) {
                        log.debug("Hitting screenpop on Server busy");
                        screenPopService.hitScreenPopHere(c, a != null ? a : null, fwpNumber != null ? fwpNumber : (a != null ? a.getFwpNumber() : null), ucid, callerId, did, skillName, dataId, agentMonitorUcid, type, uui != null ? uui : "", agentId);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                resp.e("status").t("1");
                return resp.toString();
            }

            if (null != a && ((a.getState() == Agent.State.IDLE && a.getNextFlag() == 1) || c.isOffLineMode())) {
                if (a != null && (agentMonitorUcid != null) && !a.getUcid().equals(Long.valueOf(agentMonitorUcid))) { // check if agent's current MonitorUCID and busy event MonitorUCID are different.
                    log.debug("Wrong sequence{ Agent UCID = " + a.getUcid() + ", Got UCID:" + agentMonitorUcid + " }");
                    resp.e("status").t("Wrong sequence{ Agent UCID = " + a.getUcid() + ", Got UCID:" + ucid + " } ");
                    return resp.toString();
                }

                fwpNumber = a.getFwpNumber();
                if (fwpNumber != null) {
                    fwpNumberManager.makeFwpBusy(fwpNumber.getId(), callerId, false);
                }

                agentManager.makeAgentBusy(a.getId(), callerId, channelName, type, new Long(agentMonitorUcid));
                eventManager.logEvent("incall", a.getUserId(), c.getUser().getUsername(), a.getId(), a.getAgentId(), a.getMode(), eventTime, StringUtils.isNotEmpty(ucid) ? new Long(ucid) : null, channelName, null);

                try {
                    log.debug("Getting predicitive call monitor for : " + c.getCampaignId() + " : " + agentMonitorUcid);
                    PredictiveCallMonitor savedMonitor = predictiveService.getPredictiveCallMonitor(c.getCampaignId(), agentMonitorUcid);
                    if (savedMonitor != null) {
                        log.debug("Got predicitive call monitor : " + savedMonitor);
                        savedMonitor.setAgentStatus(Constants.DIAL_ANSWERED);
                        savedMonitor.setAgentSelected(a.getAgentId());
//                        savedMonitor.setFwpNumber(fwpNumber);
                        savedMonitor.setDateModified(new Date());
                        predictiveService.savePredictiveCallMonitorToRedis(c.getCampaignId(), agentMonitorUcid, savedMonitor);
                    }
                } catch (Exception e) {
                    log.error(e);
                }

                if (!c.isOffLineMode() && !type.equalsIgnoreCase("manual")) {
                    try {

                        Token tokenResponse = TokenFactory.createToken();
                        tokenResponse.setType("busyAgent");
                        log.debug("username came in busy agent encrypt : "+username);
                        boolean maskNumber = redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username);
                        log.debug("Mask number flag :"+maskNumber);
                        if(maskNumber){
                            tokenResponse.setString("callerId", SecurityUtil.encryptUsingAes256Key(callerId));
                            tokenResponse.setString("encryptField","true");
                        }else {
                            tokenResponse.setString("callerId", callerId);
                        }
                        tokenResponse.setString("callData", callData);
                        tokenResponse.setString("ucid", ucid);
                        if (type.equalsIgnoreCase("ToolBarManual")) {
                            tokenResponse.setString("calltype", "Manual");
                        } else {
                            tokenResponse.setString("calltype", type);
                        }

                        tokenResponse.setString("dataId", dataId);
                        tokenResponse.setString("did", did);
                        tokenResponse.setString("skillName", (!skillName.isEmpty() ? skillName : ""));
                        tokenResponse.setString("dispositionType", c.getDispositionType() != null ? c.getDispositionType() : "1");
                        tokenResponse.setString("audioFile", this.audioFile != null ? this.audioFile : "");

                        if (uui != null) {
                            tokenResponse.setString("uui", uui);
                        } else {
                            tokenResponse.setString("uui", "");
                        }

                        tokenResponse.setString("screenPop", c.getScreenPopUrl().toString());
                        if (c.getPopUrlAt() == Campaign.POPAT.PLUGIN) {
                            request.setAttribute("agentPhoneNumber", "" + a.getPhoneNumber());
                            request.setAttribute("username", "" + username);
                            request.setAttribute("apiKey", "" + c.getUser().getApiKey());
                            UserIntegration userIntegration = userIntegrationManager.getUserIntegrationById(a.getUserId(), Long.valueOf(c.getScreenPopUrl()));

                            //Command plugin = new PluginManager(userIntegration, appProperty.getPluginUrl(), c.getScreenPopUrl(), request);
//                            Command integration = new PluginManager(userIntegration, redisAgentManager, appProperty.getPluginUrl(), agentMonitorUcid, ucid, type, a != null ? a.getFwpNumber().getPhoneNumber() : "",
//                                    did, callerId, "", "busyAgent", "Success", c.getUser().getApiKey(),
//                                    username, agentId, Boolean.FALSE, Boolean.FALSE, aat, eventTime, uui, "answered", "answered");
//                            integration.execute();
                        }
                        if (tokenServer.getConnector(a.getClientId()) != null) {
                            tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                            logger.debug("[" + ucid + "][" + agentId + "] Token AgentBusy Sent to Client [" + a.getClientId() + "]");
                        } else {
                            logger.debug("[" + ucid + "][" + agentId + "] Agent Busy Unable to sent to Client [" + a.getClientId() + "]");
                        }

                        String existingString = redisAgentManager.hget(username + ":agent:events", a.getAgentId());
                        Map busyMap = new Gson().fromJson(existingString, LinkedHashMap.class);

                        Set<Map.Entry> entrySet = tokenResponse.getMap().entrySet();

                        for (Map.Entry entry : entrySet) {
                            busyMap.put(entry.getKey(), entry.getValue());
                        }

                        busyMap.put("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(eventTime));
                        redisAgentManager.hset(username + ":agent:events", a.getAgentId(), new Gson().toJson(busyMap));

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        resp.e("status").t("0");
                    }
                }

                try {

                    if (c.getPopUrlAt() == Campaign.POPAT.PLUGIN) {
                        request.setAttribute("agentPhoneNumber", "" + a.getPhoneNumber());
                        request.setAttribute("username", "" + username);
                        request.setAttribute("apiKey", "" + c.getUser().getApiKey());
                        UserIntegration userIntegration = userIntegrationManager.getUserIntegrationById(a.getUserId(), Long.valueOf(c.getScreenPopUrl()));

                        //Command plugin = new PluginManager(userIntegration, appProperty.getPluginUrl(), c.getScreenPopUrl(), request);
                        Command integration = new PluginManager(userIntegration, redisAgentManager, appProperty.getPluginUrl(), agentMonitorUcid, ucid, type, a != null ? a.getFwpNumber().getPhoneNumber() : "",
                                did, callerId, "", "busyAgent", "Success", c.getUser().getApiKey(),
                                username, c.isOffLineMode() ? fwpNumber.getPhoneName() : agentId, Boolean.FALSE, Boolean.FALSE, aat, eventTime, uui, "answered", "answered", c.isOffLineMode(), "busyAgent");
                        integration.execute();
                    }

                    if (c.getPopUrlAt() == Campaign.POPAT.SERVERBUSY) {
                        log.debug("Hitting screenpop on Server busy");
                        screenPopService.hitScreenPopHere(c, a, fwpNumber != null ? fwpNumber : (a != null ? a.getFwpNumber() : null), ucid, callerId, did, skillName, dataId, agentMonitorUcid, type, uui != null ? uui : "", a.getAgentId());
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                resp.e("status").t("1");
                logger.debug("Success:[" + ucid + "] Resp=" + resp.asString());
                return resp.asString();
            } else {
                resp.e("status").t("0");
                //null != a && ((a.getState() == Agent.State.IDLE) || c.isOffLineMode())
                try {
                    String reason = (null == a) ? "Agent is null " : ("Agent state :" + a.toInfoString() + "& Campaign offline ? :" + c.isOffLineMode() + " for agent :" + a);
                    logger.error("Error: [" + ucid + "] Resp=" + resp.asString() + " | Cause:" + reason);

                } catch (Exception ignore) {
                }
                return resp.asString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "Error";
        }
    }
}

package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.CallDrop;
import com.ozonetel.occ.model.CallEvent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.CustomerCallback;
import com.ozonetel.occ.model.DialOutNumber;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.model.UserIntegration;
import com.ozonetel.occ.service.CallbacksExecutorService;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.HoldDetailManager;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.PreviewExtraDataManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.RedisGeneralQueueManager;
import com.ozonetel.occ.service.ScreenPopService;
import com.ozonetel.occ.service.UserIntegrationManager;
import com.ozonetel.occ.service.chat.impl.ChatOnCallServiceImpl;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.agentManager;
import static com.ozonetel.occ.service.impl.OCCManagerImpl.userManager;

import com.ozonetel.occ.util.*;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author rajeshchary
 */
public class UpdateReport extends OCCManagerImpl implements Command {

    private String ucid;
    private String did;
    private String agentId;
    private String callerId;
    private String fallBackRule;
    private String callStatus;
    private String audioFile;
    private String skillName;
    private String hangUpBy;
    private String uui;
    private String agentMonitorUcid;
    private String isCompleted;
    private String type;
    private String transType;
    private int transferType = 0;
    private String dialStatus;
    private boolean callCompleted;
    private String dataId;// this is used for preview or progressive dataId to getFromJson the Data
    private String customerStatus;
    private String agentStatus;
    private String campaignParam;
    private Long priId;
    private Long directDialing;// is th call is direct(1) call or not(0)
    private String campaignId;
    private GenericManager<CustomerCallback, Long> customerCallbacksManager;
    private GenericManager<CallDrop, Long> callDropManager;
    private Date startTime = new Date();
    private Date endTime = new Date();
    private RedisAgentManager redisAgentManager;
    private RedisGeneralQueueManager redisGeneralQueueManager;
    private PreviewDialerManager previewDialerManager;
    private HoldDetailManager holdDetailManager;
    private EventManager eventManager;
    private Date aat = new Date(); //agent answered time
    private Date cat = new Date();
    private Date agentAnswerTime = null;
    private Date customerAnswerTime = null;
    private PreviewExtraDataManager previewExtraDataManager;
    private static Set<CallListener> callListeners = new HashSet();
    private UserIntegrationManager userIntegrationManager;
    private AppProperty appProperty;
    private ScreenPopService screenPopService;
    private ChatOnCallServiceImpl chatOnCallService;
    private String e164;

    public UpdateReport() {
        initManagers();
    }

    public UpdateReport(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public UpdateReport(HttpServletRequest request) {
        initManagers();
        this.ucid = (String) request.getParameter("ucid");
        this.did = (String) request.getParameter("did");
        this.agentId = (String) request.getParameter("agentId");
        this.callerId = (String) request.getParameter("callerId");
        this.fallBackRule = (String) request.getParameter("fallBackRule");
        this.callStatus = (String) request.getParameter("callStatus");
        callStatus = callStatus.isEmpty() ? "Fail" : callStatus;
        this.audioFile = (String) request.getParameter("audioFile");
        this.skillName = (String) request.getParameter("skillName");
        this.hangUpBy = (String) request.getParameter("hangUpBy");
        this.uui = (String) request.getParameter("uui");
        this.agentMonitorUcid = (String) request.getParameter("agentMonitorUcid");
        log.debug("Agent monitor ucid:" + agentMonitorUcid);
        this.isCompleted = (String) request.getParameter("isCompleted");//true-call of an anget is completed.
        this.type = (String) request.getParameter("type");//check whether the call is Manual Dial by fwp or inbound
        type = (type.isEmpty() ? "Inbound" : type);
        this.transType = (String) request.getParameter("transferType");

        transferType = (!transType.isEmpty() ? Integer.parseInt(transType) : transferType);
        this.dialStatus = (String) request.getParameter("dialStatus");//DND ,Busy or Any network error
        callCompleted = Boolean.parseBoolean(request.getParameter("callCompleted"));//Knows whether the Call Completed or not
        this.dataId = (String) request.getParameter("data_id");// this is used for preview or progressive dataId to getFromJson the Data
        this.customerStatus = (String) request.getParameter("customerStatus");
        this.agentStatus = (String) request.getParameter("agentStatus");
        this.campaignParam = (String) request.getParameter("campaignId");
        this.e164 = (String) request.getParameter("cid_e164");
        try { // getting null pointr exception here.
            this.priId = new Long(request.getParameter("priId"));
        } catch (Exception ignore) {
        }
        this.directDialing = request.getParameter("DD") != null ? new Long(request.getParameter("DD")) : 0;// is th call is direct(1) call or not(0)
        this.campaignId = (campaignParam != null && !campaignParam.isEmpty()) ? campaignParam : null;

        String sTime = (String) request.getParameter("stime");
        String eTime = (String) request.getParameter("etime");
        String aat = (String) request.getParameter("aat");
        String cat = (String) request.getParameter("cat");
        String _agentAnswerTime = (String) request.getParameter("agentAnsweredTime");
        String _customerAnswerTime = (String) request.getParameter("customerAnsweredTime");
        try {
            this.startTime = sTime != null ? DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", sTime) : null;
            this.endTime = eTime != null ? DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", eTime) : null;
            this.aat = aat != null ? DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", aat) : null;
            this.agentAnswerTime = _agentAnswerTime != null ? DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", _agentAnswerTime) : null;
            this.customerAnswerTime = _customerAnswerTime != null ? DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", _customerAnswerTime) : null;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initManagers() {
        super.initialize();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        this.customerCallbacksManager = (GenericManager<CustomerCallback, Long>) webApplicationContext.getBean("customerCallbacksManager");
        this.callbacksExecutorService = (CallbacksExecutorService) webApplicationContext.getBean("callbacksExecutorService");
        this.callDropManager = (GenericManager<CallDrop, Long>) webApplicationContext.getBean("callDropManager");
        redisAgentManager = (RedisAgentManager) webApplicationContext.getBean("redisAgentManager");
        redisGeneralQueueManager = (RedisGeneralQueueManager) webApplicationContext.getBean("redisGeneralQueueManager");
        previewDialerManager = (PreviewDialerManager) webApplicationContext.getBean("previewDialerManager");
        holdDetailManager = (HoldDetailManager) webApplicationContext.getBean("holdDetailManager");
        eventManager = (EventManager) webApplicationContext.getBean("eventManager");
        previewExtraDataManager = (PreviewExtraDataManager) webApplicationContext.getBean("previewExtraDataManager");
        appProperty = (AppProperty) webApplicationContext.getBean("appProperty");
        userIntegrationManager = (UserIntegrationManager) webApplicationContext.getBean("userIntegrationManager");
        screenPopService = (ScreenPopService) AppContext.getApplicationContext().getBean("screenPopService");
        chatOnCallService = (ChatOnCallServiceImpl) webApplicationContext.getBean("chatOnCallService");

    }

    public String execute() {
        updateCallStatus(ucid, agentMonitorUcid, did, agentId, callerId, callStatus, audioFile, skillName, hangUpBy, uui, isCompleted, fallBackRule, type, transferType, dialStatus, callCompleted, dataId, customerStatus, agentStatus, campaignId, priId, directDialing, startTime, endTime, aat, cat, e164);
        return "<response><status>1</status><message>Update success</message></response>";

    }

    public void updateCallStatus(String ucid,
            String agentMonitorUcid,
            String did, String agentId,
            String callerId,
            String callStatus,
            String audioFile,
            String skillName,
            String hangUpBy,
            String uui,
            String isCompleted,
            String fallBackRule,
            String type,
            int transferType,
            String dialStatus,
            boolean callCompleted,
            String dataId,
            String customerStatus,
            String agentStatus,
            String campaignId,
            Long priId,
            Long dd,
            Date startTime, Date endTime, Date aat, Date cat, String e164) {
        Map<String, Object> reportParams = new HashMap<String, Object>();
        reportParams.put("ucid", new Long(ucid));
        reportParams.put("did", did);
        List<Report> reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);
        Token tokenResponse = TokenFactory.createToken();
        Campaign c = null;
        if (campaignId != null && !campaignId.isEmpty()) {// if campaignId is passed
            c = campaignManager.get(new Long(campaignId));
        } else if (type.equalsIgnoreCase("manual") || type.equalsIgnoreCase("ToolBarManual")) {
            c = campaignManager.getCampaignsByDid(did);
        } else {
            c = campaignManager.getCampaignsByDid(did, type);
        }
        String username = (c != null) ? c.getUser().getUsername() : getUsernameForDid(did);
        Agent a = null;
        FwpNumber f = null;
        if ((c != null && c.isOffLineMode()) || transferType == 3) {
            f = fwpNumberManager.get(new Long(agentId));
        } else {
            String cachedAgentUniqId = redisAgentManager.getString(username + ":agent:" + agentId);
//            if (!StringUtils.isBlank(cachedAgentUniqId)) {
//                a = agentManager.get(Long.valueOf(cachedAgentUniqId));
//            } else {
//                a = agentManager.getAgentByAgentIdV2(username, agentId);
//            }
            a = agentManager.getAgentByAgentIdV2(username, agentId);
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                if (!StringUtils.equalsIgnoreCase(cachedAgentUniqId, a.getId().toString())) {
                    log.debug("*** wrong agent id from redis | got from redis - " + cachedAgentUniqId + " got from DB - " + a.getId());
                }
            } else {
                log.debug("**** null/empty agent id from redis");
            }
        }

        Calendar expireCalendar = StringUtils.isBlank(c.getUser().getUserTimezone())
                ? Calendar.getInstance() : Calendar.getInstance(TimeZone.getTimeZone(c.getUser().getUserTimezone()));
        expireCalendar.set(Calendar.HOUR_OF_DAY, 23);
        expireCalendar.set(Calendar.MINUTE, 59);
        expireCalendar.set(Calendar.SECOND, 59);
        long expireTime = expireCalendar.getTimeInMillis() / 1000;
        try {
            if (isCompleted.equalsIgnoreCase("true")) {// call for an agent is completed
                if (dd == 0) {
                    log.trace("[" + ucid + "]$$$$$Going to delete queue from redis:");
                    if (redisGeneralQueueManager == null) {
                        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
                        redisAgentManager = (RedisAgentManager) webApplicationContext.getBean("redisAgentManager");
                        redisGeneralQueueManager = (RedisGeneralQueueManager) webApplicationContext.getBean("redisGeneralQueueManager");
                    }
                    redisGeneralQueueManager.deleteFromQueue(did, username, agentMonitorUcid, callerId, skillName);
                    log.debug("[" + ucid + "]Deleted from redis call queue");
                    callQueueManager.deleteCallQueue(callerId, skillName, did, new Long(agentMonitorUcid));
                    log.debug("[" + ucid + "]Deleted from db call queue");
                } else {
                    callQueueManager.deleteAgentCallQueue(callerId, skillName, did, new Long(agentMonitorUcid));
                }

//                if (a != null) {
//                    redisAgentManager.hincrBy(username + ":agent:dailycallcount:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered:" : "unanswered:") + new SimpleDateFormat("YYYY_MM_dd").format(new Date()), a.getAgentId(), 1);
//                }
                holdDetailManager.updateEndtimeWhoseEndtimeIsNull(new BigInteger(agentMonitorUcid), endTime);
                if (a != null) {
                    agentManager.updateHoldStartTime(a.getId(), false);
                }
            }
            if (callCompleted) {
                log.debug("checking if agent is chatting with : " + callerId + " ucid : " + ucid);
                if (redisAgentManager.hexists("ca:sip-sessions", callerId)) {
                    log.debug("Agent was chatting with : " + callerId + " ucid : " + ucid);
                    chatOnCallService.endChatOnCall(callerId, hangUpBy);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (audioFile != null && !audioFile.isEmpty()) {
            String regString = "http://www.kookoo.in/recordings/";
            audioFile = audioFile.replaceAll(regString, "http://recordings.kookoo.in/").replaceAll(".wav", ".mp3");
        }

        Report r = new Report();
        log.debug("Checking reports...:" + reports);
        try {
            if (reports.size() > 0) {
                r = reports.get(0);

                boolean incrementRedisCounter = false;
                if (!endTime.equals(r.getEndTime())) {//callcomplete update hasn't happend already.(sometimes same update is coming twice)
                    incrementRedisCounter = true;
                    log.debug("--> Increment count:" + (endTime.equals(r.getEndTime())));
                }

                if (!StringUtils.equalsIgnoreCase(r.getStatus(), "success")) {// if success is setString by any other event like busy or release no need to setString again 
                    r.setStatus(callStatus);//success or fail
                    r.setCustomerStatus(customerStatus);

                    if (StringUtils.equalsIgnoreCase(type, "inbound") && StringUtils.equalsIgnoreCase(fallBackRule, "agentDial")) {
                        r.setAgentStatus(dialStatus);
                    } else {
                        r.setAgentStatus(agentStatus);
                    }
                    r.setDialStatus(dialStatus);
                } else {
                    r.setStatus("Success");//success or fail
                    r.setCustomerStatus("answered");
                    r.setAgentStatus("answered");
                    r.setDialStatus("answered");
                }
                r.setSkillName(skillName);
                r.setHangUpBy(hangUpBy);
                if (startTime != null) {
                    r.setCallDate(startTime);
                }
                if (endTime != null) {
                    r.setEndTime(endTime);
                }
                if (aat != null) {
                    r.setTimeToAnswer(aat);
                }
                if (e164 != null && !e164.trim().equals("")) {
                    r.setE164(e164);
                }
                if (customerAnswerTime != null) {
                    log.debug("updating customer answer time : " + customerAnswerTime);
                    r.setCustomerAnswerTime(customerAnswerTime);
                }
                if (agentAnswerTime != null) {
                    log.debug("updating agent answer time : " + agentAnswerTime);
                    r.setAgentAnswerTime(agentAnswerTime);
                }
                r.setAudioFile(audioFile);
                r.setTransferNow(false);
                r.setCallCompleted(callCompleted);

                r = reportManager.save(r);
                if (isCompleted.equalsIgnoreCase("true")) {
                    short nextFlag = 0;

                    if (agentId != null) {//for offline agentId is fwpId and online agentId is agentId
                        if (c.isOffLineMode()) {
                            if (f != null) {
                                f.setNextFlag(new Long(0));
                                f.setDirectCallCount((f.getDirectCallCount() == null || f.getDirectCallCount() < 0) ? 0L : (f.getDirectCallCount() - 1));
                                f.setCallStatus(dialStatus);
                                f.setContact(null);
                                f.setUcid(null);
                                if (!StringUtils.equalsIgnoreCase(callStatus, "Success")) {// if the consequetive calls got Exception then push to another state
                                    f.setCallExceptions(f.getCallExceptions() + 1);
                                    //int exceptionsAllowed = redisAgentManager.exists("exceptions:allowed") ? Integer.parseInt(redisAgentManager.getString("exceptions:allowed")) : 5;
                                    String allowedExceptionsPerUser = redisAgentManager.hget("exceptions:allowed-user", username);
                                    int exceptionsAllowed = allowedExceptionsPerUser == null ? 1000 : Integer.parseInt(allowedExceptionsPerUser);
                                    if (f.getCallExceptions() >= exceptionsAllowed) {
                                        f.setState(Agent.State.EXCEPTION);
                                    }
                                }
                                fwpNumberManager.save(f);
                            }
                        } else if (a != null) {//online mode
                            nextFlag = a.getNextFlag().shortValue();
                            if (userManager.hasRole(username, Constants.AGENT_CALL_HISTORY_ROLE)) {
                                reportManager.lpushToRedisList(username + ":agentcallhistory:" + agentId, Constants.NOOFRECORDS_FORAGENT, r);
                            }

                            if (aat != null && endTime != null && StringUtils.equalsIgnoreCase(callStatus, "Success")) {
                                redisAgentManager.zincby("analytics:agentranking:" + username, Double.valueOf(endTime.getTime() - aat.getTime()), agentId);
                                redisAgentManager.expireAt("analytics:agentranking:" + username, expireTime);
                            }
                            if (StringUtils.equalsIgnoreCase(type, "inbound") && callCompleted && !(StringUtils.equalsIgnoreCase(callStatus, "Success"))) {
                                //for failed call, counter might have already been incremented so no need to update again
                                incrementRedisCounter = false;
                                log.debug(ucid + " --> Increment count:" + incrementRedisCounter);
                            }
                            if (isCompleted.equalsIgnoreCase("true") && incrementRedisCounter) {// call for an agent is completed
                                //saving call count in user timezone
                                Calendar callCountExpire = Calendar.getInstance();
                                callCountExpire.add(Calendar.DATE, 2);
                                long callCountExpireTime = callCountExpire.getTimeInMillis() / 1000;
                                String dateString = DateUtil.convertDateToString(new Date(), "YYYY_MM_dd", r.getUser().getUserTimezone());
                                log.debug("Date string for :agent:dailycallcount: " + dateString);
//                                redisAgentManager.hincrBy(username + ":agent:dailycallcount:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered:" : "unanswered:") + new SimpleDateFormat("YYYY_MM_dd").format(new Date()), a.getAgentId(), 1);
                                redisAgentManager.hincrBy(username + ":agent:dailycallcount:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered:" : "unanswered:") + dateString, a.getAgentId(), 1);
                                redisAgentManager.expireAt(username + ":agent:dailycallcount:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered:" : "unanswered:") + dateString, callCountExpireTime);
                            }

                        }

                        if (c.getPopUrlAt() == Campaign.POPAT.PLUGIN) {
                            UserIntegration userIntegration = userIntegrationManager.getUserIntegrationById(c.getUser().getId(), Long.valueOf(c.getScreenPopUrl()));

                            Command plugin = new PluginManager(userIntegration, redisAgentManager, appProperty.getPluginUrl(), agentMonitorUcid, ucid, type, f != null ? f.getPhoneNumber() : (a != null ? a.getPhoneNumber() : ""), did,
                                    callerId, audioFile, "updateCallStatus", callStatus, c.getUser().getApiKey(),
                                    username, c.isOffLineMode() ? f.getPhoneName() : agentId, Boolean.valueOf(isCompleted), Boolean.valueOf(callCompleted), r.getTimeToAnswer(), endTime, uui, customerStatus, agentStatus, c.isOffLineMode(), fallBackRule);
                            plugin.execute();

                        }

                        if (a != null) {
                            redisAgentManager.hincrBy(username + ":dialstatus:" + new SimpleDateFormat("YYYY_MM_dd").format(new Date()), dialStatus, 1);
                            //FIXME below condition is wrong when wrapup time is set to -1(No disp). agent will go to IDLE instead of ACW so below will
                            // send dropCall even though agent is released.
                            //if ((a.getState() == Agent.State.IDLE || a.getState() == Agent.State.EXCEPTION) && a.getNextFlag().equals(1) && !c.isOffLineMode() && a.getUcid().equals(Long.valueOf(agentMonitorUcid))) { //EVENT MATCH
                            boolean dropcall = false;
                            if ((a.getState() == Agent.State.IDLE || a.getState() == Agent.State.EXCEPTION) && nextFlag == 1 && !c.isOffLineMode() && a.getUcid().equals(Long.valueOf(agentMonitorUcid))) { //EVENT MATCH
                                dropcall = true;
                                tokenResponse.setType("dropCall");
                                tokenResponse.setString("callStatus", callStatus);
                                tokenResponse.setString("agentStatus", agentStatus);
                                tokenResponse.setString("dialStatus", dialStatus);
                                tokenResponse.setString("agentMonitorUcid", agentMonitorUcid);
                                tokenResponse.setString("ucid", ucid);
                                tokenResponse.setString("callType", type);

                                eventManager.logEvent(a.getState() == Agent.State.EXCEPTION ? Agent.State.EXCEPTION.toString() : Agent.State.IDLE.name(), a.getUserId(), c.getUser().getUsername(), a.getId(), a.getAgentId(), a.getMode(), new Date(), Long.valueOf(ucid), a.getState() == Agent.State.EXCEPTION ? Agent.State.EXCEPTION.toString() : null, null);
                                redisAgentManager.hset(username + ":agent:events", a.getAgentId(), new Gson().toJson(tokenResponse.getMap()));

                                //--->If it's a progressive call: Answered-> release will take care,NotAnswered -> dialer will take care. If it's different type call dialer 
                                //has to be informed.
                                if (a.getMode() == Agent.Mode.BLENDED && !StringUtils.equalsIgnoreCase(type, "progressive")) {
                                    if (previewDialerManager == null) {
                                        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
                                        previewDialerManager = (PreviewDialerManager) webApplicationContext.getBean("previewDialerManager");
                                    }
                                    previewDialerManager.informDialer(username, a.getAgentId(), campaignId != null ? Long.valueOf(campaignId) : null);
                                }
                            }
                            boolean isException = false;
                            boolean lockAgentWithException = false;
                            int exceptionCount = 0;
                            if (!StringUtils.equalsIgnoreCase(callStatus, "Success")) {// if the consequetive calls got Exception then push to another state
                                isException = true;
                                exceptionCount = a.getCallExceptions().intValue() + 1;
                                String allowedExceptionsPerUser = redisAgentManager.hget("exceptions:allowed-user", username);
                                int exceptionsAllowed = allowedExceptionsPerUser == null ? 100 : Integer.parseInt(allowedExceptionsPerUser);
                                if (exceptionCount >= exceptionsAllowed) {
                                    lockAgentWithException = true;
//                                    a.setState(Agent.State.EXCEPTION);
//                                    a.setStateReason(dialStatus);
                                    tokenResponse.setBoolean("isException", true);

                                }
                            }
                            if (a.getClientId() != null && tokenServer.getConnector(a.getClientId()) != null) {
                                tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                                log.debug("[" + ucid + "] Drop Call[" + callerId + "] Sending to ScreenPop :[" + agentId + "]");
                            } else {
                                log.debug("[" + ucid + "] Drop Call[" + callerId + "] Unable to sent to Client [" + agentId + "]");
                            }
//                            if (a.getUcid() != null && a.getUcid().equals(Long.valueOf(agentMonitorUcid))) {//EVENT MATCH
//                                agentManager.releaseAgentLockWithDialStatus(c.getUser().getUsername(), a.getId(), dialStatus, StringUtils.equalsIgnoreCase(type, "Progressive"), isException, exceptionCount, lockAgentWithException, dropcall);
//                            }
                            agentManager.releaseAgentLockFromUpdateCallStatus(c.getUser().getUsername(), a.getId(), dialStatus, StringUtils.equalsIgnoreCase(type, "Progressive"), isException, exceptionCount, lockAgentWithException, dropcall, Long.valueOf(agentMonitorUcid));

                            if (callCompleted) {
                                tokenResponse = TokenFactory.createToken();
                                tokenResponse.setType("callDetails");
                                tokenResponse.setString("endTime", endTime.getTime() + "");
                                tokenResponse.setString("startTime", startTime.getTime() + "");
                                tokenResponse.setString("agentAnswerTime", agentAnswerTime != null ? agentAnswerTime.getTime() + "" : "");
                                if (a.getClientId() != null && tokenServer.getConnector(a.getClientId()) != null) {
                                    tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                                    log.debug("[" + ucid + "] call details to toolbar :[" + agentId + "]");
                                }
                            }

                        }
                    }

                    if (callCompleted && r.getStatus().equalsIgnoreCase("fail")) {//check for dropActions
                        if (r.getSkillId() != null) {
                            Skill s = skillManager.get(r.getSkillId());
                            log.info("[" + ucid + "]Checking callDrop options=" + s);
                            if (s != null && s.isDropAction()) {
                                takeCallDropAction(s.getId(), callerId, did, skillName, c.getUser(), agentId, fallBackRule, uui, type, agentMonitorUcid, dialStatus);
                            }
                        }
                    }
                    if (type.equalsIgnoreCase("Preview") && callCompleted && StringUtils.isNotBlank(dataId)) {
                        log.debug("deleting the mapping between agent and data id : " + dataId + " -> " + redisAgentManager.hdel(Constants.PREVIEWDATA_AGENTID_MAP, dataId));
                    }

                }

            } else { //------ > Call started now.
                Skill skillObj = skillManager.getSkillsByUserAndSkillName(skillName, username);
                r.setUcid(new Long(ucid));
                r.setData_id(StringUtils.isNotBlank(dataId) ? Long.valueOf(dataId) : null);
                r.setMonitorUcid(new Long(agentMonitorUcid));
                r.setUui(!(uui != null && uui.isEmpty()) ? uui : "");
                r.setTransferNow(false);
                r.setDialStatus(dialStatus);
                r.setCallCompleted(callCompleted);
                r.setCustomerStatus(customerStatus);
                r.setAgentStatus(agentStatus);
                r.setPriId(priId);
                if (e164 != null && !e164.trim().equals("")) {
                    r.setE164(e164);
                }
                if (skillObj != null) {
                    r.setSkillId(skillObj.getId());
                } else {
                    r.setSkillId(null);
                }
                if (agentId != null) {

                    if (a != null) {
                        // adding transfer type check to log event for agent transfer
//                        if (!StringUtils.equalsIgnoreCase(type, "inbound") || transferType == 1) {
                        log.debug("*Updagint Calling event for :'" + a.getAgentId() + "' ucid : " + ucid);
                        eventManager.logEvent(Constants.CALLING, a.getUserId(), c.getUser().getUsername(), a.getId(), a.getAgentId(), a.getMode(), new Date(), Long.valueOf(ucid), null, null);
//                        }
//                        a.setContact(callerId);
//                        a.setType(type);
//                        a.setCallStatus(dialStatus);
//                        a.setNextFlag(new Long(1));//for manual need to update
//                        a.setSkillName(skillName);
//                        a.setCampignName(c.getCampignName());
//                        a.setUcid(Long.valueOf(agentMonitorUcid));//TODO check this. We were storing ucid previously.
                        r.setFwpNumber(a.getFwpNumber());
                        boolean updateTimeStamp = false;//for progressive timestamp is updated from dialer (lock agent)
                        if (type.equalsIgnoreCase("toolbarmanual") || type.equalsIgnoreCase("Preview") || (c.getUser().isRedis() && type.equalsIgnoreCase("inbound"))) {
                            updateTimeStamp = true;
//                            a.setIdleSince(0);
//                            a.setLastSelected(System.currentTimeMillis());
//                            a.setIdleTime(System.currentTimeMillis());
                        }
                        if (a.getFwpNumber() != null) {
                            log.debug("Saving ucid for fwp.." + agentMonitorUcid);
                            fwpNumberManager.setUcidForFwp(Long.valueOf(agentMonitorUcid), a.getFwpNumber().getId());

                        }
                        //a = agentManager.save(a);
                        agentManager.agentCallStarted(a.getId(), callerId, type, dialStatus, skillName, c.getCampignName(), Long.valueOf(agentMonitorUcid), updateTimeStamp);
                        log.debug("Agent saved :" + a);
                        r.setAgent(a);

                    }
                }

                r.setAgentId(agentId);
                if (f != null) {
                    if (f.getAgent() != null) {// if the agent done the Manual Dialing from physical Phone and if he also logged in with phone Number
                        //REVISIT : below code need to be assigne agentId instead of object
                        r.setAgent(agentManager.get(f.getAgent()));
                    }
                    r.setAgentId(f.getPhoneNumber());
                    r.setFwpNumber(f);

                    f.setUcid(Long.valueOf(agentMonitorUcid));
                    f.setLastSelected(System.currentTimeMillis());
                    fwpNumberManager.save(f);

                }

                if (fallBackRule.equalsIgnoreCase("DialOut")) {
                    if (agentId != null) {
                        DialOutNumber don = dialOutNumberManager.getDialOutNumberByUserAndDon(agentId, username);
                        r.setDialOutNumberId(don.getId());
                    }
                }

                log.debug("---->Chccking screenop url: for campaign:" + c + " screen pop at:" + c.getPopUrlAt());
                if (c.getPopUrlAt() == Campaign.POPAT.SERVER) {
                    screenPopService.hitScreenPopHere(c, a, f != null ? f : (a != null ? a.getFwpNumber() : null), ucid, callerId, did, skillName, dataId, agentMonitorUcid, type, uui, agentId);
                } else if (c.getPopUrlAt() == Campaign.POPAT.BOTH) {
                    screenPopService.hitScreenPopHere(c, a, f != null ? f : (a != null ? a.getFwpNumber() : null), ucid, callerId, did, skillName, dataId, agentMonitorUcid, type, uui, agentId);
                    //tokenResponse.setString("screenPopMode", c.getUser().getScreenPopMode().toString());
                } else if (c.getPopUrlAt() == Campaign.POPAT.PLUGIN) {
                    UserIntegration userIntegration = userIntegrationManager.getUserIntegrationById(c.getUser().getId(), Long.valueOf(c.getScreenPopUrl()));
                    //UserIntegration userIntegration = userIntegrationManager.get(Long.valueOf(c.getScreenPopUrl()));

                    log.debug("Going to hit plugin :" + userIntegration + " for camp:" + c);
                    Command plugin = new PluginManager(userIntegration, redisAgentManager, appProperty.getPluginUrl(), agentMonitorUcid, ucid, type, f != null ? f.getPhoneNumber() : (a != null ? a.getPhoneNumber() : ""), did,
                            callerId, audioFile, "updateCallStatus", callStatus, c.getUser().getApiKey(),
                            username, c.isOffLineMode() ? f.getPhoneName() : agentId, Boolean.valueOf(isCompleted), Boolean.valueOf(callCompleted), r.getTimeToAnswer(), endTime, uui, customerStatus, agentStatus, c.isOffLineMode(), fallBackRule);

                    plugin.execute();
                }

                if (fallBackRule.equalsIgnoreCase("AgentDial") && !c.isOffLineMode() && !type.equalsIgnoreCase("manual") && transferType != 3) {
                    if (type.equalsIgnoreCase("Preview")) {
                        tokenResponse.setString("callType", "PreviewDialing");
                    } else if (type.equalsIgnoreCase("ToolBarmanual")) {
                        tokenResponse.setString("callType", "Manual Dialing");
                    } else if (type.equalsIgnoreCase("progressive")) {
                        tokenResponse.setString("callType", "Progressive Dialing");
                    } else if (type.equalsIgnoreCase("predictive")) {
                        tokenResponse.setString("callType", "Predictive Call");
                    } else {
                        tokenResponse.setString("callType", "IncomingCall");
                    }
                    tokenResponse.setType("newCall");
                    log.debug("username came in update report encrypt : "+username);
                    boolean maskNumber = redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username);
                    log.debug("Mask number flag :"+maskNumber);
                    if(maskNumber){
                    tokenResponse.setString("callerId", SecurityUtil.encryptUsingAes256Key(callerId));
                    tokenResponse.setString("encryptField","true");
                    }else {
                        tokenResponse.setString("callerId", callerId);
                    }
                    tokenResponse.setString("callData", type);//passing actual call type
                    tokenResponse.setString("ucid", ucid);
                    tokenResponse.setString("monitorUcid", agentMonitorUcid);
                    tokenResponse.setString("dataId", dataId);
                    tokenResponse.setString("did", did);
                    tokenResponse.setString("phoneName", a.getFwpNumber().getPhoneName());
                    tokenResponse.setString("dispositionType", c.getDispositionType() != null ? c.getDispositionType() : "1");
                    tokenResponse.setString("agentUniqueID", "" + a.getId());
                    tokenResponse.setString("campaignId", c != null ? c.getCampaignId().toString() : "");
                    tokenResponse.setString("campaignName", c != null ? c.getCampignName() : "");
                    tokenResponse.setString("wrapUpTime", "" + c.getSla());
                    tokenResponse.setString("skillName", (!skillName.isEmpty() ? skillName : ""));
                    tokenResponse.setString("uui", StringUtils.trimToEmpty(uui));
                    tokenResponse.setString("campaignScript", c.getScript() != null ? StringUtils.abbreviate(c.getScript(), 200) : "");
                    tokenResponse.setString("screenPop", c.getScreenPopUrl());
                    tokenResponse.setString("screenPopOn", c.getPopUrlAt().toString());
                    log.debug("PopURL AT=" + c.getPopUrlAt() + "==" + Campaign.POPAT.PLUGIN + " for agent:" + a);

                    if (c.getPopUrlAt() == Campaign.POPAT.CLIENT || c.getPopUrlAt() == Campaign.POPAT.CLIENTBUSY || c.getPopUrlAt() == Campaign.POPAT.BOTH) {
                        tokenResponse.setString("screenPopMode", c.getUser().getScreenPopMode().toString());
                        if (StringUtils.isNotBlank(dataId)) {
                            Map<String, Object> params = new LinkedHashMap();
                            params.put("user_id", c.getUser().getId());
                            params.put("param_code", "CUST_INFO_SCREENPOP");
                            List settings = campaignManager.executeProcedure("call Get_UserParamter(?,?)", params);
                            if (settings != null && settings.size() >= 1) {
                                Map<String, Object> item = (Map<String, Object>) settings.get(0);
                                boolean custInfo = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                        ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                                if (custInfo) {
                                    StringBuilder queryString = new StringBuilder();
                                    Map<String, String> data = previewExtraDataManager.getCustomerData(Long.valueOf(dataId), c.getCampaignId());
                                    for (Map.Entry<String, String> entry : data.entrySet()) {
                                        queryString.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                                    }
                                    tokenResponse.setString("extraData", queryString.toString());
                                }
                            }

                        }

                    }
                    if (startTime != null) {
                        tokenResponse.setString("callStartTime", startTime.toString());
                    }
                    if (a.getClientId() != null && tokenServer.getConnector(a.getClientId()) != null) {
                        tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                        log.debug("[" + ucid + "] New Call[" + callerId + "] Sending  ScreenPop :[" + agentId + "]");
                    } else {
                        log.debug("[" + ucid + "] New Call[" + callerId + "] Unable to sent to Client [" + agentId + "]");
                    }

                    tokenResponse.setString("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(new Date()));
                    redisAgentManager.hset(username + ":agent:events", a.getAgentId(), new Gson().toJson(tokenResponse.getMap()));

                }
                r.setSkillName(skillName);
                if (type.equalsIgnoreCase("ToolBarmanual")) {
                    r.setType("Manual");
                } else {
                    r.setType(type);
                }
                // Updating hangupby if call timed-out in queue or customer hangup in queue
                if (callCompleted && callStatus.equalsIgnoreCase("fail")) {
                    log.debug("setting hangup : " + hangUpBy + " : " + hangUpBy.equalsIgnoreCase("AgentHangup"));
                    r.setHangUpBy(hangUpBy.equalsIgnoreCase("AgentHangup") ? "SystemHangup" : hangUpBy);
                }
                if (startTime != null) {
                    r.setCallDate(startTime);
                }
                r.setCall_data(fallBackRule);
                r.setStatus(callStatus);
                if (endTime != null) {
                    r.setEndTime(endTime);
                }
                r.setTriedNumber(1);
                r.setCampaignId(c != null ? c.getCampaignId() : null);
                if (c != null) {
                    r.setUser(c.getUser());
                    r.setOffline(c.isOffLineMode());
                }
                r.setDest(callerId);
                r.setDid(did);
                if (StringUtils.isEmpty(r.getAudioFile()) || !r.getAudioFile().contains("http")) {
                    r.setAudioFile(audioFile);
                }
                r.setTransferType(new Long(0));
                r = reportManager.save(r);

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        Integer callBackTries = 0;
        if (callCompleted && r.getUser() != null && (StringUtils.isNotEmpty(r.getUser().getCallBackUrl()) || StringUtils.isNotBlank(c.getCallbackUrl()))) {// Send the Data to the customer CRM after the call has completed

            try {// --> Retrying allowed no.of tries for the customer callbacks.
                Map<String, Object> params = new LinkedHashMap();
                params.put("user_id", r.getUser().getId());
                params.put("param_name", "CALLBACK_TRIES");
                List settings = campaignManager.executeProcedure("call Get_UserParamter(?,?)", params);
                if (settings != null && !settings.isEmpty()) {
                    Map item = (Map) settings.get(0);
                    callBackTries = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                            ? Integer.valueOf(item.get("DefaultValue").toString()) : Integer.valueOf(item.get("ParameterValue").toString());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            if (customerCallbacksManager == null) {
                ApplicationContext webApplicationContext = AppContext.getApplicationContext();
                customerCallbacksManager = (GenericManager<CustomerCallback, Long>) webApplicationContext.getBean("customerCallbacksManager");
            }
            CustomerCallback cb = new CustomerCallback(new BigInteger("" + r.getMonitorUcid()), username, new Date(), r.getUser().getCallBackUrl(), callBackTries);
            customerCallbacksManager.save(cb);
        }

        try {

            if (callCompleted) {
                try {
                    fireCallCompleted(new CallEvent(agentMonitorUcid, ucid, r.getUser(), reports, c, callStatus, callBackTries, agentId));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(r != null ? r.getCallDate() : (startTime != null ? startTime : new Date()));
                String serverDate = DateUtil.convertDateToString(r != null ? r.getCallDate() : (startTime != null ? startTime : new Date()), DateUtil.ZODA_FORMAT, r.getUser().getServerTimezone());
                String hourString = DateUtil.zodaConvertDateStringFromOneZoneToOtherZoneString(serverDate, r.getUser().getServerTimezone(), r.getUser().getUserTimezone(), "HH");
                int hour = Integer.parseInt(hourString);
                log.debug("Saving hourly call count for " + hour + " hour");
//                int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                Map<String, Object> params = new HashMap<String, Object>();
//                params.put("monitorUcid", new Long(agentMonitorUcid));
//                params.put("did", did);
                List<Report> tmpReports = reportManager.getReportByMonitorUcid(Long.valueOf(agentMonitorUcid));
                if (tmpReports != null & !tmpReports.isEmpty()) {
                    Map<String, String> agentStatusMap = new LinkedHashMap<>();
                    for (Report tmpReport : tmpReports) {
                        if (StringUtils.isNotBlank(tmpReport.getAgentId()) && !StringUtils.equalsIgnoreCase(tmpReport.getAgentId(), "0")) {
                            agentStatusMap.put(tmpReport.getAgentId(), StringUtils.equalsIgnoreCase(tmpReport.getStatus(), "success") ? "answered" : "unanswered");
                        }
                    }

                    for (Map.Entry<String, String> entry : agentStatusMap.entrySet()) {
                        String callCount = redisAgentManager.getrange("analytics:callsperhour:" + entry.getValue() + ":" + username + ":" + entry.getKey(), hour * 4, hour * 4 + 3);
                        Integer countValue = 0;
                        if (StringUtils.isBlank(callCount)) {//means key deosn't exist.
                            redisAgentManager.setString("analytics:callsperhour:answered" + ":" + username + ":" + entry.getKey(), "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                            redisAgentManager.setString("analytics:callsperhour:unanswered" + ":" + username + ":" + entry.getKey(), "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                            redisAgentManager.expireAt("analytics:callsperhour:answered" + ":" + username + ":" + entry.getKey(), expireTime);
                            redisAgentManager.expireAt("analytics:callsperhour:unanswered" + ":" + username + ":" + entry.getKey(), expireTime);
                        } else {
                            countValue = Integer.valueOf(callCount);
                        }
                        ++countValue;
                        redisAgentManager.setrange("analytics:callsperhour:" + entry.getValue() + ":" + username + ":" + entry.getKey(), hour * 4, String.format("%1$04d", countValue));

                    }
                }//---> end of agent hourly report

//                reportManager.lpushToRedisList(username + ":callerhistory:" + PhoneNumberUtil.getNationalNumber(callerId), transferType, r);
                log.debug("Exists:" + redisAgentManager.exists("analytics:callsperhour:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered" : "unanswered") + ":" + username));
                if (!redisAgentManager.exists("analytics:callsperhour:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered" : "unanswered") + ":" + username)) {
                    redisAgentManager.setString("analytics:callsperhour:answered" + ":" + username, "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    redisAgentManager.setString("analytics:callsperhour:unanswered" + ":" + username, "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    redisAgentManager.expireAt("analytics:callsperhour:answered" + ":" + username, expireTime);
                    redisAgentManager.expireAt("analytics:callsperhour:unanswered" + ":" + username, expireTime);
                }

                Integer countValue = Integer.valueOf(redisAgentManager.getrange("analytics:callsperhour:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered" : "unanswered") + ":" + username, hour * 4, hour * 4 + 3));
                ++countValue;
                redisAgentManager.setrange("analytics:callsperhour:" + (StringUtils.equalsIgnoreCase(callStatus, "Success") ? "answered" : "unanswered") + ":" + username, hour * 4, String.format("%1$04d", countValue));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void hitScreenPopHere(Campaign c, Agent a, FwpNumber f, String ucid, String callerId, String did, String skillName, String dataId, String agentMonitorUcid, String type, String uui, String agentId) {
        log.debug("------>Called hit screen pop:");
        HttpUtils httpUtils = new HttpUtils();
        try {
            if (c.getScreenPopUrl() != null && !c.getScreenPopUrl().isEmpty()) {
                //Check if the UrlContains the ? and =
                String s = c.getScreenPopUrl();
                String url = s.contains("?") ? s.substring(0, s.lastIndexOf("?")) : s;
                String cId = s.contains("?") ? s.substring(s.lastIndexOf("?") + 1) : s;
                String callerIdParam = cId.contains("=") ? cId.substring(0, cId.lastIndexOf("=")) : cId;

                StringBuilder queryString = new StringBuilder();
                queryString.append("ucid=").append(ucid);
                queryString.append("&callerID=").append(callerId);
                queryString.append("&did=").append(did);
                queryString.append("&skillName=").append(skillName);
                queryString.append("&agentUniqueID=").append(a != null ? a.getId() : null);
                queryString.append("&dataID=").append(dataId);
                queryString.append("&campaignID=").append(c.getCampaignId());
                queryString.append("&monitorUcid=").append(agentMonitorUcid);
                queryString.append("&phoneName=").append(f != null ? f.getPhoneName() : "");
                queryString.append("&").append("agentPhoneNumber").append("=").append(a != null ? a.getPhoneNumber() : (f != null ? f.getPhoneNumber() : ""));
                if (type.equalsIgnoreCase("ToolBarManual")) {
                    queryString.append("&type=").append("Manual");
                } else {
                    queryString.append("&type=").append(type);
                }
                queryString.append("&uui=").append(URLEncoder.encode(uui));

                if (!callerIdParam.isEmpty()) {
                    queryString.append("&").append(callerIdParam).append("=").append(callerId);
                }
                if (a != null && a.getAgentData() != null && !a.getAgentData().isEmpty()) {//Apend agentDetails configured in AgentData
                    queryString.append("&").append(a.getAgentData());
                }
                User u = c.getUser();
                //Send Customer Defined Params also
                boolean customerInfo = false;
                if (u != null) {
                    Map<String, Object> params = new LinkedHashMap<String, Object>();
                    params.put("user_id", u.getId());
                    List userParameters = campaignManager.executeProcedure("call Get_UserParamters(?)", params);
                    String custPH = "", agentIdPH = "", agentKeyPH = "", agentPhonePH = "", sendAgentParms = "false";
                    for (Object object : userParameters) {
                        Map<String, String> mp = (Map) object;
                        if (mp.get("ParameterValue") != null) {
                            if (mp.get("ParameterCode").equalsIgnoreCase("CUST_PH")) {
                                custPH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("AGENT_ID_PH")) {
                                agentIdPH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("AGENT_KEY_PH")) {
                                agentKeyPH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("AGENT_PHONE_PH")) {
                                agentPhonePH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("SEND_CUST_PARAMS")) {
                                sendAgentParms = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("CUST_INFO_SCREENPOP")) {
                                customerInfo = Boolean.valueOf(mp.get("ParameterValue"));
                            }
                        }
                    }

                    try {
                        if (customerInfo && StringUtils.isNotBlank(dataId)) {
                            Map<String, String> data = previewExtraDataManager.getCustomerData(Long.valueOf(dataId), c.getCampaignId());
                            for (Map.Entry<String, String> entry : data.entrySet()) {
                                queryString.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    if (sendAgentParms.equalsIgnoreCase("true")) {
                        log.debug("Sending Customer defined Params also");
                        queryString.append("&").append(custPH).append("=").append(u.getUsername());
                        queryString.append("&").append(agentIdPH).append("=").append(agentId);
                        queryString.append("&").append(agentKeyPH).append("=").append(a != null ? a.getPassword() : "");
                        queryString.append("&").append(agentPhonePH).append("=").append(a != null ? a.getPhoneNumber() : (f != null ? f.getPhoneNumber() : ""));
                    } else {
                        queryString.append("&agentID=").append(agentId);
                        queryString.append("&customer=").append(c.getUser().getUsername());
                    }
                }

                try {
                    log.debug("REQUEST=" + url + "?" + queryString.toString());
                    httpUtils.doGetRequestAsThread(url, queryString.toString());
                    // Below comment code is writter BasicAuthentication  -- Need to revisit here         
                    /* queryString.append("&popUrl="+URLEncoder.encode(url));
                     ScreenPopUtil.doGetRequestAsThread(url, queryString.toString());*/
//                    ScreenPopUtil.doBasicAuthRequest(url, queryString.toString());

                } catch (Exception e) {
                    log.error("[" + ucid + "]Error sending Server side=" + e.getMessage(), e);
                    //e.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void addCallListener(final CallListener callListener) {
        callListeners.add(callListener);
    }

    public void removeCallListener(final CallListener callListener) {
        callListeners.remove(callListener);
    }

    protected void fireCallCompleted(CallEvent callEvent) {
        for (CallListener callListener : callListeners) {
            callListener.callCompleted(callEvent);
        }
    }

    protected void fireCallStarted(CallEvent callEvent) {
        for (CallListener callListener : callListeners) {
            callListener.callStarted(callEvent);
        }
    }

    public void setCallListeners(Set<CallListener> callListeners) {
        UpdateReport.callListeners = callListeners;
    }

    public void setCustomerCallbacksManager(GenericManager<CustomerCallback, Long> customerCallbacksManager) {
        this.customerCallbacksManager = customerCallbacksManager;
    }

    public void setCallDropManager(GenericManager<CallDrop, Long> callDropManager) {
        this.callDropManager = callDropManager;
    }

    public void setCallbacksExecutorService(CallbacksExecutorService callbacksExecutorService) {
        this.callbacksExecutorService = callbacksExecutorService;

    }
    private CallbacksExecutorService callbacksExecutorService;

    public void takeCallDropAction(Long skillId,
            String callerId,
            String did, String skillName,
            User user, String agentId, String fallBackRule, String uui, String type,
            String monitorUcid, String dialStatus) {
        Map<String, Object> callDropParams = new HashMap<String, Object>();
        callDropParams.put("skillId", skillId);
        List<CallDrop> callDrops = callDropManager.findByNamedQuery("getDropActions", callDropParams);
        log.info("Call Drops=" + callDrops.size());
        log.debug("Call Drops=" + callDrops.size());
        HttpUtils httpUtils = new HttpUtils();
        //  CallerID,Did,Skill,Username,UUI,AgentID,FallBackRule,Tries,DialStatus
        StringBuilder queryString = new StringBuilder();
        queryString.append("monitorUcid=").append(monitorUcid);
        queryString.append("&CallerID=").append(callerId);
        queryString.append("&Did=").append(did);
        queryString.append("&Skill=").append(skillName);
        if (user != null) {
            queryString.append("&Username=").append(user.getUsername());
//            queryString.append("&ApiKey=").append(user.getApiKey());
            queryString.append("&ApiKey=").append(user.getCallapiKey());
        }
        queryString.append("&AgentID=").append(agentId);
        queryString.append("&FallBackRule=").append(fallBackRule);
        queryString.append("&UUI=").append(URLEncoder.encode(uui));
        if (type.equalsIgnoreCase("ToolBarManual")) {
            queryString.append("&type=").append("Manual");
        } else {
            queryString.append("&type=").append(type);
        }

        queryString.append("&dialStatus=").append(dialStatus);
        queryString.append("&Apikey=").append(dialStatus);

        for (CallDrop callDrop : callDrops) {
            if (callDrop.getActionType() == CallDrop.Action.CALLBACK) {
                queryString.append("&Tries=").append(callDrop.getActionValue());
            }
            try {
                httpUtils.sendHttpRequestAsThread(callDrop.getActionURL(), queryString.toString(), callDrop.getHttpMethod().toString());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error occured sending HttpRequest for User:" + user.getUsername() + " " + e.getMessage());
            }

        }

    }
}

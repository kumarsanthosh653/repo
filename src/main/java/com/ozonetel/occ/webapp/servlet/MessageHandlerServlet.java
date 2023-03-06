package com.ozonetel.occ.webapp.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.CallbacksGrouped;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.CampaignConfig;
import com.ozonetel.occ.model.CampaignHoldMusic;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.FakeServerEvent;
import com.ozonetel.occ.model.FakeWebsocketResponse;
import com.ozonetel.occ.model.IvrFlow;
import com.ozonetel.occ.model.JSONTicketDetails;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.model.PreviewDataAuditLog;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.model.WrappedRequestToken;
import com.ozonetel.occ.service.AgentCommandExecutor;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentToolbarCommand;
import com.ozonetel.occ.service.AgentTransferManager;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.service.CampaignConfigManager;
import com.ozonetel.occ.service.CampaignHoldMusicManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.ChatStates;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.ManualDialService;
import com.ozonetel.occ.service.OCCManager;
import com.ozonetel.occ.service.PhoneTransferManager;
import com.ozonetel.occ.service.PreviewDataAuditLogManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.SkillTransferManager;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.ToolBarManager;
import com.ozonetel.occ.service.TransferNumberManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.chat.impl.ChatBotServiceImpl;
import com.ozonetel.occ.service.chat.impl.ChatOffCommand;
import com.ozonetel.occ.service.chat.impl.ChatOnCallServiceImpl;
import com.ozonetel.occ.service.chat.impl.ChatOnCommand;
import com.ozonetel.occ.service.chat.impl.ChatServiceImpl;
import com.ozonetel.occ.service.chat.impl.ChatTransferServiceImpl;
import com.ozonetel.occ.service.chat.impl.FacebookChatServiceImpl;
import com.ozonetel.occ.service.command.AgentBridgeCommand;
import com.ozonetel.occ.service.command.AgentConferenceCommand;
import com.ozonetel.occ.service.command.BlindAgentTransferCommand;
import com.ozonetel.occ.service.command.BlindPhoneTransferCommand;
import com.ozonetel.occ.service.command.ChatAgentTransferCommand;
import com.ozonetel.occ.service.command.ChatSkillTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeAgentTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldAgentTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldPhoneTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldSkillTransferCommand;
import com.ozonetel.occ.service.command.ConsultativePhoneTransferCommand;
import com.ozonetel.occ.service.command.DropMeCommand;
import com.ozonetel.occ.service.command.GetTransferAgentListCommand;
import com.ozonetel.occ.service.command.GetTransferNumberListCommand;
import com.ozonetel.occ.service.command.HoldCommand;
import com.ozonetel.occ.service.command.IvrTransferCommand;
import com.ozonetel.occ.service.command.KickCallCommand;
import com.ozonetel.occ.service.command.ManualDialCommand;
import com.ozonetel.occ.service.command.MuteCommand;
import com.ozonetel.occ.service.command.PhoneConferenceCommand;
import com.ozonetel.occ.service.command.SkillTransferCommand;
import com.ozonetel.occ.service.command.UnHoldCommand;
import com.ozonetel.occ.service.command.UnMuteCommand;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.service.command.response.ConferenceCommandResponse;
import com.ozonetel.occ.service.command.response.GetTransferAgentListResponse;
import com.ozonetel.occ.service.command.response.GetTransferNumberListResponse;
import com.ozonetel.occ.service.impl.CheckManualDialStatus;
import com.ozonetel.occ.service.impl.MyExclusionStrategy;
import com.ozonetel.occ.service.impl.Status;
import com.ozonetel.occ.service.impl.TokenServerLocalImpl;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.DateUtil;
import com.ozonetel.occ.util.JsonUtil;
import com.ozonetel.occ.util.SecurityUtil;
import com.ozonetel.occ.webapp.util.RequestUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author aschulze
 */
public class MessageHandlerServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(MessageHandlerServlet.class);
    protected static OCCManager occManager;
    protected static ToolBarManager toolBarManager;
    protected static PreviewDialerManager previewDialerManager;
    protected static CampaignManager campaignManager;
    protected static AgentManager agentManager;
    protected static PreviewDataManager previewDataManager;
    protected static PreviewDataAuditLogManager previewDataAuditLogManager;
    protected static CampaignConfigManager campaignConfigManager;
    protected static CampaignHoldMusicManager campaignHoldMusicManager;
    private AgentTransferManager agentTransferManager;
    private PhoneTransferManager phoneTransferManager;
    private SkillTransferManager skillTransferManager;
    private AgentCommandExecutor agentCommandExecutor;
    private ManualDialService manualDialService;
    private TelephonyManager telephonyManager;
    private TransferNumberManager transferNumberManager;
    private UserManager userManager;
    private CallQueueManager callQueueManager;
    private static String chatAgentId;
    private ChatServiceImpl chatService;
    private ChatBotServiceImpl chatBotService;
    private RedisAgentManager redisAgentManager;
    private FacebookChatServiceImpl facebookChatService;
    private ReportManager reportManager;
    private static TokenServerLocalImpl tokenServer;
    private EventManager eventManager;
    private DispositionManager dispositionManager;
    private AppProperty appProperty;
    private ChatOnCallServiceImpl chatOnCallService;
    private ChatTransferServiceImpl chatTransferService;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Servlet got request");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Map<String, Object> map = processToken(new FakeServerEvent(request.getParameter("sessionId"), request.getParameter("clientId")), new WrappedRequestToken(request));
        log.debug(" 🍉 🍉 🍉 Request:" + RequestUtil.getRequestParams(request) + " -> Response:" + map);
        try {
            out.write(new GsonBuilder().serializeNulls().create().toJson(map));
        } finally {
            out.close();
        }
    }

    public void processOpened() {
        log.debug("in message handler servlet----");

//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        occManager = (OCCManager) webApplicationContext.getBean("occManager");
        toolBarManager = (ToolBarManager) webApplicationContext.getBean("toolBarManager");
        previewDialerManager = (PreviewDialerManager) webApplicationContext.getBean("previewDialerManager");
        previewDataManager = (PreviewDataManager) webApplicationContext.getBean("previewDataManager");
        previewDataAuditLogManager = (PreviewDataAuditLogManager) webApplicationContext.getBean("previewDataAuditLogManager");
        agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
        agentTransferManager = (AgentTransferManager) webApplicationContext.getBean("agentTransferManager");
        phoneTransferManager = (PhoneTransferManager) webApplicationContext.getBean("phoneTransferManager");
        skillTransferManager = (SkillTransferManager) webApplicationContext.getBean("skillTransferManager");
        agentCommandExecutor = (AgentCommandExecutor) webApplicationContext.getBean("agentCommandExecutor");
        manualDialService = (ManualDialService) webApplicationContext.getBean("manualDialService");
        telephonyManager = (TelephonyManager) webApplicationContext.getBean("telephonyManager");
        transferNumberManager = (TransferNumberManager) webApplicationContext.getBean("transferNumberManager");
        userManager = (UserManager) webApplicationContext.getBean("userManager");
        callQueueManager = (CallQueueManager) webApplicationContext.getBean("callQueueManager");
        chatService = (ChatServiceImpl) webApplicationContext.getBean("chatService");
        reportManager = (ReportManager) webApplicationContext.getBean("reportManager");
        previewDialerManager = (PreviewDialerManager) webApplicationContext.getBean("previewDialerManager");
        campaignManager = (CampaignManager) webApplicationContext.getBean("campaignManager");
        campaignConfigManager = (CampaignConfigManager) webApplicationContext.getBean("CampaignConfigManager");
        campaignHoldMusicManager = (CampaignHoldMusicManager) webApplicationContext.getBean("CampaignHoldMusicManager");
        chatBotService = (ChatBotServiceImpl) webApplicationContext.getBean("chatBotService");
        redisAgentManager = (RedisAgentManager) webApplicationContext.getBean("redisAgentManager");
        facebookChatService = (FacebookChatServiceImpl) webApplicationContext.getBean("facebookChatService");
        eventManager = (EventManager) webApplicationContext.getBean("eventManager");
        dispositionManager = (DispositionManager) webApplicationContext.getBean("dispositionManager");
        appProperty = (AppProperty) webApplicationContext.getBean("appProperty");
        chatOnCallService = (ChatOnCallServiceImpl) webApplicationContext.getBean("chatOnCallService");
        chatTransferService = (ChatTransferServiceImpl) webApplicationContext.getBean("chatTransferService");

        occManager.initialize();
        toolBarManager.initialize();
        previewDialerManager.initialize();
    }

    private TokenServerLocalImpl getTokenServer() {
        if (tokenServer == null) {
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }
        return tokenServer;

    }

    public Map<String, Object> processToken(FakeServerEvent aEvent, WrappedRequestToken aToken) {

        // here you can interpret the token type sent from the client according to your needs.
        boolean handledHere = true;
        String lNS = aToken.getNS();
        String lType = aToken.getType();
        String resp = "";
        //
        // ----- > Trimming white spaces.
        final String username = StringUtils.trim(aToken.getString("customer"));
        final String agentId = StringUtils.trim(aToken.getString("agentId"));
        final String phoneNumber = StringUtils.trim(aToken.getString("phoneNumber"));
        final Long agentUniqId = aToken.getString("agentUniqId") != null ? Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))) : null;
        boolean informDialer = false;
        final String disclaimer = aToken.getString("playDisclaimer");
        log.debug("Got request:" + username + "|" + agentId + " -> " + aToken);

        FakeWebsocketResponse lResponse = new FakeWebsocketResponse(aToken.getUtid());
        try {

            if (lType != null && "ozonetel.cloudagent".equals(lNS) && !lType.equalsIgnoreCase("response")) {
                // create a response token
                log.info("Client '" + aEvent.getSessionId() + "' sent Token: '" + aToken.toString() + "'.");
                if ("getInfo".equals(lType)) {
                    lResponse.setString("vendor", "Ozonetel");
                    lResponse.setString("version", "1.0");
                    lResponse.setString("copyright", "None");
                    lResponse.setString("license", "Full");
                } else if ("tbReconnect".equals(lType)) {
                    try {

                        String usId = aToken.getString("usId");
                        Event.AgentMode mode = null;
                        try {
                            mode = Event.AgentMode.valueOf(StringUtils.trim(StringUtils.upperCase(aToken.getString("mode"))));
                        } catch (IllegalArgumentException e) {
                            log.error("Error setting agent mode in Event(Reconnect) ->" + e.getMessage(), e);
                        }

                        Map<String, String> response = toolBarManager.tbAgentReconnect(aEvent.getSessionId(), usId, username, agentId, agentUniqId, phoneNumber, mode, aToken.getString("event"), (aToken.getString("ucid") != null ? new Long(aToken.getString("ucid")) : null));
                        lResponse.setString("status", response.get("status"));
                        lResponse.setString("mode", response.get("mode"));
                        lResponse.setString("idleTime", response.get("idleTime"));
                        lResponse.setString("latestToken", response.get("eventData"));
                        lResponse.setLong("chatCount", chatService.getActiveChatSessionsCountByAgent(username, agentId) + chatService.getActiveCallingChatSessionsCountByAgent(username, agentId));

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                } else if ("tbAgentLogin".equals(lType)) {
                    try {
                        log.debug("agent log in request");
                        String usId = aToken.getString("usId");

                        //----Chat test-------
                        chatAgentId = usId;
                        log.debug("Chat agentId:" + chatAgentId + "|" + aEvent.getSessionId());
                        //----Chat test-------
                        boolean reconnect = aToken.getBoolean("reconnect");

                        Event.AgentMode mode = null;
                        try {
                            mode = Event.AgentMode.valueOf(StringUtils.trim(StringUtils.upperCase(aToken.getString("params"))));
                        } catch (IllegalArgumentException e) {
                            log.error("Error setting agent mode in Event(Login Action/Reconnect:" + reconnect + " ) ->" + e.getMessage(), e);
                        }
                        JsonObject jsonObject = null;
                        try {
                            jsonObject = toolBarManager.tbAgentLogin(username, agentId, agentUniqId, phoneNumber, aEvent.getSessionId(), usId, reconnect, mode, aToken.getString("loginIp"));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }

                        if (jsonObject != null) {
                            if (StringUtils.equalsIgnoreCase(jsonObject.get("status").getAsString(), "Success")) {
                                log.debug("Agent logged in, hence setting othrt Parameters");
                                lResponse.setString("campaignType", (jsonObject.get("campaignType") == null ? "" : jsonObject.get("campaignType").getAsString()));
                                lResponse.setString("campaignScript", (jsonObject.get("campaignScript") == null ? "" : jsonObject.get("campaignScript").getAsString()));
                                lResponse.setString("phoneNumber", (jsonObject.get("phoneNumber") == null ? "" : jsonObject.get("phoneNumber").getAsString()));
                                lResponse.setString("agentSkill", (jsonObject.get("agentSkill") == null ? "" : jsonObject.get("agentSkill").getAsString()));
                                //
                                // ------ > Tells whether call backs feature is enabled for the customer or not.
                                lResponse.setString("cbe", (jsonObject.get("callbackRole") == null ? "" : jsonObject.get("callbackRole").getAsString()));
                                lResponse.setString("pauseReasons", new GsonBuilder()
                                        .setExclusionStrategies(new MyExclusionStrategy(User.class))
                                        .serializeNulls()
                                        .create().toJson(occManager.getPauseReasons(username)));
                                lResponse.setString("outboundEnabled", (jsonObject.get("outboundRole") == null ? "" : jsonObject.get("outboundRole").getAsString()));
                                lResponse.setString("smse", (jsonObject.get("smsRole") == null ? "0" : jsonObject.get("smsRole").getAsString()));
                                lResponse.setString("mcn", (jsonObject.get("maskCustomernumber") == null ? "0" : jsonObject.get("maskCustomernumber").getAsString()));
                                lResponse.setString("groupDisp", (jsonObject.get("groupDisps") == null ? "0" : jsonObject.get("groupDisps").getAsString()));
                                lResponse.setString("stemps", jsonObject.get("stemps").getAsString());
                                lResponse.setString("pauseAlert", (jsonObject.get("pauseAlert") == null ? "0" : jsonObject.get("pauseAlert").getAsString()));
                                lResponse.setString("handleReconnect", (jsonObject.get("handleReconnect") == null ? "0" : jsonObject.get("handleReconnect").getAsString()));
                                lResponse.setString("blend", (jsonObject.get("blendedRole") == null ? "0" : jsonObject.get("blendedRole").getAsString()));
                                lResponse.setString("ach", (jsonObject.get("agentCallHist") == null ? "0" : jsonObject.get("agentCallHist").getAsString()));
                                lResponse.setBoolean("isSip", jsonObject.get("isSip").getAsBoolean());
                                lResponse.setInteger("forceRelease", jsonObject.get("forceRelease").getAsInt());
                                lResponse.setInteger("queueAlert", jsonObject.get("queueAlert").getAsInt());

                                lResponse.setInteger("tktCustomer", jsonObject.get("tktCustomer").getAsInt());
                                lResponse.setInteger("hr", jsonObject.get("hr").getAsInt());
                                lResponse.setInteger("cr", jsonObject.get("cr").getAsInt());
                                lResponse.setInteger("mr", jsonObject.get("mr").getAsInt());
                                lResponse.setInteger("chtr", jsonObject.get("chtr").getAsInt());

                            } else {
                                log.debug("Agent not logged in, hence sending error response");
//                                lResponse.setString("resp", resp);
                                lResponse.setString("resp", "Error");
//                                lResponse.setString("status", StringUtils.equalsIgnoreCase(jsonObject.get("status").getAsString().toString()));
                                lResponse.setString("status", jsonObject.get("status").getAsString());
                                resp = "Error";
                            }
                        } else {
                            lResponse.setString("resp", resp);
                        }
                        log.debug("Final Status of resp : " + resp);
                        if (!resp.contains("Error")) {
                            lResponse.setString("agentStatus", "AUX");
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        e.printStackTrace();

                    }
//                  occManager.informDialler();
                } else if ("tbAgentLogout".equals(lType)) {
                    Event.AgentMode mode = null;
                    try {
                        mode = Event.AgentMode.valueOf(StringUtils.trim(StringUtils.upperCase(aToken.getString("params"))));
                    } catch (IllegalArgumentException e) {
                        log.error("Error setting agent mode in Event(Logout Action ) ->" + e.getMessage(), e);
                    }

                    log.debug("Logout reason:" + aToken.getString("logoutBy"));
                    resp = toolBarManager.tbAgentLogout(username, agentId, agentUniqId, phoneNumber, mode, aToken.getString("logoutBy"));

                    lResponse.setString("resp", resp);
                } else if ("tbAgentBusy".equals(lType)) {
                    resp = toolBarManager.tbAgentBusy(username, agentId);
                    lResponse.setString("resp", resp);
                    if (!resp.contains("Erro"
                            + "r")) {
                        lResponse.setString("agentStatus", "BUSY");
                    }

                } else if ("tbAgentPause".equals(lType)) {
                    String[] params = StringUtils.splitPreserveAllTokens(aToken.getString("params"), ",");
                    Event.AgentMode mode = null;
                    try {
                        mode = Event.AgentMode.valueOf(StringUtils.trim(StringUtils.upperCase(params[1])));
                    } catch (IllegalArgumentException e) {
                        log.error("Error setting agent mode in Event(Release Action) ->" + e.getMessage(), e);
                    }
                    StatusMessage pauseResponse = toolBarManager.tbAgentPause(username, agentId, agentUniqId, params[0], mode);

                    lResponse.setString("resp", pauseResponse.toOneString());
                    lResponse.setString("status", pauseResponse.getStatus().toReadableString());
                    lResponse.setString("message", pauseResponse.getMessage());

                    if (pauseResponse.getStatus() == Status.SUCCESS) {
                        lResponse.setString("agentStatus", "PAUSED");
                    }
                } else if ("tbPauseAlert".equals(lType)) {// ----> Alert will come when agent exceeds the configured pause time.
                    try {
                        System.out.println("Sending pass exceeded alert:" + agentId + " | " + aToken.getString("reason") + " | " + aToken.getString("to"));
                        final String reason = aToken.getString("reason");
                        final String to = aToken.getString("to");
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("agentUniqId", agentUniqId);
                        jsonObject.addProperty("agentId", agentId);
                        jsonObject.addProperty("username", username);
                        jsonObject.addProperty("reason", reason);
                        jsonObject.addProperty("timeout", to);
                        log.debug("pushing for pause alerts : " + jsonObject.toString() + " | " + redisAgentManager.rpush(Constants.PAUSE_ALERTS, jsonObject.toString()));
//                        new Thread(new Runnable() {
//                            public void run() {
//                                toolBarManager.alertAgentExceededPauseTime(agentUniqId, username, agentId, reason, Integer.valueOf(to));
//                            }
//                        }).start();
                        lResponse.setString("resp", "success");
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        e.printStackTrace();
                        System.out.println("Pause alert is not sent:" + e.getMessage());
                    }
                } else if ("tbAgentRelease".equals(lType)) {
                    Event.AgentMode mode = null;
                    try {
                        mode = Event.AgentMode.valueOf(StringUtils.trim(StringUtils.upperCase(aToken.getString("params"))));
                    } catch (IllegalArgumentException e) {
                        log.error("Error setting agent mode in Event(Release Action) ->" + e.getMessage(), e);
                    }

                    try {
                        resp = toolBarManager.tbAgentRelease(username, agentId, agentUniqId, mode, aToken.getString("mes"));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    lResponse.setString("resp", resp);
                    if (!resp.contains("Error")) {
                        String r[] = resp.split("~");
                        lResponse.setString("agentStatus", "IDLE");
                        if (r[1] != null && (r[1].equalsIgnoreCase(Agent.Mode.PROGRESSIVE.toString()) || r[1].equalsIgnoreCase(Agent.Mode.BLENDED.toString()))) {
                            informDialer = true;
                        }

                    }
                } else if ("tbGetMonitor".equals(lType)) {
                    lResponse.setInteger("clientCount", aEvent.getConnectorCount());
                } else if ("tbSendSMS".equals(lType)) {
                    String smsResp = "";
                    if (occManager.getUserManager().hasRole(username, Constants.SMS_ROLE)) {
//                    BigInteger ucid=new BigInteger(StringUtils.isEmpty(aToken.getString("ucid"))?"0":aToken.getString("ucid"));
                        smsResp = toolBarManager.sendSMS(username, new BigInteger(StringUtils.isEmpty(aToken.getString("ucid")) ? "0" : aToken.getString("ucid")), Long.valueOf(StringUtils.isEmpty(aToken.getString("campId")) ? "0" : aToken.getString("campId")), agentId, aToken.getString("dest"), aToken.getString("msg"), aToken.getString("entityId"), aToken.getString("templateId")).getStatus().toString();
                    } else {
                        smsResp = "Error:You don't have this feature enabled.";
                    }
                    lResponse.setString("resp", smsResp);

                }else if ("tbSendWhatsappMsg".equals(lType)) {
                    String whatsappResp = "";                    
                    log.debug("proceeding for sendWhatsappMSg with username: "+username+" and recipient : "+aToken.getString("recipient")+" and templateName"+aToken.getString("templateName")+"and replacementText : "+aToken.getString("replacementText"));
                    StatusMessage msg  = toolBarManager.sendWhatsappMSG(username, aToken.getString("recipient"), aToken.getString("templateName"), aToken.getString("replacementText"));
                    whatsappResp = msg.getStatus().toString();
                    log.debug("response for whatsapp chat message: "+msg.getMessage().toString()+" and status is : "+msg.getStatus().toString());
                    lResponse.setString("resp", whatsappResp);
                }else if ("tbGetDispositions".equals(lType)) {
                    String params = aToken.getString("params");
                    if (params != null && !params.isEmpty()) {
                        String paramKey[] = params.split("~");

                        Map dispMap = toolBarManager.getDispositions(username, agentUniqId, agentId, paramKey[0], paramKey[1], paramKey[2]);
                        String jsonDisps = new Gson().toJson(dispMap);
                        lResponse.setString("dispList", jsonDisps);
                        lResponse.setString("resp", "Success");
                    } else {
                        lResponse.setString("resp", "failed");
                    }

                } else if ("tbSetDisposition".equals(lType)) {

                    String params = aToken.getString("params");
                    if (params != null && !params.isEmpty()) {
                        log.debug("params : " + params);
                        String paramKey[] = StringUtils.splitPreserveAllTokens(params, ",");
                        String dataId = paramKey[0];
                        String dispositionCode = paramKey[1];
                         String callBackTime = paramKey[2];
                        final String ucid = paramKey[3];
                        String did = paramKey[4];
                        final String callerID = redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username) ? SecurityUtil.decryptUsingAes256Key(paramKey[5]) : paramKey[5];
                        String uui = paramKey[6];

                        String ticketId = null;
                        String ticketType = null;
                        final String ticketStatus;
                        final String ticketDesc;
                        final String ticketCmt;

                        if (paramKey.length > 7 && occManager.getUserManager().hasTicketRole(username)) {
                            ticketId = paramKey[7];
                            ticketType = paramKey[8];
                            ticketStatus = paramKey[9];
//                        if (paramKey.length > 8 && occManager.getUserManager().hasTicketRole(username)) {
//                            ticketId = paramKey[8];
//                            ticketType = paramKey[9];
//                            ticketStatus = paramKey[10];
                            ticketDesc = aToken.getString("tktDesc");
                            ticketCmt = aToken.getString("tktCmnt");
                        } else {
                            ticketStatus = null;
                            ticketDesc = null;
                            ticketCmt = null;
                        }

                        String dispComments = aToken.getString("comments");
                        Long refid = null;
                        try {
                            refid = Long.parseLong(ticketId);
                        } catch (Exception ignore) {
                        }

                        try {
                            if ((refid != null) && occManager.getUserManager().hasTicketRole(username)) {
                                log.info("Has to save the ticket with ticket id:" + refid);
                                final Long finalRefID = refid;

                                if (StringUtils.equalsIgnoreCase(ticketType, "new")) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            toolBarManager.openTicket(finalRefID, username,
                                                    agentId, callerID, ticketDesc, Long.valueOf(ucid), null, ticketCmt);
                                        }
                                    }).start();
                                } else {

                                    new Thread(
                                            new Runnable() {
                                                public void run() {
                                                    toolBarManager.updateTicket(finalRefID, ticketStatus, username, Long.valueOf(ucid), null, ticketCmt, agentId, callerID);
                                                }
                                            }).start();
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }

                        if (dispositionCode != null && !dispositionCode.equals("")) {
                            if (dataId.equals("") && StringUtils.isNotBlank(aToken.getString("monitorUcid"))) {
                                String monitorUcid = aToken.getString("monitorUcid");
                                MessageFormat messageFormat = new MessageFormat("");
                                messageFormat.applyPattern(Constants.UCID_CALL_DETAILS);
                                dataId = redisAgentManager.hget(messageFormat.format(new Object[]{monitorUcid}), "dataId");
                                log.debug("Fetching data id from redis for : " + monitorUcid + " And dataId : " + dataId);
                            }
                            resp = toolBarManager.setDisposition(agentUniqId, dataId, dispositionCode, callBackTime, ucid, did, dispComments, agentId, username, refid, uui, aToken.getString("monitorUcid"),aToken.getString("callbacktz"));
                        } else {
                            resp = "failed";
                        }
                        lResponse.setString("resp", resp);
                    }
                } else if ("tbGetTransferList".equals(lType)) {
                    try {
                        int transferType = Integer.parseInt(aToken.getString("transferType"));

                        switch (transferType) {
                            case 1: // ------ > Agent transfer list.
                                GetTransferAgentListResponse getTransferAgentListResponse = agentCommandExecutor.executeCommand(new GetTransferAgentListCommand(username, agentUniqId, agentId, agentManager));
                                lResponse.setString("status", getTransferAgentListResponse.getStatus().toReadableString());
                                lResponse.setList("message", getTransferAgentListResponse.getAgentList());
                                break;

                            case 2:

                                lResponse.setString("status", "Success");
                                lResponse.setList("message", occManager.getSkillTransferList(username, aToken.getString("did"), agentId));

                                break;

                            case 4:

                                lResponse.setString("status", "Success");
                                lResponse.setList("message", occManager.getFeedbackIVRList(username));

                                break;
                            default: // ----> Phone transfer list.
                                GetTransferNumberListResponse getTransferNumberListResponse = agentCommandExecutor.executeCommand(new GetTransferNumberListCommand(username, agentId, transferNumberManager));
                                lResponse.setString("status", getTransferNumberListResponse.getStatus().toReadableString());
                                lResponse.setList("message", getTransferNumberListResponse.getTransferNumberList());
                                break;
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        lResponse.setString("status", "Fail");
                        lResponse.setString("message", "Unknown");

                    }
                } else if (StringUtils.equalsIgnoreCase("tbGetConferenceList", lType)) {

                    try {
                        Integer confType = aToken.getString("confType") != null ? Integer.valueOf(aToken.getString("confType")) : aToken.getInteger("confType");
                        lResponse.setInteger("transferType", confType);
                        log.debug("^^^^^Conference type:" + confType + " | String:" + aToken.getString("confType") + " | Integer:" + aToken.getInteger("confType"));
                        switch (confType) {
                            case 1: // ----> agents
                                GetTransferAgentListResponse getTransferAgentListResponse = agentCommandExecutor.executeCommand(new GetTransferAgentListCommand(username, agentUniqId, agentId, agentManager));
                                lResponse.setString("status", getTransferAgentListResponse.getStatus().toReadableString());
                                lResponse.setList("message", getTransferAgentListResponse.getAgentList());
                                break;
                            case 3: // ----> Phone
                                GetTransferNumberListResponse getTransferNumberListResponse = agentCommandExecutor.executeCommand(new GetTransferNumberListCommand(username, agentId, transferNumberManager));
                                lResponse.setString("status", getTransferNumberListResponse.getStatus().toReadableString());
                                lResponse.setList("message", getTransferNumberListResponse.getTransferNumberList());
                                break;

                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        lResponse.setString("status", "Fail");

                    }

                } else if ("tbConference".equalsIgnoreCase(lType)) {
                    try {
                        log.debug("--> ConfType String:" + aToken.getString("confType") + " | Integer:" + aToken.getInteger("confType"));
                        int confType = aToken.getString("confType") != null ? Integer.parseInt(aToken.getString("confType")) : aToken.getInteger("confType");
                        AgentToolbarCommand<ConferenceCommandResponse> agentToolbarCommand = null;

                        switch (confType) {
                            case 1: // ----> Agent conference
                                agentToolbarCommand = new AgentConferenceCommand(username, agentUniqId, agentId, aToken.getString("participant"), aToken.getString("monitorUcid"),
                                        aToken.getString("ucid"), aToken.getString("did"), redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username) ? SecurityUtil.decryptUsingAes256Key(aToken.getString("callerId")) : aToken.getString("callerId"), telephonyManager);
                                break;
                            case 3: // ----> Phone conference
                                String participant = aToken.getString("participant");
                                if (StringUtils.contains(participant, "~")) {
                                    participant = StringUtils.splitPreserveAllTokens(participant, "~")[1];
                                }

                                agentToolbarCommand = new PhoneConferenceCommand(username, agentId, null, participant, Boolean.valueOf(aToken.getString("sip")), aToken.getString("monitorUcid"),
                                        aToken.getString("ucid"), aToken.getString("did"), StringUtils.isNotBlank(aToken.getString("encryptField")) && aToken.getString("encryptField").equalsIgnoreCase("true") ? SecurityUtil.decryptUsingAes256Key(aToken.getString("callerId")) : aToken.getString("callerId"), telephonyManager);
                                break;
                            default:
                                log.error("Invalid conference requeset from agent:" + agentId + " [Customer:" + username + "]");
                                break;

                        }

                        ConferenceCommandResponse conferenceStatus = agentCommandExecutor.executeCommand(agentToolbarCommand);
                        lResponse.setString("status", conferenceStatus.getStatus().toReadableString());
                        lResponse.setString("conferenceNumber", conferenceStatus.getConferenceNumber());
                        lResponse.setString("message", conferenceStatus.getMessage());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Conference failed.");
                    }

                } else if ("tbHold".equalsIgnoreCase(lType)) {
                    try {
//                        log.debug("CampaignId = " + aToken.getString("campignId") + " MonitorUCID = " + aToken.getString("monitorUcid") + " username = " + aToken.getString("username"));
                        Campaign campaign = campaignManager.get(Long.parseLong(aToken.getString("campignId")));
                        String appAudioURL = null;
                        if (occManager.getUserManager().hasRole(username, Constants.CAMPAIGN_HOLDMUSICROLE)) {
                            log.debug("CAMPAIGN_HOLDMUSIC Role assigned to the Customer having MonitorUCID : " + aToken.getString("monitorUcid"));
                            try {
                                List<CampaignConfig> campConf = campaignConfigManager.getCampaignConfigByCampaignIdAndType(campaign.getCampaignId(), Constants.CAMPAIGN_HOLDMUSIC);
                                if (campConf != null && campConf.size() > 0 && campConf.get(0).isActive()) {
                                    Integer configValue = new JSONObject(campConf.get(0).getConfigValue()).getInt("value");
                                    List<CampaignHoldMusic> campaignHoldMusicList = campaignHoldMusicManager.getAudioUrlById(new Long(configValue));
                                    if (campaignHoldMusicList != null && campaignHoldMusicList.size() > 0) {
                                        appAudioURL = campaignHoldMusicList.get(0).getAudioUrl();
                                    }
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        } else {
                            log.debug("CAMPAIGN_HOLDMUSIC Role not assigned to the Customer having MonitorUCID : " + aToken.getString("monitorUcid"));
                        }
                        log.debug("Finally appAudioURL for CampaignHold : " + appAudioURL + " with MonitorUCID : " + aToken.getString("monitorUcid"));
                        AgentCommandStatus agentCommandStatus = agentCommandExecutor.executeCommand(
                                new HoldCommand(username, agentUniqId, aToken.getString("agentNumber"), agentId, new BigInteger(aToken.getString("monitorUcid")),
                                        new BigInteger(aToken.getString("ucid")), redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username) ? SecurityUtil.decryptUsingAes256Key(aToken.getString("phoneNumberToHold")) : aToken.getString("phoneNumberToHold"),
                                        aToken.getString("did"), new BigInteger(aToken.getString("campignId")),
                                        telephonyManager, appAudioURL));
                        lResponse.setString("status", agentCommandStatus.getStatus().toReadableString());
                        lResponse.setString("message", agentCommandStatus.getMessage());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Unknown");
                    }
                } else if ("tbUnHold".equalsIgnoreCase(lType)) {
                    try {
                        AgentCommandStatus agentCommandStatus = agentCommandExecutor.executeCommand(new UnHoldCommand(username, agentUniqId, agentId, new BigInteger(aToken.getString("monitorUcid")), new BigInteger(aToken.getString("ucid")),redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username) ? SecurityUtil.decryptUsingAes256Key(aToken.getString("phoneNumberToUnHold")) : aToken.getString("phoneNumberToUnHold"), aToken.getString("did"), telephonyManager));

                        lResponse.setString("status", agentCommandStatus.getStatus().toReadableString());
                        lResponse.setString("message", agentCommandStatus.getMessage());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Unknown");
                    }

                } else if ("tbKickCall".equals(lType)) {
                    try {
                        AgentCommandStatus response = agentCommandExecutor.executeCommand(new KickCallCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("phoneNumberToKick"), telephonyManager));
                        lResponse.setString("status", response.getStatus().toReadableString());
                        lResponse.setString("message", response.getMessage());
                    } catch (Exception e) {
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Kick failed");
                        log.error(e.getMessage(), e);
                    }
                } else if ("tbMute".equals(lType)) {
                    try {
                        AgentCommandStatus response = agentCommandExecutor.executeCommand(new MuteCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("phoneNumberToMute"), telephonyManager));
                        lResponse.setString("status", response.getStatus().toReadableString());
                        lResponse.setString("message", response.getMessage());
                    } catch (Exception e) {
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Mute failed");
                        log.error(e.getMessage(), e);
                    }
                } else if ("tbUnMute".equals(lType)) {
                    try {
                        AgentCommandStatus response = agentCommandExecutor.executeCommand(new UnMuteCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("phoneNumberToUnMute"), telephonyManager));
                        lResponse.setString("status", response.getStatus().toReadableString());
                        lResponse.setString("message", response.getMessage());
                    } catch (Exception e) {
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Unmute failed");
                        log.error(e.getMessage(), e);
                    }
                } else if ("tbDropHold".equals(lType)) {
                    try {
                        AgentCommandStatus response = agentCommandExecutor.executeCommand(new DropMeCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"),
                                aToken.getString("did"), aToken.getString("agentNumber"), redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username) ? SecurityUtil.decryptUsingAes256Key(aToken.getString("customerNumber")) : aToken.getString("customerNumber"), telephonyManager));

                        lResponse.setString("status", response.getStatus().toReadableString());
                        lResponse.setString("message", response.getMessage());
                    } catch (Exception e) {
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Drop failed");
                        log.error(e.getMessage(), e);
                    }
                } else if ("tbInitiateAgentCall".equals(lType)) {
                    try {
                        log.debug("agentId : " + agentId + " agentUniqId : " + agentUniqId + " agentPhone : " + aToken.getString("agentPhone") + " username : " + username + " apiKey : " + aToken.getString("apiKey"));
                        AgentCommandStatus response = agentCommandExecutor.executeCommand(new AgentBridgeCommand(username, agentUniqId, agentId, aToken.getString("agentPhone"), aToken.getString("apiKey"), telephonyManager));

                        lResponse.setString("status", response.getStatus().toReadableString());
                        lResponse.setString("message", response.getMessage());
                    } catch (Exception e) {
                        lResponse.setString("status", Status.ERROR.toReadableString());
                        lResponse.setString("message", "Drop failed");
                        log.error(e.getMessage(), e);
                    }
                } else if ("tbSetTransfer".equals(lType)) {

                    try {
                        String params = aToken.getString("params");
                        if (params != null && !params.isEmpty()) {
                            String paramKey[] = org.apache.commons.lang3.StringUtils.splitPreserveAllTokens(params, ",");
                            String did = paramKey[0];
                            Long ucid = Long.valueOf(paramKey[1]);
                            int transferType = Integer.parseInt(paramKey[2]);
                            
                            
                            String transferId = paramKey[3];
                            String transferNumber = paramKey[4];
                            int blindTransfer = Integer.parseInt(paramKey[5]);

                            Long monitorUcid = Long.valueOf(aToken.getString("monitorUcid"));
                            String customerNumber = redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username) ? SecurityUtil.decryptUsingAes256Key(aToken.getString("custNumber")):aToken.getString("custNumber");
                            AgentToolbarCommand<AgentCommandStatus> transferCommand = null;
                            String receiverAgentId = transferId;
                            String campId = aToken.getString("campId");
                            String uui = aToken.getString("uui");
                            Campaign campaign = campaignManager.get(Long.parseLong(campId));
                            String appAudioURL = null;

                            if (occManager.getUserManager().hasRole(username, Constants.CAMPAIGN_HOLDMUSICROLE)) {
                                log.debug("CAMPAIGN_HOLDMUSIC Role assigned to the Customer having MonitorUCID : " + aToken.getString("monitorUcid"));
                                try {
                                    List<CampaignConfig> campConf = campaignConfigManager.getCampaignConfigByCampaignIdAndType(campaign.getCampaignId(), Constants.CAMPAIGN_TRANSFERMUSIC);
                                    if (campConf != null && campConf.size() > 0 && campConf.get(0).isActive()) {
                                        Integer configValue = new JSONObject(campConf.get(0).getConfigValue()).getInt("value");
                                        List<CampaignHoldMusic> campaignHoldMusicList = campaignHoldMusicManager.getAudioUrlById(new Long(configValue));
                                        if (campaignHoldMusicList != null && campaignHoldMusicList.size() > 0) {
                                            appAudioURL = campaignHoldMusicList.get(0).getAudioUrl();
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                            } else {
                                log.debug("CAMPAIGN_HOLDMUSIC Role not assigned to the Customer having MonitorUCID : " + aToken.getString("monitorUcid"));
                            }
                            log.debug("Finally appAudioURL for CampaignTransfer : " + appAudioURL + " with MonitorUCID : " + aToken.getString("monitorUcid"));
                            log.debug("uui for CampaignTransfer : " + uui + " with MonitorUCID : " + monitorUcid);
                            if (transferType == 1) {//Agent 
                                switch (blindTransfer) {
                                    case 1: //Blind
                                        transferCommand = new BlindAgentTransferCommand(username, agentId, monitorUcid, ucid, did, receiverAgentId, customerNumber, agentTransferManager, appAudioURL);
                                        break;
                                    case 2:// Consultative
                                        transferCommand = new ConsultativeAgentTransferCommand(username, agentId, monitorUcid, ucid, did, receiverAgentId, customerNumber, agentTransferManager, appAudioURL);
                                        break;
                                    case 3://Consultative-Hold
                                        transferCommand = new ConsultativeHoldAgentTransferCommand(username, agentUniqId, agentId, monitorUcid, ucid, did, receiverAgentId, customerNumber, agentTransferManager, campId, appAudioURL, aToken.getString("record"), aToken.getString("callType"),uui);
                                        break;
                                }
                            } else if (transferType == 2) {//Skill 
                                log.debug("Transfer type Skill Pushing into DB");
                           
                                transferCommand = new SkillTransferCommand(username, agentId, monitorUcid, ucid, did, null, transferId, skillTransferManager);
                                AgentCommandStatus commandStatus = agentCommandExecutor.executeCommand(transferCommand);
                                String url = paramKey[7] + "?skillName=" + transferId + "&campaignId=" + campId;
                                String agentName = StringUtils.isNotBlank(aToken.getString("agentName")) ? aToken.getString("agentName") : "";
                                String skillName = StringUtils.isNotBlank(aToken.getString("skillName")) ? aToken.getString("skillName") : "";
//                                transferCommand = new IvrTransferCommand(monitorUcid, ucid, did, phoneNumber, customerNumber, false, url, telephonyManager, username, agentId, campId, agentUniqId);
                                switch (blindTransfer) {
                                    case 1:
                                        transferCommand = new IvrTransferCommand(monitorUcid, ucid, did, phoneNumber, customerNumber, false, url, this.telephonyManager, username, agentId, campId, agentUniqId, appAudioURL, campaign, null, agentName, skillName,transferType,uui, 0);
                                        break;
                                    case 3:
                                        transferCommand = new ConsultativeHoldSkillTransferCommand(monitorUcid, ucid, did, phoneNumber, customerNumber, false, url, this.telephonyManager, username, agentId, campId, agentUniqId, appAudioURL, aToken.getString("record"),uui);
                                }
                            } else if (transferType == 3) { //Phone
                                String transferNumberName = null;
                                if (transferId != null && transferId.contains("~")) {
                                    String[] phoneDetails = transferId.split("~");
                                    transferNumberName = phoneDetails[0];
                                    transferNumber = phoneDetails[1];
                                }

                                switch (blindTransfer) {
                                    case 1: //Blind
                                        transferCommand = new BlindPhoneTransferCommand(username, agentId, monitorUcid, ucid, did, null, transferNumberName, transferNumber, customerNumber, phoneTransferManager, appAudioURL);
                                        break;
                                    case 2:// Consultative
                                        transferCommand = new ConsultativePhoneTransferCommand(username, agentId, monitorUcid, ucid, did, null, transferNumberName, transferNumber, customerNumber, phoneTransferManager, appAudioURL);
                                        break;
                                    case 3://Consultative-Hold  appAudioURL
                                        transferCommand = new ConsultativeHoldPhoneTransferCommand(username, agentId, monitorUcid, ucid, did, null, transferNumberName, transferNumber, Boolean.valueOf(aToken.getString("sip")), customerNumber, phoneTransferManager, campId, appAudioURL, aToken.getString("record"), aToken.getString("callType"));
                                        break;

                                }
                            } else if (transferType == 4) { //IVR
                                String designerNameOrUrl = paramKey[6];
                                //transferId - -3 -> custom URL, other ivr designer IDs
                                String transferUrl = "";

                                //long ivrDesignerId = NumberUtils.toLong(transferId, -3); // -3 custom URL
                                if (StringUtils.equalsIgnoreCase(transferId, "-3")) { //custom URL
                                    transferUrl = designerNameOrUrl;
                                } else { // IVR designer ID
                                    transferUrl = appProperty.getIvrFeedbackUrl().concat(transferId)
                                            .concat("&ivrName=").concat(URLEncoder.encode(designerNameOrUrl, "UTF-8"));
                                }
                                String agentName = StringUtils.isNotBlank(aToken.getString("agentName")) ? aToken.getString("agentName") : "";
                                String skillName = StringUtils.isNotBlank(aToken.getString("skillName")) ? aToken.getString("skillName") : "";
                                log.debug("IVR Transfer URL : " + transferUrl + " by agent : " + agentName);
                                transferCommand = new IvrTransferCommand(monitorUcid, ucid, did, phoneNumber, customerNumber, false, transferUrl, telephonyManager, username, agentId, campId, agentUniqId, appAudioURL, campaign, StringUtils.equalsIgnoreCase(transferId, "-3") ? null : designerNameOrUrl, agentName, skillName,transferType,uui,blindTransfer);
                            }

                            AgentCommandStatus commandStatus = agentCommandExecutor.executeCommand(transferCommand);

                            log.debug("--> Command :" + transferCommand + " | Response:" + commandStatus);
                            lResponse.setString("status", commandStatus.getStatus().toReadableString());
                            lResponse.setString("message", commandStatus.getMessage());
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        lResponse.setString("status", "Fail");
                    }
                } else if ("tbGetUcidDetails".equals(lType)) {
                    log.debug("Fetching UCID Details based on UCID : " + new BigInteger(aToken.getString("ucid")));
                    if (aToken.getString("ucid") != null) {
//                        log.debug("The Passed UCID : "+aToken.getString("ucid"));
                        Report report = reportManager.getReportByUCID(Long.parseLong(aToken.getString("ucid")));
//                        log.debug("report in MessageHandlerServlet : "+report.toLongString());

                        if (report != null) {
                            Map<String, Object> details = new LinkedHashMap<>();
                            details.put("dest", report.getDest());
                            details.put("Status", report.getStatus());
                            details.put("call_data", report.getCall_data());
                            details.put("callDate", report.getCallDate());
                            details.put("agentId", report.getAgentId());
                            details.put("triedNumber", report.getTriedNumber());
                            details.put("audioFile", report.getAudioFile());
                            details.put("data_id", report.getData_id());
                            details.put("ucid", report.getUcid());
                            details.put("endTime", report.getEndTime());
                            details.put("timeToAnswer", report.getTimeToAnswer());
                            details.put("skillName", report.getSkillName());
                            details.put("hangUpBy", report.getHangUpBy());
                            details.put("monitorUcid", report.getMonitorUcid());
                            details.put("skillId", report.getSkillId());
                            details.put("dialOutNumberId", report.getDialOutNumberId());
                            details.put("comment", report.getComment());
                            details.put("uui", report.getUui());
                            details.put("did", report.getDid());
                            details.put("type", report.getType());
                            details.put("transferType", report.getTransferType());
                            details.put("transferAgentId", report.getTransferAgentId());
                            details.put("transferSkillId", report.getTransferSkillId());
                            details.put("transferNow", report.isTransferNow());
                            details.put("transferToNumber", report.getTransferToNumber());
                            details.put("blindTransfer", report.getBlindTransfer());
                            details.put("offline", report.isOffline());
                            details.put("dialStatus", report.getDialStatus());
                            details.put("callCompleted", report.isCallCompleted());
                            details.put("customerStatus", report.getCustomerStatus());
                            details.put("agentStatus", report.getAgentStatus());
                            details.put("agentStatus", report.getAgentStatus());
                            details.put("campaignId", report.getCampaignId());
//                        log.debug("Final details : "+details);

//                            lResponse.setString("message", new Gson().toJson(report.toLongString()));
                            lResponse.setString("message", new Gson().toJson(details));
                            lResponse.setString("resp", "success");
                        } else {
                            lResponse.setString("resp", "error");
                            lResponse.setString("message", "No details found");
                        }
                    }

                } else if ("tbGetFeedBackIVRs".equals(lType)) {
                    log.debug("Fetching User based Feedback IVRs");
                    if (aToken.getString("userId") != null) {
                        List<IvrFlow> feedbackIVRs = occManager.getFeedbackIVRList(username);
                        if (feedbackIVRs.isEmpty()) {
                            lResponse.setString("resp", "error");
                            lResponse.setString("message", "No details found");
                        } else {
                            lResponse.setString("message", new Gson().toJson(feedbackIVRs));
                            lResponse.setString("resp", "success");
                        }
                    } else {
                        lResponse.setString("resp", "error");
                        lResponse.setString("message", "No details found");
                    }
                } else if ("tbPreviewDial".equals(lType)) {

                    String params = aToken.getString("params");
                    if (params != null && !params.isEmpty()) {
                        String paramKey[] = params.split(",");
                        String previewId = paramKey[0];
                        String number = paramKey[1];
                        String agentPhoneNumber = paramKey[2];
                        log.debug("Called Preview Dial " + params);
                        String mappedAgentId = redisAgentManager.hget(Constants.PREVIEWDATA_AGENTID_MAP, previewId);
                        log.debug("Got mapping as : " + mappedAgentId);
                        if (StringUtils.equalsIgnoreCase(mappedAgentId, agentUniqId + "")) {
                            resp = previewDialerManager.dial(username, agentUniqId, agentId, aToken.getString("mode"), agentPhoneNumber, previewId);
                        } else {
                            resp = "Error:data timed out";
                        }
                        String respArray[] = resp.split(":");
                        if (respArray.length > 1) {
                            lResponse.setString("status", respArray[0]);
                            lResponse.setString("message", respArray[1]);
                        }
                        lResponse.setString("resp", resp);
                        
                        PreviewDataAuditLog previewDataAuditLog = previewDataAuditLogManager.getByPidAndAgentId(new Long(previewId), agentUniqId);
                        log.debug("previewDataAuditLog from DB : "+previewDataAuditLog);
                        if (previewDataAuditLog != null) {
                            previewDataAuditLog.setAction(PreviewDataAuditLog.Action.DIAL);
                            previewDataAuditLog.setDateUpdated(new Date());
                            previewDataAuditLogManager.save(previewDataAuditLog);
                        }
                    }
                } else if ("tbManualDial".equals(lType)) {
                    log.debug("Initiaiting an Outbound call");
                    String params = aToken.getString("params");
                    String mode = aToken.getString("mode");
                    if (params != null && !params.isEmpty()) {
                        String paramKey[] = params.split(",");//campaignId , NumberTo Dial , Agent Phone Number
                        String campaignId = paramKey[0];
                        String custNumber = paramKey[1];  // Customer Number to Dial
                        String agentPhoneNumber = paramKey[2]; // Agent Phone Number

                        log.debug("Called manual Dial " + params + " | IsSip String:" + aToken.getString("isSip") + " | IsSip boolean:" + aToken.getBoolean("isSip"));
                        AgentCommandStatus agentCommandStatus = null;
                        try {
                            log.debug("CampID" + Long.valueOf(campaignId));
                            log.debug("manualDialService" + manualDialService);
                            log.debug("agentCommandExecutor" + agentCommandExecutor);
                            agentCommandStatus = agentCommandExecutor.executeCommand(new ManualDialCommand(username, agentUniqId, agentId, mode, agentPhoneNumber, aToken.getBoolean("isSip"), custNumber, Long.valueOf(campaignId), manualDialService, StringUtils.isNotBlank(disclaimer) ? disclaimer : "false"));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        log.debug("status : " + agentCommandStatus.getStatus().toReadableString());
                        log.debug("message : " + agentCommandStatus.getMessage());
                        log.debug("resp : " + agentCommandStatus.getStatus().toReadableString() + ":" + agentCommandStatus.getMessage());

                        lResponse.setString("status", agentCommandStatus.getStatus().toReadableString());
                        lResponse.setString("message", agentCommandStatus.getMessage());
                        lResponse.setString("resp", agentCommandStatus.getStatus().toReadableString() + ":" + agentCommandStatus.getMessage());
//                    }
                    }
                } else if ("tbGetPreviewCampaigns".equals(lType)) {
                    log.debug("Called tbGet Preview Campaigns " + username + "==" + agentId);
                    List<Campaign> campaigns = campaignManager.getPreviewCampaignsByAgentId(username, agentId);
                    List previewCampains = new ArrayList();
                    for (Campaign campaign : campaigns) {
                        int dataSize = 0;
                        try {
                            dataSize = toolBarManager.getPreviewDataSize(agentId, campaign.getCampaignId());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        if (dataSize > 0) {
                            previewCampains.add(campaign.getCampignName() + "~" + campaign.getCampaignId() + "~" + toolBarManager.getPreviewDataSize(agentId, campaign.getCampaignId()));
                        } else // the data is zero check whether the campaign is completed if so make the campaign as completed
                        if (previewDataManager.isPreviewCampaignCompleted(campaign.getCampaignId())) {
                            campaign.setPosition("COMPLETED");
                            campaignManager.save(campaign);
                        }
                    }
                    lResponse.setList("previewCamps", previewCampains);
                    resp = "campaigns (" + previewCampains + ")";
                    lResponse.setString("resp", resp);
                } else if ("tbGetPreviewNumber".equals(lType)) {
                    try {
                        String params = aToken.getString("params");
                        lResponse.setBoolean("previewDataResp", Boolean.FALSE);
                        if (params != null && !params.isEmpty()) {
                            PreviewData previewData = null;
                            try {
                                previewData = toolBarManager.getNextPreviewNumber(agentId, new Long(params));
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                            if (previewData != null) {
                                User user = userManager.getUserByUsername(username);
                                log.debug("Adding pid:agentUniqId mapping | " + previewData.getId() + " : " + redisAgentManager.hset(Constants.PREVIEWDATA_AGENTID_MAP, previewData.getId().toString(), "" + agentUniqId));
                                lResponse.setBoolean("previewDataResp", Boolean.TRUE);
                                lResponse.setString("pId", previewData.getId().toString());
                                lResponse.setString("pName", previewData.getName());
                                lResponse.setString("pNumber", previewData.getPhoneNumber());
                                lResponse.setString("pCid", params);
                                lResponse.setString("dateCreated", DateUtil.convertDateToString(previewData.getCreateDate(), "dd/MMM/yyyy HH:mm:ss", user.getUserTimezone()));
                                
                                PreviewDataAuditLog previewDataAuditLog = new PreviewDataAuditLog();
                                previewDataAuditLog.setPid(previewData.getId());
                                previewDataAuditLog.setAgentId(agentUniqId);
                                previewDataAuditLog.setUserId(user.getId());
                                previewDataAuditLog.setCampaignId(new Long(params));
                                previewDataAuditLog.setUUI(previewData.getName());
                                previewDataAuditLog.setPhoneNumber(previewData.getPhoneNumber());
                                previewDataAuditLog.setAction(PreviewDataAuditLog.Action.ASSIGNED);
                                previewDataAuditLog.setDateAdded(previewData.getCreateDate());
                                previewDataAuditLog.setDateAssigned(new Date());
                                previewDataAuditLog.setDateUpdated(new Date());
                                log.debug("previewDataAuditLog : "+previewDataAuditLog);
                                previewDataAuditLogManager.save(previewDataAuditLog);
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else if ("tbGetCampaigns".equals(lType)) {
                    log.debug("Called Get Campaigns " + username + "==" + agentId);
                    List<Campaign> campaignList = campaignManager.getCampaignsByAgentId(username, agentId);
                    List campaigns = new ArrayList();
                    for (Campaign campaign : campaignList) {
                        //if (!campaign.isOffLineMode()) {
                        if (!campaign.isOffLineMode() && campaign.isAllowedManual()) {
                            campaigns.add(campaign.getCampaignId() + "~" + campaign.getCampignName());
                        }
                    }
                    lResponse.setList("campList", campaigns);
                    resp = "campaigns (" + campaigns + ")";
                    lResponse.setString("resp", resp);
                } else if ("tbSetAgentMode".equals(lType)) {
                    try {
//                String params = aToken.getString("params");
                        String[] params = StringUtils.splitPreserveAllTokens(aToken.getString("params"), ",");
                        if (params != null && params.length == 2) {
                            resp = toolBarManager.tbSetAgentMode(username, agentId, agentUniqId, params[0], params[1], aToken.getBoolean("reconnect"), aToken.getString("reconDet"));
                            if (params[0].equalsIgnoreCase("progressive") || params[0].equalsIgnoreCase("blended")) {
                                informDialer = true;
                            }
                        }

                        lResponse.setString("resp", resp);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                } else if ("tbResetPreviewNumber".equals(lType)) {
                    String pId = aToken.getString("params");
                    if (pId != null && !pId.isEmpty()) {
                        String mappedAgentId = redisAgentManager.hget(Constants.PREVIEWDATA_AGENTID_MAP, pId);
                        log.debug("Got mapping as : " + mappedAgentId);
                        if (StringUtils.equalsIgnoreCase(mappedAgentId, agentUniqId + "")) {
                            resp = toolBarManager.tbResetPreviewNumber(pId);
                            if (resp.equalsIgnoreCase("Success")) {
                                log.debug("Data is reset, hence deleting the mapping : " + redisAgentManager.hdel(Constants.PREVIEWDATA_AGENTID_MAP, pId));
                            }
                        }
                        
                        PreviewDataAuditLog previewDataAuditLog = previewDataAuditLogManager.getByPidAndAgentId(Long.valueOf(pId), agentUniqId);
                        log.debug("previewDataAuditLog from DB : "+previewDataAuditLog);
                        if (previewDataAuditLog != null) {
                            previewDataAuditLog.setAction(PreviewDataAuditLog.Action.RESET);
                            previewDataAuditLog.setDateUpdated(new Date());
                            previewDataAuditLogManager.save(previewDataAuditLog);
                        }
                    }
                    lResponse.setString("resp", resp);
                } else if ("tbSkipPreviewNumber".equals(lType)) {
                    String pId = aToken.getString("params");
                    if (pId != null && !pId.isEmpty()) {
                        resp = toolBarManager.tbSkipPreviewNumber(Long.valueOf(pId), "Skipped by:" + agentId);
                        if (resp.equalsIgnoreCase("Success")) {
                            log.debug("Data is skipped, hence deleting the mapping : " + redisAgentManager.hdel(Constants.PREVIEWDATA_AGENTID_MAP, pId));
                        }
                        
                        PreviewDataAuditLog previewDataAuditLog = previewDataAuditLogManager.getByPidAndAgentId(Long.valueOf(pId), agentUniqId);
                        log.debug("previewDataAuditLog from DB : "+previewDataAuditLog);
                        if (previewDataAuditLog != null) {
                            previewDataAuditLog.setAction(PreviewDataAuditLog.Action.SKIP);
                            previewDataAuditLog.setDateUpdated(new Date());
                            previewDataAuditLogManager.save(previewDataAuditLog);
                        }
                    }
                    lResponse.setString("resp", resp);
                } else if ("tbClosePreviewNumber".equals(lType)) {
                    String pId = aToken.getString("pId");
                    if (pId != null && !pId.isEmpty()) {
                        resp = toolBarManager.tbClosePreviewNumber(Long.valueOf(pId), "Closed by:" + agentId, aToken.getString("reason"), aToken.getString("remarks"));
                        if (resp.equalsIgnoreCase("Success")) {
                            log.debug("Data is closed, hence deleting the mapping : " + redisAgentManager.hdel(Constants.PREVIEWDATA_AGENTID_MAP, pId));
                        }
                        
                        PreviewDataAuditLog previewDataAuditLog = previewDataAuditLogManager.getByPidAndAgentId(Long.valueOf(pId), agentUniqId);
                        log.debug("previewDataAuditLog from DB : "+previewDataAuditLog);
                        if (previewDataAuditLog != null) {
                            previewDataAuditLog.setAction(PreviewDataAuditLog.Action.CLOSE);
                            previewDataAuditLog.setDateUpdated(new Date());
                            previewDataAuditLog.setDisposition(aToken.getString("reason"));
                            previewDataAuditLog.setComments(aToken.getString("remarks"));
                            previewDataAuditLogManager.save(previewDataAuditLog);
                        }
                    }
                    lResponse.setString("resp", resp);
                } else if ("tbGetCustomerData".equals(lType)) {
                    log.debug("Getting tbGetCustomerData invoked");
                    String dataId = aToken.getString("params");
                    log.debug("Fetching dataId from Token : " + dataId);
                    if (dataId.equals("") && StringUtils.isNotBlank(aToken.getString("monitorUcid"))) {
//                    if (StringUtils.equalsIgnoreCase(aToken.getString("callType"), "predictive") && StringUtils.isNotBlank(aToken.getString("monitorUcid"))) {
//                        List<Report> reports = reportManager.getReportByMonitorUcid(Long.valueOf(aToken.getString("monitorUcid")));
//                        if (!reports.isEmpty()) {
//                            dataId = "" + reports.get(0).getData_id();
//                            log.debug("Data id from reports:" + dataId + ", for monitor ucid:" + aToken.getString("monitorUcid"));
//                        } else {
//                            log.debug("Got no reports for monitorUcid:" + aToken.getString("monitorUcid"));
//                        }
                        String monitorUcid = aToken.getString("monitorUcid");
                        MessageFormat messageFormat = new MessageFormat("");
                        messageFormat.applyPattern(Constants.UCID_CALL_DETAILS);
                        dataId = redisAgentManager.hget(messageFormat.format(new Object[]{monitorUcid}), "dataId");
                        log.debug("Fetching data id from redis for : " + monitorUcid + " And dataId : " + dataId);
                    }
                    if (dataId != null && !dataId.isEmpty()) {
                        lResponse.setList("custData", toolBarManager.tbGetCustomerData(dataId));
                    }
                    lResponse.setString(resp, "success");
//                }
//
//            }
//
//            if (dataId != null && !dataId.isEmpty()) {
//                lResponse.setList("custData", toolBarManager.tbGetCustomerData(dataId));
//            }
//            lResponse.setString(resp, "success");
                } else if ("tbGetCallBackList".equals(lType)) {
                    List<CallbacksGrouped> groupedCallbackList = toolBarManager.tbGetGroupedCallBackList(username, agentId);
                    Gson gson = new GsonBuilder().create();
                    String gsonData = gson.toJson(groupedCallbackList);
                    log.debug("Gson grouped:" + gsonData);
//                lResponse.setList("callBackList", toolBarManager.tbGetCallBackList(username, agentId));
                    lResponse.setString("callBackList", gsonData);
                    lResponse.setString("date", DateUtil.getDateTime("dd-MM-yyyy", new Date()));
                    lResponse.setString("resp", "success");
                } else if ("deleteCallback".equals(lType)) {
                    try {
                        log.debug("Deleting CB:" + aToken.getString("cbId"));
                        toolBarManager.tbDeleteCallback(username, agentId, Long.valueOf(aToken.getString("cbId")));
                        lResponse.setString("resp", "Success");
                    } catch (Exception e) {
                        lResponse.setString("resp", "Fail");
                        log.error(e.getMessage(), e);
                    }
                } else if ("tbGetAgentPerformance".equals(lType)) {
                    log.debug("Agent performance for : " + agentId + " : " + username);
                    Map<String, Object> performance = reportManager.getAgentPerformance(userManager.getUserByUsername(username).getId(), agentUniqId);
                    if (performance != null) {
                        lResponse.setString("performance", new Gson().toJson(performance));
                        lResponse.setString("resp", "Success");
                    } else {
                        lResponse.setString("resp", "Fail");
                    }
                } else if ("tbGetAgentCallHist".equals(lType)) {
                    List<Report> list = toolBarManager.getAgentCallHistory(username, agentId);
                    if (list != null) {
                        User user = userManager.getUserByUsername(username);

                        for (Report report : list) {
                            report.setUser(null);
                            report.setCallDate(DateUtil.convertFromOneTimeZoneToOhter(report.getCallDate(), user.getServerTimezone(), user.getUserTimezone()));
                            report.setEndTime(DateUtil.convertFromOneTimeZoneToOhter(report.getEndTime(), user.getServerTimezone(), user.getUserTimezone()));
//                            report.setCallDate(DateUtil.convertDateToString(report.getCallDate(), "yyyy-MM-dd HH:mm:ss", report.getUser().getServerTimezone()));
                        }
                        lResponse.setString("callHist", new Gson().toJson(list));
                        lResponse.setString("resp", "Success");
                    } else {
                        lResponse.setString("resp", "Fail");
                    }
                } else if ("tbRescheduleCallback".equals(lType)) {
                    JsonObject jsonObject = toolBarManager.tbResechduleCallback(username, agentId, aToken.getString("cbTime"), Long.valueOf(aToken.getString("cbId")), aToken.getString("rsComment"), aToken.getString("callbacktz"));
                    log.debug("Reschedule response:" + jsonObject + " | Date:" + aToken.getString("cbTime") + " | id:" + Long.valueOf(aToken.getString("cbId")));
                    lResponse.setString("resp", jsonObject.get("status").getAsString());
                    lResponse.setString("message", jsonObject.get("message").getAsString());
                } else if ("tbFailCallback".equals(lType)) {
                    String resp1 = "Fail";
                    try {
                        resp1 = toolBarManager.tbFailCallback(username, agentId, Long.valueOf(aToken.getString("cbId")));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    lResponse.setString("resp", resp1);
                } else if ("tbCallBackDial".equals(lType)) {
                    log.debug("inside CallBack Dial");
                    String params = aToken.getInteger("params").toString();
                    log.debug("Params = " + params);
                    if (params != null && !params.isEmpty()) {
                        log.debug("Called CallBack Dial " + params);
                        resp = previewDialerManager.callBackDial(params);
                        log.debug("resp UUI: " + resp);
                        String respArray[] = resp.split(":");
                        if (respArray.length > 1) {
                            lResponse.setString("status", respArray[0]);
                            lResponse.setString("message", respArray[1]);
                        }
                        lResponse.setString("resp", resp);

                    }
                } else if (StringUtils.equalsIgnoreCase("tbGetTicketID", lType)) {
                    String randomNumber = "" + toolBarManager.generateTicketId(new Long(aToken.getString("ucid")));
//                String randomNumber = "" + toolBarManager.generateTicketId(1331874044920L);
                    log.info("Generated new ticket id:" + randomNumber);
                    lResponse.setString("resp", randomNumber);
                } else if (StringUtils.equalsIgnoreCase("tbGetTicketDetails", lType)) {
                    try {

                        String ticketId = aToken.getString("tikcetId");
                        log.info("Getting ticket details:" + ticketId);
                        JSONTicketDetails jSONTicketDetails = toolBarManager.getTicketDetails(new Long(StringUtils.trim(ticketId)), username);
                        String callDetails = new GsonBuilder().serializeNulls().create().toJson(jSONTicketDetails);
                        if (StringUtils.isNotEmpty(callDetails)) {
                            log.info("Ticket details :" + callDetails);
                            lResponse.setString("details", callDetails);
                            lResponse.setString("resp", "success");
                        } else {
                            lResponse.setString("resp", "fail");
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        lResponse.setString("resp", "fail");
                    }

                } else if (StringUtils.equalsIgnoreCase("tbGetTicketByPhone", lType)) {
                    String numberToSearch = aToken.getString("phoneNumber");
                    log.info("Number to search for tickets :" + numberToSearch);
                    String details = toolBarManager.getTicketDetailByPhone(username, numberToSearch);
                    if (StringUtils.isEmpty(details)) {
                        lResponse.setString("resp", "fail");
                    } else {
                        lResponse.setString("resp", "success");
                        lResponse.setString("details", details);
                    }
                } else if (StringUtils.equalsIgnoreCase("tbCheckManualDialStatus", lType)) {
                    log.debug("Check Manual Dial Status for UCID: " + aToken.getString("ucid"));
                    Command c = new CheckManualDialStatus(aToken.getString("ucid"));
                    resp = c.execute();
                    String[] s = resp.split(":");
                    if (s.length > 1) {
                        lResponse.setString("event", s[0]);
                        lResponse.setString("callStatus", s[1]);
                    }
                } else if (StringUtils.equalsIgnoreCase("tbGetQueueCount", lType)) {
                    log.debug("Getting call queue count:");
                    try {
                        lResponse.setInteger("queueCount", callQueueManager.getCallQueueCountForAgent(username, agentId));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else if (StringUtils.equalsIgnoreCase("tbResetPwd", lType)) {
                    log.debug("Password reset request..");
                    StatusMessage statusMessage = agentManager.updatePassword(aToken.getString("customer"), aToken.getString("agentId"), agentUniqId, aToken.getString("curPwd"), aToken.getString("newPwd"));
                    lResponse.setString("status", statusMessage.getStatus().toReadableString());
                    lResponse.setString("message", statusMessage.getMessage());
                } else if ("processClosed".equalsIgnoreCase(lType)) {
                    log.info("Client '" + aEvent.getSessionId() + "' disconnected.");
                    chatService.removeClientIdFromSess(aEvent.getSessionId(), aEvent.getId());
                } else if ("checkCustSess".equalsIgnoreCase(lType)) {
                    try {
                        String apiKey = aToken.getString("apiKey");
                        String did = aToken.getString("did");
                        String sessionIdToCheck = aEvent.getSessionId();
                        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionIdToCheck);
                        log.debug("Chat session details:" + chatSessionDetails);

                        if (chatSessionDetails == null) {//new chat customer
                            log.debug("custDetails---" + aToken.getMap("custDetails"));

                            Map<String, String> custDetails = (Map<String, String>) aToken.getMap("custDetails");

                            Campaign campaign = campaignManager.getCampaignsByDid(did, "Chat");

                            // ----> Chat started just now :/
                            chatService.setUpChatSession(sessionIdToCheck, custDetails.get("custId"), apiKey, campaign.getUser().getUsername(), custDetails.get("custName"), custDetails.get("custMail"), custDetails.get("custPhone"), aEvent.getId(), campaign.getCampaignId(), did, null, campaign.getUser().getId() + "" + new Date().getTime(), campaign.getUser().getId(),"Web","",custDetails.get("custId"), did);
                            chatService.addSessionState(sessionIdToCheck, ChatStates.CHAT_BOT);
                            if (!StringUtils.isBlank(custDetails.get("autoTimeout"))) {
                                chatService.updateChatSessionWithTimeout(sessionIdToCheck, new Long(custDetails.get("autoTimeout")));
                            }
                            chatService.saveChatDetails(sessionIdToCheck, ChatStates.CHAT_BOT, did, null);

                            lResponse.setString("resp", "new");

                            try {
                                chatBotService.askIvr(sessionIdToCheck, "", apiKey, did, aEvent.getId(), "welcome");
                            } catch (Exception ex) {
                                log.error(ex.getMessage(), ex);
                            }

                        } else if (chatSessionDetails.getAgentId() != null) {//cust already in chat with agent
                            chatService.addClientIdToSess(sessionIdToCheck, aEvent.getId());
                            log.debug(sessionIdToCheck + " Customer is chatting to:" + chatSessionDetails.getAgentId());
                            lResponse.setString("resp", "old");
                            lResponse.setString("sessId", sessionIdToCheck);
                            lResponse.setString("log", chatService.getChatJson(sessionIdToCheck));
                            lResponse.setString("chatCustName", chatSessionDetails.getChatCustName());
                            lResponse.setBoolean("custEnd", chatSessionDetails.isCustomerEnded());

                        } else if (chatSessionDetails.getChatState().equals(ChatStates.NEXT_FREE_AGENT)) {
                            log.debug("Fetching agent....for " + sessionIdToCheck);
//                    if (redisAgentManager.exists(chatService.getSessionClientIdMapKey(sessionIdToCheck))) {
//                        agentFinderService.findAgent(apiKey1, "", sessionIdToCheck, aEvent.getId(), did1, skill1);
//                    }
                            chatService.addClientIdToSess(sessionIdToCheck, aEvent.getId());

                            try {
                                chatBotService.askIvr(sessionIdToCheck, "", apiKey, did, aEvent.getId(), "cctransfer");
                            } catch (Exception ex) {
                                java.util.logging.Logger.getLogger(ChatHandlerServlet.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            lResponse.setString("resp", "fetchingAgent");
                            lResponse.setString("message", "We're trying to get an agent for you...");
                            lResponse.setString("log", chatService.getChatJson(sessionIdToCheck));

                        } else if (chatSessionDetails.getChatState().equals(ChatStates.CHAT_BOT)) {
                            chatService.addClientIdToSess(sessionIdToCheck, aEvent.getId());

                            log.debug(sessionIdToCheck + " Customer is chatting to IVR");
                            lResponse.setString("resp", "old");
                            lResponse.setString("sessId", sessionIdToCheck);
                            lResponse.setString("log", chatService.getChatJson(sessionIdToCheck));
                            log.debug("response--->" + lResponse);
                        }

                        lResponse.setString("agentId", agentId);
                        lResponse.setString("user", username);
                        log.debug("Sending response --- " + lResponse);
                        //  aEvent.sendToken(lResponse);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else if ("custchat".equals(lType)) {
                    final String sessionId = aEvent.getSessionId();
                    //String user_agentId = chatService.getAssociatedAgent(sessionId);
                    String text = aToken.getString("text");
                    String apiKey = aToken.getString("apiKey");
                    String did = aToken.getString("did");

                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
                    if (chatSessionDetails.getAgentId() != null) { //talking to agent
                        String agentWsId = redisAgentManager.getAgentWsId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
                        log.debug("sending message to agent::" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId() + " | ws id:" + agentWsId);
                        User u = userManager.getUser(chatSessionDetails.getCaUserId().toString());
                        log.debug("Agent msg server : " + u.getUrlMap().getLocalIp());
                        Token tokenResponse = TokenFactory.createToken();

                        tokenResponse.setString("type", "inchatmulti");
                        tokenResponse.setString("chatmsg", text);
                        tokenResponse.setString("chatCustSessionId", sessionId);
                        tokenResponse.setLong("timestamp", System.currentTimeMillis());
                        chatService.sendTokenToMsgServer(u.getUrlMap().getLocalIp(), sessionId, tokenResponse, true);
                        //tokenServer.sendToken(tokenServer.getConnector(agentWsId), tokenResponse);
                    } else {
                        try {
                            log.debug("sending message to IVR : " + text);
                            chatBotService.askIvr(sessionId, text, apiKey, did, aEvent.getId(), "text");
                        } catch (Exception ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    }
                    chatService.saveChatMessage(sessionId, false, System.currentTimeMillis(), text, "text", null);
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                    //aEvent.sendToken(lResponse);
                } else if ("multiAgentChatMsg".equals(lType)) {
                    // ---> This is chat message from agent to user.
                    try {
                        String text = aToken.getString("text");

                        String customer_sessionid = aToken.getString("chatCustSessionId");

                        log.debug("Customer session id:" + customer_sessionid);
                        if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, customer_sessionid)) {
                            log.debug("--> This  is FB Chat bro...");
                            facebookChatService.sendMsgToUser(customer_sessionid, text, "text", null);//customer_sessionid --> FB sender ID
                        } else {
                            Token tokenResponse = TokenFactory.createToken();
                            tokenResponse.setType("chatMsg");
                            tokenResponse.setString("agent", agentId);
                            tokenResponse.setString("msg", text);
                            tokenResponse.setLong("ts", System.currentTimeMillis());
                            log.debug("Regular chat:...");
                            chatService.sendTokenToMsgServer(appProperty.getChatClientMsgSrvr(), customer_sessionid, tokenResponse, false);

                        }
                        long timestamp = System.currentTimeMillis();
                        chatService.saveChatMessage(customer_sessionid, true, timestamp, text, "text", agentId);
                        boolean isFirstReply = aToken.getBoolean("isFirstReply") != null ? aToken.getBoolean("isFirstReply") : false;
                        log.debug("is first reply ? " + isFirstReply);
                        if (isFirstReply) {
                            chatService.updateChatDetailsWithAgentTta(customer_sessionid, timestamp);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    lResponse.setLong("timestamp", System.currentTimeMillis());
                    lResponse.setString("status", "Success");
                    //aEvent.sendToken(lResponse);
                } else if ("disposeChat".equals(lType)) {

                    try {

                        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(aToken.getString("sessionId"));
                        if (chatSessionDetails != null && !chatSessionDetails.isCustomerEnded() && chatSessionDetails.getMonitorUcid().equals(aToken.getString("monitorUcid"))) {//agent ended chat.
                            log.debug("Agent initiated end chat.");
                            chatService.UpdateChatDetails_agentEnd(aToken.getString("sessionId"), aToken.getString("disp"), aToken.getString("comment"), chatSessionDetails.getAgentId());

                            // chatService.tearDownChatSession(customer_sessionid);
                            new ChatOffCommand(eventManager, agentManager, chatService, redisAgentManager, aToken.getString("sessionId"), chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId(), Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))), false, appProperty.getChatClientMsgSrvr(), facebookChatService).chatOff();
                        } else {//customer has already ended chat, just dispose.
                            log.debug("Customer already ended chat just update disp.");
                            chatService.updateDisposition(aToken.getString("sessionId"), aToken.getString("disp"), aToken.getString("comment"), aToken.getString("monitorUcid"));
                            chatService.tearDownChatSession(aToken.getString("sessionId"));
                            //decrement active chat count for agent
                            try {
                                agentManager.decrementAgentChatSessionsInDB(Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))));
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                            if (chatSessionDetails != null && chatSessionDetails.getChatState().equals(ChatStates.AGENT)) {
                                //Update last busy event misc. details with count of chats handled in this busy session
                                //do below only after tear down, since we are removing the agent-session mapping in tear down
                                if (chatService.getActiveChatSessionsCountByAgent(username, agentId) <= 0) {
                                    log.debug("The last supper \uD83C\uDF72 \uD83C\uDF72 for the agent : so update event with chat count");
                                    eventManager.updateChatSessionsCountInBusyEvent(username, Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))), agentId, chatService.getChatCountHandledSofar(username, agentId));
                                }
                            }
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    lResponse.setString("resp", "Success");
                    //aEvent.sendToken(lResponse);

                } else if ("forceDisposeChat".equalsIgnoreCase(lType)) {
                    log.debug("Force disposing chat " + aToken.getString("sessionId"));
                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(aToken.getString("sessionId"));
                    chatService.updateDisposition(aToken.getString("sessionId"), aToken.getString("disp"), aToken.getString("comment"), chatSessionDetails.getMonitorUcid());
                    chatService.tearDownChatSession(aToken.getString("sessionId"));
                } else if ("pauseAfterSession".equalsIgnoreCase(lType)) {
                    log.debug("Agent will be paused after current session | " + aToken.getString("agentUniqId")
                            + agentManager.pauseChatSessions(Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId")))));
                } else if ("custEndChat".equalsIgnoreCase(lType)) {
                    chatService.custEndChat(aEvent.getSessionId());
                    lResponse.setString("resp", "Success");
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                    // aEvent.sendToken(lResponse);
                } else if ("custImgMsg".equals(lType)) {
                    final String sessionId = aEvent.getSessionId();
                    String url = aToken.getString("imageUrl");
                    String apiKey = aToken.getString("apiKey");
                    String did = aToken.getString("did");

                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
                    if (chatSessionDetails.getAgentId() != null) { //talking to agent
                        String agentWsId = redisAgentManager.getAgentWsId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
                        log.debug("sending IMAGE to agent::" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId() + " | ws id:" + agentWsId);
//                        Agent a = agentManager.getAgentByAgentId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
                        Agent a = agentManager.getAgentByAgentIdUserId(chatSessionDetails.getCaUserId(), chatSessionDetails.getAgentId());
                        User u = userManager.getUser(chatSessionDetails.getCaUserId().toString());

                        log.debug("Agent msg server : " + u.getUrlMap().getLocalIp());
                        Token tokenResponse = TokenFactory.createToken();

                        tokenResponse.setString("type", "inchatmulti");
                        tokenResponse.setString("chatmsg", "<a href='" + url + "' target='_blank'><img style='margin-top: 3px;margin-bottom: 3px;max-width: 99%;max-height: 500px;' src='" + url + "' alt='Image'></a>");
                        tokenResponse.setString("chatCustSessionId", sessionId);
                        tokenResponse.setLong("timestamp", System.currentTimeMillis());
                        //tokenServer.sendToken(tokenServer.getConnector(agentWsId), tokenResponse);
                        chatService.sendTokenToMsgServer(u.getUrlMap().getLocalIp(), sessionId, tokenResponse, true);
                    } else {
                        try {
                            log.debug("sending Image url to IVR : " + url);
                            chatBotService.askIvr(sessionId, url, apiKey, did, aEvent.getId(), "image");
                        } catch (Exception ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    }
                    chatService.saveChatMessage(sessionId, false, System.currentTimeMillis(), url, "image", null);
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                    //aEvent.sendToken(lResponse);
                } else if ("custMediaMsg".equals(lType)) { //media msg from customer(chatwidget) to agent
                    final String sessionId = aEvent.getSessionId();
                    String url = aToken.getString("url");
                    String apiKey = aToken.getString("apiKey");
                    String did = aToken.getString("did");
                    String type = aToken.getString("mediaType");

                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
                    if (chatSessionDetails.getAgentId() != null) { //talking to agent
                        log.debug("sending Media to agent::" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId());
                        User u = userManager.getUser(chatSessionDetails.getCaUserId().toString());
                        log.debug("Agent msg server : " + u.getUrlMap().getLocalIp());

                        Token tokenResponse = TokenFactory.createToken();
                        tokenResponse.setString("type", "custMediaMsg");
                        tokenResponse.setString("mediaType", type);
                        tokenResponse.setString("url", url);
                        tokenResponse.setString("chatCustSessionId", sessionId);
                        tokenResponse.setLong("timestamp", System.currentTimeMillis());
                        //tokenServer.sendToken(tokenServer.getConnector(agentWsId), tokenResponse);
                        chatService.sendTokenToMsgServer(u.getUrlMap().getLocalIp(), sessionId, tokenResponse, true);
                    } else {
                        try {
                            log.debug("sending Image url to IVR : " + url);
                            chatBotService.askIvr(sessionId, url, apiKey, did, aEvent.getId(), type);
                        } catch (Exception ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    }
                    chatService.saveChatMessage(sessionId, false, System.currentTimeMillis(), url, type, null);
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                    //aEvent.sendToken(lResponse);
                } else if ("agentMediaMsg".equals(lType)) { //media msg from agent(agent toolbar) to customer
                    try {
                        String url = aToken.getString("url");
                        String mediaType = aToken.getString("mediaType");
                        String customer_sessionid = aToken.getString("chatCustSessionId");
                        String fileName = aToken.getString("fileName");
                        log.debug("Customer session id:" + customer_sessionid);
                        if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, customer_sessionid)) {
                            log.debug("--> This  is FB Chat bro...");
                            facebookChatService.sendMsgToUser(customer_sessionid, url, mediaType, fileName);//customer_sessionid --> FB sender ID
                        } else {
                            Token tokenResponse = TokenFactory.createToken();
                            tokenResponse.setType("mediaMsg");
                            tokenResponse.setString("agent", agentId);
                            tokenResponse.setString("msg", url);
                            tokenResponse.setString("mediaType", mediaType);
                            tokenResponse.setString("fileName", fileName);
                            tokenResponse.setLong("ts", System.currentTimeMillis());
                            log.debug("Regular chat:...");
                            chatService.sendTokenToMsgServer(appProperty.getChatClientMsgSrvr(), customer_sessionid, tokenResponse, false);
                        }
                        chatService.saveChatMessage(customer_sessionid, true, System.currentTimeMillis(), url, mediaType, agentId);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    lResponse.setLong("timestamp", System.currentTimeMillis());
                    lResponse.setString("status", "Success");
                    //aEvent.sendToken(lResponse);
                } else if ("storeUserInfo".equals(lType)) {
                    Map<String, String> custDetails = (Map<String, String>) aToken.getMap("custDetails");
                    //update in redis and DB
                    chatService.updateChatSessionWithUserInfo(aEvent.getSessionId(), custDetails.get("custName"), custDetails.get("custMail"), custDetails.get("custPhone"), custDetails.get("extraData"));
                    try {
                        chatBotService.askIvr(aEvent.getSessionId(), "", aToken.getString("apiKey"), aToken.getString("did"), aEvent.getId(), "storeUserInfo");
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                    chatService.saveChatMessage(aEvent.getSessionId(), false, System.currentTimeMillis(), new Gson().toJson(custDetails), "storeUserInfo", null);
                    lResponse.setString("resp", "Success");
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                    //aEvent.sendToken(lResponse);
                } else if ("checIfAgentIsChatting".equalsIgnoreCase(lType)) {
                    log.debug("Agent is chatting with clients :" + chatService.getActiveChatSessionsCountByAgent(username, agentId));
                    lResponse.setString("resp", "Success");
                    lResponse.setLong("count", chatService.getActiveChatSessionsCountByAgent(username, agentId) + chatService.getActiveCallingChatSessionsCountByAgent(username, agentId));
                    lResponse.setList("sessionList", new ArrayList(chatService.getAgentChatSessions(username, agentId)));
                    lResponse.setList("callingSessionList", new ArrayList(chatService.getAgentCallingChatSessions(username, agentId)));
                    //aEvent.sendToken(lResponse);
                } else if ("fetchChatSession".equalsIgnoreCase(lType)) {
                    String sessionId = aToken.getString("sessionId");
                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
                    chatSessionDetails.setStateStartTime((System.currentTimeMillis() - chatSessionDetails.getStateStartTime()) / 1000);
                    if (chatSessionDetails.getChatState().equals(ChatStates.CALLING)) {
                        lResponse.setString("state", "calling");
                        lResponse.setString("custName", chatSessionDetails.getChatCustName());
                        lResponse.setString("sessionId", sessionId);
                        lResponse.setString("monitorUcid", chatSessionDetails.getMonitorUcid());
                        lResponse.setString("did", chatSessionDetails.getDid());
                        lResponse.setLong("campaignId", chatSessionDetails.getCampaignId());
                        lResponse.setString("clientId", sessionId);
                        lResponse.setString("skillName", chatSessionDetails.getSkill());
                        lResponse.setString("currentChatHist", chatService.getChatJson(sessionId));
                        lResponse.setString("sessionDetails", new JsonUtil<ChatSessionDetails>().convertToJson(chatSessionDetails));
                    } else {
                        Campaign campaign = campaignManager.get(chatSessionDetails.getCampaignId());
                        lResponse.setString("state", "busy");
                        lResponse.setString("sessionId", aToken.getString("sessionId"));
                        lResponse.setString("sessionDetails", new JsonUtil<ChatSessionDetails>().convertToJson(chatSessionDetails));
                        lResponse.setString("currentChatHist", chatService.getChatJson(aToken.getString("sessionId")));
                        lResponse.setString("previousChatHist", chatService.getChatHistoryByPhoneNumberAndUserId(chatSessionDetails.getPhoneNumber(), campaign.getUser().getId()));

                        List<Disposition> dispositions = dispositionManager.getDispositionsByCampaign(chatSessionDetails.getCampaignId());
                        Map<String, String> dispositionMap = new LinkedHashMap<>();
                        for (Disposition disposition : dispositions) {
                            //dispositionNames += (dispositionNames != null && !dispositionNames.equals("") ? "," : "") + (disposition.isActive() ? disposition.getReason() : "");
                            if (StringUtils.isNotBlank(disposition.getReason())) {
                                dispositionMap.put(disposition.getReason(), disposition.getReason());
                            }
                        }
                        lResponse.setString("dispMap", new Gson().toJson(dispositionMap));
                        lResponse.setString("screenPop", campaignManager.get(chatSessionDetails.getCampaignId()).getScreenPopUrl());
                    }
                    //aEvent.sendToken(lResponse);
                } else if ("agentStartedTyping".equalsIgnoreCase(lType)) {
                    String customer_sessionid = aToken.getString("chatCustSessionId");
                    if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, customer_sessionid)) {
                        log.debug("--> This  is FB Chat bro...");
                        facebookChatService.sendMsgToUser(customer_sessionid, "", "agentStartedTyping", null);//customer_sessionid --> FB sender ID
                    } else {
                        Token tokenResponse = TokenFactory.createToken();
                        tokenResponse.setType("agentStartedTyping");
                        tokenResponse.setString("agent", agentId);
                        log.debug("Agent started typing for session : " + customer_sessionid);
                        chatService.sendTokenToMsgServer(appProperty.getChatClientMsgSrvr(), customer_sessionid, tokenResponse, false);
                    }
                    lResponse.setString("resp", "Success");
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                } else if ("agentNoLongerTyping".equalsIgnoreCase(lType)) {
                    String customer_sessionid = aToken.getString("chatCustSessionId");
                    if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, customer_sessionid)) {
                        log.debug("--> This  is FB Chat bro...");
                        facebookChatService.sendMsgToUser(customer_sessionid, "", "agentNoLongerTyping", null);//customer_sessionid --> FB sender ID
                    } else {
                        Token tokenResponse = TokenFactory.createToken();
                        tokenResponse.setType("agentNoLongerTyping");
                        tokenResponse.setString("agent", agentId);
                        log.debug("Agent no longer typing for session : " + customer_sessionid);
                        chatService.sendTokenToMsgServer(appProperty.getChatClientMsgSrvr(), customer_sessionid, tokenResponse, false);
                    }
                    lResponse.setString("resp", "Success");
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                } else if ("storeCustomerFeedback".equalsIgnoreCase(lType)) {
                    String customer_sessionid = aEvent.getSessionId();
                    Map<String, String> feedback = (Map<String, String>) aToken.getMap("feedback");
                    chatService.storeFeedback(customer_sessionid, new Gson().toJson(feedback));
                    lResponse.setString("resp", "Success");
                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    log.debug("Sending response --- " + lResponse);
                } else if ("regSipChat".equalsIgnoreCase(lType)) {
                    String apiKey = aToken.getString("apiKey");
                    String did = aToken.getString("did");
                    String sessionIdToCheck = aEvent.getSessionId();
                    String sipNumber = aToken.getString("sipNumber");
                    String usId = aToken.getString("usId");
                    log.debug("custDetails---" + aToken.getMap("custDetails"));
                    Map<String, String> custDetails = (Map<String, String>) aToken.getMap("custDetails");

                    Campaign campaign = campaignManager.getCampaignsByDid(did);
                    log.debug("got campaign -- > " + campaign);
                    // ----> Chat started just now :/
                    chatService.setUpChatSession(sessionIdToCheck, custDetails.get("custId"), apiKey, campaign.getUser().getUsername(), custDetails.get("custName"), custDetails.get("custMail"), custDetails.get("custPhone"), usId, campaign.getCampaignId(), did, null, campaign.getUser().getId() + "" + new Date().getTime(), campaign.getUser().getId(),"","",custDetails.get("custId"), did);
                    chatService.addSessionState(sessionIdToCheck, ChatStates.CHAT_BOT);
//                    chatService.saveChatDetails(sessionIdToCheck, ChatStates.CHAT_BOT, did, null);
                    redisAgentManager.hset("ca:sip-sessions", sipNumber, sessionIdToCheck);
                    redisAgentManager.sadd("ca:sip-chat-sessions", sessionIdToCheck);
                    lResponse.setString("resp", "success");
//                    aEvent.sendToken(lResponse);
                } else if ("initChatOnCall".equals(lType)) {
                    String callerId = redisAgentManager.sismember(Constants.ENCRYPT_FIELD,username) ? SecurityUtil.decryptUsingAes256Key(aToken.getString("callerId")) : aToken.getString("callerId");
                    String usId = aToken.getString("usId");
                    if (redisAgentManager.hexists("ca:sip-sessions", callerId)) {
                        lResponse.setString("resp", "success");
//                        aEvent.sendToken(lResponse);
                        chatOnCallService.initChatOnCall(agentId, Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))), usId, callerId, aToken.getString("did"), aToken.getString("skill"), aToken.getString("monitorUcid"));
                    } else {
                        lResponse.setString("resp", "fail");
//                        aEvent.sendToken(lResponse);
                    }
                } else if ("tbGetChatSkills".equals(lType)) {
                    String did = aToken.getString("did");
                    lResponse.setString("status", "Success");
                    lResponse.setList("message", chatService.getSkillTransferList(did));
                } else if ("tbGetChatAgents".equals(lType)) {
                    String did = aToken.getString("did");
                    lResponse.setString("status", "Success");
                    lResponse.setList("message", agentManager.getChatTransferAgentList(did, agentUniqId));
                } else if ("tbChatTransfer".equals(lType)) {
                    String sessionId = aToken.getString("chatCustSessionId");
                    String params = aToken.getString("params");
                    String did = aToken.getString("did");
                    String apiKey = aToken.getString("apiKey");
                    Boolean isDecline = aToken.getBoolean("isDecline");
                    if (params != null && !params.isEmpty()) {
                        String paramKey[] = org.apache.commons.lang3.StringUtils.splitPreserveAllTokens(params, ",");
                        String transferType = paramKey[0];
                        String transferTo = paramKey[1];
                        AgentToolbarCommand<AgentCommandStatus> transferCommand = null;

                        if (transferType.equals("1")) { //skill transfer
                            transferCommand = new ChatSkillTransferCommand(username, agentId, agentUniqId, sessionId, aEvent.getId(), apiKey, did, transferTo, isDecline, chatTransferService);
                        } else if (transferType.equals("2")) { //agent transfer
                            transferCommand = new ChatAgentTransferCommand(username, agentId, agentUniqId, transferTo, sessionId, aEvent.getId(), apiKey, did, isDecline, chatTransferService);
                        } else {
                            log.debug("received incorrect transfer type : " + transferType);
                            lResponse.setString("status", "Fail");
                        }
                        AgentCommandStatus commandStatus = agentCommandExecutor.executeCommand(transferCommand);
                        log.debug("--> Command :" + transferCommand + " | Response:" + commandStatus);
                        lResponse.setString("status", commandStatus.getStatus().toReadableString());
                        lResponse.setString("message", commandStatus.getMessage());
                    }
                } else if ("tbAcceptChat".equals(lType)) {
                    String apiKey = aToken.getString("apiKey");
                    String sessionId = aToken.getString("chatCustSessionId");
                    String monitorUcid = aToken.getString("monitorUcid");
                    String did = aToken.getString("did");
                    String campaignId = aToken.getString("campaignId");
                    String clientId = aToken.getString("clientId");
                    String custName = aToken.getString("custName");
                    String skillName = aToken.getString("skillName");
                    Campaign campaign = campaignManager.get(new Long(campaignId));
                    User user = userManager.getUserByApiKey(apiKey);
                    new ChatOnCommand(monitorUcid, apiKey, did, new Long(campaignId), sessionId, clientId, agentId, agentUniqId, aEvent.getId(), custName, user, campaign, redisAgentManager, chatService, agentManager, dispositionManager, eventManager, facebookChatService).chatOn();
                    chatService.addSessionState(sessionId, ChatStates.AGENT);
                    chatService.UpdateChatDetailsSystemEnd(sessionId, null, null);
                    chatService.saveChatDetails(sessionId, ChatStates.AGENT, did, skillName);
                    lResponse.setString("status", "success");
                } else {
                    handledHere = false;
                    log.debug("@#@#@#@# Bad token:" + lType + "| NS:" + (lNS) + " | " + aToken);
                    // if unknown type in this namespace, return corresponding error message
                    lResponse.setInteger("code", -1);
                    lResponse.setString("msg", "Token type '" + lType + "' not supported in namespace '" + lNS + "'.");
                }

                lResponse.setString("agentId", agentId);
                lResponse.setString("user", username);

                // After sending the token check if we need inform Dialer or Not....
                if (informDialer) {
                    previewDialerManager.informDialer(username, agentId, StringUtils.isEmpty(aToken.getString("campId")) ? null : Long.valueOf(aToken.getString("campId")));
                }
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return lResponse.getMap();

    }

    public void processClosed(WebSocketServerEvent aEvent) {
        try {
            log.info("Client '" + aEvent.getSessionId() + "' disconnected.");
            chatService.removeClientIdFromSess(aEvent.getSessionId(), aEvent.getConnector().getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    public void init() throws ServletException {
        super.init();
        processOpened();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Got get request.......");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Handles Tokens";
    }// </editor-fold>
}

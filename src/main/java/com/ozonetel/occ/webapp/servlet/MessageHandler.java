package com.ozonetel.occ.webapp.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallbacksGrouped;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.JSONTicketDetails;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentCommandExecutor;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentToolbarCommand;
import com.ozonetel.occ.service.AgentTransferManager;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.ManualDialService;
import com.ozonetel.occ.service.OCCManager;
import com.ozonetel.occ.service.PhoneTransferManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.SkillTransferManager;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.ToolBarManager;
import com.ozonetel.occ.service.TransferNumberManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.command.AgentConferenceCommand;
import com.ozonetel.occ.service.command.BlindAgentTransferCommand;
import com.ozonetel.occ.service.command.BlindPhoneTransferCommand;
import com.ozonetel.occ.service.chat.impl.ChatServiceImpl;
import com.ozonetel.occ.service.command.ConsultativeAgentTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldAgentTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldPhoneTransferCommand;
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
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.DateUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketPacket;
//import org.jwebsocket.appserver.WebSocketHttpSessionMerger;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author aschulze
 */
public class MessageHandler extends HttpServlet implements WebSocketServerTokenListener {

    private static Logger log = Logger.getLogger(MessageHandler.class);
    protected static OCCManager occManager;
    protected static ToolBarManager toolBarManager;
    protected static PreviewDialerManager previewDialerManager;
    protected static CampaignManager campaignManager;
    protected static AgentManager agentManager;
    protected static PreviewDataManager previewDataManager;
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
    private ReportManager reportManager;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("This session: " + request.getSession().getId());
//            out.println("Http sessions: " + WebSocketHttpSessionMerger.getHttpSessionsCSV());
//            out.println("WebSocket sessions: " + WebSocketHttpSessionMerger.getWebSocketSessionsCSV());
        } finally {
            out.close();
        }
    }

    @Override
    public void init() {
        log.info("Adding servlet '" + getClass().getSimpleName() + "' to WebSocket listeners...");
        TokenServer lServer = (TokenServer) JWebSocketFactory.getServer("ts0");
        if (lServer != null) {
//			lServer.addListener(this);
        }

    }

    /*
     * @Override public void processOpened(WebSocketServerEvent aEvent) {
     * log.info("Opened WebSocket session: " +
     * aEvent.getSession().getSessionId()); // if a new web socket connection
     * has been started, // update the session tables accordingly
     * WebSocketHttpSessionMerger.addWebSocketSession(aEvent.getSession()); }
     *
     * @Override public void processPacket(WebSocketServerEvent aEvent,
     * WebSocketPacket aPacket) { log.info("Received WebSocket packet: " +
     * aPacket.getASCII()); }
     *
     * @Override public void processToken(WebSocketServerTokenEvent aEvent,
     * Token aToken) { log.info("Received WebSocket token: " +
     * aToken.toString()); }
     *
     * @Override public void processClosed(WebSocketServerEvent aEvent) {
     * log.info("Closed WebSocket session: " +
     * aEvent.getSession().getSessionId()); // if a web socket connection has
     * been terminated, // update the session tables accordingly
     * WebSocketHttpSessionMerger.removeWebSocketSession(aEvent.getSession()); }
     */
    public void processOpened(WebSocketServerEvent aEvent) {
        log.debug("in message handler----");
        log.info("Client '" + aEvent.getSessionId() + "' connected.");

//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        occManager = (OCCManager) webApplicationContext.getBean("occManager");
        toolBarManager = (ToolBarManager) webApplicationContext.getBean("toolBarManager");
        previewDialerManager = (PreviewDialerManager) webApplicationContext.getBean("previewDialerManager");
        previewDataManager = (PreviewDataManager) webApplicationContext.getBean("previewDataManager");
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

        occManager.initialize();
        toolBarManager.initialize();
        previewDialerManager.initialize();
    }

    public void processPacket(WebSocketServerEvent aEvent, WebSocketPacket aPacket) {
        // here you can process any non-token low level message, if desired
    }

    public void processToken(WebSocketServerTokenEvent aEvent, Token aToken) {

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
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        previewDialerManager = (PreviewDialerManager) webApplicationContext.getBean("previewDialerManager");
        campaignManager = (CampaignManager) webApplicationContext.getBean("campaignManager");
        try {
            if (username == null || agentId == null || lType.equalsIgnoreCase("ping")) {
                Token lResponse1 = aEvent.createResponse(aToken);
                lResponse1.setInteger("code", -1);
                lResponse1.setString("msg", "Token type '" + lType + "' not supported in namespace '" + lNS + "'.");
                //assuming after this statement processing stops.
                aEvent.sendToken(lResponse1);

            } else // check if token has a type and a matching namespace
            {
                if (lType != null && "ozonetel.cloudagent".equals(lNS) && !lType.equalsIgnoreCase("response")) {
                    // create a response token
                    log.info("Client '" + aEvent.getSessionId() + "' sent Token: '" + aToken.toString() + "'.");
                    Token lResponse = aEvent.createResponse(aToken);
                    if ("getInfo".equals(lType)) {
                        // if type is "getInfo" return some server information
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
                            log.debug("ToolBarManager=" + toolBarManager);
                            log.debug("AEVENT=" + aEvent.toString());
                            log.debug("ToolBarManager=" + toolBarManager);
                            Map<String, String> response = toolBarManager.tbAgentReconnect(aEvent.getSessionId(), usId, username, agentId, agentUniqId, phoneNumber, mode, aToken.getString("event"),(aToken.getString("ucid")!=null ? new Long(aToken.getString("ucid")) : null));

                            lResponse.setString("status", response.get("status"));
                            lResponse.setString("mode", response.get("mode"));
                            lResponse.setString("idleTime", response.get("idleTime"));
                            lResponse.setString("latestToken", response.get("eventData"));
                            lResponse.setLong("chatCount", chatService.getActiveChatSessionsCountByAgent(username, agentId));

                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }

                    } else if ("tbAgentLogin".equals(lType)) {
                        try {
                            log.debug("agent log in request");
                            String usId = aToken.getString("usId");

                            //----Chat test-------
                            chatAgentId = usId;
                            log.debug("Chat agentId:" + chatAgentId + "|" + aEvent.getConnector().getSession().getSessionId());
                            //----Chat test-------
                            boolean reconnect = aToken.getBoolean("reconnect");

//                log.debug("[tbAgentLogin][usId]:[" + usId + "]");
                            Event.AgentMode mode = null;
                            try {
                                mode = Event.AgentMode.valueOf(StringUtils.trim(StringUtils.upperCase(aToken.getString("params"))));
                            } catch (IllegalArgumentException e) {
                                log.error("Error setting agent mode in Event(Login Action/Reconnect:" + reconnect + " ) ->" + e.getMessage(), e);
                            }
                            JsonObject jsonObject = null;
                            try {
                                jsonObject = toolBarManager.tbAgentLogin(username, agentId, agentUniqId, phoneNumber, aEvent.getSessionId(), usId, reconnect, mode, "I don't know your IP");
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
//                String[] response = resp.split("~");

                            if (jsonObject != null) {
//                    if (response[0].contains("Success")) {
                                if (StringUtils.equalsIgnoreCase(jsonObject.get("status").getAsString(), "Success")) {
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

//                            lResponse.setString("tktCustomer", "" + (userManager.hasTicketRole(username) ? 1 : 0));
//                            lResponse.setString("hr", "" + (userManager.hasRole(username, Constants.HOLD_ROLE) ? 1 : 0));
//                            lResponse.setString("cr", "" + (userManager.hasRole(username, Constants.CONFERENCE_ROLE) ? 1 : 0));
//                            lResponse.setString("mr", "" + (userManager.hasRole(username, Constants.MUTE_ROLE) ? 1 : 0));
//                            lResponse.setString("chtr", "" + (userManager.hasRole(username, Constants.CONSULTATIVEHOLDTRFR_ROLE) ? 1 : 0));
//                     lResponse.setList("dispList", dispNames);
                                } else {
                                    lResponse.setString("resp", resp);
                                }
                            } else {
                                lResponse.setString("resp", resp);
                            }

                            if (!resp.contains("Error")) {
                                lResponse.setString("agentStatus", "AUX");
                            }

                        } catch (Exception e) {
                            log.error(e.getMessage(), e);

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
                        if (!resp.contains("Error")) {
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

                            new Thread(new Runnable() {
                                public void run() {
                                    toolBarManager.alertAgentExceededPauseTime(agentUniqId, username, agentId, reason, Integer.valueOf(to));
                                }
                            }).start();
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
                    } else if ("tbGetDispositions".equals(lType)) {

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
//                String pauseValues = aToken.getString("pause");
                        if (params != null && !params.isEmpty()) {
                            String paramKey[] = StringUtils.splitPreserveAllTokens(params, ",");
//                    log.info("All params:" + Arrays.asList(paramKey));
                            String dataId = paramKey[0];
                            String dispositionCode = paramKey[1];
                            String callBackTime = paramKey[2];
                            final String ucid = paramKey[3];
                            String did = paramKey[4];
                            final String callerID = paramKey[5];
                            String uui = paramKey[6];
//                            String skillName = paramKey[7];
                            String ticketId = null;
                            String ticketType = null;
                            final String ticketStatus;
                            final String ticketDesc;
                            final String ticketCmt;

//                    log.info(":::::Get Skill Name as  :" + skillName);
                            if (paramKey.length > 7 && occManager.getUserManager().hasTicketRole(username)) {
                                ticketId = paramKey[7];
                                ticketType = paramKey[8];
                                ticketStatus = paramKey[9];
//                            if (paramKey.length > 8 && occManager.getUserManager().hasTicketRole(username)) {
//                                ticketId = paramKey[8];
//                                ticketType = paramKey[9];
//                                ticketStatus = paramKey[10];
                                ticketDesc = aToken.getString("tktDesc");
                                ticketCmt = aToken.getString("tktCmnt");
                            } else {
                                ticketStatus = null;
                                ticketDesc = null;
                                ticketCmt = null;
                            }

//                    log.info("--------->Setting dispositions and ticket ID :" + ticketId);
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
//                                resp = toolBarManager.setDisposition(agentUniqId, dataId, dispositionCode, callBackTime, ucid, did, dispComments, agentId, username, refid);
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
                                            aToken.getString("ucid"), aToken.getString("did"), aToken.getString("callerId"), telephonyManager);
                                    break;
                                case 3: // ----> Phone conference
                                    String participant = aToken.getString("participant");
                                    if (StringUtils.contains(participant, "~")) {
                                        participant = StringUtils.splitPreserveAllTokens(participant, "~")[1];
                                    }

                                    agentToolbarCommand = new PhoneConferenceCommand(username, agentId, null, participant, Boolean.valueOf(aToken.getString("sip")), aToken.getString("monitorUcid"),
                                            aToken.getString("ucid"), aToken.getString("did"), aToken.getString("callerId"), telephonyManager);
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
                            log.debug("CampaignId = " + aToken.getString("campignId") + " MonitorUCID = " + aToken.getString("monitorUcid") + " username = " + aToken.getString("username"));
                            Campaign campaign = campaignManager.get(Long.parseLong(aToken.getString("campignId")));
                            String appAudioURL = null;
//                            String appAudioURL = campaign.getAppAudioFiles().iterator().next().getAudio_url();
//                            AgentCommandStatus agentCommandStatus = agentCommandExecutor.executeCommand(
//                                    new HoldCommand(username, agentUniqId, aToken.getString("agentNumber"), agentId, new BigInteger(aToken.getString("monitorUcid")),
//                                            new BigInteger(aToken.getString("ucid")), aToken.getString("phoneNumberToHold"),
//                                            aToken.getString("did"), new BigInteger(aToken.getString("campignId")),
//                                            telephonyManager));
                            AgentCommandStatus agentCommandStatus = agentCommandExecutor.executeCommand(
                                    new HoldCommand(username, agentUniqId, aToken.getString("agentNumber"), agentId, new BigInteger(aToken.getString("monitorUcid")),
                                            new BigInteger(aToken.getString("ucid")), aToken.getString("phoneNumberToHold"),
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
                            AgentCommandStatus agentCommandStatus = agentCommandExecutor.executeCommand(new UnHoldCommand(username, agentUniqId, agentId, new BigInteger(aToken.getString("monitorUcid")), new BigInteger(aToken.getString("ucid")), aToken.getString("phoneNumberToUnHold"), aToken.getString("did"), telephonyManager));

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
                                    aToken.getString("did"), aToken.getString("agentNumber"), aToken.getString("customerNumber"), telephonyManager));

                            lResponse.setString("status", response.getStatus().toReadableString());
                            lResponse.setString("message", response.getMessage());
                        } catch (Exception e) {
                            lResponse.setString("status", Status.ERROR.toReadableString());
                            lResponse.setString("message", "Drop failed");
                            log.error(e.getMessage(), e);
                        }
                    } else if ("tbSetTransfer".equals(lType)) {
//914030952023,18135159647326141,3,Pavan_Land~04030247125,,3
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
                                String customerNumber = aToken.getString("custNumber");
                                AgentToolbarCommand<AgentCommandStatus> transferCommand = null;
                                String receiverAgentId = transferId;
                                String campId = aToken.getString("campId");
                                Campaign campaign = campaignManager.get(Long.parseLong(campId));
                                String appAudioURL = null;
//                        try{
//                        appAudioURL = campaign.getAppAudioFiles().iterator().next().getAudio_url();
//                        }catch(NoSuchElementException e){
//                            log.debug("No Audio URL Found for the CampaignID : "+aToken.getString("campignId")+" with monitorUcid : "+aToken.getString("monitorUcid"));
//                        }
                                log.debug("appAudioURL : " + appAudioURL);
                                if (transferType == 1) {//Agent 
                                    switch (blindTransfer) {
                                        case 1: //Blind
                                            transferCommand = new BlindAgentTransferCommand(username, agentId, monitorUcid, ucid, did, receiverAgentId, customerNumber, agentTransferManager, appAudioURL);
                                            break;
                                        case 2:// Consultative
                                            transferCommand = new ConsultativeAgentTransferCommand(username, agentId, monitorUcid, ucid, did, receiverAgentId, customerNumber, agentTransferManager, appAudioURL);
                                            break;
                                        case 3://Consultative-Hold
                                            transferCommand = new ConsultativeHoldAgentTransferCommand(username, agentUniqId, agentId, monitorUcid, ucid, did, receiverAgentId, customerNumber, agentTransferManager, campId, appAudioURL, "false", aToken.getString("callType"),null);
                                            break;
                                    }
                                } else if (transferType == 2) {//Skill      
                                    transferCommand = new SkillTransferCommand(username, agentId, monitorUcid, ucid, did, null, transferId, skillTransferManager);
                                    String url = paramKey[7] + "?skillName=" + transferId + "&campaignId=" + campId;
                                    transferCommand = new IvrTransferCommand(monitorUcid, ucid, did, phoneNumber, customerNumber, false, url, telephonyManager, username, agentId, campId, agentUniqId, appAudioURL, campaign, null, null, null,transferType,null,0);
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
                                        case 3://Consultative-Hold
                                            transferCommand = new ConsultativeHoldPhoneTransferCommand(username, agentId, monitorUcid, ucid, did, null, transferNumberName, transferNumber, Boolean.valueOf(aToken.getString("sip")), customerNumber, phoneTransferManager, campId, appAudioURL, "true", aToken.getString("callType"));
                                            break;

                                    }
                                } else if (transferType == 4) {
                                    String url = paramKey[6];
                                    log.debug("IVR transfer url:" + url);
                                    transferCommand = new IvrTransferCommand(monitorUcid, ucid, did, phoneNumber, customerNumber, false, url, telephonyManager, username, agentId, campId, agentUniqId, appAudioURL, campaign, null, null, null,transferType,null,blindTransfer);
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
                    } else if ("tbPreviewDial".equals(lType)) {

                        String params = aToken.getString("params");
                        if (params != null && !params.isEmpty()) {
                            String paramKey[] = params.split(",");
                            String previewId = paramKey[0];
                            String number = paramKey[1];
                            String agentPhoneNumber = paramKey[2];
                            log.debug("Called Preview Dial " + params);
                            //PreviewData previewData = previewDataManager.get(new Long(previewId));
                            resp = previewDialerManager.dial(username, agentUniqId, agentId, aToken.getString("mode"), agentPhoneNumber, previewId);
                            String respArray[] = resp.split(":");
                            if (respArray.length > 1) {
                                lResponse.setString("status", respArray[0]);
                                lResponse.setString("message", respArray[1]);
                            }
                            lResponse.setString("resp", resp);
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
//                    if (isBlockedNumber(username, custNumber)) {
//                        log.debug("ERROR:" + custNumber + " is blacklisted number in manualDial");
//                        lResponse.setString("status", "ERROR");
//                        lResponse.setString("message", custNumber + " is blacklisted number");
//                        lResponse.setString("resp", "ERROR:" + custNumber + " is blacklisted number");
//                    } else {
                            AgentCommandStatus agentCommandStatus = null;
                            try {
                                agentCommandStatus = agentCommandExecutor.executeCommand(new ManualDialCommand(username, agentUniqId, agentId, mode, agentPhoneNumber, aToken.getBoolean("isSip"), custNumber, Long.valueOf(campaignId), manualDialService, "false"));
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
                            {
                                if (previewDataManager.isPreviewCampaignCompleted(campaign.getCampaignId())) {
                                    campaign.setPosition("COMPLETED");
                                    campaignManager.save(campaign);
                                }
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
                                    lResponse.setBoolean("previewDataResp", Boolean.TRUE);
                                    lResponse.setString("pId", previewData.getId().toString());
                                    lResponse.setString("pName", previewData.getName());
                                    lResponse.setString("pNumber", previewData.getPhoneNumber());
                                    lResponse.setString("pCid", params);
                                    lResponse.setString("dateCreated", DateUtil.convertDateToString(previewData.getCreateDate(), "dd/MMM/yyyy HH:mm:ss"));
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
                            resp = toolBarManager.tbResetPreviewNumber(pId);
                        }
                        lResponse.setString("resp", resp);
                    } else if ("tbSkipPreviewNumber".equals(lType)) {
                        String pId = aToken.getString("params");
                        if (pId != null && !pId.isEmpty()) {
                            resp = toolBarManager.tbSkipPreviewNumber(Long.valueOf(pId), "Skipped by:" + agentId);
                        }
                        lResponse.setString("resp", resp);
                    } else if ("tbGetCustomerData".equals(lType)) {
                        String dataId = aToken.getString("params");
                        if (StringUtils.equalsIgnoreCase(aToken.getString("callType"), "predictive") && StringUtils.isNotBlank(aToken.getString("monitorUcid"))) {
                            List<Report> reports = reportManager.getReportByMonitorUcid(Long.valueOf(aToken.getString("monitorUcid")));
                            if (!reports.isEmpty()) {
                                dataId = "" + reports.get(0).getData_id();
                                log.debug("Data id from reports:" + dataId + ", for monitor ucid:" + aToken.getString("monitorUcid"));
                            } else {
                                log.debug("Got no reports for monitorUcid:" + aToken.getString("monitorUcid"));
                            }
                        }

                        if (dataId != null && !dataId.isEmpty()) {
                            lResponse.setList("custData", toolBarManager.tbGetCustomerData(dataId));
                        }
                        lResponse.setString(resp, "success");
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
                        JsonObject jsonObject = toolBarManager.tbResechduleCallback(username, agentId, aToken.getString("cbTime"), Long.valueOf(aToken.getString("cbId")), aToken.getString("rsComment"),aToken.getString("callbacktz"));
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
                        StatusMessage statusMessage = agentManager.updatePassword(username, agentId, agentUniqId, aToken.getString("curPwd"), aToken.getString("newPwd"));
                        lResponse.setString("status", statusMessage.getStatus().toReadableString());
                        lResponse.setString("message", statusMessage.getMessage());
                    } else {
                        handledHere = false;
                        log.debug("@#@#@#@# Bad token:" + lType + "| NS:" + (lNS) + " | " + aToken);
                        // if unknown type in this namespace, return corresponding error message
                        lResponse.setInteger("code", -1);
                        lResponse.setString("msg", "Token type '" + lType + "' not supported in namespace '" + lNS + "'.");
                    }

                    lResponse.setString("agentId", agentId);
                    lResponse.setString("user", username);
                    if (handledHere) {
                        aEvent.sendToken(lResponse);
                        log.trace("Sending respons:" + lResponse);
                    }
                    // After sending the token check if we need inform Dialer or Not....
                    if (informDialer) {
                        previewDialerManager.informDialer(username, agentId, StringUtils.isEmpty(aToken.getString("campId")) ? null : Long.valueOf(aToken.getString("campId")));
                    }
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void processClosed(WebSocketServerEvent aEvent) {
        try {
            log.info("Client '" + aEvent.getSessionId() + "' disconnected.");
            chatService.removeClientIdFromSess(aEvent.getSessionId(), aEvent.getConnector().getId());
            //chatService.checkIfAgentIsInChat(aEvent.getSessionId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        return "Short description";
    }// </editor-fold>
}

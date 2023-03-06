package com.ozonetel.occ.webapp.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.chat.impl.ChatServiceImpl;
import com.ozonetel.occ.service.chat.impl.ChatBotServiceImpl;
import com.ozonetel.occ.util.AppContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.springframework.context.ApplicationContext;
import org.jwebsocket.token.TokenFactory;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.model.FakeServerEvent;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.model.FakeWebsocketResponse;
import com.ozonetel.occ.model.WrappedRequestToken;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.ChatStates;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.chat.impl.ChatOffCommand;
import com.ozonetel.occ.service.chat.impl.FacebookChatServiceImpl;
import com.ozonetel.occ.service.impl.TokenServerLocalImpl;
import java.util.ArrayList;
import com.ozonetel.occ.util.JsonUtil;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author pavanj
 */
public class ChatHandlerServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(ChatHandlerServlet.class);
    private static TokenServerLocalImpl tokenServer;
    private ChatServiceImpl chatService;
    private ChatBotServiceImpl chatBotService;
    private RedisAgentManager redisAgentManager;
    private FacebookChatServiceImpl facebookChatService;
    private UserManager userManager;
    private AgentManager agentManager;
    private CampaignManager campaignManager;
    private DispositionManager dispositionManager;
    private String chatClientMsgSrvr;
    private EventManager eventManager;

    private TokenServerLocalImpl getTokenServer() {
        if (tokenServer == null) {
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }
        return tokenServer;

    }

    public void setChatClientMsgSrvr(String chatClientMsgSrvr) {
        this.chatClientMsgSrvr = chatClientMsgSrvr;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Map<String, Object> map = processToken(new FakeServerEvent(request.getParameter("sessionId"), request.getParameter("clientId")), new WrappedRequestToken(request));

        try {
            out.write(new GsonBuilder().serializeNulls().create().toJson(map));
        } finally {
            out.close();
        }
    }

    public void processOpened() {
        log.debug("Listening with chant handler----");
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        chatService = (ChatServiceImpl) webApplicationContext.getBean("chatService");
        chatBotService = (ChatBotServiceImpl) webApplicationContext.getBean("chatBotService");
        redisAgentManager = (RedisAgentManager) webApplicationContext.getBean("redisAgentManager");
        facebookChatService = (FacebookChatServiceImpl) webApplicationContext.getBean("facebookChatService");
        userManager = (UserManager) webApplicationContext.getBean("userManager");
        agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
        campaignManager = (CampaignManager) webApplicationContext.getBean("campaignManager");
        eventManager = (EventManager) webApplicationContext.getBean("eventManager");
        dispositionManager = (DispositionManager) webApplicationContext.getBean("dispositionManager");

    }

    public Map<String, Object> processToken(FakeServerEvent aEvent, WrappedRequestToken aToken) {

        try {
            if (aToken.getString("username") != null && aToken.getString("username").equalsIgnoreCase("chat")) {
//           aEvent.getId();
                log.debug("Chat request from client:" + aEvent.getSessionId() + " | dsessionid:" + aEvent.getSessionId());
                log.debug("Event:" + aEvent + " | Token:" + aToken);
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
        FakeWebsocketResponse lResponse = new FakeWebsocketResponse(aToken.getUtid());

        try {

            String lNS = aToken.getNS();
            String lType = aToken.getType();
            String resp = "";

            final String username = StringUtils.trim(aToken.getString("customer"));
            final String agentId = StringUtils.trim(aToken.getString("agentId"));
//        final Long agentUniqId = Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId")));

            if (username == null || agentId == null || lType.equalsIgnoreCase("ping")) {

                lResponse.setInteger("code", -1);
                lResponse.setString("msg", "Token type '" + lType + "' not supported in namespace '" + lNS + "'.");
                //assuming after this statement processing stops.
//                aEvent.sendToken(lResponse1);

            } else if (lType != null && "ozonetel.cloudagent".equals(lNS) && !lType.equalsIgnoreCase("response")) {
                // create a response token
//                log.info("Client '" + aEvent.getSessionId() + "' sent Token: '" + aToken.toString() + "'.");

//                Token lResponse = aEvent.createResponse(aToken);
                if ("checkCustSess".equalsIgnoreCase(lType)) {
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
                            chatService.setUpChatSession(sessionIdToCheck, custDetails.get("custId"), apiKey, campaign.getUser().getUsername(), custDetails.get("custName"), custDetails.get("custMail"), custDetails.get("custPhone"), aEvent.getId(), campaign.getCampaignId(), did, null, campaign.getUser().getId() + "" + new Date().getTime(), campaign.getUser().getId(),"","","","");
                            chatService.addSessionState(sessionIdToCheck, ChatStates.CHAT_BOT);

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
                    //String custMailPhone = aToken.getString("custMailPhone");

                    tokenServer = getTokenServer();
                    //broadcast the msg to other tabs if they exist
                    Token broadCast = TokenFactory.createToken();
                    broadCast.setType("brCast");
                    broadCast.setString("msg", text);
                    Set<String> clientWsIds = chatService.getClientWsidsforSession(sessionId);

                    for (String clientWsId : clientWsIds) {
                        if (clientWsId.equals(aEvent.getId())) {
                            continue;
                        }
                        tokenServer.sendToken(tokenServer.getConnector(clientWsId), broadCast);
                    }

                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
                    if (chatSessionDetails.getAgentId() != null) { //talking to agent
                        String agentWsId = redisAgentManager.getAgentWsId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
                        log.debug("sending message to agent::" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId() + " | ws id:" + agentWsId);
                        Agent a = agentManager.getAgentByAgentIdUserId(chatSessionDetails.getCaUserId(), chatSessionDetails.getAgentId());
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
                            java.util.logging.Logger.getLogger(ChatHandlerServlet.class.getName()).log(Level.SEVERE, null, ex);
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
                            chatService.sendTokenToMsgServer(chatClientMsgSrvr, customer_sessionid, tokenResponse, false);
//                        if (tokenServer == null) {
//                            tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
//                        }
//
//                        Set<String> clientWsIds = chatService.getClientWsidsforSession(customer_sessionid);
//                        for (String clientWsId : clientWsIds) {
//                            tokenServer.sendToken(tokenServer.getConnector(clientWsId), tokenResponse);
//                        }

                        }

                        chatService.saveChatMessage(customer_sessionid, true, System.currentTimeMillis(), text, "text", agentId);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    lResponse.setLong("timestamp", System.currentTimeMillis());
                    lResponse.setString("status", "Success");
                    //aEvent.sendToken(lResponse);
                } else if ("disposeChat".equals(lType)) {

                    try {

                        if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, aToken.getString("sessionId"))) {
                            log.debug("--> Ending FB Chat bro...");
                            facebookChatService.sendMsgToUser(aToken.getString("sessionId"), "", "disconnect", null);//customer_sessionid --> FB sender ID
                            // ---> sessionId <-> fbSenderId
                            //chatBotService.askIvr(aToken.getString("sessionId"), "", "", "", aToken.getString("sessionId"), "disconnect");
                        }
                        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(aToken.getString("sessionId"));
                        if (chatSessionDetails != null && !chatSessionDetails.isCustomerEnded() && chatSessionDetails.getMonitorUcid().equals(aToken.getString("monitorUcid"))) {//agent ended chat.
                            log.debug("Agent initiated end chat.");
                            chatService.UpdateChatDetails_agentEnd(aToken.getString("sessionId"), aToken.getString("disp"), aToken.getString("comment"), chatSessionDetails.getAgentId());

                            // chatService.tearDownChatSession(customer_sessionid);
                            new ChatOffCommand(eventManager, agentManager, chatService, redisAgentManager, aToken.getString("sessionId"), chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId(), Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))), false, chatClientMsgSrvr,facebookChatService).chatOff();
                        } else {//customer has already ended chat, just dispose.
                            log.debug("Customer already ended chat just update disp.");
                            chatService.updateDisposition(aToken.getString("sessionId"), aToken.getString("disp"), aToken.getString("comment"), aToken.getString("monitorUcid"));
                            //decrement active chat count for agent
                            try {
                                agentManager.decrementAgentChatSessionsInDB(Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))));
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                            chatService.tearDownChatSession(aToken.getString("sessionId"));
                            //Update last busy event misc. details with count of chats handled in this busy session
                            if (chatService.getActiveChatSessionsCountByAgent(username, agentId) <= 0) {
                                log.debug("The last supper \uD83C\uDF72 \uD83C\uDF72 for the agent : so update event with chat coung");
                                eventManager.updateChatSessionsCountInBusyEvent(username, Long.valueOf(StringUtils.trim(aToken.getString("agentUniqId"))), agentId, chatService.getChatCountHandledSofar(username, agentId));

                            }
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    lResponse.setString("resp", "Success");
                    //aEvent.sendToken(lResponse);

                } else if ("custEndChat".equalsIgnoreCase(lType)) {

                    chatService.setCustomerEnded(aEvent.getSessionId());
                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(aEvent.getSessionId());

                    if (chatSessionDetails.getChatState() == ChatStates.AGENT) {
                        if (chatSessionDetails.getAgentId() != null) { //talking to agent
                            //inform agent
                            String agentWsId = redisAgentManager.getAgentWsId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
                            log.debug("sending message to agent::" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId() + " | ws id:" + agentWsId);
                            User u = userManager.getUser(chatSessionDetails.getCaUserId().toString());

                            log.debug("Agent msg server : " + u.getUrlMap().getLocalIp());
                            Token tokenResponse = TokenFactory.createToken();
                            tokenResponse.setString("type", "custEnd");
                            tokenResponse.setString("chatCustSessionId", aEvent.getSessionId());
                            tokenResponse.setLong("timestamp", System.currentTimeMillis());
                            //tokenServer.sendToken(tokenServer.getConnector(agentWsId), tokenResponse);
                            chatService.sendTokenToMsgServer(u.getUrlMap().getLocalIp(), aEvent.getSessionId(), tokenResponse, true);
                        }
                    } else {
                        //teardown chat only if its not with agent, else when toolbar refresh/reconnect happens unable to fetch chat details and is not visible on agent toolbar.
                        // -----> Remove all cleintid set mapped to session id.
                        chatService.tearDownChatSession(aEvent.getSessionId());
                    }
                    // ----> Write to db
                    chatService.UpdateChatDetails_custEnd(aEvent.getSessionId());
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

                    tokenServer = getTokenServer();

                    //broadcast the msg to other tabs if they exist
                    Token broadCast = TokenFactory.createToken();
                    broadCast.setType("imageMsg");
                    broadCast.setString("imageSrc", url);
                    broadCast.setLong("ts", System.currentTimeMillis());
                    Set<String> clientWsIds = chatService.getClientWsidsforSession(sessionId);

                    for (String clientWsId : clientWsIds) {
                        if (clientWsId.equals(aEvent.getId())) {
                            continue;
                        }
                        tokenServer.sendToken(tokenServer.getConnector(clientWsId), broadCast);
                    }

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
                    lResponse.setLong("count", chatService.getActiveChatSessionsCountByAgent(username, agentId));
                    lResponse.setList("sessionList", new ArrayList(chatService.getAgentChatSessions(username, agentId)));
                    //aEvent.sendToken(lResponse);
                } else if ("fetchChatSession".equalsIgnoreCase(lType)) {
                    ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(aToken.getString("sessionId"));
                    chatSessionDetails.setStateStartTime((System.currentTimeMillis() - chatSessionDetails.getStateStartTime()) / 1000);
                    lResponse.setString("sessionId", aToken.getString("sessionId"));
                    lResponse.setString("sessionDetails", new JsonUtil<ChatSessionDetails>().convertToJson(chatSessionDetails));
                    lResponse.setString("chatHist", chatService.getChatJson(aToken.getString("sessionId")));
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
                    //aEvent.sendToken(lResponse);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return lResponse.getMap();
    }

//    @Override
//    public void processClosed(WebSocketServerEvent aEvent) {
//        try {
//            log.info("Client '" + aEvent.getSessionId() + "' disconnected.");
//            chatService.removeClientIdFromSess(aEvent.getSessionId(), aEvent.getId());
//            // chatService.checkIfAgentIsInChat(aEvent.getSessionId());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
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

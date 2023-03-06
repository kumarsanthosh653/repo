package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jamesmurty.utils.XMLBuilder;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DataManager;
import com.ozonetel.occ.service.DialOutNumberManager;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.service.OCCManager;
import com.ozonetel.occ.service.PauseReasonManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.service.TransferNumberManager;
import com.ozonetel.occ.service.UserManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.Agent.State;
import com.ozonetel.occ.model.CallQueue;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.IvrFlow;
import com.ozonetel.occ.model.PauseReason;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentToolbarCommand;
import com.ozonetel.occ.service.ChatStates;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.IvrFlowManager;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.ToolBarManager;
import com.ozonetel.occ.service.chat.impl.ChatServiceImpl;
import com.ozonetel.occ.service.chat.impl.FacebookChatServiceImpl;
import com.ozonetel.occ.service.command.ChatSkillTransferCommand;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.DateUtil;
import com.ozonetel.occ.util.DndUtils;
import com.ozonetel.occ.util.PhoneNumberUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Administrator
 *
 */
public class OCCManagerImpl implements OCCManager {

    protected static AgentManager agentManager = null;

    public List<String> waitQueue = new ArrayList<String>();
    CampaignJob campaignJob;
    CampaignJobByAgent campaignJobByAgent;
    private DispositionManager dispositionManager;
    protected EventManager eventManager;
    protected static CampaignManager campaignManager;
    protected static SkillManager skillManager;
    protected static DialOutNumberManager dialOutNumberManager;
    protected static ReportManager reportManager;
    private DataManager dataManager;
    private PreviewDataManager previewDataManager;
    protected static CallBackManager callBackManager;
    protected static CallQueueManager callQueueManager;
    private PauseReasonManager pauseReasonManager;
    protected static UserManager userManager;
    protected static FwpNumberManager fwpNumberManager;
    private TransferNumberManager transferNumberManager;
    private int dialedCallCount = 0;
    private int connectedCallCount = 0;
    Log log = LogFactory.getLog(getClass());
    public TokenServerLocalImpl tokenServer;
    private ToolBarManager toolBarManager;
    private PreviewDialerManager previewDialerManager;
    private RedisAgentManager redisAgentManager;
    protected DndUtils dndUtils;
    private ChatServiceImpl chatService;
    private String chatClientMsgSrvr;
    private static IvrFlowManager ivrFlowManager;
    private FacebookChatServiceImpl facebookChatService;
    private PhoneNumberUtil phoneNumberUtil;

    public OCCManagerImpl() {
        super();
    }

    public void initialize() {

//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();

        agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
        eventManager = (EventManager) webApplicationContext.getBean("eventManager");
        dispositionManager = (DispositionManager) webApplicationContext.getBean("dispositionManager");
        callBackManager = (CallBackManager) webApplicationContext.getBean("callBackManager");
        campaignManager = (CampaignManager) webApplicationContext.getBean("campaignManager");
        previewDataManager = (PreviewDataManager) webApplicationContext.getBean("previewDataManager");
        dataManager = (DataManager) webApplicationContext.getBean("dataManager");
        reportManager = (ReportManager) webApplicationContext.getBean("reportManager");
        skillManager = (SkillManager) webApplicationContext.getBean("skillManager");
        dialOutNumberManager = (DialOutNumberManager) webApplicationContext.getBean("dialOutNumberManager");
        pauseReasonManager = (PauseReasonManager) webApplicationContext.getBean("pauseReasonManager");
        callQueueManager = (CallQueueManager) webApplicationContext.getBean("callQueueManager");
        userManager = (UserManager) webApplicationContext.getBean("userManager");
        fwpNumberManager = (FwpNumberManager) webApplicationContext.getBean("fwpNumberManager");
        transferNumberManager = (TransferNumberManager) webApplicationContext.getBean("transferNumberManager");
        toolBarManager = (ToolBarManager) webApplicationContext.getBean("toolBarManager");
        toolBarManager.initialize();
        tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        previewDialerManager = (PreviewDialerManager) webApplicationContext.getBean("previewDialerManager");
        redisAgentManager = (RedisAgentManager) webApplicationContext.getBean("redisAgentManager");
        dndUtils = (DndUtils) webApplicationContext.getBean("dndUtils");
        chatService = (ChatServiceImpl) webApplicationContext.getBean("chatService");
        ivrFlowManager = (IvrFlowManager) webApplicationContext.getBean("ivrFlowManager");
        facebookChatService = (FacebookChatServiceImpl) webApplicationContext.getBean("facebookChatService");
        phoneNumberUtil = (PhoneNumberUtil) webApplicationContext.getBean("phoneNumberUtil");
    }

    public int getConnectedCallCount() {
        return connectedCallCount;
    }

    public void setConnectedCallCount(int connectedCallCount) {
//        OCCManager.connectedCallCount = connectedCallCount;
    }

    public int getDialedCallCount() {
        return dialedCallCount;
    }

    public void setDialedCallCount(int dialedCallCount) {
//        OCCManager.dialedCallCount = dialedCallCount;
    }

    public String loginAgent(String agentId, String username) {
        if (null == agentManager) {
            initialize();
        }
        Agent agent = agentManager.getAgentByAgentIdV2(username, agentId);
        if (null != agent && agent.isLoggedIn() && agent.getState() == Agent.State.AUX) {
//            log.debug("Success: Agent Loggedin");
            return "Success: Agent Loggedin";
        } else {
            return "Error: Agent Login Failed";
        }

    }

    public String releaseAgentByAdmin(String username, String agentUniqueId, String whoDidIt) {
        if (null == agentManager) {
            initialize();
        }

        Agent a = agentManager.get(new Long(agentUniqueId));
        log.debug("Force Release Agent [" + a + "] By Admin and informing the Client| Who did it..?" + whoDidIt);
        if (null != a && a.getClientId() != null) {
            try {
                chatService.checkIfAgentIsInChat(username, a.getAgentId(), a.getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            Token tokenResponse = TokenFactory.createToken();
            tokenResponse.setType("agentReleaseByAdmin");
            tokenResponse.setString("who", whoDidIt);
            String existingString = redisAgentManager.hget(username + ":agent:events", a.getAgentId());
            Map forceLogoffMap = new Gson().fromJson(existingString, LinkedHashMap.class);
            Set<Map.Entry> entrySet = tokenResponse.getMap().entrySet();

            for (Map.Entry entry : entrySet) {
                forceLogoffMap.put(entry.getKey(), entry.getValue());
            }

            forceLogoffMap.put("eventTime", new SimpleDateFormat(Constants.DATE_FORMAT_STRING).format(new Date()));
            redisAgentManager.hset(username + ":agent:events", a.getAgentId(), new Gson().toJson(forceLogoffMap));

            if (tokenServer.getConnector(a.getClientId()) != null) {
                tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
            } else {
                log.debug("[" + a.getAgentId() + "]Agent Unable to sent to Client [" + a.getClientId() + "]" + " so will not be released.");
                return "Error:Can't connect to agent.";
            }

            return "Success";

        } else {
            log.debug("[" + a.getAgentId() + "] Agent is NULL..!!");
            return "Error:Didn't get agent/Client ID is null.";
        }
    }

    public String logoffAgentByAdmin(String username, Long agentUniqueId, String whoDidIt) {
        try {
            if (null == agentManager) {
                initialize();
            }

            Agent a = agentManager.get(agentUniqueId);

            log.debug("Logging off agent by admin:" + a);

            if (null != a) {
                toolBarManager.tbAgentLogout(username, a.getAgentId(), a.getId(), a.getPhoneNumber(), Event.AgentMode.valueOf(a.getMode().toString()), "logoffByAdmin:" + whoDidIt);
                log.debug("Force Logoff Agent[" + a.getAgentId() + "] By Admin ");

                //Already updating as below in the @toolBarManager.tbAgentLogout call don't need to do it again.
                if (a.getClientId() != null) {
                    Token tokenResponse = TokenFactory.createToken();
                    tokenResponse.setType("agentLogoffByAdmin");
                    if (tokenServer.getConnector(a.getClientId()) != null) {
                        tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                    } else {
                        log.debug("Agent Unable to sent to Client [" + a.getClientId() + "]");
                    }
                }
                return "Success";
            } else {
                log.debug("Agent is NULL..!!");
                return "Error";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "Error";
    }

    public String releaseChatByAdmin(String sessionId, String who) {
        log.debug("Force releasing chat : " + sessionId);
        if (null == chatService) {
            initialize();
        }
        //Inform customer
        log.debug("Force release : Sending end chat to client:" + sessionId);
        if (redisAgentManager.sismember(RedisKeys.FB_CHAT_SENDER_ID_SET, sessionId)) {
            log.debug("--> This  is FB Chat bro...");
            facebookChatService.sendMsgToUser(sessionId, "chat_force_released", "disconnect", null);//sessionId --> FB sender ID
        } else {
            Token tokenResponse = TokenFactory.createToken();
            tokenResponse.setType("endChat");
            tokenResponse.setString("agentClosed", "Admin");
            chatService.sendTokenToMsgServer(chatClientMsgSrvr, sessionId, tokenResponse, false);
        }
        chatService.setCustomerEnded(sessionId);
        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
        Agent a = null;
        //check if customer is talking to agent
        if ((chatSessionDetails.getChatState() == ChatStates.AGENT || chatSessionDetails.getChatState() == ChatStates.CALLING) && chatSessionDetails.getAgentId() != null) {
            String cachedAgentUniqId = redisAgentManager.getString(chatSessionDetails.getCaUserName() + ":agent:" + chatSessionDetails.getAgentId());
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                a = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                a = agentManager.getAgentByAgentIdV2(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
            }
            log.debug("Chat is with agent : " + a + " is agent logged in ? " + a.isLoggedIn());
        }
        if (a != null && a.getClientId() != null) {
            chatService.UpdateChatDetailsSystemEnd(sessionId, who, chatService.getChatJson(sessionId));
            String agentWsId = redisAgentManager.getAgentWsId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
            log.debug("Force Realease : sending message to agent::" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId() + " | ws id:" + agentWsId);
            //inform agent
            Token tokenResponse1 = TokenFactory.createToken();
            tokenResponse1.setString("type", "custEnd");
            tokenResponse1.setString("chatCustSessionId", sessionId);
            tokenResponse1.setString("timestamp", new SimpleDateFormat("HH:mm").format(new Date().getTime()));
            tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse1);
        } else {
            chatService.UpdateChatDetailsSystemEnd(sessionId, who, chatService.getChatJson(sessionId));
            chatService.tearDownChatSession(sessionId);
        }
        return "Success";
    }

    public synchronized String getNextFreeAgent(String did, String callerId, String dataId, String ucid, String agentMonitorUcid, String skillName, String cId, String agentId, String agentPh) {
        try {
            XMLBuilder xmlb = getXMLBuilder("nextAgent");
            Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
            queryParams.put("ucid", agentMonitorUcid);
            queryParams.put("callerId", callerId);
            queryParams.put("did", did);
            queryParams.put("skillName", skillName);
            List l = new ArrayList();
            if (agentId != null || agentPh != null) {
                queryParams.put("agentId", agentId);
                queryParams.put("agentPh", agentPh);
                xmlb.e("DD").t("1");
                l = agentManager.executeProcedure("{call Get_DirectAgentPhone(?,?,?,?,?,?)}", queryParams);
            } else {
                xmlb.e("DD").t("0");
                l = agentManager.executeProcedure("{call Get_FreeAgentPhone(?,?,?,?)}", queryParams);
            }
            log.debug("result l = " + l.toString());
            if (l.size() > 0) {
                for (Object object : l) {
                    HashMap<String, String> mp = (HashMap<String, String>) object;
                    String campaignExists = mp.get("CampaignExists");
                    String skillExists = mp.get("SkillExists");
//                    log.debug("campaignExists=" + campaignExists + " skillExists=" + skillExists);
                    if (campaignExists.equalsIgnoreCase("1")) {
                        if (skillExists.equalsIgnoreCase("1")) {
                            xmlb.e("status").t("1");
                            xmlb.e("cId").t(mp.get("CampaignID"));// Assign Campaign
                            xmlb.e("recOn").t(mp.get("RecON"));
                            // Prepare Queue Confguration START
                            XMLBuilder qXml = xmlb.e("Q");
                            qXml.e("P").t(mp.get("QueuePosition"));
                            qXml.e("S").t(mp.get("QueueSize"));
                            qXml.e("T").t(mp.get("QueueTime"));
                            XMLBuilder fbrEle = qXml.e("fbr");
                            String fallBackRule = mp.get("FallbackRule");
                            if (fallBackRule.equalsIgnoreCase("1")) {//dialout
                                fbrEle.a("type", "DO").t(mp.get("FallbackValue"));
                            } else if (fallBackRule.equalsIgnoreCase("2")) {//Disconnect
                                fbrEle.a("type", "DIS");
                            } else if (fallBackRule.equalsIgnoreCase("3")) {//voicemail
                                fbrEle.a("type", "VM");
                            } else if (fallBackRule.equalsIgnoreCase("4")) {//Skill Transfer
                                fbrEle.a("type", "TS").t(mp.get("FallbackValue"));
                            } else if (fallBackRule.equalsIgnoreCase("5")) {//IVR Transfer
                                fbrEle.a("type", "TI").t(mp.get("FallbackValue"));
                            }
                            if (mp.get("CampaignStatus").equalsIgnoreCase("online")) {
                                String agent = mp.get("Agent");
                                if (!agent.equalsIgnoreCase("0")) {
                                    xmlb.e("a").a("id", agent).a("sms", String.valueOf(false)).t(mp.get("PhoneNumber"));
//                                    informToolBar("newcall", skillName, "AgentDial", callerId, "UUI Pending", ucid, agentMonitorUcid, dataId, did, mp.get("CampaignID"), mp.get("ClientID"));
                                }

                            } else if ((mp.get("CampaignStatus").equalsIgnoreCase("offline"))) {
                                String phoneId = mp.get("PhoneID");
                                if (!phoneId.equalsIgnoreCase("0")) {
                                    xmlb.e("a").a("id", mp.get("PhoneID")).a("sms", String.valueOf(false)).t(mp.get("PhoneNumber"));
                                }
                            }

                        } else {
                            xmlb.e("status").t("0");
                            xmlb.e("message").t("Invalid Skill...");
                        }
                    } else {
                        xmlb.e("status").t("0");
                        xmlb.e("message").t("Invalid Campaign..");
                    }

                    return xmlb.asString();
                }
            } else {
                log.debug("Nothing returned from Query");
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
        return "Error";
    }

    public String informToolBar(String type, String skillName, String fallBackRule, String callerId, String uui, String ucid, String agentMonitorUcid, String dataId, String did, String campaignId, String clientId) {

//            if (fallBackRule.equalsIgnoreCase("AgentDial") && !c.isOffLineMode() && transferType != 3) {
        Token tokenResponse = TokenFactory.createToken();
        if (type.equalsIgnoreCase("Preview")) {
            tokenResponse.setString("callType", "PreviewDialing");
        } else if (type.equalsIgnoreCase("ToolBarmanual")) {
            tokenResponse.setString("callType", "Manual Dialing");
        } else {
            tokenResponse.setString("callType", "IncomingCall");
        }
        tokenResponse.setType("newCall");
        tokenResponse.setString("callerId", callerId);
        tokenResponse.setString("callData", uui);
        tokenResponse.setString("ucid", ucid);
        tokenResponse.setString("monitorUcid", agentMonitorUcid);
        tokenResponse.setString("dataId", dataId);
        tokenResponse.setString("did", did);
        short tktCustomer = 0;
        Campaign c = null;
        if (campaignId != null) {
            c = campaignManager.get(new Long(campaignId));
        }
        if (getUserManager().hasTicketRole(c.getUser().getUsername())) {
            tktCustomer = 1;
            log.info(c.getUser().getUsername() + " has tkt role");
        }

        tokenResponse.setString("tktCustomer", "" + tktCustomer);
        log.debug("campaignId=" + c.getCampaignId().toString());
        tokenResponse.setString("wrapUpTime", "" + c.getSla());
        tokenResponse.setString("dispositionType", c.getDispositionType() != null ? c.getDispositionType() : "1");
        tokenResponse.setString("campaignId", c.getCampaignId().toString());
        tokenResponse.setString("campaignName", c.getCampignName());
        log.debug("Sending campaign name as:" + c.getCampignName());

        tokenResponse.setString("skillName", (!skillName.isEmpty() ? skillName : ""));

        if (uui != null) {
            tokenResponse.setString("uui", uui);
        } else {
            tokenResponse.setString("uui", "");
        }

        tokenResponse.setString("screenPop", c.getScreenPopUrl());
        tokenResponse.setString("screenPopMode", c.getUser().getScreenPopMode().toString());
        log.debug("Client ID=" + clientId + "==" + tokenServer.toString());
        log.debug("tokenServer.getConnector(clientId)" + clientId);
        if (tokenServer.getConnector(clientId) != null) {
            tokenServer.sendToken(tokenServer.getConnector(clientId), tokenResponse);
            log.debug("New Call Sending  ScreenPop :[" + callerId + "]");
        } else {
            log.debug("New Call Unable to sent to Client [" + clientId + "]");
        }
        return "success";
    }

    /* public String getNextFreeAgent1(String did, String callerId, String dataId, String ucid, String agentMonitorUcid, String skillName, String cId, String agentId, String agentPh) {
     //Get The Campaign Based on DID
     String type = "inbound"; //in future we need to pass this since there are diff campaigns with same Did
     Campaign c = null;
     if (cId != null && !cId.isEmpty()) {
     c = campaignManager.get(new Long(cId));
     } else {
     c = campaignManager.getCampaignsByDid(did, type);
     }
     if (c != null && c.getPosition().equalsIgnoreCase("STOPING")) { // if the Campaign is stoping position we should not allow since it is going to stop state
     c = null;
     }
     try {
     XMLBuilder xmlb = getXMLBuilder("nextAgent");
     boolean isValidCamp = false;
     Skill skill = null;
     if (c != null) {
     skill = skillManager.getSkillsByUserAndSkillName(skillName, c.getUser().getUsername());
     for (Skill s : c.getSkills()) {
     if (s.getSkillName().equalsIgnoreCase(skillName)) {
     isValidCamp = true;// Means the skill is assigned to given Did
     break;
     }
     }
     } else {
     xmlb.e("status").t("1").up().e("message").t("No campaign with the DID");
     log.debug("[" + ucid + "]:" + xmlb.asString());
     return xmlb.asString();
     //            return "No Campaign with the DID";
     }
     //            status , campaignId , Recon, QueuePosition, QueueSize
     if (isValidCamp) {// Campaign is Validagent
     int position = 0;
     xmlb.e("status").t("1");
     xmlb.e("cId").t(c.getCampaignId().toString());// Assign Campaign
     xmlb.e("recOn").t(String.valueOf(skill.getRecOn()));// Assign Campaign
     position = isCallInQueue(callerId, skillName, did, agentMonitorUcid); // Get the Postion of the Queue

     // Prepare Queue Confguration START
     XMLBuilder qXml = xmlb.e("Q");
     qXml.e("P").t(String.valueOf(position));
     qXml.e("S").t(String.valueOf(skill.getQueueSize().toString()));
     qXml.e("T").t(String.valueOf(skill.getQueueTimeOut().toString()));
     XMLBuilder fbrEle = qXml.e("fbr");
     if (skill.getFallBackRule().equalsIgnoreCase("1")) {//dialout
     fbrEle.a("type", "DO").t(skill.getDialOutNumber().getDialOutNumber().toString());
     } else if (skill.getFallBackRule().equalsIgnoreCase("2")) {//Disconnect
     fbrEle.a("type", "DIS");
     } else if (skill.getFallBackRule().equalsIgnoreCase("3")) {//voicemail
     fbrEle.a("type", "VM");
     } else if (skill.getFallBackRule().equalsIgnoreCase("4")) {//Skill Transfer
     fbrEle.a("type", "TS").t(skill.getQueueSkillTransfer().getSkillName());
     } else if (skill.getFallBackRule().equalsIgnoreCase("5")) {//IVR Transfer
     fbrEle.a("type", "TI").t(skill.getQueueIvrTransfer());
     }

     if (position != 1) {// If Position is not one then keep the caller in Queue
     return xmlb.asString();
     //                          return "4~" + position+"~"+c.getCampaignId();
     } else {//If Position=1 then get the agent for him
     //if campaign is INBOUND

     if (c != null && c.getCampaignType().equalsIgnoreCase("INBOUND") && skill != null) {
     //Get the Agents for the Inbound Campaign for  Non AgentWise
     //Check if the campaign is running in off line mode and get Hunting Fwp Numbers
     //                        synchronized (this) {
     if (c.isOffLineMode()) {
     List<FwpNumber> fwpNumbers = new ArrayList<FwpNumber>();
     if (agentPh != null) {// for direct routing
     FwpNumber f = fwpNumberManager.getFwpNumberByName(agentPh, c.getUser().getUsername());
     if (f != null && f.getNextFlag() == 0 && f.getState() == State.IDLE) {
     fwpNumbers.add(f);
     }
     } else {
     fwpNumbers = skillManager.getHuntingFwpNumbersBySkill(skill.getId());
     }
     log.debug("FwpNumbers = " + fwpNumbers.size());
     if (fwpNumbers != null && fwpNumbers.size() > 0) {
     for (FwpNumber fwpNumber : fwpNumbers) {
     fwpNumber.setLastSelected(System.currentTimeMillis());
     fwpNumber.setNextFlag(new Long(1));
     fwpNumberManager.save(fwpNumber);
     //                                    if (skill.getFallBackRule().equalsIgnoreCase("4")) {
     callQueueManager.deleteCallQueue(callerId, skillName, did, new Long(agentMonitorUcid));
     //                                    }
     xmlb.e("a").a("id", fwpNumber.getId().toString()).a("sms", String.valueOf(fwpNumber.isSms())).t(fwpNumber.getPhoneNumber());
     log.debug("[" + ucid + "] Resp =" + xmlb.asString());
     return xmlb.asString();
     }
     }

     } else {
     //Get The List of availble Idle agent for the Skill
     List<Agent> agents = new ArrayList<Agent>();
     log.debug("Get Inbound Agents==" + Agent.Mode.INBOUND);
     if (agentId != null) {//for direct routing
     Agent a = agentManager.getAgentByAgentId(c.getUser().getUsername(), agentId);
     if (a != null && a.getNextFlag() == 0 && a.getState() == State.IDLE) {
     agents.add(a);
     }
     } else {
     agents = agentManager.getAgentsBySkill(skill.getId(), Agent.Mode.INBOUND);
     }
     log.debug("[" + c.getdId() + "][INBOUND] Non AgentWise Agents Size=[" + agents.size() + "]");
     if (agents.size() > 0) {
     for (Agent agent : agents) {
     if ((agent.getState() == State.IDLE) || c.isOffLineMode()) {
     //need to be set the lastSelected time for Multiple requests
     agent.setLastSelected(System.currentTimeMillis());
     agent.setNextFlag(new Long(1));
     agent.setUcid(new Long(agentMonitorUcid));
     agentManager.save(agent);
     callQueueManager.deleteCallQueue(callerId, skillName, did, new Long(agentMonitorUcid));
     xmlb.e("a").a("id", agent.getAgentId().toString()).a("sms", agent.getFwpNumber() != null ? String.valueOf(agent.getFwpNumber().isSms()) : "false").t(agent.getPhoneNumber());
     log.debug("[" + ucid + "] Resp =" + xmlb.asString());
     return xmlb.asString();
     }
     }
     }
     }
     //                        }
     }
     log.debug("[" + ucid + "] Resp =" + xmlb.asString());
     return xmlb.asString();

     }
     } else { // if isValidCamp = false
     xmlb.e("status").t("0");
     xmlb.e("message").t("No Campaign assigned with the given Skill");
     log.debug("[" + ucid + "] Resp=" + xmlb.asString());
     return xmlb.asString();
     }
     } catch (Exception e) {
     //            e.printStackTrace();
     return "ERROR";
     }

     }*/
    /**
     * *
     * get the campaign from the did. Each did can be assigned to only one
     * campaign. from campaign get the user who created the campaign. finally
     * get the agent associated with that user. this is the agent we need to
     * update
     *
     * @param did
     * @return
     */
    public String getUsernameForDid(String did) {
        Campaign c = campaignManager.getCampaignsByDid(did);
        if (c != null) {
            User u = c.getUser();
            return u.getUsername();
        } else {
            return null;
        }
    }

    public boolean isValidAgent(String customer, String agentId, String phoneNumber, String pin) {
        Agent a = agentManager.getAgentByAgentIdV2(customer, agentId);

        if (a != null) {
//            if (a.getPassword().equals(pin) && a.isLoggedIn() && a.getClientId() == null && ((a.getSkills().size() > 0) || (a.getCampaign() != null))) {
            if (a.getPassword().equals(pin) && a.isLoggedIn() && a.getClientId() == null) {
                Map<String, Object> reportParams = new HashMap<String, Object>();
                reportParams.put("phoneNumber", phoneNumber);
                reportParams.put("userId", a.getUserId());

                List<FwpNumber> fwpNumbers = fwpNumberManager.findByNamedQuery("isFwpValid", reportParams);
                List<Agent> agents = agentManager.findByNamedQuery("isAgentLoginByPhoneNumber", reportParams);

                if (agents.size() > 0 || fwpNumbers.isEmpty()) {
                    log.debug("Agent[" + a.getAgentId() + "] with the phoneNumber[" + phoneNumber + "] is already login by other Agent");
                    return false;
                } else {
                    log.debug("Agent[" + agentId + "] with the phoneNumber[" + phoneNumber + "] is has logged In");
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public String releaseAllAgents() {
        try {
            List<Agent> allAgents = agentManager.getAll();
            for (Agent agent : allAgents) {
                agent.setLoggedIn(false);
                agent.setState(State.LOGOUT);
                agentManager.save(agent);
            }
            log.debug("Success: Releasing all Agents");
            return "Success: Releasing all Agents";
        } catch (Exception e) {
            log.debug("Error: Releasing all Agents");
            return "Error: Releasing all Agents";
        }
    }

    public String getCallBackList(String agentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("agentId", agentId);
        List<Data> datas = dataManager.findByNamedQuery("data", params);
        StringBuilder sb = new StringBuilder("List [");
        for (Data data : datas) {
            sb.append("{name=" + data.getCall_data() + ",contactNumber=" + data.getDest() + ",callBackTime=" + DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss", data.getCallBackTime()) + ",dataId=" + data.getData_id() + "}");
        }
        sb.append("]");
        return sb.toString();
    }

    //For WebSocket
    public String getCallBackListByAgent(String agentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("agentId", agentId);
        List<Data> datas = dataManager.findByNamedQuery("data", params);
        StringBuilder sb = new StringBuilder("");

//        for (Data data : datas) {
        for (int i = 0; i < datas.size(); i++) {
            Data data = datas.get(i);
            sb.append("\"" + data.getDest() + ",Preview," + data.getData_id() + "\":\"" + data.getCall_data()
                    + "\"" + (i == datas.size() - 1 ? "" : ","));

        }
//        sb.append("}");
        return sb.toString();
    }

    //This Method Update the agents Previous Event EndTime
    public void updatePreviousEvent(Long agentId) {
//        log.debug("Updating the Previous events..for agent[" + agentId + "]");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("agentId", agentId);
//        log.debug("Event Manager" + eventManager);
        String getEventByAgentId = "select d from Event d where d.agent.id =:agentId order by d.eventId desc";
        List<Event> events = eventManager.findByLimitQuery(getEventByAgentId, params, 1);
        log.debug("Events ==" + events);
        if (events.size() > 0) {
            Event event = events.get(0);
            log.debug("Last event:" + event);
            if (event.getEvent() != null && !event.getEvent().equalsIgnoreCase("login")) {
                event.setEndTime(Calendar.getInstance().getTime());
            }
            eventManager.save(event);
        }
    }

    public void updateLastLoginEvent(Long agentId, String eventName) {
//        log.debug("updateLastLoginEvent agent[" + agentId + "] , EventName[" + eventName + "]");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("agentId", agentId);
        params.put("eventName", eventName);
        String getlastLoginEvent = "select d from Event d where d.agent.id =:agentId and d.event =:eventName order by d.eventId desc";
        List<Event> events = eventManager.findByLimitQuery(getlastLoginEvent, params, 1);
//        log.debug("Events Size ===" + events.size());
        if (events.size() > 0) {
            Event event = events.get(0);
            event.setEndTime(Calendar.getInstance().getTime());
            eventManager.save(event);
            log.debug("Saved event:" + event);
        }
    }

    public String isAgent(String did, String agentId) {

        log.debug("PhoneLogin did[" + did + "] agentId[" + agentId + "]");
        String resp = "false";
        Agent agent = agentManager.getAgentByAgentIdV2(getUsernameForDid(did), agentId);

        if (agent != null) {
            log.debug("is Valid Agent");
            resp = "true";
        } else {
            log.debug("is not Agent");
        }

        return resp;

    }

    /**
     * This Method is to Return the campId Based on the agent Id which in turn
     * used to pass the campId to InformDialler
     *
     * @param userName
     * @param agentId
     * @return
     */
//    public String getCampaignByAgent(String userName, String agentId) {
//        Agent a = agentManager.getAgentByAgentId(userName, agentId);
//        if (a != null) {
//            if (a.getCampaign() != null) {
//                return a.getCampaign().getCampaignId().toString();
//            }
//        }
//        return null;
//
//    }
//    public Long getCampaignIDByAgent(String userName, String agentId) {
//        if (agentManager == null) {
//            initialize();
//        }
//        log.info("Agent manager :" + agentManager + " | User name:" + userName + " | agent ID:" + agentId);
//        Agent a = agentManager.getAgentByAgentId(userName, agentId);
//        log.info("Agent:" + a);
//        if (a != null) {
//            log.info("Campaign:" + a.getCampaign());
//            if (a.getCampaign() != null) {
//                log.info("Campaign :" + a.getCampaign());
//                return a.getCampaign().getCampaignId();
//            }
//        }
//        return null;
//
//    }

    /*public void updateCallStatus_new(String ucid, String agentMonitorUcid, String did, String agentId, String callerId, String callStatus, String audioFile, String skillName, String hangUpBy, String uui, String isCompleted, String fallBackRule, String type, int transferType, String dialStatus, boolean callCompleted, String dataId, String customerStatus, String agentStatus, String campaignId, Long priId, String startTime, String endTime, String tta) {
     Map<String, Object> reportParams = new HashMap<String, Object>();
     reportParams.put("ucid", new Long(ucid));
     reportParams.put("did", did);
     List<Report> reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);
     Campaign c = null;
     if (campaignId != null && !campaignId.isEmpty()) {// if campaignId is passed
     c = campaignManager.get(new Long(campaignId));
     } else if (type.equalsIgnoreCase("manual") || type.equalsIgnoreCase("ToolBarManual")) {
     c = campaignManager.getCampaignsByDid(did);
     } else {
     c = campaignManager.getCampaignsByDid(did, type);
     }
     String username = getUsernameForDid(did);
     Agent a = null;
     FwpNumber f = null;
     if (c.isOffLineMode() || type.equalsIgnoreCase("manual") || transferType == 3) {
     f = fwpNumberManager.get(new Long(agentId));
     } else {
     a = agentManager.getAgentByAgentId(username, agentId);
     }
     if (isCompleted.equalsIgnoreCase("true")) {// TO-DO need to check for specific call Queue also

     callQueueManager.deleteCallQueue(callerId, skillName, did, new Long(agentMonitorUcid));
     }
     if (!audioFile.isEmpty()) {
     String regString = "http://www.kookoo.in/recordings/";
     audioFile = audioFile.replaceAll(regString, "http://recordings.kookoo.in/").replaceAll(".wav", ".mp3");
     }

     Report r = new Report();
     r.setUcid(new Long(ucid));
     r.setMonitorUcid(new Long(agentMonitorUcid));
     r.setUui(!uui.isEmpty() ? uui : "");
     r.setTransferNow(false);
     r.setDialStatus(dialStatus);
     r.setCallCompleted(callCompleted);
     r.setCustomerStatus(customerStatus);
     r.setAgentStatus(agentStatus);
     r.setPriId(priId);
     r.setStatus(callStatus);//success or fail
     r.setSkillName(skillName);
     r.setHangUpBy(hangUpBy);
     r.setEndTime(new Date());
     r.setAudioFile(audioFile);
     r.setTransferType(new Long(transferType));
     r.setTransferNow(false);// need to be change here
     r.setDialStatus(dialStatus);
     r.setCallCompleted(callCompleted);
     r.setCustomerStatus(customerStatus);
     r.setAgentStatus(agentStatus);
     r.setDest(callerId);
     try {
     SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
     Date callStartDate = sf.parse(startTime);
     r.setCallDate(callStartDate);
     r.setEndTime(sf.parse(endTime));
     Calendar cal = Calendar.getInstance();
     cal.setTime(callStartDate);
     cal.add(Calendar.SECOND, Integer.parseInt(tta));
     r.setTimeToAnswer(cal.getTime());
     } catch (Exception e) {
     log.debug("Error: Invalid Start or End Time ");
     }

     if (isCompleted.equalsIgnoreCase("true")) {
     if (agentId != null) {
     if (c.isOffLineMode()) {
     if (f != null) {
     f.setNextFlag(new Long(0));
     f.setCallStatus(dialStatus);
     if (f.getAgent() != null) {// if the agent done the Manual Dialing from physical Phone and if he also logged in with phone Number 
     r.setAgent(f.getAgent());
     }
     r.setAgentId(f.getPhoneNumber());
     r.setFwpNumber(f);
     fwpNumberManager.save(f);
     }
     } else if (a != null) {
     if (!StringUtils.equalsIgnoreCase(type, "Progressive")) {
     a.setNextFlag(new Long(0));
     }
     a.setCallStatus(dialStatus);
     r.setAgent(a);
     r.setFwpNumber(a.getFwpNumber());
     agentManager.save(a);
     }
     r.setAgentId(agentId);

     if (skillName != null) {
     Skill s = skillManager.getSkillsByUserAndSkillName(skillName, username);
     if (s != null) {
     r.setSkill(s);

     }
     }
     if (fallBackRule.equalsIgnoreCase("DialOut")) {
     if (agentId != null) {
     DialOutNumber don = dialOutNumberManager.getDialOutNumberByUserAndDon(agentId, username);
     r.setDialOutNumber(don);
     }
     }

     reportManager.save(r);
     log.debug("Report Saved...");

     if (a != null) {
     //                        if (a.getState() == State.IDLE && !c.isOffLineMode() && !type.equalsIgnoreCase("manual")) {
     if (a.getState() == State.IDLE && !c.isOffLineMode()) {
     Token tokenResponse = TokenFactory.createToken();
     tokenResponse.setType("dropCall");
     tokenResponse.setString("callStatus", callStatus);
     tokenResponse.setString("callType", type);

     if (a.getClientId() != null && tokenServer.getConnector(a.getClientId()) != null) {
     tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
     log.debug("Drop Call[" + callerId + "] Sending to ScreenPop :[" + agentId + "]");
     } else {
     log.debug("Drop Call[" + callerId + "] Unable to sent to Client [" + agentId + "]");
     }
     }
     }
     }

     if (callCompleted) {
     callBackManager.sendDataToCustomer(agentMonitorUcid, r.getUser().getUsername());
     }

     }

     }*/
    public void resetAgentFlag(String ucid) {
        log.debug("Set NextFlag to 0 for UCID=[" + ucid + "]");
        Map<String, Object> reportParams = new HashMap<String, Object>();
        reportParams.put("ucid", new Long(ucid));
        List<Agent> agents = agentManager.findByNamedQuery("getAgentsByUcid", reportParams);
        for (Agent agent : agents) {
            agent.setNextFlag(new Long(0));
            agent.setUcid(null);
            agentManager.save(agent);
        }

    }

    @Override
    public List<PauseReason> getPauseReasons(String username) {
        return pauseReasonManager.getPauseReasonByUser(username);
    }

    public int isCallInQueue(String callerId, String skillName, String did, String agentMonitorUcid) {
        //check whether the caller is already in queue
        boolean isNew = callQueueManager.isCallInQueue(callerId, skillName, did, new Long(agentMonitorUcid));
        int pos = 0;
        //if the caller is not present in the Queue you need to insert it into Queue
        if (!isNew) {
            CallQueue callQueue = new CallQueue();
            callQueue.getCallQueuePK().setCallerId(callerId);
            callQueue.getCallQueuePK().setDid(did);
            callQueue.setUserId(userManager.getUserByUsername(getUsernameForDid(did)).getId());
            callQueue.getCallQueuePK().setSkillName(skillName);
            callQueue.setStartTime(new Date());
            callQueue.setIsActive(true);
            callQueue.setReqCount(new Long(1));
            callQueue.getCallQueuePK().setUcid(new Long(agentMonitorUcid));
            callQueueManager.save(callQueue);
        }
        // if the Caller  is Present then get the position of the caller in the Queue
        pos = callQueueManager.getPostionInQueue(callerId, skillName, did, new Long(agentMonitorUcid));

        log.debug("Queue position for [" + callerId + "][" + skillName + "][" + did + "] = " + pos);

        return pos;
    }
//this method returns if the callerId is an Existing agent for the user or did 
//If it is an Agent it returns 1    

    public String checkAgent(String did, String callerId, String ucid, String agentMonitorUcid, Map requestParams) {
//        String userName = getUsernameForDid(did);
        String type = "inbound"; //in future we need to pass this since there are diff campaigns with same Did
        try {

            Campaign c;
            log.debug("Campaign id:" + ArrayUtils.toString((String[]) requestParams.get("campaignId")));
            if (requestParams.get("campaignId") != null && ArrayUtils.isNotEmpty((String[]) requestParams.get("campaignId")) && StringUtils.isNotBlank(((String[]) requestParams.get("campaignId"))[0])) {
                c = campaignManager.get(Long.valueOf(((String[]) requestParams.get("campaignId"))[0]));
            } else {
                c = campaignManager.getCampaignsByDid(did, type);
            }
            if (c != null && c.getPosition().equalsIgnoreCase("STOPING")) { // if the Campaign is stoping position we should not allow since it is going to stop state
                c = null;
            }
//        log.debug("CampaignId=" + campaignId);

            XMLBuilder resp = getXMLBuilder("checkAgent");
            if (c != null) {
                resp.e("status").t("1");
                resp.e("cId").t(c.getCampaignId().toString());
//                User u = userManager.getUserByUsername(userName);
                User u = c.getUser();
                Map<String, Object> params = new LinkedHashMap<String, Object>();
                params.put("userId", u.getId());
                int clen = StringUtils.length(callerId);
                if (clen > 10) {
                    params.put("phoneNumber", StringUtils.substring(callerId, clen - 10));
                } else {
                    params.put("phoneNumber", callerId);
                }
                //Check if this is Blocked Number or not
                if (phoneNumberUtil.isBlockedNumber(params.get("phoneNumber").toString(), c.getCampaignId())) {
                    resp.e("isBlocked").t("1");
                    log.debug("[" + ucid + "]=" + resp.asString());
                    return resp.asString();
                }
//                List blockedNumbers = campaignManager.executeProcedure("{call Get_UserBlockedNumber(?,?)}", params);
//                log.debug("blocked Numbers=" + blockedNumbers);
//                Map<String, String> mp = (Map) blockedNumbers.get(0);
//                if (mp.get("BlockedNumber").equalsIgnoreCase("Exists")) {
//                    resp.e("isBlocked").t("1");
//                    log.debug("[" + ucid + "]=" + resp.asString());
//                    return resp.asString();
//                }
                List<FwpNumber> fwpNumbers = fwpNumberManager.findByNamedQuery("isPhoneNumberValid", params);
                if (fwpNumbers.size() > 0) {
                    FwpNumber fwpNumber = fwpNumbers.get(0);
                    resp.e("a").a("id", fwpNumber.getId().toString());

                    log.debug("[" + ucid + "]=" + resp.asString());
                    return resp.asString();
                }
                List<Agent> agents = agentManager.findByNamedQuery("isAgentLoginByPhoneNumber", params);
                log.debug(agents.size());
                if (agents.size() > 0) {
//                    log.debug("[" + did + "][" + callerId + "] is an Agent");
                    Agent a = agents.get(0);
//                    log.debug(did + "-" + callerId + "-null-" + u.getId().toString() + "-" + ucid + "-" + agentMonitorUcid + "- -manual");
                    resp.e("a").a("id", a.getAgentId().toString());
                    log.debug("[" + ucid + "]=" + resp.asString());
                    return resp.asString();
                } else {
                    log.debug("[" + did + "][" + callerId + "] is Not an Agent ");

                    log.debug("[" + ucid + "]=" + resp.asString());
                    return resp.asString();
                }
            } else {
                resp.e("status").t("0");
                resp.e("message").t("Error: No campaign with the did");
                log.debug("[" + ucid + "]=" + resp.asString());
                return resp.asString();
//            return "Error: No campaign with the did";
            }
        } catch (Exception e) {
            log.error("CheckAgent Error:" + e.getMessage(), e);
            return "ERROR";
        }
    }

    @Override
    public List<String> getAgentTransferList(String username, String did, String agentId) {
        List<String> agents = new ArrayList();
        if (username != null) {
            List<Agent> idleAgents = agentManager.getIdleAgentsByUser(username);
            for (Agent agent : idleAgents) {
                if (!agent.getAgentId().equalsIgnoreCase(agentId)) {
                    agents.add(agent.getAgentId());
                }
            }

        }
        return agents;

    }

    public List<String> getSkillTransferList(String username, String did, String agentId) {
        List<String> skills = new ArrayList();
        String type = "inbound"; //in future we need to pass this since there are diff campaigns with same Did
        Campaign c = campaignManager.getCampaignsByDid(did, type);
        if (c != null) {
            List<Skill> skillList = campaignManager.getCampaignSkills(c.getCampaignId());
            for (Skill skill : skillList) {
                skills.add(skill.getSkillName());
            }
        }
        return skills;
    }

    public List getFwpNumberList(String username) {
        return fwpNumberManager.getFwpNumbersByUser(username);
    }

    @Override
    public List<TransferNumber> getPhoneTransferList(String username) {
        return transferNumberManager.getTransferNumbersByUser(username);
    }

    public String setTransfer(String ucid, String did, String username, String transferType, String transferId, final String transferNumber, int blindTransfer) {
        Map<String, Object> reportParams = new HashMap<String, Object>();
        reportParams.put("ucid", new Long(ucid));
        reportParams.put("did", did);
        String resp = "";
        List<Report> reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);
        if (!reports.isEmpty()) {
            Report r = reports.get(0);
            r.setTransferType(new Long(transferType));//1 is for Agent Transfer
            r.setTransferNow(true);
            r.setBlindTransfer(blindTransfer);
            //If username is null or empty try to get the username from Report Obj
            username = (!username.isEmpty() && username != null) ? username : r.getUser().getUsername();
            if (transferType.equalsIgnoreCase("2")) {// skill Transfer
                Skill s = skillManager.getSkillsByUserAndSkillName(transferId, username);//here transferId Means skillName
                r.setTransferSkillId(s.getId());
                resp = "Transfer to skill [" + s.getSkillName() + "]";
            } else if (transferType.equalsIgnoreCase("1") || transferType.equalsIgnoreCase("4")) {// Agent Transfer or call Repair
                Agent a = agentManager.getAgentByAgentIdV2(username, transferId);
                r.setTransferAgentId(a.getId());
                if (blindTransfer == 3) {// consultative Hold
                    if (StringUtils.endsWithIgnoreCase(transferId, "-2")) {
                        resp = makeConsultativeHoldTransfer(r.getUcid().toString(), r.getMonitorUcid().toString(), did, r.getAgentId(), transferNumber, r.getDest(), username, Integer.parseInt(transferType));
                    } else {
                        resp = makeConsultativeHoldTransfer(r.getUcid().toString(), r.getMonitorUcid().toString(), did, r.getAgentId(), transferId, r.getDest(), username, Integer.parseInt(transferType));
                    }
                    if (!resp.equalsIgnoreCase("success")) {
                        return resp;
                    }
                }
                resp = "success";
            } else {
                if (transferId.equalsIgnoreCase("-2")) {
                    r.setTransferToNumber(transferNumber);
                } else {
//                    log.debug("Split the Number " + transferId);
                    String[] tranString = transferId.split("~");
                    if (tranString[1] != null) {
                        r.setTransferToNumber(tranString[1].trim());
                    }
                }
                if (blindTransfer == 3) {// consultative Hold
                    Agent a = r.getAgent();
                    if (StringUtils.endsWithIgnoreCase(transferId, "-2")) {
                        resp = makeConsultativeHoldTransfer(r.getUcid().toString(), r.getMonitorUcid().toString(), did, r.getAgentId(), transferNumber, r.getDest(), username, Integer.parseInt(transferType));
                    } else {
                        resp = makeConsultativeHoldTransfer(r.getUcid().toString(), r.getMonitorUcid().toString(), did, r.getAgentId(), transferId, r.getDest(), username, Integer.parseInt(transferType));
                    }
                    if (!resp.equalsIgnoreCase("success")) {
                        return resp;
                    }
                }
//                resp = "Transfer to PhoneNumber [" + r.getTransferToNumber() + "]";
                resp = "success";
            }

            reportManager.save(r);
            log.debug(resp);
            return resp;
        } else {
            return "Transfer failed";
        }

    }

    public String transferCheck(String ucid, String did) {
        Map<String, Object> reportParams = new HashMap<String, Object>();
        reportParams.put("ucid", new Long(ucid));
        reportParams.put("did", did);
        List<Report> reports = reportManager.findByNamedQuery("getTransferReportByUcid", reportParams);
        try {
            XMLBuilder resp = getXMLBuilder("transferCheck");
            if (!reports.isEmpty()) {
                Report r = reports.get(0);
                r.setTransferNow(false);
                reportManager.save(r);
                Agent a = agentManager.get(r.getAgent().getId());//should get agent from local DB for client Id and actual state.

                if (tokenServer.getConnector(a.getClientId()) != null && r.getBlindTransfer() != 3) {
                    Token tokenResponse = TokenFactory.createToken();
                    tokenResponse.setType("transferCheck");
                    tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                    log.debug("Success: Agent[" + a.getAgentId() + "] Transfer Check sent to Client");
                } else {
                    log.debug("Error: Agent[" + a.getAgentId() + "] Transfer Check Unable to sent to Client [" + a.getClientId() + "]");
                }
                resp.e("status").t("1");
                // ------ > If transfer type is skill set <blind> tag value to 1 .
                resp.e("blind").t(r.getTransferType() == 2 ? "1" : String.valueOf(r.getBlindTransfer()));
                if (r.getTransferType() == 1 || r.getTransferType() == 4) {//Agent Transfer or call Repair
                    Agent receiverAgent = agentManager.get(r.getTransferAgentId());
                    log.debug("~~~~Agent's fwp:" + receiverAgent.getFwpNumber());
                    resp.e("a").a("id", receiverAgent.getAgentId()).t(receiverAgent.getPhoneNumber())
                            .a("isSip", "" + receiverAgent.getFwpNumber().isSip());
                } else if (r.getTransferType() == 2) {//Skill Transfer

                    resp.e("TS").t(skillManager.get(r.getTransferSkillId()).getSkillName());
                } else if (r.getTransferType() == 3) {//Ohone Transfer
                    FwpNumber fwp = fwpNumberManager.getFwpNumberByPhone(r.getTransferToNumber(), r.getUser().getId());
                    resp.e("ph").a("id", fwp != null ? fwp.getId().toString() : "-1").
                            a("isSip", fwp != null ? "" + fwp.isSip() : "false")
                            .t(r.getTransferToNumber());
                } else {
                    return resp.asString();
                }
            } else {
                resp.e("status").t("0");
            }

            log.debug("[" + ucid + "]=" + resp.asString());

            return resp.asString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";

        }
    }

    public String transferFailed(String did, String agentId) {
        Agent a = agentManager.getAgentByAgentIdV2(getUsernameForDid(did), agentId);
        // Tell the Client that the transfered Agent is Failed
        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setType("transferFailed");
        try {
            XMLBuilder resp = getXMLBuilder("transferFailed");
            if (tokenServer.getConnector(a.getClientId()) != null) {
                tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                log.debug("Agent[" + a.getAgentId() + "] Transfer Failed sent to Client");
                resp.e("status").t("1");
                return resp.asString();
//            return "Success";
            } else {
                log.debug("Agent[" + a.getAgentId() + "] TransferFailed Unable to sent to Client [" + a.getClientId() + "]");
                resp.e("status").t("0");
                resp.e("message").t("TransferFailed Unable to sent to Client" + a.getAgentId());
                return resp.asString();
//            return "Failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }

    }

    public XMLBuilder getXMLBuilder(String event) throws Exception {
        XMLBuilder builder = XMLBuilder.create("response");
        builder.e("action").t(event);
        return builder;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    private String makeConsultativeHoldTransfer(String ucid, String monitorUcid, String did, String agentId, String agentPhoneNumber, String custNumber, String username, int confType) {
        String resp = "success";
        Agent conferencedAgent = null;
        try {
            if (confType == 1) {
                log.trace("Agent conference for :" + agentPhoneNumber);
                // REVIST: Dont know why agentPhonenumber is paased in agentId place here
                conferencedAgent = agentManager.getAgentByAgentIdV2(username, agentPhoneNumber);
                log.debug("### Making agent busy before holding:" + conferencedAgent);
                if (conferencedAgent.getState() == Agent.State.IDLE) {
                    conferencedAgent.setNextFlag(1L);
                    agentManager.save(conferencedAgent);
                } else {
                    log.info("Can't get agent [" + conferencedAgent + "] to conference, state : " + conferencedAgent.getState());
                    return "fail";
                }

            }
        } catch (Exception ignore) {
        }

        Map mp = new HashMap<String, String>();
        mp.put("ucid", ucid);
        mp.put("monitorUcid", monitorUcid);
        mp.put("holdAction", "CALL_HOLD");
        mp.put("phoneNumber", custNumber);
        mp.put("did", did);
        mp.put("agentId", agentId);
        mp.put("username", username);
        Agent a = agentManager.getAgentByAgentIdV2(username, agentId);
        mp.put("agentUniqueId", (a != null ? a.getId() : null));
        Command c = new HoldManager(mp);
        String callResp = c.execute();
        log.debug("Transfer Hold Response=" + callResp);
        Token tokenResponse = TokenFactory.createToken();
        tokenResponse.setType("unHoldCustomer");
        tokenResponse.setString("custNumber", custNumber);

        if (StringUtils.equalsIgnoreCase("success", callResp)) {
            if (a.getClientId() != null && tokenServer.getConnector(a.getClientId()) != null) {
                tokenServer.sendToken(tokenServer.getConnector(a.getClientId()), tokenResponse);
                log.debug("Send unhold event to ScreenPop :[" + agentId + "]");
            } else {
                log.debug("Unable to sent UnHold event to Client [" + agentId + "]");
            }

        } else {
            try {
                if (confType == 1 & conferencedAgent != null) {
                    conferencedAgent.setNextFlag(0L);
                    agentManager.save(conferencedAgent);
                }
            } catch (Exception ignore) {
            }
            return callResp;
        }
        mp.put("holdAction", "CONFERENCE_HOLD");
        mp.put("confType", confType);
        mp.put("holdNumber", custNumber);
        mp.put("phoneNumber", agentPhoneNumber);
        c = new HoldManager(mp);
        callResp = c.execute();
        log.debug("Transfer Response=" + callResp);

        JsonObject jsonObject = new JsonParser().parse(callResp).getAsJsonObject();
        String status = jsonObject.get("status").toString().replaceAll("\"", "");

        if (!status.equalsIgnoreCase("success")) {
            return status;
        }

        return resp;

    }

    public List<IvrFlow> getFeedbackIVRList(String username) {
        List<IvrFlow> ivrlists = new ArrayList();

//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("userId", userManager.getUserByUsername(username).getId());
//        params.put("ivrType", 3);
//        ivrlists = ivrFlowManager.findByNamedQuery("getFeedbackIvrList", params);
        return ivrFlowManager.getFeedbackIVRList(userManager.getUserByUsername(username).getId());
//        ivrlists.forEach(System.out::println);
//        return ivrlists;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setPauseReasonManager(PauseReasonManager pauseReasonManager) {
        this.pauseReasonManager = pauseReasonManager;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public static void setCampaignManager(CampaignManager campaignManager) {
        OCCManagerImpl.campaignManager = campaignManager;
    }

    public static void setSkillManager(SkillManager skillManager) {
        OCCManagerImpl.skillManager = skillManager;
    }

    public static void setDialOutNumberManager(DialOutNumberManager dialOutNumberManager) {
        OCCManagerImpl.dialOutNumberManager = dialOutNumberManager;
    }

    public static void setReportManager(ReportManager reportManager) {
        OCCManagerImpl.reportManager = reportManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void setPreviewDataManager(PreviewDataManager previewDataManager) {
        this.previewDataManager = previewDataManager;
    }

    public static void setCallBackManager(CallBackManager callBackManager) {
        OCCManagerImpl.callBackManager = callBackManager;
    }

    public static void setCallQueueManager(CallQueueManager callQueueManager) {
        OCCManagerImpl.callQueueManager = callQueueManager;
    }

    public static void setFwpNumberManager(FwpNumberManager fwpNumberManager) {
        OCCManagerImpl.fwpNumberManager = fwpNumberManager;
    }

    public void setTransferNumberManager(TransferNumberManager transferNumberManager) {
        this.transferNumberManager = transferNumberManager;
    }

    public void setToolBarManager(ToolBarManager toolBarManager) {
        this.toolBarManager = toolBarManager;
    }

    public void setChatClientMsgSrvr(String chatClientMsgSrvr) {
        this.chatClientMsgSrvr = chatClientMsgSrvr;
    }

//    public static IvrFlowManager getIvrFlowManager() {
//        return ivrFlowManager;
//    }
    public static void setIvrFlowManager(IvrFlowManager aIvrFlowManager) {
        ivrFlowManager = aIvrFlowManager;
    }

}

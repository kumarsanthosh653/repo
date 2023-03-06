package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesmurty.utils.XMLBuilder;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.MultiSkillFallback;
import com.ozonetel.occ.service.AgentAlertsManager;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.DialOutNumberManager;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.service.RedisGetNextFreeAgentService;
import com.ozonetel.occ.service.RedisManager;
import com.ozonetel.occ.util.AppContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.ozonetel.occ.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author rajeshchary
 */
public class FreeAgent extends OCCManagerImpl implements Command {

    private String campaignId;
    private String did;
    private String callerId;
    private String dataId;
    private String ucid;
    private String agentMonitorUcid;
    private String skillName;
    private String agentId;
    private String agentPh;
    private String user;
    private String callPriority;
    private String type;

    private AgentAlertsManager agentAlertsManager;
    private RedisGetNextFreeAgentService redisGetNextFreeAgentService;
    private FwpNumberManager fwpNumberManager;
    private RedisManager<Agent> redisAgentManager;
    private String isRedis;
    private static Logger logger = Logger.getLogger(FreeAgent.class);

    public FreeAgent(HttpServletRequest request) {
        //
        // ------> Initializes the managers
        super.initialize();
        agentAlertsManager = (AgentAlertsManager) AppContext.getApplicationContext().getBean("agentAlertsManager");
        redisGetNextFreeAgentService = (RedisGetNextFreeAgentService) AppContext.getApplicationContext().getBean("redisGetNextFreeAgentService");
        fwpNumberManager = (FwpNumberManager) AppContext.getApplicationContext().getBean("fwpNumberManager");
        dialOutNumberManager = (DialOutNumberManager) AppContext.getApplicationContext().getBean("dialOutNumberManager");
        redisAgentManager = (RedisManager) AppContext.getApplicationContext().getBean("redisAgentManager");

        this.campaignId = request.getParameter("campaignId");
        this.did = request.getParameter("did");
        this.callerId = request.getParameter("callerId");
        this.dataId = request.getParameter("data_id");
        this.ucid = request.getParameter("ucid");
        this.agentMonitorUcid = request.getParameter("agentMonitorUcid");
        this.skillName = request.getParameter("skillName");
        this.agentId = request.getParameter("agentId");
        this.agentPh = request.getParameter("agentPh");
        this.isRedis = request.getParameter("isRedis");
        this.user = request.getParameter("user");
        this.callPriority = request.getParameter("callPriority");
        this.type = request.getParameter("type");

        type = (type != null && !type.isEmpty()) ? type : "inbound";
        agentId = (agentId != null && !agentId.isEmpty() && !agentId.equalsIgnoreCase("0")) ? agentId : null;
        agentPh = (agentPh != null && !agentPh.isEmpty() && !agentPh.equalsIgnoreCase("0")) ? agentPh : null;

    }

    @Override
    public String execute() {

        //
        // ----- > Direct dial doesn't work in Redis as of now.
        if (StringUtils.equalsIgnoreCase(isRedis, "1") && agentId == null && agentPh == null) {

            long startTime = System.currentTimeMillis();
            logger.debug("$$$$$$Started executing RedisGetNextFreeAgent..");
            String redisResponse
                    = redisGetNextFreeAgentService.getNextFreeAgent(user, campaignId, did, callerId, ucid, agentMonitorUcid, skillName, agentId, agentPh);
            log.info("$$$$$$$$RedisFreeAgent took :" + (System.currentTimeMillis() - startTime) + "ms");
            if (StringUtils.equalsIgnoreCase(redisResponse, "Campaign is offline")) {
                return getNextFreeAgent(did, callerId, dataId, ucid, agentMonitorUcid, skillName, campaignId, agentId, agentPh);
            } else {
                //
                // ----> Return redisResponse instead of dbNextFreeAgent if redis functionality has to be enabled.
//                dbNextFreeAgent = getNextFreeAgent(did, callerId, dataId, ucid, agentMonitorUcid, skillName, campaignId, agentId, agentPh);
                logger.debug("::#Redis NextFreeAgent:" + redisResponse);
//                logger.debug("::#DB NextFreeAgent   :" + dbNextFreeAgent);
                return redisResponse;
            }
        } else {//----> check redis flag also
            return getNextFreeAgent(did, callerId, dataId, ucid, agentMonitorUcid, skillName, campaignId, agentId, agentPh);
        }

    }

    @Override
    public String getNextFreeAgent(String did, String callerId, String dataId, String ucid, String agentMonitorUcid, String skillName, String cId, String agentId, String agentPh) {
        try {
            XMLBuilder xmlb = getXMLBuilder("nextAgent");
            Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
            queryParams.put("ucid", agentMonitorUcid);
            queryParams.put("callerId", callerId);
            queryParams.put("did", did);
            queryParams.put("skillName", skillName);
            List l = new ArrayList();
            boolean directDial = false;
            if (agentId != null || agentPh != null) {
                queryParams.put("agentId", agentId);
                queryParams.put("agentPh", agentPh);
                xmlb.e("DD").t("1");
                directDial = true;

                logger.trace("Params:" + queryParams);
//                if (redisAgentManager.sismember("optimal:users", StringUtils.lowerCase(user))) {
//                    logger.debug("FreeAgent for optimal users : Get_DirectAgentPhoneV5");
//                    l = agentManager.executeProcedure("{call Get_DirectAgentPhoneV5(?,?,?,?,?,?)}", queryParams);
//                } else {
//                    l = agentManager.executeProcedure("{call Get_DirectAgentPhoneV7(?,?,?,?,?,?)}", queryParams);
//                }
                l = agentManager.executeProcedure("{call Get_DirectAgentPhoneV7(?,?,?,?,?,?)}", queryParams);
                logger.debug( "Next free agent(Get_DirectAgentPhoneV7)|Params:" + queryParams+"| result l = " + l.toString());

            } else {
                int numericCallPriority;
                if (StringUtils.isBlank(callPriority) || StringUtils.equalsIgnoreCase(callPriority, "0")) {
                    numericCallPriority = 0;
                } else {
                    numericCallPriority = 1000 - Integer.parseInt(callPriority) * 100;
                }
                xmlb.e("DD").t("0");
                queryParams.put("pPriority", "" + numericCallPriority);
                queryParams.put("acdchoice", 1);//0---> round robin,1--> most idle agent.
                queryParams.put("type", type);
                logger.trace("Params:" + queryParams);
//                if (redisAgentManager.sismember("optimal:users", StringUtils.lowerCase(user))) {
//                    logger.debug("FreeAgent for optimal users : Get_FreeAgentPhoneV10");
//                    l = agentManager.executeProcedure("{call Get_FreeAgentPhoneV10(?,?,?,?,?,?,?)}", queryParams);
//                } else {
//                    l = agentManager.executeProcedure("{call Get_FreeAgentPhoneV12(?,?,?,?,?,?,?)}", queryParams);
//                }
                Campaign campaign = campaignManager.get(Long.valueOf(this.campaignId));
                if(campaign.isA2a_calling()){
                    l = agentManager.executeProcedure("{call Get_FreeAgentPhoneV13(?,?,?,?,?,?,?)}", queryParams);
                    logger.debug( "Next free agent(Get_FreeAgentPhoneV13)|Params:" + queryParams+"| result l = " + l.toString());
                }else{
                    if(redisAgentManager.sismember("ca:callpriority:users", StringUtils.lowerCase(user))){
                        log.debug("In callpriority:users");
                        l = agentManager.executeProcedure("{call Get_FreeAgentPhoneV15(?,?,?,?,?,?,?)}", queryParams);
                        logger.debug( "Next free agent(Get_FreeAgentPhoneV15)|Params:" + queryParams+"| result l = " + l.toString());
                    }
                    else {
                        l = agentManager.executeProcedure("{call Get_FreeAgentPhoneV12(?,?,?,?,?,?,?)}", queryParams);
                        logger.debug( "Next free agent(Get_FreeAgentPhoneV12)|Params:" + queryParams+"| result l = " + l.toString());
                    }
                }
            }

            logger.debug("Direct dial:" + directDial + " | Agent id: " + agentId + " | Phone:" + agentPh);

            if (l.size() > 0) {
                for (Object object : l) {
                    HashMap<String, String> mp = (HashMap<String, String>) object;
                    String campaignExists = mp.get("CampaignExists");
                    String skillExists = mp.get("SkillExists");
//                     log.debug("campaignExists=" + campaignExists + " skillExists=" + skillExists);
                    if (campaignExists.equalsIgnoreCase("1")) {
                        if (skillExists.equalsIgnoreCase("1")) {
                            xmlb.e("status").t("1");
                            xmlb.e("cId").t(mp.get("CampaignID"));// Assign Campaign
                            xmlb.e("recOn").t(mp.get("RecON"));
                            // Prepare Queue Confguration START
                            XMLBuilder qXml = xmlb.e("Q");
                            qXml.e("P").t(mp.get("QueuePosition"));
                            qXml.e("S").t(mp.get("QueueSize"));
                            XMLBuilder fbrEle = qXml.e("fbr");
                            String fallBackRule = mp.get("FallbackRule");

                            qXml.e("T").t(mp.get("QueueTime"));

                            switch (fallBackRule) {
                                case "1": // ----> Dialout//dialout

                                    String[] dialOutDetails = StringUtils.split((mp.get("FallbackValue")), "~");
                                    fbrEle.a("type", "DO")
                                            .a("isSip", "" + StringUtils.equals(dialOutDetails[1], "1"))
                                            .t(dialOutDetails[0]);
                                    break;
                                case "2"://-----> Disconnect
                                    fbrEle.a("type", "DIS");
                                    break;
                                case "3"://-----> voicemail
                                    fbrEle.a("type", "VM");
                                    break;
                                case "4"://-----> Skill Transfer
                                    fbrEle.a("type", "TS").t(mp.get("FallbackValue"));
                                    break;
                                case "5"://-----> IVR Transfer
                                    fbrEle.a("type", "TI").t(mp.get("FallbackValue"));
                                    break;
                                case "6"://-----> Mult skill fall back
                                    MultiSkillFallback multiSkill = new Gson().fromJson(mp.get("FallBackDetails"), MultiSkillFallback.class);
                                    multiSkill.setMainSkill(skillName);
                                    multiSkill.setSkillIndex(0);

                                    switch (multiSkill.getFallbackType()) {
                                        case "1": // ----> Dialout//dialout
                                            multiSkill.setFallbackType("DO");
                                            break;
                                        case "2"://-----> Disconnect
                                            multiSkill.setFallbackType("DIS");
                                            break;
                                        case "3"://-----> voicemail
                                            multiSkill.setFallbackType("VM");
                                            break;
                                        case "4"://-----> Skill Transfer
                                            multiSkill.setFallbackType("TS");
                                            break;
                                        case "5"://-----> IVR Transfer
                                            multiSkill.setFallbackType("TI");
                                            break;
                                    }

//                                    fbrEle.a("type", "TS").t(multiSkill.getSkills().get(0).getSkillName());
                                    //
                                    //----> Keeping main skill at 0 index to match with the current index
//                                    multiSkill.getSkills().add(0, new MultiSkillFallbackSkill(null, skillName, Integer.valueOf(mp.get("QueueTime"))));
                                    fbrEle.a("type", "MS").t(new GsonBuilder().create().toJson(multiSkill));
//                                qXml.e("MS").t(new GsonBuilder().create().toJson(multiSkill));
                                    break;
                            }

                            if (mp.get("CampaignStatus").equalsIgnoreCase("online")) {
                                String agent = mp.get("Agent");
                                if (!agent.equalsIgnoreCase("0")) {//-----> Agent is available

                                    //FwpNumber fwpNumber = fwpNumberManager.get(Long.valueOf(mp.get("PhoneID")));
                                    xmlb.e("a").a("id", agent).a("sms", String.valueOf(false))
                                            .a("agentUniqId", mp.get("AgentID"))
                                            .a("userUniqId", mp.get("UserID"))
                                            .a("campState", mp.get("CampaignStatus"))
                                            .a("agentMode", mp.get("AgentMode"))
                                            .a("isSip", "" + (mp.get("IsSIP").equals("0") ? false : true))
                                            .t(mp.get("PhoneNumber"));

                                    agentManager.lockAgent(new Long(mp.get("AgentID")), this.callerId);
//                                    log.debug("*Updagint Calling event for :'" + new Long(mp.get("AgentID")) + "' for userId:" + mp.get("UserID"));
//                                    eventManager.logEvent(Constants.CALLING, new Long(mp.get("UserID")), mp.get("UserName"), new Long(mp.get("AgentID")),
//                                            agent, Agent.Mode.values()[Integer.valueOf(mp.get("AgentMode"))],
//                                            new Date(), new Long(ucid), null, null);
//                                    informToolBar("newcall", skillName, "AgentDial", callerId, "UUI Pending", ucid, agentMonitorUcid, dataId, did, mp.get("CampaignID"), mp.get("ClientID"));
                                } else if (directDial == true) {
                                    //
                                    //------ > Send an alert to the agent that there is an incoming call.
                                    //Campaign campaign = campaignManager.get(Long.valueOf(this.campaignId));

                                    logger.debug("Alerting agent :" + agentId + " that there is an incoming call from :" + callerId);
                                    boolean maskNumber = redisAgentManager.sismember(Constants.ENCRYPT_FIELD,user);
                                    log.debug("Mask number flag for sticky agent:"+maskNumber);
                                    if(maskNumber){
                                        logger.debug("encrypting callerId for sticky call");
                                        agentAlertsManager.alertAgentWithKey(mp.get("UserName"), agentId, "Incoming call", SecurityUtil.encryptUsingAes256Key(callerId),
                                                "There is an incoming call from " + SecurityUtil.encryptUsingAes256Key(this.callerId),true);

                                    }else {
                                        agentAlertsManager.alertAgentWithKey(mp.get("UserName"), agentId, "Incoming call", callerId,
                                                "There is an incoming call from " + this.callerId,false);                                    }


                                }

                            } else if ((mp.get("CampaignStatus").equalsIgnoreCase("offline"))) {
                                String phoneId = mp.get("PhoneID");
                                if (!phoneId.equalsIgnoreCase("0")) {
                                    xmlb.e("a").a("id", mp.get("PhoneID"))
                                            .a("campState", mp.get("CampaignStatus"))
                                            .a("sms", String.valueOf(false))
                                            .a("isSip", "" + (mp.get("IsSIP").equals("0") ? false : true))
                                            .t(mp.get("PhoneNumber"));
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
                logger.error("UCID:" + agentMonitorUcid + "Nothing returned from Query");
                return "ERROR";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "ERROR";
        }
        return "Error";
    }
}

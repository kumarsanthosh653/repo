package com.ozonetel.occ.webapp.action;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.command.response.AgentLoginResponse;
import com.ozonetel.occ.model.CallbacksGrouped;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.CampaignInfo;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.JSONTicketDetails;
import com.ozonetel.occ.model.MiniTicket;
import com.ozonetel.occ.model.PreviewDataMap;
import com.ozonetel.occ.model.PreviewExtraData;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.SMSTemplate;
import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.ToolBarManager;
import com.ozonetel.occ.service.impl.CheckManualDialStatus;
import com.ozonetel.occ.service.impl.HoldManager;
import com.ozonetel.occ.service.impl.Status;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author V.J.Pavan Srinivas
 */
public class AgentToolbarAction extends BaseAction implements Preparable {

    @Override
    public void prepare() throws Exception {
        if (StringUtils.isNotBlank(agentMode)) {
            try {
                mode = Event.AgentMode.valueOf(agentMode);
                log.trace("Mode:" + mode);
            } catch (IllegalArgumentException e) {
                log.error("Error setting agent mode in Event(Login Action/Reconnect:" + reconnect + " ) ->" + e.getMessage(), e);
            }
        }

        actionStatus = "Success";

    }

    public String tbAgentLogin() {
//-----------------------------------------        
/*
        agentLoginResponse = new AgentLoginResponse(Status.SUCCESS);
        try {
            //FIXME get session id here.
            JsonObject jsonObject = toolBarManager.tbAgentLogin(customer, agentId, phoneNumber, null, usId, reconnect, mode);
            if (jsonObject != null) {

                if (StringUtils.equalsIgnoreCase(jsonObject.get("status").getAsString(), "Success")) {
                    agentLoginResponse.setCampaignType(jsonObject.get("campaignType") == null ? "" : jsonObject.get("campaignType").getAsString());
                    agentLoginResponse.setCampaignScript(jsonObject.get("campaignScript") == null ? "" : jsonObject.get("campaignScript").getAsString());
                    agentLoginResponse.setPhoneNumber(jsonObject.get("phoneNumber") == null ? "" : jsonObject.get("phoneNumber").getAsString());
                    agentLoginResponse.setAgentSkill(jsonObject.get("agentSkill") == null ? "" : jsonObject.get("agentSkill").getAsString());
                    //
                    // ------ > Tells whether call backs feature is enabled for the customer or not.
                    agentLoginResponse.setCbe(Integer.parseInt(jsonObject.get("callbackRole") == null ? "0" : jsonObject.get("callbackRole").getAsString()));
                    agentLoginResponse.setPauseReasons(toolBarManager.getPauseReasons(customer));
                    agentLoginResponse.setOutboundEnabled(Integer.parseInt(jsonObject.get("outboundRole") == null ? "0" : jsonObject.get("outboundRole").getAsString()));
                    agentLoginResponse.setSmse(Integer.parseInt(jsonObject.get("smsRole") == null ? "0" : jsonObject.get("smsRole").getAsString()));
                    agentLoginResponse.setMcn(Integer.parseInt(jsonObject.get("maskCustomernumber") == null ? "0" : jsonObject.get("maskCustomernumber").getAsString()));
                    //agentLoginResponse.setSmse(Integer.parseInt(jsonObject.get("stemps").getAsString()));

                    List<SMSTemplate> smsTemplates = new Gson()
                            .fromJson(jsonObject.get("stemps").getAsString(), new TypeToken<List<SMSTemplate>>() {
                            }.getType());
                    agentLoginResponse.setSmsTemplates(smsTemplates);
                    agentLoginResponse.setPauseAlert(Integer.parseInt(jsonObject.get("pauseAlert") == null ? "0" : jsonObject.get("pauseAlert").getAsString()));
                    agentLoginResponse.setBlendedRole(Integer.parseInt(jsonObject.get("blendedRole") == null ? "0" : jsonObject.get("blendedRole").getAsString()));
                    agentLoginResponse.setAgentCallHist(Integer.parseInt(jsonObject.get("agentCallHist") == null ? "0" : jsonObject.get("agentCallHist").getAsString()));

//                     lResponse.setList("dispList", dispNames);
                } else {
                    agentLoginResponse.setStatus(Status.valueOf(jsonObject.get("status").getAsString()));
                    agentLoginResponse.setMessage(jsonObject.get("message") != null ? jsonObject.get("message").getAsString() : "Unknown");
                }
            } else {
                agentLoginResponse.setStatus(Status.ERROR);
                agentLoginResponse.setMessage("Unknown");
            }

            if (!StringUtils.containsIgnoreCase(agentLoginResponse.getStatus().toString(), "Error")) {
                agentLoginResponse.setAgentStatus("AUX");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
*/
        return SUCCESS;
    }

    public String tbAgentLogout() {
//------------------------------
//        actionStatus = toolBarManager.tbAgentLogout(customer, agentId, phoneNumber, mode, logoutBy);
        return SUCCESS;
    }

    public String tbAgentBusy() {
//------------------------------
        prepareResponseAndMessage(toolBarManager.tbAgentBusy(customer, agentId));
        return SUCCESS;
    }

    public String tbAgentPause() {
//------------------------------  
//        prepareResponseAndMessage(toolBarManager.tbAgentPause(customer, agentId, pauseReason, mode));
        return SUCCESS;
    }

    public String tbPauseAlert() {
//------------------------------
        try {
            new Thread(new Runnable() {
                public void run() {
                    toolBarManager.alertAgentExceededPauseTime(null,customer, agentId, pauseReason, timeOut);
                }
            }).start();
            actionStatus = "Success";
            actionMessage = "Sending alert.";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            actionStatus = "Error";
            actionMessage = e.getMessage();
        }

        return SUCCESS;
    }

    public String tbAgentRelease() {
//-------------------------

//        prepareResponseAndMessage(toolBarManager.tbAgentRelease(customer, agentId, mode, releaseMsg));
        if (!StringUtils.containsIgnoreCase(getActionStatus(), "Error")) {
            String r[] = StringUtils.split(getActionMessage(), "~");
            agentStatus = "IDLE";
            if (r[1] != null && (r[1].equalsIgnoreCase(Agent.Mode.PROGRESSIVE.toString()) || r[1].equalsIgnoreCase(Agent.Mode.BLENDED.toString()))) {
                previewDialerManager.informDialer(customer, agentId, campId);
            }

        }
        return SUCCESS;
    }

    public String tbSendSMS() {
//------------------------
        if (userManager.hasRole(customer, Constants.SMS_ROLE)) {
            prepareResponseAndMessage(toolBarManager.sendSMS(customer, new BigInteger(StringUtils.isEmpty(ucid) ? "0" : ucid), campId, agentId, smsDest, smsMsg, null,null).toString());
        } else {
            actionStatus = "Error";
            actionMessage = "You don't have this feature enabled";
        }
        return SUCCESS;
    }

    public String tbGetDispositions() {
//-----------------------------
        actionStatus = "Success";
        dispMap = toolBarManager.getDispositions(customer,null, agentId, did, campType, String.valueOf(campId));
        return SUCCESS;
    }

    public String tbSetDisposition() {
//------------------------------  
        try {
            if (userManager.hasTicketRole(customer) && (ticketId != null)) {

                log.info("Has to save the ticket with ticket id:" + ticketId);

                final Long finalRefID = ticketId;

                if (StringUtils.equalsIgnoreCase(ticketType, "new")) {
                    new Thread(new Runnable() {
                        public void run() {
                            toolBarManager.openTicket(finalRefID, customer,
                                    agentId, custNumber, ticketDesc, Long.valueOf(ucid), null, ticketCmt);
                        }
                    }).start();
                } else {

                    new Thread(
                            new Runnable() {
                        public void run() {
                            toolBarManager.updateTicket(finalRefID, ticketStatus, customer, Long.valueOf(ucid), null, ticketCmt, agentId, custNumber);
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (dispositionCode != null && !dispositionCode.equals("")) {
            actionStatus = toolBarManager.setDisposition(null,"" + dataId, dispositionCode, callBackTime, ucid, did, dispComments, agentId, customer, ticketId, null, monitorUcid,callbackTz);
        } else {
            actionStatus = "fail";
        }

        return SUCCESS;
    }

    public String tbGetTransferList() {
//------------------------------          
        try {
            switch (transferType) {
                case 1:
                    transferList = toolBarManager.getAgentTransferList(customer, did, agentId);
                    break;
                case 2:
                    transferList = toolBarManager.getSkillTransferList(customer, did, agentId);
                    break;
                default:
                    transferNumList = toolBarManager.getPhoneTransferList(customer);
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            actionStatus = "Fail";
            actionMessage = e.getMessage();
        }
        return SUCCESS;
    }

    public String tbGetConferenceList() {
//------------------------------          
        switch (confType) {
            case 1:
                transferList = toolBarManager.getAgentTransferList(customer, did, agentId);
                break;
            case 3:
                transferNumList = toolBarManager.getPhoneTransferList(customer);
                break;
            default:
                actionMessage = "Wrong conference type:" + confType;
                break;

        }
        return SUCCESS;
    }

    public String tbSetTransfer() {
//------------------------------
        if (StringUtils.isNotBlank(ucid)) {
            actionMessage = toolBarManager.setTransfer(ucid, did, customer, String.valueOf(transferType), transferId, transferNumber, blindTransfer);
            actionStatus = "Success";
        } else {
            actionMessage = "Transfer failed,cause:UCID is blank";
            actionStatus = "Fail";
        }
        return SUCCESS;
    }

    public String tbPreviewDial() {
//------------------------------        
//        actionStatus = previewDialerManager.dial(customer, agentId, phoneNumber, String.valueOf(dataId));
        return SUCCESS;
    }

    public String tbManualDial() {
//------------------------------        
        actionStatus = previewDialerManager.manualDial(customer, agentId, custNumber, phoneNumber, String.valueOf(campId));
        return SUCCESS;
    }

    public String tbGetPreviewCampaigns() {
//------------------------------        
        List<Campaign> campaigns = campaignManager.getPreviewCampaignsByAgentId(customer, agentId);
        for (Campaign campaign : campaigns) {
            int dataSize = 0;
            this.campaigns = new ArrayList<>();
            try {
                dataSize = toolBarManager.getPreviewDataSize(agentId, campaign.getCampaignId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (dataSize > 0) {
                this.campaigns.add(new CampaignInfo(campaign.getCampaignId(), campaign.getCampignName(), toolBarManager.getPreviewDataSize(agentId, campaign.getCampaignId())));
            } else // the data is zero check whether the campaign is completed if so make the campaign as completed
             if (previewDataManager.isPreviewCampaignCompleted(campaign.getCampaignId())) {
                    campaign.setPosition("COMPLETED");
                    campaignManager.save(campaign);
                }
        }
        return SUCCESS;
    }

    public String tbGetCampaigns() {
//------------------------------           
        List<Campaign> tmpCampList = campaignManager.getCampaignsByAgentId(customer, agentId);
        campaigns = new ArrayList<>();
        for (Campaign campaign : tmpCampList) {
            if (!campaign.isOffLineMode()) {
                campaigns.add(new CampaignInfo(campaign.getCampaignId(), campaign.getCampignName()));
            }
        }
        return SUCCESS;
    }

    public String tbSetAgentMode() {
//------------------------------     
//        prepareResponseAndMessage(toolBarManager.tbSetAgentMode(customer, agentId, agentMode, agentState, false, null));
        return SUCCESS;
    }

    public String tbResetPreviewNumber() {
//------------------------------        
        actionStatus = toolBarManager.tbResetPreviewNumber(String.valueOf(dataId));
        return SUCCESS;
    }

    public String tbSkipPreviewNumber() {
//------------------------------        
        actionStatus = toolBarManager.tbSkipPreviewNumber(dataId, "Skipped by:" + agentId);
        return SUCCESS;
    }

    public String tbGetCustomerData() {
//-----------------------------        
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("dataId", dataId);
        List<PreviewExtraData> custDataList = previewExtraDataManager.findByNamedQuery("getCustomerData", params);
        if (custDataList != null && !custDataList.isEmpty()) {
            PreviewExtraData extraData = custDataList.get(0);
            PreviewDataMap dataMap = extraData.getPreviewData().getCampaign().getPreviewDataMap();
            String[] mapDataNames = dataMap.getValue().split(",");
            String[] mapDataValues = extraData.getData().split("~");
            custData = new ArrayList<>();
            int i = 0;
            Map<String, String> tmpMap = new HashMap<>(1);
            for (String s : mapDataNames) {
                tmpMap.put(s, (mapDataValues.length > i) ? mapDataValues[i].toString() : "");
                custData.add(tmpMap);
                i++;
            }
        }
        return SUCCESS;
    }

    public String tbGetCallBackList() {
//------------------------------        
        groupedCallbackList = toolBarManager.tbGetGroupedCallBackList(customer, agentId);
        return SUCCESS;
    }

    public String tbDeleteCallback() {
//------------------------------
        try {
            toolBarManager.tbDeleteCallback(customer, agentId, callbackId);
        } catch (Exception e) {
            actionStatus = "Fail";
            log.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    public String tbGetAgentCallHist() {
//------------------------------        
        agentCallHist = toolBarManager.getAgentCallHistory(customer, agentId);
        return SUCCESS;
    }

    public String tbRescheduleCallback() {
//------------------------------        
        JsonObject jsonObject = toolBarManager.tbResechduleCallback(customer, agentId, cbTime, callbackId, rsComment,callbackTz);
        actionStatus = jsonObject.get("status").getAsString();
        actionMessage = jsonObject.get("message").getAsString();
        return SUCCESS;
    }

    public String tbFailCallback() {
//------------------------------ 
        actionStatus = toolBarManager.tbFailCallback(customer, agentId, callbackId);
        return SUCCESS;
    }

    public String tbCallBackDial() {
//------------------------------      
        prepareResponseAndMessage(previewDialerManager.callBackDial("" + callbackId));
        return SUCCESS;
    }

    public String tbGetTicketID() {
//------------------------------      
        ticketId = toolBarManager.generateTicketId(new Long(ucid));
        return SUCCESS;
    }

    public String tbGetTicketDetails() {
//------------------------------    
        jSONTicketDetails = toolBarManager.getTicketDetails(ticketId, customer);
        return SUCCESS;
    }

    public String tbGetTicketByPhone() {
//------------------------------       

        ticketsByPhone = new Gson().fromJson(toolBarManager.getTicketDetailByPhone(customer, custNumber), new TypeToken<List<MiniTicket>>() {
        }.getType());
        return SUCCESS;
    }

    public String tbHoldCall() {
//------------------------------      
        Map mp = new HashMap<>();
        mp.put("holdAction", holdAction);
        mp.put("ucid", ucid);
        mp.put("monitorUcid", monitorUcid);
        mp.put("phoneNumber", custNumber);
        mp.put("agentId", agentId);
        mp.put("username", customer);
        mp.put("did", did);
        mp.put("confType", confType);
        Command c = new HoldManager(mp);
        prepareResponseAndMessage(c.execute());
        return SUCCESS;
    }

    public String tbConference() {
//------------------------------ 
        Map mp = new HashMap<>();
        mp.put("holdAction", holdAction);
        mp.put("ucid", ucid);
        mp.put("monitorUcid", monitorUcid);
        mp.put("phoneNumber", custNumber);
        mp.put("agentId", agentId);
        mp.put("username", customer);
        mp.put("did", did);
        mp.put("confType", confType);
        Command c = new HoldManager(mp);
        String response = c.execute();
        String details;
        JsonObject jsonObject = null;
        if (StringUtils.equalsIgnoreCase(holdAction, "CONFERENCE")) {
            log.debug("Conference response:" + response);
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response);
            jsonObject = jsonElement.getAsJsonObject();
            details = jsonObject.get("status").toString().replaceAll("\"", "");
        } else {
            details = response;
        }
        if (!details.equalsIgnoreCase("success")) {
            log.debug("11111 Sending confernce response:" + details);
            if (StringUtils.equalsIgnoreCase(holdAction, "CONFERENCE")) {
                actionStatus = "Fail";
                actionMessage = details;
            } else {
                actionStatus = details;
            }
        } else {
            if (StringUtils.equalsIgnoreCase(holdAction, "CONFERENCE")) {
                setPhoneNumber(jsonObject.get("phone").toString().replaceAll("\"", ""));
            }
            actionStatus = "success";
        }

        return SUCCESS;
    }

    public String tbCheckManualDialStatus() {
//------------------------------        
        Command c = new CheckManualDialStatus(ucid);
        prepareResponseAndMessage(c.execute());
        return SUCCESS;
    }
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Utility Methods START!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private void prepareResponseAndMessage(String resp) {
        actionStatus = resp;
        actionMessage = resp;
        if (StringUtils.isNotBlank(resp) && StringUtils.split(resp, ":").length > 1) {
            actionStatus = WordUtils.capitalizeFully(StringUtils.split(resp, ":")[0]);
            actionMessage = WordUtils.capitalizeFully(StringUtils.split(resp, ":")[1]);
        }
    }

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Utility Methods END!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//
//-------------------------------Getters-START--------------------------------
    public JSONTicketDetails getjSONTicketDetails() {
        return jSONTicketDetails;
    }

    public AgentLoginResponse getAgentLoginResponse() {
        return agentLoginResponse;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAgentStatus() {
        return agentStatus;
    }

    public Map<String, String> getDispMap() {
        return dispMap;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getCustomer() {
        return customer;
    }

    public List<String> getTransferList() {
        return transferList;
    }

    public List<TransferNumber> getTransferNumList() {
        return transferNumList;
    }

    public List<CampaignInfo> getCampaigns() {
        return campaigns;
    }

    public List<Map<String, String>> getCustData() {
        return custData;
    }

    public List<CallbacksGrouped> getGroupedCallbackList() {
        return groupedCallbackList;
    }

    public List<Report> getAgentCallHist() {
        return agentCallHist;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public List<MiniTicket> getTicketsByPhone() {
        return ticketsByPhone;
    }

    //-------------------------------Getters-END----------------------------------
    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public void setBlindTransfer(int blindTransfer) {
        this.blindTransfer = blindTransfer;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getResponseToken() {
        return responseToken;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    // ------> Incoming parameters.
    public void setLogoutBy(String logoutBy) {
        this.logoutBy = logoutBy;
    }

    public void setUsId(String usId) {
        this.usId = usId;
    }

    public void setPauseReason(String pauseReason) {
        this.pauseReason = pauseReason;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public void setAgentMode(String agentMode) {
        this.agentMode = agentMode;
    }

    public void setToolBarManager(ToolBarManager toolBarManager) {
        this.toolBarManager = toolBarManager;
    }

//    public void setAgentNumber(String agentNumber) {
//        this.agentNumber = agentNumber;
//    }
    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public void setReleaseMsg(String releaseMsg) {
        this.releaseMsg = releaseMsg;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public void setCampId(Long campId) {
        this.campId = campId;
    }

    public void setSmsMsg(String smsMsg) {
        this.smsMsg = smsMsg;
    }

    public void setSmsDest(String smsDest) {
        this.smsDest = smsDest;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setCampType(String campType) {
        this.campType = campType;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public void setDispositionCode(String dispositionCode) {
        this.dispositionCode = dispositionCode;
    }

    public void setDispComments(String dispComments) {
        this.dispComments = dispComments;
    }

    public void setCallBackTime(String callBackTime) {
        this.callBackTime = callBackTime;
    }

    public void setCustNumber(String custNumber) {
        this.custNumber = custNumber;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public void setTicketDesc(String ticketDesc) {
        this.ticketDesc = ticketDesc;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }

    public void setTicketCmt(String ticketCmt) {
        this.ticketCmt = ticketCmt;
    }

    public void setPreviewDialerManager(PreviewDialerManager previewDialerManager) {
        this.previewDialerManager = previewDialerManager;
    }

    public void setPreviewDataManager(PreviewDataManager previewDataManager) {
        this.previewDataManager = previewDataManager;
    }

    public void setPreviewExtraDataManager(GenericManager<PreviewExtraData, Long> previewExtraDataManager) {
        this.previewExtraDataManager = previewExtraDataManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setConfType(Integer confType) {
        this.confType = confType;
    }

    public void setAgentState(String agentState) {
        this.agentState = agentState;
    }

    public void setCallbackId(Long callbackId) {
        this.callbackId = callbackId;
    }

    public void setCbTime(String cbTime) {
        this.cbTime = cbTime;
    }

    public void setRsComment(String rsComment) {
        this.rsComment = rsComment;
    }

    public void setHoldAction(String holdAction) {
        this.holdAction = holdAction;
    }

    public void setMonitorUcid(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCallbackTz() {
        return callbackTz;
    }

    public void setCallbackTz(String callbackTz) {
        this.callbackTz = callbackTz;
    }

    /**
     * Declare all properties here.
     */
    private String customer;
    private String agentId;
    private String responseToken;

    //
    //-------> Return params
    private AgentLoginResponse agentLoginResponse;
    private String actionStatus;
    private String actionMessage;
    private String agentStatus;
    private Map<String, String> dispMap;
    private List<String> transferList;
    private List<TransferNumber> transferNumList;
    private List<CampaignInfo> campaigns;

    private List<Map<String, String>> custData;
    private List<CallbacksGrouped> groupedCallbackList;
    private List<Report> agentCallHist;
    private Long ticketId;
    private JSONTicketDetails jSONTicketDetails;
    private List<MiniTicket> ticketsByPhone;

    // ------> Incoming parameters.
    private String transferId;
    private String transferNumber;
    private int blindTransfer;
    private String usId;//Agent client Id.
    private boolean reconnect;
    private String agentMode;
//    private String agentNumber;
    private String logoutBy;
    private String pauseReason;
    private Integer timeOut;
    private Event.AgentMode mode;
    private String releaseMsg;
    private String ucid;
    private Long campId;
    private String smsMsg;
    private String smsDest;
    private String did;
    private String campType;
    private Long dataId;
    private String dispositionCode;
    private String dispComments;
    private String callBackTime;
    private String custNumber;

    private String ticketType;
    private String ticketStatus;
    private String ticketDesc;
    private String ticketCmt;
    private Integer transferType;
    private Integer confType;
    private String agentState;
    private Long callbackId;
    private String cbTime;
    private String rsComment;

    private String holdAction;
    private String monitorUcid;
    private String callbackTz;
    /**
     * agent's phone number.
     */
    private String phoneNumber;

    // ------>  Managers
    private ToolBarManager toolBarManager;
    private PreviewDialerManager previewDialerManager;
    private PreviewDataManager previewDataManager;
    private CampaignManager campaignManager;
    private GenericManager<PreviewExtraData, Long> previewExtraDataManager;

}

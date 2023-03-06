package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.CampaignConfiguration;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.ManualDialService;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.util.DateUtil;
import com.ozonetel.occ.util.DndUtils;
import com.ozonetel.occ.util.HttpUtils;
import com.ozonetel.occ.util.KookooUtils;
import com.ozonetel.occ.util.PhoneNumberUtil;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author pavanj
 */
public class ManualDialServiceImpl implements ManualDialService {

    @Override
    public StatusMessage manualDial(String username, Long agentUniqId, String agentId, String agentMode, String custNumber, String agentNumber, boolean isSip, Long campaignId, String uui, boolean checkPrefix,String disclaimer) {
//------------------------------------- 
        Date startDate = new Date();
        String obUrl = null;
        Agent agent = null;

        try {
            agent = agentManager.get(agentUniqId);
            logger.debug("The Agent is : " + agent);
            if (agent.getFwpNumber() == null) {
                logger.debug("There is no FWP Number associated with the Agent:" + agent);
                return new StatusMessage(Status.ERROR, "There is an issue in Dialing, Please Re-Login and try.");
            }

            if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED") && !agentManager.lockIfAgentAvailable(username, agentId, custNumber)) {//check if agent is in blended mode.
                logger.debug("Agent is already in call:'" + agentId + "' belongs to user:" + username);
                return new StatusMessage(Status.ERROR, "Another call is initiated");
            }

            Campaign campaign = campaignManager.get(campaignId);
            if (phoneNumberUtil.isBlockedNumber(custNumber, campaignId)) {
                if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED")) {//unlock agent before return, because we are locking agent if he is in blended mode.
                    agentManager.releaseAgentLockWithDialStatus(username, agent.getId(), "BlockedNumber", false, false, 0, false, true);
                }

                saveManualDialErrorReport(username, agentUniqId, agentId, startDate, campaign, custNumber, "BlockedNumber", "BlockedNumber", "NotDialed");
                return new StatusMessage(Status.ERROR, custNumber + " is a black listed number");
            }

            boolean dndChecked = true;

            if (campaign.isDndCheck()) {
                StatusMessage statusMessage = dndUtils.checkDnd(custNumber);
                // ---- > If number is in DND return with 
                if (statusMessage.getStatus() == Status.SUCCESS && StringUtils.equalsIgnoreCase(statusMessage.getMessage(), "DND")) {
                    statusMessage.setStatus(Status.ERROR);
                    saveManualDialErrorReport(username, agentUniqId, agentId, startDate, campaign, custNumber, statusMessage.getMessage(), statusMessage.getMessage(), "NotDialed");

                    if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED")) {//unlock agent before return, because we are locking agent if he is in blended mode.
                        agentManager.releaseAgentLockWithDialStatus(username, agent.getId(), "BlockedNumber", false, false, 0, false, true);
                    }

                    return statusMessage;
                } else if (statusMessage.getStatus() == Status.ERROR) {
                    dndChecked = false;
                }

            }

            if (checkPrefix) {
                custNumber = StringUtils.isBlank(campaign.getCallPrefix()) ? custNumber : campaign.getCallPrefix() + custNumber;
            }

            URIBuilder callBackUrl = new URIBuilder(appProperty.getKooKooCallBackUrl() + "/OutBoundCallBack");
            callBackUrl.addParameter("cAgentId", agentId)
                    .addParameter("cDialNumber", custNumber);

            Date currentDate = new Date();//--> Start time should be considered before call is queued.

            URIBuilder manualApiUrl = new URIBuilder(appProperty.getPreviewDialerUrl());
            manualApiUrl.addParameter("cAgentId", agentId)
                    .addParameter("cAgentPhoneNumber", agentNumber)
                    .addParameter("cType", "ToolBarManual") //type is Preview or Inbound or Progressive or manual
                    .addParameter("cDid", campaign.getdId())
                    .addParameter("campaignId", campaign.getCampaignId().toString())
                    .addParameter("cDialNumber", custNumber)
                    .addParameter("priority", "1")
                    .addParameter("ip", campaign.getUser().getUrlMap().getLocalIp())
                    .addParameter("user", campaign.getUser().getUsername())
                    .addParameter("stime", DateUtil.convertDateToString(startDate, DateUtil.MYSQL_FORMAT))
                    .addParameter("uui", uui);
            manualApiUrl.addParameter("cCampAgentWise", (campaign.getDialMethod() == Campaign.DialMethod.Agentwise) + "");
            manualApiUrl.addParameter("discliamer",disclaimer);

            CampaignConfiguration campaignConfiguration = campaignConfigurationManager.get(campaign.getCampaignId());
            if (campaignConfiguration != null) {
                if (campaignConfiguration.isDidMasking() != null) {
                    manualApiUrl.setParameter("did_masking", campaignConfiguration.isDidMasking() ? "yes" : "no");
                }

                if (campaignConfiguration.getRingTime() != null) {
                    manualApiUrl.setParameter("ringtime", "" + campaignConfiguration.getRingTime());
                }

            }

            logger.debug("Adding callback url as " + callBackUrl.build().toString());
            logger.debug("Adding manual api  url as " + manualApiUrl.build().toString());
            URIBuilder builder = new URIBuilder(appProperty.getKooKooOutBoundUrl());
//            logger.debug("Accessing the Calls API Key : "+campaign.getUser().getCallapiKey());

            //builder.addParameter("agentId", agentId);
            builder.addParameter("phone_no", agentNumber)
                    //                    .addParameter("api_key", campaign.getUser().getApiKey())

                    .addParameter("api_key", campaign.getUser().getCallapiKey())
                    .addParameter("caller_id", campaign.getdId())
                    .addParameter("priority", "1")
                    .addParameter("callback_url", callBackUrl.build().toString())
                    .addParameter("dndChecked", "" + dndChecked)
                    .addParameter("isSip", "" + isSip)
                    .addParameter("url", manualApiUrl.build().toString());
            if (campaign.getFallbackDid() != null) {
                builder.addParameter("fallback_caller_id", campaign.getFallbackDid());
            }
            obUrl = builder.build().toString();
            HttpResponseDetails manualDialResponse = HttpUtils.doGet(obUrl);

            if (logger.isDebugEnabled()) {
                logger.debug("ManualDial -> " + obUrl + " | Response:" + manualDialResponse);
            }

            StatusMessage outboundResponse = KookooUtils.parseKookooResponse(manualDialResponse.getResponseBody());

            if (KookooUtils.parseKookooResponse(manualDialResponse.getResponseBody()).getStatus() == Status.QUEUED) {
                UpdateReport r = new UpdateReport();
                r.updateCallStatus(outboundResponse.getMessage(), outboundResponse.getMessage(), campaign.getdId(), agentId, custNumber, outboundResponse.getStatus().toString(), "", "0", "AgentHangup", uui, "false", "AgentDial", "ToolBarManual", 0, "not_answered", false, "", "NotDialed", Status.QUEUED.toReadableString(), campaign.getCampaignId().toString(), 0L, 0L, currentDate, currentDate, currentDate, currentDate, null);

            } else {
                saveManualDialErrorReport(username, agentUniqId, agentId, startDate, campaign, custNumber, outboundResponse.getMessage(), "NotDialed", outboundResponse.getMessage());
                if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED")) {//unlock agent before return, because we are locking agent if he is in blended mode.
                    agentManager.releaseAgentLockWithDialStatus(username, agent.getId(), "Error", false, false, 0, false, true);
                }
            }

            return outboundResponse;

        } catch (Exception ex) {
            if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED")) {//unlock agent before return, because we are locking agent if he is in blended mode.
                agentManager.releaseAgentLockWithDialStatus(username, agent.getId(), "Error", false, false, 0, false, true);
            }
            logger.error("Outbound request fail : " + obUrl + " -> " + ex.getMessage(), ex);
            return new StatusMessage(Status.EXCEPTION, "Please try again.");
        }
    }

    private void saveManualDialErrorReport(String username, Long agentUniqueId, String agentId, Date startDate, Campaign campaign,
            String customerNumber, String dialStatus, String customerStatus, String agentStatus) {
        Agent agent = null;
        if (agentUniqueId != null) {
            agent = agentManager.get(agentUniqueId);
        } else {
            agent = agentManager.getAgentByAgentIdV2(username, agentId);
        }
        long monitorUcid = (System.currentTimeMillis() * 1000) + (100 + new Random().nextInt(900));
        Report report = new Report();
        report.setAgent(agent);
        report.setAgentId(agent != null ? agent.getAgentId() : agentId);
        report.setAgentStatus(agentStatus);
        if (agent != null) {
            report.setFwpNumber(agent.getFwpNumber());
        }
        report.setUcid(monitorUcid);
        report.setMonitorUcid(monitorUcid);
        report.setAudioFile("-1");
        report.setBlindTransfer(0);
        report.setCallCompleted(true);
        report.setCallDate(startDate);
        report.setCall_data("Manual Dial");
        report.setCampaignId(campaign.getCampaignId());
        report.setCustomerStatus(customerStatus);
        report.setDest(customerNumber);
        report.setDialStatus(dialStatus);
        report.setDid(campaign.getdId());
        report.setEndTime(new Date());
        report.setHangUpBy("SystemHangup");
        report.setOffline(campaign.isOffLineMode());
        report.setStatus("Fail");
        report.setTransferType(0L);
        report.setTransferNow(false);
        report.setType("Manual");
        report.setUui("Manual Dial");
        report.setUser(campaign.getUser());
        reportManager.save(report);
    }

    @Override
    public StatusMessage checkDialStatus(String ucid) {
//-------------------------------------         
        try {
            HttpResponseDetails httpResponseDetails = HttpUtils.doGet(appProperty.getManualDialCheckUrl() + ucid);

            JSONObject json = new JSONObject(httpResponseDetails.getResponseBody());
            String status = json.getString("status");
            String response = "";
            JSONArray jSONArray = json.getJSONArray("data");
            JSONObject jsonData = jSONArray.getJSONObject(0);

            if (status.equalsIgnoreCase("0")) {// No Call Found Close the Call
                response = "Call Completed:" + jsonData.getString("CallStatus");
            } else {// 
                response = "Call in Progress:" + jsonData.getString("CallStatus");
            }

            return new StatusMessage(Status.SUCCESS, response);

        } catch (IOException | JSONException ioe) {
            logger.error(ioe.getMessage(), ioe);
        }

        return new StatusMessage(Status.ERROR, "Not known");
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    public void setDndUtils(DndUtils dndUtils) {
        this.dndUtils = dndUtils;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setPhoneNumberUtil(PhoneNumberUtil phoneNumberUtil) {
        this.phoneNumberUtil = phoneNumberUtil;
    }

    public void setCampaignConfigurationManager(GenericManager<CampaignConfiguration, Long> campaignConfigurationManager) {
        this.campaignConfigurationManager = campaignConfigurationManager;
    }

    private CampaignManager campaignManager;
    private AppProperty appProperty;
    private static Logger logger = Logger.getLogger(ManualDialServiceImpl.class);
    private DndUtils dndUtils;
    private AgentManager agentManager;
    private ReportManager reportManager;
    private PhoneNumberUtil phoneNumberUtil;
    private GenericManager<CampaignConfiguration, Long> campaignConfigurationManager;
}

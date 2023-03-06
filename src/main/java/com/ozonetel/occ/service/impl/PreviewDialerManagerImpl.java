/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.ErrorReportService;
import com.ozonetel.occ.service.ManualDialService;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.PreviewDialerManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.util.AppContext;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

/**
 *
 * @author root
 */
public class PreviewDialerManagerImpl extends OCCManagerImpl implements PreviewDialerManager {

    public PreviewDialerManagerImpl() {
        super();
    }

    @Override
    public void initialize() {
        errorReportService = (ErrorReportService) AppContext.getApplicationContext().getBean("errorReportService");
        super.initialize();
    }

    public Object getBean(String name) {

//        ApplicationContext ctx =
//                ContextLoader.getCurrentWebApplicationContext();
        ApplicationContext ctx = AppContext.getApplicationContext();
        return ctx.getBean(name);
    }

    public void updatePreivewDialStatus(String ucid, String did, String phoneNumber, Date startTime, String Status) {
        reportManager = (ReportManager) getBean("reportManager");
        Map<String, Object> reportParams = new HashMap<String, Object>();
        reportParams.put("ucid", new Long(ucid));
        reportParams.put("did", did);
        List<Report> reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);
        if (reports.size() > 0) {
            log.debug("[" + did + "][" + ucid + "] Already Present");
        } else {
            log.debug("[" + did + "][" + ucid + "] New Preview Dialing");
            Report r = new Report();
            r.setUcid(new Long(ucid));
            r.setMonitorUcid(new Long(ucid));
            r.setCallDate(startTime);
            r.setStatus(Status);
            r.setCall_data("Preview");
            r.setDid(did);
            r.setDest(phoneNumber);
        }
    }

    public String dial_old(String username, String agentId, String agentPhoneNumber, String previewId) {

        //cType: Preview , cAgentId: agentId , cDid:did , cDialNumber:callerId , cCampAgentWise:true/false
        log.debug("Called PreviewDial In ManagerImpl");
        PreviewDataManager previewDataManager = (PreviewDataManager) getBean("previewDataManager");
        PreviewData previewData = previewDataManager.get(new Long(previewId));
        if (previewData != null) {
            String campaignId = previewData.getCampaign() != null ? previewData.getCampaign().getCampaignId().toString() : null;
            AppProperty appProperty = (AppProperty) getBean("appProperty");
            String kookooOutBoundUrl = appProperty.getKooKooOutBoundUrl();
            StringBuilder callBackUrl = new StringBuilder();
            callBackUrl.append(appProperty.getKooKooCallBackUrl())
                    .append("OutBoundCallBack?cAgentId=").append(agentId)
                    .append("&cDialNumber=").append(previewData.getPhoneNumber());

            UserManager userManager = (UserManager) getBean("userManager");
//            String apiKey = userManager.getUserByUsername(username).getApiKey();
            String apiKey = userManager.getUserByUsername(username).getCallapiKey();
            StringBuilder outBoundAppUrl = new StringBuilder();
            outBoundAppUrl.append(appProperty.getPreviewDialerUrl());
            outBoundAppUrl.append("?cAgentId=").append(agentId)
                    .append("&cAgentPhoneNumber=").append(agentPhoneNumber)
                    .append("&cType=").append("preview") //type is Preview or Inbound or Progressive
                    .append("&cDid=").append(previewData.getCampaign().getdId())
                    .append("&cDialNumber=").append(previewData.getPhoneNumber())
                    .append("&cCampAgentWise=").append(previewData.getCampaign().getDialMethod() == Campaign.DialMethod.Agentwise);

            StringBuilder kUrl = new StringBuilder();
            try {
                URIBuilder kookooOutboundURIBuilder = new URIBuilder(kookooOutBoundUrl);
                kookooOutboundURIBuilder.addParameter("phone_no", agentPhoneNumber);
                kookooOutboundURIBuilder.addParameter("api_key", apiKey);
                kookooOutboundURIBuilder.addParameter("caller_id", previewData.getCampaign().getdId());
                kookooOutboundURIBuilder.addParameter("callback_url", callBackUrl.toString());
                kookooOutboundURIBuilder.addParameter("url", outBoundAppUrl.toString());
                kUrl = new StringBuilder(kookooOutboundURIBuilder.build().toString());
            } catch (URISyntaxException ex) {
                log.error(ex.getMessage(), ex);

                kUrl.append(kookooOutBoundUrl)
                        .append("&phone_no=").append(agentPhoneNumber)
                        .append("&api_key=").append(apiKey)
                        .append("&caller_id=").append(previewData.getCampaign().getdId())
                        .append("&callback_url=").append(URLEncoder.encode(callBackUrl.toString()))
                        .append("&url=").append(URLEncoder.encode(outBoundAppUrl.toString()));
            }

            HttpMethodParams hmp = new HttpClientParams();
            HttpClient msuClient = new HttpClient();
            HttpMethod method = new GetMethod(kUrl.toString());
            log.debug("kUrl=" + kUrl);

            String message = "";
            String status = "";
            try {
                int code = msuClient.executeMethod(method);
                log.debug("Code =" + code);
                String response = method.getResponseBodyAsString();
                log.debug("Response = " + response);

                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = (Document) docBuilder.parse(method.getResponseBodyAsStream());
                status = doc.getElementsByTagName("status").item(0).getTextContent();
                message = doc.getElementsByTagName("message").item(0).getTextContent();

                if (status.equalsIgnoreCase("queued")) {//this means
                    UpdateReport r = new UpdateReport();
                    r.updateCallStatus(message, message, previewData.getCampaign().getdId(), agentId, previewData.getPhoneNumber(), status, "", "0", "AgentHangup", "Preview Dial", "false", "AgentDial", "Preview", 0, message, false, previewData.getId().toString(), "", "", campaignId, new Long(0), new Long(0), new Date(), new Date(), new Date(), new Date(), null);
//                informToolBar("Preview", "No SKill", "AgentDial", previewData.getPhoneNumber(), "UUI Pending", message, message, previewData.getId().toString(), previewData.getCampaign().getdId(), campaignId, mp.get("ClientID"));
//                informToolBar("newCall", "0", status, agentId, status, apiKey, agentPhoneNumber, status, apiKey, campaignId, agentId) 
//              String resp = setAgentBusy(previewData.getCampaign().getdId(),agentId, previewData.getId().toString(), previewData.getPhoneNumber(), "" ,"","previewDial",message ,message ,"Preview-Dialer","0","Preview",0);
                    previewData.setUcid(new Long(message));
                }

                previewData.setStatus(status);
                previewData.setDialMessage(message);
                previewDataManager.save(previewData);

            } catch (Exception e) {
                log.error("Unable to Connect KooKoo Outbound due to some Errors..");
                e.printStackTrace();

            }

            return status + ":" + message;
        } else {
            return "ERROR";
        }

    }

    @Override
    public String dial(String username, Long agentUniqId, String agentId, String agentMode, String agentPhoneNumber, String previewId) {

        log.debug("Called PreviewDial In ManagerImpl for agent id:" + agentId);
        PreviewDataManager previewDataManager = (PreviewDataManager) getBean("previewDataManager");
        PreviewData previewData = previewDataManager.get(new Long(previewId));
        if (previewData != null) {

            if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED") && !agentManager.lockIfAgentAvailable(username, agentId, previewData.getPhoneNumber())) {//check if agent is in blended mode.
                log.debug("Agent is already in call:'" + agentId + "' belongs to user:" + username);
                return "Error:Unable to Dial now please try after some time!!!";
            }

            AppProperty appProperty = (AppProperty) getBean("appProperty");
            String dialerUrl = appProperty.getDialerUrl();

            StringBuilder kUrl = new StringBuilder();

            kUrl.append(dialerUrl)
                    .append("/informDialer?action=PreviewDial")
                    .append("&campaignId=").append(previewData.getCampaign().getCampaignId())
                    .append("&dataId=").append(previewData.getId())
                    .append("&agentId=").append(agentId)
                    .append("&agentPhoneNumber=").append(agentPhoneNumber);

            try {
                URIBuilder urib = new URIBuilder(kUrl.toString());

                String dialerServerAndPort = redisAgentManager.hget(Constants.REDIS_DIALER_SERVER_MAPPINGS_KEY, redisAgentManager.hget(Constants.REDIS_UESR_DIALER_MAP, username));
                urib.setHost(dialerServerAndPort.split(":")[0]);
                urib.setPort(Integer.valueOf(dialerServerAndPort.split(":")[1]));
                kUrl = new StringBuilder(urib.build().toString());
            } catch (URISyntaxException | NullPointerException | IndexOutOfBoundsException ex) {
                log.error(ex.getMessage(), ex);
                if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED")) {//unlock agent before return, because we are locking agent if he is in blended mode.
                    agentManager.releaseOnlyAgentFlag(agentUniqId);
                }
            }

            HttpClient msuClient = new HttpClient();
            HttpMethod method = new GetMethod(kUrl.toString());
            log.debug("kUrl=" + kUrl);

            String status = "";
            try {
                int code = msuClient.executeMethod(method);
                log.debug("Code =" + code);
                String response = method.getResponseBodyAsString();
                log.debug("Response = " + response);
                status = response;
                if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED") && StringUtils.containsIgnoreCase(response, "fail")) {
                    agentManager.releaseOnlyAgentFlag(agentUniqId);
                }
            } catch (Exception e) {
                log.error("Unable to Connect to KooKoo Outbound due to some Errors..:" + e.getMessage(), e);
                status = "Error:Unable to Dial now please try after some time!!!";
                if (StringUtils.equalsIgnoreCase(agentMode, "BLENDED")) {//unlock agent before return, because we are locking agent if he is in blended mode.
                    agentManager.releaseOnlyAgentFlag(agentUniqId);
                }
            }

            return status;
        } else {
            return "ERROR:Invalid dial";
        }

    }

    public String manualDial(String username, String agentId, String custNumber, String agentPhoneNumber, String campaignId) {

        //cType: Manual , cAgentId: agentId , cDid:did , cDialNumber:callerId , cCampAgentWise:true/false
        log.debug("Called Manual Dial In ManagerImpl");
        Campaign c = campaignManager.get(new Long(campaignId));
        if (c != null) {
            AppProperty appProperty = (AppProperty) getBean("appProperty");
            log.debug(appProperty.getKooKooOutBoundUrl() + "=" + appProperty.getKooKooOutBoundUrl());
            String kookooOutBoundUrl = appProperty.getKooKooOutBoundUrl();
            StringBuilder callBackUrl = new StringBuilder();
            callBackUrl.append(appProperty.getKooKooCallBackUrl())
                    .append("/OutBoundCallBack?cAgentId=").append(agentId)
                    .append("&cDialNumber=").append(custNumber);

            UserManager userManager = (UserManager) getBean("userManager");
//            String apiKey = userManager.getUserByUsername(username).getApiKey();
            String apiKey = userManager.getUserByUsername(username).getCallapiKey();
            StringBuilder outBoundAppUrl = new StringBuilder();
            outBoundAppUrl.append(appProperty.getPreviewDialerUrl());
            outBoundAppUrl.append("?cAgentId=").append(agentId)
                    .append("&cAgentPhoneNumber=").append(agentPhoneNumber)
                    .append("&cType=").append("ToolBarManual") //type is Preview or Inbound or Progressive or manual
                    .append("&cDid=").append(c.getdId())
                    .append("&campaignId=").append(c.getCampaignId())
                    .append("&cDialNumber=").append(custNumber)
                    .append("&priority=1")
                    .append("&ip=").append(c.getUser().getUrlMap().getLocalIp())
                    .append("&cCampAgentWise=").append(c.getDialMethod() == Campaign.DialMethod.Agentwise);

            URIBuilder builder = new URIBuilder();
            try {
                builder = new URIBuilder(kookooOutBoundUrl);
            } catch (URISyntaxException ex) {
                builder.setPath(kookooOutBoundUrl);
            }

            builder.addParameter("phone_no", agentPhoneNumber)
                    .addParameter("api_key", apiKey)
                    .addParameter("caller_id", c.getdId())
                    .addParameter("priority", "1")
                    .addParameter("callback_url", callBackUrl.toString())
                    .addParameter("url", outBoundAppUrl.toString());

            String finalOutboundURL = null;
            try {
                finalOutboundURL = builder.build().toString();
            } catch (URISyntaxException ex) {
                log.error(ex.getMessage(), ex);
                return "ERROR";

            }
            HttpClient msuClient = new HttpClient();
            HttpMethod method = new GetMethod(finalOutboundURL);
            log.debug("KooKoo outbound URL=" + finalOutboundURL);

            String message = "";
            String status = "";
            try {
                int code = msuClient.executeMethod(method);
                log.debug("Code =" + code);
                String response = method.getResponseBodyAsString();
                log.debug("Response = " + response);

                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = (Document) docBuilder.parse(method.getResponseBodyAsStream());
                status = doc.getElementsByTagName("status").item(0).getTextContent();
                message = doc.getElementsByTagName("message").item(0).getTextContent();

                if (status.equalsIgnoreCase("queued")) {//this means 
                    UpdateReport r = new UpdateReport();
                    r.updateCallStatus(message, message, c.getdId(), agentId, custNumber, status, "", "0", "AgentHangup", "Manual Dial", "false", "AgentDial", "ToolBarManual", 0, message, false, "", "", "", c.getCampaignId().toString(), new Long(0), new Long(0), new Date(), new Date(), new Date(), new Date(), null);
                }

            } catch (Exception e) {
                log.error("Unable to Connect KooKoo Outbound due to some Errors..");
                e.printStackTrace();
            }

            return status + ":" + message;
        } else {
            return "ERROR";
        }

    }

    @Override
    public String informDialer(final String username, final String agentId, final Long releasedFromCampaignId) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                String response = "Fail";
                if (username != null) {

                    AppProperty appProperty = (AppProperty) getBean("appProperty");
                    String dialerURL = appProperty.getDialerUrl() + "/informDialerByAgent?action=dialByAgent&userName=" + username;
                    try {
                        URIBuilder urib = new URIBuilder(dialerURL);
                        urib.addParameter("agentId", agentId);
                        urib.addParameter("infromedBy", "MessageServer");
                        urib.addParameter("informTimestamp", "" + System.currentTimeMillis());
                        if (releasedFromCampaignId != null) {
                            Integer dialInterval = campaignManager.get(releasedFromCampaignId).getDialInterval();
                            urib.addParameter("delay", "" + (dialInterval == null ? 0 : dialInterval));
                        }

                        String dialerServerAndPort = redisAgentManager.hget(Constants.REDIS_DIALER_SERVER_MAPPINGS_KEY, redisAgentManager.hget(Constants.REDIS_UESR_DIALER_MAP, username));
                        urib.setHost(dialerServerAndPort.split(":")[0]);
                        urib.setPort(Integer.valueOf(dialerServerAndPort.split(":")[1]));
                        dialerURL = urib.build().toString();
                    } catch (URISyntaxException | NullPointerException | IndexOutOfBoundsException ex) {
                        log.error(ex.getMessage(), ex);
                    }

                    HttpClient msuClient = new HttpClient();
                    log.debug("Informing dialer :" + dialerURL);
                    HttpMethod method = new GetMethod(dialerURL.toString());
                    try {

                        int code = msuClient.executeMethod(method);
                        log.debug("Code =" + code);
                        response = method.getResponseBodyAsString();
                        log.debug("Response = " + response);

                    } catch (Exception e) {
                        log.error("Unable to Connect Dailer due to some Errors..");
                        e.printStackTrace();

                    }

                }
            }

        }).start();
        return "SUCCESS";
    }

    public String informDialer(Long campaignId) {

        String response = "Fail";
        AppProperty appProperty = (AppProperty) getBean("appProperty");
        if (campaignId != null) {
            String dialerURL = appProperty.getDialerUrl() + "/informDialer";
            HttpMethodParams hmp = new HttpClientParams();
            HttpClient msuClient = new HttpClient();
            hmp.setParameter("campaignId", campaignId);
            hmp.setParameter("action", "dialByCampaignId");

            try {
                URIBuilder urib = new URIBuilder(dialerURL);

                String dialerServerAndPort = redisAgentManager.hget(Constants.REDIS_DIALER_SERVER_MAPPINGS_KEY, redisAgentManager.hget(Constants.REDIS_UESR_DIALER_MAP, campaignManager.get(campaignId).getUser().getUsername()));
                urib.setHost(dialerServerAndPort.split(":")[0]);
                urib.setPort(Integer.valueOf(dialerServerAndPort.split(":")[1]));
                dialerURL = urib.build().toString();
            } catch (URISyntaxException | NullPointerException | IndexOutOfBoundsException ex) {
                log.error(ex.getMessage(), ex);
            }

            HttpMethod method = new GetMethod(dialerURL.toString());
            method.setParams(hmp);
            try {

                int code = msuClient.executeMethod(method);
                log.debug("Code =" + code);
                response = method.getResponseBodyAsString();
                log.debug("Response = " + response);

            } catch (Exception e) {
                log.error("Unable to Connect Dailer due to some Errors..");
                e.printStackTrace();

            }

            return response;

        } else {
            return "ERROR";
        }
    }

    @Override
    public String callBackDial(String callBackId) {

        //callback Id
        log.debug("Going to do call back dial for call back with id --> " + callBackId);
        try {
            CallBack callBack = callBackManager.get(new Long(callBackId));
            if (callBack != null) {
                Agent agent = agentManager.get(callBack.getAgent().getId());//Agent comes from local DB. Callback stays in main house.
                String agentId = agent.getAgentId();
                String agentPhoneNumber = agent.getPhoneNumber();
                String customerNumber = callBack.getCallbackNumber();
                Long campaignId = callBack.getCampaign().getCampaignId();
                String username = callBack.getUser().getUsername();
                boolean isSip = agent.getFwpNumber() != null ? agent.getFwpNumber().isSip() : false;
                String uui = "Manual Dial";
                if (callBack.getUui() != null) {
                    uui = callBack.getUui();
                }
                log.debug("Finally The uui = " + uui);

                // --> as prefix is already saved in callback, passing false to the service.
                StatusMessage outboundResponse = manualDialService.manualDial(username, agent.getId(), agentId, agent.getMode().name(), customerNumber, agentPhoneNumber, isSip, campaignId, uui, false, "false");

                if (outboundResponse.getStatus() == Status.QUEUED) {
                    callBack.setCalled(true);
                    callBackManager.save(callBack);
                }

                return outboundResponse.getStatus() + ":" + outboundResponse.getMessage();
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            log.error("Unable to Connect KooKoo Outbound due to some Errors..");
            return "ERROR";

        }
    }

    public void setErrorReportService(ErrorReportService errorReportService) {
        this.errorReportService = errorReportService;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setManualDialService(ManualDialService manualDialService) {
        this.manualDialService = manualDialService;
    }

    protected ErrorReportService errorReportService;
    private RedisAgentManager redisAgentManager;
    private ManualDialService manualDialService;

}

package com.ozonetel.occ.service.impl;

import com.jamesmurty.utils.XMLBuilder;
import com.ozonetel.occ.dao.DispositionDao;
import com.ozonetel.occ.model.*;
import com.ozonetel.occ.service.*;
import com.ozonetel.occ.util.AppContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

@WebService(serviceName = "DispositionService", endpointInterface = "com.ozonetel.occ.service.DispositionManager")
public class DispositionManagerImpl extends GenericManagerImpl<Disposition, Long> implements DispositionManager {

    DispositionDao dispositionDao;
    private ReportManager reportManager;
    private UserManager userManager;
    private AgentManager agentManager;
    private OCCManager occManager;
    private ToolBarManager toolBarManager;

    public void setToolBarManager(ToolBarManager toolBarManager) {
        this.toolBarManager = toolBarManager;
    }

    public DispositionManagerImpl() {
        super(null);
    }

    private void init() {
        if (toolBarManager == null) {
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            toolBarManager = (ToolBarManager) webApplicationContext.getBean("toolBarManager");
            occManager = (OCCManager) webApplicationContext.getBean("occManager");
            occManager.initialize();
            toolBarManager.initialize();
        }

    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public DispositionManagerImpl(DispositionDao dispositionDao) {
        super(dispositionDao);
        this.dispositionDao = dispositionDao;
    }

    @Override
    public List<Disposition> getDispositionsByCampaign(Long campaignId) {
        return dispositionDao.getDispositionsByCampaign(campaignId);
    }

    @Override
    public List<Disposition> getActiveDispositionsByCampaign(Long campaignId) {
        Map<String, Object> dispParams = new HashMap<String, Object>();
        dispParams.put("campaignId", campaignId);
        dispParams.put("active", true);
        return findByNamedQuery("dispositionsByCampaignIdAndActive", dispParams);
    }

    @Override
    public List<Disposition> getActiveDispositionsWithoutCampaigns(Long campaignId) {
        return dispositionDao.getActiveDispositionsWithoutCampaigns(campaignId);
    }

    @Override
    public String setDispositionByApi(String dispositionCode, String dispComments, String ucid, String did,
            String customer, boolean pauseAfterDispose,
            String agentId, String pauseReason, String apiKey, String responseType , String callBackTz) {
        log.debug("Set dispositon request for ucid:" + ucid);

        String response = null;
        StatusMessage statusObject = null;
        try {
            init();
            User user = userManager.getUserByUsernameandAPIKey(customer, apiKey);
            if (user != null) {
                if (pauseAfterDispose == true && StringUtils.isBlank(pauseReason)) {
                    statusObject = new StatusMessage(Status.FAIL, "Provide pause reason");
                    log.debug("Status of disposition setting for ucid:" + ucid + " |" + statusObject);
                } else {
                    Agent agent = agentManager.getAgentByAgentIdUserId(user.getId(), agentId);
                    if (agent == null) {
                        statusObject = new StatusMessage(Status.FAIL, "Invalid agent");
                        log.debug("Invalid agent:" + agent + " for user:" + user + " | agentid->" + agentId);
                    } else {
                        statusObject = validateAndSaveDisp(dispositionCode, ucid, did, dispComments, customer, agent,callBackTz);
                        log.debug("Status of disposition setting for ucid:" + ucid + " |" + statusObject);

                        if (statusObject.getStatus() == Status.SUCCESS) {
                            if (pauseAfterDispose == true) {
                                response = agentManager.sendPauseAlertToAgent(customer, agent.getId(), agentId, pauseReason, agent.getClientId());
                                log.debug("Pause response:" + response + " while disposing for UCID:" + ucid);
                            } else {
                                log.info("Releasing agent with agent ID :'" + agentId + "' for customer:" + customer);
                                response = occManager.releaseAgentByAdmin(customer, "" + agent.getId(), "DispositionAPI");
                                log.info("Releasing agent with agent ID:" + agentId + " -> " + response);
                            }
                        } else {
                            log.info("Not releasing agent ");
                        }
                    }
                }

            } else {
                statusObject = new StatusMessage(Status.FAIL, "Authentication fail/Invalid user");
                log.debug("Authentication fails for ucid:" + ucid + " and user is:" + user);
            }

        } catch (Exception e) {
            statusObject = new StatusMessage(Status.ERROR, "Unable to Dispose . Please retry after some time.");
            log.error(e.getMessage(), e);
        }

        log.debug("Response Type = " + responseType + " and status object:" + statusObject + " | for ucid;:" + ucid + "| disp:" + dispositionCode);
        if (StringUtils.equalsIgnoreCase(responseType, "xml")) { //default json

            try {
                XMLBuilder responsXmlBuilder = XMLBuilder.create("response");
                responsXmlBuilder.e("status").text(statusObject.getStatus().toReadableString());
                if (StringUtils.equalsIgnoreCase("Success", statusObject.getMessage())) {
                    String[] dispositions = StringUtils.split(statusObject.getMessage(), ",");
                    if (dispositions == null || dispositions.length == 0) {
                        responsXmlBuilder.e("details").text("No dispositions found");
                    } else {
                        XMLBuilder dipositions = responsXmlBuilder.e("dispositions");
                        for (String disp : dispositions) {
                            dipositions.e("disposition").t(disp);
                        }
                    }

                } else {
                    responsXmlBuilder.e("details").text(statusObject.getMessage());
                }

                response = responsXmlBuilder.asString();

            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }

        } else {

            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("status", statusObject.getStatus().toReadableString());
                if (StringUtils.equalsIgnoreCase("Success", statusObject.getMessage())) {
                    jSONObject.put("details", StringUtils.split(statusObject.getMessage(), ","));
                } else {
                    jSONObject.put("details", statusObject.getMessage());
                }
            } catch (JSONException ex) {
                log.error(ex.getMessage(), ex);
            }

            response = jSONObject.toString();

        }

        return response;

    }

    private StatusMessage validateAndSaveDisp(String dispositionCode, String ucid, String did, String dispComments, String username, Agent agent,String callBackTz) {
        StatusMessage status = null;
        status = new StatusMessage(Status.SUCCESS, "Disposition saved successfully");
        if (StringUtils.isEmpty(dispositionCode)) {
            log.error("Not saving disposition as disposition  code is empty :[" + dispositionCode + "]" + " & Detials -> ucid:" + ucid + "|did:" + did);
            return new StatusMessage(Status.FAIL, "Empty disposition code");
        }

        log.debug("Setting Dispositions in Reports by ucid:" + ucid);
        if (StringUtils.isNotEmpty(ucid) && StringUtils.isNotEmpty(did)) {
            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("ucid", new Long(ucid));
            reportParams.put("did", did);
            List<Report> reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);
            if (CollectionUtils.isEmpty(reports)) {
                return new StatusMessage(Status.FAIL, "Invalid ucid [" + ucid + "] or did [" + did + "]");
            }
            Report tmpReport = reports.get(0);

            log.debug("Agent:" + agent + " | Report :" + tmpReport.toLongString() + "| Transfer type : "+tmpReport.getTransferType()+" -> for disp saving with ucid:" + ucid + " & did:" + did);
            log.debug("|| Same monitorUCID for agent and report?" + tmpReport.getMonitorUcid().equals(agent.getUcid()));
            if (tmpReport.getDisposition() != null && !tmpReport.getDisposition().isEmpty()) {
                return new StatusMessage(Status.FAIL, "This call is already Disposed.");
            } else if (!tmpReport.getMonitorUcid().equals(agent.getUcid())) {
                return new StatusMessage(Status.FAIL, "Agent is not on this call.");
            } else if (tmpReport.getTransferType() != 0 && agent.getState() != Agent.State.BUSY) {
                log.debug("This call is Transfered .....");
            } else if (!tmpReport.isCallCompleted()) {
                return new StatusMessage(Status.FAIL, "Agent is still on call.Please save disposition after call completion.");
            }

            toolBarManager.setDisposition(agent.getId(), null, dispositionCode,
                    null, ucid, did, dispComments, agent.getAgentId(), username, null, null, tmpReport.getMonitorUcid().toString(),callBackTz);

        } else {
            if (StringUtils.isEmpty(ucid) && StringUtils.isEmpty(did)) {
                log.error("Not saving disposition as empty  ucid & did is received UCID:[" + ucid + "],DID:[" + did + "]" + " & Detials -> ucid:" + ucid + "|did:" + did + "|Dispostion:" + dispositionCode);
                status = new StatusMessage(Status.FAIL, "Invalid ucid(" + ucid + ") & DID (" + did + ")");
            } else if (StringUtils.isEmpty(ucid)) {
                log.error("Error:Invalid ucid(" + ucid + ")" + " & Detials -> ucid:" + ucid + "|did:" + did + did + "|Dispostion:" + dispositionCode);
                status = new StatusMessage(Status.FAIL, "Invalid ucid(" + ucid + ")");
            } else {
                status = new StatusMessage(Status.FAIL, "Invalid DID (" + did + ")");
                log.error("Error:Invalid DID (" + did + ")" + " & Detials -> ucid:" + ucid + "|did:" + did + did + "|Dispostion:" + dispositionCode);
            }
            return status;
        }

        return status;
    }

}

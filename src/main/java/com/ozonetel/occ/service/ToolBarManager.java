/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import com.google.gson.JsonObject;
import com.ozonetel.occ.model.CallbacksGrouped;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.JSONTicketDetails;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This Interface is for Screen PopUp
 *
 * @author root
 */
public interface ToolBarManager extends OCCManager {

    public JsonObject tbAgentLogin(String username, String agentId, Long agentUniqId, String phoneNumber, String sessionId, String usId, boolean reconnect, Event.AgentMode mode, String loginIp);

    public String tbAgentLogout(String username, String agentId, Long agentUniqId, String phoneNumber, Event.AgentMode mode, String logoffMessage);

    public String tbAgentRelease(String username, String agentId, Long agentUniqId, Event.AgentMode mode, String releaseMessage);

    public StatusMessage tbAgentPause(String username, String agentId, Long agentUniqId, String reason, Event.AgentMode mode);

    /**
     * Set the agent in the AUX mode.This is to pause the agent while setting
     * disposition in ACW mode.
     *
     * @param username username
     * @param agentId agent_id
     * @return status message
     */
    public String tbAgentBusy(String username, String agentId);

    /**
     *
     * @param username
     * @param agentId
     * @param mode
     * @param state READY/PAUSE
     * @return
     */
    public String tbSetAgentMode(String username, String agentId, Long agentUniqId, String mode, String state, boolean reconnect, String reconnectDetails);

    public Map<String, String> getDispositions(String username, Long agentUniqId, String agentId, String did, String type, String campaignId);

//    public String setDisposition(Long agentUniqueId,String dataId, String dispositionCode, String callBackTime, String ucid, String did, String dispComments, String agentId, String username, Long ticketID);
    public String setDisposition(Long agentUniqueId, String dataId, String dispositionCode, String callBackTime, String ucid, String did, String dispComments, String agentId, String username, Long ticketID, String uui, String monitorUcid, String callBackTz);

    public PreviewData getNextPreviewNumber(String agentId, Long campaignId);

    public int getPreviewDataSize(String agentId, Long campaignId);

    public String tbResetPreviewNumber(String pId);

    public String tbSkipPreviewNumber(Long pid, String message);

    public List tbGetCustomerData(String dataId);

    public List<String> tbGetCallBackList(String username, String agentId);

    public List<CallbacksGrouped> tbGetGroupedCallBackList(String username, String agentId);

    public boolean tbDeleteCallback(String username, String agentId, Long cbId);

    public JsonObject tbResechduleCallback(String username, String agentId, String time, Long callbackId, String rescheduleComment,String callBackTz);

    public String tbFailCallback(String username, String agentId, Long callbackId);

    /**
     * This returns the ticket details required by the user. This are some
     * details from the Report
     *
     * @param ticketId TicketNO
     * @return ticket details
     */
    public JSONTicketDetails getTicketDetails(Long ticketId, String user);

    public String getTicketDetailByPhone(String user, String phoneNumber);

    public Long generateTicketId(Long ucid);

    public boolean sendTicketDetails(Long refID, Long ucid, String status, Long userID, Date startDate,
            Date updateDate, Date closeDate, Long agentID, String callerID, String comment, String disposition, String skillName,
            Long campaignID);

    public Long getAgentID(String user, String agentID);

    public String openTicket(Long ticketID, String username, String agent_id, String callerID, String desc, Long ucid, Long monitorUCID, String comment);

    public String updateTicket(Long ticketID, String status, String username, Long ucid, Long monitorUCID, String comment, String agent_id, String callerID);

    /**
     * Requests configured SMS_URL and returns the response.
     *
     * @param username
     * @param agentId
     * @param destination
     * @param msg
     * @param entityId
     * @param templateId
     * @return
     */
    public StatusMessage sendSMS(String username, BigInteger ucid, Long campaignId, String agentId, String destination, String msg, String entityId, String templateId);

    public StatusMessage sendWhatsappMSG(String username, String recipient, String templateName, String replacementText);

    public void alertAgentExceededPauseTime(Long agentUniqueId, String user, String agentId, String reason, int timeout);

    public List<Report> getAgentCallHistory(String user, String agentId);

    public Map<String, String> tbAgentReconnect(String sessionId, String usId, String username, String agentId, Long agentUniqId, String phoneNumber, Event.AgentMode mode, String event, Long ucid);

    public String tbClosePreviewNumber(Long pid, String message, String disp, String comment);
}

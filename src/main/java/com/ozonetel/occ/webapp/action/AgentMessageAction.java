package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AgentCommandExecutor;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentToolbarCommand;
import com.ozonetel.occ.service.AgentTransferManager;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.Dialer;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.ManualDialService;
import com.ozonetel.occ.service.PauseReasonManager;
import com.ozonetel.occ.service.PhoneTransferManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.PreviewExtraDataManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.SMSManager;
import com.ozonetel.occ.service.SMSTemplateManager;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.TicketManager;
import com.ozonetel.occ.service.TransferNumberManager;
import com.ozonetel.occ.service.command.AgentConferenceCommand;
import com.ozonetel.occ.service.command.AgentLoginCommand;
import com.ozonetel.occ.service.command.AgentLogoutCommand;
import com.ozonetel.occ.service.command.AgentPauseAlertCommand;
import com.ozonetel.occ.service.command.AgentPauseCommand;
import com.ozonetel.occ.service.command.AgentReleaseCommand;
import com.ozonetel.occ.service.command.BlindAgentTransferCommand;
import com.ozonetel.occ.service.command.BlindPhoneTransferCommand;
import com.ozonetel.occ.service.command.CallbackDialCommand;
import com.ozonetel.occ.service.command.CheckManualDialStatusCommand;
import com.ozonetel.occ.service.command.ConsultativeAgentTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldAgentTransferCommand;
import com.ozonetel.occ.service.command.DeleteCallbackCommand;
import com.ozonetel.occ.service.command.FailCallbackCommand;
import com.ozonetel.occ.service.command.GetAgentCallHistoryCommand;
import com.ozonetel.occ.service.command.GetCallbackListCommand;
import com.ozonetel.occ.service.command.GetCustomerDataCommand;
import com.ozonetel.occ.service.command.GetDispositionsCommand;
import com.ozonetel.occ.service.command.GetOnlineCampaignsCommand;
import com.ozonetel.occ.service.command.GetPreviewCampaignsCommand;
import com.ozonetel.occ.service.command.GetPreviewDataCommand;
import com.ozonetel.occ.service.command.GetTicketDetailsCommand;
import com.ozonetel.occ.service.command.GetTicketIdCommand;
import com.ozonetel.occ.service.command.GetTicketsByPhoneCommand;
import com.ozonetel.occ.service.command.GetTransferAgentListCommand;
import com.ozonetel.occ.service.command.GetTransferNumberListCommand;
import com.ozonetel.occ.service.command.GetTransferSkillListCommand;
import com.ozonetel.occ.service.command.HoldCommand;
import com.ozonetel.occ.service.command.KickCallCommand;
import com.ozonetel.occ.service.command.ManualDialCommand;
import com.ozonetel.occ.service.command.MuteCommand;
import com.ozonetel.occ.service.command.PhoneConferenceCommand;
import com.ozonetel.occ.service.command.PreviewDialCommand;
import com.ozonetel.occ.service.command.ReschduleCallbackCommand;
import com.ozonetel.occ.service.command.ResetPreviewNumberCommand;
import com.ozonetel.occ.service.command.SendSMSCommand;
import com.ozonetel.occ.service.command.SetAgentModeCommand;
import com.ozonetel.occ.service.command.SetDispositionCommand;
import com.ozonetel.occ.service.command.SkipPreviewNumberCommand;
import com.ozonetel.occ.service.command.UnHoldCommand;
import com.ozonetel.occ.service.command.UnMuteCommand;
import com.ozonetel.occ.service.command.response.AgentToolbarResponse;
import com.ozonetel.occ.service.impl.Status;
import com.ozonetel.occ.util.DateUtil;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;

/**
 *
 * @author pavanj
 */
public class AgentMessageAction extends BaseAction implements Preparable {

    @Override
    public void prepare() throws Exception {
        if (StringUtils.isNotBlank(mode)) {
            try {
                agentMode = Event.AgentMode.valueOf(mode);
                log.trace("Mode:" + agentMode);
            } catch (IllegalArgumentException e) {
                LOG.error(e.getMessage(), e);
            }
        }

    }

    @Override
    public String execute() throws Exception {

        AgentToolbarCommand agentToolbarCommand = null;
        log.debug("User Manager:" + userManager);
        switch (type) {

            case "tbAgentLogin":
                agentToolbarCommand = new AgentLoginCommand(customer, agentId, phoneNumber, "fake." + customer + "." + agentId, Boolean.valueOf(reconnect), agentMode, agentManager, userManager, sMSTemplateManager, pauseReasonManager);
                break;
            case "tbAgentLogout":
                agentToolbarCommand = new AgentLogoutCommand(customer, agentId, phoneNumber, agentMode, logoutBy, agentManager);
                break;
            case "tbAgentPause":
                agentToolbarCommand = new AgentPauseCommand(customer, agentId, reason, agentMode, agentManager);
                break;
            case "tbAgentRelease":
                agentToolbarCommand = new AgentReleaseCommand(customer, agentId, releaseMsg, agentMode, agentManager);
                break;
            case "tbGetMonitor":
                break;
            case "tbSendSMS":
                agentToolbarCommand = new SendSMSCommand(customer, agentId, new BigInteger(StringUtils.isBlank(ucid) ? "0" : ucid), campId == null ? 0L : campId, dest, msg, userManager, sMSManager);
                break;
            case "tbGetDispositions":
                agentToolbarCommand = new GetDispositionsCommand(customer, agentId, did, mode, String.valueOf(campId), agentManager, userManager, campaignManager, dispositionManager);
                break;
            case "tbPauseAlert":
                agentToolbarCommand = new AgentPauseAlertCommand(customer, agentId, reason, timeout, agentManager, mailEngine);
                break;
            case "tbSetDisposition":

                agentToolbarCommand = new SetDispositionCommand(customer, agentId, tktId, dataId, disposition,
                        callbackTime, ucid, comments, previewDataManager, reportManager, callBackManager, userManager, agentManager);

                //TODO change to thread pool.
                if (tktId != null) {
                    new Thread(
                            new Runnable() {
                        public void run() {
                            StatusMessage statusMessage = null;
                            if (StringUtils.equalsIgnoreCase(tktType, "new")) {
                                statusMessage = ticketManager.openTicket(tktId, customer, agentId, callerId, Long.valueOf(ucid), Long.valueOf(monitorUcid), tktCmnt, tktDesc);
                                log.debug("New ticket creation status:" + statusMessage);
                            } else {
                                statusMessage = ticketManager.updateTicket(tktId, customer, agentId, callerId, Long.valueOf(ucid), Long.valueOf(monitorUcid), tktCmnt, tktStatus);
                                log.debug("Update ticket status:" + statusMessage);
                            }
                        }
                    }).start();

                }

                break;
            case "tbGetTransferSkillList":
                agentToolbarCommand = new GetTransferSkillListCommand(customer, agentId, campaignType, did, skillManager);
                break;
            case "tbGetTransferAgentList":
            case "tbGetConferenceAgentList":
                agentToolbarCommand = new GetTransferAgentListCommand(customer, null,agentId, agentManager);
                break;
            case "tbGetTransferNumberList":
            case "tbGetConferencePhoneList":
                agentToolbarCommand = new GetTransferNumberListCommand(customer, agentId, transferNumberManager);
                break;
            case "tbPreviewDial":
                agentToolbarCommand = new PreviewDialCommand(customer, agentId, phoneNumber, Long.valueOf(dataId), customerNumber, dialer);
                break;
            case "tbManualDial":
//                agentToolbarCommand = new ManualDialCommand(customer, agentId, agentMode.toString(), phoneNumber, sip, customerNumber, campId, manualDialService);
                break;
            case "tbGetPreviewCampaigns":
                agentToolbarCommand = new GetPreviewCampaignsCommand(customer, agentId, previewDataManager, campaignManager);
                break;
            case "tbGetPreviewNumber":
                agentToolbarCommand = new GetPreviewDataCommand(customer, agentId, campId, previewDataManager, campaignManager);
                break;
            case "tbGetCampaigns":
                agentToolbarCommand = new GetOnlineCampaignsCommand(customer, agentId, campaignManager);
                break;
            case "tbSetAgentMode":
                agentToolbarCommand = new SetAgentModeCommand(customer, agentId, agentMode.toString(), agentStatus, agentManager);
                break;
            case "tbResetPreviewNumber":
                agentToolbarCommand = new ResetPreviewNumberCommand(customer, agentId, Long.valueOf(dataId), previewDataManager, messageSource);
                break;
            case "tbSkipPreviewNumber":
                agentToolbarCommand = new SkipPreviewNumberCommand(customer, agentId, Long.valueOf(dataId), messageSource.getMessage("data.skipped.byAgent", new Object[]{agentId}, Locale.getDefault()), previewDataManager, messageSource);
                break;
            case "tbGetCustomerData":
                agentToolbarCommand = new GetCustomerDataCommand(Long.valueOf(dataId), customer, agentId, previewExtraDataManager);
                break;
            case "tbGetCallBackList":
                agentToolbarCommand = new GetCallbackListCommand(customer, agentId, callBackManager);
                break;
            case "deleteCallback":
                agentToolbarCommand = new DeleteCallbackCommand(customer, agentId, Long.valueOf(dataId), callBackManager, messageSource);
                break;
            case "tbRescheduleCallback":
                try {
                    agentToolbarCommand = new ReschduleCallbackCommand(customer, agentId, DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", cbTime),
                            cbId,
                            rsComment, callBackManager,null);
                } catch (NumberFormatException | ParseException ex) {
                    log.error(ex.getMessage(), ex);
                }
                break;
            case "tbGetAgentCallHist":
                agentToolbarCommand = new GetAgentCallHistoryCommand(customer, agentId, agentManager);
                break;
            case "tbFailCallback":
                agentToolbarCommand = new FailCallbackCommand(customer, agentId, cbId, callBackManager);
                break;
            case "tbCallBackDial":
                agentToolbarCommand = new CallbackDialCommand(customer, agentId, cbId, phoneNumber, callBackManager);
                break;
            case "tbGetTicketID":
                agentToolbarCommand = new GetTicketIdCommand(customer, agentId, Long.valueOf(ucid), reportManager);
                break;
            case "tbGetTicketDetails":
                agentToolbarCommand = new GetTicketDetailsCommand(customer, agentId, tikcetId, ticketManager);
                break;
            case "tbGetTicketByPhone":
                agentToolbarCommand = new GetTicketsByPhoneCommand(customer, agentId, searchPhone, ticketManager);
                break;
            case "tbHold":
                agentToolbarCommand = new HoldCommand(customer, agentId, new BigInteger(monitorUcid), new BigInteger(ucid), holdNumber, did, new BigInteger(holdCampId), telephonyManager, null);
                break;
            case "tbUnHold":
                agentToolbarCommand = new UnHoldCommand(customer, agentId, new BigInteger(monitorUcid), new BigInteger(ucid), holdNumber, did, telephonyManager);
                break;
            case "tbMute":
                agentToolbarCommand = new MuteCommand(customer, agentId, monitorUcid, ucid, did, phoneNumberToMute, telephonyManager);
                break;
            case "tbUnMute":
                agentToolbarCommand = new UnMuteCommand(customer, agentId, monitorUcid, ucid, did, phoneNumberToMute, telephonyManager);
                break;
            case "tbAgentConference":
//                agentToolbarCommand = new AgentConferenceCommand(customer, agentId, participantAgentId, monitorUcid, ucid, did, customerNumber, telephonyManager);
                break;
            case "tbPhoneConference":
                agentToolbarCommand = new PhoneConferenceCommand(customer, agentId,
                        transferNumberId, participantPhone, sip, monitorUcid, ucid, did, customerNumber, telephonyManager);
                break;
            case "tbKickCall":
                agentToolbarCommand = new KickCallCommand(customer, agentId, monitorUcid, ucid, did, phoneNumberToKick, telephonyManager);
                break;

            case "tbBlindAgentTransfer":
                agentToolbarCommand = new BlindAgentTransferCommand(customer, agentId, Long.valueOf(monitorUcid),
                        Long.valueOf(ucid), did, receiverAgentId, customerNumber, agentTransferManager, null);
                break;
            case "tbConsultativeAgentTransfer":
                agentToolbarCommand = new ConsultativeAgentTransferCommand(customer, agentId, Long.valueOf(monitorUcid),
                        Long.valueOf(ucid), did, receiverAgentId, customerNumber, agentTransferManager, null);
                break;
            case "tbConsultativeHoldAgentTransfer":
//                agentToolbarCommand = new ConsultativeHoldAgentTransferCommand(customer, agentId, Long.valueOf(monitorUcid),
//                        Long.valueOf(ucid), did, receiverAgentId, customerNumber, agentTransferManager,""+campId);
                break;
            case "tbBlindPhoneTransfer":
                agentToolbarCommand = new BlindPhoneTransferCommand(customer, agentId, Long.valueOf(monitorUcid),
                        Long.valueOf(ucid), did, transferNumberId, transferNumberName, receiverPhone, customerNumber, phoneTransferManager, null);
                break;
            case "tbConsultativePhoneTransfer":
                agentToolbarCommand = new BlindPhoneTransferCommand(customer, agentId, Long.valueOf(monitorUcid),
                        Long.valueOf(ucid), did, transferNumberId, transferNumberName, receiverPhone, customerNumber, phoneTransferManager, null);
                break;
            case "tbConsultativeholdPhoneTransfer":
                agentToolbarCommand = new BlindPhoneTransferCommand(customer, agentId, Long.valueOf(monitorUcid),
                        Long.valueOf(ucid), did, transferNumberId, transferNumberName, receiverPhone, customerNumber, phoneTransferManager, null);
                break;
            case "tbCheckManualDialStatus":
                agentToolbarCommand = new CheckManualDialStatusCommand(customer, agentId, ucid, manualDialService);
                break;
        }

        try {
            commandResponse = agentCommandExecutor.executeCommand(agentToolbarCommand);
            if (commandResponse != null) {
                commandResponse.setReqType(type);
                commandResponse.setCallbackArgs(callbackArgs);
                commandResponse.setCallbackfn(callbackfn);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SUCCESS;
    }

    public String getStatus() {
        if (commandResponse != null) {
            return commandResponse.getStatus().toReadableString();
        }
        return Status.ERROR.toReadableString();

    }

    public String getReqType() {
        return type;
    }

    public String getType() {
        return "response";
    }

    public AgentToolbarResponse getDetails() {
        return commandResponse;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public String getNs() {
        return ns;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setManualDialService(ManualDialService manualDialService) {
        this.manualDialService = manualDialService;
    }

    public void setTransferNumberManager(TransferNumberManager transferNumberManager) {
        this.transferNumberManager = transferNumberManager;
    }

    public void setTicketManager(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    public void setCallBackManager(CallBackManager callBackManager) {
        this.callBackManager = callBackManager;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setsMSTemplateManager(SMSTemplateManager sMSTemplateManager) {
        this.sMSTemplateManager = sMSTemplateManager;
    }

    public void setPauseReasonManager(PauseReasonManager pauseReasonManager) {
        this.pauseReasonManager = pauseReasonManager;
    }

    public void setsMSManager(SMSManager sMSManager) {
        this.sMSManager = sMSManager;
    }

    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    public void setPreviewDataManager(PreviewDataManager previewDataManager) {
        this.previewDataManager = previewDataManager;
    }

    public void setDialer(Dialer dialer) {
        this.dialer = dialer;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setPreviewExtraDataManager(PreviewExtraDataManager previewExtraDataManager) {
        this.previewExtraDataManager = previewExtraDataManager;
    }

    public void setTelephonyManager(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }

    public void setAgentTransferManager(AgentTransferManager agentTransferManager) {
        this.agentTransferManager = agentTransferManager;
    }

    public void setPhoneTransferManager(PhoneTransferManager phoneTransferManager) {
        this.phoneTransferManager = phoneTransferManager;
    }

    public void setAgentMode(Event.AgentMode agentMode) {
        this.agentMode = agentMode;
    }

    public void setReconnect(String reconnect) {
        this.reconnect = reconnect;
    }

    public void setLogoutBy(String logoutBy) {
        this.logoutBy = logoutBy;
    }

    public void setAgentCommandExecutor(AgentCommandExecutor agentCommandExecutor) {
        this.agentCommandExecutor = agentCommandExecutor;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setReleaseMsg(String releaseMsg) {
        this.releaseMsg = releaseMsg;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public void setMonitorUcid(String monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setCampId(Long campId) {
        this.campId = campId;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setTktId(Long tktId) {
        this.tktId = tktId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public void setCallbackTime(String callbackTime) {
        this.callbackTime = callbackTime;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setTktType(String tktType) {
        this.tktType = tktType;
    }

    public void setTktCmnt(String tktCmnt) {
        this.tktCmnt = tktCmnt;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public void setTktDesc(String tktDesc) {
        this.tktDesc = tktDesc;
    }

    public void setTktStatus(String tktStatus) {
        this.tktStatus = tktStatus;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public void setAgentStatus(String agentStatus) {
        this.agentStatus = agentStatus;
    }

    public void setCbTime(String cbTime) {
        this.cbTime = cbTime;
    }

    public void setCbId(Long cbId) {
        this.cbId = cbId;
    }

    public void setRsComment(String rsComment) {
        this.rsComment = rsComment;
    }

    public void setTikcetId(Long tikcetId) {
        this.tikcetId = tikcetId;
    }

    public void setSearchPhone(String searchPhone) {
        this.searchPhone = searchPhone;
    }

    public void setHoldNumber(String holdNumber) {
        this.holdNumber = holdNumber;
    }

    public void setParticipantAgentId(String participantAgentId) {
        this.participantAgentId = participantAgentId;
    }

    public void setTransferNumberId(Long transferNumberId) {
        this.transferNumberId = transferNumberId;
    }

    public void setReceiverAgentId(String receiverAgentId) {
        this.receiverAgentId = receiverAgentId;
    }

    public void setParticipantPhone(String participantPhone) {
        this.participantPhone = participantPhone;
    }

    public void setPhoneNumberToKick(String phoneNumberToKick) {
        this.phoneNumberToKick = phoneNumberToKick;
    }

    public void setTransferNumberName(String transferNumberName) {
        this.transferNumberName = transferNumberName;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public void setCommandResponse(AgentToolbarResponse commandResponse) {
        this.commandResponse = commandResponse;
    }

    public void setSip(boolean sip) {
        this.sip = sip;
    }

    public void setPhoneNumberToMute(String phoneNumberToMute) {
        this.phoneNumberToMute = phoneNumberToMute;
    }

    public void setCallbackfn(String callbackfn) {
        this.callbackfn = callbackfn;
    }

    public void setCallbackArgs(String callbackArgs) {
        this.callbackArgs = callbackArgs;
    }

    //
    // ----- > Managers pool
    private ManualDialService manualDialService;
    private TransferNumberManager transferNumberManager;
    private TicketManager ticketManager;
    private CallBackManager callBackManager;
    private ReportManager reportManager;
    private CampaignManager campaignManager;
    private AgentManager agentManager;
    private DispositionManager dispositionManager;

    private SMSTemplateManager sMSTemplateManager;
    private PauseReasonManager pauseReasonManager;
    private SMSManager sMSManager;

    private SkillManager skillManager;
    private PreviewDataManager previewDataManager;
    private Dialer dialer;
    private MessageSource messageSource;
    private PreviewExtraDataManager previewExtraDataManager;
    private TelephonyManager telephonyManager;
    private AgentTransferManager agentTransferManager;
    private PhoneTransferManager phoneTransferManager;
    private AgentCommandExecutor agentCommandExecutor;
    // ----- > Managers pool END <--------

    // ----> Most common <------
    private String ns;
    private String type;
    private String customer;
    private String phoneNumber;
    private String agentId;
    private String mode;
    private Event.AgentMode agentMode;
    // ----> Most common <------

    private String reconnect;
    private String logoutBy;
    private String reason;
    private String releaseMsg;
    private String ucid;
    private String monitorUcid;
    private String msg;
    private String dest;
    private Long campId;
    private String did;
    private Integer timeout;
    private Long tktId;
    private String dataId;
    private String disposition;
    private String callbackTime;
    private String comments;
    private String tktType;
    private String tktCmnt;
    private String callerId;
    private String tktDesc;
    private String tktStatus;
    private String campaignType;
    private String customerNumber;
    private String agentStatus;
    private String cbTime;
    private Long cbId;
    private String rsComment;
    private Long tikcetId;
    private String searchPhone;
    private String holdNumber;
    private String participantAgentId;
    private Long transferNumberId;
    private String receiverAgentId;
    private String participantPhone;
    private String phoneNumberToKick;
    private String transferNumberName;
    private String receiverPhone;
    private boolean sip;
    private String phoneNumberToMute;
    private AgentToolbarResponse commandResponse;
    private String callbackfn;
    private String callbackArgs;
    private String holdCampId;

    /**
     * @return the holdCampId
     */
    public String getHoldCampId() {
        return holdCampId;
    }

    /**
     * @param holdCampId the holdCampId to set
     */
    public void setHoldCampId(String holdCampId) {
        this.holdCampId = holdCampId;
    }

}

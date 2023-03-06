package com.ozonetel.occ.service.impl;

import com.google.gson.GsonBuilder;
import com.ozonetel.occ.service.command.response.AgentToolbarResponse;
import com.ozonetel.occ.service.command.AgentLogoutCommand;
import com.ozonetel.occ.service.command.AgentLoginCommand;
import com.ozonetel.occ.service.command.AgentReleaseCommand;
import com.ozonetel.occ.service.command.SendSMSCommand;
import com.ozonetel.occ.service.command.AgentPauseCommand;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentToolbarCommand;
import com.ozonetel.occ.service.AgentTransferManager;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.Dialer;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.MailEngine;
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
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.command.AgentConferenceCommand;
import com.ozonetel.occ.service.command.AgentPauseAlertCommand;
import com.ozonetel.occ.service.command.BlindAgentTransferCommand;
import com.ozonetel.occ.service.command.BlindPhoneTransferCommand;
import com.ozonetel.occ.service.command.CallbackDialCommand;
import com.ozonetel.occ.service.command.CheckManualDialStatusCommand;
import com.ozonetel.occ.service.command.ConsultativeAgentTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldAgentTransferCommand;
import com.ozonetel.occ.service.command.ConsultativeHoldPhoneTransferCommand;
import com.ozonetel.occ.service.command.DeleteCallbackCommand;
import com.ozonetel.occ.service.command.DropMeCommand;
import com.ozonetel.occ.service.command.FailCallbackCommand;
import com.ozonetel.occ.service.command.GetAgentCallHistoryCommand;
import com.ozonetel.occ.service.command.GetCallbackListCommand;
import com.ozonetel.occ.service.command.GetCustomerDataCommand;
import com.ozonetel.occ.service.command.GetOnlineCampaignsCommand;
import com.ozonetel.occ.service.command.GetDispositionsCommand;
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
import com.ozonetel.occ.service.command.SetAgentModeCommand;
import com.ozonetel.occ.service.command.SetDispositionCommand;
import com.ozonetel.occ.service.command.SkipPreviewNumberCommand;
import com.ozonetel.occ.service.command.UnHoldCommand;
import com.ozonetel.occ.service.command.UnMuteCommand;
import com.ozonetel.occ.util.DateUtil;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.token.Token;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author pavanj
 */
public class AgentMessageHandler implements WebSocketServerTokenListener, MessageSourceAware {

    @Override
    public void processToken(WebSocketServerTokenEvent wsste, final Token aToken) {

        try {
            String namespace = aToken.getNS();
            String agentCommand = aToken.getType();

            final String username = StringUtils.trim(aToken.getString("customer"));
            final String agentId = StringUtils.trim(aToken.getString("agentId"));
            final String agentPhoneNumber = StringUtils.trim(aToken.getString("phoneNumber"));

            log.debug(">>>>> Token:" + wsste);

            AgentToolbarCommand agentToolbarCommand = null;

            Event.AgentMode mode = null;
            if (StringUtils.isNotBlank(aToken.getString("mode"))) {
                try {
                    mode = Event.AgentMode.valueOf(StringUtils.upperCase(aToken.getString("mode")));
                } catch (IllegalArgumentException e) {
                    log.error("Error setting agent mode in Event(Logout Action ) ->" + e.getMessage(), e);
                }
            }
            switch (agentCommand) {
                case "tbAgentLogin":
                    agentToolbarCommand = new AgentLoginCommand(username, agentId, agentPhoneNumber, aToken.getString("usId"), Boolean.valueOf(aToken.getString("reconnect")), mode, agentManager, userManager, sMSTemplateManager, pauseReasonManager);
                    break;
                case "tbAgentLogout":
                    agentToolbarCommand = new AgentLogoutCommand(username, agentId, agentPhoneNumber, mode, aToken.getString("logoutBy"), agentManager);
                    break;
                case "tbAgentPause":
                    agentToolbarCommand = new AgentPauseCommand(username, agentId, aToken.getString("reason"), mode, agentManager);
                    break;
                case "tbAgentRelease":
                    agentToolbarCommand = new AgentReleaseCommand(username, agentId, aToken.getString("releaseMsg"), mode, agentManager);
                    break;
                case "tbGetMonitor":
                    break;
                case "tbSendSMS":
                    agentToolbarCommand = new SendSMSCommand(username, agentId, new BigInteger(StringUtils.isEmpty(aToken.getString("ucid")) ? "0" : aToken.getString("ucid")), aToken.getObject("campId") == null ? 0 : Long.valueOf(aToken.getObject("campId").toString()), aToken.getString("dest"), aToken.getString("msg"), userManager, sMSManager);
                    break;
                case "tbGetDispositions":
                    agentToolbarCommand = new GetDispositionsCommand(username, agentId, aToken.getString("did"), mode.toString(), aToken.getString("campId"), agentManager, userManager, campaignManager, dispositionManager);
                    break;
                case "tbPauseAlert":
                    agentToolbarCommand = new AgentPauseAlertCommand(username, agentId, aToken.getString("reason"), Integer.valueOf(aToken.getString("timeout")), agentManager, mailEngine);
                    break;
                case "tbSetDisposition":
                    final Long ticketId = aToken.getObject("tktId") != null ? Long.valueOf(aToken.getObject("tktId").toString()) : aToken.getLong("tktId");
                    agentToolbarCommand = new SetDispositionCommand(username, agentId, ticketId, aToken.getString("dataId"), aToken.getString("disposition"),
                            aToken.getString("callbackTime"), aToken.getString("ucid"), aToken.getString("comments"), previewDataManager, reportManager, callBackManager, userManager, agentManager);

                    //TODO change to thread pool.
                    if (ticketId != null) {
                        new Thread(
                                new Runnable() {
                            public void run() {
                                StatusMessage statusMessage = null;
                                if (StringUtils.equalsIgnoreCase(aToken.getString("tktType"), "new")) {
                                    statusMessage = ticketManager.openTicket(ticketId, username, agentId, aToken.getString("callerId"), Long.valueOf(aToken.getString("ucid")), Long.valueOf(aToken.getString("monitorUcid")), aToken.getString("tktCmnt"), aToken.getString("tktDesc"));
                                    log.debug("New ticket creation status:" + statusMessage);
                                } else {
                                    statusMessage = ticketManager.updateTicket(ticketId, username, agentId, aToken.getString("callerId"), Long.valueOf(aToken.getString("ucid")), Long.valueOf(aToken.getString("monitorUcid")), aToken.getString("tktCmnt"), aToken.getString("tktStatus"));
                                    log.debug("Update ticket status:" + statusMessage);
                                }
                            }
                        }).start();

                    }

                    break;
                case "tbGetTransferSkillList":
                    agentToolbarCommand = new GetTransferSkillListCommand(username, agentId, aToken.getString("campaignType"), aToken.getString("did"), skillManager);
                    break;
                case "tbGetTransferAgentList":
                case "tbGetConferenceAgentList":
                    agentToolbarCommand = new GetTransferAgentListCommand(username, null, agentId, agentManager);
                    break;
                case "tbGetTransferNumberList":
                case "tbGetConferencePhoneList":
                    agentToolbarCommand = new GetTransferNumberListCommand(username, agentId, transferNumberManager);
                    break;
                case "tbPreviewDial":
                    agentToolbarCommand = new PreviewDialCommand(username, agentId, agentPhoneNumber, Long.valueOf(aToken.getObject("dataId").toString()), aToken.getString("customerNumber"), dialer);
                    break;
                case "tbManualDial":
//                    agentToolbarCommand = new ManualDialCommand(username, agentId, mode.toString(), agentPhoneNumber, aToken.getBoolean("isSip"), aToken.getString("customerNumber"), aToken.getObject("campId") != null ? Long.valueOf(aToken.getObject("campId").toString()) : aToken.getLong("campId"), manualDialService);
                    break;
                case "tbGetPreviewCampaigns":
                    agentToolbarCommand = new GetPreviewCampaignsCommand(username, agentId, previewDataManager, campaignManager);
                    break;
                case "tbGetPreviewNumber":
                    agentToolbarCommand = new GetPreviewDataCommand(username, agentId, aToken.getObject("campId") != null ? Long.valueOf(aToken.getObject("campId").toString()) : aToken.getLong("campId"), previewDataManager, campaignManager);
                    break;
                case "tbGetCampaigns":
                    agentToolbarCommand = new GetOnlineCampaignsCommand(username, agentId, campaignManager);
                    break;
                case "tbSetAgentMode":
                    agentToolbarCommand = new SetAgentModeCommand(username, agentId, aToken.getString("mode"), aToken.getString("agentStatus"), agentManager);
                    break;
                case "tbResetPreviewNumber":
                    agentToolbarCommand = new ResetPreviewNumberCommand(username, agentId, aToken.getObject("dataId") != null ? Long.valueOf(aToken.getObject("dataId").toString()) : aToken.getLong("dataId"), previewDataManager, messageSource);
                    break;
                case "tbSkipPreviewNumber":
                    agentToolbarCommand = new SkipPreviewNumberCommand(username, agentId, aToken.getObject("dataId") != null ? Long.valueOf(aToken.getObject("dataId").toString()) : aToken.getLong("dataId"), messageSource.getMessage("data.skipped.byAgent", new Object[]{agentId}, Locale.getDefault()), previewDataManager, messageSource);
                    break;
                case "tbGetCustomerData":
                    agentToolbarCommand = new GetCustomerDataCommand(aToken.getObject("dataId") != null ? Long.valueOf(aToken.getObject("dataId").toString()) : aToken.getLong("dataId"), username, agentId, previewExtraDataManager);
                    break;
                case "tbGetCallBackList":
                    agentToolbarCommand = new GetCallbackListCommand(username, agentId, callBackManager);
                    break;
                case "deleteCallback":
                    agentToolbarCommand = new DeleteCallbackCommand(username, agentId, aToken.getObject("dataId") != null ? Long.valueOf(aToken.getObject("dataId").toString()) : aToken.getLong("dataId"), callBackManager, messageSource);
                    break;
                case "tbRescheduleCallback":
                    try {
                        agentToolbarCommand = new ReschduleCallbackCommand(username, agentId, DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", aToken.getString("cbTime")),
                                aToken.getObject("cbId") != null ? Long.valueOf(aToken.getObject("cbId").toString()) : aToken.getLong("cbId"),
                                aToken.getString("rsComment"), callBackManager,aToken.getString("callbacktz"));
                    } catch (NumberFormatException | ParseException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                    break;
                case "tbGetAgentCallHist":
                    agentToolbarCommand = new GetAgentCallHistoryCommand(username, agentId, agentManager);
                    break;
                case "tbFailCallback":
                    agentToolbarCommand = new FailCallbackCommand(username, agentId, aToken.getObject("cbId") != null ? Long.valueOf(aToken.getObject("cbId").toString()) : aToken.getLong("cbId"), callBackManager);
                    break;
                case "tbCallBackDial":
                    agentToolbarCommand = new CallbackDialCommand(username, agentId, aToken.getObject("cbId") != null ? Long.valueOf(aToken.getObject("cbId").toString()) : aToken.getLong("cbId"), agentPhoneNumber, callBackManager);
                    break;
                case "tbGetTicketID":
                    agentToolbarCommand = new GetTicketIdCommand(username, agentId, Long.valueOf(aToken.getString("ucid")), reportManager);
                    break;
                case "tbGetTicketDetails":
                    agentToolbarCommand = new GetTicketDetailsCommand(username, agentId, Long.valueOf(aToken.getObject("tikcetId").toString()), ticketManager);
                    break;
                case "tbGetTicketByPhone":
                    agentToolbarCommand = new GetTicketsByPhoneCommand(username, agentId, aToken.getString("searchPhone"), ticketManager);
                    break;
                case "tbHold":
                    agentToolbarCommand = new HoldCommand(username, agentId, new BigInteger(aToken.getString("monitorUcid")), new BigInteger(aToken.getString("ucid")), aToken.getString("holdNumber"), aToken.getString("did"), new BigInteger(aToken.getString("campId")), telephonyManager, null);
                    break;
                case "tbUnHold":
                    agentToolbarCommand = new UnHoldCommand(username, agentId, new BigInteger(aToken.getString("monitorUcid")), new BigInteger(aToken.getString("ucid")), aToken.getString("holdNumber"), aToken.getString("did"), telephonyManager);
                    break;
                case "tbMute":
                    agentToolbarCommand = new MuteCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("phoneNumberToMute"), telephonyManager);
                    break;
                case "tbUnMute":
                    agentToolbarCommand = new UnMuteCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("phoneNumberToMute"), telephonyManager);
                    break;
                case "tbAgentConference":
//                    agentToolbarCommand = new AgentConferenceCommand(username, agentId, aToken.getString("participantAgentId"), aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("customerNumber"), telephonyManager);
                    break;
                case "tbPhoneConference":
                    agentToolbarCommand = new PhoneConferenceCommand(username, agentId,
                            aToken.getObject("transferNumberId") != null ? Long.valueOf(aToken.getObject("transferNumberId").toString()) : aToken.getLong("transferNumberId"), aToken.getString("participantPhone"), Boolean.valueOf(aToken.getString("sip")), aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("customerNumber"), telephonyManager);
                    break;
                case "tbKickCall":
                    agentToolbarCommand = new KickCallCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"), aToken.getString("did"), aToken.getString("phoneNumberToKick"), telephonyManager);
                    break;

                case "tbDropHold":
                    agentToolbarCommand = new DropMeCommand(username, agentId, aToken.getString("monitorUcid"), aToken.getString("ucid"),
                            aToken.getString("did"), aToken.getString("agentNumber"), aToken.getString("customerNumber"), telephonyManager);
                    break;

                case "tbBlindAgentTransfer":
                    agentToolbarCommand = new BlindAgentTransferCommand(username, agentId, Long.valueOf(aToken.getString("monitorUcid")),
                            Long.valueOf(aToken.getString("ucid")), aToken.getString("did"), aToken.getString("receiverAgentId"), aToken.getString("customerNumber"), agentTransferManager, null);
                    break;
                case "tbConsultativeAgentTransfer":
                    agentToolbarCommand = new ConsultativeAgentTransferCommand(username, agentId, Long.valueOf(aToken.getString("monitorUcid")),
                            Long.valueOf(aToken.getString("ucid")), aToken.getString("did"), aToken.getString("receiverAgentId"), aToken.getString("customerNumber"), agentTransferManager, null);
                    break;
                case "tbConsultativeHoldAgentTransfer":
//                    agentToolbarCommand = new ConsultativeHoldAgentTransferCommand(username, agentId, Long.valueOf(aToken.getString("monitorUcid")),
//                            Long.valueOf(aToken.getString("ucid")), aToken.getString("did"), aToken.getString("receiverAgentId"), aToken.getString("customerNumber"), agentTransferManager,aToken.getString("campId"));
                    break;
                case "tbBlindPhoneTransfer":
                    agentToolbarCommand = new BlindPhoneTransferCommand(username, agentId, Long.valueOf(aToken.getString("monitorUcid")),
                            Long.valueOf(aToken.getString("ucid")), aToken.getString("did"), aToken.getObject("transferNumberId") != null ? Long.valueOf(aToken.getObject("transferNumberId").toString()) : aToken.getLong("transferNumberId"), aToken.getString("transferNumberName"), aToken.getString("receiverPhone"), aToken.getString("customerNumber"), phoneTransferManager, null);
                    break;
                case "tbConsultativePhoneTransfer":
                    agentToolbarCommand = new ConsultativeHoldPhoneTransferCommand(username, agentId, Long.valueOf(aToken.getString("monitorUcid")),
                            Long.valueOf(aToken.getString("ucid")), aToken.getString("did"), aToken.getObject("transferNumberId") != null ? Long.valueOf(aToken.getObject("transferNumberId").toString()) : aToken.getLong("transferNumberId"), aToken.getString("transferNumberName"), aToken.getString("receiverPhone"),
                            aToken.getBoolean("isSip"), aToken.getString("customerNumber"), phoneTransferManager, aToken.getString("campId"), null, "false", aToken.getString("callType"));
                    break;
                case "tbConsultativeholdPhoneTransfer":
                    agentToolbarCommand = new ConsultativeHoldPhoneTransferCommand(username, agentId, Long.valueOf(aToken.getString("monitorUcid")),
                            Long.valueOf(aToken.getString("ucid")), aToken.getString("did"), aToken.getObject("transferNumberId") != null ? Long.valueOf(aToken.getObject("transferNumberId").toString()) : aToken.getLong("transferNumberId"), aToken.getString("transferNumberName"), aToken.getString("receiverPhone"),
                            aToken.getBoolean("isSip"), aToken.getString("customerNumber"), phoneTransferManager, aToken.getString("campId"), null, "false", aToken.getString("callType"));
                    break;
                case "tbCheckManualDialStatus":
                    agentToolbarCommand = new CheckManualDialStatusCommand(username, agentId, aToken.getString("ucid"), manualDialService);
                    break;
            }

            AgentToolbarResponse commandResponse = null;

            commandResponse = agentCommandExecutor.executeCommand(agentToolbarCommand);
            if (commandResponse != null) {
                commandResponse.setNs(namespace);
                commandResponse.setReqType(agentCommand);
                commandResponse.setCallbackfn(aToken.getString("callbackfn"));
                commandResponse.setCallbackArgs(aToken.getString("callbackArgs"));

                log.debug("Response:" + commandResponse);

                Token responseToken = wsste.createResponse(aToken);
                responseToken.setString("status", commandResponse.getStatus().toReadableString());
                responseToken.setString("details", new GsonBuilder()
                        .setExclusionStrategies(new MyExclusionStrategy(User.class))
                        .create().toJson(commandResponse));
                log.debug("<<<<<<Sending token:" + responseToken);
                wsste.sendToken(responseToken);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Token responseToken = wsste.createResponse(aToken);
            responseToken.setString("status", "Fail");
            responseToken.setString("details", null);
            log.debug("<<<<<<Sending token:" + responseToken);
            wsste.sendToken(responseToken);
        }

    }

    @Override
    public void processOpened(WebSocketServerEvent wsse) {

    }

    @Override
    public void processPacket(WebSocketServerEvent wsse, WebSocketPacket wsp) {

    }

    @Override
    public void processClosed(WebSocketServerEvent wsse) {
        log.info("Client '" + wsse.getSessionId() + "' disconnected.");
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
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

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setMailEngine(MailEngine mailEngine) {
        this.mailEngine = mailEngine;
    }

    public void setPreviewDataManager(PreviewDataManager previewDataManager) {
        this.previewDataManager = previewDataManager;
    }

    public void setCallBackManager(CallBackManager callBackManager) {
        this.callBackManager = callBackManager;
    }

    public void setTicketManager(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    public void setTransferNumberManager(TransferNumberManager transferNumberManager) {
        this.transferNumberManager = transferNumberManager;
    }

    public void setDialer(Dialer dialer) {
        this.dialer = dialer;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setManualDialService(ManualDialService manualDialService) {
        this.manualDialService = manualDialService;
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

    public void setAgentCommandExecutor(AgentCommandExecutorImpl agentCommandExecutor) {
        this.agentCommandExecutor = agentCommandExecutor;
    }

    private AgentCommandExecutorImpl agentCommandExecutor;

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
    private UserManager userManager;
    private SMSTemplateManager sMSTemplateManager;
    private PauseReasonManager pauseReasonManager;
    private SMSManager sMSManager;
    private MailEngine mailEngine;
    private SkillManager skillManager;
    private PreviewDataManager previewDataManager;
    private Dialer dialer;
    private MessageSource messageSource;
    private PreviewExtraDataManager previewExtraDataManager;
    private TelephonyManager telephonyManager;
    private AgentTransferManager agentTransferManager;
    private PhoneTransferManager phoneTransferManager;
    // ----- > Managers pool END <--------

    private static Logger log = Logger.getLogger(AgentMessageHandler.class);

}

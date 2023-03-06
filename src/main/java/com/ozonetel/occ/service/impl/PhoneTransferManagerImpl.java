package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AgentTokenManager;
import com.ozonetel.occ.service.PhoneTransferManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.TelephonyManager;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author pavanj
 */
public class PhoneTransferManagerImpl implements PhoneTransferManager, MessageSourceAware {

    @Override
    public StatusMessage consultativeHoldTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhone, boolean isSip, String custNumber, String campId, String appAudioURL, String record, String callType) {
        //-------------------------------------           
        StatusMessage statusMessage = telephonyManager.phoneTransfer(username, agentId, transferNumberId, receiverPhone, isSip, monitorUcid.toString(), ucid.toString(), did, custNumber, true, campId, appAudioURL, record, callType);

//        // ----> Send unHold command to agent tool-bar.
//        Map<String, String> requestMap = new LinkedHashMap<>(1);
//        requestMap.put("custNumber", custNumber);
//        agentTokenManager.sendTokenToAgent(username, agentId, "unHoldCustomer", requestMap);
        TransferType transferType = TransferType.CONSULTATIVE_HOLD;
        if (statusMessage.getStatus() == Status.SUCCESS) {

            // ----> Save in Report table.
            saveInReport(ucid, did, transferType, receiverPhone);

            return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.transfer", new Object[]{transferType.toReadableString(), Participant.PHONE.toString().toLowerCase(), transferNumberName, receiverPhone}, Locale.getDefault()));
        }

        return new StatusMessage(Status.ERROR, messageSource.getMessage("fail.transfer", new Object[]{transferType.toReadableString(), statusMessage.getMessage()}, Locale.getDefault()));
    }

    @Override
    public StatusMessage consultativeTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhone, String custNumber, String appAudioURL) {
//-------------------------------------           
        TransferType transferType = TransferType.CONSULTATIVE;

        // ----> Save in Report table.
        saveInReport(Long.valueOf(ucid), did, transferType, receiverPhone);
        return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.transfer", new Object[]{transferType.toReadableString(), Participant.PHONE.toString().toLowerCase(), transferNumberName, receiverPhone}, Locale.getDefault()));
    }

    @Override
    public StatusMessage blindTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhone, String custNumber, String appAudioURL) {
//-------------------------------------           
        TransferType transferType = TransferType.BLIND;

        // ----> Save in Report table.
        saveInReport(Long.valueOf(ucid), did, transferType, receiverPhone);
        return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.transfer", new Object[]{transferType.toReadableString(), Participant.PHONE.toString().toLowerCase(), transferNumberName, receiverPhone}, Locale.getDefault()));
    }

    private void saveInReport(Long ucid, String did, TransferType transferType, String receiverPhone) {
//-------------------------------------         
        Report report = reportManager.getReportByUcidAndDid(Long.valueOf(ucid), did);
        report.setTransferType((long) Participant.PHONE.ordinal());
        report.setTransferNow(true);
        report.setBlindTransfer(transferType.ordinal());
        report.setTransferToNumber(receiverPhone);
        reportManager.save(report);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setTelephonyManager(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }

    public void setAgentTokenManager(AgentTokenManager agentTokenManager) {
        this.agentTokenManager = agentTokenManager;
    }

    private ReportManager reportManager;
    private MessageSource messageSource;
    private TelephonyManager telephonyManager;
    private AgentTokenManager agentTokenManager;
}

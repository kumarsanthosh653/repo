package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.PhoneTransferManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ConsultativeHoldPhoneTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ConsultativeHoldPhoneTransferCommand(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhoneNumber, boolean isSip, String custNumber, PhoneTransferManager phoneTransferManager, String campId, String appAudioURL, String record, String callType) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.transferNumberId = transferNumberId;
        this.transferNumberName = transferNumberName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.custNumber = custNumber;
        this.isSip = isSip;
        this.campId = campId;
        this.phoneTransferManager = phoneTransferManager;
        this.appAudioURL = appAudioURL;
        this.record = record;
        this.callType = callType;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(phoneTransferManager.consultativeHoldTransfer(username, agentId, monitorUcid, ucid, did, transferNumberId, transferNumberName, receiverPhoneNumber, isSip, custNumber, campId, appAudioURL, record, callType));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentInitiated", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("UCID", ucid)
                .append("did", did)
                .append("ReceiverPhoneId", transferNumberId)
                .append("ReceiverPhoneName", transferNumberName)
                .append("ReceiverPhoneNumber", receiverPhoneNumber)
                .append("CustomerNumber", custNumber)
                .toString();
    }

    private final Long monitorUcid;
    private final Long ucid;
    private final String did;
    private final String campId;
    private final Long transferNumberId;
    private final String transferNumberName;
    private final String receiverPhoneNumber;
    private final String custNumber;
    private final boolean isSip;
    private final PhoneTransferManager phoneTransferManager;
    private final String appAudioURL;
    private final String record;
    private final String callType;
}

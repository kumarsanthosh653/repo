package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.PhoneTransferManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ConsultativePhoneTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ConsultativePhoneTransferCommand(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhoneNumber, String custNumber, PhoneTransferManager phoneTransferManager, String appAudioURL) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.transferNumberId = transferNumberId;
        this.transferNumberName = transferNumberName;
        this.receiverPhone = receiverPhoneNumber;
        this.custNumber = custNumber;
        this.phoneTransferManager = phoneTransferManager;
        this.appAudioURL = appAudioURL;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(phoneTransferManager.consultativeTransfer(username, agentId, monitorUcid, ucid, did, transferNumberId, transferNumberName, receiverPhone, custNumber, appAudioURL));
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
                .append("ReceiverPhoneNumber", receiverPhone)
                .append("CustomerNumber", custNumber)
                .toString();
    }

    private final Long monitorUcid;
    private final Long ucid;
    private final String did;
    private final Long transferNumberId;
    private final String transferNumberName;
    private final String receiverPhone;
    private final String custNumber;
    private final PhoneTransferManager phoneTransferManager;
    private String appAudioURL;
}

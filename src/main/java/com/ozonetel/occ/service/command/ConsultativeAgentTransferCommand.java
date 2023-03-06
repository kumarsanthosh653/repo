package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentTransferManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ConsultativeAgentTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ConsultativeAgentTransferCommand(String username, String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, AgentTransferManager agentTransferManager, String appAudioURL) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.receiverAgentId = receiverAgentId;
        this.custNumber = custNumber;
        this.agentTransferManager = agentTransferManager;
        this.appAudioURL = appAudioURL;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(agentTransferManager.consultativeTransfer(username, agentId, monitorUcid, ucid, did, receiverAgentId, custNumber, appAudioURL));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentInitiated", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("UCID", ucid)
                .append("did", did)
                .append("ReceiverAgentId", receiverAgentId)
                .append("CustomerNumber", custNumber)
                .toString();
    }

    private final Long monitorUcid;
    private final Long ucid;
    private final String did;
    private final String receiverAgentId;
    private final String custNumber;
    private final AgentTransferManager agentTransferManager;
    private String appAudioURL;
}

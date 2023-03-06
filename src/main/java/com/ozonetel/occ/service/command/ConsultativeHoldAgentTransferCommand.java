package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentTransferManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ConsultativeHoldAgentTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ConsultativeHoldAgentTransferCommand(String username,  Long initiateAgentUniqid,String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, AgentTransferManager agentTransferManager, String campId, String appAudioURL, String record, String callType,String uui) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.receiverAgentId = receiverAgentId;
        this.custNumber = custNumber;
        this.campId = campId;
        this.initiateAgentUniqid = initiateAgentUniqid;
        this.agentTransferManager = agentTransferManager;
        this.appAudioURL = appAudioURL;
        this.record = record;
        this.callType = callType;
        this.uui=uui;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(agentTransferManager.consultativeHoldTransfer(username, initiateAgentUniqid, agentId, monitorUcid, ucid, did, receiverAgentId, custNumber,campId, appAudioURL, record, callType,uui));
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
                .append("uui", uui)
                .toString();
    }

    private final String record;
    private final Long monitorUcid;
    private final Long ucid;
    private final String did;
    private final String campId;
    private final String receiverAgentId;
    private final String custNumber;
    private  Long initiateAgentUniqid;
    private final AgentTransferManager agentTransferManager;
    private String appAudioURL;
    private String callType;
    private String uui;
}

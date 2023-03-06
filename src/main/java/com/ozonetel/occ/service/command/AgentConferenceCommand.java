package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.ConferenceCommandResponse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class AgentConferenceCommand extends AbstractAgentToolbarCommand<ConferenceCommandResponse> {

    public AgentConferenceCommand(String username, Long initiateAgentUniqId, String agentId, String participantAgentId, String monitorUcid, String ucid, String did, String customerNumber, TelephonyManager telephonyManager) {
        super(username, agentId);
        this.participantAgentId = participantAgentId;
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.customerNumber = customerNumber;
        this.initiateAgentUniqId = initiateAgentUniqId;
        this.telephonyManager = telephonyManager;
    }

    @Override
    public ConferenceCommandResponse execute() {
//------------------------------------- 
        return new ConferenceCommandResponse(telephonyManager.agentConference(username, initiateAgentUniqId, agentId, participantAgentId, monitorUcid, ucid, did, customerNumber, false));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Username", username)
                .append("AgentId", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("UCID", ucid)
                .append("DID", did)
                .append("CustomerNumber", customerNumber)
                .toString();
    }

    private final String participantAgentId;
    private final String monitorUcid;
    private final String ucid;
    private final String did;
    private final String customerNumber;
    private final Long initiateAgentUniqId;
    private final TelephonyManager telephonyManager;
}

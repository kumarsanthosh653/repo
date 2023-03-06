package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.ConferenceCommandResponse;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class PhoneConferenceCommand extends AbstractAgentToolbarCommand<ConferenceCommandResponse> {

    public PhoneConferenceCommand(String username, String agentId, Long transferNumberId, String participantPhoneNumber, boolean isSip, String monitorUcid, String ucid, String did, String customerNumber, TelephonyManager telephonyManager) {
        super(username, agentId);
        this.transferNumberId = transferNumberId;
        this.participantPhoneNumber = participantPhoneNumber;
        this.isSip = isSip;
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.customerNumber = customerNumber;
        this.telephonyManager = telephonyManager;
    }

    @Override
    public ConferenceCommandResponse execute() {
//-------------------------------------         
        return new ConferenceCommandResponse(telephonyManager.phoneConference(username, agentId, transferNumberId, participantPhoneNumber, isSip, monitorUcid, ucid, did, customerNumber, false));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("UCID", ucid)
                .append("DID", did)
                .append("TransferNumberId", transferNumberId)
                .append("ParticipantNumber", participantPhoneNumber)
                .append("CustomerNumber", customerNumber)
                .toString();
    }

    private final Long transferNumberId;
    private final String participantPhoneNumber;
    private final boolean isSip;
    private final String monitorUcid;
    private final String ucid;
    private final String did;
    private final String customerNumber;
    private final TelephonyManager telephonyManager;
}

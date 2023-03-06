package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class DropMeCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public DropMeCommand(String username, String agentId, String monitorUcid, String ucid, String did, String agentNumber, String customerNumber, TelephonyManager telephonyManager) {
        super(username, agentId);

        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.customerNumber = customerNumber;
        this.agentNumber = agentNumber;
        this.telephonyManager = telephonyManager;
    }

    @Override
    public AgentCommandStatus execute() {
//------------------------------------
//        StatusMessage statusMessage = telephonyManager.dorpHold(username, agentId, monitorUcid, ucid, did, agentNumber, customerNumber);
//        if (statusMessage.getStatus() == Status.ERROR && "404".equals(statusMessage.getMessage())) {
//            //TODO release agent here.
//        }
//        return new AgentCommandStatus(statusMessage);

        return new AgentCommandStatus(telephonyManager.dorpHold(username, agentId, monitorUcid, ucid, did, agentNumber, customerNumber));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Customer", username)
                .append("Agent", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("UCID", ucid)
                .append("DID", did)
                .append("CustomerNumber", customerNumber)
                .append("AgentNumber", agentNumber)
                .toString();
    }

    private final String monitorUcid;
    private final String ucid;
    private final String did;
    private final String customerNumber;
    private final String agentNumber;
    private final TelephonyManager telephonyManager;

}

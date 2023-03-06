package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class KickCallCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public KickCallCommand(String username, String agentId, String monitorUcid, String ucid, String did, String phoneNumberToKick, TelephonyManager telephonyManager) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.phoneNumberToKick = phoneNumberToKick;
        this.telephonyManager = telephonyManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(telephonyManager.kickCall(username, agentId, monitorUcid, ucid, did, phoneNumberToKick));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("UCID", ucid)
                .append("DID", did)
                .append("KickNumber", phoneNumberToKick)
                .toString();
    }

    private final String monitorUcid;
    private final String ucid;
    private final String did;
    private final String phoneNumberToKick;
    private final TelephonyManager telephonyManager;
}

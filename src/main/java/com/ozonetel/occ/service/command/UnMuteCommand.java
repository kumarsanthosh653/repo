package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class UnMuteCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public UnMuteCommand(String username, String agentId, String monitorUcid, String ucid, String did, String phoneNumberMute, TelephonyManager telephonyManager) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.phoneNumberToUnMute = phoneNumberMute;
        this.telephonyManager = telephonyManager;
    }
 public UnMuteCommand(String username, Long agentUniqueId,String agentId, String monitorUcid, String ucid, String did, String phoneNumberMute, TelephonyManager telephonyManager) {
        super(username,agentUniqueId, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.phoneNumberToUnMute = phoneNumberMute;
        this.telephonyManager = telephonyManager;
    }
    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(telephonyManager.unMute(username,agentUniqueId, agentId, monitorUcid, ucid, phoneNumberToUnMute, did));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("DID", did)
                .append("UCID", ucid)
                .append("NumberToMute", phoneNumberToUnMute)
                .toString();
    }

    private final String monitorUcid;
    private final String ucid;
    private final String did;
    private final String phoneNumberToUnMute;
    private final TelephonyManager telephonyManager;
}

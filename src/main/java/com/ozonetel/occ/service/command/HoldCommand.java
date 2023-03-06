package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import java.math.BigInteger;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class HoldCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public HoldCommand(String username, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToHold, String did, BigInteger campId, TelephonyManager telephonyManager, String audioUrl) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.numberToHold = numberToHold;
        this.did = did;
        this.campId = campId;
        this.telephonyManager = telephonyManager;
        this.audioUrl=audioUrl;
    }

    public HoldCommand(String username, Long agentUniqueId, String _agentLoginNumber, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToHold, String did, BigInteger campId, TelephonyManager telephonyManager, String audioUrl) {
        super(username, agentUniqueId, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.numberToHold = numberToHold;
        this.did = did;
        this.campId = campId;
        this.agentLoginNumber = _agentLoginNumber;
        this.telephonyManager = telephonyManager;
        this.audioUrl=audioUrl;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(telephonyManager.hold(username, agentUniqueId,agentLoginNumber, agentId, monitorUcid, ucid, numberToHold, did, campId, audioUrl));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("DID", did)
                .append("UCID", ucid)
                .append("HoldNumber", numberToHold)
                .toString();
    }

    private String agentLoginNumber;
    private final BigInteger monitorUcid;
    private final BigInteger ucid;
    private final String numberToHold;
    private final String did;
    private final BigInteger campId;
    private TelephonyManager telephonyManager;
    private String audioUrl;

}

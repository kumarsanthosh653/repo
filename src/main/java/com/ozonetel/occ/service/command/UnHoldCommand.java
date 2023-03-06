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
public class UnHoldCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {
    
    public UnHoldCommand(String username, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToHold, String did, TelephonyManager telephonyManager) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.numberToUnHold = numberToHold;
        this.did = did;
        this.telephonyManager = telephonyManager;
    }
    
     public UnHoldCommand(String username,Long agentUniqueId, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToHold, String did, TelephonyManager telephonyManager) {
        super(username,agentUniqueId, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.numberToUnHold = numberToHold;
        this.did = did;
        this.telephonyManager = telephonyManager;
    }
    
    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(telephonyManager.unHold(username,agentUniqueId, agentId, monitorUcid, ucid, numberToUnHold, did));
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("MonitorUcid", monitorUcid)
                .append("DID", did)
                .append("UCID", ucid)
                .append("HoldNumber", numberToUnHold)
                .toString();
    }
    
    
    private final BigInteger monitorUcid;
    private final BigInteger ucid;
    private final String numberToUnHold;
    private final String did;
    private TelephonyManager telephonyManager;
    
}

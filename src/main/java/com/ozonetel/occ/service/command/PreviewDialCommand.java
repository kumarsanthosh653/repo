package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.Dialer;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class PreviewDialCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public PreviewDialCommand(String username, String agentId, String agentPhoneNumber, Long dataId, String customerNumber, Dialer dialer) {
        super(username, agentId);
        this.agentPhoneNumber = agentPhoneNumber;
        this.dataId = dataId;
        this.customerNumber = customerNumber;
        this.dialer = dialer;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(dialer.dial(username, agentId, agentPhoneNumber, dataId, customerNumber));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("AgentPhoneNumber", agentPhoneNumber)
                .append("DataId", dataId)
                .append("CustomerNumber", customerNumber)
                .toString();
    }

    final private String agentPhoneNumber;
    final private Long dataId;
    final private String customerNumber;
    private Dialer dialer;

}

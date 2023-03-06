package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class CallbackDialCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public CallbackDialCommand(String username, String agentId, Long callbackId, String agentPhoneNumber, CallBackManager callBackManager) {
        super(username, agentId);
        this.callbackId = callbackId;
        this.callBackManager = callBackManager;
        this.agentPhoneNumber = agentPhoneNumber;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(callBackManager.callBackDial(username, agentId, agentPhoneNumber, callbackId));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("User", username)
                .append("AgentId", agentId)
                .append("AgentNumber", agentPhoneNumber)
                .append("CallbackId", callbackId)
                .toString();
    }

    private final String agentPhoneNumber;
    private final Long callbackId;
    private final CallBackManager callBackManager;
}

package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class FailCallbackCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public FailCallbackCommand(String username, String agentId, Long callbackId, CallBackManager callBackManager) {
        super(username, agentId);
        this.callbackId = callbackId;
        this.callBackManager = callBackManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(Status.SUCCESS, callBackManager.failCallback(username, agentId, callbackId));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("CallbackId", callbackId)
                .toString();
    }

    private final Long callbackId;
    private final CallBackManager callBackManager;
}

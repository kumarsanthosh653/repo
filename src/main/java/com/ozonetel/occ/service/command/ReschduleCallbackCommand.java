package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ReschduleCallbackCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ReschduleCallbackCommand(String username, String agentId, Date newDate, Long callbackId, String rescheduleComment, CallBackManager callBackManager,String callBackTz) {
        super(username, agentId);
        this.newDate = newDate;
        this.callbackId = callbackId;
        this.rescheduleComment = rescheduleComment;
        this.callBackManager = callBackManager;
        this.callBackTz = callBackTz;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        return new AgentCommandStatus(callBackManager.rescheduleCallback(username, agentId, newDate, callbackId, rescheduleComment,callBackTz));

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("NewDate", newDate)
                .append("CallbackId", callbackId)
                .append("RescheduleComment", rescheduleComment)
                .toString();
    }

    private final Date newDate;
    private final Long callbackId;
    private final String rescheduleComment;
    private final CallBackManager callBackManager;
    private final String callBackTz;
}

package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.service.impl.Status;
import java.util.Locale;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.MessageSource;

/**
 *
 * @author pavanj
 */
public class DeleteCallbackCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public DeleteCallbackCommand(String username, String agentId, Long dataId, CallBackManager callBackManager, MessageSource messageSource) {
        super(username, agentId);
        this.dataId = dataId;
        this.callBackManager = callBackManager;
        this.messageSource = messageSource;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        if (callBackManager.deleteCallback(username, agentId, dataId)) {
            return new AgentCommandStatus(Status.SUCCESS, messageSource.getMessage("success.delete.callback", null, Locale.getDefault()));
        } else {
            return new AgentCommandStatus(Status.ERROR, messageSource.getMessage("fail.delete.callback", null, Locale.getDefault()));
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("DataId", dataId)
                .toString();
    }

    private final Long dataId;
    private final CallBackManager callBackManager;
    private final MessageSource messageSource;
}

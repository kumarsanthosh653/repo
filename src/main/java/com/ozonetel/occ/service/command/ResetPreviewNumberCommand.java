package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.service.impl.Status;
import java.util.Locale;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.MessageSource;

/**
 *
 * @author pavanj
 */
public class ResetPreviewNumberCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ResetPreviewNumberCommand(String username, String agentId, Long dataId, PreviewDataManager previewDataManager, MessageSource messageSource) {
        super(username, agentId);
        this.dataId = dataId;
        this.previewDataManager = previewDataManager;
        this.messageSource = messageSource;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------        
        previewDataManager.resetPreviewNumber(dataId);
        return new AgentCommandStatus(Status.SUCCESS, messageSource.getMessage("success.data.reset", null, Locale.getDefault()));
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
    private final PreviewDataManager previewDataManager;
    private final MessageSource messageSource;

}

package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.MailEngine;
import com.ozonetel.occ.service.impl.Status;
import com.ozonetel.occ.util.TimeConverter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class AgentPauseAlertCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public AgentPauseAlertCommand(String username, String agentId, String reason, int timeout, AgentManager agentManager, MailEngine mailEngine) {
        super(username, agentId);
        this.username = username;
        this.agentId = agentId;
        this.reason = reason;
        this.timeout = timeout;
        this.agentManager = agentManager;
        this.mailEngine = mailEngine;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------
//        Agent agent = agentManager.getAgentByAgentId(username, agentId);
//        List<String> recipients = new ArrayList(2);
//        
//        recipients.add(agent.getUser().getEmail());
//        if (StringUtils.isNotBlank(agent.getEmail())) {
//            recipients.add(agent.getEmail());
//        }
//        Map<String, Object> model = new LinkedHashMap<>();
//        model.put("agent", agent);
//        model.put("pauseReason", reason);
//        model.put("timeout", TimeConverter.secondsToTime((long) timeout));
//        try {
//            logger.info("Sending pause time exceeded alert:" + recipients + " | " + model);
//            mailEngine.sendMimeMessage(recipients.toArray(new String[0]), null, null, null, null, "pauseAlert.vm",
//                    model, "CloudAgent Alert", null);
//            logger.info("Sent pause time exceeded alert:" + recipients + " | " + model);
//            return new AgentCommandStatus(Status.SUCCESS, "Sent alert");
//        } catch (MessagingException ex) {
//            logger.error(ex.getMessage(), ex);
//            return new AgentCommandStatus(Status.ERROR, ex.getMessage());
//        }
return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Username", username)
                .append("AgentId", agentId)
                .append("Reason", reason)
                .append("Timeout", timeout)
                .toString();

    }

    private final String reason;
    private final int timeout;
    private final AgentManager agentManager;
    private final MailEngine mailEngine;

}

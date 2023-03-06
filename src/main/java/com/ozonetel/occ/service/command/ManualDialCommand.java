package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.ManualDialService;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class ManualDialCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ManualDialCommand(String username, Long agentUniqId, String agentId, String agentMode, String agentNumber, boolean isSip, String custNumber, Long campaignId, ManualDialService manualDial, String disclaimer) {
        super(username, agentId);
        this.agentNumber = agentNumber;
        this.custNumber = custNumber;
        this.campaignId = campaignId;
        this.isSip = isSip;
        this.agentMode = agentMode;
        this.agentUniqId = agentUniqId;
        this.manualDial = manualDial;
        this.disclaimer = disclaimer;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------         
        return new AgentCommandStatus(manualDial.manualDial(username, agentUniqId, agentId, agentMode, custNumber, agentNumber, isSip, campaignId, "", true,disclaimer));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("AgentNumber", agentNumber)
                .append("IsSip", isSip)
                .append("CustomerNumber", custNumber)
                .append("CampaignId", campaignId)
                .append("Disclaimer", disclaimer)
                .toString();
    }

    private final ManualDialService manualDial;
    private final boolean isSip;
    private final String agentNumber;
    private final String custNumber;
    private final Long campaignId;
    private final String agentMode;
    private final Long agentUniqId;
    private final String disclaimer;
}

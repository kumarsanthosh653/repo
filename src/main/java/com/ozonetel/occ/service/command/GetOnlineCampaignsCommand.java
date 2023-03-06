package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.command.response.GetCampaignsResponse;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetOnlineCampaignsCommand extends AbstractAgentToolbarCommand<GetCampaignsResponse> {

    public GetOnlineCampaignsCommand(String username, String agentId, CampaignManager campaignManager) {
        super(username, agentId);
        this.campaignManager = campaignManager;
    }

    @Override
    public GetCampaignsResponse execute() {
//-------------------------------------        
        return new GetCampaignsResponse(Status.SUCCESS, campaignManager.getAllOnlineCampaignsInfoByAgentId(username, agentId));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .toString();
    }
    private final CampaignManager campaignManager;
}

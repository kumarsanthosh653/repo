package com.ozonetel.occ.service.command;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.command.response.GetPreviewDataResponse;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetPreviewDataCommand extends AbstractAgentToolbarCommand<GetPreviewDataResponse> {

    public GetPreviewDataCommand(String username, String agentId, Long campaignId, PreviewDataManager previewDataManager, CampaignManager campaignManager) {
        super(username, agentId);
        this.campaignId = campaignId;
        this.previewDataManager = previewDataManager;
        this.campaignManager = campaignManager;
    }

    @Override
    public GetPreviewDataResponse execute() {
//-------------------------------------                
        Campaign campaign = campaignManager.get(campaignId);
        logger.debug("Get data for campaign:" + campaign);
        PreviewData previewData;
        if (campaign.getDialMethod() == Campaign.DialMethod.Agentwise) {
            logger.debug("Getting number for campaign for agent wise");
            previewData = previewDataManager.getNumberToDialForAgent(campaignId, agentId);
        } else {
            logger.debug("Getting number for campaign for non agent wise");
            previewData = previewDataManager.getNumberToDialNonAgentWise(campaignId);
        }

        if (previewData == null) {
            return new GetPreviewDataResponse(Status.ERROR, previewData);
        } else {
            return new GetPreviewDataResponse(Status.SUCCESS, previewData);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("CampaignId", campaignId)
                .toString();
    }

    final private Long campaignId;
    private PreviewDataManager previewDataManager;
    private CampaignManager campaignManager;

}

package com.ozonetel.occ.service.command;

import com.ozonetel.occ.model.CampaignInfo;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.command.response.GetCampaignsResponse;
import com.ozonetel.occ.service.impl.Status;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetPreviewCampaignsCommand extends AbstractAgentToolbarCommand<GetCampaignsResponse> {

    public GetPreviewCampaignsCommand(String username, String agentId, PreviewDataManager previewDataManager, CampaignManager campaignManager) {
        super(username, agentId);
        this.previewDataManager = previewDataManager;
        this.campaignManager = campaignManager;
    }

    @Override
    public GetCampaignsResponse execute() {
//-------------------------------------        
        List<CampaignInfo> list = campaignManager.getPreviewCampaignsInfoByAgentId(username, agentId);
        for (CampaignInfo oci : list) {
            if (oci.isAgentWise()) {
                oci.setPendingData(previewDataManager.getCountOfNumbersRemainingToDialForAgent(oci.getId(), agentId));
            } else {
                oci.setPendingData(previewDataManager.getCountOfNumbersRemainingToDial(oci.getId()));
            }
        }

        Iterator<CampaignInfo> it = list.iterator();
        CampaignInfo campaignInfo;
        while (it.hasNext()) {
            campaignInfo = it.next();
            if (campaignInfo.getPendingData() == 0) {
                it.remove();
            }
        }
        return new GetCampaignsResponse(Status.SUCCESS, list);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .toString();
    }

    private PreviewDataManager previewDataManager;
    private CampaignManager campaignManager;

}

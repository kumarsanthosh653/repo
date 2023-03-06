package com.ozonetel.occ.service.command;

import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.service.command.response.GetDispositionListResponse;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.impl.Status;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetDispositionsCommand extends AbstractAgentToolbarCommand<GetDispositionListResponse> {

    public GetDispositionsCommand(String username, String agentId, String did, String agentCallMode, String campaignId, AgentManager agentManager, UserManager userManager, CampaignManager campaignManager, DispositionManager dispositionManager) {
        super(username, agentId);
        this.did = did;
        this.agentMode = agentCallMode;
        this.campaignId = campaignId;

        this.userManager = userManager;
        this.campaignManager = campaignManager;
        this.agentManager = agentManager;
        this.dispositionManager = dispositionManager;
    }

    @Override
    public GetDispositionListResponse execute() {
//-------------------------------------        
        GetDispositionListResponse getDispositionListResponse = new GetDispositionListResponse(Status.SUCCESS);
        List<Disposition> dispositions = new ArrayList<>();
//        Agent agent = agentManager.getAgentByAgentId(username, agentId);
        Agent agent = null;
        Campaign campaign = null;
        if (agent != null) {
            if (campaignId != null && !campaignId.isEmpty()) {
                campaign = campaignManager.get(new Long(campaignId));
            } else if (agentMode.equalsIgnoreCase("manual")) {
                campaign = campaignManager.getCampaignsByDid(did);
            } else {
                campaign = campaignManager.getCampaignsByDid(did, agentMode);
            }
            if (campaign != null && !campaign.getDispositions().isEmpty()) {

                dispositions = dispositionManager.getActiveDispositionsWithoutCampaigns(campaign.getCampaignId());
                logger.debug("Success : [" + username + "][" + agentId + "][" + did + "][" + agentMode + "] returned Dispositions = " + dispositions.size());

            } else {
                logger.debug("Error : [" + username + "][" + agentId + "][" + did + "][" + agentMode + "] have no Dipositions or campaign is null :" + (campaign == null ? "Campaing is null:" : "Dispositions are empty."));
            }
        } else {
            logger.debug("Error : [" + username + "][" + agentId + "][" + did + "][" + agentMode + "] agent is Null to return Dispositons");
        }

        if (userManager.hasRole(username, Constants.CALLBACKS_ROLE)) {
            dispositions.add(new Disposition(-300L, "callBack"));
        }
        getDispositionListResponse.setDispositionList(dispositions);
        return getDispositionListResponse;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("DID", did)
                .append("AgentMode", agentMode)
                .append("CampaignId", campaignId)
                .toString();
    }

    private final UserManager userManager;
    private final CampaignManager campaignManager;
    private final AgentManager agentManager;
    private final DispositionManager dispositionManager;
    private final String did;
    private final String agentMode;
    private final String campaignId;

}

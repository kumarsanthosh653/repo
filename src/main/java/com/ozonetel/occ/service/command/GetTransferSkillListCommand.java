package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.GetTransferSkillListResponse;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetTransferSkillListCommand extends AbstractAgentToolbarCommand<GetTransferSkillListResponse> {

    public GetTransferSkillListCommand(String username, String agentId, String campaignType, String did, SkillManager skillManager) {
        super(username, agentId);
        this.did = did;
        this.campaignType = campaignType;
        this.skillManager = skillManager;
    }

    @Override
    public GetTransferSkillListResponse execute() {
//-------------------------------------        
        return new GetTransferSkillListResponse(Status.SUCCESS, skillManager.getTransferSkillList(username, campaignType, did));

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("did", did)
                .append("CampType", campaignType)
                .toString();
    }

    private final String did;
    private final String campaignType;
    private  SkillManager skillManager;
}

package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.SkillTransferManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class SkillTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public SkillTransferCommand(String username, String agentId, Long monitorUcid, Long ucid, String did, Long skillId, String skillName, SkillTransferManager skillTransferManager) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.skillId = skillId;
        this.skillName = skillName;
        this.skillTransferManager = skillTransferManager;
    }

    @Override
    public AgentCommandStatus execute() {
//----------------------------------        
        return new AgentCommandStatus(skillTransferManager.skillTransfer(username, agentId, monitorUcid, ucid, did, skillId, skillName));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("SkillId", skillId)
                .append("SkillName", skillName)
                .append("MonitorUcid", monitorUcid)
                .append("UCID", ucid)
                .append("DID", did)
                .toString();
    }

    private final Long monitorUcid;
    private final Long ucid;
    private final String did;
    private final Long skillId;
    private final String skillName;
    private final SkillTransferManager skillTransferManager;
}

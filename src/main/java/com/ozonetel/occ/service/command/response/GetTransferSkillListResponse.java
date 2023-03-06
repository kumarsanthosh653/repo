package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetTransferSkillListResponse extends AgentToolbarResponse {

    private List<Skill> skillTransferList;

    public GetTransferSkillListResponse(Status status) {
        super(status);
    }

    public GetTransferSkillListResponse(Status status, List<Skill> skillTransferList) {
        super(status);
        this.skillTransferList = skillTransferList;
    }

    public List<Skill> getSkillTransferList() {
        return skillTransferList;
    }

    public void setSkillTransferList(List<Skill> skillTransferList) {
        this.skillTransferList = skillTransferList;
    }

    @Override
    public String getReqType() {
        return reqType;
    }

    @Override
    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    @Override
    public String getNs() {
        return ns;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("Command", reqType).append("Status", status).append("Namespace", ns)
                .append("Transfer Skills", skillTransferList).toString();
    }

}

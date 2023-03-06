package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.CampaignInfo;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Response for get Preview/All campaigns command.
 *
 * @author pavanj
 */
public class GetCampaignsResponse extends AgentToolbarResponse {

    private final List<CampaignInfo> previewCamps;

    public GetCampaignsResponse(Status status, List<CampaignInfo> previewCamps) {
        super(status);
        this.previewCamps = previewCamps;
    }

    public List<CampaignInfo> getPreviewCamps() {
        return previewCamps;
    }

    @Override
    public Status getStatus() {
        return super.getStatus();
    }

    @Override
    public String getNs() {
        return super.getNs();
    }

    @Override
    public String getReqType() {
        return super.getReqType();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("Command", reqType).append("Status", status).append("Namespace", ns)
                .append("Campaign Details", previewCamps).toString();
    }

}

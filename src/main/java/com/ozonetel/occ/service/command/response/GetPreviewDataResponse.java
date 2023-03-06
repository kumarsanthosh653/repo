package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.service.impl.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetPreviewDataResponse extends AgentToolbarResponse {

    private PreviewData previewData;

    public GetPreviewDataResponse(Status status, PreviewData previewData) {
        super(status);
        this.previewData = previewData;
    }

    public PreviewData getPreviewData() {
        return previewData;
    }

    public void setPreviewData(PreviewData previewData) {
        this.previewData = previewData;
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
                .append("Data", previewData).toString();
    }

}

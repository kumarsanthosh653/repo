package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetDispositionListResponse extends AgentToolbarResponse {

    private List<Disposition> dispositionList;

    public GetDispositionListResponse(Status status) {
        super(status);
    }

    public void setDispositionList(List<Disposition> dispositionList) {
        this.dispositionList = dispositionList;
    }

    public List<Disposition> getDispositionList() {
        return dispositionList;
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
                .append("Disposition List", dispositionList).toString();
    }

}

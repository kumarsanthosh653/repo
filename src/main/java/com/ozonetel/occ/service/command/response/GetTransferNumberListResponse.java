package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.TransferNumber;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetTransferNumberListResponse extends AgentToolbarResponse {

    private List<TransferNumber> transferNumberList;

    public GetTransferNumberListResponse(Status status) {
        super(status);
    }

    public GetTransferNumberListResponse(Status status, List<TransferNumber> transferNumberList) {
        super(status);
        this.transferNumberList = transferNumberList;
    }

    public List<TransferNumber> getTransferNumberList() {
        return transferNumberList;
    }

    public void setTransferNumberList(List<TransferNumber> transferNumberList) {
        this.transferNumberList = transferNumberList;
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
                .append("Transfer Numbers", transferNumberList).toString();
    }

}

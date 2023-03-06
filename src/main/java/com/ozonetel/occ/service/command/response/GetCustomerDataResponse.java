package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.service.impl.Status;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetCustomerDataResponse extends AgentToolbarResponse {

    private Map<String, String> customerData;

    public GetCustomerDataResponse(Status status) {
        super(status);
    }

    public GetCustomerDataResponse(Status status, Map<String, String> customerData) {
        super(status);
        this.customerData = customerData;
    }

    public Map<String, String> getCustomerData() {
        return customerData;
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
                .append("Customer Data", customerData).toString();
    }

}

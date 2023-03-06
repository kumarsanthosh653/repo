package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.PreviewExtraDataManager;
import com.ozonetel.occ.service.command.response.GetCustomerDataResponse;
import com.ozonetel.occ.service.impl.Status;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class GetCustomerDataCommand extends AbstractAgentToolbarCommand<GetCustomerDataResponse> {

    public GetCustomerDataCommand(Long dataId, String username, String agentId, PreviewExtraDataManager previewExtraDataManager) {
        super(username, agentId);
        this.dataId = dataId;
        this.previewExtraDataManager = previewExtraDataManager;
    }

    @Override
    public GetCustomerDataResponse execute() {
//-------------------------------------        
        Map<String, String> customerData = null;
        if (customerData != null && !customerData.isEmpty()) {
            return new GetCustomerDataResponse(Status.SUCCESS, customerData);
        } else {
            return new GetCustomerDataResponse(Status.ERROR, customerData);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("DataId", dataId)
                .toString();
    }

    private final Long dataId;
    private final PreviewExtraDataManager previewExtraDataManager;
}

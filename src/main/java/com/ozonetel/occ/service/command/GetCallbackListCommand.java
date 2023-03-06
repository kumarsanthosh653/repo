package com.ozonetel.occ.service.command;

import com.ozonetel.occ.model.CallbackScheduleDetailsGroup;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.command.response.GetCallbackListResponse;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;

/**
 *
 * @author pavanj
 */
public class GetCallbackListCommand extends AbstractAgentToolbarCommand<GetCallbackListResponse> {

    public GetCallbackListCommand(String username, String agentId, CallBackManager callBackManager) {
        super(username, agentId);
        this.callBackManager = callBackManager;
    }

    @Override
    public GetCallbackListResponse execute() {
        List<CallbackScheduleDetailsGroup> list = callBackManager.getCallbackScheduleDetails(username, agentId);
        if (list != null && !list.isEmpty()) {
            return new GetCallbackListResponse(Status.SUCCESS, list);
        } else {
            return new GetCallbackListResponse(Status.ERROR, list);

        }
    }

    private final CallBackManager callBackManager;
}

package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.CallbackScheduleDetailsGroup;
import com.ozonetel.occ.service.impl.Status;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class GetCallbackListResponse extends AgentToolbarResponse {

    private final List<CallbackScheduleDetailsGroup> callbackScheduleDetailsGroups;
    private final Date detailsDate;

    public GetCallbackListResponse(Status status, List<CallbackScheduleDetailsGroup> callbackScheduleDetailsGroups) {
        super(status);
        this.callbackScheduleDetailsGroups = callbackScheduleDetailsGroups;
        detailsDate = new Date();
    }

    public GetCallbackListResponse(Status status, List<CallbackScheduleDetailsGroup> callbackScheduleDetailsGroups, Date detailsDate) {
        super(status);
        this.callbackScheduleDetailsGroups = callbackScheduleDetailsGroups;
        this.detailsDate = detailsDate;
    }

    public List<CallbackScheduleDetailsGroup> getCallbackScheduleDetailsGroups() {
        return callbackScheduleDetailsGroups;
    }

    public Date getDetailsDate() {
        return detailsDate;
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
                .append("Callback Details", callbackScheduleDetailsGroups).toString();
    }

}

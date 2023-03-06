package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.impl.Status;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class SetDispositionCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public SetDispositionCommand(String username, String agentId, Long ticketId, String dataId, String dispositionCode, String callBackTime, String ucid, String dispComments, PreviewDataManager previewDataManager, ReportManager reportManager, CallBackManager callBackManager, UserManager userManager, AgentManager agentManager) {
        super(username,null, agentId);
        this.dataId = dataId;
        this.dispositionCode = dispositionCode;
        this.callBackTime = callBackTime;
        this.ucid = ucid;
        this.dispComments = dispComments;
        this.agentId = agentId;
        this.username = username;
        this.ticketId = ticketId;
        this.previewDataManager = previewDataManager;
        this.reportManager = reportManager;
        this.callBackManager = callBackManager;
        this.userManager = userManager;
        this.agentManager = agentManager;
    }

    @Override
    public AgentCommandStatus execute() {
//----------------------------------        
        PreviewData data = null;
        Report report = null;

        if (dataId != null && !dataId.isEmpty() && (data = previewDataManager.get(new Long(dataId))) != null) {
            data.setDisposition(dispositionCode.equalsIgnoreCase("-300") ? "Callback" : dispositionCode);
            data = previewDataManager.save(data);// Saved Dispositon in PreviewData Table
            logger.debug("^^^@Set disposition in data:" + data.toLongString());
        }

        if (ucid != null && !ucid.isEmpty() && (report = reportManager.getReportByUCID(Long.valueOf(ucid))) != null) {
            report.setDisposition(dispositionCode.equalsIgnoreCase("-300") ? "Callback" : dispositionCode.trim());
            report.setComment(dispComments);
            report.setRefId(ticketId);
            report = reportManager.save(report);
        }

        try {
            //
            // ------ > Save call back details only if the user has the permissions.
            if (StringUtils.equalsIgnoreCase(dispositionCode, "-300") && userManager.hasRole(username, Constants.CALLBACKS_ROLE)) {
                Date callbackTime = null;
                logger.debug("callBackTime=" + callBackTime);
                if (callBackTime != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    try {
                        callbackTime = simpleDateFormat.parse(callBackTime);
                    } catch (Exception e) {
                        logger.error("Uh ya!! unable to Convert CallBackTime..");
                    }

                        //TODO uncomment below line if you use the class. Commenting as not using this.
//                    callBackManager.scheduleCallback(data != null ? data.getPhoneNumber() : report.getDest(), dispComments, callbackTime, data != null ? data.getCampaign() : report.getCampaign(), agentManager.getAgentByAgentId(username, agentId));
                }
            }
        } catch (Exception e) {
            logger.error("Exception in saving call back:" + e.getMessage(), e);
        }
        logger.debug("success : [" + username + "][" + agentId + "][" + ucid + "] has set Disp = [" + dispositionCode + "]");

        return new AgentCommandStatus(Status.SUCCESS, "Disposition is set");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("DataId", dataId)
                .append("CallBackTime", callBackTime)
                .append("UCID", ucid)
                .append("DispComments", dispComments)
                .append("TicketID", ticketId)
                .toString();
    }

    private final String dataId;
    private final String dispositionCode;
    private final String callBackTime;
    private final String ucid;
    private final String dispComments;
    private final Long ticketId;

    private PreviewDataManager previewDataManager;
    private ReportManager reportManager;
    private CallBackManager callBackManager;
    private UserManager userManager;
    private AgentManager agentManager;
    private CampaignManager campaignManager;
}

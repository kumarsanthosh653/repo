package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.AgentCommandStatus;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.SMSManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.impl.Status;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author pavanj
 */
public class SendSMSCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public SendSMSCommand(String username, String agentId, BigInteger ucid, Long campaignId, String destination, String message, UserManager userManager, SMSManager sMSManager) {
        super(username, agentId);
        this.ucid = ucid;
        this.campaignId = campaignId;
        this.destination = destination;
        this.message = message;

        this.userManager = userManager;
        this.sMSManager = sMSManager;
    }

    @Override
    public AgentCommandStatus execute() {
//-------------------------------------
        if (userManager.hasRole(username, Constants.SMS_ROLE)) {
            List paramList = userManager.getUserSettings(username);

            if (!paramList.isEmpty()) {
                boolean sendSMS = false;
                String smsURL = null;
                String requestType = "GET";

                Map item;
                for (Object param : paramList) {
                    item = (Map) param;

                    switch (item.get("ParameterCode").toString()) {
                        case "SEND_SMS":
                            sendSMS = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                            break;
                        case "SMS_URL":
                            smsURL = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                    ? item.get("DefaultValue").toString() : item.get("ParameterValue").toString();
                            requestType = item.get("RequestType") == null || StringUtils.isEmpty(item.get("RequestType").toString())
                                    ? item.get("DefaultRequestType").toString() : item.get("RequestType").toString();
                            break;
                    }

                }

                if (sendSMS) {
                    return new AgentCommandStatus(sMSManager.sendSMS(username, ucid, campaignId, smsURL, requestType, agentId, destination, message, null, null));
                } else {
                    return new AgentCommandStatus(Status.ERROR, "This feature has been disabled by Admin.");
                }
            } else {
                return new AgentCommandStatus(Status.ERROR, "This feature is not enabled for you.");
            }
        } else {
            return new AgentCommandStatus(Status.ERROR, "This feature is not enabled for you.");
        }

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User", username)
                .append("AgentId", agentId)
                .append("UCID", ucid)
                .append("CampId", campaignId)
                .append("Destination", destination)
                .append("Message", message)
                .toString();
    }

    private final UserManager userManager;
    private final SMSManager sMSManager;

    private final BigInteger ucid;
    private final Long campaignId;
    private final String destination;
    private final String message;
}

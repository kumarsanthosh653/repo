package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.command.response.AgentLoginResponse;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.Role;
import com.ozonetel.occ.model.SMSTemplate;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.PauseReasonManager;
import com.ozonetel.occ.service.SMSTemplateManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.impl.Status;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author pavanj
 */
public class AgentLoginCommand extends AbstractAgentToolbarCommand<AgentLoginResponse> {

    public AgentLoginCommand(String username, String agentId, String agentPhoneNumber, String usId, boolean reconnect, Event.AgentMode mode, AgentManager agentManager, UserManager userManager, SMSTemplateManager sMSTemplateManager, PauseReasonManager pauseReasonManager) {
        super(username,null, agentId);
        this.usId = usId;
        this.reconnect = reconnect;
        this.mode = mode;
        this.agentPhoneNumber = agentPhoneNumber;

        this.agentManager = agentManager;
        this.userManager = userManager;
        this.sMSTemplateManager = sMSTemplateManager;
        this.pauseReasonManager = pauseReasonManager;
    }

    @Override
    public AgentLoginResponse execute() {
//-------------------------------------        
        logger.debug("<><><><><><><Agent Login Response Executing... ");
        AgentLoginResponse agentLoginResponse = new AgentLoginResponse(Status.SUCCESS);
        String status = agentManager.loginAgent(username, agentId, agentPhoneNumber, usId, reconnect, mode);
        
        logger.debug("Agent login " + status + "  : [" + username + "][" + agentId + "] is " + (reconnect ? "reconnected" : "logged") + " in with #" + agentPhoneNumber);
        agentLoginResponse.setStatus(Status.SUCCESS);
        agentLoginResponse.setCampaignType("Campaign");
        agentLoginResponse.setCampaignScript("Hello sir/Madam");
        agentLoginResponse.setPhoneNumber(agentPhoneNumber);
        //
        // ----- > Not using as of now.
//            response.put("agentSkill", agentManager);

        User user = userManager.getUserByUsername(username);

        Set<Role> roles = user.getRoles();
        Set<String> roleNames = new HashSet<>(roles.size());

        for (Role role : roles) {
            roleNames.add(role.getName());
        }

        agentLoginResponse.setCbe(roleNames.contains(Constants.CALLBACKS_ROLE) ? 1 : 0);
        agentLoginResponse.setOutboundEnabled(roleNames.contains(Constants.OUTBOUND_ROLE) ? 1 : 0);
        agentLoginResponse.setBlendedRole(roleNames.contains(Constants.BLENDED_ROLE) ? 1 : 0);
        agentLoginResponse.setAgentCallHist(roleNames.contains(Constants.AGENT_CALL_HISTORY_ROLE) ? 1 : 0);

        agentLoginResponse.setPauseReasons(pauseReasonManager.getPauseReasonByUser(username));
        boolean sendSMS = false;
        boolean pauseAlert = false;
        boolean maskCustomerNumber = false;

        List paramList = userManager.getUserSettings(user.getId());

        if (!paramList.isEmpty()) {

            Map item;
            for (Object param : paramList) {
                item = (Map) param;

                switch (item.get("ParameterCode").toString()) {
                    case "SEND_SMS":
                        sendSMS = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                        break;
                    case "PAUSE_EMAIL_ALERT":
                        pauseAlert = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                        break;
                    case "MASK_CUSTOMER_NUMBER":
                        maskCustomerNumber = item.get("ParameterValue") == null || StringUtils.isEmpty(item.get("ParameterValue").toString())
                                ? Boolean.valueOf(item.get("DefaultValue").toString()) : Boolean.valueOf(item.get("ParameterValue").toString());
                        break;
                }

            }

        }

        if (!roleNames.contains(Constants.SMS_ROLE)) {
            sendSMS = false;
        }

        agentLoginResponse.setSmse(sendSMS ? 1 : 0);
        agentLoginResponse.setMcn(maskCustomerNumber ? 1 : 0);

        if (sendSMS) {
            agentLoginResponse.setSmsTemplates(sMSTemplateManager.getSMSTemplatesByUser(username));
        } else {
            agentLoginResponse.setSmsTemplates(new ArrayList<SMSTemplate>(0));//emtpy array
        }

        agentLoginResponse.setPauseAlert(pauseAlert ? 1 : 0);
        return agentLoginResponse;

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Username", username)
                .append("AgentId", agentId)
                .append("Reconnect", reconnect)
                .append("Mode", mode)
                .append("AgentNumber", agentPhoneNumber)
                .toString();
    }

    private final String usId;//jwebsocket client id
    private final boolean reconnect;
    private final Event.AgentMode mode;
    private final AgentManager agentManager;
    private final UserManager userManager;
    private final SMSTemplateManager sMSTemplateManager;
    private final PauseReasonManager pauseReasonManager;
    private final String agentPhoneNumber;

}

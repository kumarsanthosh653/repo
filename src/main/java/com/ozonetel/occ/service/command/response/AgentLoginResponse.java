package com.ozonetel.occ.service.command.response;

import com.ozonetel.occ.model.SMSTemplate;
import com.ozonetel.occ.service.impl.Status;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class is to provide response on agent login.
 *
 * @author V.J.Pavan Srinivas
 */
public class AgentLoginResponse extends AgentToolbarResponse {

    private String campaignType;
    private String campaignScript;
    private String phoneNumber;
    private String agentSkill;
    private int cbe;//Callback feature enabled or not.
    private List pauseReasons;
    private int outboundEnabled;
    private int smse;//SMS enabled.
    private int mcn; //Mask customer number.
    private List<SMSTemplate> smsTemplates;
    private int pauseAlert;//send alert to admin if agent exceeds the configured pause time.
    private int blendedRole;
    private int agentCallHist;//Agent call history enabled ?
    private String message;
    private String agentStatus;
    boolean isSip;

    public AgentLoginResponse(Status status) {
        super(status);
    }

    public String getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(String agentStatus) {
        this.agentStatus = agentStatus;
    }

    public boolean isIsSip() {
        return isSip;
    }

    public void setIsSip(boolean isSip) {
        this.isSip = isSip;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    public String getCampaignScript() {
        return campaignScript;
    }

    public void setCampaignScript(String campaignScript) {
        this.campaignScript = campaignScript;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAgentSkill() {
        return agentSkill;
    }

    public void setAgentSkill(String agentSkill) {
        this.agentSkill = agentSkill;
    }

    public int getCbe() {
        return cbe;
    }

    public void setCbe(int cbe) {
        this.cbe = cbe;
    }

    public List getPauseReasons() {
        return pauseReasons;
    }

    public void setPauseReasons(List pauseReasons) {
        this.pauseReasons = pauseReasons;
    }

    public int getOutboundEnabled() {
        return outboundEnabled;
    }

    public void setOutboundEnabled(int outboundEnabled) {
        this.outboundEnabled = outboundEnabled;
    }

    public int getSmse() {
        return smse;
    }

    public void setSmse(int smse) {
        this.smse = smse;
    }

    public int getMcn() {
        return mcn;
    }

    public void setMcn(int mcn) {
        this.mcn = mcn;
    }

    public List<SMSTemplate> getSmsTemplates() {
        return smsTemplates;
    }

    public void setSmsTemplates(List<SMSTemplate> smsTemplates) {
        this.smsTemplates = smsTemplates;
    }

    public int getPauseAlert() {
        return pauseAlert;
    }

    public void setPauseAlert(int pauseAlert) {
        this.pauseAlert = pauseAlert;
    }

    public int getBlendedRole() {
        return blendedRole;
    }

    public void setBlendedRole(int blendedRole) {
        this.blendedRole = blendedRole;
    }

    public int getAgentCallHist() {
        return agentCallHist;
    }

    public void setAgentCallHist(int agentCallHist) {
        this.agentCallHist = agentCallHist;
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
                .append("CallHistoryEnabled", agentCallHist)
                .append("AgentSkill", agentSkill)
                .append("AgentStatus", agentStatus)
                .append("BlendedRole", blendedRole)
                .append("SIP", isSip)
                .append("CampaignScript", campaignScript)
                .append("CampType", campaignType)
                .append("CallbackEnabled(cbe)", cbe)
                .append("MaskCallerNumber(mcn)", mcn)
                .append("Message", message)
                .append("OutboundEnabled", outboundEnabled)
                .append("PauseAlert", pauseAlert)
                .append("PauseReasons", pauseReasons)
                .append("PhoneNumber", phoneNumber)
                .append("SMS Enabled(smse)", smse)
                .append("SMS Templates", smsTemplates)
                .toString();
    }

}

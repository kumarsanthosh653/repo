package com.ozonetel.occ.service.command;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;

/**
 *
 * @author pavanj
 */
public class IvrTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public IvrTransferCommand(Long monitorUcid, Long ucid, String did, String agentNumber, String custNumber, boolean isSip, String transferUrl, TelephonyManager telephonyManager, String username, String agentId, String campId, Long agentUniqId, String appAudioURL, Campaign campaign, String designerName, String agentName, String skillName, int transferType,String uui, int blindTransfer) {
        super(username, agentId);
        this.monitorUcid = monitorUcid;
        this.ucid = ucid;
        this.did = did;
        this.agentNumber = agentNumber;
        this.custNumber = custNumber;
        this.isSip = isSip;
        this.transferUrl = transferUrl;
        this.telephonyManager = telephonyManager;
        this.campId = campId;
        this.agentUniqId = agentUniqId;
        this.appAudioURL = appAudioURL;
        this.campaign = campaign;
        this.designerName = designerName;
        this.agentName = agentName;
        this.skillName = skillName;
        this.transferType=transferType;
        this.uui=uui;
        this.blindTransfer = blindTransfer;
    }

    @Override
    public AgentCommandStatus execute() {
        return new AgentCommandStatus(telephonyManager.ivrTransfer(username, agentId, "" + monitorUcid, "" + ucid, did, agentNumber, custNumber, isSip, transferUrl, campId, agentUniqId, appAudioURL, campaign, designerName, agentName, skillName,transferType,uui, blindTransfer));
    }

    public void setTelephonyManager(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    private final Long monitorUcid;
    private final Long ucid;
    private final String did;
    private final String agentNumber;
    private final String custNumber;
    private final boolean isSip;
    private final String transferUrl;
    private TelephonyManager telephonyManager;
    private final String campId;
    private final Long agentUniqId;
    private String appAudioURL;
    private Campaign campaign;
    private String designerName;
    private String agentName;
    private String skillName;
    private int transferType;
    private String uui;
    private int blindTransfer;
}

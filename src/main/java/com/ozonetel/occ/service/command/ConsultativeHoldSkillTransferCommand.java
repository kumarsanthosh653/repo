/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.command;

import com.ozonetel.occ.service.AbstractAgentToolbarCommand;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.command.response.AgentCommandStatus;

/**
 *
 * @author ozone
 */
public class ConsultativeHoldSkillTransferCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public ConsultativeHoldSkillTransferCommand(Long monitorUcid, Long ucid, String did, String agentNumber, String custNumber, boolean isSip, String transferUrl, TelephonyManager telephonyManager, String username, String agentId, String campId, Long agentUniqId, String appAudioURL, String record,String uui) {
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
        this.record = record;
        this.uui = uui;
    }

    @Override
    public AgentCommandStatus execute() {
        return new AgentCommandStatus(telephonyManager.consultativeHoldSkillTransfer(username, agentId, "" + monitorUcid, "" + ucid, did, agentNumber, custNumber, isSip, transferUrl, campId, agentUniqId, appAudioURL, record,uui));
    }

    public void setTelephonyManager(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }

    private final String record;
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
    private String uui;
}

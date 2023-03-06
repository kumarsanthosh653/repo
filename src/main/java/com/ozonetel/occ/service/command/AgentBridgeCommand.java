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
public class AgentBridgeCommand extends AbstractAgentToolbarCommand<AgentCommandStatus> {

    public AgentBridgeCommand(String username, Long agentUniqueId, String agentId, String agentPhone, String apiKey, TelephonyManager telephonyManager) {
        super(username, agentUniqueId, agentId);
        this.apiKey = apiKey;
        this.agentPhone = agentPhone;
        this.telephonyManager = telephonyManager;
    }

    @Override
    public AgentCommandStatus execute() {
        return new AgentCommandStatus(telephonyManager.initiateAgentBridge(agentId, agentUniqueId, agentPhone, username, apiKey));
    }

    private final TelephonyManager telephonyManager;
    private final String agentPhone;
    private final String apiKey;

}

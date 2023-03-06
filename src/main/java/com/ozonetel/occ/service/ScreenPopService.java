/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.FwpNumber;

/**
 *
 * @author aparna
 */
public interface ScreenPopService {

    public void hitScreenPopHere(Campaign c, Agent a, FwpNumber f, String ucid, String callerId, String did, String skillName, String dataId, String agentMonitorUcid, String type, String uui, String agentId);
}

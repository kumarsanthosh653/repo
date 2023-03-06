/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AgentFreeStatus;
import com.ozonetel.occ.model.FwpNumber;

public interface AgentFreeStatusDao extends GenericDao<AgentFreeStatus, Long> {

    public int saveAgentLogin(Long id, boolean reconnect, String phoneNumber, FwpNumber fwp, String clientId);

    public int saveAgentLogout(Long id);

    public int saveReleasedAgent(Long id);

    public int savePauseAgent(Long id);

    public int saveAgentMode(Long id, Agent.Mode mode, boolean reconnect);

    public int saveAgentReconnect(Long id, String clientId);

    public int releaseAgentLock(Long id, boolean dropcall);

    public int releaseAgentLockFlag(Long id);

    public boolean lockAgentFreeStatusIfAvailable(Long id);

    public boolean decrementChatSessionCount(Long id);
}

package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;

/**
 *
 * @author pavanj
 */
public interface AgentTransferManager {

    public StatusMessage consultativeHoldTransfer(String username, Long initiateAgentUniqid, String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, String campId, String appAudioURL, String record, String callType,String uui);

    public StatusMessage consultativeTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, String appAudioURL);

    public StatusMessage blindTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, String appAudioURL);
}

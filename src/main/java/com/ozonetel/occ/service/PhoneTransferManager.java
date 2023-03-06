package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;

/**
 *
 * @author pavanj
 */
public interface PhoneTransferManager {

    public StatusMessage consultativeHoldTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhone, boolean isSip, String custNumber, String campId, String appAudioURL, String record, String callType);

    public StatusMessage consultativeTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhone, String custNumber, String appAudioURL);

    public StatusMessage blindTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long transferNumberId, String transferNumberName, String receiverPhone, String custNumber, String appAudioURL);
}

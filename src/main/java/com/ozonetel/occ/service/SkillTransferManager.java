package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;

/**
 *
 * @author pavanj
 */
public interface SkillTransferManager {

    public StatusMessage skillTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, Long skillId,String skillName);
}

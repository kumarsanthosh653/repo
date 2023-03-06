package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;

/**
 *
 * @author pavanj
 */
public interface ManualDialService {

    /**
     * 
     * @param username
     * @param agentUniqId
     * @param agentId
     * @param agentMode
     * @param custNumber
     * @param agentNumber
     * @param isSip
     * @param campaignId
     * @param uui
     * @param checkPrefix when calling from callback dial this should be <code>false</code> as the number is already having the prefix.
     * While using from other places this should be <code>true</code>.
     * @param disclaimer
     * @return
     */
    public StatusMessage manualDial(String username, Long agentUniqId, String agentId, String agentMode, String custNumber, String agentNumber, boolean isSip, Long campaignId, String uui, boolean checkPrefix, String disclaimer);
    
    
    public StatusMessage checkDialStatus(String ucid);
}

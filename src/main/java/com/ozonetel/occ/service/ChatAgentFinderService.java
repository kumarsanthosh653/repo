package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;

/**
 *
 * @author pavanj
 */
public interface ChatAgentFinderService {

    public StatusMessage findAgent(String apiKey, String chatCustName, String sessionId, String clientId,String did, String skill);
}

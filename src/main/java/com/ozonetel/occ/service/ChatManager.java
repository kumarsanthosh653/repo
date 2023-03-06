package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;

/**
 *
 * @author pavanj
 */
public interface ChatManager {

    public StatusMessage sendMessage(String senderName, String recipientAgentID, String recipientAgentName, String recipientClientId, String message,String timeSent);
}

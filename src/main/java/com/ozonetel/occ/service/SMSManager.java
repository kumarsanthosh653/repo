package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;
import java.math.BigInteger;

/**
 *
 * @author pavanj
 */
public interface SMSManager {
    /**
     *
     * @param user
     * @param agentId
     * @param destination
     * @param msg
     * @return
     */
    public StatusMessage sendSMS(String user, BigInteger ucid,Long campaignId,String url,String requestType,String agentId,String destination,String msg, String entityId, String templateId);

    public StatusMessage sendWhatsappMSG(String username,String whatsappURL,String requestType, String recipient, String templateName, String replacementText);
}

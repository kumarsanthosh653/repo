package com.ozonetel.occ.service;

import com.ozonetel.occ.model.StatusMessage;

/**
 *
 * @author pavanj
 */
public interface Dialer {

    /**
     * Sends preview dial command( {@link com.ozonetel.occ.Constants.PREVIEW_DIAL} ) to Dialer.
     *
     * @param username user name
     * @param agentId agent login id
     * @param agentPhoneNumber agent number
     * @param dataId preview data id
     * @param customerNumber customer number
     * @return
     */
    public StatusMessage dial(String username, String agentId, String agentPhoneNumber, Long dataId,String customerNumber);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import java.util.Date;

/**
 *
 * @author root
 */
public interface PreviewDialerManager extends OCCManager {

    public void updatePreivewDialStatus(String ucid, String did, String phoneNumber, Date startTime, String Status);

    public String dial(String username, Long agentUniqId, String agentId, String agentMode, String agentPhoneNumber, String previewId);

    public String manualDial(String username, String agentId, String custNumber, String agentPhoneNumber, String campaignId);

    public String informDialer(String username, String agentId, final Long releasedFromCampaignId);

    public String callBackDial(String callBackId);
    

}

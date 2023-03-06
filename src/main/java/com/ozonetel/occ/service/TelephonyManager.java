package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.ConferenceStatus;
import com.ozonetel.occ.model.StatusMessage;
import java.math.BigInteger;

/**
 * All telephony actions.
 * <ul>
 * <li>CALL_HOLD</li>
 * <li>CONFERENCE</li>
 * </ul>
 *
 * @author pavanj
 */
public interface TelephonyManager {

    public StatusMessage hold(String customer, Long agentUniqueId, String agentLoginNumber, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToHold, String did, BigInteger campId, String audioUrl);

    public StatusMessage unHold(String customer, Long agentUniqueId, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToUnHold, String did);

    public StatusMessage mute(String customer, Long agentUniqueId, String agentId, String monitorUcid, String ucid, String numberToMute, String did);

    public StatusMessage unMute(String customer, Long agentUniqueId, String agentId, String monitorUcid, String ucid, String numberToMute, String did);

    /**
     * Locks the participant agent and sends conference request to Tele api.
     * Logs the conference event in DB.
     *
     * @param username
     * @param initiatedAgentId
     * @param participantAgentId
     * @param monitorUcid
     * @param ucid
     * @param did
     * @param customerNumber
     * @param isConferenceHold if yes <code>CONFERENCE_HOLD</code> request will
     * be sent other wise just <code>CONFERENCE</code> will be sent.
     * @return
     */
    public ConferenceStatus agentConference(String username, Long initiateAgentUniqId, String initiatedAgentId, String participantAgentId, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold);

    public ConferenceStatus agentTransfer(String username, Long initiateAgentUniqId, String initiatedAgentId, String participantAgentId, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold, String campId, String appAudioURL, String record, String callType,String uui);

    public ConferenceStatus phoneConference(String username, String initiatedAgentId, Long transferNumberId, String participantPhone, boolean isSip, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold);

    public ConferenceStatus phoneTransfer(String username, String initiatedAgentId, Long transferNumberId, String participantPhone, boolean isSip, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold, String campId, String appAudioURL, String record, String callType);

    public StatusMessage kickCall(String username, String agentId, String monitorUcid, String ucid, String did, String phoneNumberToKick);

    /**
     * Sends <code>DROP_HOLD</code> command to telephony API.
     *
     * @param username
     * @param agentId
     * @param monitorUcid
     * @param ucid
     * @param did
     * @param holdNumber Customer's Phonenumber.
     * @return
     */
    public StatusMessage dorpHold(String username, String agentId, String monitorUcid, String ucid, String did, String agentNumber, String holdNumber);

    public StatusMessage ivrTransfer(String username, String agentId, String monitorUcid, String ucid, String did, String agentNumber, String customerNumber, boolean isSip, String url, String campId, Long agentUniqId, String appAudioURL, Campaign campaign, String designerName, String agentName, String skillName, int transferType,String uui, int blindTransfer);

    public StatusMessage consultativeHoldSkillTransfer(String username, String agentId, String monitorUcid, String ucid, String did, String agentNumber, String customerNumber, boolean isSip, String url, String campId, Long agentUniqId, String appAudioURL, String record,String uui);

    public StatusMessage initiateAgentBridge(String agentId, Long agentUniqId, String agentPhone, String user, String apiKey);

}

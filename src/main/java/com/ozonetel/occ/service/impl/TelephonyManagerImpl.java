package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.service.TelephonyCommand;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallConfDetail;
import com.ozonetel.occ.model.CallHoldDetail;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.ConferenceStatus;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.TelephonyManager;
import com.ozonetel.occ.service.HoldDetailManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.HttpUtils;
import com.ozonetel.occ.util.KookooUtils;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author pavanj
 */
public class TelephonyManagerImpl implements TelephonyManager, MessageSourceAware {

    @Override
    public StatusMessage kickCall(String user, String agentId, String monitorUcid, String ucid, String did, String phoneNumberToKick) {
//-------------------------------------                 
        try {
            return sendTelephonyRequest(TelephonyCommand.KICK_CALL, user, agentId, monitorUcid, "", phoneNumberToKick, false, did, null, null, null);
        } catch (HttpResponseException he) {

            logger.error(he.getMessage(), he);
            return new StatusMessage(Status.ERROR,
                    messageSource.getMessage(he.getStatusCode() == 404 ? "404.kickcall" : "fail.phoneconference", null, Locale.getDefault()));

        } catch (IOException | URISyntaxException ex) {
            logger.error(ex.getMessage(), ex);
            return new StatusMessage(Status.ERROR,
                    messageSource.getMessage("fail.phoneconference", null, Locale.getDefault()));
        }
    }

    @Override
    public StatusMessage mute(String customer, Long agentUniqueId, String agentId, String monitorUcid, String ucid, String numberToMute, String did) {
//-------------------------------------        
        try {
            logger.debug(" customer--" + customer + " agentId--" + agentId + " monitorUcid--" + monitorUcid + " ucid--" + ucid + " numberTomute--" + numberToMute + " did--" + did);
            return sendTelephonyRequest(TelephonyCommand.CALL_MUTE, customer, agentId, monitorUcid, "", numberToMute, false, did, null, null, null);
        } catch (IOException | URISyntaxException ex) {
            logger.error(ex.getMessage(), ex);
            return new StatusMessage(Status.ERROR,
                    messageSource.getMessage("fail.mute", null, Locale.getDefault()));
        }
    }

    @Override
    public StatusMessage unMute(String customer, Long agentUniqueId, String agentId, String monitorUcid, String ucid, String numberToMute, String did) {
//-------------------------------------        
        try {
            return sendTelephonyRequest(TelephonyCommand.CALL_UNMUTE, customer, agentId, monitorUcid, "", numberToMute, false, did, null, null, null);
        } catch (IOException | URISyntaxException ex) {
            logger.error(ex.getMessage(), ex);
            return new StatusMessage(Status.ERROR,
                    messageSource.getMessage("fail.unmute", null, Locale.getDefault()));
        }
    }

    @Override
    public StatusMessage hold(String customer, Long agentUniqueId, String agentLoginNumber, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToHold, String did, BigInteger campId, String audioUrl) {
//-------------------------------------         

        try {

            //Added by Rajesh 
            User user = userManager.getUserByUsername(customer);
            logger.debug("The Username : " + user.getUsername() + " UserId : " + user.getId());

            CallHoldDetail tmpHoldDetail = holdDetailManager.getCallHoldDetailByUCID(ucid, numberToHold);
            if (tmpHoldDetail != null && tmpHoldDetail.getEndTime() == null) {//there is hold record on the same number with no end time.

                tmpHoldDetail.setEndTime(new Date());
                holdDetailManager.save(tmpHoldDetail);
                agentManager.updateHoldStartTime(agentUniqueId, false);
                logger.debug("Saved call hold detail while doing hold on the same number:" + tmpHoldDetail);
            } else {
                logger.error("Got CallHoldDetail as null for ucid:" + ucid + " | Caller id:" + numberToHold + " | monitor ucid:" + monitorUcid + " | CampID:" + campId + " | UserName:" + customer);
            }
            logger.debug("Sending hold request to kookoo and the CampaignID : " + campId);
            StatusMessage statusMessage = sendTelephonyRequest(TelephonyCommand.CALL_HOLD, customer, agentId, monitorUcid.toString(), agentLoginNumber, numberToHold, false, did, null, null, audioUrl);

            if (statusMessage.getStatus() == Status.SUCCESS) {//if hold is success log it in database.
                CallHoldDetail callHoldDetail = new CallHoldDetail();
                callHoldDetail.setStartTime(new Date());
                callHoldDetail.setMonitorUcid(monitorUcid);
                callHoldDetail.setCallerNumber(numberToHold);
                callHoldDetail.setUcid(ucid);
                callHoldDetail.setUserId(BigInteger.valueOf(user.getId()));
                callHoldDetail.setCampId(campId);
                if (StringUtils.isNotBlank(did)) {
                    callHoldDetail.setDid(new Long(did));
                }
                Agent a = null;
                if (agentUniqueId != null) {
                    a = agentManager.get(agentUniqueId);
                } else {
                    a = agentManager.getAgentByAgentIdV2(customer, agentId);
                }
                callHoldDetail.setAgent(a);
                holdDetailManager.save(callHoldDetail);
                logger.debug("Agent contact : " + a.getContact() + " customer to hold : " + numberToHold);
                if (StringUtils.equals(a.getContact(), numberToHold)) {
                    agentManager.updateHoldStartTime(a.getId(), true);
                }
            }

            return statusMessage;
        } catch (Exception ex) {
            logger.error(TelephonyCommand.CALL_HOLD + " failed | Exception:" + ex.getMessage(), ex);
            return new StatusMessage(Status.ERROR, messageSource.getMessage("exception.hold.request", null, Locale.getDefault()));
        }

    }

    @Override
    public StatusMessage unHold(String customer, Long agentUniqueId, String agentId, BigInteger monitorUcid, BigInteger ucid, String numberToUnHold, String did) {
//-------------------------------------         

        try {

            StatusMessage statusMessage = sendTelephonyRequest(TelephonyCommand.CALL_UNHOLD, customer, agentId, monitorUcid.toString(), "", numberToUnHold, false, did, null, null, null);

            if (statusMessage.getStatus() == Status.SUCCESS) {//if hold is success log it in database.
                CallHoldDetail callHoldDetail = holdDetailManager.getCallHoldDetailByUCID(ucid, numberToUnHold);
                if (callHoldDetail != null) {
                    callHoldDetail.setEndTime(new Date());
                    holdDetailManager.save(callHoldDetail);
                }
                agentManager.updateHoldStartTime(agentUniqueId, false);
            }

            return statusMessage;
        } catch (Exception ex) {
            logger.error(TelephonyCommand.CALL_HOLD + " failed | Exception:" + ex.getMessage(), ex);
            return new StatusMessage(Status.ERROR, messageSource.getMessage("exception.hold.request", null, Locale.getDefault()));
        }

    }

    @Override
    public ConferenceStatus phoneConference(String user, String initiatedAgentId, Long transferNumberId, String participantPhone, boolean isSip, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold) {
//-------------------------------------                 
        // ---- > log event
        CallConfDetail confDetail = new CallConfDetail();
        confDetail = callConfDetailManager.save(confDetail);
        Long confId = confDetail.getId();

        TelephonyCommand telephonyCommand = isConferenceHold ? TelephonyCommand.TRANSFER : TelephonyCommand.CONFERENCE;
        // ---- > Prepare conf callback URL
//        StringBuilder confCbUrl = new StringBuilder(conferenceCallbackUrl);
//        confCbUrl.append("?ucid=").append(ucid)
//                .append("&monitorUcid=").append(monitorUcid)
//                .append("&action=").append(telephonyCommand.toString())
//                .append("&agentId=").append(initiatedAgentId).append("&id=").append(confId)
//                .append("&phoneNumber=").append(participantPhone).append("&user=").append(user);
        try {

            URIBuilder confCbUrl = new URIBuilder(conferenceCallbackUrl);
            confCbUrl.addParameter("ucid", ucid)
                    .addParameter("monitorUcid", monitorUcid)
                    .addParameter("action", telephonyCommand.toString())
                    .addParameter("agentId", initiatedAgentId)
                    .addParameter("id", "" + confId)
                    .addParameter("phoneNumber", participantPhone)
                    .addParameter("user", user);
            logger.debug("cb url" + confCbUrl.build().toString());
            //Date startTime = new Date();
            confDetail.setStartTime(new Date());
            Agent initiateAgent = agentManager.getAgentByAgentIdV2(user, initiatedAgentId);
            // ----> Do telephony request
            //sendTelephonyRequest(TelephonyCommand command, String user, String initiateAgentId, String monitorUcid, String iniateAgentNumber, String participantNumber, boolean isSip, String did, String holdNumber, String callbackUrl)
            StatusMessage statusMessage = sendTelephonyRequest(telephonyCommand, user, initiatedAgentId, monitorUcid, initiateAgent.getFwpNumber().getPhoneNumber(), participantPhone, isSip, did, customerNumber, confCbUrl.build().toString(), null);

            confDetail.setConfCreator(initiateAgent);
            confDetail.setOtherParticipant(participantPhone);
            confDetail.setUserId(initiateAgent.getUserId());
            confDetail.setUcid(new BigInteger(ucid));
            confDetail.setDid(did);
            confDetail.setPickUpTime(new Date());
            confDetail.setDialStatus(statusMessage.getStatus().toString());
            confDetail.setExitStatus(statusMessage.getMessage().toString());
            callConfDetailManager.save(confDetail);
            return new ConferenceStatus(statusMessage.getStatus(), statusMessage.getMessage(), participantPhone);
        } catch (Exception ex) {
            logger.error("Phone conference failed: {Agent initiated=" + initiatedAgentId
                    + ", Participante phone=" + participantPhone + ", ucid=" + ucid + " } " + ex.getMessage(), ex);
        }

        return new ConferenceStatus(Status.ERROR,
                messageSource.getMessage("fail.phoneconference", null, Locale.getDefault()));
    }

    @Override
    public ConferenceStatus phoneTransfer(String user, String initiatedAgentId, Long transferNumberId, String participantPhone, boolean isSip, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold, String campId, String appAudioURL, String record, String callType) {
//-------------------------------------                 

        TelephonyCommand telephonyCommand = isConferenceHold ? TelephonyCommand.TRANSFER : TelephonyCommand.CONFERENCE;

        try {
            Agent initiateAgent = agentManager.getAgentByAgentIdV2(user, initiatedAgentId);
            // ----> Do telephony request
            //sendTelephonyRequestV2(TelephonyCommand command, String user, String initiateAgentId, String participantAgentId, String customerNumber,String monitorUcid, String iniateAgentNumber, String participantNumber, boolean isSip, String did, String holdNumber)
            StatusMessage statusMessage = sendTelephonyRequest_transfer(telephonyCommand, user, initiatedAgentId, "0", customerNumber, monitorUcid, initiateAgent.getFwpNumber().getPhoneNumber(), participantPhone, isSip, did, customerNumber, campId, "3", appAudioURL, record, callType,null);

            return new ConferenceStatus(statusMessage.getStatus(), statusMessage.getMessage(), participantPhone);
        } catch (Exception ex) {
            logger.error("Phone conference failed: {Agent initiated=" + initiatedAgentId
                    + ", Participante phone=" + participantPhone + ", ucid=" + ucid + " } " + ex.getMessage(), ex);
        }

        return new ConferenceStatus(Status.ERROR,
                messageSource.getMessage("fail.phoneconference", null, Locale.getDefault()));
    }

    @Override
    public ConferenceStatus agentConference(String user, Long initiateAgentUniqId, String initiatedAgentId, String participantAgentId, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold) {
//-------------------------------------         
        Agent participantAgent = null;
        // ----> Lock the participant agent.
        if (agentManager.lockIfAgentAvailable(user, participantAgentId, customerNumber)) {
            String cachedAgentUniqId = redisAgentManager.getString(user + ":agent:" + participantAgentId);
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                participantAgent = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                participantAgent = agentManager.getAgentByAgentIdV2(user, participantAgentId);
            }
        }
        try {
            if (participantAgent != null && (participantAgent.getMode() == Agent.Mode.INBOUND || participantAgent.getMode() == Agent.Mode.BLENDED)) {

                // ---- > log event
                CallConfDetail confDetail = new CallConfDetail();
                confDetail = callConfDetailManager.save(confDetail);
                Long confId = confDetail.getId();

                TelephonyCommand telephonyCommand = isConferenceHold ? TelephonyCommand.TRANSFER : TelephonyCommand.CONFERENCE;
                String participantAgentNumber = participantAgent.getFwpNumber().getPhoneNumber();
                // ---- > Prepare conf callback URL
//                StringBuilder confCbUrl = new StringBuilder(conferenceCallbackUrl);
                URIBuilder confCbUrl = new URIBuilder(conferenceCallbackUrl);
                confCbUrl.addParameter("ucid", ucid)
                        .addParameter("action", telephonyCommand.toString())
                        .addParameter("agentId", initiatedAgentId)
                        .addParameter("id", "" + confId)
                        .addParameter("phoneNumber", participantAgentNumber)
                        .addParameter("user", user)
                        .addParameter("confAgentId", participantAgent.getAgentId())
                        .addParameter("holdNumber", customerNumber)
                        .addParameter("did", did)
                        .addParameter("confAgentUniqId", "" + participantAgent.getId())
                        .addParameter("monitorUcid", monitorUcid);
                logger.debug("cb url" + confCbUrl.build().toString());

                confDetail.setStartTime(new Date());
                Agent initiateAgent = agentManager.get(initiateAgentUniqId);

                Token tokenResponse = TokenFactory.createToken();
                tokenResponse.setType("incomingConferenceCall");
                tokenResponse.setString("custNumber", customerNumber);
                tokenResponse.setString("callType", "Conference Call");
                tokenResponse.setString("callerId", initiatedAgentId);
                tokenResponse.setString("ucid", ucid);
                tokenResponse.setString("monitorUcid", monitorUcid);
                tokenResponse.setString("did", did);
                tokenResponse.setString("uui", customerNumber);
                tokenServer = getTokenServer();
                if (participantAgent.getClientId() != null && tokenServer.getConnector(participantAgent.getClientId()) != null) {
                    tokenServer.sendToken(tokenServer.getConnector(participantAgent.getClientId()), tokenResponse);
                    logger.debug("Sent conference calling event to participant agent :[" + participantAgent + "]");
                } else {
                    logger.fatal("Unable to send event to participant agent [" + participantAgent + "]");
                }

                // ----> Do telephony request
                StatusMessage statusMessage = sendTelephonyRequest(telephonyCommand, user, initiatedAgentId, monitorUcid, initiateAgent.getFwpNumber().getPhoneNumber(), participantAgentNumber, participantAgent.getFwpNumber().isSip(), did, customerNumber, confCbUrl.build().toString(), null);
                confDetail.setAgentParticipant(participantAgent);
                confDetail.setOtherParticipant(participantAgentNumber);
                confDetail.setConfCreator(initiateAgent);
                confDetail.setUserId(initiateAgent.getUserId());
                confDetail.setUcid(new BigInteger(ucid));
                confDetail.setDid(did);
                confDetail.setPickUpTime(new Date());
                confDetail.setEndTime(new Date());
                confDetail.setDialStatus(statusMessage.getStatus().toString());
                confDetail.setExitStatus(statusMessage.getMessage().toString());
                callConfDetailManager.save(confDetail);

                // ----> Unlock participant agent if conference fails.
                if (statusMessage.getStatus() != Status.SUCCESS) {
                    agentManager.releaseOnlyAgentFlag(participantAgent.getId());
//                    participantAgent.setNextFlag(0L);
//                    agentManager.save(participantAgent);
                }

                return new ConferenceStatus(statusMessage.getStatus(), statusMessage.getMessage(), participantAgentNumber);

            } else {
                if (participantAgent != null) {
                    logger.info("Unlocking participant agent:" + participantAgent + " | Agent is not in inbound/Blended.");
                    agentManager.releaseOnlyAgentFlag(participantAgent.getId());
//                    participantAgent.setNextFlag(0L);
//                    agentManager.save(participantAgent);
                }
                logger.info("Can't get agent [" + participantAgent + "] to conference, state : " + participantAgent);
                return new ConferenceStatus(Status.ERROR,
                        messageSource.getMessage(participantAgent == null ? "error.conference.invalidagent" : "error.conference.agentnotidle", null, Locale.getDefault()));
            }
        } catch (Exception ex) {
            logger.error("Agent conference failed: {Agent initiated=" + initiatedAgentId
                    + ", Participante Agent=" + participantAgentId + ", ucid=" + ucid + " } " + ex.getMessage(), ex);
            logger.info("Unlocking participant agent:" + participantAgent + " | Exception:" + ex.getMessage());
            agentManager.releaseOnlyAgentFlag(participantAgent.getId());
//            participantAgent.setNextFlag(0L);
//            agentManager.save(participantAgent);
        }

        return new ConferenceStatus(Status.ERROR,
                messageSource.getMessage("fail.agentconference", null, Locale.getDefault()));

    }

    @Override
    public ConferenceStatus agentTransfer(String user, Long initiateAgentUniqId, String initiatedAgentId, String participantAgentId, String monitorUcid, String ucid, String did, String customerNumber, boolean isConferenceHold, String campId, String appAudioURL, String record, String callType,String uui) {
//-------------------------------------         

        Agent participantAgent = null;
        // ----> Lock the participant agent.
        if (agentManager.lockIfAgentAvailable(user, participantAgentId, customerNumber)) {
            String cachedAgentUniqId = redisAgentManager.getString(user + ":agent:" + participantAgentId);
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                participantAgent = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                participantAgent = agentManager.getAgentByAgentIdV2(user, participantAgentId);
            }
        }

        try {
            if (participantAgent != null && (participantAgent.getMode() == Agent.Mode.INBOUND || participantAgent.getMode() == Agent.Mode.BLENDED)) {

                TelephonyCommand telephonyCommand = isConferenceHold ? TelephonyCommand.TRANSFER : TelephonyCommand.CONFERENCE;
                String participantAgentNumber = participantAgent.getFwpNumber().getPhoneNumber();

                Agent initiateAgent = agentManager.get(initiateAgentUniqId);
                // ----> Do telephony request
                StatusMessage statusMessage = sendTelephonyRequest_transfer(telephonyCommand, user, initiatedAgentId, participantAgentId, customerNumber, monitorUcid, initiateAgent.getFwpNumber().getPhoneNumber(), participantAgentNumber, participantAgent.getFwpNumber().isSip(), did, customerNumber, campId, "1", appAudioURL, record, callType,uui);
                // ----> Unlock participant agent if conference fails.
                if (statusMessage.getStatus() != Status.SUCCESS) {
                    agentManager.releaseOnlyAgentFlag(participantAgent.getId());
//                    participantAgent.setNextFlag(0L);
//                    agentManager.save(participantAgent);
                }

                return new ConferenceStatus(statusMessage.getStatus(), statusMessage.getMessage(), participantAgentNumber);

            } else {
                if (participantAgent != null) {
                    logger.info("Unlocking participant agent:" + participantAgent + " | Agent is not in inbound/Blended.");
                    agentManager.releaseOnlyAgentFlag(participantAgent.getId());
//                    participantAgent.setNextFlag(0L);
//                    agentManager.save(participantAgent);
                }
                logger.info("Can't get agent [" + participantAgent + "] to conference, state : " + participantAgent);
                return new ConferenceStatus(Status.ERROR,
                        messageSource.getMessage(participantAgent == null ? "error.conference.invalidagent" : "error.conference.agentnotidle", null, Locale.getDefault()));
            }
        } catch (Exception ex) {
            logger.error("Agent conference failed: {Agent initiated=" + initiatedAgentId
                    + ", Participante Agent=" + participantAgentId + ", ucid=" + ucid + " } " + ex.getMessage(), ex);
            logger.info("Unlocking participant agent:" + participantAgent + " | Exception:" + ex.getMessage());
            agentManager.releaseOnlyAgentFlag(participantAgent.getId());
//            participantAgent.setNextFlag(0L);
//            agentManager.save(participantAgent);
        }

        return new ConferenceStatus(Status.ERROR,
                messageSource.getMessage("fail.agentconference", null, Locale.getDefault()));

    }

    @Override
    public StatusMessage dorpHold(String username, String agentId, String monitorUcid, String ucid, String did, String agentNumber, String holdNumber) {
//-------------------------------------        
        try {
            return sendTelephonyRequest(TelephonyCommand.DROP_HOLD, username, agentId, monitorUcid, "", agentNumber, false, did, holdNumber, null, null);
        } catch (IOException | URISyntaxException ex) {
            logger.error(ex.getMessage(), ex);
            if (ex instanceof HttpResponseException) {
                logger.error("Drop me failed -> " + ((HttpResponseException) (ex)).getStatusCode() + " | " + ((HttpResponseException) (ex)).getMessage());
                if (((HttpResponseException) (ex)).getStatusCode() == 404) {
                    return new StatusMessage(Status.ERROR,
                            "404");
                }
            }
            return new StatusMessage(Status.ERROR,
                    messageSource.getMessage("fail.dropme", null, Locale.getDefault()));
        }
    }

   @Override
    public StatusMessage ivrTransfer(String username, String agentId, String monitorUcid, String ucid, String did, String agentNumber, String customerNumber, boolean isSip, String url, String campId, Long agentUniqId, String appAudioURL, Campaign campaign, String designerName, String agentName, String skillName,int transferType,String uui, int blindTransfer) {
        try {
           
            URIBuilder appUrl = new URIBuilder(url);
            appUrl.setParameter("agentId", agentId);
            appUrl.setParameter("agentUniqueId", "" + agentUniqId);
            appUrl.setParameter("username", username);
            appUrl.setParameter("monitorUcid", monitorUcid);
            appUrl.setParameter("campaignId", campId);
            appUrl.setParameter("agentName", agentName);
            appUrl.setParameter("campaignName", campaign.getCampignName());
            //appUrl.setParameter("transfer", String.valueOf(transferType));
            if (StringUtils.isNotBlank(skillName)) {
                appUrl.setParameter("fromSkillName", skillName);
            }
            
            if (StringUtils.isNotBlank(uui)) {
                appUrl.setParameter("uui", uui);
            }
            logger.debug("App url for ivr transfer : " + appUrl.build().toString());

            URIBuilder uRIBuilder = new URIBuilder(telephonyApiUrl);
            
            String transferTypeStr = TelephonyCommand.IVR_TRANSFER.toString();
            
            if (transferType == 4 && blindTransfer == 3) 
                transferTypeStr = TelephonyCommand.CONSULTATIVE_IVR_TRANSFER.toString();
            
            uRIBuilder.setParameter(ACTION, transferTypeStr);
            String tmpUcid = redisAgentManager.getString(monitorUcid);
            if (tmpUcid != null) {//getting real UCID for dial by holding agent(we generate our own UCID)
                logger.debug("Using real UCID instead of created UCID -> original(KooKoo) :" + tmpUcid + " | Created:" + monitorUcid);
                uRIBuilder.setParameter("ucid", tmpUcid);
            } else {
                uRIBuilder.setParameter("ucid", monitorUcid);
            }
//            uRIBuilder.setParameter("ucid", monitorUcid);
            uRIBuilder.setParameter("phoneno", customerNumber);
            uRIBuilder.setParameter("agentNumber", agentNumber);
            uRIBuilder.setParameter("did", did);
            uRIBuilder.setParameter("appURL", appUrl.build().toString());
            uRIBuilder.setParameter("isSip", "" + isSip);
         //   uRIBuilder.setParameter("transferType", String.valueOf(transferType));

            if (StringUtils.isNotBlank(appAudioURL)) {
                uRIBuilder.setParameter("moh_url", appAudioURL);
            }

            logger.debug("uui came : "+uui);
            if (StringUtils.isNotBlank(uui)) {
                uRIBuilder.setParameter("uui", uui);
            }
            String requestUrl = uRIBuilder.build().toString();

            // ----> Send telephony API request.
            logger.debug("Telephony  request/IVR Transfer(" + TelephonyCommand.IVR_TRANSFER + ") | url:" + requestUrl);
            HttpResponseDetails hrd = HttpUtils.doGet(requestUrl, 60000);
            
            logger.debug("Telephony  request/IVR Transfer :" + requestUrl + " | Response :" + (hrd != null ? hrd.toLongString() : hrd));
            StatusMessage sm = KookooUtils.parseKookooResponse(hrd.getResponseBody());
            
           
           // if(transferType !=2){
            
            if (sm.getStatus() == Status.SUCCESS && transferType != 2){
               
                int isUpdated = reportManager.updateReportWithTransferDetails(Long.valueOf(ucid), new Long(TransferType.IVR.ordinal()), designerName);
                logger.debug("updated report with transfer details ? " + isUpdated + " | " + ucid + " | designer | " + (StringUtils.isNotBlank(designerName) ? designerName : ""));
            }
            
            return sm;
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return new StatusMessage(Status.ERROR, "unknown");
    }

    @Override
    public StatusMessage consultativeHoldSkillTransfer(String username, String agentId, String monitorUcid, String ucid, String did, String agentNumber, String customerNumber, boolean isSip, String url, String campId, Long agentUniqId, String appAudioURL, String record,String uui) {
        logger.debug("Consultative hold skill transfer for : " + agentId + " | " + monitorUcid);
        try {
            if (StringUtils.isBlank(agentNumber)) {
                agentNumber = agentManager.get(agentUniqId).getPhoneNumber();
            }
            User user1 = userManager.getUserByUsername(username);
            logger.debug(user1);
            // ----- > Prepare telephony request URL
            URIBuilder uRIBuilder = new URIBuilder(telephonyApiUrl);
            uRIBuilder.setParameter(ACTION, "CONSULTATIVE_SKILL_TRANSFER");
            uRIBuilder.setParameter("ucid", monitorUcid);
            uRIBuilder.setParameter("phoneno", customerNumber);
            uRIBuilder.setParameter("agentNumber", agentNumber);
            uRIBuilder.setParameter("did", did);
            uRIBuilder.setParameter("isSip", "" + isSip);
            uRIBuilder.setParameter("initiatedAgentId", agentId);
//            uRIBuilder.setParameter("transferAgentId", participantAgentId);
            uRIBuilder.setParameter("customerNumber", customerNumber);
            uRIBuilder.setParameter("MsgSrvIp", user1.getUrlMap().getLocalIp());
            uRIBuilder.setParameter("campaignId", campId);
            uRIBuilder.setParameter("transferType", "1");
            uRIBuilder.setParameter("holdNumber", customerNumber);
            if (StringUtils.isNotBlank(appAudioURL)) {
                uRIBuilder.setParameter("moh_url", appAudioURL);
            }

            if (StringUtils.isNotBlank(record)) {
                uRIBuilder.setParameter("record", record);
            }
            logger.debug("uui came : "+uui);
            if (StringUtils.isNotBlank(uui)) {
                uRIBuilder.setParameter("uui", uui);
            }

            // ---> app url
            URIBuilder appUrl = new URIBuilder(url);
            appUrl.setParameter("agentId", agentId);
            appUrl.setParameter("username", username);
            appUrl.setParameter("monitorUcid", monitorUcid);
//            appUrl.setParameter("campaignId", campId);
            appUrl.setParameter("agentUniqueId", "" + agentUniqId);
            logger.debug("App url for ivr transfer : " + appUrl.build().toString());
            uRIBuilder.setParameter("appURL", appUrl.build().toString());

            Map<String, Object> params = new LinkedHashMap();
            //boolean playRing = false;
            params.put("user_id", user1.getId());
            params.put("isAdmin", 0);
            params.put("param_code", "CONFERENCE_RING");
            List userParam = callConfDetailManager.executeProcedure("call Get_UserParamterV2(?,?,?)", params);
            String val = ((Map<String, Object>) userParam.get(0)).get("ParameterValue") == null ? ((Map<String, Object>) userParam.get(0)).get("DefaultValue").toString() : ((Map<String, Object>) userParam.get(0)).get("ParameterValue").toString();
            logger.debug("Ring enabled--->" + val + " for params " + params);
            uRIBuilder.setParameter("playRing", "" + val);

            String requestUrl = uRIBuilder.build().toString();

            // ----> Send telephony API request.
            logger.debug("Telephony  request(" + TelephonyCommand.IVR_TRANSFER + ") | url:" + requestUrl);
            HttpResponseDetails hrd = HttpUtils.doGet(requestUrl, 60000);
            logger.debug("Telephony request :" + requestUrl + " | Response :" + (hrd != null ? hrd.toLongString() : hrd));
            return KookooUtils.parseKookooResponse(hrd.getResponseBody());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new StatusMessage(Status.ERROR, "unknown");
    }

    /**
     * @param command
     * @param monitorUcid
     * @param participantNumber
     * @param did
     * @param holdNumber only for <code>TelephonyCommand.CONFERENCE_HOLD</code>
     * @param callbackUrl only for
     * <code>TelephonyCommand.CONFERENCE,TelephonyCommand.CALL_BARGEIN</code>
     * @return
     */
    private StatusMessage sendTelephonyRequest(TelephonyCommand command, String user, String initiateAgentId, String monitorUcid, String iniateAgentNumber, String participantNumber, boolean isSip, String did, String holdNumber, String callbackUrl, String audioUrl) throws URISyntaxException, IOException {
//-------------------------------------         
        logger.debug("Making request for Hold to Kookoo");
        try {
            if (StringUtils.isBlank(iniateAgentNumber) && StringUtils.isNotBlank(initiateAgentId)) {
                iniateAgentNumber = agentManager.getAgentByAgentIdV2(user, initiateAgentId).getPhoneNumber();
            }

            // ----- > Prepare telephony request URL
            URIBuilder uRIBuilder = new URIBuilder(telephonyApiUrl);
            uRIBuilder.setParameter(ACTION, command.toString());
            String tmpUcid = redisAgentManager.getString(monitorUcid);
            if (tmpUcid != null) {//getting real UCID for dial by holding agent(we generate our own UCID)
                logger.debug("Using real UCID instead of created UCID -> original(KooKoo) :" + tmpUcid + " | Created:" + monitorUcid);
                uRIBuilder.setParameter("ucid", tmpUcid);
            } else {
                uRIBuilder.setParameter("ucid", monitorUcid);
            }
            uRIBuilder.setParameter("phoneno", participantNumber);
            uRIBuilder.setParameter("agentNumber", iniateAgentNumber);
            uRIBuilder.setParameter("did", did);
            uRIBuilder.setParameter("isSip", "" + isSip);
            uRIBuilder.setParameter("username", user);

            if (StringUtils.isNotBlank(audioUrl)) {
                uRIBuilder.setParameter("moh_url", audioUrl);
            }

            if (StringUtils.isNotBlank(callbackUrl)) {
                uRIBuilder.setParameter("cburl", callbackUrl);
            }

            //----> hold number is added newly.
            if (command == TelephonyCommand.TRANSFER || command == TelephonyCommand.DROP_HOLD) {
                uRIBuilder.setParameter("holdNumber", holdNumber);
            }
            if (command == TelephonyCommand.TRANSFER || command == TelephonyCommand.CONFERENCE) {
                Map<String, Object> params = new LinkedHashMap();
                //boolean playRing = false;
                params.put("user_id", userManager.getUserByUsername(user).getId());
                params.put("isAdmin", 0);
                params.put("param_code", "CONFERENCE_RING");
                List userParam = callConfDetailManager.executeProcedure("call Get_UserParamterV2(?,?,?)", params);
                String val = ((Map<String, Object>) userParam.get(0)).get("ParameterValue") == null ? ((Map<String, Object>) userParam.get(0)).get("DefaultValue").toString() : ((Map<String, Object>) userParam.get(0)).get("ParameterValue").toString();
                uRIBuilder.setParameter("playRing", "" + val);
            }
            String requestUrl = uRIBuilder.build().toString();

            // ----> Send telephony API request.
            logger.debug("Telephony  request(" + command + ") | url:" + requestUrl);
            HttpResponseDetails hrd = HttpUtils.doGet(requestUrl, 60000);
            logger.debug("Conference request :" + requestUrl + " | Response :" + (hrd != null ? hrd.toLongString() : hrd));
            return KookooUtils.parseKookooResponse(hrd.getResponseBody());
        } catch (Exception e) {
            if (e instanceof HttpResponseException) {
                logger.error(e.getMessage() + "(code:" + ((HttpResponseException) e).getStatusCode() + ")", e);
                throw e;
            } else {
                logger.error(e.getMessage(), e);
            }
        }
        return new StatusMessage(Status.ERROR, "unknown");
    }

    private StatusMessage sendTelephonyRequest_transfer(TelephonyCommand command, String user, String initiateAgentId, String participantAgentId, String customerNumber, String monitorUcid, String iniateAgentNumber, String participantNumber, boolean isSip, String did, String holdNumber, String campId, String transferType, String appAudioURL, String record, String callType,String uui) throws URISyntaxException, IOException {
//-------------------------------------         
        logger.debug("in telephony request v2:" + user + "--" + userManager + " : campaign id:" + campId + " | did:" + did);
        try {
            if (StringUtils.isBlank(iniateAgentNumber) && StringUtils.isNotBlank(initiateAgentId)) {
                iniateAgentNumber = agentManager.getAgentByAgentIdV2(user, initiateAgentId).getPhoneNumber();
            }
            User user1 = userManager.getUserByUsername(user);
            logger.debug(user1);
            // ----- > Prepare telephony request URL
            URIBuilder uRIBuilder = new URIBuilder(telephonyApiUrl);
            uRIBuilder.setParameter(ACTION, command.toString());
            String tmpUcid = redisAgentManager.getString(monitorUcid);
            if (tmpUcid != null) {//getting real UCID for dial by holding agent(we generate our own UCID)
                logger.debug("Using real UCID instead of created UCID -> original(KooKoo) :" + tmpUcid + " | Created:" + monitorUcid);
                uRIBuilder.setParameter("ucid", tmpUcid);
            } else {
                uRIBuilder.setParameter("ucid", monitorUcid);
            }
            uRIBuilder.setParameter("phoneno", participantNumber);
            uRIBuilder.setParameter("agentNumber", iniateAgentNumber);
            uRIBuilder.setParameter("did", did);
            uRIBuilder.setParameter("isSip", "" + isSip);
            uRIBuilder.setParameter("initiatedAgentId", initiateAgentId);
            uRIBuilder.setParameter("transferAgentId", participantAgentId);
            uRIBuilder.setParameter("customerNumber", customerNumber);
            uRIBuilder.setParameter("MsgSrvIp", user1.getUrlMap().getLocalIp());
            uRIBuilder.setParameter("campaignId", campId);
            uRIBuilder.setParameter("transferType", transferType);
            uRIBuilder.setParameter("username", user);
            if (StringUtils.isNotBlank(appAudioURL)) {
                uRIBuilder.setParameter("moh_url", appAudioURL);
            }

            if (StringUtils.isNotBlank(record)) {
                uRIBuilder.setParameter("record", record);
            }
            if (StringUtils.isNotBlank(callType)) {
                uRIBuilder.setParameter("callType", callType.equalsIgnoreCase("manual") ? "ToolBarManual" : callType);
            }

            logger.debug("uui came : "+uui);
            if (StringUtils.isNotBlank(uui)) {
                uRIBuilder.setParameter("uui", uui);
            }
            //----> hold number is added newly.
            if (command == TelephonyCommand.TRANSFER || command == TelephonyCommand.DROP_HOLD) {
                uRIBuilder.setParameter("holdNumber", holdNumber);
            }

            if (command == TelephonyCommand.TRANSFER || command == TelephonyCommand.CONFERENCE) {
                Map<String, Object> params = new LinkedHashMap();
                //boolean playRing = false;
                params.put("user_id", userManager.getUserByUsername(user).getId());
                params.put("isAdmin", 0);
                params.put("param_code", "CONFERENCE_RING");
                List userParam = callConfDetailManager.executeProcedure("call Get_UserParamterV2(?,?,?)", params);
                String val = ((Map<String, Object>) userParam.get(0)).get("ParameterValue") == null ? ((Map<String, Object>) userParam.get(0)).get("DefaultValue").toString() : ((Map<String, Object>) userParam.get(0)).get("ParameterValue").toString();
                logger.debug("Ring enabled--->" + val + " for params " + params);
                uRIBuilder.setParameter("playRing", "" + val);
            }

            String requestUrl = uRIBuilder.build().toString();

            // ----> Send telephony API request.
            logger.debug("Telephony  request(" + command + ") | url:" + requestUrl);
            HttpResponseDetails hrd = HttpUtils.doGet(requestUrl, 60000);
            logger.debug("Telephony request :" + requestUrl + " | Response :" + (hrd != null ? hrd.toLongString() : hrd));
            return KookooUtils.parseKookooResponse(hrd.getResponseBody());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof HttpResponseException) {
                throw e;
            }
        }
        return new StatusMessage(Status.ERROR, "unknown");
    }

    @Override
    public StatusMessage initiateAgentBridge(String agentId, Long agentUniqId, String agentPhone, String user, String apiKey) {
        try {
//            User user1 = userManager.getUserByUsername(user);
            logger.debug("agentId : " + agentId + " agentUniqId : " + agentUniqId + " agentPhone : " + agentPhone + " username : " + user + " apiKey : " + apiKey);
            tokenServer = getTokenServer();
            URIBuilder ivrUrl = new URIBuilder(agentBridgeIvrUrl);
            ivrUrl.setParameter("apiKey", apiKey);
            ivrUrl.setParameter("agentId", agentId);
            ivrUrl.setParameter("agentUniqId", agentUniqId.toString());
            ivrUrl.setParameter("agentphonenumber", agentPhone);
            ivrUrl.setParameter("username", user);
//            ivrUrl.setParameter("mdlIp", user1.getUrlMap().getLocalIp());
            ivrUrl.setParameter("wsServerIp", tokenServer.getWsServerUrl());

            URIBuilder callBackUrl = new URIBuilder(agentBridgeCallBackUrl);
            callBackUrl.setParameter("apiKey", apiKey);
            callBackUrl.setParameter("agentId", agentId);
            callBackUrl.setParameter("agentUniqId", agentUniqId.toString());
            callBackUrl.setParameter("agentphonenumber", agentPhone);
            callBackUrl.setParameter("username", user);
            callBackUrl.setParameter("wsServerIp", tokenServer.getWsServerUrl());

            URIBuilder uRIBuilder = new URIBuilder(kooKooAgentBridgeUrl);
            uRIBuilder.setParameter("api_key", apiKey);
            uRIBuilder.setParameter("phone_no", agentPhone);
            uRIBuilder.setParameter("url", ivrUrl.build().toString());
            uRIBuilder.setParameter("callback_url", callBackUrl.build().toString());

            String requestUrl = uRIBuilder.build().toString();

            // ----> Send agent bridge API request.
            logger.debug("Sending Agent Bridge API request | url:" + requestUrl);
            HttpResponseDetails hrd = HttpUtils.doGet(requestUrl, 60000);
            logger.debug("Agent Bridge API request :" + requestUrl + " | Response :" + (hrd != null ? hrd.toLongString() : hrd));
            return KookooUtils.parseKookooResponse(hrd.getResponseBody());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new StatusMessage(Status.ERROR, "unknown");
    }

    private TokenServerLocalImpl getTokenServer() {
        if (tokenServer == null) {
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }

        return tokenServer;
    }

    public void setTelephonyApiUrl(String telephonyApiUrl) {
        this.telephonyApiUrl = telephonyApiUrl;
    }

    public void setConferenceCallbackUrl(String conferenceCallbackUrl) {
        this.conferenceCallbackUrl = conferenceCallbackUrl;
    }

    public void setKooKooAgentBridgeUrl(String kooKooAgentBridgeUrl) {
        this.kooKooAgentBridgeUrl = kooKooAgentBridgeUrl;
    }

    public void setAgentBridgeIvrUrl(String agentBridgeIvrUrl) {
        this.agentBridgeIvrUrl = agentBridgeIvrUrl;
    }

    public void setAgentBridgeCallBackUrl(String agentBridgeCallBackUrl) {
        this.agentBridgeCallBackUrl = agentBridgeCallBackUrl;
    }

    public void setHoldDetailManager(HoldDetailManager holdDetailManager) {
        this.holdDetailManager = holdDetailManager;
    }

    public void setCallConfDetailManager(GenericManager<CallConfDetail, Long> callConfDetailManager) {
        this.callConfDetailManager = callConfDetailManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setTokenServer(TokenServerLocalImpl tokenServer) {
        this.tokenServer = tokenServer;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    private String conferenceCallbackUrl;
    private HoldDetailManager holdDetailManager;
    private GenericManager<CallConfDetail, Long> callConfDetailManager;
    private AgentManager agentManager;
    private UserManager userManager;
    private String telephonyApiUrl;
    private static final Logger logger = Logger.getLogger(TelephonyManagerImpl.class);
    private MessageSource messageSource;
    private final String ACTION = "action";
    private RedisAgentManager redisAgentManager;
    private TokenServerLocalImpl tokenServer;
    private String kooKooAgentBridgeUrl;
    private String agentBridgeIvrUrl;
    private String agentBridgeCallBackUrl;
    private ReportManager reportManager;
}

package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentTokenManager;
import com.ozonetel.occ.service.AgentTransferManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.TelephonyManager;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author pavanj
 */
public class AgentTransferManagerImpl implements AgentTransferManager, MessageSourceAware {

    @Override
    public StatusMessage consultativeHoldTransfer(String username, Long initiateAgentUniqid, String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, String campId, String appAudioURL, String record, String callType,String uui) {
//-------------------------------------  

        StatusMessage statusMessage = telephonyManager.agentTransfer(username, initiateAgentUniqid, agentId, receiverAgentId, monitorUcid.toString(), ucid.toString(), did, custNumber, true, campId, appAudioURL, record, callType,uui);

//        // ----> Send unHold command to agent tool-bar.
//        Map<String, String> requestMap = new LinkedHashMap<>(1);
//        requestMap.put("custNumber", custNumber);
//        agentTokenManager.sendTokenToAgent(username, agentId, "unHoldCustomer", requestMap);
        TransferType transferType = TransferType.CONSULTATIVE_HOLD;
        if (statusMessage.getStatus() == Status.SUCCESS) {//----> Agent is locked successfully.

            Agent receiverAgent = null;
            String cachedAgentUniqId = redisAgentManager.getString(username + ":agent:" + receiverAgentId);
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                receiverAgent = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                receiverAgent = agentManager.getAgentByAgentIdV2(username, receiverAgentId);
            }
            // ----> Save in Report table.
            saveInReport(ucid, did, transferType, receiverAgent);

            return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.transfer", new Object[]{transferType.toReadableString(), Participant.AGENT.toString().toLowerCase(), receiverAgent.getAgentName(), receiverAgent.getAgentId()+":"+receiverAgent.getPhoneNumber()}, Locale.getDefault()));
        }

        return new StatusMessage(Status.ERROR, messageSource.getMessage("fail.transfer", new Object[]{transferType.toReadableString(), statusMessage.getMessage()}, Locale.getDefault()));
    }

    @Override
    public StatusMessage consultativeTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, String appAudioURL) {
//------------------------------------- 
        Agent receiverAgent = agentManager.getAgentByAgentIdV2(username, receiverAgentId);

        // ----> Save in Report table.
        saveInReport(ucid, did, TransferType.CONSULTATIVE, receiverAgent);
        return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.transfer", new Object[]{TransferType.CONSULTATIVE.toReadableString(), Participant.AGENT.toString().toLowerCase(), receiverAgent.getAgentName(), receiverAgent.getAgentId()+":"+receiverAgent.getPhoneNumber()}, Locale.getDefault()));
    }

    @Override
    public StatusMessage blindTransfer(String username, String agentId, Long monitorUcid, Long ucid, String did, String receiverAgentId, String custNumber, String appAudioURL) {
//-------------------------------------         
        Agent receiverAgent = null;
        if (agentManager.lockIfAgentAvailable(username, receiverAgentId, custNumber)) {
            String cachedAgentUniqId = redisAgentManager.getString(username + ":agent:" + receiverAgentId);
            if (!StringUtils.isBlank(cachedAgentUniqId)) {
                receiverAgent = agentManager.get(Long.valueOf(cachedAgentUniqId));
            } else {
                receiverAgent = agentManager.getAgentByAgentIdV2(username, receiverAgentId);
            }
        }
        if (receiverAgent != null && (receiverAgent.getMode() == Agent.Mode.INBOUND || receiverAgent.getMode() == Agent.Mode.BLENDED)) {
            // ----> Save in Report table.
            saveInReport(ucid, did, TransferType.BLIND, receiverAgent);
            return new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.transfer", new Object[]{TransferType.BLIND.toReadableString(), Participant.AGENT.toString().toLowerCase(), receiverAgent.getAgentName(), receiverAgent.getAgentId()+":"+receiverAgent.getPhoneNumber()}, Locale.getDefault()));
        } else if (receiverAgent != null) {
            log.info("Unlocking participant agent:" + receiverAgent + " | Agent is not in inbound/Blended.");
            agentManager.releaseOnlyAgentFlag(receiverAgent.getId());
        }
//        return new StatusMessage(Status.ERROR, messageSource.getMessage(receiverAgent == null ? "error.conference.invalidagent" : "error.conference.agentnotidle", null, Locale.getDefault()));
            return new StatusMessage(Status.ERROR, messageSource.getMessage("fail.transfer", new Object[]{TransferType.BLIND.toReadableString(), receiverAgent == null ? "Invalid Agent" : "Agent is not IDLE"}, Locale.getDefault()));

    }

    private void saveInReport(Long ucid, String did, TransferType transferType, Agent receiverAgent) {
//-------------------------------------         
        Report report = reportManager.getReportByUcidAndDid(ucid, did);
        report.setTransferType((long) Participant.AGENT.ordinal());
        report.setTransferNow(true);
        report.setBlindTransfer(transferType.ordinal());
        report.setTransferAgentId(receiverAgent.getId());
        reportManager.save(report);
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setAgentTokenManager(AgentTokenManager agentTokenManager) {
        this.agentTokenManager = agentTokenManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setTelephonyManager(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    private ReportManager reportManager;
    private AgentManager agentManager;
    private AgentTokenManager agentTokenManager;
    private TelephonyManager telephonyManager;
    private MessageSource messageSource;
    private RedisAgentManager redisAgentManager;
    private static final Logger log = Logger.getLogger(AgentTransferManagerImpl.class
    );
}

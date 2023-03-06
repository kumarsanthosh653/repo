package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.ErrorReportService;
import com.ozonetel.occ.service.ReportManager;
import java.util.Date;
import java.util.Random;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author pavanj
 */
public class ErrorReportServiceImpl implements ErrorReportService {

    @Override
    public void saveDialErrorReport(String username, String agentId, Date startDate, Campaign campaign, String customerNumber, String dialStatus, String customerStatus, String agentStatus, String uui, String callData, String type, Long dataId) {
        Agent agent;

        if (StringUtils.isNotBlank(agentId)) {
            agent = agentManager.getAgentByAgentIdV2(username, agentId);
        } else {
            agent = null;
        }

        long monitorUcid = (System.currentTimeMillis() * 1000) + (100 + new Random().nextInt(900));
        Report report = new Report();
        report.setAgent(agent);
        report.setAgentId(agentId);
        report.setAgentStatus(agentStatus);
        if (agent != null) {
            report.setFwpNumber(agent.getFwpNumber());
        }
        report.setUcid(monitorUcid);
        report.setMonitorUcid(monitorUcid);
        report.setAudioFile("-1");
        report.setBlindTransfer(0);
        report.setCallCompleted(true);
        report.setCallDate(startDate);
        report.setCall_data(callData);
        report.setCampaignId(campaign.getCampaignId());
        report.setCustomerStatus(customerStatus);
        report.setDest(customerNumber);
        report.setDialStatus(dialStatus);
        report.setDid(campaign.getdId());
        report.setEndTime(new Date());
        report.setHangUpBy(Constants.SYSTEM_HANGUP);
        report.setOffline(campaign.isOffLineMode());
        report.setStatus("Fail");
        report.setTransferType(0L);
        report.setTransferNow(false);
        report.setType(type);
        report.setUui(uui);
        report.setUser(campaign.getUser());
        report.setData_id(dataId);
        reportManager.save(report);

    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    private AgentManager agentManager;
    private ReportManager reportManager;

}

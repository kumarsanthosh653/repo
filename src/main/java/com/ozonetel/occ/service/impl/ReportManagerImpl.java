package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.dao.ReportDao;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.service.RedisManager;
import com.ozonetel.occ.service.ReportManager;
import java.util.Date;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebService;

import net.sf.jasperreports.engine.JasperPrint;

@WebService(serviceName = "ReportService", endpointInterface = "com.ozonetel.occ.service.ReportManager")
public class ReportManagerImpl extends GenericManagerImpl<Report, Long> implements ReportManager {

    ReportDao reportDao;

    public ReportManagerImpl(ReportDao reportDao) {
        super(reportDao);
        this.reportDao = reportDao;
    }
    private RedisManager<Report> redisReportManager;

    public void setRedisReportManager(RedisManager<Report> redisReportManager) {
        this.redisReportManager = redisReportManager;
    }

    public List<Data> getDataByCampaignId(Long campaignId) {
        // TODO Auto-generated method stub
        return reportDao.getDataByCampaignId(campaignId);
    }

    public List<Campaign> getCampaignsByUserId(Long id) {
        // TODO Auto-generated method stub
        return reportDao.getCampaignsByUserId(id);
    }

    public List<Report> getReportByCampaignId(Long campaignId) {
        return reportDao.getReportByCampaignId(campaignId);
    }

    public boolean deleteReportByCampaignId(Long campaignId2) {
        // TODO Auto-generated method stub
        return reportDao.deleteReportByCampaignId(campaignId2);
    }

    public List<Report> getReportByCampaignTriedNumberStatus(Long campaignId,
            Integer currentTrail, String string, String string2) {
        // TODO Auto-generated method stub
        return reportDao.getReportByCampaignTriedNumberStatus(campaignId,
                currentTrail, string, string2);
    }

    public JasperPrint runReport(String reportFileName, HashMap paramMap) {
        return reportDao.runReport(reportFileName, paramMap);
    }

    public List<Event> getAgentStatesBySkill(Long skillId, Date forDate) {
        return reportDao.getAgentStatesBySkill(skillId, forDate);
    }

    public Report getReportByUCID(Long ucid) {
        return reportDao.getReportByUCID(ucid);
    }

    public List<Report> getReportsByRefID(Long refId) {
        return reportDao.getReportsByRefID(refId);
    }

    @Override
    public Long getReportIdByUcid(Long ucid) {
        return reportDao.getReportIdByUcid(ucid);
    }

    public Report getReportByUcidAndDid(Long ucid, String did) {
        return reportDao.getReportByUcidAndDid(ucid, did);
    }

    @Override
    public Long lpushToRedisList(String key, int noOfElementToKeep, Report report) {
        return redisReportManager.lpush(key, noOfElementToKeep, redisReportManager.toJson(report));
    }

    @Override
    public List<Report> getReportByMonitorUcid(Long monitorUcid) {
        return reportDao.getReportByMonitorUcid(monitorUcid);
    }

    @Override
    public Map<String, Object> getAgentPerformance(Long userId, Long agentUniqueId) {
//        Agent agentObj = agentDao.get(agentUniqueId);
        Map<String, Object> agentPerformance = new LinkedHashMap<>();
        List list = reportDao.getAgentPerformance(userId, agentUniqueId);
        if (list.size() > 0) {
            agentPerformance = (Map<String, Object>) list.get(0);
        }
        list = reportDao.getAgentSummary(agentUniqueId);
        if (list.size() > 0) {
            agentPerformance.putAll((Map<String, Object>) list.get(0));
        }

        List<Map<String, Object>> pauseList = reportDao.getPauseSummary(agentUniqueId, userId);
        Map<Object, Object> pauseSummary = new LinkedHashMap<>(pauseList.size());

        pauseList.forEach(
                tmpMap -> pauseSummary.put(tmpMap.get("Break"), tmpMap.get("TotalBreakTime"))
        );

        agentPerformance.put("pauseSummary", pauseSummary);
        return agentPerformance;
    }
 @Override
    public int updateReportWithTransferDetails(Long ucid, Long transferType, String designerName) {
        return reportDao.updateReportWithTransferDetails(ucid, transferType, designerName);
    }
}

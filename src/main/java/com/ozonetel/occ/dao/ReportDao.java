package com.ozonetel.occ.dao;

import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JasperPrint;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.Report;
import java.util.Date;
import java.util.Map;

/**
 * An interface that provides a data management interface to the Report table.
 */
public interface ReportDao extends GenericDao<Report, Long> {

    List<Data> getDataByCampaignId(Long campaignId);

    List<Campaign> getCampaignsByUserId(Long id);

    List<Report> getReportByCampaignId(Long campaignId);

    boolean deleteReportByCampaignId(Long campaignId2);

    List<Report> getReportByCampaignTriedNumberStatus(Long campaignId,
            Integer currentTrail, String string, String string2);

    JasperPrint runReport(String reportFileName, HashMap paramMap);

    List<Event> getAgentStatesBySkill(Long skillId, Date forDate);

    public Report getReportByUCID(Long ucid);

    public List<Report> getReportsByRefID(Long refId);

    public Long getReportIdByUcid(Long ucid);

    public Report getReportByUcidAndDid(Long ucid, String did);

    public List<Report> getReportByMonitorUcid(Long monitorUcid);

    public List getAgentPerformance(Long user, Long agent);

    public List<Map<String, Object>> getAgentSummary(Long agentId);

    public List<Map<String, Object>> getPauseSummary(Long agentId, Long userId);

    public int updateReportWithTransferDetails(Long ucid, Long transferType, String designerName);
}

package com.ozonetel.occ.service;

import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.Report;
import java.util.Date;
import java.util.Map;
import net.sf.jasperreports.engine.JasperPrint;

@WebService
public interface ReportManager extends GenericManager<Report, Long> {

    //List<Data> getDataByCampaignId(Long campaignId);
    List<Campaign> getCampaignsByUserId(Long id);

    List<Report> getReportByCampaignId(Long campaignId);

    boolean deleteReportByCampaignId(Long campaignId2);

    List<Report> getReportByCampaignTriedNumberStatus(Long campaignId, Integer currentTrail, String string, String string2);

    public JasperPrint runReport(final String reportFileName, HashMap paramMap);

    List<Event> getAgentStatesBySkill(Long skillId, Date forDate);

    public Report getReportByUCID(Long ucid);

    public Long getReportIdByUcid(Long ucid);

    public List<Report> getReportsByRefID(Long refId);

    public Long lpushToRedisList(final String key, int noOfElementToKeep, Report report);

    public Report getReportByUcidAndDid(Long ucid, String did);

    public List<Report> getReportByMonitorUcid(Long monitorUcid);

    public Map<String, Object> getAgentPerformance(Long userId, Long agentUniqueId);

    public int updateReportWithTransferDetails(Long ucid, Long transferType, String designerName);
}

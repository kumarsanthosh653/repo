package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.CampaignDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.dao.ReportDao;
import com.ozonetel.occ.model.Event;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;

import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;

public class ReportDaoHibernate extends GenericDaoHibernate<Report, Long> implements ReportDao {

    private CampaignDao campaignDao;

    public ReportDaoHibernate() {
        super(Report.class);
    }

    public void setCampaignDao(CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
    }

    public List<Data> getDataByCampaignId(Long campaignId) {
        List<Data> dataList = new ArrayList<Data>();
        dataList = getHibernateTemplate().find("from Report report where report.campaign.campaignId=?", campaignId);
        return dataList;
    }

    public List<Campaign> getCampaignsByUserId(Long id) {
        List<Campaign> campaigns = new ArrayList<Campaign>();
        campaigns = getHibernateTemplate().find("from Campaign cg where cg.user.id=?", id);
        return campaigns;
    }

    public List<Report> getReportByCampaignId(Long campaignId) {
        List<Report> dataList = new ArrayList<Report>();
        dataList = getHibernateTemplate().find("from Report report where report.campaign.campaignId=?", campaignId);
        return dataList;
    }

    public boolean deleteReportByCampaignId(Long campaignId2) {
        try {
            List<Report> dataList = getReportByCampaignId(campaignId2);
            for (Iterator iterator = dataList.iterator(); iterator.hasNext();) {
                Report report = (Report) iterator.next();
                getHibernateTemplate().delete(report);
            }
        } catch (Exception e) {
//			e.printStackTrace(); // commented
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    public List<Report> getReportByCampaignTriedNumberStatus(Long campaignId, Integer currentTrail, String string, String string2) {
        List<Report> dataList = new ArrayList<Report>();
        //dataList = getHibernateTemplate().find("from Report report where report.campaign.campaignId="+campaignId+" and report.triedNumber="+currentTrail+" and report.status='"+string+"' and report.call_data!='"+string2+"'");
        dataList = getHibernateTemplate().find("from Report report where report.status=?", string);
        return dataList;
    }

    public JasperPrint runReport(String reportFileName, HashMap paramMap) {
        try {

            String reportModulePath = "D:" + File.separatorChar + "Reports" + File.separatorChar;
            String filePath = reportModulePath + reportFileName;
            //	paramMap.put("logoPath", reportModulePath + "logo.png");
            System.out.println("filePath+reportFileName=" + filePath);
            log.debug("Trying to load Report D:\\Reports\\reportTemplate.jasper");
            JasperReport jr = JasperManager.loadReport(filePath);
            log.debug("Loading Report...!!!!");
            log.debug(jr.getQuery().getLanguage());
            if ((jr.getQuery().getLanguage()).equals("hql")) {
                paramMap.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION, getSession());
            } else {
                log.error("jr.getQuery().getLanguage()) is not HQL");
            }
            Connection conn = getSession().connection();
            log.debug("Connection : " + conn);
            JasperPrint jasperPrint = JasperFillManager.fillReport(filePath, paramMap, conn);
            log.debug("Jasper print : " + jasperPrint);
            return jasperPrint;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<Event> getAgentStatesBySkill(Long skillId, Date forDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt = "";
        dt = sdf.format(forDate);
        String sql = "select a.agent_id as agentId, a.agent_name as agentName, e.event_data as reason, e.start_time as startTime , e.end_time as endTime , "
                + " (case when e.event is null  then 'Not Available' else e.event end) as event from agent a "
                + " join skill_agents s on s.agent_id=a.id and  s.skill_id = " + skillId
                + " left outer join  agent_data e on a.agent_id =e.agentid and '" + dt + "' between e.start_time and e.end_time ";
        List l = getSession().createSQLQuery(sql).addScalar("agentId", Hibernate.STRING).addScalar("agentName", Hibernate.STRING).addScalar("reason", Hibernate.STRING).addScalar("event", Hibernate.STRING).addScalar("startTime", Hibernate.TIMESTAMP).addScalar("endTime", Hibernate.TIMESTAMP).list();
        return l;

    }

    public Report getReportByUCID(Long ucid) {
        List<Report> reports = getHibernateTemplate().find("from Report r where r.ucid=?", new Object[]{ucid});
        if (CollectionUtils.isNotEmpty(reports)) {
            return reports.get(0);
        }
        return null;
    }

    public Report getReportByUcidAndDid(Long ucid, String did) {
        Map<String, Object> params = new LinkedHashMap<>(2);
        params.put("ucid", ucid);
        params.put("did", did);
        List<Report> list = findByNamedQuery("getReportByUcid", params);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Report> getReportsByRefID(Long refId) {
        return getHibernateTemplate().find("from Report r where r.refId=?", new Object[]{refId});
    }

    @Override
    public Long getReportIdByUcid(Long ucid) {
        return DataAccessUtils.longResult(getHibernateTemplate().find("select r.report_id from Report r where r.ucid=?", ucid));
    }

    @Override
    public List<Report> getReportByMonitorUcid(Long monitorUcid) {
        return getHibernateTemplate().find("from Report r where r.monitorUcid=?", monitorUcid);
    }

    @Override
    public List getAgentPerformance(Long user, Long agent) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user_id", user);
        params.put("agent_id", agent);
        return executeProcedure("CALL Rep_AgentPerformace(?,?)", params);
    }

    @Override
    public List<Map<String, Object>> getAgentSummary(Long agentId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("agentId", agentId);
        return this.executeProcedure("call Get_AgentSummary(?)", params);
    }

    @Override
    public List<Map<String, Object>> getPauseSummary(Long agentId, Long userId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId", userId);
        params.put("agentId", agentId);

        return this.executeProcedure("CALL Dsh_AgentBreakDetailsV1(?,?)", params);
    }

    @Override
    public int updateReportWithTransferDetails(Long ucid, Long transferType, String designerName) {
        return getHibernateTemplate().bulkUpdate("update Report r set r.transferType=?, r.transferToNumber=? where r.ucid=?", transferType, designerName, ucid);
    }
}

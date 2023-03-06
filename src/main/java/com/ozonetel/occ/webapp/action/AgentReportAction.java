package com.ozonetel.occ.webapp.action;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.RoleManager;
import com.ozonetel.occ.util.DateUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 * @author Sudhakar
 */
public class AgentReportAction extends BaseAction {
	
    private String agentId;
    private String date;
    private String status;
    private EventManager eventManager;
    private AgentManager agentManager;
    private List reports = new ArrayList();
    
    
    private ReportManager reportManager;
    
    
	 public void setReportManager(ReportManager reportManager) {
		this.reportManager = reportManager;
	}

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public String getAgentId() {
        return agentId;
    }
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List getReports() {
        return reports;
    }

    public List<Agent> getAgents() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", getRequest().getRemoteUser());
        return agentManager.findByNamedQuery("agentsByUser", params);
    }
    
    public String search () {
        try {
            boolean isExec = true;
            Map<String,Object> params = new HashMap<String,Object>();
            StringBuilder queryString = new StringBuilder("select e from Event e where ");

            if (getDate() != null && !getDate().equals("")) {
                    queryString.append("e.startTime between :fromDate and :toDate and ");
                    params.put("fromDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss",getDate()+" 00:00:00"));
                    params.put("toDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss",getDate()+" 23:59:59"));
            }
            if (getAgentId() != null && !getAgentId().equals("")){
                queryString.append("e.agent.agentId = :agentId and ");
                params.put("agentId", getAgentId());
            }
            if (getStatus() != null && !getStatus().equals("")){
                queryString.append("e.event = :event ");
                params.put("event", getStatus());
            }

            if (queryString.toString().endsWith("where ")) {
                queryString.delete(queryString.toString().length()-6, queryString.toString().length());
                isExec = false;
            } else if (queryString.toString().endsWith("and ")) {
                queryString.delete(queryString.toString().length()-4, queryString.toString().length());
            }
            queryString.append("order by e.startTime desc");
            if (isExec) {
                reports = eventManager.findByNamedParams(queryString.toString(), params);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
//            e.printStackTrace();// commented
        }
            return SUCCESS;
    }
    
 public String agentPerformance(){
    	
    	HashMap map = new HashMap();
    	String reportName = "reportTemplate";
    	JasperPrint jp = reportManager.runReport(reportName + ".jasper", map);
    	/** ****************************** */
        byte[] reportAsBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String exportType = "application/pdf";

        JRExporter exporter = null;
      //  if ("application/pdf".equals(exportType)) {
            reportName = reportName + ".pdf";
            System.out.println("reportName=" + reportName);
            exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

      //  } 

        try {
            exporter.exportReport();
        } catch (JRException e) {
        	log.debug("File Name ->"+reportName);
            log.error(e.toString());
            addActionError(getText("errors.reportProblem"));
            
            e.printStackTrace();
        	
            return ERROR;
        }
        reportAsBytes = baos.toByteArray();
        getResponse().setContentType(exportType);
        getResponse().setHeader("Content-Disposition", "attachment; filename=" + reportName);
        getResponse().setContentLength(reportAsBytes.length);

        ServletOutputStream s;
		try {
			s = getResponse().getOutputStream();
			s.write(reportAsBytes, 0, reportAsBytes.length);
	        s.flush();
	        s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    	
    	log.debug("Agent Performance...!!");
    	
    	return SUCCESS;
    }
}
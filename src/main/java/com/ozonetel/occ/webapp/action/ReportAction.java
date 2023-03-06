package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.model.Location;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DialOutNumberManager;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.LocationManager;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.util.DateUtil;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;

public class ReportAction extends BaseAction implements Preparable {
    private ReportManager reportManager;
    private CampaignManager campaignManager;
    private DispositionManager dispositionManager;
    private LocationManager locationManager;
    private List<Report> reports;
    private Report report;
    private Long  report_id;
    private Date date;
    private Date toDate;
    private Long campaignId;
    private String status;
    private String callData;
    private String fromTime;
    private String toTime;
    private String callerId;
    private Long ucid;
    private Long locationId;
    private String agentId;
    private String disposition;

    private String totalCalls;

    private String skillName;

    public List<Disposition> dispositionList= new ArrayList<Disposition>();

    private AgentManager agentManager;

    private SkillManager skillManager;

    private DialOutNumberManager dialOutNumberManager;


    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setDispositionManager(DispositionManager dispositionManager) {
        this.dispositionManager = dispositionManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public AgentManager getAgentManager() {
		return agentManager;
	}

	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}
    public SkillManager getSkillManager() {
		return skillManager;
	}

	public void setSkillManager(SkillManager skillManager) {
		this.skillManager = skillManager;
	}

	public List getReports() {
        return reports;
    }

    public List<Location> getLocations() {
        return locationManager.getLocationsByUser(getRequest().getRemoteUser());
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Date getToDate() {
        return toDate;
    }
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Long getCampaignId() {
        return campaignId;
    }
    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getCallData() {
        return callData;
    }
    public void setCallData(String callData) {
        this.callData = callData;
    }

    public String getFromTime() {
        return fromTime;
    }
    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }
    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getAgentId() {
        return agentId;
    }
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCallerId() {
        return callerId;
    }
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public Long getUcid() {
        return ucid;
    }
    public void setUcid(Long ucid) {
        this.ucid = ucid;
    }

    public Long getLocationId() {
        return locationId;
    }
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getDisposition() {
        return disposition;
    }
    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public List<Campaign> getCampaignList() {
        List<Campaign> campaigns = new ArrayList<Campaign>();
//        campaigns.add(null);
        if (getRequest().isUserInRole(Constants.ADMIN_ROLE)) {
            campaigns.addAll(campaignManager.getAllDistinct());
        } else {
            campaigns.addAll(campaignManager.getCampaignsByUserId(userManager.getUserByUsername(getRequest().getRemoteUser()).getId()));
        }
        return campaigns;
    }

    public List<Agent> getAgentList() {
        return agentManager.getAgentsByUser(getRequest().getRemoteUser());
    }

    public List<Skill> getSkillList() {
        return skillManager.getSkillsByUser(getRequest().getRemoteUser());
    }

    public List<Disposition> getDispositionList() {
        return dispositionList;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String reportId = getRequest().getParameter("report.report_id");
            if (reportId != null && !reportId.equals("")) {
                report = reportManager.get(new Long(reportId));
            }
        }
    }

//    public String list() {
//        //reports = reportManager.getAll();
//    	reports = new ArrayList<Report>();
//        String userName = getRequest().getRemoteUser();
//        User user = userManager.getUserByUsername(userName);
//
//        if(null != user && !user.getFirstName().equalsIgnoreCase("admin")){
//        	List<Campaign> campaign = new ArrayList<Campaign>();
//        	campaign = reportManager.getCampaignsByUserId(user.getId());
//        	for (Iterator iterator = campaign.iterator(); iterator.hasNext();) {
//				Campaign campaign2 = (Campaign) iterator.next();
//				reports.addAll(reportManager.getReportByCampaignId(campaign2.getCampaignId()));
//			}
//        }else{
//        	reports = reportManager.getAll();
//        }
//        return SUCCESS;
//    }

    public void setReport_id(Long  report_id) {
        this. report_id =  report_id;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String delete() {
        reportManager.remove(report.getReport_id());
        saveMessage(getText("report.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (report_id != null) {
            report = reportManager.get(report_id);
        } else {
            report = new Report();
        }

        return SUCCESS;
    }

    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }

        if (delete != null) {
            return delete();
        }

        boolean isNew = (report.getReport_id() == null);

        reportManager.save(report);

        String key = (isNew) ? "report.added" : "report.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }

//    public String reportByChoice() throws Exception{
//        String date = getRequest().getParameter("date");
//        String campaignId = getRequest().getParameter("campaignName");
//        String status = getRequest().getParameter("status");
//        log.debug("date "+date);
//        log.debug("campaignId "+campaignId);
//        log.debug("status"+status);
//        Map<String,Object> params = new HashMap<String, Object>();
//        if ((date != null && !date.equals("")) &&
//                (campaignId != null && !campaignId.equals("")) &&
//                (status != null && !status.equals(""))) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(DateUtil.convertStringToDate(date));
//            cal.add(Calendar.DAY_OF_MONTH, 1);
//            params.put("fromDate",DateUtil.convertStringToDate(date));
//            params.put("toDate",cal.getTime());
//            params.put("campaignId",new Long(campaignId));
//            params.put("status",status);
//            reports = reportManager.findByNamedQuery("reportByChoice", params);
//        } else {
//            if (getRequest().isUserInRole(Constants.ADMIN_ROLE)) {
//            reports = reportManager.getAll();
//            } else {
//                params.put("userId", userManager.getUserByUsername(getRequest().getRemoteUser()).getId());
//                reports = reportManager.findByNamedQuery("reportByUser", params);
//            }
//        }
//        return "ajax";
//    }

    
    List<Report> agentReports = new ArrayList<Report>();
    
    
    public List<Report> getAgentReports() {
		return agentReports;
	}

	public void setAgentReports(List<Report> agentReports) {
		this.agentReports = agentReports;
	}

    public String list() throws Exception{
//        log.debug("date "+getDate());
//        log.debug("campaignId "+getCampaignId());
//        log.debug("status"+getStatus());
        Map<String,Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        /*if (getDate() != null && getCampaignId() != null &&
                (getStatus() != null && !getStatus().equals(""))) {
            cal.setTime(getDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            params.put("fromDate",getDate());
            params.put("toDate",cal.getTime());
            params.put("campaignId",getCampaignId());
            params.put("status",getStatus());
            reports = reportManager.findByNamedQuery("reportByChoice", params);
        } else if (getDate() != null && getCampaignId() != null) {
            cal.setTime(getDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            params.put("fromDate",getDate());
            params.put("toDate",cal.getTime());
            params.put("campaignId",getCampaignId());
            reports = reportManager.findByNamedQuery("reportByDateAndCampaign", params);
        } else if (getDate() != null && getStatus() != null && !getStatus().equals("")) {
            cal.setTime(getDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            params.put("fromDate",getDate());
            params.put("toDate",cal.getTime());
            params.put("status",getStatus());
            reports = reportManager.findByNamedQuery("reportByDateAndStatus", params);
        } else if (getCampaignId() != null && getStatus() != null && !getStatus().equals("")) {
            params.put("campaignId",getCampaignId());
            params.put("status",getStatus());
            reports = reportManager.findByNamedQuery("reportByCampaignAndStatus", params);
        } else if (getDate() != null) {
            cal.setTime(getDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            params.put("fromDate",getDate());
            params.put("toDate",cal.getTime());
            reports = reportManager.findByNamedQuery("reportByDate", params);
        } else if (getCampaignId() != null) {
            params.put("campaignId",getCampaignId());
            reports = reportManager.findByNamedQuery("reportByCampaign", params);
        } else if (getStatus() != null && !getStatus().equals("")) {
            params.put("status",getStatus());
            reports = reportManager.findByNamedQuery("reportByStatus", params);*/
//        } else {
//            if (getRequest().isUserInRole(Constants.ADMIN_ROLE)) {
//            reports = reportManager.getAll();
//            } else {
//                params.put("userId", userManager.getUserByUsername(getRequest().getRemoteUser()).getId());
//                reports = reportManager.findByNamedQuery("reportByUser", params);
//            }
//        }
        
      /*  for (Report report : reports) {
        	List<Agent> agents = agentManager.getAgentByAgentId(report.getAgentId());
        	if(agents.size() == 1){
        		report.setAgentId(agents.get(0).getAgentName());
        		agentReports.add(report);
        	}
			
		}*/
        if(null != reports)
        	reports.clear();
        boolean isExec = true;
        StringBuilder queryString = new StringBuilder("select distinct r from Report r where ");
        StringBuffer totalCallQuery = new StringBuffer("select DISTINCT(r.monitorUcid) from Report r where ");
        if ((getFromTime() != null && !getFromTime().equals(""))
                    && (getToTime() != null && !getToTime().equals(""))) {
                queryString.append("r.callDate between :fromDate and :toDate and ");
                totalCallQuery.append("r.callDate between :fromDate and :toDate and ");
                params.put("fromDate",DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromTime()));
                params.put("toDate",DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToTime()));
            }

        if (campaignId != null && !campaignId.equals("")) {
            queryString.append("r.campaign.campaignId =:campaignId and ");
            totalCallQuery.append("r.campaign.campaignId =:campaignId and ");
            log.debug("getCampaignId() : "+getCampaignId());
            params.put("campaignId", getCampaignId());
        }
        if (getCallData() != null && !getCallData().equals("")) {
            queryString.append("r.call_data = :callData and ");
            totalCallQuery.append("r.call_data = :callData and ");
            params.put("callData", getCallData());
        }
        log.debug("getSkillName()="+getSkillName());
        if (getSkillName() != null && !getSkillName().equals("")) {
            queryString.append("r.skillName= :skillName and ");
            totalCallQuery.append("r.skillName= :skillName and ");
            params.put("skillName", getSkillName());
        }
        if (getStatus() != null && !getStatus().equals("")) {
            
//            log.debug("status="+getStatus());
              if (getStatus().equalsIgnoreCase("fail")) {
                  queryString.append("(r.status!= 'Success' or r.status is null) and ");
            totalCallQuery.append("(r.status != 'Success' or r.status is null) and ");
              }else{
                  queryString.append("r.status =:status and ");
            totalCallQuery.append("r.status =:status and ");
             params.put("status", getStatus());
              }
//            if (getStatus().equalsIgnoreCase("fail")) {
//               // queryString.append("r.campaign.campaignId = c.campaignId and r.triedNumber = c.ruleNot ");
//            	 queryString.append("r.campaign.campaignId = c.campaignId ");
//            }
            
           
        }

        if (getCallerId() != null && !getCallerId().equals("")) {
            queryString.append("r.dest =:callerId and ");
            totalCallQuery.append("r.dest =:callerId and ");
            params.put("callerId", getCallerId());
        }

        if (getUcid() != null) {
            queryString.append("r.monitorUcid = :ucid and ");
            totalCallQuery.append("r.monitorUcid = :ucid and ");
            params.put("ucid", getUcid());
        }

        if (getLocationId() != null) {
            queryString.append("r.skill.location.id = :locationId and ");
            totalCallQuery.append("r.skill.location.id = :locationId and ");
            params.put("locationId", getLocationId());
        }

        if (queryString.toString().endsWith("where ")) {
            queryString.delete(queryString.toString().length() - 6, queryString.toString().length());
            totalCallQuery.delete(totalCallQuery.toString().length() - 6, totalCallQuery.toString().length());
            isExec = false;
        } else if (queryString.toString().endsWith("and ")) {
            queryString.delete(queryString.toString().length() - 4, queryString.toString().length());
            totalCallQuery.delete(totalCallQuery.toString().length() - 4, totalCallQuery.toString().length());
        }

        queryString.append("order by r.report_id desc");
        totalCallQuery.append("order by r.report_id desc");
        log.debug("queryString.toString() : "+queryString.toString());
        log.debug("totalCallQuery.toString() : "+totalCallQuery.toString());
        if (isExec) {
            reports = reportManager.findByNamedParams(queryString.toString(), params);
            totalCalls = ""+(reportManager.findByNamedParams(totalCallQuery.toString(), params).size());
        }
        if(null != reports)
        	log.debug("Reports Size : "+reports.size()+"totalCalls="+totalCalls);
        
        return SUCCESS;
    }

    public String refreshTime() {
        getRequest().setAttribute("dateTime",DateUtil.getDateTime("dd/MM/yyyy hh:mm:ss aaa", new Date()));
//        log.debug(dateTime);
        return "ajax";
    }

    public String downloadFile() {
        DataInputStream bis = null;
        ServletOutputStream out = null;
        try {
            String id = getRequest().getParameter("id");
            if (id != null && !id.equals("")) {
                report = reportManager.get(new Long(id));
                File file = new File(getRequest().getSession().getServletContext().getInitParameter("audio_path")+File.separator+report.getAudioFile());
//                File file = new File(getRequest().getSession().getServletContext().getRealPath("sounds")+Constants.FILE_SEP+report.getAudioFile());
                log.debug("File path == "+file.getAbsolutePath());
                if (file.exists()) {
                    getResponse().setContentLength((int)file.length());
                    getResponse().setContentType("application/octet-stream; UTF-8");
                    getResponse().setHeader("Content-Disposition", "attachment; filename="+file.getName());
                    out = getResponse().getOutputStream();
                    bis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                    if (bis.available() > 0) {
                        int bytesRead = 0;
                        byte[] bytes = new byte[8124];
                        while ((bytesRead = (bis.read(bytes, 0, bytes.length))) != -1) {
                            out.write(bytes, 0, bytesRead);
                        }
                    }
                    bis.close();
                    out.close();
                    return null;
                } else {
                    saveErrors(getText("report.error.file"));
                }
            } else {
                saveErrors(getText("report.error.download"));
            }
        } catch (FileNotFoundException e) {
            saveErrors(getText("report.error.file"));
            log.error(e.getMessage());
        } catch (Exception e) {
            saveErrors(getText("report.error.download"));
            log.error(e.getMessage());
        } finally {
            try {
                if (bis != null) bis.close();
                if (out != null) out.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return SUCCESS;
    }


    public String getAgentStatesBySkill(){
        if (log.isDebugEnabled()) {
            log.debug("Entering 'AgentStates By skill' method");
            
        }
        String skillId = getRequest().getParameter("skillId");
        String forDate = getRequest().getParameter("forDate");
        Date dt = null;
        
        if(!forDate.isEmpty() && forDate != null){
           try{
               String[] s = forDate.split("\\.");
            dt = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss",forDate);
           }catch(ParseException p){
           log.debug("Exception in forDate Conversion="+forDate);
           }
        }
        List<Event> agentStateList = reportManager.getAgentStatesBySkill(new Long(skillId), dt);
        List l = new ArrayList();
        Iterator iter = agentStateList.iterator();
        while (iter.hasNext())
            {
                 Object[] obj = (Object[]) iter.next();
                 Map mp = new HashMap();
                 mp.put("agentId", obj[0]);
                 mp.put("agentName", obj[1]);
                 mp.put("reason", obj[2]);
                 mp.put("event", obj[3]);
                 mp.put("startTime", obj[4]);
                 mp.put("endTime", obj[5]);
                 l.add(mp);
            }
        
        log.debug("reports Size="+agentStateList.size());
        getRequest().setAttribute("agentStateList", l);
        
        return SUCCESS;
    }
    public String crReport() {
        if (log.isDebugEnabled()) {
            log.debug("Entering 'CallRecordingsReport' method");
        }
        try {
            boolean isExec = true;

            User u = userManager.getUserByUsername(getRequest().getRemoteUser());
            StringBuffer queryString = new StringBuffer("select r from Report r where r.status = 'Success' and r.campaign.user.id="+u.getId()+" and ");
//            StringBuffer totalCallQuery = new StringBuffer("select DISTINCT(r.monitorUcid) from Report r where ");
            Map<String, Object> params = new HashMap<String, Object>();
            if ((getFromTime() != null && !getFromTime().equals(""))
                    && (getToTime() != null && !getToTime().equals(""))) {
                queryString.append("r.callDate between :fromDate and :toDate and ");
//                totalCallQuery.append("r.callDate between :fromDate and :toDate and ");
                params.put("fromDate",DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromTime()));
                params.put("toDate",DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToTime()));
            }
            if (getCallerId() != null && !getCallerId().equals("")) {
                queryString.append("r.dest =:callerId and ");
//                totalCallQuery.append("r.dest =:callerId and ");
                params.put("callerId", getCallerId());
            }
            if (getCampaignId() != null) {
                queryString.append("r.campaign.campaignId =:campaignId and ");
//                totalCallQuery.append("r.campaign.campaignId =:campaignId and ");
                params.put("campaignId", getCampaignId());
            }
            if (getAgentId() != null && !getAgentId().equals("")) {
                queryString.append("r.agentId =:agentId and ");
//                totalCallQuery.append("r.agentId =:agentId and ");
                params.put("agentId", getAgentId());
            }
            if (getDisposition() != null && !getDisposition().equals("")) {
                queryString.append("r.disposition =:disposition and ");
//                totalCallQuery.append("r.disposition =:disposition and ");
                params.put("disposition", getDisposition());
            }

            if (queryString.toString().endsWith("where ")) {
                queryString.delete(queryString.toString().length() - 6, queryString.toString().length());
//                totalCallQuery.delete(totalCallQuery.toString().length() - 6, totalCallQuery.toString().length());
                isExec = false;
            } else if (queryString.toString().endsWith("and ")) {
                queryString.delete(queryString.toString().length() - 4, queryString.toString().length());
//                totalCallQuery.delete(totalCallQuery.toString().length() - 4, totalCallQuery.toString().length());
            }

            queryString.append("order by r.report_id desc");
            log.debug(queryString.toString());
//            log.debug(totalCallQuery.toString());
            if (isExec) {
                reports = reportManager.findByNamedParams(queryString.toString(), params);
//                totalCalls = ""+(reportManager.findByNamedParams(queryString.toString(), params).size());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e.fillInStackTrace());
        }

        return SUCCESS;
    }

    public String updateDispositions() {
        String campaignId2 = getRequest().getParameter("id");
        if (campaignId2 != null && !campaignId2.equals("")) {
            dispositionList = dispositionManager.getDispositionsByCampaign(new Long(campaignId2));
        }
        return "ajax";
    }

    /**
     * @return the skillName
     */
    public String getSkillName() {
        return skillName;
    }

    /**
     * @param skillName the skillName to set
     */
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    /**
     * @return the totalCalls
     */
    public String getTotalCalls() {
        return totalCalls;
    }

    /**
     * @param totalCalls the totalCalls to set
     */
    public void setTotalCalls(String totalCalls) {
        this.totalCalls = totalCalls;
    }

    /**
     * @return the dialOutNumberManager
     */
    public DialOutNumberManager getDialOutNumberManager() {
        return dialOutNumberManager;
    }

    /**
     * @param dialOutNumberManager the dialOutNumberManager to set
     */
    public void setDialOutNumberManager(DialOutNumberManager dialOutNumberManager) {
        this.dialOutNumberManager = dialOutNumberManager;
    }

    public boolean isGreater() {
        if ((getFromTime() != null && !getFromTime().equals(""))
                && (getToTime() != null && !getToTime().equals(""))) {
            try {
                return DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromTime()).before(DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToTime()));
            } catch (ParseException e) {
                log.error(e.getMessage(),e.fillInStackTrace());
                return false;
            } catch (Exception e) {
                log.error(e.getMessage(),e.fillInStackTrace());
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void validate() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            getFieldErrors().clear();
            if ((getFromTime() != null && !getFromTime().equals(""))
                    || (getToTime() != null && !getToTime().equals(""))) {
                if (getFromTime() == null || getFromTime().equals("")) {
                    super.addActionError(getText("errors.requiredField", new String[] {getText("report.fromTime")}));
                } else if (!getFromTime().matches("^(1[0-2]|0[1-9])/(3[0-1]|[1-2][0-9]|0[1-9])/((19[0-9]{2})|[2-9][0-9]{3}) (2[0-3]|[0-1][0-9])(:([0-5][0-9])){2}$")) {
                    super.addActionError(getText("errors.dateTime", new String[] {getText("report.fromTime")}));
                }
                if (getToTime() == null || getToTime().equals("")) {
                    super.addActionError(getText("errors.requiredField", new String[] {getText("report.toTime")}));
                } else if (!getToTime().matches("^(1[0-2]|0[1-9])/(3[0-1]|[1-2][0-9]|0[1-9])/((19[0-9]{2})|[2-9][0-9]{3}) (2[0-3]|[0-1][0-9])(:([0-5][0-9])){2}$")) {
                    super.addActionError(getText("errors.dateTime", new String[] {getText("report.toTime")}));
                } else if (!isGreater()) {
                    super.addActionError(getText("errors.twoDates"));
                }
            }
        }
    }
}
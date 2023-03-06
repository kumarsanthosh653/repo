package com.ozonetel.occ.webapp.action;

import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.util.DateUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sudhakar
 */
public class SystemReportAction extends BaseAction {

    private CampaignManager campaignManager;
    private String fromDate;
    private String toDate;
    private List report = new ArrayList();

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public String getFromDate() {
        return fromDate;
    }
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public List getReport() {
        return report;
    }
    public void setReport(List report) {
        this.report = report;
    }

    public String systemReport() {
        log.debug("Entering 'systemReport' method");
        try {
            if ((getFromDate() != null && !getFromDate().equals(""))
                    && (getToDate() != null && !getToDate().equals(""))) {
                Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
                User u = userManager.getUserByUsername(getRequest().getRemoteUser());
                queryParams.put("userId", u.getId());
                queryParams.put("fromDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromDate()));
                queryParams.put("toDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToDate()));
                report = campaignManager.executeProcedure("{call Rep_DailerBy_Date(?,?,?)}", queryParams);
            }
        } catch (ParseException e) {
            log.error(e.getMessage(),e.fillInStackTrace());
        } catch (Exception e) {
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return SUCCESS;
    }

    public boolean isGreater() {
        if ((getFromDate() != null && !getFromDate().equals(""))
                && (getToDate() != null && !getToDate().equals(""))) {
            try {
                return DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromDate()).before(DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToDate()));
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

     public boolean isGreaterThanToday() {
        if ((getFromDate() != null && !getFromDate().equals(""))
                && (getToDate() != null && !getToDate().equals(""))) {
            try {
                Date fD = DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromDate());
                Date tD = DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToDate());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, -10);
                if(!fD.before(cal.getTime())){
                    log.debug("From date is After Todays Date -10");
                }
                if(!tD.before(cal.getTime())){
                    log.debug("To date is After Todays Date -10");
                }

               return (fD.before(cal.getTime()) && tD.before(cal.getTime()));
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
            if (getFromDate() == null || getFromDate().equals("")) {
                super.addFieldError("fromDate", getText("errors.requiredField", new String[] {getText("report.fromTime")}));
            } else if (!getFromDate().matches("^(1[0-2]|0[1-9])/(3[0-1]|[1-2][0-9]|0[1-9])/((19[0-9]{2})|[2-9][0-9]{3}) (2[0-3]|[0-1][0-9])(:([0-5][0-9])){2}$")) {
                super.addFieldError("fromDate", getText("errors.invalidDate", new String[] {getText("report.fromTime")}));
            }
            if (getToDate() == null || getToDate().equals("")) {
                super.addFieldError("toDate", getText("errors.requiredField", new String[] {getText("report.toTime")}));
            } else if (!getToDate().matches("^(1[0-2]|0[1-9])/(3[0-1]|[1-2][0-9]|0[1-9])/((19[0-9]{2})|[2-9][0-9]{3}) (2[0-3]|[0-1][0-9])(:([0-5][0-9])){2}$")) {
                super.addFieldError("toDate", getText("errors.invalidDate", new String[] {getText("report.toTime")}));
            } else if (!isGreater()) {
                super.addFieldError("toDate", getText("errors.twoDates"));
            }else if (!isGreaterThanToday()) {
                    super.addFieldError("toDate", getText("errors.greaterThanToday"));
                }
        }
    }
}
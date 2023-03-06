package com.ozonetel.occ.webapp.action;

import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.util.DateUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sudhakar
 */
public class CampaignReportAction extends BaseAction {

    private CampaignManager campaignManager;
    private Long campaignId;
    private String fromDate;
    private String toDate;
    private List report = new ArrayList();

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public Long getCampaignId() {
        return campaignId;
    }
    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
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

    public List<Campaign> getCampaigns() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", getRequest().getRemoteUser());
        return campaignManager.findByNamedQuery("getCampaignByUser", params);
    }

    public String dayWiseReport() {
        log.debug("Entering 'dayWiseReport' method");
        try {
            if ((getFromDate() != null && !getFromDate().equals(""))
                    && (getToDate() != null && !getToDate().equals(""))) {
                Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
                queryParams.put("fromDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromDate()));
                queryParams.put("toDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToDate()));
                report = campaignManager.executeProcedure("{call Rep_CampaignFor_Day(?,?)}", queryParams);
            }
        } catch (ParseException e) {
            log.error(e.getMessage(),e.fillInStackTrace());
        } catch (Exception e) {
            log.error(e.getMessage(),e.fillInStackTrace());
        }
        return SUCCESS;
    }

    public String dateWiseReport() {
        log.debug("Entering 'dateWiseReport' method");
        try {
            if (getCampaignId() != null
                    && (getFromDate() != null && !getFromDate().equals(""))
                    && (getToDate() != null && !getToDate().equals(""))) {
                Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
                queryParams.put("campaignId", getCampaignId());
                queryParams.put("fromDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getFromDate()));
                queryParams.put("toDate", DateUtil.convertStringToDate("MM/dd/yyyy HH:mm:ss", getToDate()));
                report = campaignManager.executeProcedure("{call Rep_CampaignBy_Date(?,?,?)}", queryParams);
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

    @Override
    public void validate() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            getFieldErrors().clear();
            if (getRequest().getParameter("method:dateWiseReport") != null && !getRequest().getParameter("method:dateWiseReport").equals("")) {
                if (getCampaignId() == null) {
                    super.addFieldError("campaignId", getText("errors.requiredField", new String[] {getText("report.campaignId")}));
                }
            }
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
            }
        }
    }
}
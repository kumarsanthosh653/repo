package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Campaign;
import java.util.Date;

/**
 *
 * @author pavanj
 */
public interface ErrorReportService {
    public void saveDialErrorReport(String username, String agentId, Date startDate, Campaign campaign,
            String customerNumber, String dialStatus, String customerStatus, String agentStatus, String uui,
            String callData,String type,Long dataId);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.webapp.servlet;

import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Report;
import com.ozonetel.occ.service.ReportManager;
import com.ozonetel.occ.service.impl.UpdateReport;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.util.DateUtil;
import com.ozonetel.occ.webapp.util.RequestUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author root
 */
    public class OutBoundCallBack extends HttpServlet {

    private Log log = LogFactory.getLog(OutBoundCallBack.class);
    private ReportManager reportManager;

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        reportManager = (ReportManager) webApplicationContext.getBean("reportManager");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = null;

        try {
            response.setContentType("text/html;charset=UTF-8");
            out = response.getWriter();
            log.debug("Request parameters :" + RequestUtil.getRequestParams(request));

            String ucid = request.getParameter("sid");
            String did = request.getParameter("caller_id");//DID
            String start_time = request.getParameter("start_time");//
            String status = request.getParameter("status");
            String agentId = request.getParameter("cAgentId");// AgentId
            String callerId = request.getParameter("cDialNumber");// Customer Phone Number
            String end_time = request.getParameter("end_time");// end time is picktime + duration
            String ivr_flow_details = request.getParameter("ivr_flow_details");

            //Call is answered but IVR is not hit.
            if (StringUtils.equalsIgnoreCase(status, Constants.DIAL_ANSWERED) && ivr_flow_details != null) {//null check is to make sure that param exists.
                try {
                    JSONArray jsonArray = new JSONArray(ivr_flow_details);
                    if (jsonArray.length() == 1 && StringUtils.equalsIgnoreCase(jsonArray.getJSONObject(0).getString("Name"), "CALL_START")) {
                        status = "not_answered";
                        log.debug("Making status as "+status+" for ucid:"+ucid+" -> Actual status:{}"+ request.getParameter("status"));
                    }
                } catch (JSONException e) {
                    log.error(e.getMessage(), e);
                }
            }
            
            Date startTime = null, endTime = null;
            try {
                startTime = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", start_time);
                endTime = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", end_time);

            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }

            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("ucid", new Long(ucid));
            reportParams.put("did", did);
            List<Report> reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);

            if (reports.size() > 0) { // this is temporary fix for proptiger need to revisit
            } else {
                try {
                    Thread.sleep(2000);
                } catch (Exception ignore) {

                }
                log.debug(ucid + "Waited 2 secs and trying again..");
                reports = reportManager.findByNamedQuery("getReportByUcid", reportParams);
            }

            if (reports.size() > 0) {
                Report r = reports.get(0);
                log.debug("[" + ucid + "]Done By CallBackURL=" + r.isCallCompleted());
                System.out.println("[" + new Date() + "][" + ucid + "]Done By CallBackURL =" + r.isCallCompleted());
                if (!status.equalsIgnoreCase("answered") && !r.isCallCompleted()) { // if the call is not dropped by the kookooapi then we need to drop explictly
                    log.debug("Updating report with params : "+ucid+" | "+did+" | "+agentId+" | "+callerId+" | "+status+" | "+r.getCampaignId() == null ? null : r.getCampaignId().toString()+" | " + startTime);
                    UpdateReport ur = new UpdateReport();
                    ur.updateCallStatus(ucid, ucid, did, agentId, callerId, "Fail", "", "0", "AgentHangup", "Manual Dial", "true", "AgentDial", "ToolBarManual", 0, status, true, "", "NotDialed", status,
                            r.getCampaignId() == null ? null : r.getCampaignId().toString(), new Long(0), new Long(0), startTime,
                            endTime == null ? new Date() : endTime, null, null, r.getE164());
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

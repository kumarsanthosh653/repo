/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.webapp.servlet;

import com.google.gson.Gson;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallQueueManager;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.webapp.util.RequestUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author ozone
 */
public class LdbHandler extends HttpServlet {

    private static Logger log = Logger.getLogger(LdbHandler.class);
    private AgentManager agentManager;
    private FwpNumberManager fwpNumberManager;
    private CallQueueManager callQueueManager;

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
        fwpNumberManager = (FwpNumberManager) webApplicationContext.getBean("fwpNumberManager");
        callQueueManager = (CallQueueManager) webApplicationContext.getBean("callQueueManager");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
//        log.debug("Action : "+action);
        String resp = "";
        try {
            switch (action) {
                case "syncFwp":
                    resp = fwpNumberManager.syncFwpToLdb(new Long(request.getParameter("fwpId")), new Long(request.getParameter("userId")));
                    break;
                case "syncMultiFwp":
                    resp = fwpNumberManager.syncFwpsToLdb(request.getParameter("fwpIds"), new Long(request.getParameter("userId")));
                    break;
                case "syncFwpsForUser":
                    resp = fwpNumberManager.syncFwpsForUser(new Long(request.getParameter("userId")));
                    break;
                case "isAgentLoginByPhoneNumber":
                    resp = agentManager.isAgentLoginByPhoneNumber(new Long(request.getParameter("FwpId")), new Long(request.getParameter("userId"))).toOneString();
                    break;
                case "delFwpNo": {
                    resp = fwpNumberManager.delPhoneNumber(new Long(request.getParameter("FwpId")), new Long(request.getParameter("userId"))).toOneString();
                    break;
                }
                case "delAgent": {
                    resp = this.agentManager.delAgent(new Long(request.getParameter("agentId"))).toOneString();
                    break;
                }
                case "isAgentLoggedin":
                    resp = agentManager.isAgentLoggedin(new Long(request.getParameter("agentId")));
                    break;
                case "isValidAgent":
                    resp = StringUtils.trim(agentManager.isValidAgent(new Long(request.getParameter("agentId")), new Long(request.getParameter("fwpId")), new Long(request.getParameter("userId"))).toOneString());
                    break;
                case "releasePhoneNumberFromSystemMonitor":
                    resp = fwpNumberManager.releasePhoneNumberFromSystemMonitor(new Long(request.getParameter("fwpId")));
                    break;
                case "getCallQueueForUser":
                    resp = new Gson().toJson(callQueueManager.getCallQueuesByUser(new Long(request.getParameter("userId"))));
                    break;
                case "getCallQueueForSubUser":
                    resp = new Gson().toJson(callQueueManager.getCallQueuesBySubUser(new Long(request.getParameter("userId")), request.getParameter("campaigns")));
                    break;
                case "getCallQueueForUserAndDate":
                    resp = new Gson().toJson(callQueueManager.getCallQueuesByUserAndDate(new Long(request.getParameter("userId")), request.getParameter("fromDate"), request.getParameter("toDate")));
                    break;
                case "getCallQueueForSubUserAndDate":
                    resp = new Gson().toJson(callQueueManager.getCallQueuesBySubUserAndDate(new Long(request.getParameter("userId")), request.getParameter("campaigns"), request.getParameter("fromDate"), request.getParameter("toDate")));
                    break;
                case "getCallQueueCountSkillWise":
                    resp = new Gson().toJson(callQueueManager.getCallQueueCountSkillWise(new Long(request.getParameter("userId"))));
                    break;
                case "getAgentCallQueueList":
                    resp = new Gson().toJson(callQueueManager.getAgentCallQueueList(new Long(request.getParameter("userId"))));
                    break;
                case "getAgentCallQueueListByDate":
                    resp = new Gson().toJson(callQueueManager.getAgentCallQueueListByDate(new Long(request.getParameter("userId")), request.getParameter("fromDate"), request.getParameter("toDate")));
                    break;
                case "getFwpNumberStates":
                    resp = new Gson().toJson(fwpNumberManager.getFwpNumberStatesforMonitor(new Long(request.getParameter("userId"))));
                    break;
                case "getLoggedInAgentsForUser":
                    resp = new Gson().toJson(agentManager.getLoggedInAgentsForUser(new Long(request.getParameter("userId")), new Long(request.getParameter("subUserId"))));
                    break;
                case "getLoggedInAgentsForChat":
                    resp = new Gson().toJson(agentManager.getLoggedInAgentsForChat(new Long(request.getParameter("userId"))));
                    break;
                case "getLoggedInAgentsForUserAndCampaign":
                    resp = new Gson().toJson(agentManager.getLoggedInAgentsForUserAndCampaign(new Long(request.getParameter("userId")), new Long(request.getParameter("subUserId")), new Long(request.getParameter("campaigns"))));
                    break;
                case "getInboundAgentStatusSkillWise":
                    /* Map<String, Object> params = new LinkedHashMap<>();
                    int userId = new Integer(request.getParameter("userId"));
                    String skillId = request.getParameter("skillId");
                    params.put("pUserID", userId);
                    params.put("pSkillID", skillId);
                    log.debug("params for getInboundAgentStatusSkillWise :: "+params);
                    resp = new Gson().toJson(agentManager.executeProcedure("call Get_InboundAgentStatusSkillWise(?,?)", params));
                     */ resp = new Gson().toJson(agentManager.getInboundAgentStatusSkillWise(new Long(request.getParameter("userId")), request.getParameter("skillId")));
                    break;
                default:
                    log.error("Invalid action:" + action);
                    break;
            }

//            log.info("Request params:" + RequestUtil.getRequestParams(request) + " -> " + resp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        PrintWriter out = response.getWriter();
//        log.info("Request params:" + RequestUtil.getRequestParams(request) + " -> " + resp);
        try {
            out.println(resp);
            out.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            out.close();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}

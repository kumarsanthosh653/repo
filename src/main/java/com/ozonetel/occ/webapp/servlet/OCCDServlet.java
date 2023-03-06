package com.ozonetel.occ.webapp.servlet;

/**
 * OCCDServlet.java rajesh Chary Date : Aug 9, 2010 Email : rajesh@ozonetel.com,
 * nb.nalluri@yahoo.com
 */
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.PredictiveCallMonitor;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.Command;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.EventManager;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ozonetel.occ.service.OCCManager;
import com.ozonetel.occ.service.RedisAgentManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.impl.BusyAgent;
import com.ozonetel.occ.service.impl.FreeAgent;
import com.ozonetel.occ.service.impl.PredictiveServiceImpl;
import com.ozonetel.occ.service.impl.ReleaseAgent;
import com.ozonetel.occ.service.impl.UpdateReport;
import com.ozonetel.occ.util.AppContext;
import com.ozonetel.occ.webapp.util.RequestUtil;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.server.TokenServer;
import org.springframework.context.ApplicationContext;

/**
 * Servlet implementation class AgentManager
 */
public class OCCDServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    String customerId = null;
    private static Map gMap = new HashMap();
    private OCCManager occManager;
    private static Logger log = Logger.getLogger(OCCDServlet.class);
    private AgentManager agentManager;
    private EventManager eventManager;
    private PredictiveServiceImpl predictiveService;
    private RedisAgentManager redisAgentManager;
    private UserManager userManager;
    private DispositionManager dispositionManager;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public OCCDServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.debug("OCCDServlet init");
        ServletContext context = getServletContext();
        customerId = context.getInitParameter("customerId");
//        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        occManager = (OCCManager) webApplicationContext.getBean("occManager");
        agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
        eventManager = (EventManager) webApplicationContext.getBean("eventManager");
        predictiveService = (PredictiveServiceImpl) webApplicationContext.getBean("predictiveServiceImpl");
        redisAgentManager = (RedisAgentManager) AppContext.getApplicationContext().getBean("redisAgentManager");
        userManager = (UserManager) webApplicationContext.getBean("userManager");
        dispositionManager = (DispositionManager) webApplicationContext.getBean("dispositionManager");
        occManager.initialize();

    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @SuppressWarnings("unchecked")
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {

        String event = request.getParameter("action");
//        log.info("Event::::->"+event);
        try {
            if ("formLogin".equals(event)) {
                String customer = (String) request.getParameter("customer");
                String agentId = (String) request.getParameter("agentid");
                String phoneNumber = (String) request.getParameter("phoneNumber");
                String pin = (String) request.getParameter("pin");
                String nextPage = request.getContextPath() + "/cloudagent/agent_login.jsp";
                if (occManager.isValidAgent(customer, agentId, phoneNumber, pin)) {
                    log.debug("Agent is a valid agent");

                    nextPage = request.getContextPath() + "/cloudagent/agent_toolbar.jsp";
                    request.getSession().setAttribute("loggedIn", "true");
                    request.getSession().setAttribute("customer", customer);
                    request.getSession().setAttribute("phoneNumber", phoneNumber);
                    request.getSession().setAttribute("agentUserName", agentId);
                    User user = occManager.getUserManager().getUserByUsername(customer);
                    String ip = user.getAgentLoginIP();
                    String webSite = user.getWebsite();
                    log.info("Agent login ip for customer :" + customer + " is -> " + ip);
                    log.info("UserWebSite :" + customer + " is -> " + ip);
                    request.getSession().setAttribute("agentLoginIP", ip);
                    request.getSession().setAttribute("custWebsite", webSite);

                } else {
                    log.debug("Agent is Invalid or else it may loggedin Already");
                    request.setAttribute("Error", "Error: Please check the login details and try again!!!");
                    request.getSession().setAttribute("loggedIn", "false");
                    nextPage = request.getContextPath() + "/cloudagent/agent_login.jsp?Error=true";
                }
                try {
                    response.sendRedirect(nextPage);
                } catch (IOException ex) {
                    log.error(ex.getMessage(), ex);
                }
            } else {
                log.info("Got request with action:" + event + " with reqest params " + RequestUtil.getRequestParams(request));
                String resp = "";
                if ("nextAgent".equalsIgnoreCase(event)) {
                    log.info("$$$$$$Servlet called FreeAgent command.." + request.getParameter("campaignId") + " : " + request.getParameter("ucid"));
                    try {
                        PredictiveCallMonitor savedMonitor = predictiveService.getPredictiveCallMonitor(new Long(request.getParameter("campaignId")), request.getParameter("agentMonitorUcid"));
                        log.debug("Got predictive call : " + savedMonitor);
                        if (savedMonitor != null) {
                            savedMonitor.setAgentStatus(Constants.DIALING);
                            savedMonitor.setDateModified(new Date());
                            predictiveService.savePredictiveCallMonitorToRedis(new Long(request.getParameter("campaignId")), request.getParameter("agentMonitorUcid"), savedMonitor);
                        }
                    } catch (Exception e) {
                        log.error(e);
                    }
                    Command c = new FreeAgent(request);
                    resp = c.execute();

                    log.info("NextAgent Resposnse=[" + resp + "]");
                } else if ("busyAgent".equalsIgnoreCase(event)) {
                    Command c = new BusyAgent(request, new Date());
                    resp = c.execute();
                } else if ("releaseAgent".equalsIgnoreCase(event)) {
//                    log.info("Release Agent request:" + RequestUtil.getRequestParams(request));
                    Command c = new ReleaseAgent(request, new Date());
                    resp = c.execute();
//                    log.info("Release agent response for params:" + RequestUtil.getRequestParams(request) + " -> " + resp);

                } else if ("releaseAllAgents".equalsIgnoreCase(event)) { // releasing agents.....
                    //			String agentId=(String)request.getParameter("agentId");
                    //			String number=(String)request.getParameter("number");
                    resp = occManager.releaseAllAgents();
                    //occManager.informDialler();
                } else if ("isAgent".equalsIgnoreCase(event)) {
                    String agentId = (String) request.getParameter("agentId");
                    String did = (String) request.getParameter("did");
                    resp = occManager.isAgent(did, agentId);
                } else if ("updateCallStatus".equalsIgnoreCase(event)) {
                        Command c = new UpdateReport(request);
                        resp = c.execute();
                } else if ("resetAgentFlag".equalsIgnoreCase(event)) {
                    String agentMonitorUcid = (String) request.getParameter("agentMonitorUcid");
                    occManager.resetAgentFlag(agentMonitorUcid);
                } else if ("checkAgent".equalsIgnoreCase(event)) {
                    String did = (String) request.getParameter("did");
                    String callerId = (String) request.getParameter("callerId");
                    String ucid = (String) request.getParameter("ucid");
                    String agentMonitorUcid = (String) request.getParameter("agentMonitorUcid");
                    resp = occManager.checkAgent(did, callerId, ucid, agentMonitorUcid, request.getParameterMap());
                } else if ("transferCheck".equalsIgnoreCase(event)) {
                    String did = (String) request.getParameter("did");
                    String ucid = (String) request.getParameter("ucid");
                    resp = occManager.transferCheck(ucid, did);
                } else if ("transferCheck".equalsIgnoreCase(event)) {
                    String ucid = (String) request.getParameter("ucid");
                    String did = (String) request.getParameter("did");
                    String username = (String) request.getParameter("username");
                    String transferType = (String) request.getParameter("transferType");
                    String transferId = (String) request.getParameter("transferId");
                    String transferNumber = (String) request.getParameter("transferNumber");
                    int blindTransfer = 1;
                    if (ucid != null && !ucid.equals("")) {
                        resp = occManager.setTransfer(ucid, did, username, transferType, transferId, transferNumber, blindTransfer);
                    } else {
                        resp = "Transfer failed";
                    }

                    resp = occManager.transferCheck(ucid, did);

                } else if ("transferFailed".equalsIgnoreCase(event)) {
                    String did = (String) request.getParameter("did");
                    String agentId = (String) request.getParameter("agentId");
                    resp = occManager.transferFailed(did, agentId);

                } else if ("releaseAgentByAdmin".equalsIgnoreCase(event)) {
                    resp = occManager.releaseAgentByAdmin(request.getParameter("username"), request.getParameter("agentUniqueId"), request.getParameter("who"));
                } else if ("logoffAgentByAdmin".equalsIgnoreCase(event)) {
                    resp = occManager.logoffAgentByAdmin(request.getParameter("username"), new Long(request.getParameter("agentUniqueId")), request.getParameter("who"));
                } else if ("bargeAgentScreen".equalsIgnoreCase(event)) {
                    resp = agentManager.sendScreenBargeRequest(request.getParameter("username"), Long.valueOf(request.getParameter("id")), request.getParameter("agentId"), request.getParameter("peerId"));
                    log.debug("Screen barge response." + resp);
                } else if ("pauseAgent".equalsIgnoreCase(event)) {
                    resp = agentManager.sendPauseAlertToAgent(request.getParameter("username"),
                            request.getParameter("agentUniqId") == null ? null : Long.valueOf(request.getParameter("agentUniqId")), request.getParameter("agentId"), request.getParameter("reason"), request.getParameter("clientId"));
                } else if ("releaseChatByAdmin".equalsIgnoreCase(event)) {
                    log.debug(request.getParameter("SessionId"));
                    resp = occManager.releaseChatByAdmin(request.getParameter("SessionId"), request.getParameter("who"));
                } else if (StringUtils.equalsIgnoreCase(event, "setDisposition")) {
                    log.debug("Set disposition request:" + RequestUtil.getRequestParams(request));
                    resp = dispositionManager.setDispositionByApi(request.getParameter("disposition"), request.getParameter("dispComment"),
                            request.getParameter("ucid"), request.getParameter("did"),
                            request.getParameter("user"), Boolean.valueOf(request.getParameter("pauseAfterDispose")),
                            request.getParameter("agentId"), request.getParameter("pauseReason"),
                            request.getParameter("apiKey"), request.getParameter("responseFormat"),
                            request.getParameter("callbacktz"));
                } else {
                    log.debug("Bad event:" + RequestUtil.getRequestParams(request));
                }

                log.info("Action:" + event + " with reqest params " + RequestUtil.getRequestParams(request) + "'s resp -> " + resp);
                ServletOutputStream out = response.getOutputStream();
                out.println(resp);

            }

        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;//don't keep ,just throw away to return 500 resp code to caller..
        }

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processRequest(request, response);
    }
}

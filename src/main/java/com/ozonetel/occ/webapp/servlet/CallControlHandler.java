/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.webapp.servlet;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentTokenManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.util.AppContext;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author pavanj
 */
public class CallControlHandler extends HttpServlet {

    private static Logger log = Logger.getLogger(CallControlHandler.class);
    private AgentManager agentManager;
    private UserManager userManager;
    private AgentTokenManager agentTokenManager;

    public CallControlHandler() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
        agentTokenManager = (AgentTokenManager) webApplicationContext.getBean("agentTokenManager");
        userManager =  (UserManager) webApplicationContext.getBean("userManager");
    }

    @SuppressWarnings("unchecked")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String event = request.getParameter("event");
        log.debug("Handling event : "+ event);
        String resp = "";
        try {
            if ("answer".equalsIgnoreCase(event) || "decline".equalsIgnoreCase(event)) {
                log.debug("inside");
//                resp = agentManager.performCallEvent(event, request.getParameter("agentId"), request.getParameter("apiKey"), request.getParameter("ucid"));
                try {
                    Agent a = agentManager.getAgentByAgentIdV2(userManager.getUserByApiKey(request.getParameter("apiKey")).getUsername(), request.getParameter("agentId"));
//        log.debug(a);
                    Token tokenResponse = TokenFactory.createToken();
                    tokenResponse.setType("performCallEvent");
                    tokenResponse.setString("event", event);
                    resp = ""+agentTokenManager.sendTokenToAgent(a, tokenResponse);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    resp = "ERROR";
                }
            }

            ServletOutputStream out = response.getOutputStream();
            out.println(resp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

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

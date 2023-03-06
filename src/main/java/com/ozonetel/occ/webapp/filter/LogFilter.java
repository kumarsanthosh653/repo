/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.webapp.filter;

import com.ozonetel.occ.webapp.util.RequestUtil;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author pavanj
 */
public class LogFilter extends OncePerRequestFilter {

    private static final Logger log = Logger.getLogger(LogFilter.class);

    @Override
    public void doFilterInternal(HttpServletRequest hsr, HttpServletResponse hsr1, FilterChain fc) throws ServletException, IOException {
        try {

            MDC.put("user", StringUtils.trimToEmpty(hsr.getParameter("user")));
            MDC.put("sessionID", hsr.getSession().getId());
            MDC.put("agentMonitorUCID", StringUtils.trimToEmpty(hsr.getParameter("agentMonitorUcid")));
            MDC.put("agent", StringUtils.trimToEmpty(hsr.getParameter("agentId")));
            MDC.put("ucid", StringUtils.trimToEmpty(hsr.getParameter("ucid")));
            log.debug("Income URL -> " + hsr.getRequestURL() + " | Params:" + RequestUtil.getRequestParams(hsr));
            fc.doFilter(hsr, hsr1);
        } catch (IOException | ServletException e) {
            log.error(e.getMessage(), e);
        } finally {
            MDC.remove("agentMonitorUCID");
            MDC.remove("sessionID");
            MDC.remove("user");
            MDC.remove("agent");
            MDC.remove("ucid");
        }
    }

}

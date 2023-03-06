package com.ozonetel.occ.webapp.filter;

import com.ozonetel.occ.webapp.util.RequestUtil;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 *
 * @author pavanj
 */
public class CommandLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.trace("Filter '" + filterConfig.getFilterName() + "' configured successfully ");
    }

    @Override
    public void doFilter(ServletRequest hsr, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {

            String userName = hsr.getParameter("userName");
            String agentId = hsr.getParameter("userName");
            logger.trace("Income URL -> " + ((HttpServletRequest) hsr).getRequestURL() + "?" + ((HttpServletRequest) hsr).getQueryString() + " | Params:" + RequestUtil.getRequestParams(((HttpServletRequest) hsr)));

            MDC.put("user", userName == null ? "" : userName);
            MDC.put("agent", ":" + agentId == null ? "" : agentId);
            MDC.put("sessionID", ((HttpServletRequest) hsr).getSession().getId());

//            logger.trace("Income URL -> " + RequestUtil.getAppURL(hsr) + "?" + hsr.getQueryString());
            chain.doFilter(hsr, response);
        } finally {
            MDC.remove("sessionId");
            MDC.remove("user");
            MDC.remove("agent");

        }
    }

    @Override
    public void destroy() {

    }

    private static Logger logger = Logger.getLogger(CommandLogFilter.class);
}

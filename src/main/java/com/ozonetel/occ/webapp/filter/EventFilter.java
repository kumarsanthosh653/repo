package com.ozonetel.occ.webapp.filter;

import com.ozonetel.occ.service.RedisAgentManager;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author pavanj
 */
public class EventFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String monitorUcid = request.getParameter("agentMonitorUcid");
        String action = StringUtils.upperCase(request.getParameter("action"));
        
        Integer reqId = 0;
        reqId = request.getParameter("seqId") != null ? Integer.parseInt(request.getParameter("seqId")) : 0;
        switch (action) {
            case "BUSYAGENT":
            case "CHECKAGENT":
            case "RELEASEAGENT":
            case "UPDATECALLSTATUS":

//            case "TRANSFERCHECK":
//            case "TRANSFERFAILED":
                String tmpSeqId = redisAgentManager.getString("seq:" + monitorUcid);
                if (StringUtils.isNotBlank(tmpSeqId)) {
                    Integer lastSeqId = Integer.valueOf(tmpSeqId);
                    if (lastSeqId + 1 == reqId) {//accepted
                        logger.debug(">>>ACK:Accept | Command:" + action);
                        redisAgentManager.setString("seq:" + monitorUcid, "" + reqId);
                        filterChain.doFilter(request, response);
                    } else {
                        logger.debug(">>>ACK:Reject | Command:" + action + "| Received: " + reqId + " | Expected: " + (lastSeqId + 1));
                        PrintWriter pw = response.getWriter();
                        response.setContentType("application/xml");
                        if (logger.isDebugEnabled()) {
                            logger.debug("Response:" + "<response><status>0</status><message>Wrong sequence</message><<seq>>" + (lastSeqId + 1) + "</<seq>></response>");
                        }
                        pw.println("<response><status>0</status><message>Wrong sequence</message><seq>" + (lastSeqId + 1) + "</seq></response>");
                    }
                    if (StringUtils.equalsIgnoreCase(request.getParameter("callCompleted"), "true")) {
                        redisAgentManager.del("seq:" + monitorUcid);
                    }
                } else {
                    redisAgentManager.setString("seq:" + monitorUcid, "" + reqId);
                    filterChain.doFilter(request, response);
                }
                break;
            default:
                logger.debug(">>>ACK:No check Accept | Command:" + action);
                filterChain.doFilter(request, response);
                break;

        }
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    private RedisAgentManager redisAgentManager;

}

package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.MiniTicket;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.TicketManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.util.HttpUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class TicketManagerImpl implements TicketManager {

    private static Logger log = Logger.getLogger(TicketManagerImpl.class);

    @Override
    public StatusMessage openTicket(Long ticketID, String username, String agent_id, String callerID, Long ucid, Long monitorUCID, String comment, String desc) {

        //TODO Seperate security check.
        if (!userManager.hasTicketRole(username)) {
            return new StatusMessage(Status.ERROR, "You don't have permission to create/update ticket.");
        }

        try {
            URIBuilder uRIBuilder = new URIBuilder(ticketSystemURL.toURI());
            uRIBuilder.addParameter("action", "openTicket");
            uRIBuilder.addParameter("ticketID", "" + ticketID);
            uRIBuilder.addParameter("username", username);
            uRIBuilder.addParameter("agent_id", agent_id);
            uRIBuilder.addParameter("callerID", callerID);
            uRIBuilder.addParameter("ucid", "" + ucid);
            uRIBuilder.addParameter("monitorUCID", "" + monitorUCID);
            uRIBuilder.addParameter("comment", comment);
            uRIBuilder.addParameter("desc", desc);
            String url = uRIBuilder.build().toString();
            HttpResponseDetails httpResponseDetails = HttpUtils.doGet(uRIBuilder.build().toString());
            log.debug("Open ticket :" + url + " | Http Response: " + httpResponseDetails.toLongString());
            return new StatusMessage(Status.SUCCESS, httpResponseDetails.getResponseBody());
        } catch (URISyntaxException | IOException ex) {
            log.error(ex.getMessage(), ex);
            return new StatusMessage(Status.ERROR, "exception");
        }

    }

    @Override
    public StatusMessage updateTicket(Long ticketID, String username, String agent_id, String callerID, Long ucid, Long monitorUCID, String comment, String status) {

        //TODO Seperate security check.
        if (!userManager.hasTicketRole(username)) {
            return new StatusMessage(Status.ERROR, "You don't have permission to create/update ticket.");
        }

        try {
            URIBuilder uRIBuilder = new URIBuilder(ticketSystemURL.toURI());
            uRIBuilder.addParameter("action", "updateTicket");
            uRIBuilder.addParameter("ticketID", "" + ticketID);
            uRIBuilder.addParameter("username", username);
            uRIBuilder.addParameter("agent_id", agent_id);
            uRIBuilder.addParameter("callerID", callerID);
            uRIBuilder.addParameter("ucid", "" + ucid);
            uRIBuilder.addParameter("monitorUCID", "" + monitorUCID);
            uRIBuilder.addParameter("comment", comment);
            uRIBuilder.addParameter("status", status);
            String url = uRIBuilder.build().toString();
            HttpResponseDetails httpResponseDetails = HttpUtils.doGet(uRIBuilder.build().toString());
            log.debug("Update ticket :" + url + " | Http Response: " + httpResponseDetails.toLongString());
            return new StatusMessage(Status.SUCCESS, httpResponseDetails.getResponseBody());
        } catch (URISyntaxException | IOException ex) {
            log.error(ex.getMessage(), ex);
            return new StatusMessage(Status.ERROR, "exception");
        }

    }

    @Override
    public MiniTicket getTicketHistory(String user, Long ticketId) {
        try {
            URIBuilder uRIBuilder = new URIBuilder(ticketSystemURL.toURI());
            uRIBuilder.addParameter("action", "getTicket");
            uRIBuilder.addParameter("ticketID", "" + ticketId);
            uRIBuilder.addParameter("username", user);

            HttpResponseDetails httpResponseDetails = HttpUtils.doGet(uRIBuilder.build().toString());
            if (StringUtils.isNotBlank(httpResponseDetails.getResponseBody())) {
                return new Gson().fromJson(httpResponseDetails.getResponseBody(), MiniTicket.class);
            }
        } catch (URISyntaxException | IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public List<MiniTicket> getTicketsByCustomerNumber(String user, String customerNumber) {
        try {
            URIBuilder uRIBuilder = new URIBuilder(ticketSystemURL.toURI());
            uRIBuilder.addParameter("action", "getTicketsByPhone");
            uRIBuilder.addParameter("username", user);
            uRIBuilder.addParameter("phoneNumber", customerNumber);

            HttpResponseDetails httpResponseDetails = HttpUtils.doGet(uRIBuilder.build().toString());
            if (StringUtils.isNotBlank(httpResponseDetails.getResponseBody())) {
                return new Gson().fromJson(httpResponseDetails.getResponseBody(), new TypeToken<List<MiniTicket>>(){}.getType());
            }
        } catch (URISyntaxException | IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }
    

    public void setTicketSystemURL(URL ticketSystemURL) {
        this.ticketSystemURL = ticketSystemURL;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    private URL ticketSystemURL;
    private UserManager userManager;

}

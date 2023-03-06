package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.SMSReport;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.SMSManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.util.HttpUtils;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class SMSManagerImpl implements SMSManager {

    private static Logger logger = Logger.getLogger(SMSManagerImpl.class);

    @Override
    public StatusMessage sendSMS(String user, BigInteger ucid, Long campaignId, String url, String requestType, String agentId, String destination, String msg, String entityId, String templateId) {
        logger.info("Agent " + agentId + " of " + user + " sending the message --" + msg + "-- to " + destination);
        StatusMessage statusMessage = new StatusMessage();
        try {
            switch (requestType.toUpperCase()) {
                case "GET":
                    try {
                        MessageFormat formatter = new MessageFormat(url);
                        url = formatter.format(new Object[]{URLEncoder.encode(destination, "UTF-8"),
                            URLEncoder.encode(msg, "UTF-8"), URLEncoder.encode(entityId, "UTF-8"), URLEncoder.encode(templateId, "UTF-8")});
                        statusMessage = new StatusMessage(Status.SUCCESS, httpUtils.doGet(url).getResponseBody());
                        logger.info(">>><<URL:" + url);
                    } catch (IOException ex) {
                        statusMessage = new StatusMessage(Status.ERROR, ex.getMessage());
                    }
                    break;
                case "POST":

                    String queryString;
                    try {

                        queryString = new URL(url).getQuery();
                        if (StringUtils.isNotEmpty(queryString)) {
                            MessageFormat formatter = new MessageFormat(queryString);
                            queryString = formatter.format(new Object[]{URLEncoder.encode(destination, "UTF-8"),
                                URLEncoder.encode(msg, "UTF-8"), URLEncoder.encode(entityId, "UTF-8"), URLEncoder.encode(templateId, "UTF-8")});
                        }
                        HttpResponseDetails postResponse = httpUtils.sendHttpPOSTRequest(
                                (StringUtils.contains(url, "?") ? url.substring(0, url.indexOf("?")) : url), queryString);

                        statusMessage = new StatusMessage(Status.SUCCESS, postResponse.getResponseBody());
                        logger.info(">>><<URL:" + url + "| data:" + queryString);
                    } catch (Exception ex) {
                        logger.error("Exception in sending message {User=" + user + " , AgentId="
                                + agentId + " , Destination=" + destination + " , Message=" + msg + "}:" + ex.getMessage(), ex);
                        statusMessage = new StatusMessage(Status.ERROR, ex.getMessage());
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            statusMessage = new StatusMessage(Status.ERROR, e.getMessage());
        }

        try {
            logger.debug("Agent " + agentId + " of " + user + " sending the message --" + msg + "-- to " + destination + " --> " + statusMessage);

            sMSReportManager.save(new SMSReport(userManager.getUserByUsername(user).getId(),
                    agentId, ucid, campaignId, new Date(), StringUtils.abbreviate(statusMessage.getMessage(), 100), StringUtils.abbreviate(msg, 255), destination));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return statusMessage;
    }


    @Override
    public StatusMessage sendWhatsappMSG(String username,String whatsappURL,String requestType, String recipient, String templateName, String replacementText) {
        logger.info("User "  + username + " sending message to whatsapp with url -- " + whatsappURL + " having requestMethodType -- " +requestType +
                " with recipient -- "+recipient+" , templateName -- "+templateName+" and text is -- " + replacementText);
        StatusMessage statusMessage = new StatusMessage();
        try {
                String queryString;
                try {

                    queryString = new URL(whatsappURL).getQuery();
                    if (StringUtils.isNotEmpty(queryString)) {
                        MessageFormat formatter = new MessageFormat(queryString);
                        queryString = formatter.format(new Object[]{URLEncoder.encode(recipient, "UTF-8"),
                            URLEncoder.encode(templateName, "UTF-8"), URLEncoder.encode(replacementText, "UTF-8")});
                    }
                    HttpResponseDetails postResponse = httpUtils.sendHttpPOSTRequest(
                            (StringUtils.contains(whatsappURL, "?") ? whatsappURL.substring(0, whatsappURL.indexOf("?")) : whatsappURL), queryString);
                    logger.debug("got response from whatsapp url : "+postResponse.getResponseBody());
                    statusMessage = new StatusMessage(Status.SUCCESS, postResponse.getResponseBody());
                    logger.info(">>><<URL:" + whatsappURL + "| data:" + queryString);
                } catch (Exception ex) {
                    logger.error("Exception in sending whatsapp message {User "  + username + " sending message to whatsapp with url -- " + whatsappURL + " having requestMethodType -- " +requestType +
                " with recipient -- "+recipient+" , templateName -- "+templateName+" and text is -- " + replacementText+"}:" + ex.getMessage(), ex);
                    statusMessage = new StatusMessage(Status.ERROR, ex.getMessage());
                }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            statusMessage = new StatusMessage(Status.ERROR, e.getMessage());
        }        

        return statusMessage;
    }

    public void setHttpUtils(HttpUtils httpUtils) {
        this.httpUtils = httpUtils;
    }

    public void setsMSReportManager(GenericManager<SMSReport, Long> sMSReportManager) {
        this.sMSReportManager = sMSReportManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    private UserManager userManager;
    private HttpUtils httpUtils;
    private GenericManager<SMSReport, Long> sMSReportManager;
}

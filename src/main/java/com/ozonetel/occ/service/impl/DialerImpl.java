package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.Dialer;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.util.HttpUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author pavanj
 */
public class DialerImpl implements Dialer, MessageSourceAware {

    @Override
    public StatusMessage dial(String username, String agentId, String agentPhoneNumber, Long dataId, String customerNumber) {
//--------------------------------------------------------         
        StatusMessage reMessage = new StatusMessage(Status.SUCCESS, messageSource.getMessage("success.previewdial", null, Locale.getDefault()));
        if (agentManager.lockIfAgentAvailable(username, agentId, customerNumber)) {
            return new StatusMessage(Status.ERROR, messageSource.getMessage("error.previewdial.agent.locked", null, "You can't make calls now.", Locale.getDefault()));
        } else {
            Long campaignId = previewDataManager.getCampaignIdofData(dataId);
            if (campaignId != null) {

                try {
                    URIBuilder uRIBuilder = new URIBuilder(appProperty.getDialerApi());
                    uRIBuilder.addParameter("campaignId", "" + campaignId);
                    uRIBuilder.addParameter("dataId", "" + dataId);
                    uRIBuilder.addParameter("agentId", agentId);
                    uRIBuilder.addParameter("agentPhoneNumber", agentPhoneNumber);
                    HttpResponseDetails responseDetails = HttpUtils.doGet(uRIBuilder.build().toString());

                    if (responseDetails.getStatusCode() != 200 || StringUtils.containsIgnoreCase(responseDetails.getResponseBody(), "error")) {
                        return new StatusMessage(Status.ERROR, messageSource.getMessage("fail.previewdial", null, "Call failed.", Locale.getDefault()));
                    }

                } catch (URISyntaxException | IOException ex) {
                    logger.error(ex.getMessage(), ex);
                    return new StatusMessage(Status.ERROR, messageSource.getMessage("fail.previewdial", null, Locale.getDefault()));
                }
            }
        }
        return reMessage;
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }

    public void setPreviewDataManager(PreviewDataManager previewDataManager) {
        this.previewDataManager = previewDataManager;
    }

    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    private PreviewDataManager previewDataManager;
    private AgentManager agentManager;
    private AppProperty appProperty;
    private MessageSource messageSource;

    private static Logger logger = Logger.getLogger(DialerImpl.class);
}

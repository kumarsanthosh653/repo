package com.ozonetel.occ.service.chat.impl;

import com.google.gson.JsonObject;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.ChatAgentFinderService;
import com.ozonetel.occ.service.RedisAgentManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

/**
 *
 * @author aparna
 */
public class ChatBotServiceImpl {

    private Logger logger = Logger.getLogger(ChatBotServiceImpl.class);
    private String chatCallbackUrl;
    private String kookoochatbotUrl;
    private CampaignManager campaignManager;

    public CampaignManager getCampaignManager() {
        return campaignManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public String getKookoochatbotUrl() {
        return kookoochatbotUrl;
    }

    public void setKookoochatbotUrl(String kookoochatbotUrl) {
        this.kookoochatbotUrl = kookoochatbotUrl;
    }

    public String getChatCallbackUrl() {
        return chatCallbackUrl;
    }

    public void setChatCallbackUrl(String chatCallbackUrl) {
        this.chatCallbackUrl = chatCallbackUrl;
    }

    public void askIvr(final String clientsessionId, String text, final String apiKey, final String did, final String clientId, String msgType) throws Exception {
        try {

            logger.debug("got from client : " + text);

            JsonObject reqObj = new JsonObject();
            reqObj.addProperty("sessionId", clientsessionId);
            reqObj.addProperty("message", text);
            reqObj.addProperty("apiKey", apiKey);
            reqObj.addProperty("did", did);
            reqObj.addProperty("timestamp", System.currentTimeMillis());
            reqObj.addProperty("clientId", clientId);
            reqObj.addProperty("messageType", msgType);
            logger.debug("Got chat campaign as : "+campaignManager.getCampaignsByDid(did,"Chat"));
            //logger.debug(campaignManager.getCampaignsByDid(did,"Chat").getIvrFlow().getAppUrl()!=null);
            reqObj.addProperty("appUrl", (campaignManager.getCampaignsByDid(did,"Chat").getIvrFlow().getAppUrl()!=null)? campaignManager.getCampaignsByDid(did,"Chat").getIvrFlow().getAppUrl() : null);
            reqObj.addProperty("ivrFlowId", "0");
            //reqObj.addProperty("custMailPhone", custMailPhone);
            reqObj.addProperty("callBackUrl", chatCallbackUrl);

            logger.debug("Sending request with data : " + reqObj.toString());
            HttpClient client = new DefaultHttpClient();
            HttpPost p = new HttpPost(kookoochatbotUrl);
            p.setEntity(new StringEntity(reqObj.toString(),
                    ContentType.create("application/json")));
            HttpResponse response = client.execute(p);
            logger.debug("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            logger.debug(result);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof HttpResponseException) {
                throw e;
            }
        }
       
    }

}

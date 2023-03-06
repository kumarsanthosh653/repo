package com.ozonetel.occ.service.impl;

import com.google.gson.GsonBuilder;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.util.HttpUtils;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.token.Token;

/**
 *
 * @author pavanj
 */
public class TokenServerLocalImpl {

    public WebSocketConnector getConnector(String aId) {
        return new WebSocketConnectorLocalImpl(aId);
    }

    public void sendToken(WebSocketConnector aTarget, Token aToken) {
        String connectorId = aTarget.getNodeId();
        Map tokenMap = aToken.getMap();
        try {
            URIBuilder urib = new URIBuilder(webScoketServerUrl);
            urib.addParameter("clientId", connectorId);
            urib.addParameter("token", new GsonBuilder().serializeNulls().create().toJson(tokenMap));
            log.debug("Hitting   😑  🔨:" + urib.build());
            HttpResponseDetails httpResponseDetails = HttpUtils.doGet(urib.build().toString(), 10000);
            log.debug("Hit   😑  🔨   Sending token to agent:" + urib.build().toString() + " | " + httpResponseDetails);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendChatToken(WebSocketConnector aTarget, Token aToken) {
        try {
            String connectorId = aTarget.getNodeId();
            Map tokenMap = aToken.getMap();
            HttpResponseDetails httpResponse = null;
            StringBuilder queryString = new StringBuilder();

            queryString.append("clientId=").append(connectorId);
            queryString.append("&token=").append(URLEncoder.encode(new GsonBuilder().serializeNulls().create().toJson(tokenMap), "UTF-8"));
//            queryString = String.format("token=%s", URLEncoder.encode(new GsonBuilder().serializeNulls().create().toJson(tokenMap), "UTF-8"));
            try {
                httpResponse = HttpUtils.sendHttpPOSTRequest(webScoketServerUrl, queryString.toString());
                log.info(">>><<URL:" + webScoketServerUrl + "| data:" + queryString.toString() + "| Response:" + httpResponse);
            } catch (ConnectException ex) {
                log.error(ex.getMessage(), ex);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
//            String response = null;
//            List<NameValuePair> formparams = new ArrayList<>();
//            formparams.add(new BasicNameValuePair("clientId", connectorId));
//            formparams.add(new BasicNameValuePair("token", new GsonBuilder().serializeNulls().create().toJson(tokenMap)));
//            response = new HttpUtils().doPostRequest(webScoketServerUrl, formparams);
//            log.info(">>><<URL:" + webScoketServerUrl + "| data:" + formparams + "| Response:" + response);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

    }

    public void setWebScoketServerUrl(String webScoketServerUrl) {
        this.webScoketServerUrl = webScoketServerUrl;
    }

    public String getWsServerUrl(){
        return this.webScoketServerUrl;
    }
    
    private String webScoketServerUrl;
    Log log = LogFactory.getLog(getClass());

}

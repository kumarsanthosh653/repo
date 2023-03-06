/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.PreviewExtraDataManager;
import com.ozonetel.occ.service.ScreenPopService;
import com.ozonetel.occ.util.HttpUtils;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author aparna
 */
public class ScreenPopServiceImpl implements ScreenPopService {

    private static Logger log = Logger.getLogger(ScreenPopServiceImpl.class);
    private CampaignManager campaignManager;
    private PreviewExtraDataManager previewExtraDataManager;

    public CampaignManager getCampaignManager() {
        return campaignManager;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public PreviewExtraDataManager getPreviewExtraDataManager() {
        return previewExtraDataManager;
    }

    public void setPreviewExtraDataManager(PreviewExtraDataManager previewExtraDataManager) {
        this.previewExtraDataManager = previewExtraDataManager;
    }

    @Override
    public void hitScreenPopHere(Campaign c, Agent a, FwpNumber f, String ucid, String callerId, String did, String skillName, String dataId, String agentMonitorUcid, String type, String uui, String agentId) {
        log.debug("------>Called hit screen pop:");
        HttpUtils httpUtils = new HttpUtils();
        try {
            if (c.getScreenPopUrl() != null && !c.getScreenPopUrl().isEmpty()) {
                //Check if the UrlContains the ? and =
                String s = c.getScreenPopUrl();
                String url = s.contains("?") ? s.substring(0, s.lastIndexOf("?")) : s;
                String cId = s.contains("?") ? s.substring(s.lastIndexOf("?") + 1) : s;
                String callerIdParam = cId.contains("=") ? cId.substring(0, cId.lastIndexOf("=")) : cId;
                String callerIdParamValue = cId.contains("=") ? cId.substring(cId.lastIndexOf("=") + 1) : null;

                StringBuilder queryString = new StringBuilder();
                queryString.append("ucid=").append(ucid);
                queryString.append("&callerID=").append(callerId);
                queryString.append("&did=").append(did);
                queryString.append("&skillName=").append(skillName);
                queryString.append("&agentUniqueID=").append(a != null ? a.getId() : null);
                queryString.append("&dataID=").append(dataId);
                queryString.append("&campaignID=").append(c.getCampaignId());
                queryString.append("&monitorUcid=").append(agentMonitorUcid);
                queryString.append("&phoneName=").append(f != null ? f.getPhoneName() : "");
                queryString.append("&").append("agentPhoneNumber").append("=").append(a != null ? a.getPhoneNumber() : (f != null ? f.getPhoneNumber() : ""));
                if (type.equalsIgnoreCase("ToolBarManual")) {
                    queryString.append("&type=").append("Manual");
                } else {
                    queryString.append("&type=").append(type);
                }
                queryString.append("&uui=").append(URLEncoder.encode(uui));

                if (!callerIdParam.isEmpty()) {
                    log.debug("callerId param : " + callerIdParam);
                    log.debug("callerId param value : " + callerIdParamValue);
                    if (callerIdParamValue == null || callerIdParamValue.isEmpty())
                        queryString.append("&").append(callerIdParam).append("=").append(callerId);
                    else
                        queryString.append("&").append(callerIdParam).append("=").append(callerIdParamValue);
//                    log.debug("QueryString : " + queryString);
                }
                if (a != null && a.getAgentData() != null && !a.getAgentData().isEmpty()) {//Apend agentDetails configured in AgentData
                    queryString.append("&").append(a.getAgentData());
//                    log.debug("QueryString : " + queryString);
                }
                User u = c.getUser();
                //Send Customer Defined Params also
                boolean customerInfo = false;
                if (u != null) {
                    Map<String, Object> params = new LinkedHashMap<String, Object>();
                    params.put("user_id", u.getId());
                    List userParameters = campaignManager.executeProcedure("call Get_UserParamters(?)", params);
                    String custPH = "", agentIdPH = "", agentKeyPH = "", agentPhonePH = "", sendAgentParms = "false";
                    for (Object object : userParameters) {
                        Map<String, String> mp = (Map) object;
                        if (mp.get("ParameterValue") != null) {
                            if (mp.get("ParameterCode").equalsIgnoreCase("CUST_PH")) {
                                custPH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("AGENT_ID_PH")) {
                                agentIdPH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("AGENT_KEY_PH")) {
                                agentKeyPH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("AGENT_PHONE_PH")) {
                                agentPhonePH = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("SEND_CUST_PARAMS")) {
                                sendAgentParms = mp.get("ParameterValue");
                            }
                            if (mp.get("ParameterCode").equalsIgnoreCase("CUST_INFO_SCREENPOP")) {
                                customerInfo = Boolean.valueOf(mp.get("ParameterValue"));
                            }
                        }
                    }

                    try {
                        if (customerInfo && StringUtils.isNotBlank(dataId)) {
                            Map<String, String> data = previewExtraDataManager.getCustomerData(Long.valueOf(dataId), c.getCampaignId());
                            for (Map.Entry<String, String> entry : data.entrySet()) {
                                queryString.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//                                log.debug("QueryString : " + queryString);
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    if (sendAgentParms.equalsIgnoreCase("true")) {
                        log.debug("Sending Customer defined Params also");
                        queryString.append("&").append(custPH).append("=").append(u.getUsername());
                        queryString.append("&").append(agentIdPH).append("=").append(agentId);
                        queryString.append("&").append(agentKeyPH).append("=").append(a != null ? a.getPassword() : "");
                        queryString.append("&").append(agentPhonePH).append("=").append(a != null ? a.getPhoneNumber() : (f != null ? f.getPhoneNumber() : ""));
                    } else {
                        queryString.append("&agentID=").append(agentId);
                        queryString.append("&customer=").append(c.getUser().getUsername());
                    }
                }

                try {
                    log.debug("REQUEST=" + url + "?" + queryString.toString());
                    httpUtils.doGetRequestAsThread(url, queryString.toString());
                    // Below comment code is writter BasicAuthentication  -- Need to revisit here         
                    /* queryString.append("&popUrl="+URLEncoder.encode(url));
                     ScreenPopUtil.doGetRequestAsThread(url, queryString.toString());*/
//                    ScreenPopUtil.doBasicAuthRequest(url, queryString.toString());

                } catch (Exception e) {
                    log.error("[" + ucid + "]Error sending Server side=" + e.getMessage(), e);
                    //e.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

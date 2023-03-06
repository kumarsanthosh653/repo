/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.util;

import com.google.i18n.phonenumbers.Phonenumber;
import com.ozonetel.occ.model.CampaignConfiguration;
import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.impl.Status;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class PhoneNumberUtil {

    private static Logger log = Logger.getLogger(PhoneNumberUtil.class);

    public boolean isBlockedNumber(Long userId, String phoneNumber) {

        boolean isBlocked = false;

        try {

            log.debug("user ::: " + userId + " :: phoneNumber :: " + (phoneNumber.length() > 10 ? phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length()) : phoneNumber));

            if (userId != null) {
                Map<String, Object> params = new LinkedHashMap();
                params.put("user_id", userId);
                params.put("param_code", "CHECK_BLOCK_NUMBERS");
                List userParameters = campaignManager.executeProcedure("call Get_UserParamter(?,?)", params);
                if (userParameters != null && !userParameters.isEmpty()) {
                    Map<String, Object> checkBlockedNumber = (Map<String, Object>) userParameters.get(0);
                    log.debug(checkBlockedNumber);
                    log.debug("block parameter checking if condition :::: " + ((checkBlockedNumber.get("ParameterValue") != null
                            && !checkBlockedNumber.get("ParameterValue").toString().isEmpty()
                            && checkBlockedNumber.get("ParameterValue").toString().equalsIgnoreCase("true"))
                            || checkBlockedNumber.get("DefaultValue").toString().equalsIgnoreCase("true")));
                    if ((checkBlockedNumber.get("ParameterValue") != null
                            && !checkBlockedNumber.get("ParameterValue").toString().isEmpty()
                            && checkBlockedNumber.get("ParameterValue").toString().equalsIgnoreCase("true"))
                            || checkBlockedNumber.get("DefaultValue").toString().equalsIgnoreCase("true")) {
                        params.clear();
                        params.put("user_id", userId);
                        params.put("phoneNumber", phoneNumber.length() > 10 ? phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length()) : phoneNumber);
                        List blockedNumbers = campaignManager.executeProcedure("call Get_UserBlockedNumber(?,?)", params);
                        if (blockedNumbers != null && !blockedNumbers.isEmpty()) {
                            Map<String, Object> blockedNumber = (Map<String, Object>) blockedNumbers.get(0);
                            if (blockedNumber.get("BlockedNumber").toString().equalsIgnoreCase("Exists")) {
                                isBlocked = true;
                            }
                        }
                    }
                }
            } else {
                log.error("invalid user selection");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.debug("isBlocked ::: " + isBlocked);
        return isBlocked;

    }

    public boolean isBlockedNumber(String phoneNumber, Long campaignId) {
        boolean isBlocked = false;
        CampaignConfiguration campaignConfiguration = campaignConfigurationManager.get(campaignId);
        if (campaignConfiguration != null && campaignConfiguration.getAllowBlockCheck() != null) {
            StatusMessage statusMessage = new StatusMessage(Status.ERROR, "Not Exists");
            try {
                URIBuilder url = new URIBuilder(numberCheckUrl);
                url.addParameter("phoneNo", getE164Format(phoneNumber));
                url.addParameter("campaignId", campaignId + "");
                HttpResponseDetails httpResponseDetails = HttpUtils.doGet(url.build().toString());
                statusMessage = KookooUtils.parseKookooResponse(httpResponseDetails.getResponseBody());
                log.debug("Phone number : " + phoneNumber + " Number check response : " + statusMessage + " | Configuration : " + campaignConfiguration.getAllowBlockCheck());
                if (campaignConfiguration.getAllowBlockCheck() == 1) { //block check
                    if (statusMessage.getStatus() == Status.SUCCESS && StringUtils.equalsIgnoreCase(statusMessage.getMessage(), "Exists")) {
                        isBlocked = true; //number is blocked
                    }
                } else if (campaignConfiguration.getAllowBlockCheck() == 2) { //allow check
                    if (statusMessage.getStatus() == Status.SUCCESS && StringUtils.equalsIgnoreCase(statusMessage.getMessage(), "Exists")) {
                        isBlocked = false; //numer is allowed to dial
                    } else {
                        isBlocked = true; //numer is not allowed to dial
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (URISyntaxException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        log.debug("isBlocked ::: " + isBlocked);
        return isBlocked;
    }

    public static String getE164Format(String number) {
        com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
        String tmpNumber = number;
        try {
            Phonenumber.PhoneNumber indiaNumberProto = phoneUtil.parse(number, "IN");
            tmpNumber = phoneUtil.format(indiaNumberProto, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164).replaceAll(" ", "");
            log.debug("tmpNumber = " + tmpNumber);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tmpNumber;
    }

    public static String getNationalNumber(String number) {
        com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
        String tmpNumber = number;
        try {
            Phonenumber.PhoneNumber indiaNumberProto = phoneUtil.parse(number, "IN");
            if (phoneUtil.isValidNumber(indiaNumberProto)) {
                return phoneUtil.format(indiaNumberProto, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.NATIONAL).replaceAll(" ", "");
            } else {
                log.warn("Supplied number [" + number + "] is not a valid phone number.");
                return number;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tmpNumber;
    }

    public static boolean isValidPhoneNumber(String number) {
//        log.trace("called valid phone number:"+number);
        if (StringUtils.startsWith(number, "#")) {
            return true;
        } else {
            try {
                com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber indiaNumberProto = phoneUtil.parse(number, "IN");
                return phoneUtil.isValidNumber(indiaNumberProto);
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static String getNationalNumberForAgent(String number) {
//        return number;
        if (StringUtils.startsWith(number, "#")) {
            return number;
        } else {
            return getNationalNumber(number);
        }
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

    public void setCampaignConfigurationManager(GenericManager<CampaignConfiguration, Long> campaignConfigurationManager) {
        this.campaignConfigurationManager = campaignConfigurationManager;
    }

    public void setNumberCheckUrl(String numberCheckUrl) {
        this.numberCheckUrl = numberCheckUrl;
    }

    private CampaignManager campaignManager;
    private GenericManager<CampaignConfiguration, Long> campaignConfigurationManager;
    private String numberCheckUrl;
}

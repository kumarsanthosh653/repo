/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.webapp.util;

import com.ozonetel.occ.util.AppContext;
import com.zoho.crm.ctiapisdk.client.Callanswered;
import com.zoho.crm.ctiapisdk.client.Calldialed;
import com.zoho.crm.ctiapisdk.client.Callhungup;
import com.zoho.crm.ctiapisdk.client.Callmissed;
import com.zoho.crm.ctiapisdk.client.Callreceived;
import com.zoho.crm.ctiapisdk.util.CtiApiUtil;
import java.util.Date;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author rajesh
 */
public class ZohoPlugin extends HibernateDaoSupport {

    private static Logger logger = Logger.getLogger(ZohoPlugin.class.getName());
    /**
     * parameters used for Zoho API for CTI integration
     *
     * @param	ctiname	-	your service name provided by Zoho for Zoho API usage
     * @param	authtoken	-	authtoken provided by Zoho and configured in TPI by
     * the customer
     * @param	currenttime	-	current epoch time in seconds (GMT)
     * @param	callrefid	-	unique call reference id. This should be unique in all
     * Zoho API calls (for all call state) to refer a single call
     * @param	ctinumber	-	TPI number
     * @param	custnumber	-	Customer Number
     * @param	ctiagentref	-	TPI clientname/extn/callforwardno in which the user
     * attended the call and hungup the call
     * @param	dtmfcode	-	DTMF Code pressed by the customer on hearing the
     * welcome IVR message like press 1 for sales, press 2 for support, etc.,
     * @param	direction	-	type of call direction, either inbound or outbound
     * @param	password	-	password shared by Zoho for you ie., TPI (Third Party
     * Integrator)	*
     */
    private String ctiname = "ozonetel";
    private String authtoken = "6ef540bbe2d20f7209701c4b53e28b54";
    //String currenttime;	//Assign Current time here when you are using API with current time
    //String callrefid=CtiApiUtil.getCallRefId();
    private String callrefid = CtiApiUtil.getCallRefId();
    private String direction = "inbound";
    private String ctinumber = "9948739989";
    private String custnumber = "9876543210";
    private String ctiagentref = "9948739989";
    private String recordingurl = "";
    private int recordingduration = 0;//Call Duration in seconds
    private String voicemailurl = "";
    private int voicemailduration = 0;//Voice mail call Duration in seconds
    private String password = "KKc56ad78d86f4e5dcaaa5e53e190f01bd";
    private String method = "";
    private boolean isCompleted = false;
    private boolean callCompleted = false;
    private String callType = "inbound";
    private String callStatus = "Fail";
    private String username = "";
    private String ctikey = "hngEFdo5rjAUJU9S";
    private String callbackparam = "";
    private String pluginUrl;

    public ZohoPlugin(String pluginUrl, String method, HttpServletRequest request) {
//        this.callrefid = (String) request.getParameter("ucid");
        this.callrefid = (String) request.getParameter("agentMonitorUcid");
        this.callType = (String) request.getParameter("type");
        this.direction = callType != null ? (callType.equalsIgnoreCase("inbound") ? callType : "outbound") : "";
        logger.log(Level.INFO, "ZohoPlugin CallType=" + this.direction);
        String agentPhoneNumber = (String) request.getAttribute("agentPhoneNumber");
//        agentPhoneNumber = (agentPhoneNumber.length() > 10 ? agentPhoneNumber.substring(agentPhoneNumber.length() - 10) : agentPhoneNumber);
        this.ctinumber = (String) request.getParameter("did");
        logger.log(Level.INFO, "cti number=" + ctinumber);
        this.custnumber = (String) request.getParameter("callerId");
        this.ctiagentref = agentPhoneNumber;
        this.recordingurl = (String) request.getParameter("audioFile");
        this.recordingurl = (this.recordingurl != null ? (this.recordingurl.equalsIgnoreCase("-1") ? "" : this.recordingurl) : "");
        this.method = (String) request.getParameter("action");
        this.isCompleted = Boolean.parseBoolean(request.getParameter("isCompleted"));
        this.callCompleted = Boolean.parseBoolean(request.getParameter("callCompleted"));
        this.callType = (String) request.getParameter("type");
        this.callStatus = (String) request.getParameter("callStatus");
        this.password = (String) request.getAttribute("apiKey");
        this.username = (String) request.getAttribute("username");
        this.callbackparam = (String) request.getAttribute("callBackParam");
        setSessionFactory((SessionFactory) AppContext.getApplicationContext().getBean("sessionFactory"));
        String token = getAuthToken();
        this.authtoken = token.split("~")[0];
        this.ctikey = token.split("~")[1];

//        this.sTime = (String) request.getParameter("stime");
//        this.eTime = (String) request.getParameter("etime");
    }

    public ZohoPlugin(String pluginUrl, String monitorUcid, String ucid, String callType, String agentNumber,
            String did, String custNumber, String audioFile, String action, String callStatus, String apiKey, String username, Boolean isCompleted, Boolean callCompleted, Date sTime, Date eTime, String callBackParam) {
//        this.callrefid = ucid;
        this.callrefid = monitorUcid;

        this.direction = callType != null ? (callType.equalsIgnoreCase("inbound") ? callType : "outbound") : "";
        logger.log(Level.INFO, "ZohoPlugin CallType=" + this.direction);
        String agentPhoneNumber = agentNumber;
        this.ctinumber = did;
//        logger.log(Level.INFO, "cti number=" + ctinumber);
        this.custnumber = custNumber;
        this.ctiagentref = agentPhoneNumber;
        if (callStatus.equalsIgnoreCase("success")) {
            this.recordingurl = (audioFile != null ? (audioFile.equalsIgnoreCase("-1") ? "" : audioFile) : "");
        } else {
            this.recordingurl = "";
        }
        this.method = action;
        this.isCompleted = isCompleted;
        this.callCompleted = callCompleted;
        this.callType = callType;
        this.callStatus = callStatus;
        this.password = apiKey;
        this.username = username;
        this.callbackparam = callBackParam;
        setSessionFactory((SessionFactory) AppContext.getApplicationContext().getBean("sessionFactory"));
        String token = getAuthToken();
        this.authtoken = token.split("~")[0];
        this.ctikey = token.split("~")[1];
        logger.log(Level.INFO, "[StartTime=" + sTime + "][EndTime=" + eTime + "]");
        if (sTime != null && eTime != null) {
            this.recordingduration = (int) TimeUnit.MILLISECONDS.toSeconds(eTime.getTime() - sTime.getTime());
        }

    }

    public String execute() {
        if (!method.isEmpty()) {
            if (method.equalsIgnoreCase("updateCallStatus") && !isCompleted && callType.equalsIgnoreCase("inbound")) {
                method = "callReceived";
//                Callreceived creceived = new Callreceived(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, password);
//                Callreceived creceived = new Callreceived(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, ctikey);
//                logger.log(Level.INFO, "ZohoPlugin CallReceived Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + ctikey);
//                logger.log(Level.INFO, "ZohoPlugin CallReceived Response=" + creceived.callZohoSupportCtiApi());
            } else if (method.equalsIgnoreCase("updateCallStatus") && !isCompleted && !callType.equalsIgnoreCase("inbound")) {
                method = "callDialed";

//                Calldialed cdialed = new Calldialed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, password);
//                Calldialed cdialed = new Calldialed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, callbackparam, ctikey);
//                logger.log(Level.INFO, "ZohoPlugin CallDialed Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + callbackparam + "," + ctikey);	//To call a calldialed API
//                logger.log(Level.INFO, "ZohoPlugin CallDialed Response=" + cdialed.callZohoSupportCtiApi());	//To call a calldialed API
            } else if (method.equalsIgnoreCase("busyAgent")) {
                method = "callAnswered";
//                Callanswered canswered = new Callanswered(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, password);
//                Callanswered canswered = new Callanswered(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, ctikey);
//                logger.log(Level.INFO, "ZohoPlugin CallAnswered Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + direction + "," + ctikey);	//To call a callanswered API
//                logger.log(Level.INFO, "ZohoPlugin CallAnswered Response=" + canswered.callZohoSupportCtiApi());	//To call a callanswered API
            } else if (method.equalsIgnoreCase("updateCallStatus") && isCompleted && !callStatus.equalsIgnoreCase("fail")) {
                method = "callHungup";
//                Callhungup chungup = new Callhungup(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, recordingduration, recordingurl, password);
//                Callhungup chungup = new Callhungup(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, recordingduration, recordingurl, ctikey);
//
//                logger.log(Level.INFO, "ZohoPlugin Callhungup Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + direction + "," + recordingduration + "," + recordingurl + "," + ctikey);	//To call a callhungup API
//                logger.log(Level.INFO, "ZohoPlugin Callhungup Response=" + chungup.callZohoSupportCtiApi());	//To call a callhungup API
            } else if (method.equalsIgnoreCase("updateCallStatus") && callCompleted && callStatus.equalsIgnoreCase("fail")) {
                method = "callMissed";
//                Callmissed cmissed = new Callmissed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, voicemailduration, voicemailurl, password);
//                Callmissed cmissed = new Callmissed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, recordingduration, recordingurl, ctikey);
//                logger.log(Level.INFO, "ZohoPlugin Callmissed Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + direction + "," + recordingduration + "," + recordingurl + "," + ctikey);	//To call a callmissed API
//                logger.log(Level.INFO, "ZohoPlugin Callmissed Response=" + cmissed.callZohoSupportCtiApi());	//To call a callmissed API
            }
        }

        return "Success";

    }

    public String execute1() {
        logger.log(Level.INFO, "ctiagentRef" + this.ctiagentref);
        if (!method.isEmpty()) {
            if (method.equalsIgnoreCase("updateCallStatus") && !isCompleted && callType.equalsIgnoreCase("inbound")) {
//                Callreceived creceived = new Callreceived(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, password);
                Callreceived creceived = new Callreceived(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, ctikey);
                logger.log(Level.INFO, "ZohoPlugin CallReceived Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + ctikey);
                logger.log(Level.INFO, "ZohoPlugin CallReceived Response=" + creceived.callZohoSupportCtiApi());
            } else if (method.equalsIgnoreCase("updateCallStatus") && !isCompleted && !callType.equalsIgnoreCase("inbound")) {
//                Calldialed cdialed = new Calldialed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, password);
                Calldialed cdialed = new Calldialed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, callbackparam, ctikey);
                logger.log(Level.INFO, "ZohoPlugin CallDialed Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + callbackparam + "," + ctikey);	//To call a calldialed API
                logger.log(Level.INFO, "ZohoPlugin CallDialed Response=" + cdialed.callZohoSupportCtiApi());	//To call a calldialed API
            } else if (method.equalsIgnoreCase("busyAgent")) {
//                Callanswered canswered = new Callanswered(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, password);
                Callanswered canswered = new Callanswered(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, ctikey);
                logger.log(Level.INFO, "ZohoPlugin CallAnswered Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + direction + "," + ctikey);	//To call a callanswered API
                logger.log(Level.INFO, "ZohoPlugin CallAnswered Response=" + canswered.callZohoSupportCtiApi());	//To call a callanswered API
            } else if (method.equalsIgnoreCase("updateCallStatus") && isCompleted && !callStatus.equalsIgnoreCase("fail")) {
//                Callhungup chungup = new Callhungup(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, recordingduration, recordingurl, password);
                Callhungup chungup = new Callhungup(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, recordingduration, recordingurl, ctikey);

                logger.log(Level.INFO, "ZohoPlugin Callhungup Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + direction + "," + recordingduration + "," + recordingurl + "," + ctikey);	//To call a callhungup API
                logger.log(Level.INFO, "ZohoPlugin Callhungup Response=" + chungup.callZohoSupportCtiApi());	//To call a callhungup API
            } else if (method.equalsIgnoreCase("updateCallStatus") && callCompleted && callStatus.equalsIgnoreCase("fail")) {
//                Callmissed cmissed = new Callmissed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, voicemailduration, voicemailurl, password);
                Callmissed cmissed = new Callmissed(ctiname, authtoken, callrefid, ctinumber, custnumber, ctiagentref, direction, recordingduration, recordingurl, ctikey);
                logger.log(Level.INFO, "ZohoPlugin Callmissed Request=" + ctiname + "," + authtoken + "," + callrefid + "," + ctinumber + "," + custnumber + "," + ctiagentref + "," + direction + "," + recordingduration + "," + recordingurl + "," + ctikey);	//To call a callmissed API
                logger.log(Level.INFO, "ZohoPlugin Callmissed Response=" + cmissed.callZohoSupportCtiApi());	//To call a callmissed API
            }
        }
        return "success";
    }

    public String getAuthToken() {
        String token = null;
        String ctiKey = null;
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();
            Query query = (Query) session.createSQLQuery("select p.ParameterValue from App_UserParameter p"
                    + " join app_user u on u.id = p.UserID"
                    + " join App_Parameter ap on p.ParameterID = ap.ParameterID "
                    + " where u.username='" + username + "' and ap.ParameterCode in ('AUTH_TOKEN','ZOHO_CTI_KEY')");
            List l = query.list();
            transaction.commit();
            if (l.size() > 0) {
                token = l.get(0).toString();
            }
            if (l.size() > 1) {
                ctiKey = l.get(1).toString();
            }
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        System.out.println("AuthToken for =" + username + "=" + token);
        logger.log(Level.INFO, token);
        return token + "~" + ctiKey;
    }
}

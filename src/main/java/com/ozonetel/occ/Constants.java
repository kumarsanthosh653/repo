package com.ozonetel.occ;

/**
 * Constant values used throughout the application.
 *
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class Constants {
    //~ Static fields/initializers =============================================

    /**
     * The name of the ResourceBundle used in this application
     */
    public static final String BUNDLE_KEY = "ApplicationResources";
    /**
     * File separator from System properties
     */
    public static final String FILE_SEP = System.getProperty("file.separator");
    /**
     * User home from System properties
     */
    public static final String USER_HOME = System.getProperty("user.home") + FILE_SEP;
    /**
     * The name of the configuration hashmap stored in application scope.
     */
    public static final String CONFIG = "appConfig";
    /**
     * Session scope attribute that holds the locale set by the user. By setting
     * this key to the same one that Struts uses, we get synchronization in
     * Struts w/o having to do extra work or have two session-level variables.
     */
    public static final String PREFERRED_LOCALE_KEY = "org.apache.struts2.action.LOCALE";
    /**
     * The request scope attribute under which an editable user form is stored
     */
    public static final String USER_KEY = "userForm";
    /**
     * The request scope attribute that holds the user list
     */
    public static final String USER_LIST = "userList";
    /**
     * The request scope attribute for indicating a newly-registered user
     */
    public static final String REGISTERED = "registered";
    /**
     * The name of the Administrator role, as specified in web.xml
     */
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    /**
     * The name of the User role, as specified in web.xml
     */
    public static final String USER_ROLE = "ROLE_USER";
    /**
     * The role for Agent hourly call report.
     */
    public static final String AGENT_HOURLY_REPORT_ROLE = "ROLE_AGENT_HRLY_RPRT";
    /**
     * Use this role to enable or disable Callbacks to a particular user.
     */
    public static final String CALLBACKS_ROLE = "ROLE_CALLBACKS";
    /**
     * ROLE that enables SMS feature from agent toolbar.
     */
    public static final String SMS_ROLE = "ROLE_SMS";
    /**
     * ROLE that enables SMS feature from agent toolbar.
     */
    public static final String AGENT_CALL_HISTORY_ROLE = "ROLE_AGENT_CALHIST";
    /**
     * The name of the user's role list, a request-scoped attribute when
     * adding/editing a user.
     */
    public static final String USER_ROLES = "userRoles";
    /**
     * The name of the available roles list, a request-scoped attribute when
     * adding/editing a user.
     */
    public static final String AVAILABLE_ROLES = "availableRoles";
    /**
     * A Jedis pool for connecting to redis
     */
    public static final String JEDIS_POOL = "jedisPool";
    public static final int NOOFRECORDS_FORAGENT = 20;
    /**
     * The name of the CSS Theme setting.
     */
    public static final String CSS_THEME = "csstheme";
    public static final String CAMPAIGN_CALLERS = "campaignCallers";
    public static final String TICKET_ROLE = "ROLE_TICKET";
    public static final String HOLD_ROLE = "ROLE_HOLD";
    public static final String OUTBOUND_ROLE = "ROLE_OUTBOUND";
    public static final String EMAILALERT_ROLE = "ROLE_EMAIL_ALERTS";
    public static final String CONFERENCE_ROLE = "ROLE_CONFERENCE";
    public static final String MUTE_ROLE = "ROLE_MUTE";
    public static final String CONSULTATIVEHOLDTRFR_ROLE = "ROLE_CONSLTHOLDTRFR";
    public static final String BLENDED_ROLE = "ROLE_BLENDED";
    public static final String CONF_AGENT = "AgentConference";
    public static final String CONF_PHONE = "PhoneConference";
    public static final String CONF_DIRECT = "NumberConference";
    public static final String READY = "READY";
    public static final String PAUSED = "PAUSED";
    public static final String CAMPAIGN_HOLDMUSICROLE = "ROLE_CAMPAIGNHOLDMUSIC";
    public static final String CAMPAIGN_HOLDMUSIC = "HOLD_FILE";
    public static final String CAMPAIGN_TRANSFERMUSIC = "TRANSFER_FILE";
    /**
     * status when we do outbound.
     */
    public static final String SENT = "sent";
    public static final String CAMP_COMPLETED = "COMPLETED";
    public static final String CALLING = "calling";
    public static final String DIALING = "Dialing";
    public static final String DIAL_ANSWERED = "answered";

    public static final String EVENT_LOGIN = "login";
    public static final String EVENT_LOGOUT = "logout";
    public static final String EVENT_RECONNECT = "reconnect";
    public static final String EVENT_RELEASE = "release";
    public static final String EVENT_PAUSE = "pause";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAIL = "fail";
    public static final String STATUS = "status";

    /**
     * Sends <code>PreviewDial</code> command to dialer.
     */
    public static final String PREVIEW_DIAL = "PreviewDial";

    public static final String SYSTEM_HANGUP = "SystemHangup";

    public static String DATE_FORMAT_STRING = "dd-MM-yyyy HH:mm:ss.S";

    public static String REDIS_DIALER_SERVER_MAPPINGS_KEY = "dialer:servers";
    public static String REDIS_UESR_DIALER_MAP = "user:dialer-server";
    public static String REDIS_AGENT_ZOHO_USER_MAPPING = "ca:agent:zoho-user:mapping";
    public static final String RETRY_JOB_KEY = "ca:ucid:retry-job";
    public static String REDIS_AGENT_ZOHO_EUROPE_USER_MAPPING = "ca:agent:zoho-europe-user:mapping";
    public static String REDIS_AGENT_ZOHO_EUROPE_CRMV2_USER_MAPPING = "ca:agent:zoho-europe-crmv2-user:mapping";
    public static String UCID_CALL_DETAILS = "ca:{0}:call-details";
    public static String PAUSE_ALERTS = "ca:pasuseAlerts";
    public static String PREVIEWDATA_AGENTID_MAP = "ca:dataId:agentId";
    public static String ENCRYPT_FIELD="ca:numbermasking:users";
}

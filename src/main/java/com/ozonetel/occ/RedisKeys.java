package com.ozonetel.occ;

/**
 *
 * @author pavanj
 */
public class RedisKeys {

    // -------------------------------------CHAT KEYS START ----------------------------
//    public static final String CLIENT_SESSION_ID_AGENT_MAP = "cachat:client:session-id:user-agent:map";
//    public static final String AGENT_CLIENT_SESSION_ID_MAP = "cachat:client:user-agent:session-id:map";
//    public static final String SESSION_CHATCUST_NAME = "cachat:clientsess:name:map";
//    public static final String CLIENT_SESSION_MONITORUCID_UCID_MAP = "cachat:clientsess:monitorucid-ucid:map";
//    public static final String CLIENT_SESSION_CLIENTID_SET = "cachat:client:session-id:wsclientid:set:{0}";
    public static final String AGENT_CLIENTID_MAP = "cachat:user-agent:wsclientid:map";
//    public static final String CLIENT_SESSION_CAMPAIGNID_DID_MAP = "cachat:clientsess:campaign-did:map";
    public static final String FB_CHAT_SENDER_ID_SET = "fb:chat:sender-ids";
    public static final String FB_CHAT_SENDER_ID_ACCESSTOKEN_MAP = "fb:chat:senderid-accesstoken:map";
    public static final String CHAT_SENDER_ID_MSGQUEUE_MAP = "chat:senderid-msgqueue:map";
    public static final String SESSION_NEXTFREEAGENT_SET = "cachat:session:freeagent:map";
    public static final String USER_SESSION_STATE_MAP = "cachat:{0}:session-id:state:map";
    public static final String USER_CHAT_SESSIONS_SET = "cachat:user:{0}:sessions";

    // ----> Place holder is client session.
    public static final String CHAT_DATA = "cachat:session:chat:{0}";

    public static final String AGENT_SESSIONID_AGENTID_MAP = "ca:agentsessionid:agentid:map";
    public static final String AGENTID_AGENT_SESSIONID_MAP = "ca:agentid:agentsessionid:map";
    public static final String AGENT_CHAT_SESSION_COUNTS = "ca:chat:session-count";

    public static final String CLIENT_SESSION_CHAT_DETAILS = "ca:session:chat-details";
    public static final String CACHAT_SESSION_LASTACTIVE_TMES = "cachat:session:active-timestamp";

    // -------------------------------------CHAT KEYS END ----------------------------
    /**
     * {0} -> campaign id Hash field for campaigns
     */
    public static final String CAMPAIGN_OBJ_STORE_HASH_FIELD = "campaign:{0}";

    public static final String CAMPAIGN_OBJ_STORE_HASH_KEY = "campaign:objects";
    /**
     * Hash key to store agent objects
     */
    public static final String AGENT_OBJ_STORE_HASH_KEY = "agent:objects";

    public static final String AGENTLOGINID_AGENTUNIQUEID_MAP_HASH_KEY = "{0}:agent-loginid:agent-uniqueid:map";

    /**
     * {0} -> username
     */
    public static final String FWPNAME_FWPID_MAP_HASH_KEY = "{0}:fwpname-fwpid:map";
    public static final String FWP_OBJ_STORE_HASH_KEY = "agent:objects";

    /**
     *
     * <blockquote><pre>{0} -> User name</pre></blockquote>
     */
    public static final String RUNNING_CAMPAIGNS_KEY = "{0}:runningcampaigns";
    /**
     * campaign type did map hash key
     */
    public static final String CAMPAIGN_TYPE_DID_ID_MAP_HASH_KEY = "camptype:did-id:map";
    /**
     * campaign type did map hash field
     * <blockquote><pre>{0} ->  Campaign type</pre>
     * <pre>{1} ->  did</pre></blockquote>
     */
    public static final String CAMPAIGN_TYPE_DID_ID_MAP_HASH_FIELD = "{0}:did:{1}";
    /**
     * {0} -> campaign id store skill id
     */
    public static final String CAMPAIGN_SKILL_IDS_SET = "campaign:{0}:skill-ids";
    /**
     * hash key which stores skill object
     *
     *
     */
    public static final String SKILL_OBJ_STORE_HASH_KEY = "skill:objects";
    /**
     * hash key where user objects are stored. field is username.
     */
    public static final String USER_OBJ_STORE_HASH_KEY = "user:objects";

    /**
     * stores name and id map for user.
     *
     * <blockquote><pre>{0} -> user</pre></blockquote>
     * hgetall demouser:skills:name-id-map
     * <ul>
     * <li>1)"renew_immediately"</li>
     * <li> 2)"222" </li>
     * <li>3)"trips" </li>
     * <li>4)"221" </li>
     * </ul>
     */
    public static final String USER_SKILLS_NAME_ID_MAP_HASH_KEY = "{0}:skills:name-id-map";

    /**
     * Redis Set key which stores skill object
     * <blockquote><pre>{0} ->  skill id</pre></blockquote>
     *
     */
    public static final String SKILL_AGNET_ID_SET_KEY = "skill:{0}:agent-ids";
    public static final String AGENT_SKILL_ID_SET_KEY = "agent:{0}:skill-ids";
    public static final String AGENT_EVENTS_HASH = "ca:agent:events";
    public static final String AGENT_EVENTIDS_CACHE = "ca:agent:event-ids";

    /**
     * callbacks ucid holding set
     */
    public static String CUSTOMER_CALLBACKS_SET = "customer:callbacks:ucids";
    public static String CUSTOMER_CHAT_CALLBACKS_SET = "customer:chat-callbacks:ucids";
    public static String CUSTOMER_CALLBACKSV2_SET = "customer:callbacksv2:ucids";
    public static String CUSTOMER_CALLBACKSV2_TODISPOSE_SET = "customer:callbacksv2:ucids:todispose";
    public static String CUSTOMER_CALLBACKSV2_TRIES = "customer:callbacksv2:tries";
    public static String CUSTOMER_CALLBACKSV3_SET = "customer:callbacksv3:ucids";
    public static String CUSTOMER_CALLBACKSV3_TODISPOSE_SET = "customer:callbacksv3:ucids:todispose";
    public static String CUSTOMER_CALLBACKSV3_TRIES = "customer:callbacksv3:tries";
    public static String CUSTOMER_CALLBACK_OLD_ARCHITECTURE = "customer:callback:old:architecture";

}

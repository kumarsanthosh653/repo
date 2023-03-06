package com.ozonetel.occ.service.chat.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.*;
import com.ozonetel.occ.service.impl.Status;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 *
 * @author ozonetel
 */
public class FacebookChatServiceImpl {

    private Logger logger = Logger.getLogger(FacebookChatServiceImpl.class);

    public StatusMessage incomeMessage(String incomeData, final String did, String user, final String apiKey, final String skill, String accessToken) {
        logger.debug("In FBService :" + incomeData);

        try {
            JsonElement jelement = new JsonParser().parse(incomeData);
            JsonObject jobject = jelement.getAsJsonObject();
//        String senderId = jobject.get("sender").getAsString();
            final String senderId = StringUtils.replace(jobject.get("sender").getAsString(), "\"", "");
            long timeStamp = jobject.get("time").getAsLong();
//        String monitorUcid = "" + timeStamp + "" + getRandomNumber(100, 999);
            String message = jobject.get("message").getAsString();
            String messageType = "text";
            String link = "";
            String phoneNumber = null;
            String channel = "";
            String callbackUrl = "";
            String customerId = "";
            String recipient = "";
            String uui = "";
            String custName = "";
            String email = "";
            if (jobject.has("messageType")) {
                messageType = jobject.get("messageType").getAsString();
                logger.debug("Message Type : " + messageType);
            }
            if (jobject.has("link")) {
                link = jobject.get("link").getAsString();
                logger.debug("Media link : " + link);
            }
            if (jobject.has("phoneNumber")) {
                phoneNumber = jobject.get("phoneNumber").getAsString();
                logger.debug("Cust phone : " + phoneNumber);
            }
            if (jobject.has("channel")) {
                channel = jobject.get("channel").getAsString();
                logger.debug("Chat from channel : " + channel);
            }
            if (jobject.has("callbackURL")) {
                callbackUrl = jobject.get("callbackURL").getAsString();
                logger.debug("kookoo callback url for chat  : " + callbackUrl);
            }
            if (jobject.has("customerId")) {
                customerId = jobject.get("customerId").getAsString();
                logger.debug("Customer id  : " + customerId);
            }
            if (jobject.has("recipient")) {
                recipient = jobject.get("recipient").getAsString();
                logger.debug("Chat came to  : " + recipient);
            }
            if (jobject.has("uui")) {
                uui = jobject.get("uui").getAsString();
                logger.debug("uui  : " + uui);
            }
            if (jobject.has("name")) {
                custName = jobject.get("name").getAsString();
                logger.debug("name  : " + custName);
            }
            if (jobject.has("email")) {
                email = jobject.get("email").getAsString();
                logger.debug("email  : " + email);
            }
            ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                    append("did", did)
                    .append("senderId", senderId)
                    .append("timestamp", timeStamp)
                    .append("user", user)
                    .append("skill", skill)
                    .append("message", message)
                    .append("channel", channel)
                    .append("name", custName)
                    .append("email", email)
                    .append("phone", phoneNumber)
                    .append("callbackUrl", callbackUrl)
                    .append("SessionIdExists?", chatService.checkChatClientSessionExists(senderId));
            logger.debug(toStringBuilder.build());

            Campaign campaign = campaignManager.getCampaignsByDid(did, "Chat");
            if (!chatService.checkChatClientSessionExists(senderId) && campaign != null) {

                chatService.setUpChatSession(senderId, uui, apiKey, campaign.getUser().getUsername(), custName, email, phoneNumber, senderId, campaign.getCampaignId(), did, skill, campaign.getUser().getId() + "" + new Date().getTime(), campaign.getUser().getId(), channel, callbackUrl, customerId, recipient);
                chatService.addSessionState(senderId, ChatStates.NEXT_FREE_AGENT);
                logger.debug("In incomeMessage skill name came : "+skill+"and user came : "+user);
                Long skillId = StringUtils.isNotEmpty(skill) && StringUtils.isNotEmpty(user) ? skillManager.getSkillsByUserAndSkillName(skill, user).getId() : null;
                logger.debug("set skillId in incomeMessage "+skillId);
                chatService.updateChatSessionWithSkill(senderId, skill,skillId);
                chatService.saveChatDetails(senderId, ChatStates.NEXT_FREE_AGENT, did, skill);
                chatService.addClientIdToSess(senderId, senderId);

                redisAgentManager.sadd(RedisKeys.FB_CHAT_SENDER_ID_SET, senderId);
                redisAgentManager.hset(RedisKeys.FB_CHAT_SENDER_ID_ACCESSTOKEN_MAP, senderId, accessToken);
                chatService.saveChatMessage(senderId, false, System.currentTimeMillis(), StringUtils.isNotBlank(link) ? (link + " | " + message) : message, messageType, null);

                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            logger.debug("apiKey--" + apiKey + " clientsessionId--" + senderId + " clientId--" + senderId + " did--" + did);
                            agentFinderService.findAgent(apiKey, senderId, senderId, senderId, did, skill);
                        }
                    }
                    ).start();
                } catch (Exception e) {
                    logger.error("exception occured while fetching agent");
                    logger.error(e.getMessage(), e);
                }
            } else if (campaign == null) {
                return new StatusMessage(Status.ERROR, "No campaign found");
            } else if (messageType.equalsIgnoreCase("userinfo")) {
                logger.debug("Updating cust details from FaceBook for : " + senderId);
                JsonObject custDetails = jobject.getAsJsonObject("custDetails");
                logger.debug(custDetails);
                String name = (!custDetails.get("custName").isJsonNull() && !custDetails.get("custName").getAsString().isEmpty()) ? custDetails.get("custName").getAsString() : null;
                String mail = (!custDetails.get("custMail").isJsonNull() && !custDetails.get("custMail").getAsString().isEmpty()) ? custDetails.get("custMail").getAsString() : null;
                String phone = (!custDetails.get("custPhone").isJsonNull() && !custDetails.get("custPhone").getAsString().isEmpty()) ? custDetails.get("custPhone").getAsString() : null;
                chatService.updateChatSessionWithUserInfo(senderId, name, mail, phone, "");
            } else if (messageType.equalsIgnoreCase("disconnect")) {
                logger.debug("disconnecting FB chat : " + senderId);
                chatService.custEndChat(senderId);
            } else {
                logger.debug("Exists:" + senderId);
                User user1 = userManager.getUserByUsername(user);
                sendMsgToAgent(senderId, message, messageType, link, user1);
            }

            return new StatusMessage(Status.SUCCESS, "Success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new StatusMessage(Status.ERROR, "Exception occured");
        }
    }

    public String sendMsgToAgent(String senderId, String message, String messageType, String mediaLink, User user) {
        logger.debug("Sender id:" + senderId + " -> message:" + message + " -> message type: " + messageType);
        Token tokenResponse = TokenFactory.createToken();
        if (messageType.equalsIgnoreCase("text")) {
            tokenResponse.setString("type", "inchatmulti");
            tokenResponse.setString("chatmsg", message);
            tokenResponse.setString("chatCustSessionId", senderId);
            tokenResponse.setLong("timestamp", System.currentTimeMillis());
        } else if (messageType.equalsIgnoreCase("status")) {
            tokenResponse.setString("type", "msgStatus");
            tokenResponse.setString("chatmsg", message);
            tokenResponse.setString("chatCustSessionId", senderId);
            tokenResponse.setLong("timestamp", System.currentTimeMillis());
        } else if (messageType.equalsIgnoreCase("location")) {
            tokenResponse.setString("type", "custLocation");
            tokenResponse.setString("coordinates", message);
            tokenResponse.setString("chatCustSessionId", senderId);
            tokenResponse.setLong("timestamp", System.currentTimeMillis());
        } else {
            tokenResponse.setString("type", "custMediaMsg");
            tokenResponse.setString("url", mediaLink);
            tokenResponse.setString("msg", message);
            tokenResponse.setString("chatCustSessionId", senderId);
            tokenResponse.setString("mediaType", messageType);
            tokenResponse.setLong("timestamp", System.currentTimeMillis());
        }
        ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(senderId);
        logger.debug("Got user agent:" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId());
        if (!StringUtils.isBlank(chatSessionDetails.getAgentId())) {
            logger.debug("Agent msg server : " + user.getUrlMap().getLocalIp());
            chatService.sendTokenToMsgServer(user.getUrlMap().getLocalIp(), senderId, tokenResponse, true);
        }
        chatService.saveChatMessage(senderId, false, System.currentTimeMillis(), StringUtils.isNotBlank(mediaLink) ? (mediaLink + " | " + message) : message, messageType, null);

        return "Success";
    }

    public int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }

    /**
     *
     * @param userId FB sender id
     * @param msg
     * @return
     */
    public String sendMsgToUser(String sessionId, String msg, String type, String fileName) {

        JsonObject session_id_obj = new JsonObject();
        session_id_obj.addProperty("id", sessionId);

        JsonObject text_obj = new JsonObject();
        text_obj.addProperty("text", msg);

        JsonObject type_obj = new JsonObject();
        type_obj.addProperty("type", type);
        
        JsonObject manObj = new JsonObject();
        manObj.add("recipient", session_id_obj);
        manObj.add("message", text_obj);
        manObj.add("type", type_obj);
        
        if (fileName != null && !fileName.isEmpty()) {
            JsonObject file_name_obj = new JsonObject();
            file_name_obj.addProperty("fileName", fileName);
            
            manObj.add("fileName", file_name_obj);
        }
        
        logger.debug(manObj.toString());

        try {

            String access_token = redisAgentManager.hget(RedisKeys.FB_CHAT_SENDER_ID_ACCESSTOKEN_MAP, "" + sessionId);

            String callbackUrl = chatService.getChatSessionDetails(sessionId).getCallbackUrl();
            URIBuilder builder = new URIBuilder(this.fbUrl);
            if (!StringUtils.isBlank(callbackUrl)) {
                builder = new URIBuilder(callbackUrl);
            }
            builder.addParameter("access_token", access_token);
            HttpClient c = new DefaultHttpClient();
            HttpPost p = new HttpPost(builder.build());
            p.setHeader("Content-Type", "application/json; charset=UTF-8");
            //logger.debug(p);
            p.setEntity(new StringEntity(manObj.toString(), "UTF-8"));
//            p.setEntity(new StringEntity(manObj.toString(),
//                    ContentType.create("application/json")));
            HttpResponse r = c.execute(p);

            BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
            String line = "";
            StringBuilder output = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                logger.debug("Line is:" + line);
                output.append(line);

            }
            logger.debug("Sending message by Ferry (⛴  ⛴  ⛴  ⛴):" + (builder.build().toString()) + " | Resp:" + output);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return "SUCCESS";
    }

//    public void setAgentManager(AgentManager agentManager) {
//        this.agentManager = agentManager;
//    }
    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setChatService(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    public void setFbUrl(String fbUrl) {
        this.fbUrl = fbUrl;
    }

    public void setCampaignManager(CampaignManager campaignManager) {
        this.campaignManager = campaignManager;
    }

//    public void setDispositionManager(DispositionManager dispositionManager) {
//        this.dispositionManager = dispositionManager;
//    }
//    public void setCallQueueManager(CallQueueManager callQueueManager) {
//        this.callQueueManager = callQueueManager;
//    }
//    public void setEventManager(EventManager eventManager) {
//        this.eventManager = eventManager;
//    }
    public void setAgentFinderService(ChatAgentFinderService agentFinderService) {
        this.agentFinderService = agentFinderService;
    }
    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }
    private CampaignManager campaignManager;
//    private AgentManager agentManager;
    private RedisAgentManager redisAgentManager;
    private UserManager userManager;
    private ChatServiceImpl chatService;
//    private DispositionManager dispositionManager;
    private String fbUrl;
//    private CallQueueManager callQueueManager;
//    private EventManager eventManager;
    private ChatAgentFinderService agentFinderService;
    private SkillManager skillManager;

}

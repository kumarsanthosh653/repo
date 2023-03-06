package com.ozonetel.occ.webapp.action;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.ozonetel.occ.model.AppProperty;
import com.ozonetel.occ.model.ChatSessionDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.model.User;

import com.ozonetel.occ.service.*;

import com.ozonetel.occ.service.chat.impl.ChatServiceImpl;
import com.ozonetel.occ.service.chat.impl.FacebookChatServiceImpl;
import com.ozonetel.occ.service.impl.Status;
import com.ozonetel.occ.service.impl.TokenServerLocalImpl;
import com.ozonetel.occ.util.AppContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author pavanj
 */
public class ChatAction extends BaseAction {

    public String incomingMessage() {
        log.debug("Got incme message:" + incomeData);
        try {
            statusMessage = facebookChatService.incomeMessage(incomeData, did, user, apiKey, skill, accessToken);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SUCCESS;
    }

    public String sendMessage() {
//--------------------------   
        log.debug("Have to send message.s");
        statusMessage = chatManager.sendMessage(senderName, recipientAgentID, recipientAgentName, recipientClientId, message, timeSent);
        log.debug(statusMessage);
        return SUCCESS;
    }

    public String broadcastMsg() {
        try {
            JSONObject jsonObject = new JSONObject(agentsJson);
            Iterator it = jsonObject.keys();
            String key;
            while (it.hasNext()) {
                key = it.next().toString();
                statusMessage = chatManager.sendMessage(senderName, jsonObject.getJSONObject(key).get("agentId").toString(), jsonObject.getJSONObject(key).get("agentName").toString(), jsonObject.getJSONObject(key).get("clientId").toString(), message, timeSent);
                log.trace(key + "|" + jsonObject.getJSONObject(key).get("agentName") + " | -> " + statusMessage);
            }
            statusMessage = new StatusMessage(Status.SUCCESS, new SimpleDateFormat("hh:mm:ss").format(new Date()));

        } catch (JSONException e) {
            statusMessage = new StatusMessage(Status.ERROR, "Can't send right now. " + new SimpleDateFormat("hh:mm:ss").format(new Date()));
            log.error(e.getMessage(), e);
        }
        return SUCCESS;
    }

    public String ivrMsg() {
        log.debug("in ivr Msg-----");
        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader reader = getRequest().getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        String data = buffer.toString();
        log.debug("Got reply from IVR --- " + data);

        try {
            JSONObject result = new JSONObject(data);
            Token tokenResponse = TokenFactory.createToken();
            final String sessionId = result.getString("sessionId");
            final String clientId = result.getString("clientId");
            final String apiKey1 = result.getString("apiKey");
            final String did1 = result.getString("did");
            String from = result.has("from") ? result.getString("from") : "kookoo";

            if (result.getString("messageType").equals("cctransfer")) {

                User usr = userManager.getUserByApiKey(apiKey1);
                if (redisAgentManager.sismember("optimal:users", StringUtils.lowerCase(usr.getUsername())) && from.equals("kookoo")) {
                    log.debug("Optimal user so forwarding to message server | sessId : " + sessionId);
                    result.put("from", "ChatServer");
                    HttpClient client = new DefaultHttpClient();
                    HttpPost p = new HttpPost("http://" + usr.getUrlMap().getLocalIp() + ":8080/OCCDV2/ivrMsg.html");
                    p.setEntity(new StringEntity(result.toString(),
                            ContentType.create("application/json")));
                    log.debug(sessionId + " Posting to " + p + " with data : " + result.toString());
                    HttpResponse response = client.execute(p);
                    log.debug(sessionId + " Response Code : " + response.getStatusLine().getStatusCode());
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer httpResponse = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        httpResponse.append(line);
                    }
                    log.debug(httpResponse);

                } else {
                    log.debug("Handling cc transfer here " + sessionId);
                    final String skill1 = result.getString("skill");
                    log.debug("In ivrMessage skill name came : "+skill1+" and user came : "+usr.getUsername());
                    final Long skillId = StringUtils.isNotEmpty(skill1) && StringUtils.isNotEmpty(usr.getUsername()) ? skillManager.getSkillsByUserAndSkillName(skill1, usr.getUsername()).getId() : null;
                    chatService.addSessionState(sessionId, ChatStates.NEXT_FREE_AGENT);
                    chatService.UpdateChatDetailsSystemEnd(sessionId, null, chatService.getChatJson(sessionId));


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            log.debug("apiKey--" + apiKey1 + " clientsessionId--" + sessionId + " clientId--" + clientId + " did--" + did1);
                            chatService.saveChatDetails(sessionId, ChatStates.NEXT_FREE_AGENT, did1, skill1);
                            log.debug("set skillId in ivrMessage "+ skillId);
                            chatService.updateChatSessionWithSkill(sessionId, skill1, skillId);
                            statusMessage = agentFinderService.findAgent(apiKey1, chatService.getChatSessionDetails(sessionId).getChatCustName(), sessionId, clientId, did1, skill1);
                        }
                    }
                    ).start();
                }

                tokenResponse.setString("resp", "wait");
                tokenResponse.setString("type", "getanagent");
                tokenResponse.setString("message", "We're trying to get an agent for you...");

            } else if (result.getString("messageType").equals("image")) {
                tokenResponse.setType("imageMsg");
                tokenResponse.setString("imageSrc", result.getString("url"));
                tokenResponse.setString("ts", result.getString("timestamp"));
                chatService.saveChatMessage(result.getString("sessionId"), true, System.currentTimeMillis(), result.getString("url"), "image", "IVR");

            } else if (result.getString("messageType").equals("disconnect")) {
                tokenResponse.setType("endChat");
                tokenResponse.setString("agentClosed", "timeout");
                chatService.sendTokenToSession(sessionId, tokenResponse);
                // ----> Write to db
                chatService.UpdateChatDetailsSystemEnd(sessionId, "System:ICR", chatService.getChatJson(sessionId));
                // -----> Remove all cleintid set mapped to session id.
                chatService.tearDownChatSession(sessionId);
            } else if (result.getString("messageType").equals("userinfo")) {
                tokenResponse.setType("collectUserInfo");
                if (result.has("name")) {
                    tokenResponse.setString("name", result.getString("name"));
                }
                if (result.has("email")) {
                    tokenResponse.setString("email", result.getString("email"));
                }
                if (result.has("phone")) {
                    tokenResponse.setString("phone", result.getString("phone"));
                }
                if (result.has("extra_data")) {
                    tokenResponse.setString("extra_data", result.getString("extra_data"));
                }
                chatService.saveChatMessage(result.getString("sessionId"), true, System.currentTimeMillis(), new Gson().toJson(tokenResponse.getMap()), "collectUserInfo", "IVR");
            } else if (result.getString("messageType").equals("interactive")) {
                tokenResponse.setType("interactive");
                tokenResponse.setString("msg", result.getString("message"));
            } else {
                tokenResponse.setType("chatMsg");
                tokenResponse.setString("agent", "IVR");
                tokenResponse.setString("ts", result.getString("timestamp"));

                if (result.has("message")) {
                    tokenResponse.setString("msg", result.getString("message"));
                }

                chatService.saveChatMessage(result.getString("sessionId"), true, System.currentTimeMillis(), result.getString("message"), "text", "IVR");
            }
            if (tokenServer == null) {
                ApplicationContext webApplicationContext = AppContext.getApplicationContext();
                tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
            }
            if (from.equals("kookoo")) {
                log.debug("Sending response to client " + tokenResponse);
                chatService.sendTokenToSession(result.getString("sessionId"), tokenResponse);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return SUCCESS;
    }

    public String saveImage() {
//        try {
        log.debug(file);
        log.debug("Trying to save file " + fileFileName + " content type : " + fileContentType + "| session id:" + getSession().getId());
        log.debug("file description --- " + description);

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        log.debug("preparing the host information for sftp.");
        try {

            String SFTPHOST = appProperty.getChatSftpHost();
            int SFTPPORT = 22;
            String SFTPUSER = appProperty.getChatSftpHostUsername();
            String SFTPPASS = appProperty.getChatSftpHostPassword();
            //TODO : Change to /var/www/html in production
            //String SFTPWORKINGDIR = "/var/www/"+appProperty.getChatSftpHostWorkingDir();
            String SFTPWORKINGDIR = "/var/www/html/" + appProperty.getChatSftpHostWorkingDir();
            log.debug("SFTP WORKING DIR : " + SFTPWORKINGDIR);
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            log.debug("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            log.debug("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            File f = new File(SFTPWORKINGDIR, System.currentTimeMillis() + "_" + fileFileName);
            channelSftp.put(new FileInputStream(file), f.getName());
            log.debug("File transfered successfully to host." + f.getParent() + "--" + f.getParentFile() + "--" + f.getAbsolutePath());
            fileSavePath = "http://" + SFTPHOST + "/" + appProperty.getChatSftpHostWorkingDir() + "/" + f.getName();
            log.debug("file save path --->" + fileSavePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("Exception found while tranfer the response.");
        } finally {

            channelSftp.exit();
            log.debug("sftp Channel exited.");
            channel.disconnect();
            log.debug("Channel disconnected.");
            session.disconnect();
            log.debug("Host Session disconnected.");
        }
        return SUCCESS;
    }

    public String chatMsgSender() {
        statusMessage = new StatusMessage(Status.SUCCESS, "message sent successfully");
        log.debug("Have to send new chat msg-----");
        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader reader = getRequest().getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String data = buffer.toString();
            log.debug("Got new msg with data ---> " + data);

//        try {
            JSONObject result = new JSONObject(data);
            String sessionId = result.getString("sessionId");
            //preparing token response
            JSONObject tokenRespJson = new JSONObject(result.getString("tokenResponseJson"));
            Iterator it = tokenRespJson.keys();
            Token tokenResponse = TokenFactory.createToken();
            while (it.hasNext()) {
                String key = it.next().toString();
                tokenResponse.setString(key, tokenRespJson.get(key).toString());
            }
            log.debug("Prepared token response :--> " + tokenResponse);

            //log.debug(result.getString("tokenResponse"));
            if (result.getBoolean("toAgent")) {
                log.debug("Sending msg to AGENT...Session : " + sessionId);
                ChatSessionDetails chatSessionDetails = chatService.getChatSessionDetails(sessionId);
                log.debug("Got user agent:" + chatSessionDetails.getCaUserName() + "|" + chatSessionDetails.getAgentId());
                String agentWsId = redisAgentManager.getAgentWsId(chatSessionDetails.getCaUserName(), chatSessionDetails.getAgentId());
                log.debug("Agent WSID : " + agentWsId);
                chatService.sendTokenToAgent(tokenResponse, agentWsId);
            } else {
                log.debug("Sending msg to CLIENT...Session : " + sessionId);
                chatService.sendTokenToSession(sessionId, tokenResponse);
            }
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }

        return SUCCESS;
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setRecipientAgentID(String recipientAgentID) {
        this.recipientAgentID = recipientAgentID;
    }

    public void setRecipientAgentName(String recipientAgentName) {
        this.recipientAgentName = recipientAgentName;
    }

    public void setRecipientClientId(String recipientClientId) {
        this.recipientClientId = recipientClientId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public void setAgentsJson(String agentsJson) {
        this.agentsJson = agentsJson;
    }

    public void setFacebookChatService(FacebookChatServiceImpl facebookChatService) {
        this.facebookChatService = facebookChatService;
    }

    public void setIncomeData(String incomeData) {
        this.incomeData = incomeData;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setChatService(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    public void setAgentFinderService(ChatAgentFinderService agentFinderService) {
        this.agentFinderService = agentFinderService;
    }

    public void setRedisAgentManager(RedisAgentManager redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public AppProperty getAppProperty() {
        return appProperty;
    }

    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }
    private String did;
    private String user;
    private String apiKey;
    private String skill;
    private String accessToken;

    private String incomeData;
    private FacebookChatServiceImpl facebookChatService;
    private String senderName;
    private String recipientAgentID;
    private String recipientAgentName;
    private String recipientClientId;
    private String message;
    private String timeSent;
    private ChatManager chatManager;
    private StatusMessage statusMessage;
    private String agentsJson;
    private ChatServiceImpl chatService;
    private static TokenServerLocalImpl tokenServer;
    private ChatAgentFinderService agentFinderService;
    private RedisAgentManager redisAgentManager;
    private File file;
    private String description;
    private String fileFileName;
    private String fileContentType;
    private String fileSavePath;
    private AppProperty appProperty;
    private UserManager userManager;
    private SkillManager skillManager;
}

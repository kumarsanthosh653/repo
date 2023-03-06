package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.AgentAlertsManager;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.util.AppContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author pavanj
 */
public class AgentAlertsManagerImpl implements AgentAlertsManager {

    private static Logger logger = Logger.getLogger(AgentAlertsManagerImpl.class);

    private void initTokenServer() {
        // tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
        tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
    }

    @Override
    public boolean alertAgent(String user, String agentId, String type, String message) {
        initTokenServer();

        try {
//            Agent agent = agentManager.getAgentByAgentId(user, agentId)̧;
            Agent agent = null;
            if (agent != null) {
                return alertAgent(user, agentId, agent.getClientId(), type, message);
            } else {
                logger.error("Got agent as null for agentId:" + agentId);
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    @Override
    public boolean alertAgentWithKey(String user, String agentId, String type, String notificationKey, String message,Boolean encryptField) {
        initTokenServer();
        logger.debug("alerting agent with params notificationKey : "+notificationKey+" message: "+message+" and encrypt flag : "+encryptField);
        try {
            Agent agent = agentManager.getAgentByAgentIdV2(user, agentId);
            if (agent != null && StringUtils.isNotBlank(agent.getClientId())) {
                Token messageToken = TokenFactory.createToken();
                messageToken.setType("agentAlert");
                messageToken.setString("alertType", type);
                messageToken.setString("msg", message);
                messageToken.setString("agentId", agentId);
                messageToken.setString("alertKey", notificationKey);
                messageToken.setString("user", user);
                if(encryptField){
                    logger.debug("setting encryptfield as true");
                    messageToken.setString("encryptField","true");
                }

                if (StringUtils.isNotBlank(agent.getClientId())) {
                    WebSocketConnector connector = tokenServer.getConnector(agent.getClientId());

                    if (connector == null) {
                        logger.warn("Connection for agent " + agentId + " with ClientId:" + agent.getClientId() + "not found.....");
                    } else {
                        tokenServer.sendToken(connector, messageToken);
                        return true;
                    }
                }
            } else {
                logger.error("Unable to send the message:" + message + " to the agent:" + agent);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return false;
    }

    @Override
    public boolean alertAgent(String clientId, String type, String message) {
        initTokenServer();

        try {
            Token messageToken = TokenFactory.createToken();
            messageToken.setType("agentAlert");
            messageToken.setString("alertType", type);
            messageToken.setString("msg", message);

            if (StringUtils.isNotBlank(clientId)) {

                WebSocketConnector connector = tokenServer.getConnector(clientId);

                if (connector == null) {
                    logger.warn("No client with ClientId:" + clientId + " found.....");
                    return false;
                } else {
                    tokenServer.sendToken(connector, messageToken);
                    logger.info("Message " + message + " sent to agent with client id:" + clientId);
                    System.out.println("Message " + message + " sent to agent with client id:" + clientId);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    @Override
    public boolean alertAgent(String user, String agentId, String clientId, String type, String message) {

        initTokenServer();

        try {
            Token messageToken = TokenFactory.createToken();
            messageToken.setType("agentAlert");
            messageToken.setString("alertType", type);
            messageToken.setString("msg", message);
            messageToken.setString("agentId", agentId);
            messageToken.setString("user", user);

            if (StringUtils.isNotBlank(clientId)) {
              
                logger.debug("Token server:"+tokenServer);
                System.out.println("Token server:"+tokenServer);
                WebSocketConnector connector = tokenServer.getConnector(clientId);

                if (connector == null) {
                    logger.warn("Connection for agent " + agentId + " with ClientId:" + clientId + "not found.....");
                    return false;
                } else {
                    tokenServer.sendToken(connector, messageToken);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;

    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }
    private AgentManager agentManager;
    private TokenServerLocalImpl tokenServer;
}

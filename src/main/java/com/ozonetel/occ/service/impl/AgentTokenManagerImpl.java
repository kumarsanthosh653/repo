package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.AgentTokenManager;
import com.ozonetel.occ.util.AppContext;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author pavanj
 */
public class AgentTokenManagerImpl implements AgentTokenManager {

    private void initTokenServer() {
        if (tokenServer == null) {
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }
    }

    @Override
    public boolean sendTokenToAgent(String user, String agentLoginId, Token tokentToSend) {
        initTokenServer();
        return sendTokenToAgent(agentManager.getAgentByAgentIdV2(user, agentLoginId), tokentToSend);
    }

    @Override
    public boolean sendTokenToAgent(Agent agent, Token tokentToSend) {
        initTokenServer();
        if (agent != null && StringUtils.isNotBlank(agent.getClientId()) && tokenServer.getConnector(agent.getClientId()) != null) {
            tokenServer.sendToken(tokenServer.getConnector(agent.getClientId()), tokentToSend);
            return true;
        }

        return false;
    }

    @Override
    public boolean sendTokenToAgent(String user, String agentLoginId, String tokenType, Map<String, String> params) {
        initTokenServer();
        return sendTokenToAgent(agentManager.getAgentByAgentIdV2(user, agentLoginId), tokenType, params);
    }

    @Override
    public boolean sendTokenToAgent(Agent agent, String tokenType, Map<String, String> params) {
        initTokenServer();
        logger.debug("==> Sending token to agent:" + agent + " | Token Type:" + tokenType + " | Details:" + params + " | Token server:" + tokenServer);

        if (agent != null && StringUtils.isNotBlank(agent.getClientId()) && tokenServer.getConnector(agent.getClientId()) != null) {
            Token token = TokenFactory.createToken();
            token.setType(tokenType);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                token.setString(entry.getKey(), entry.getValue());
            }
            tokenServer.sendToken(tokenServer.getConnector(agent.getClientId()), token);
        }

        return false;
    }

//    public void setTokenServer(TokenServer tokenServer) {
//        this.tokenServer = tokenServer;
//    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public TokenServerLocalImpl tokenServer;
    private AgentManager agentManager;
    private static Logger logger = Logger.getLogger(AgentTokenManagerImpl.class);

}

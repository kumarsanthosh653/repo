package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AgentEvent;
import com.ozonetel.occ.service.AgentEventNotifier;
import com.ozonetel.occ.util.AppContext;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author pavanj
 */
public class AgentEventNotifierImpl implements AgentEventNotifier {

    private void initTokenServer() {
        if (tokenServer == null) {
            //tokenServer = (TokenServer) JWebSocketFactory.getServer("ts0");
            ApplicationContext webApplicationContext = AppContext.getApplicationContext();
            tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
        }
    }

    @Override
    public boolean notifyEvent(Agent agent, String event, AgentEvent agentEvent) {
        initTokenServer();

        Token eventToken = TokenFactory.createToken();
        eventToken.setType(event);
        eventToken.setString("details", new Gson().toJson(agentEvent));

        WebSocketConnector connector = tokenServer.getConnector(agent.getClientId());
        if (connector == null) {
            logger.error("Can't send event:" + event + " | Event details:" + eventToken + " to agent:" + agent + " | Reason: websocket connector is " + connector);
        } else {
            tokenServer.sendToken(connector, eventToken);
            logger.error("Sent event:" + event + " | Event details:" + eventToken + " to agent:" + agent);
        }
        return false;
    }

    private static final Logger logger = Logger.getLogger(AgentEventNotifierImpl.class);
    private TokenServerLocalImpl tokenServer;
}

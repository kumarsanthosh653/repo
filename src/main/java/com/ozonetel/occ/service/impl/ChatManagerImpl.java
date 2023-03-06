package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.ChatManager;
import com.ozonetel.occ.util.AppContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author pavanj
 */
public class ChatManagerImpl implements ChatManager {

    private void init() {
        ApplicationContext webApplicationContext = AppContext.getApplicationContext();
        tokenServer = (TokenServerLocalImpl) webApplicationContext.getBean("tokenServer");
    }

    @Override
    public StatusMessage sendMessage(String senderName, String recipientAgentID, String recipientAgentName, String recipientClientId, String message, String timeSent) {
        init();
        StatusMessage statusMessage = new StatusMessage(Status.ERROR, "");
        WebSocketConnector connector = tokenServer.getConnector(recipientClientId);

        if (connector == null) {
            statusMessage.setMessage("Unable to connect to client. " + new SimpleDateFormat("hh:mm:ss").format(new Date()));
        } else {
            Token messageToken = TokenFactory.createToken();
            messageToken.setType("chatMessage");
            messageToken.setString("Sender", senderName);
            messageToken.setString("rAgentID", recipientAgentID);
            messageToken.setString("rAgentName", recipientAgentName);
            messageToken.setString("rClientId", recipientClientId);
            messageToken.setString("msg", message);
            messageToken.setString("timeSent", timeSent);
            tokenServer.sendToken(connector, messageToken);
            statusMessage.setStatus(Status.SUCCESS);
            statusMessage.setMessage(new SimpleDateFormat("hh:mm:ss").format(new Date()));
        }
        logger.debug(" (" + senderName + " → " + recipientAgentID + ") : " + message + " [" + (statusMessage.getStatus() == Status.SUCCESS ? "" : "") + "] | Reason:" + statusMessage);
        return statusMessage;
    }

    private TokenServerLocalImpl tokenServer;
    private static Logger logger = Logger.getLogger(ChatManagerImpl.class);

}

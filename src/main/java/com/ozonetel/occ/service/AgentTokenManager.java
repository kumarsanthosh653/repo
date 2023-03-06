package com.ozonetel.occ.service;

import com.ozonetel.occ.model.Agent;
import java.util.Map;
import org.jwebsocket.token.Token;

/**
 *
 * @author pavanj
 */
public interface AgentTokenManager {

    /**
     * Sends token to agent with agent login id <code>agnetLoginId</code> of
     * user <code>user</code>
     *
     * @param user
     * @param agentLoginId
     * @param tokentToSend
     * @return true if token is sent otherwise false.
     */
    public boolean sendTokenToAgent(String user, String agentLoginId, Token tokentToSend);

    /**
     * Sends token to agnet <code>agent</code>
     *
     * @param agent
     * @param tokentToSend
     * @return
     */
    public boolean sendTokenToAgent(Agent agent, Token tokentToSend);

    /**
     *
     * @param user
     * @param agentLoginId
     * @param tokenType
     * @param params
     * @return
     */
    public boolean sendTokenToAgent(String user, String agentLoginId, String tokenType, Map<String, String> params);

    /**
     *
     * @param agent
     * @param tokenType
     * @param params
     * @return
     */
    public boolean sendTokenToAgent(Agent agent, String tokenType, Map<String, String> params);
}

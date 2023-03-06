package com.ozonetel.occ.service;

/**
 *
 * @author pavanj
 */
public interface AgentAlertsManager {

    /**
     * Sends the
     * <code>message</code> to the agent with
     * <code>agentId</code>
     *
     * @param user
     * @param agentId
     * @param type
     * @param message
     * @return true if message is sent otherwise false.
     */
    public boolean alertAgent(String user, String agentId, String type, String message);

    /**
     * Sends the
     * <code>message</code> to the agent with
     * <code>agentId</code>.Use this if you already know the jWebSocket client
     * ID.
     *
     * @param user
     * @param agentId
     * @param clientId
     * @param type
     * @param message
     * @return true if message is sent otherwise false.
     */
    public boolean alertAgent(String user, String agentId, String clientId, String type, String message);

    /**
     *
     * @param clientId
     * @param type
     * @param message
     * @return
     */
    public boolean alertAgent(String clientId, String type, String message);

    /**
     * Alerts the agent with the
     * <code>message</code> and alert can be identified with the
     * <code>notificationKey</code> on client side.
     *
     * @param user
     * @param agentId
     * @param type
     * @param notificationKey
     * @param message
     * @return
     */
        public boolean alertAgentWithKey(String user, String agentId, String type, String notificationKey, String message,Boolean encryptField);
}

package com.ozonetel.occ.service;

import com.ozonetel.occ.service.command.response.AgentToolbarResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 * @param <T>
 */
public abstract class AbstractAgentToolbarCommand<T extends AgentToolbarResponse> implements AgentToolbarCommand<T> {

    protected String username;
    protected String agentId;
    protected Long agentUniqueId;
    protected Logger logger = Logger.getLogger(this.getClass());

    public AbstractAgentToolbarCommand(String username, Long agentUniqueId,String agentId) {
        this.username = StringUtils.trim(username);
        this.agentId = StringUtils.trim(agentId);
        this.agentUniqueId = agentUniqueId;
    }
    public AbstractAgentToolbarCommand(String username,String agentId) {
        this.username = StringUtils.trim(username);
        this.agentId = StringUtils.trim(agentId);
    }
    
    public AbstractAgentToolbarCommand(String username,Long _agentUniqId) {
        this.username = StringUtils.trim(username);
        this.agentUniqueId = _agentUniqId;
    }

}

package com.ozonetel.occ.service;

import com.ozonetel.occ.service.command.response.AgentToolbarResponse;

/**
 *
 * @author pavanj
 * @param <T>
 * 
 */
public interface AgentToolbarCommand<T extends AgentToolbarResponse>{

    public final String COMMAND_STATUS="status";
    public T execute();
}

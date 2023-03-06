package com.ozonetel.occ.service;

import com.ozonetel.occ.model.MiniTicket;
import com.ozonetel.occ.model.StatusMessage;
import java.util.List;

/**
 *
 * @author pavanj
 */
public interface TicketManager {

    /**
     * *
     * Opens new ticket
     *
     * @param ticketID
     * @param username
     * @param agent_id
     * @param callerID
     * @param ucid
     * @param monitorUCID
     * @param comment
     * @param desc
     * @return
     */
    public StatusMessage openTicket(Long ticketID, String username, String agent_id, String callerID, Long ucid, Long monitorUCID, String comment, String desc);

    /**
     * updates existing ticket
     *
     * @param ticketID
     * @param username
     * @param agent_id
     * @param callerID
     * @param ucid
     * @param monitorUCID
     * @param comment
     * @param status
     * @return
     */
    public StatusMessage updateTicket(Long ticketID, String username, String agent_id, String callerID, Long ucid, Long monitorUCID, String comment, String status);

    /**
     *
     * @param user
     * @param ticketId
     * @return MiniTicket object
     * @see {@link  com.ozonetel.occ.model.MiniTicket}.
     */
    public MiniTicket getTicketHistory(String user, Long ticketId);

    /**
     * 
     * @param user
     * @param customerNumber
     * @return 
     */
    public List<MiniTicket> getTicketsByCustomerNumber(String user, String customerNumber);
}

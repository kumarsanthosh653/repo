/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao.hibernate;

import com.ozonetel.occ.dao.TicketDao;
import com.ozonetel.occ.model.Ticket;

/**
 *
 * @author pavanj
 */
public class TicketDaoHibernate extends GenericDaoHibernate<Ticket, Long> implements TicketDao {

    public TicketDaoHibernate() {
        super(Ticket.class);
    }
}

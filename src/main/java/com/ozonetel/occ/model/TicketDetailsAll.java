/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.util.List;

/**
 *
 * @author pavanj
 */
public class TicketDetailsAll {

    private String status;
    private String desc;
    private List<TicketDetails> ticketDetais;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TicketDetails> getTicketDetais() {
        return ticketDetais;
    }

    public void setTicketDetais(List<TicketDetails> ticketDetais) {
        this.ticketDetais = ticketDetais;
    }

    @Override
    public String toString() {
        return "TicketDetailsAll{" + "status=" + status + ", desc=" + desc + ", ticketDetais=" + ticketDetais + '}';
    }
}

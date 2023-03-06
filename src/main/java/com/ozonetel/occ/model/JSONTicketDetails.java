/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.util.List;
import java.util.Set;

/**
 *
 * @author pavanj
 */
public class JSONTicketDetails {

    private String status;
    private String desc;
    private Set<TicketDetails> details;

    public Set<TicketDetails> getDetails() {
        return details;
    }

    public void setDetails(Set<TicketDetails> details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "JSONTicketDetails{" + "status=" + status + ", desc=" + desc + ", details=" + details + '}';
    }
}

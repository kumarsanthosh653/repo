/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "ticket_report")
public class TicketDetails extends BaseObject implements Serializable, Comparable<TicketDetails> {

    private Long id;
    private transient MiniTicket ticket;
    private Long ucid;
    private Long monitorUcid;
    private Agent agent;
    private Date createDate;
    private String comment;
    private String phoneNumber;
    private String status;

    public TicketDetails() {
    }

    public TicketDetails(MiniTicket ticket, Long ucid, Long monitorUcid, Agent agent, Date createDate, String comment) {
        this.ticket = ticket;
        this.ucid = ucid;
        this.monitorUcid = monitorUcid;
        this.agent = agent;
        this.createDate = createDate;
        this.comment = comment;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "phonenumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @ManyToOne(targetEntity = Agent.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "agentid", insertable = true, updatable = true)
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Column(name = "comments")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(name = "createddate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "monitor_ucid")
    public Long getMonitorUcid() {
        return monitorUcid;
    }

    public void setMonitorUcid(Long monitorUcid) {
        this.monitorUcid = monitorUcid;
    }

    @ManyToOne(targetEntity = MiniTicket.class)
    @JoinColumn(name = "ticket_id")
    public MiniTicket getTicket() {
        return ticket;
    }

    public void setTicket(MiniTicket ticket) {
        this.ticket = ticket;
    }

    @Column(name = "ucid")
    public Long getUcid() {
        return ucid;
    }

    public void setUcid(Long ucid) {
        this.ucid = ucid;
    }

    public int compareTo(TicketDetails o) {
        Date thisDate = this.createDate;
        Date anotherDate = o.getCreateDate();
        return -(thisDate.compareTo(anotherDate));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TicketDetails other = (TicketDetails) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "TicketDetails{" + "id=" + id + ", ticket=" + ticket + ", ucid=" + ucid + ", monitorUcid=" + monitorUcid + ", agent=" + agent + ", createDate=" + createDate + ", comment=" + comment + ", phoneNumber=" + phoneNumber + ", status=" + status + '}';
    }
}

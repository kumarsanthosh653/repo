package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "ticket")
public class MiniTicket extends BaseObject implements Serializable {

    private Long id;
    private Long ticketID;
    private User user;
    private Date startDate;
    private String status;
    private Agent agent;
    private Date closeDate;
    private String phoneNumber;
    private String tktDesc;
    private Set<TicketDetails> ticketDetails = new LinkedHashSet<TicketDetails>();

    public MiniTicket() {
    }

    public MiniTicket(Long ticketID, User user, Date startDate, String status, Agent agent, String phoneNumber, String desc) {
        this.ticketID = ticketID;
        this.user = user;
        this.startDate = startDate;
        this.status = status;
        this.agent = agent;
        this.phoneNumber = phoneNumber;
        this.tktDesc = desc;
    }

    @OneToMany(targetEntity = TicketDetails.class, fetch = FetchType.EAGER, mappedBy = "ticket")
    public Set<TicketDetails> getTicketDetails() {
        return ticketDetails;
    }

    public void setTicketDetails(Set<TicketDetails> ticketDetails) {
        this.ticketDetails = ticketDetails;
    }

    @ManyToOne(targetEntity = Agent.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "agent_id", insertable = true, updatable = true)
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Column(name = "closedate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    @Column(name = "ticket_desc")
    public String getTktDesc() {
        return tktDesc;
    }

    public void setTktDesc(String tktDesc) {
        this.tktDesc = tktDesc;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "phonenumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "startdate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "ticket_id")
    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    @ManyToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "MiniTicket{" + "id=" + id + ", ticketID=" + ticketID + ", user=" + user + ", startDate=" + startDate + ", status=" + status + ", agent=" + agent + ", closeDate=" + closeDate + ", phoneNumber=" + phoneNumber + ", tktDesc=" + tktDesc + ", ticketDetails=" + ticketDetails + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MiniTicket other = (MiniTicket) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.ticketID != other.ticketID && (this.ticketID == null || !this.ticketID.equals(other.ticketID))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 23 * hash + (this.ticketID != null ? this.ticketID.hashCode() : 0);
        return hash;
    }
}

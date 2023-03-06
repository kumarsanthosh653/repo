/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {

    private Long id;
    private Long ticket;
    private Set<Report> reports = new HashSet<Report>();
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ticket_no")
    public Long getTicket() {
        return ticket;
    }

    public void setTicket(Long ticket) {
        this.ticket = ticket;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ticket_report",
    joinColumns = {
        @JoinColumn(name = "ticket_id")},
    inverseJoinColumns =
    @JoinColumn(name = "ucid"))
    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

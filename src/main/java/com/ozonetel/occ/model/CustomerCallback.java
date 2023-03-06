/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author pavanj
 */
@Entity
@Table(name = "customer_callback")
public class CustomerCallback implements Serializable {

    private Long id;
    /**
     * Agent monitor UCID
     */
    private BigInteger ucid;
    private String username;
    private boolean sent;
    private Date dateModified;
    private String callbackUrl;
    private Integer maxTreis;

    public CustomerCallback() {
    }

    public CustomerCallback(BigInteger ucid, String username, Date dateModified, String callbackUrl,Integer maxTreis) {
        this.ucid = ucid;
        this.username = username;
        this.dateModified = dateModified;
        this.callbackUrl = callbackUrl;
        this.maxTreis = maxTreis;
    }

    public CustomerCallback(BigInteger ucid, String username, boolean sent) {
        this.ucid = ucid;
        this.username = username;
        this.sent = sent;
    }

    @Column(name = "callback_url")
    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @Column(name = "date_modified")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    @Column(name = "max_tries")
    public Integer getMaxTreis() {
        return maxTreis;
    }

    public void setMaxTreis(Integer maxTreis) {
        this.maxTreis = maxTreis;
    }

    @Column(name = "ucid")
    public BigInteger getUcid() {
        return ucid;
    }

    /**
     *
     * @param ucid Agent monitor UCID
     */
    public void setUcid(BigInteger ucid) {
        this.ucid = ucid;
    }

    @Column(name = "user")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "sent")
    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 37 * hash + (this.ucid != null ? this.ucid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomerCallback other = (CustomerCallback) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.ucid != other.ucid && (this.ucid == null || !this.ucid.equals(other.ucid))) {
            return false;
        }
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CustomerCallback{" + "id=" + id + ", ucid=" + ucid + ", username=" + username + ", sent=" + sent + '}';
    }
}

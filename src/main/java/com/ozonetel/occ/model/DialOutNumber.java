/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Rajesh
 */
@Entity
@Table(name = "dialout_number")
public class DialOutNumber extends BaseObject {

    private Long id;
    private String dialOutNumber;
    private String dialOutName;
    private Boolean sip;
    private User user;

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "is_SIP")
    public Boolean isSip() {
        return sip;
    }

    public void setSip(Boolean sip) {
        this.sip = sip;
    }

    /**
     * @return the dialOutNumber
     */
    @Column(name = "dialout_number")
    public String getDialOutNumber() {
        return dialOutNumber;
    }

    /**
     * @param dialOutNumber the dialOutNumber to set
     */
    public void setDialOutNumber(String dialOutNumber) {
        this.dialOutNumber = dialOutNumber;
    }

    /**
     * @return the dialOutName
     */
    @Column(name = "dialout_name")
    public String getDialOutName() {
        return dialOutName;
    }

    /**
     * @param dialOutName the dialOutName to set
     */
    public void setDialOutName(String dialOutName) {
        this.dialOutName = dialOutName;
    }

    /**
     * @return the user
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.dialOutNumber);
        hash = 79 * hash + Objects.hashCode(this.dialOutName);
        hash = 79 * hash + Objects.hashCode(this.sip);
        hash = 79 * hash + Objects.hashCode(this.user);
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
        final DialOutNumber other = (DialOutNumber) obj;
        if (!Objects.equals(this.dialOutNumber, other.dialOutNumber)) {
            return false;
        }
        if (!Objects.equals(this.dialOutName, other.dialOutName)) {
            return false;
        }
        if (!Objects.equals(this.sip, other.sip)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DialOutNumber{" + "id=" + id + ", dialOutNumber=" + dialOutNumber + ", dialOutName=" + dialOutName + ", sip=" + sip + ", user=" + user + '}';
    }

}

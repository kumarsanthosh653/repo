/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="FWP_FreeStatus")
public class FwpFreeStatus extends BaseObject implements Serializable{
    
    private Long id;
    private String phoneNumber;
    private Long lastSelected;
    private Integer priority;
    private User user;
    private Agent.State state;
    private Long directCallCount;
    private boolean sip;

    /**
     * @return the id
     */
    @Id
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

    /**
     * @return the phoneNumber
     */
    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the lastSelected
     */
    @Column(name = "lastSelected")
    public Long getLastSelected() {
        return lastSelected;
    }

    /**
     * @param lastSelected the lastSelected to set
     */
    public void setLastSelected(Long lastSelected) {
        this.lastSelected = lastSelected;
    }

    /**
     * @return the priority
     */
    @Column(name = "priority")
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return the user
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the state
     */
    @Enumerated
    @Column(name = "state")
    public Agent.State getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(Agent.State state) {
        this.state = state;
    }

    /**
     * @return the directCallCount
     */
    @Column(name = "DirectCallCounter")
    public Long getDirectCallCount() {
        return directCallCount;
    }

    /**
     * @param directCallCount the directCallCount to set
     */
    public void setDirectCallCount(Long directCallCount) {
        this.directCallCount = directCallCount;
    }

    /**
     * @return the sip
     */
    @Column(name = "is_SIP")
    public boolean isSip() {
        return sip;
    }

    /**
     * @param sip the sip to set
     */
    public void setSip(boolean sip) {
        this.sip = sip;
    }
    
    public String toString() {
        //return "FWPFreeStatus{" + "id=" + id + ", phoneNumber=" + phoneNumber + ",user=" + user + ", state=" + state '}'";
        return "FwpNumber{" + "id=" + id + ", phoneNumber=" + phoneNumber + ", user=" + user + ", state=" + state + '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FwpFreeStatus)) {
            return false;
        }

        final FwpFreeStatus FwpFreeStatus = (FwpFreeStatus) o;

        return !(phoneNumber != null ? !phoneNumber.equals(FwpFreeStatus.phoneNumber) : FwpFreeStatus.phoneNumber != null);
    }

    @Override
    public int hashCode() {
        return 0;
    }
    
}
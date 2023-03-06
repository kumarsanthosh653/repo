/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author root
 */
@Entity
@Table(name = "fwp_numbers")
@NamedQueries({
    @NamedQuery(name = "isFwpValid", query = "select f from FwpNumber f where f.phoneNumber =:phoneNumber and f.userId=:userId"),
    //    @NamedQuery(name = "isPhoneNumberValid", query = "select f from FwpNumber f where :phoneNumber like '%'||f.phoneNumber and f.user.id=:userId"),
    @NamedQuery(name = "isPhoneNumberValid", query = "select f from FwpNumber f where f.phoneNumber like '%'||:phoneNumber and f.userId=:userId")
//    @NamedQuery(name = "checkAgentByUser", query = "select f from FwpNumber f where f.phoneNumber =:phoneNumber and f.user.username=:userName")
})
public class FwpNumber extends BaseObject implements Cloneable {

    private static final long serialVersionUID = 2398467008929349886L;
    private Long id;
    private String phoneName;
    private String phoneNumber;
    private String contact;
    private Integer priority;
    private Long nextFlag;
    private Long lastSelected;
    private Long agent;
    private Long userId;
    private Agent.State state;
    private boolean sms;
    private String callStatus;
    private Long directCallCount;
    private Long callExceptions;
    private Long ucid;
    private boolean sip;

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
    public boolean isSip() {
        return sip;
    }

    public void setSip(boolean sip) {
        this.sip = sip;
    }

    /**
     * @return the ucid
     */
    @Column(name = "ucid")
    public Long getUcid() {
        return ucid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUcid(Long ucid) {
        this.ucid = ucid;
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
     * @return the contact
     */
    @Column(name = "contact")
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
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
     * @return the nextFlag
     */
    @Column(name = "nextFlag")
    public Long getNextFlag() {
        return nextFlag;
    }

    /**
     * @param nextFlag the nextFlag to set
     */
    public void setNextFlag(Long nextFlag) {
        this.nextFlag = nextFlag;
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

//    @Override
//    public String toString() {
//        return this.phoneName + "~" + this.phoneNumber;
//    }
    @Override
    public String toString() {
        return "FwpNumber{" + "id=" + id + ", phoneName=" + phoneName + ", phoneNumber=" + phoneNumber + ", sms=" + sms + ", sip=" + sip + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FwpNumber)) {
            return false;
        }

        final FwpNumber fwpNumber = (FwpNumber) o;

        return !(phoneNumber != null ? !phoneNumber.equals(fwpNumber.phoneNumber) : fwpNumber.phoneNumber != null);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * @return the agent
     */
//    @ManyToOne(targetEntity = Agent.class)
//    @JoinColumn(name = "agent_id", insertable = true, updatable = true)
    @Column(name = "agent_id")
    public Long getAgent() {
        return agent;
    }

    /**
     * @param agent the agent to set
     */
    public void setAgent(Long agent) {
        this.agent = agent;
    }

    /**
     * @return the user
     */
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    /**
     * @param user the user to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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
     * @return the sms
     */
    @Column(name = "sms")
    public boolean isSms() {
        return sms;
    }

    /**
     * @param sms the sms to set
     */
    public void setSms(boolean sms) {
        this.sms = sms;
    }

    /**
     * @return the callStatus
     */
    @Column(name = "call_status")
    public String getCallStatus() {
        return callStatus;
    }

    /**
     * @param callStatus the callStatus to set
     */
    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    @Column(name = "phone_name")
    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    @Column(name = "DirectCallCounter")
    public Long getDirectCallCount() {
        return directCallCount;
    }

    public void setDirectCallCount(Long directCallCount) {
//        if (directCallCount != null && directCallCount >= 0) {// we should not allow the callcounter to set less than Zero
        this.directCallCount = directCallCount;
//        }
    }

    /**
     * @return the callExceptions
     */
    @Column(name = "call_exeception")
    public Long getCallExceptions() {
        return callExceptions;
    }

    /**
     * @param callExceptions the callExceptions to set
     */
    public void setCallExceptions(Long callExceptions) {
        this.callExceptions = callExceptions;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

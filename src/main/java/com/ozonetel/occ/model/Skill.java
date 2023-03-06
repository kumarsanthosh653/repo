/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Rajesh
 */
@Entity
@Table(name = "skill")
public class Skill extends BaseObject implements Serializable {
    private static final long serialVersionUID = 8622279941389085439L;

    private Long id;
    private String skillName;
    private Location location;
    private String skillDetail1;
    private String skillDetail2;
    private String skillDetail3;
    private User user;
    private Campaign campaign;
    private Set<Agent> agents = new HashSet<Agent>();
    private Set<FwpNumber> fwpNumbers = new HashSet<FwpNumber>();
    private String fallBackRule;
//    private String dialoutNumber;
    private DialOutNumber dialOutNumber;
    private Integer queueSize;
    private Integer queueTimeOut;
    private Skill queueSkillTransfer;
    private String queueIvrTransfer;
    private String fallbackDetails;
    private int recOn;// whether the Recording should start on agent or 0 - total 1- converation
    private boolean dropAction;
    private boolean active;
    private Long priority;

    public Skill() {
    }

    public Skill(Long id, String skillName) {
        this.id = id;
        this.skillName = skillName;
    }

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

    @Column(name = "Fallback_Details")
    public String getFallbackDetails() {
        return fallbackDetails;
    }

    public void setFallbackDetails(String fallbackDetails) {
        this.fallbackDetails = fallbackDetails;
    }

    /**
     * @return the skillName
     */
    @Column(name = "skillName")
    public String getSkillName() {
        return skillName;
    }

    /**
     * @param skillName the skillName to set
     */
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @ManyToOne(targetEntity = Campaign.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "campaign_id", nullable = true)
    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "skill_agents",
            joinColumns = {
                @JoinColumn(name = "skill_id")},
            inverseJoinColumns
            = @JoinColumn(name = "agent_id"))
    public Set<Agent> getAgents() {
        return agents;
    }

    public void setAgents(Set<Agent> agents) {
        this.agents = agents;
    }

    /**
     * Convert skill agents to LabelValue objects for convenience.
     *
     * @return a list of LabelValue objects with agent information
     */
    @Transient
    public List<LabelValue> getAgentList() {
        List<LabelValue> skillAgents = new ArrayList<LabelValue>();

        if (this.agents != null) {
            for (Agent agent : agents) {
                System.out.println(agents);
                // convert the skill's agents to LabelValue Objects
                skillAgents.add(new LabelValue(agent.getAgentName(), agent.getAgentId()));
            }
        }

        return skillAgents;
    }

    /**
     *
     * @return a list of Agents which are idle and by Priority and by
     * LastSelected
     */
    @Transient
    public List<Agent> getIdleAgentsByPriAndLastSel() {
        Set<Agent> agents = this.agents;
        return null;
    }

    public void addAgent(Agent agent) {
        getAgents().add(agent);
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the fallBackRule
     */
    @Column(name = "fallBack_Rule")
    public String getFallBackRule() {
        return fallBackRule;
    }

    /**
     * @param fallBackRule the fallBackRule to set
     */
    public void setFallBackRule(String fallBackRule) {
        this.fallBackRule = fallBackRule;
    }

    /**
     * @return the dialoutNumber
     */
    @ManyToOne(targetEntity = DialOutNumber.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "don_id")
    public DialOutNumber getDialOutNumber() {
        return dialOutNumber;
    }

    public void setDialOutNumber(DialOutNumber dialOutNumber) {
        this.dialOutNumber = dialOutNumber;
    }

    @Override
    public String toString() {
//        throw new UnsupportedOperationException("Not supported yet.");
        return this.skillName;
    }

    @Override
    public boolean equals(Object o) {
//        throw new UnsupportedOperationException("Not supported yet.");
        if (this == o) {
            return true;
        }
        if (!(o instanceof Skill)) {
            return false;
        }

        final Skill skill = (Skill) o;

        return !(id != null ? id != skill.getId() : skill.getId() != null);

    }

    @Override
    public int hashCode() {
//        throw new UnsupportedOperationException("Not supported yet.");
        return 0;
    }

    /**
     * @return the skillDetail1
     */
    @Column(name = "skill_detail1")
    public String getSkillDetail1() {
        return skillDetail1;
    }

    /**
     * @param skillDetail1 the skillDetail1 to set
     */
    public void setSkillDetail1(String skillDetail1) {
        this.skillDetail1 = skillDetail1;
    }

    @Column(name = "Priority")
    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    /**
     * @return the skillDetail2
     */
    @Column(name = "skill_detail2")
    public String getSkillDetail2() {
        return skillDetail2;
    }

    /**
     * @param skillDetail2 the skillDetail2 to set
     */
    public void setSkillDetail2(String skillDetail2) {
        this.skillDetail2 = skillDetail2;
    }

    /**
     * @return the skillDetail3
     */
    @Column(name = "skill_detail3")
    public String getSkillDetail3() {
        return skillDetail3;
    }

    /**
     * @param skillDetail3 the skillDetail3 to set
     */
    public void setSkillDetail3(String skillDetail3) {
        this.skillDetail3 = skillDetail3;
    }

    /**
     * @return the location
     */
    @ManyToOne(targetEntity = Location.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the fwpNumbers
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "skill_fwpNumbers",
            joinColumns = {
                @JoinColumn(name = "skill_id")},
            inverseJoinColumns
            = @JoinColumn(name = "fwp_id"))
    public Set<FwpNumber> getFwpNumbers() {
        return fwpNumbers;
    }

    /**
     * @param fwpNumbers the fwpNumbers to set
     */
    public void setFwpNumbers(Set<FwpNumber> fwpNumbers) {
        this.fwpNumbers = fwpNumbers;
    }

    /**
     * Convert skill fwpNumbers to LabelValue objects for convenience.
     *
     * @return a list of LabelValue objects with Fwp Number information
     */
    @Transient
    public List<LabelValue> getFwpNumberList() {
        List<LabelValue> skillFwpNumbers = new ArrayList<LabelValue>();

        if (this.fwpNumbers != null) {
            for (FwpNumber fwpNumber : fwpNumbers) {
//                System.out.println(agents);
                // convert the skill's Fwp Numbers to LabelValue Objects
                skillFwpNumbers.add(new LabelValue(fwpNumber.getPhoneNumber(), fwpNumber.getId().toString()));
            }
        }

        return skillFwpNumbers;
    }

    public void addFwpNumber(FwpNumber fwpNumber) {
        getFwpNumbers().add(fwpNumber);
    }

    /**
     * @return the queueSize
     */
    @Column(name = "QueueSize")
    public Integer getQueueSize() {
        return queueSize;
    }

    /**
     * @param queueSize the queueSize to set
     */
    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    /**
     * @return the queueTimeOut
     */
    @Column(name = "QueueTimeout")
    public Integer getQueueTimeOut() {
        return queueTimeOut;
    }

    /**
     * @param queueTimeOut the queueTimeOut to set
     */
    public void setQueueTimeOut(Integer queueTimeOut) {
        this.queueTimeOut = queueTimeOut;
    }

    /**
     * @return the queueSkillTransfer
     */
    @ManyToOne(targetEntity = Skill.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "QueueSkillTransfer", insertable = true, updatable = true)
    public Skill getQueueSkillTransfer() {
        return queueSkillTransfer;
    }

    /**
     * @param queueSkillTransfer the queueSkillTransfer to set
     */
    public void setQueueSkillTransfer(Skill queueSkillTransfer) {
        this.queueSkillTransfer = queueSkillTransfer;
    }

    /**
     * @return the queueIvrTransfer
     */
    @Column(name = "QueueAppTransferURL")
    public String getQueueIvrTransfer() {
        return queueIvrTransfer;
    }

    /**
     * @param queueIvrTransfer the queueIvrTransfer to set
     */
    public void setQueueIvrTransfer(String queueIvrTransfer) {
        this.queueIvrTransfer = queueIvrTransfer;
    }

    @Column(name = "Rec_On")
    public int getRecOn() {
        return recOn;
    }

    public void setRecOn(int recOn) {
        this.recOn = recOn;
    }

    /**
     * @return the dropAction
     */
    @Column(name = "DropAction")
    public boolean isDropAction() {
        return dropAction;
    }

    /**
     * @param dropAction the dropAction to set
     */
    public void setDropAction(boolean dropAction) {
        this.dropAction = dropAction;
    }

    /**
     * @return the active
     */
    @Column(name = "isactive")
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}

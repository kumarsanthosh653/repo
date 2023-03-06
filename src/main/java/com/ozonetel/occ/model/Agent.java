package com.ozonetel.occ.model;

import java.io.Serializable;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "agent")
@NamedQueries({
    @NamedQuery(name = "agentsByUser", query = "select a from Agent a where a.userId =:username"),
    @NamedQuery(name = "getAgentsByUcid", query = "select a from Agent a where a.active is true  and a.ucid =:ucid"),
    @NamedQuery(name = "isAgentLoginByPhoneNumber", query = "select a from Agent a where a.clientId != null and a.active is true  and  a.phoneNumber = :phoneNumber and a.userId=:userId"),
    @NamedQuery(name = "isAgentLoginByPhoneNumberId", query = "select a from Agent a where a.clientId != null and a.active is true  and  a.fwpNumber.id = :FwpId and a.userId=:userId")    
})
public class Agent extends BaseObject implements Serializable, Comparable<Agent> {          

    private static final long serialVersionUID = 4011809977558163592L;
    private Long id;
    private String agentId;
    private String phoneNumber;
    private long idleSince;
    private long idleTime;
    private boolean loggedIn;
    private State state;
    private String contact;
//    private transient Campaign campaign;
    private Long campaignId;
    private String channelName;
    private String password;
    private String agentName;
    private String clientId;
//    private User user;
    private Long userId;
    private Long priority;
    private Long ucid;//For Agent Selection when for CustomerType
    private Long nextFlag;
    private Long lastSelected;
//    private transient Skill skill;
    private String since;
    private String stateReason;
    private boolean hunting;
    private FwpNumber fwpNumber;
    private boolean active;
    private String type; //to know call is  outbound , inbound or manual in system monitor
    private Mode mode;
    private String callStatus;
    private String agentData;
    private Long directCallCount;
    private Long callExceptions;
    private String email;
    private boolean sipLogin;
    private String campignName;
    private String skillName;
    private Integer sessionCount;
    private Date holdStartTime;

    public Agent() {
    }

    public Agent(Long id, String agentId) {
        this.id = id;
        this.agentId = agentId;
    }

    public Agent(Long id, String agentId, String agentName) {
        this.id = id;
        this.agentId = agentId;
        this.agentName = agentName;
    }

    public Agent(Long id, String agentId, String agentName, boolean isSipLogin) {
        this.id = id;
        this.agentId = agentId;
        this.agentName = agentName;
        this.sipLogin = isSipLogin;
    }

    @Column(name = "campign_name")
    public String getCampignName() {
        return campignName;
    }

    public void setCampignName(String campignName) {
        this.campignName = campignName;
    }

    @Column(name = "skillName")
    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @Transient
    public boolean isSipLogin() {
        return sipLogin;
    }

    public void setSipLogin(boolean sipLogin) {
        this.sipLogin = sipLogin;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "agent_data")
    public String getAgentData() {
        return agentData;
    }

    public void setAgentData(String agentData) {
        this.agentData = agentData;
    }

    @Column(name = "idle_time")
    public long getIdleTime() {
        return idleTime;
    }

    /**
     * @return the mode
     */
    @Enumerated
    @Column(name = "mode")
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public static enum Mode {
        //0 - Inbound
        //1 - Preview
        //2 - Manual
        //3 - Progressive
        //4 - Blended
        //8 - Chat -> matching with agent_data(event) table data_id

        INBOUND, PREVIEW, MANUAL, PROGRESSIVE, BLENDED, DUMMY, DUMMY1, DUMMY2, CHAT;

    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }

    @Column(name = "agent_name")
    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Column(name = "contact")
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Column(name = "campaign_id")
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * @return the clientId
     */
    @Column(name = "clientId")
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the user
     */
//    @Transient
//    public User getUser() {
//        return user;
//    }
//
//    /**
//     * @param user the user to set
//     */
//    public void setUser(User user) {
//        this.user = user;
//    }
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
    public Long getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Long priority) {
        this.priority = priority;
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
     * @return the since
     */
    @Transient
    public String getSince() {
        return since;
    }

    /**
     * @param since the since to set
     */
    public void setSince(String since) {
        this.since = since;
    }

    /**
     * @return the stateReason
     */
    @Column(name = "state_reason")
    public String getStateReason() {
        return stateReason;
    }

    /**
     * @param stateReason the stateReason to set
     */
    public void setStateReason(String stateReason) {
        this.stateReason = stateReason;
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
     * @return the hunting
     */
    @Column(name = "is_hunting")
    public boolean isHunting() {
        return hunting;
    }

    /**
     * @param hunting the hunting to set
     */
    public void setHunting(boolean hunting) {
        this.hunting = hunting;
    }

    /**
     * @return the fwpNumber
     */
    /**
     * @return the user
     */
    @ManyToOne(targetEntity = FwpNumber.class)
    @JoinColumn(name = "fwp_id", insertable = true, updatable = true)
    public FwpNumber getFwpNumber() {
        return fwpNumber;
    }

    /**
     * @param fwpNumber the fwpNumber to set
     */
    public void setFwpNumber(FwpNumber fwpNumber) {
        this.fwpNumber = fwpNumber;
    }

    /**
     * @return the active
     */
    @Column(name = "is_active")
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the type
     */
    @Column(name = "type")
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public static enum State {

        /**
         * 0- logged out 1- login and aux state 2- idle 3- busy 4- acw
         * 5-Exception
         */
        LOGOUT, AUX, IDLE, BUSY, ACW, EXCEPTION;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "idle_since")
    public long getIdleSince() {
        return idleSince;
    }

    public void setIdleSince(long idleSince) {
        this.idleSince = idleSince;
    }

    public Agent(String agentId) {
        this.agentId = agentId;
    }

    @Column(name = "agent_id")
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Enumerated
    @Column(name = "state")
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Column(name = "logged_in")
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "campaign_id", insertable = false, updatable = false)
//    public Campaign getCampaign() {
//        return campaign;
//    }
//
//    public void setCampaign(Campaign campaign) {
//        this.campaign = campaign;
//    }
    @Column(name = "sess_count")
    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
//    public Skill getSkill() {
//        return skill;
//    }
//
//    public void setSkill(Skill skill) {
//        this.skill = skill;
//    }
    @Column(name = "HoldStartTime")
    public Date getHoldStartTime() {
        return holdStartTime;

    }

    public void setHoldStartTime(Date holdStartTime) {
        this.holdStartTime = holdStartTime;
    }

    public int compareTo(Agent o) {
        return compare(this, o);
    }

    public static int compare(Agent thisAgent, Agent otherAgent) {
        int idleComparedValue = thisAgent.lastSelected < otherAgent.lastSelected ? -1 : (thisAgent.lastSelected == otherAgent.lastSelected ? 0 : 1);
        return thisAgent.priority < otherAgent.priority ? -1 : (thisAgent.priority == otherAgent.priority ? idleComparedValue : 1);
    }

    public String toShortString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Agent", this.agentName)
                .append("AgentId", this.agentId).toString();
    }

    /**
     * Displays the agent state info.
     *
     * @return
     */
    public String toInfoString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Id", this.id)
                .append("Agent", this.agentName)
                .append("AgentId", this.agentId)
                .append("NextFlag", this.nextFlag)
                .append("Priority", this.priority)
                .append("State", this.state)
                .append("Mode", this.mode)
                .append("LastSelected", this.lastSelected)
                .append("DirectCallCount", this.directCallCount)
                .append("Active", this.isActive())
                .toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Id", this.id)
                .append("Agent", this.agentName)
                .append("AgentId", this.agentId)
                .append("User", this.userId)
                .append("State", this.state)
                .append("Mode", this.mode)
                .append("Fwp", this.fwpNumber)
                .append("ClientID", clientId)
                .append("SipLogin", this.fwpNumber != null ? this.fwpNumber.isSip() : this.sipLogin)
                .append("MonitorUCID", this.ucid)
                .toString();
//               return this.agentName + "-" +( this.state==null?"AUX":this.state.name());
    }

    public String toLongString() {
        return "Agent{" + "id=" + id + ", agentId=" + agentId + ", phoneNumber=" + phoneNumber + ", idleSince=" + idleSince + ", idleTime=" + idleTime + ", loggedIn=" + loggedIn + ", state=" + state + ", agentName=" + agentName + ", clientId=" + clientId + ", user=" + userId + ", priority=" + priority + ", nextFlag=" + nextFlag + ", lastSelected=" + lastSelected + ", stateReason=" + stateReason + ", fwpNumber=" + fwpNumber + ", active=" + active + ", type=" + type + ", mode=" + mode + ", directCallCount=" + directCallCount + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }

        final Agent agent = (Agent) o;

        return !(agentName != null ? !agentName.equals(agent.agentName) : agent.agentName != null);

    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Column(name = "call_status")
    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    @Column(name = "DirectCallCounter")
    public Long getDirectCallCount() {
        return directCallCount;
    }

    public void setDirectCallCount(Long directCallCount) {
//        if (directCallCount != null && directCallCount >= 0) {// we should not allow the callcounter to set  less than Zero
        this.directCallCount = directCallCount;
        /*} else {
            this.directCallCount = 0L;
        }*/
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
}

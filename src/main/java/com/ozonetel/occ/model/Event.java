package com.ozonetel.occ.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Event.java NarayanaBabu.Nalluri Date : Aug 14, 2010 Email :
 * nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */
@Entity
@Table(name = "agent_data")
/*@NamedQueries({
    //    @NamedQuery(name = "getEventbyDataId", query = "select d from Event d where d.agent.agentId =:agentId and data.data_id =:dataId"),
    @NamedQuery(name = "getEventByAgentId", query = "select d from Event d where d.agent.id =:agentId order by d.eventId desc limit 0,1"),
    @NamedQuery(name = "getlastLoginEvent", query = "select d from Event d where d.agent.id =:agentId and d.event =:eventName order by d.eventId desc limit 0,1")
})*/
public class Event extends BaseObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long eventId;
    private Date startTime;
    private Date endTime;
    private String event;
    private String eventData;
    private Long ucid;
//    private Data data;
    //@JsonIgnore
    private Long userId;
    private AgentMode mode;
    private Long agentId;
    private String miscDetails;

    public enum AgentMode {

        MODE(0),
        MANUAL(1),
        INBOUND(2),
        PROGRESSIVE(3),
        PREVIEW(4),
        BLENDED(5),
        EXCEPTION(6),
        DUMMY(7),//I hate 7
        CHAT(8);
        private int value;

        AgentMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "eventid")
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Enumerated
    @Column(name = "data_id")
    public AgentMode getMode() {
        return mode;
    }

    public void setMode(AgentMode mode) {
        this.mode = mode;
    }

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "event")
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Column(name = "event_data")
    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    /**
     * @return the endTime
     */
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "agentid")
    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    @Column(name = "MiscDetails")
    public String getMiscDetails() {
        return miscDetails;
    }

    public void setMiscDetails(String miscDetails) {
        this.miscDetails = miscDetails;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the ucid
     */
    @Column(name = "ucid")
    public Long getUcid() {
        return ucid;
    }

    /**
     * @param ucid the ucid to set
     */
    public void setUcid(Long ucid) {
        this.ucid = ucid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((eventData == null) ? 0 : eventData.hashCode());
        result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;

        if (event == null) {
            if (other.event != null) {
                return false;
            }
        } else if (!event.equals(other.event)) {
            return false;
        }
        if (eventData == null) {
            if (other.eventData != null) {
                return false;
            }
        } else if (!eventData.equals(other.eventData)) {
            return false;
        }
        if (eventId == null) {
            if (other.eventId != null) {
                return false;
            }
        } else if (!eventId.equals(other.eventId)) {
            return false;
        }
        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Event{" + "eventId=" + eventId + ", startTime=" + startTime + ", endTime=" + endTime + ", event=" + event + ", eventData=" + eventData + ", ucid=" + ucid + ", userId=" + userId + ", mode=" + mode + ", agentId=" + agentId + ", miscDetails=" + miscDetails + '}';
    }

    /// --> You made some history, and it was costly. So ignoring you... :/
//    /*
//     * @return the data
//     */
//    @ManyToOne(targetEntity = Data.class)
//    @JoinColumn(name = "data_id")
//    public Data getData() {
//        return data;
//    }
//
//    /**
//     * @param data the data to set
//     */
//    public void setData(Data data) {
//        this.data = data;
//    }
//    /**
//     * @return the user
//     */
//    @ManyToOne(targetEntity = User.class)
//    @JoinColumn(name = "user_id", insertable = true, updatable = false)
//    public User getUser() {
//        return user;
//    }
//    /**
//     * @param user the user to set
//     */
//    public void setUser(User user) {
//        this.user = user;
//    }
// @ManyToOne(targetEntity = Agent.class, fetch = FetchType.LAZY)
//    @JoinColumn(name = "agentid", referencedColumnName = "id", insertable = true, updatable = false)
//    public Agent getAgent() {
//        return agent;
//    }
//
//    public void setAgent(Agent agent) {
//        this.agent = agent;
//    }
}

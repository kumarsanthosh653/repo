package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Campaign.java
 *
 * @author NBabu Date : Oct 28, 2009 Email : nbabu@ozonetel.com,
 * nb.nalluri@yahoo.com
 */
@Entity
@Table(name = "campaign")
@NamedQueries({
    @NamedQuery(name = "campaignsByPosition", query = "select c from Campaign c where c.position like :position"),
    @NamedQuery(name = "campaignsByInBound", query = "select c from Campaign c where c.campaignType like 'InBound' and  c.isDelete = False"),
    @NamedQuery(name = "campaignsByTypeAndUserAndStatus", query = "select c from Campaign c where c.campaignType in (:type) and  c.isDelete = :status and c.user.username = :username"),
    @NamedQuery(name = "getCampaignStatusByDid", query = "select c from Campaign c where c.dId like :did"),
    @NamedQuery(name = "getCampaignByDidAndUser", query = "select c from Campaign c where c.dId = :did and c.user.id = :userId and c.isDelete = :status"),
    @NamedQuery(name = "getCampaignByUser", query = "select c from Campaign c where c.user.username = :username")
})
public class Campaign extends BaseObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4480149079847955270L;
    private Long campaignId;
    private String campignName;
    private Date playTime;
    private String ruleNac; // Not Answered Calls
    private Integer ruleNot; // No Of Tries;
    private Boolean acwNac; // Try AlternativeNumber;
    private Integer currentTrail; // current Trail Number
    private Integer destIter;
    private String screenPopUrl;
    private Date nextRun;
    private String Status;
    private String timeStart;
    private String campaignType;
    private String position;
    private Integer sla;
    private Boolean isDelete = false;
    private transient Set<Agent> agents = new HashSet<Agent>();
    private transient Set<Skill> skills = new HashSet<Skill>();
    private transient Set<Disposition> dispositions = new HashSet<Disposition>();
    private User user;
    private String dId;
    //private boolean agentWise;
    private String fallBackRule;
    private String dialoutNumber;
    private String script;
    private boolean offLineMode;
    private transient Set<PreviewData> previewDatas = new HashSet<PreviewData>();
    private transient PreviewDataMap previewDataMap;
    private POPAT popUrlAt;
    private boolean dndCheck;
    private String callPrefix;
    private boolean allowedManual;
    private DialMethod dialMethod;
    private boolean agentWise;
    private IvrFlow IvrFlow;
    private String dispositionType;
    private String callbackUrl;
    private Integer dialInterval;
    private boolean a2a_calling;
    private String fallbackDid;

//    private Set<Role> roles = new HashSet<Role>();
//    private Set<AppAudioFiles> appAudioFiles = new HashSet<AppAudioFiles>();
/*
     public void setRoles(Set<Role> roles) {
     this.roles = roles;
     }

     @ManyToMany(fetch = FetchType.LAZY)
     @JoinTable(name = "user_role",
     joinColumns = {
     @JoinColumn(name = "user_id")},
     inverseJoinColumns
     = @JoinColumn(name = "role_id"))
     public Set<Role> getRoles() {
     return roles;
     }
     */
    /*
     @ManyToMany(fetch = FetchType.EAGER)
     @JoinTable(name = "campaign_audios",
     joinColumns = {
     @JoinColumn(name = "campaign_id")},
     inverseJoinColumns
     = @JoinColumn(name = "audio_id"))
     public Set<AppAudioFiles> getAppAudioFiles() {
     return appAudioFiles;
     }

     public void setAppAudioFiles(Set<AppAudioFiles> appAudioFiles) {
     this.appAudioFiles = appAudioFiles;
     }
     */
    public static enum DialMethod {

        Nonagentwise,
        Agentwise,
        Skillwise
    }

    @Column(name = "dial_interval")
    public Integer getDialInterval() {
        return dialInterval;
    }

    public void setDialInterval(Integer dialInterval) {
        this.dialInterval = dialInterval;
    }

    @Enumerated
    @Column(name = "type_id")
    public DialMethod getDialMethod() {
        return dialMethod;
    }

    public void setDialMethod(DialMethod dialMethod) {
        this.dialMethod = dialMethod;
    }

    @Column(name = "DND_enable")
    public boolean isDndCheck() {
        return dndCheck;
    }

    public void setDndCheck(boolean dndCheck) {
        this.dndCheck = dndCheck;
    }

    @ManyToOne(targetEntity = PreviewDataMap.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id")
    public PreviewDataMap getPreviewDataMap() {
        return previewDataMap;
    }

    public void setPreviewDataMap(PreviewDataMap previewDataMap) {
        this.previewDataMap = previewDataMap;
    }

    @Column(name = "campaign_type")
    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "campaign_id")
    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    @Column(name = "campign_name", updatable = false)
    public String getCampignName() {
        return campignName;
    }

    public void setCampignName(String campignName) {
        this.campignName = campignName;
    }

    @Column(name = "play_time")
    public Date getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Date playTime) {
        this.playTime = playTime;
    }

    @Column(name = "rule_nac")
    public String getRuleNac() {
        return ruleNac;
    }

    public void setRuleNac(String ruleNac) {
        this.ruleNac = ruleNac;
    }

    @Column(name = "call_prefix")
    public String getCallPrefix() {
        return callPrefix;
    }

    public void setCallPrefix(String callPrefix) {
        this.callPrefix = callPrefix;
    }

    @Column(name = "rule_not")
    public Integer getRuleNot() {
        return ruleNot;
    }

    public void setRuleNot(Integer ruleNot) {
        this.ruleNot = ruleNot;
    }

    @Column(name = "rule_tan")
    public Boolean getAcwNac() {
        return acwNac;
    }

    public void setAcwNac(Boolean acwNac) {
        this.acwNac = acwNac;
    }

    @Column(name = "sla")
    public Integer getSla() {
        return sla;
    }

    public void setSla(Integer sla) {
        this.sla = sla;
    }

    @Column(name = "is_delete")
    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        if (isDelete == null) {
            isDelete = false;
        }
        this.isDelete = isDelete;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    public Set<Agent> getAgents() {
        return agents;
    }

    public void setAgents(Set<Agent> agents) {
        this.agents = agents;
    }

    public void addAgent(Agent agent) {
        getAgents().add(agent);
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "campaign_skills",
            joinColumns = {
                @JoinColumn(name = "campaign_id")},
            inverseJoinColumns
            = @JoinColumn(name = "skill_id"))
    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public void addSkill(Skill skill) {
        getSkills().add(skill);
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        //int result = super.hashCode();
        int result = 1;
        result = prime * result + ((Status == null) ? 0 : Status.hashCode());
        result = prime * result
                + ((campaignId == null) ? 0 : campaignId.hashCode());
        result = prime * result
                + ((campaignType == null) ? 0 : campaignType.hashCode());
        result = prime * result
                + ((campignName == null) ? 0 : campignName.hashCode());
        result = prime * result
                + ((currentTrail == null) ? 0 : currentTrail.hashCode());
        result = prime * result
                + ((destIter == null) ? 0 : destIter.hashCode());
        result = prime * result
                + ((screenPopUrl == null) ? 0 : screenPopUrl.hashCode());
        result = prime * result + ((nextRun == null) ? 0 : nextRun.hashCode());
        result = prime * result
                + ((playTime == null) ? 0 : playTime.hashCode());
        result = prime * result
                + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((ruleNac == null) ? 0 : ruleNac.hashCode());
        result = prime * result + ((ruleNot == null) ? 0 : ruleNot.hashCode());
        result = prime * result + ((acwNac == null) ? 0 : acwNac.hashCode());
        result = prime * result + ((sla == null) ? 0 : sla.hashCode());
        result = prime * result
                + ((timeStart == null) ? 0 : timeStart.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        Campaign other = (Campaign) obj;
        if (Status == null) {
            if (other.Status != null) {
                return false;
            }
        } else if (!Status.equals(other.Status)) {
            return false;
        }

        if (campaignId == null) {
            if (other.campaignId != null) {
                return false;
            }
        } else if (!campaignId.equals(other.campaignId)) {
            return false;
        }
        if (campaignType == null) {
            if (other.campaignType != null) {
                return false;
            }
        } else if (!campaignType.equals(other.campaignType)) {
            return false;
        }
        if (campignName == null) {
            if (other.campignName != null) {
                return false;
            }
        } else if (!campignName.equals(other.campignName)) {
            return false;
        }
        if (currentTrail == null) {
            if (other.currentTrail != null) {
                return false;
            }
        } else if (!currentTrail.equals(other.currentTrail)) {
            return false;
        }
        if (destIter == null) {
            if (other.destIter != null) {
                return false;
            }
        } else if (!destIter.equals(other.destIter)) {
            return false;
        }
        if (screenPopUrl == null) {
            if (other.screenPopUrl != null) {
                return false;
            }
        } else if (!screenPopUrl.equals(other.screenPopUrl)) {
            return false;
        }
        if (nextRun == null) {
            if (other.nextRun != null) {
                return false;
            }
        } else if (!nextRun.equals(other.nextRun)) {
            return false;
        }
        if (playTime == null) {
            if (other.playTime != null) {
                return false;
            }
        } else if (!playTime.equals(other.playTime)) {
            return false;
        }
        if (position == null) {
            if (other.position != null) {
                return false;
            }
        } else if (!position.equals(other.position)) {
            return false;
        }
        if (ruleNac == null) {
            if (other.ruleNac != null) {
                return false;
            }
        } else if (!ruleNac.equals(other.ruleNac)) {
            return false;
        }
        if (ruleNot == null) {
            if (other.ruleNot != null) {
                return false;
            }
        } else if (!ruleNot.equals(other.ruleNot)) {
            return false;
        }
        if (acwNac == null) {
            if (other.acwNac != null) {
                return false;
            }
        } else if (!acwNac.equals(other.acwNac)) {
            return false;
        }
        if (sla == null) {
            if (other.sla != null) {
                return false;
            }
        } else if (!sla.equals(other.sla)) {
            return false;
        }
        if (timeStart == null) {
            if (other.timeStart != null) {
                return false;
            }
        } else if (!timeStart.equals(other.timeStart)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Campaign{" + "campaignId=" + campaignId + ", campignName=" + campignName + ", currentTrail=" + currentTrail + ", Status=" + Status + ", campaignType=" + campaignType + ", dId=" + dId + ", DialMethod=" + dialMethod + ", offLineMode=" + offLineMode + ", dndCheck=" + dndCheck + '}';
    }

    @Column(name = "frequency")
    public String getScreenPopUrl() {
        return screenPopUrl;
    }

    public void setScreenPopUrl(String screenPopUrl) {
        this.screenPopUrl = screenPopUrl;
    }

    @Column(name = "next_run")
    public Date getNextRun() {
        return nextRun;
    }

    public void setNextRun(Date nextRun) {
        this.nextRun = nextRun;
    }

    @Column(name = "status")
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Column(name = "time_start")
    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    @Column(name = "position")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Transient
    public Integer getDestIter() {
        return destIter;
    }

    public void setDestIter(Integer destIter) {
        this.destIter = destIter;
    }

    @Column(name = "current_trail")
    public Integer getCurrentTrail() {
        return currentTrail;
    }

    public void setCurrentTrail(Integer currentTrail) {
        this.currentTrail = currentTrail;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Rel_Camp_Disp",
            joinColumns = {
                @JoinColumn(name = "Campaign_id")},
            inverseJoinColumns
            = @JoinColumn(name = "Disp_id"))
    public Set<Disposition> getDispositions() {
        return dispositions;
    }

    public void setDispositions(Set<Disposition> dispositions) {
        this.dispositions = dispositions;
    }

    public void addDisposition(Disposition disposition) {
        getDispositions().add(disposition);
    }

    /**
     * @return the dId
     */
    @Column(name = "dId")
    public String getdId() {
        return dId;
    }

    /**
     * @param dId the dId to set
     */
    public void setdId(String dId) {
        this.dId = dId;
    }

    /**
     * @return the agentWise
     */
    @Column(name = "agentWise")
    public boolean isAgentWise() {
        return agentWise;
    }

    /**
     * @param agentWise the agentWise to set
     */
    public void setAgentWise(boolean agentWise) {
        this.agentWise = agentWise;
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
    @Column(name = "dialout_Number")
    public String getDialoutNumber() {
        return dialoutNumber;
    }

    /**
     * @param dialoutNumber the dialoutNumber to set
     */
    public void setDialoutNumber(String dialoutNumber) {
        this.dialoutNumber = dialoutNumber;
    }

    /**
     * @return the script
     */
    @Column(name = "script")
    public String getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the offLineMode
     */
    @Column(name = "offLine_mode")
    public boolean isOffLineMode() {
        return offLineMode;
    }

    /**
     * @param offLineMode the offLineMode to set
     */
    public void setOffLineMode(boolean offLineMode) {
        this.offLineMode = offLineMode;
    }

    /**
     * @return the previewDatas
     */
    @OneToMany(targetEntity = PreviewData.class, fetch = FetchType.LAZY, mappedBy = "campaign")
    public Set<PreviewData> getPreviewDatas() {
        return previewDatas;
    }

    /**
     * @param previewDatas the previewDatas to set
     */
    public void setPreviewDatas(Set<PreviewData> previewDatas) {
        this.previewDatas = previewDatas;
    }

    /**
     * @return the popUrlAt
     */
    @Enumerated
    @Column(name = "hit_pop_url")
    public POPAT getPopUrlAt() {
        return popUrlAt;
    }

    /**
     * @param popUrlAt the popUrlAt to set
     */
    public void setPopUrlAt(POPAT popUrlAt) {
        this.popUrlAt = popUrlAt;
    }

    /**
     * @return the allowedManual
     */
    @Column(name = "manual_dialing")
    public boolean isAllowedManual() {
        return allowedManual;
    }

    public void setAllowedManual(boolean allowedManual) {
        this.allowedManual = allowedManual;
    }

    /**
     * @return the IvrFlow
     */
    @ManyToOne(targetEntity = IvrFlow.class)
    @JoinColumn(name = "IVRFlowID", insertable = true, updatable = true)
    public IvrFlow getIvrFlow() {
        return IvrFlow;
    }

    /**
     * @param IvrFlow the IvrFlow to set
     */
    public void setIvrFlow(IvrFlow IvrFlow) {
        this.IvrFlow = IvrFlow;
    }

    @Column(name = "Disposition_Type")
    public String getDispositionType() {
        return dispositionType;
    }

    public void setDispositionType(String dispositionType) {
        this.dispositionType = dispositionType;
    }

    @Column(name = "CallBack_URL")
    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public static enum POPAT {

        //0 - ClientSide
        //1 - ServerSide
        //2 - Both
        //3 - ClientSideOnBusy
        CLIENT, SERVER, BOTH, CLIENTBUSY, PLUGIN, SERVERBUSY;
    }

    @Column(name = "a2a_calling")
    public boolean isA2a_calling() {
        return a2a_calling;
    }

    public void setA2a_calling(boolean a2a_calling) {
        this.a2a_calling = a2a_calling;
    }

    @Column(name = "fallback_DID")
    public String getFallbackDid() {
        return fallbackDid;
    }

    public void setFallbackDid(String fallbackDid) {
        this.fallbackDid = fallbackDid;
    }
}

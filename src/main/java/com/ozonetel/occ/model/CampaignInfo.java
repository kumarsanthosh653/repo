package com.ozonetel.occ.model;

import java.util.Objects;

/**
 *
 * @author pavanj
 */
public class CampaignInfo {

    private Long id;
    private String name;
    private boolean agentWise;
    private int pendingData;

    public CampaignInfo() {
    }

    public CampaignInfo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CampaignInfo(Long id, String name, int pendingData) {
        this.id = id;
        this.name = name;
        this.pendingData = pendingData;
    }

    public CampaignInfo(Long id, String name, boolean agentWise) {
        this.id = id;
        this.name = name;
        this.agentWise = agentWise;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPendingData() {
        return pendingData;
    }

    public void setPendingData(int pendingData) {
        this.pendingData = pendingData;
    }

    public boolean isAgentWise() {
        return agentWise;
    }

    public void setAgentWise(boolean agentWise) {
        this.agentWise = agentWise;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
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
        final CampaignInfo other = (CampaignInfo) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OutboundCampaignInfo{" + "id=" + id + ", name=" + name + ", agentWise=" + agentWise + ", size=" + pendingData + '}';
    }

}

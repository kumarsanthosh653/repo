/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

/**
 *
 * @author pavanj
 */
public class MultiSkillFallbackSkill {

    private Long id;
    private String skillName;
    private Integer queueTime;

    public MultiSkillFallbackSkill() {
    }

    public MultiSkillFallbackSkill(Long id, String skillName, Integer queueTime) {
        this.id = id;
        this.skillName = skillName;
        this.queueTime = queueTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(Integer queueTime) {
        this.queueTime = queueTime;
    }

    @Override
    public String toString() {
        return "MultiSkill{" + "id=" + id + ", skillName=" + skillName + ", queueTime=" + queueTime + '}';
    }
}

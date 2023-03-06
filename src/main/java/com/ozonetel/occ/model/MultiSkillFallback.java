/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.util.List;

/**
 *
 * @author pavanj
 */
public class MultiSkillFallback {

    private List<MultiSkillFallbackSkill> skills;
    private String fallbackType;
    private String fallbackValue;
    private Integer skillIndex;
    private String mainSkill;

    public Integer getSkillIndex() {
        return skillIndex;
    }

    public void setSkillIndex(Integer skillIndex) {
        this.skillIndex = skillIndex;
    }

    public String getMainSkill() {
        return mainSkill;
    }

    public void setMainSkill(String mainSkill) {
        this.mainSkill = mainSkill;
    }

    public List<MultiSkillFallbackSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<MultiSkillFallbackSkill> skills) {
        this.skills = skills;
    }

    public String getFallbackType() {
        return fallbackType;
    }

    public void setFallbackType(String fallbackType) {
        this.fallbackType = fallbackType;
    }

    public String getFallbackValue() {
        return fallbackValue;
    }

    public void setFallbackValue(String fallbackValue) {
        this.fallbackValue = fallbackValue;
    }

    @Override
    public String toString() {
        return "MultiSkillFallback{" + "skills=" + skills + ", fallbackType=" + fallbackType + ", fallbackValue=" + fallbackValue + ", index=" + skillIndex + ", mainSkill=" + mainSkill + '}';
    }
}

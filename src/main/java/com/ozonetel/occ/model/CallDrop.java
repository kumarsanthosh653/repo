/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/**
 *
 * @author rajeshchary
 */
@Entity
@Table(name = "Skill_DropActions")
@NamedQueries({
    @NamedQuery(name = "getDropActions", query = "select d from CallDrop d where d.skillId=:skillId ")
})
public class CallDrop extends BaseObject implements Serializable {

    private Long id;
    private Action actionType;
    private String actionURL;
    private String actionValue;
    private Integer httpMethod;
    private Long skillId;
    private User user;

    /**
     * @return the user
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "UserID")
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
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static enum Action {

        CALLBACK,
        SMS,
        ANYURL
    }

    public static enum HttpMethod {

        GET,
        POST
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SeqID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ActionType")
    public Action getActionType() {
        return actionType;
    }

    public void setActionType(Action actionType) {
        this.actionType = actionType;
    }

    @Column(name = "ActionURL")
    public String getActionURL() {
        return actionURL;
    }

    public void setActionURL(String actionURL) {
        this.actionURL = actionURL;
    }

    @Column(name = "ActionValue")
    public String getActionValue() {
        return actionValue;
    }

    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }

    @Column(name = "ActionURLType")
   
    public Integer getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(Integer httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Column(name = "SkillID")
    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + (this.actionType != null ? this.actionType.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "CalldropAction{" + "actionId=" + id + ", actionType=" + actionType + ", actionURL=" + actionURL + ", actionValue=" + actionValue + ", method=" + httpMethod + '}';
    }
}

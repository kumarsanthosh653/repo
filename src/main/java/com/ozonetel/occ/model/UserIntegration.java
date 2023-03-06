package com.ozonetel.occ.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author PavanJ
 */
@Entity
@Table(name = "User_Integration")
public class UserIntegration implements Serializable {

    private Long id;
    private Integration integration;
    private String authCode;
    private String authToken;
    private String refreshToken;
    private String scope;
    private Long userId;
    private Date dateeIntegrated;
    private String location;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = Integration.class, optional = false)
    @JoinColumn(name = "integration_id", insertable = true, updatable = false)
    public Integration getIntegration() {
        return integration;
    }

    public void setIntegration(Integration integration) {
        this.integration = integration;
    }

    @Column(name = "auth_code")
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @Column(name = "access_token")
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Column(name = "refresh_token")

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Column(name = "scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_integrated")
    public Date getDateeIntegrated() {
        return dateeIntegrated;
    }

    public void setDateeIntegrated(Date dateeIntegrated) {
        this.dateeIntegrated = dateeIntegrated;
    }
    
    @Column(name="domain_location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "UserIntegration{" + "id=" + id + ", integration=" + integration + ", authCode=" + authCode + ", authToken=" + authToken + ", refreshToken=" + refreshToken + ", scope=" + scope + ", userId=" + userId + '}';
    }

}

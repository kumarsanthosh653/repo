package com.ozonetel.occ.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the basic "user" object in AppFuse that allows for
 * authentication and user management. It implements Acegi Security's
 * UserDetails interface.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a> Updated by
 * Dan Kibler (dan@getrolling.com) Extended to implement Acegi UserDetails
 * interface by David Carter david@carter.net
 */
@Entity
@Table(name = "app_user")
public class User extends BaseObject implements Serializable, UserDetails {

    private static final long serialVersionUID = 3832626162173359411L;
//    Campaign campaign;
    private Long id;
    private String username;                    // required
    private String password;                    // required
    private String confirmPassword;
    private String passwordHint;
    private String firstName;                   // required
    private String lastName;                    // required
    private String email;                       // required; unique
    private String phoneNumber;
    private String extension;
    private String website;
    private Address address = new Address();
    private Integer version;
    private Set<Role> roles = new HashSet<Role>();
    private boolean enabled;
    private boolean accountExpired;
    private boolean accountLocked;
    private boolean credentialsExpired;
    private Long screenPopMode;
    private String apiKey;
    private String callapiKey;
    private String callBackUrl;
    private String agentLoginIP;
    private UrlMap urlMap;
    private boolean redis;
    private String userTimezone;
    private String serverTimezone;

    @Column(name = "agent_login_ip")
    public String getAgentLoginIP() {
        return agentLoginIP;
    }

    public void setAgentLoginIP(String agentLoginIP) {
        this.agentLoginIP = agentLoginIP;
    }

    //  private boolean available;
    /**
     * Default constructor - creates a new instance with no values set.
     */
    public User() {
    }

    /**
     * Create a new instance and set the username.
     *
     * @param username login name for user.
     */
    public User(final String username) {
        this.username = username;
    }

    @Column(name = "is_redis")
    public boolean isRedis() {
        return redis;
    }

    public void setRedis(boolean redis) {
        this.redis = redis;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @Column(nullable = false, length = 50, unique = true)
    public String getUsername() {
        return username;
    }

    @Column(nullable = false)
    public String getPassword() {
        return password;
    }

    @Transient
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @Column(name = "password_hint")
    public String getPasswordHint() {
        return passwordHint;
    }

    @Column(name = "first_name", nullable = false, length = 50)
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "last_name", nullable = false, length = 50)
    public String getLastName() {
        return lastName;
    }

    @Column(nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    @Column(name = "extension")
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Returns the full name.
     *
     * @return firstName + ' ' + lastName
     */
    @Transient
    public String getFullName() {
        return firstName + ' ' + lastName;
    }

    @Embedded
    public Address getAddress() {
        return address;
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

    /**
     * @return the urlMap
     */
    @ManyToOne(targetEntity = UrlMap.class)
    @JoinColumn(name = "urlmap_id")
    public UrlMap getUrlMap() {
        return urlMap;
    }

    /**
     * @param urlMap the urlMap to set
     */
    public void setUrlMap(UrlMap urlMap) {
        this.urlMap = urlMap;
    }

    /**
     * Convert user roles to LabelValue objects for convenience.
     *
     * @return a list of LabelValue objects with role information
     */
    @Transient
    public List<LabelValue> getRoleList() {
        List<LabelValue> userRoles = new ArrayList<LabelValue>();

        if (this.roles != null) {
            for (Role role : roles) {
                // convert the user's roles to LabelValue Objects
                userRoles.add(new LabelValue(role.getName(), role.getName()));
            }
        }

        return userRoles;
    }

    /**
     * Adds a role for the user
     *
     * @param role the fully instantiated role
     */
    public void addRole(Role role) {
        getRoles().add(role);
    }

    /**
     * @see
     * org.springframework.security.userdetails.UserDetails#getAuthorities()
     * @return GrantedAuthority[] an array of roles.
     */
//    @Transient
//    public GrantedAuthority[] getAuthorities() {
//        return roles.toArray(new GrantedAuthority[0]);
//    }
    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new LinkedHashSet<GrantedAuthority>();
        grantedAuthorities.addAll(roles);
        return grantedAuthorities;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    @Column(name = "account_enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @Column(name = "account_expired", nullable = false)
    public boolean isAccountExpired() {
        return accountExpired;
    }

    /**
     * @see
     * org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
     */
    @Transient
    public boolean isAccountNonExpired() {
        return !isAccountExpired();
    }

    @Column(name = "account_locked", nullable = false)
    public boolean isAccountLocked() {
        return accountLocked;
    }

    /**
     * @see
     * org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
     */
    @Transient
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }

    @Column(name = "credentials_expired", nullable = false)
    public boolean isCredentialsExpired() {
        return credentialsExpired;
    }

    /**
     * @see
     * org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Transient
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        final User user = (User) o;

        return !(username != null ? !username.equals(user.getUsername()) : user.getUsername() != null);

    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (username != null ? username.hashCode() : 0);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
//        ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
//                .append("username", this.username)
//                .append("enabled", this.enabled)
//                .append("accountExpired", this.accountExpired)
//                .append("credentialsExpired", this.credentialsExpired)
//                .append("accountLocked", this.accountLocked);
//
//        GrantedAuthority[] auths = this.getAuthorities();
//        if (auths != null) {
//            sb.append("Granted Authorities: ");
//
//            for (int i = 0; i < auths.length; i++) {
//                if (i > 0) {
//                    sb.append(", ");
//                }
//                sb.append(auths[i].toString());
//            }
//        } else {
//            sb.append("No Granted Authorities");
//        }
//        return sb.toString();
        return username;
    }

    /**
     * @return the screenPopMode 1 for Embed with agentScreen 2 for popup
     */
    @Column(name = "sp_mode")
    public Long getScreenPopMode() {
        return screenPopMode;
    }

    /**
     * @param screenPopMode the screenPopMode to set
     */
    public void setScreenPopMode(Long screenPopMode) {
        this.screenPopMode = screenPopMode;
    }

    /**
     * @return the apiKey
     */
    @Column(name = "kk_api_key")
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
     /**
     * @return the callapiKey
     */
    @Column(name = "calls_api_key")
    public String getCallapiKey() {
        return callapiKey;
    }

    /**
     * @param callapiKey the callapiKey to set
     */
    public void setCallapiKey(String callapiKey) {
        this.callapiKey = callapiKey;
    }

    /**
     * @return the callBackUrl
     */
    @Column(name = "callback_url")
    public String getCallBackUrl() {
        return callBackUrl;
    }

    /**
     * @param callBackUrl the callBackUrl to set
     */
    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    /**
     * @return the userTimezone
     */
    @Column(name = "Timezone")
    public String getUserTimezone() {
        return userTimezone;
    }

    /**
     * @param userTimezone the userTimezone to set
     */
    public void setUserTimezone(String userTimezone) {
        this.userTimezone = userTimezone;
    }

    /**
     * @return the serverTimezone
     */
    @Column(name = "ServerTimezone")
    public String getServerTimezone() {
        return serverTimezone;
    }

    /**
     * @param serverTimezone the serverTimezone to set
     */
    public void setServerTimezone(String serverTimezone) {
        this.serverTimezone = serverTimezone;
    }

   
}

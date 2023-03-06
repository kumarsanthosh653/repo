package com.ozonetel.occ.dao.hibernate;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.ozonetel.occ.dao.UserDao;
import com.ozonetel.occ.model.Role;
import com.ozonetel.occ.model.User;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import javax.persistence.Table;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete and
 * retrieve User objects.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * Modified by <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 * Extended to implement Acegi UserDetailsService interface by David Carter
 * david@carter.net Modified by <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * to work with the new BaseDaoHibernate implementation that uses generics.
 */
public class UserDaoHibernate extends GenericDaoHibernate<User, Long> implements UserDao, UserDetailsService {

    /**
     * Constructor that sets the entity to User.class.
     */
    public UserDaoHibernate() {
        super(User.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
        return (List<User>) getHibernateTemplate().find("from User u order by upper(u.username)");
    }

    public List<Role> getUserRoles(String userName) {
        return getHibernateTemplate().find("select u.roles from User u  where u.username=?", userName);
    }

    /**
     * {@inheritDoc}
     */
    public User saveUser(User user) {
        log.debug("user's id: " + user.getId());
        getHibernateTemplate().saveOrUpdate(user);
        // necessary to throw a DataIntegrityViolation and catch it in UserManager
        getHibernateTemplate().flush();
        return user;
    }

    /**
     * Overridden simply to call the saveUser method. This is happenening
     * because saveUser flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param user the user to save
     * @return the modified user (with a primary key set if they're new)
     */
    @Override
    public User save(User user) {
        return this.saveUser(user);
    }

    /**
     * {@inheritDoc}
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List users = getHibernateTemplate().find("from User where username=?", username);
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("user '" + username + "' not found...");
        } else {
            return (UserDetails) users.get(0);
        }
    }

    public User getUserByApiKey(String apiKey) {
        List users = getHibernateTemplate().find("from User where apiKey=?", apiKey);
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("user '" + apiKey + "' not found...");
        } else {
            return (User) users.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserPassword(String username) {
        log.debug("getting Password of : " + username);
        SimpleJdbcTemplate jdbcTemplate
                = new SimpleJdbcTemplate(SessionFactoryUtils.getDataSource(getSessionFactory()));
        Table table = AnnotationUtils.findAnnotation(User.class, Table.class);
        return jdbcTemplate.queryForObject(
                "select password from " + table.name() + " where username=?", String.class, username);

    }

    @Override
    public List getUserSettings(Long userId) {
        Map<String, Object> params = new LinkedHashMap();
        params.put("user_id", userId);
        return executeProcedure("call Get_UserParamters(?)", params);
    }

    @Override
    public String getUserSetting(Long userId, String settingName, boolean isAdmin) {
        String settingValue = null;
        Map<String, Object> params = new LinkedHashMap();
        params.put("user_id", userId);
        params.put("isAdmin", isAdmin ? 1 : 0);
        params.put("param_code", settingName);
        List chatCustsPerAgent = executeProcedure("call Get_UserParamterV2(?,?,?)", params);
        if (chatCustsPerAgent != null && !chatCustsPerAgent.isEmpty()) {
            settingValue = ((Map<String, Object>) chatCustsPerAgent.get(0)).get("ParameterValue") == null ? ((Map<String, Object>) chatCustsPerAgent.get(0)).get("DefaultValue").toString() : ((Map<String, Object>) chatCustsPerAgent.get(0)).get("ParameterValue").toString();
        }

        return settingValue;

    }

    @Override
    public User getUserByUsernameandAPIKey(String username, String apiKey) {
        List users = getHibernateTemplate().find("from User where username=? and apiKey=?", new Object[]{username, apiKey});
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return (User) users.get(0);
        }
    }

}

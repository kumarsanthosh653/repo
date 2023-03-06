package com.ozonetel.occ.service.impl;

import com.ozonetel.occ.Constants;
import com.ozonetel.occ.dao.UserDao;
import com.ozonetel.occ.model.Role;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.UserExistsException;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.service.UserService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jws.WebService;
import javax.persistence.EntityExistsException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implementation of UserManager interface.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@WebService(serviceName = "UserService", endpointInterface = "com.ozonetel.occ.service.UserService")
public class UserManagerImpl extends UniversalManagerImpl implements UserManager, UserService {

    private UserDao dao;
    private PasswordEncoder passwordEncoder;

    /**
     * Set the Dao for communication with the data layer.
     *
     * @param dao the UserDao that communicates with the database
     */
    @Required
    public void setUserDao(UserDao dao) {
        this.dao = dao;
    }

    /**
     * Set the PasswordEncoder used to encrypt passwords.
     *
     * @param passwordEncoder the PasswordEncoder implementation
     */
    @Required
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    public User getUser(String userId) {
        return dao.get(new Long(userId));
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getUsers(User user) {
        return dao.getUsers();
    }

    /**
     * {@inheritDoc}
     */
    public User saveUser(User user) throws UserExistsException {

        if (user.getVersion() == null) {
            // if new user, lowercase userId
            user.setUsername(user.getUsername().toLowerCase());
        }

        // Get and prepare password management-related artifacts
        boolean passwordChanged = false;
        if (passwordEncoder != null) {
            // Check whether we have to encrypt (or re-encrypt) the password
            if (user.getVersion() == null) {
                // New user, always encrypt
                passwordChanged = true;
            } else {
                // Existing user, check password in DB
                String currentPassword = dao.getUserPassword(user.getUsername());
                if (currentPassword == null) {
                    passwordChanged = true;
                } else if (!currentPassword.equals(user.getPassword())) {
                    passwordChanged = true;
                }
            }

            // If password was changed (or new user), encrypt it
            if (passwordChanged) {
                user.setPassword(passwordEncoder.encodePassword(user.getPassword(), null));
            }
        } else {
            log.warn("PasswordEncoder not set, skipping password encryption...");
        }

        try {
            return dao.saveUser(user);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();// commented
            log.error(e.getMessage());
//            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername() + "' already exists!");
        } catch (EntityExistsException e) { // needed for JPA
//            e.printStackTrace();// commented
            log.error(e.getMessage());
//            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeUser(String userId) {
        log.debug("removing user: " + userId);
        dao.remove(new Long(userId));
    }

    /**
     * {@inheritDoc}
     *
     * @param username the login name of the human
     * @return User the populated user object
     * @throws UsernameNotFoundException thrown when username not found
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return (User) dao.loadUserByUsername(username);
    }

    public boolean hasRole(String username, String roleName) {
        Set<Role> roles = getUserByUsername(username).getRoles();
        for (Role role : roles) {
            if (StringUtils.equalsIgnoreCase(roleName, role.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Role> getUserRoles(String userName) {
        return dao.getUserRoles(userName);
    }

    public boolean hasTicketRole(String username) {
        Set<Role> roles = getUserByUsername(username).getRoles();
        for (Role role : roles) {
            if (StringUtils.equalsIgnoreCase(Constants.TICKET_ROLE, role.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Map> getUserSettings(Long userId) {
        return dao.getUserSettings(userId);
    }

    @Override
    public List<Map> getUserSettings(String username) {
        return getUserSettings(getUserByUsername(username).getId());
    }

    @Override
    public User getUserByApiKey(String apiKey) {
        return dao.getUserByApiKey(apiKey);
    }

    @Override
    public String getUserSetting(Long userId, String settingName, boolean isAdmin) {
        return dao.getUserSetting(userId, settingName, isAdmin);
    }
    
    public User getUserByUsernameandAPIKey(String username, String apiKey) {
        return dao.getUserByUsernameandAPIKey(username, apiKey);
    }

}

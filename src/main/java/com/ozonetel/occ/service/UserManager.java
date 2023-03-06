package com.ozonetel.occ.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.ozonetel.occ.dao.UserDao;
import com.ozonetel.occ.model.Role;
import com.ozonetel.occ.model.User;
import java.util.Map;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * Modified by <a href="mailto:dan@getrolling.com">Dan Kibler </a>
 */
public interface UserManager extends UniversalManager {

    /**
     * Convenience method for testing - allows you to mock the DAO and set it on
     * an interface.
     *
     * @param userDao the UserDao implementation to use
     */
    void setUserDao(UserDao userDao);

    /**
     * Retrieves a user by userId. An exception is thrown if user not found
     *
     * @param userId the identifier for the user
     * @return User
     */
    User getUser(String userId);

    /**
     * Finds a user by their username.
     *
     * @param username the user's username used to login
     * @return User a populated user object
     * @throws
     * org.springframework.security.userdetails.UsernameNotFoundException
     * exception thrown when user not found
     */
    User getUserByUsername(String username) throws UsernameNotFoundException;

    User getUserByApiKey(String apiKey) throws UsernameNotFoundException;

    /**
     * Retrieves a list of users, filtering with parameters on a user object
     *
     * @param user parameters to filter on
     * @return List
     */
    List getUsers(User user);

    /**
     * Saves a user's information.
     *
     * @param user the user's information
     * @throws UserExistsException thrown when user already exists
     * @return user the updated user object
     */
    User saveUser(User user) throws UserExistsException;

    /**
     * Removes a user from the database by their userId
     *
     * @param userId the user's id
     */
    void removeUser(String userId);

    public boolean hasRole(String username, String roleName);

    boolean hasTicketRole(String username);

    public List<Map> getUserSettings(String username);

    public List<Map> getUserSettings(Long userId);

    public String getUserSetting(Long userId, String settingName, boolean isAdmin);

    public List<Role> getUserRoles(String userName);

    public User getUserByUsernameandAPIKey(String username, String apiKey);
}

package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.apache.struts2.ServletActionContext;


import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.LabelValue;
import com.ozonetel.occ.model.Role;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.UserExistsException;
import com.ozonetel.occ.webapp.util.RequestUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Action for facilitating User Management feature.
 */
public class UserAction extends BaseAction implements Preparable {
    private static final long serialVersionUID = 6776558938712115191L;
    private List users;
    private User user;
    private String id;
    
   /* public List<Role> getAvailableRoles(){
    	List<Role> roleList = roleManager.getAll(null);
    	if(null != user)
    		roleList.removeAll(user.getRoles());
    	return roleList;
    }*/


    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            if (!"".equals(getRequest().getParameter("user.id"))) {
                user = userManager.getUser(getRequest().getParameter("user.id"));
            }
        }
    }

    /**
     * Holder for users to display on list screen
     * @return list of users
     */
    public List getUsers() {
        return users;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Delete the user passed in.
     * @return success
     */
    public String delete() {
        userManager.removeUser(user.getId().toString());
        List<String> args = new ArrayList<String>();
        args.add(user.getFullName());
        saveMessage(getText("user.deleted", args));

        return SUCCESS;
    }

    /**
     * Grab the user from the database based on the "id" passed in.
     * @return success if user found
     * @throws IOException can happen when sending a "forbidden" from response.sendError()
     */
    public String edit() throws IOException {
        HttpServletRequest request = getRequest();
        boolean editProfile = (request.getRequestURI().indexOf("editProfile") > -1);

        // if URL is "editProfile" - make sure it's the current user
        if (editProfile) {
            // reject if id passed in or "list" parameter passed in
            // someone that is trying this probably knows the AppFuse code
            // but it's a legitimate bug, so I'll fix it. ;-)
            if ((request.getParameter("id") != null) || (request.getParameter("from") != null)) {
                ServletActionContext.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
                log.warn("User '" + request.getRemoteUser() + "' is trying to edit user '" 
                         + request.getParameter("id") + "'");

                return null;
            }
        }

        // if a user's id is passed in
        if (id != null) {
            // lookup the user using that id
            user = userManager.getUser(id);
        } else if (editProfile) {
            user = userManager.getUserByUsername(request.getRemoteUser());
        } else {
            user = new User();
            user.addRole(new Role(Constants.USER_ROLE));
        }
        
        List<Role> availableRoles = roleManager.getRoles(null);
        availableRoles.removeAll(user.getRoles());
        List<LabelValue> rList = new ArrayList<LabelValue>();
        for (Role role : availableRoles) {
        	rList.add(new LabelValue(role.getName(), role.getName()));
		}
        request.getSession().setAttribute(Constants.AVAILABLE_ROLES, rList);

        if (user.getUsername() != null) {
            user.setConfirmPassword(user.getPassword());

            // if user logged in with remember me, display a warning that they can't change passwords
            log.debug("checking for remember me login...");

            AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
            SecurityContext ctx = SecurityContextHolder.getContext();

            if (ctx != null) {
                Authentication auth = ctx.getAuthentication();

                if (resolver.isRememberMe(auth)) {
                    getSession().setAttribute("cookieLogin", "true");
                    saveMessage(getText("userProfile.cookieLogin"));
                }
            }
        }

        return SUCCESS;
    }

    /**
     * Default: just returns "success"
     * @return "success"
     */
    public String execute() {
        return SUCCESS;
    }

    /**
     * Sends users to "mainMenu" when !from.equals("list"). Sends everyone else to "cancel"
     * @return "mainMenu" or "cancel"
     */
    public String cancel() {
        if (!"list".equals(from)) {
            return "mainMenu";
        }
        return "cancel";
    }

    /**
     * Save user
     * @return success if everything worked, otherwise input
     * @throws IOException when setting "access denied" fails on response
     */
    public String save() throws Exception {

        Integer originalVersion = user.getVersion();

        boolean isNew = ("".equals(getRequest().getParameter("user.version")));
        // only attempt to change roles if user is admin
        // for other users, prepare() method will handle populating
        if (getRequest().isUserInRole(Constants.ADMIN_ROLE)) {
            user.getRoles().clear(); // APF-788: Removing roles from user doesn't work
            String[] userRoles = getRequest().getParameterValues("userRoles");

            for (int i = 0; userRoles != null && i < userRoles.length; i++) {
                String roleName = userRoles[i];
                user.addRole(roleManager.getRole(roleName));
            }
        }

        try {
        	user = userManager.saveUser(user);
            
            // Fix for available Roles...!!!!
            List<Role> availableRoles = roleManager.getRoles(null);
            availableRoles.removeAll(user.getRoles());
            List<LabelValue> rList = new ArrayList<LabelValue>();
            for (Role role : availableRoles) {
            	rList.add(new LabelValue(role.getName(), role.getName()));
    		}
            getRequest().getSession().setAttribute(Constants.AVAILABLE_ROLES, rList);
            
            
        } catch (AccessDeniedException ade) {
            // thrown by UserSecurityAdvice configured in aop:advisor userManagerSecurity
            log.warn(ade.getMessage());
            getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (UserExistsException e) {
            List<String> args = new ArrayList<String>();
            args.add(user.getUsername());
            args.add(user.getEmail());
            addActionError(getText("errors.existing.user", args));

            // reset the version # to what was passed in
            user.setVersion(originalVersion);
            // redisplay the unencrypted passwords
            user.setPassword(user.getConfirmPassword());
            return INPUT;
        } 

        if (!"list".equals(from)) {
            // add success messages
            saveMessage(getText("user.saved"));
            return "mainMenu";
        } else {
            // add success messages
            List<String> args = new ArrayList<String>();
            args.add(user.getFullName());
            if (isNew) {
                saveMessage(getText("user.added", args));
                // Send an account information e-mail
                mailMessage.setSubject(getText("signup.email.subject"));
                try {
                    sendUserMessage(user, getText("newuser.email.message", args),
                                    RequestUtil.getAppURL(getRequest()));
                } catch (MailException me) {
                    addActionError(me.getCause().getLocalizedMessage());
                }
                return SUCCESS;
            } else {
                saveMessage(getText("user.updated.byAdmin", args));
                return INPUT;
            }
        }
    }

    /**
     * Fetch all users from database and put into local "users" variable for retrieval in the UI.
     * @return "success" if no exceptions thrown
     */
    public String list() {
        users = userManager.getUsers(new User());
        return SUCCESS;
    }
}

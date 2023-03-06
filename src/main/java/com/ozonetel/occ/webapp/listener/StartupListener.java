package com.ozonetel.occ.webapp.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.service.LookupManager;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * <p>StartupListener class used to initialize and database settings and
 * populate any application-wide drop-downs.
 * <p/>
 * <p>Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the GenericDao or service level to
 * avoid LazyInitializationException. Hibernate.initialize() works well for
 * doing this.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListener implements ServletContextListener {

    private static final Log log = LogFactory.getLog(StartupListener.class);

    @SuppressWarnings({"unchecked"})
    public void contextInitialized(ServletContextEvent event) {
        log.debug("Initializing context...");

        ServletContext context = event.getServletContext();

        Logger logger = null;
//        String log4jFile = event.getServletContext().getInitParameter("log4jFileName");
//        DOMConfigurator.configure(event.getServletContext().getRealPath(log4jFile));
        logger = LogManager.getLogger(StartupListener.class.getName());
//        logger.debug("Log4j path:"+event.getServletContext().getRealPath(log4jFile));

        // Orion starts Servlets before Listeners, so check if the config
        // object already exists
        Map<String, Object> config = (HashMap<String, Object>) context.getAttribute(Constants.CONFIG);

        if (config == null) {
            config = new HashMap<String, Object>();
        }

        if (context.getInitParameter(Constants.CSS_THEME) != null) {
            config.put(Constants.CSS_THEME, context.getInitParameter(Constants.CSS_THEME));
        }

        ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(context);

        /*String[] beans = ctx.getBeanDefinitionNames();
         for (String bean : beans) {
         log.debug(bean);
         }*/

        PasswordEncoder passwordEncoder = null;
        try {
            ProviderManager provider = (ProviderManager) ctx.getBean("_authenticationManager");
            for (Object o : provider.getProviders()) {
                AuthenticationProvider p = (AuthenticationProvider) o;
                if (p instanceof RememberMeAuthenticationProvider) {
                    config.put("rememberMeEnabled", Boolean.TRUE);
                } else if (ctx.getBean("passwordEncoder") != null) {
                    passwordEncoder = (PasswordEncoder) ctx.getBean("passwordEncoder");
                }
            }
        } catch (NoSuchBeanDefinitionException n) {
            log.debug("authenticationManager bean not found, assuming test and ignoring...");
            // ignore, should only happen when testing
        }
        context.setAttribute(Constants.CONFIG, config);

        // output the retrieved values for the Init and Context Parameters
        if (log.isDebugEnabled()) {
            log.debug("Remember Me Enabled? " + config.get("rememberMeEnabled"));
            if (passwordEncoder != null) {
                log.debug("Password Encoder: " + passwordEncoder.getClass().getName());
            }
            log.debug("Populating drop-downs...");
        }

        setupContext(context);
    }

    /**
     * This method uses the LookupManager to lookup available roles from the
     * data layer.
     *
     * @param context The servlet context
     */
    public static void setupContext(ServletContext context) {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        LookupManager mgr = (LookupManager) ctx.getBean("lookupManager");

        // get list of possible roles
        context.setAttribute(Constants.AVAILABLE_ROLES, mgr.getAllRoles());

        //Updating the ScreenPop tool bar to its Factoiry Settings
//        mgr.updateToolBarSettings();//we need to enable this for factory settings
        //TODO: actually the Hahmap will be popultaed from databse and we get all customers
//        Map<String,CampaignCallers> campaignCallers=new HashMap();
//        campaignCallers.put("39876544", new CampaignCallers("campaignId1"));
//        campaignCallers.put("39876545", new CampaignCallers("campaignId2"));
//        context.setAttribute(Constants.CAMPAIGN_CALLERS, campaignCallers);
//        log.debug("Drop-down initialization complete [OK]");
    }

    /**
     * Shutdown servlet context (currently a no-op method).
     *
     * @param servletContextEvent The servlet context event
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //LogFactory.release(Thread.currentThread().getContextClassLoader());
        //Commented out the above call to avoid warning when SLF4J in classpath.
        //WARN: The method class org.apache.commons.logging.impl.SLF4JLogFactory#release() was invoked.
        //WARN: Please see http://www.slf4j.org/codes.html for an explanation.
    }
}

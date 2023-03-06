/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.webapp.listener;
//	---------------------------------------------------------------------------
//	jWebSocket - Context Listener for Web Applications
//	Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import org.jwebsocket.appserver.ServletBridge;
import org.jwebsocket.config.JWebSocketConfig;

import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.server.TokenServer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Web application life cycle listener.
 *
 * @author aschulze
 */
public class ContextListener implements ServletContextListener {

    /**
     * initializes the web application on startup.
     *
     * @param sce
     */
    private static Logger logger = Logger.getLogger(ContextListener.class);

    public void contextInitialized(ServletContextEvent sce) {
        /*
        final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        final WebSocketServerTokenListener listener1 = (WebSocketServerTokenListener) springContext.getBean("messageHandler");
        final WebSocketServerTokenListener listener = (WebSocketServerTokenListener) springContext.getBean("chatHandler");
//        final WebSocketServerTokenListener listener = (WebSocketServerTokenListener) springContext.getBean("agentMessageHandler");

        logger.info("Got listener---->" + listener);

        // the following line must not be removed due to GNU LGPL 3.0 license! 
        JWebSocketFactory.printCopyrightToConsole();
        JWebSocketConfig.initForWebApp(sce.getServletContext());
        // start the jWebSocket server sub system
        JWebSocketFactory.start();

        logger.info("Started jWebsocket server ....");

        // get the token server
        TokenServer lServer = (TokenServer) JWebSocketFactory.getServer("ts0");
        if (lServer != null) {
            // and add the sample listener to the server's listener chain
            lServer.addListener(listener);
            lServer.addListener(listener1);
        }

        ServletBridge.setServer(lServer);*/
        logger.debug("Context listener started..");
    }

    /**
     * cleans up the web application on termination.
     *
     * @param sce
     */
    public void contextDestroyed(ServletContextEvent sce) {

        // stop the jWebSocket server sub system
        JWebSocketFactory.stop();

    }
}

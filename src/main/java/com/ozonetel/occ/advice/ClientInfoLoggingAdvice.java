/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.advice;

import java.util.logging.Level;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.slf4j.MDC;

/**
 *
 * @author pavanj
 */

public class ClientInfoLoggingAdvice {

    private static Logger log = Logger.getLogger(ClientInfoLoggingAdvice.class);

//    @Around(value = "execution(* com.ozonetel.occ.webapp.servlet.MessageHandler.process*(..))")
    public void aroundMessageHandler(ProceedingJoinPoint joinPoint) {
        try {
            WebSocketServerEvent serverEvent = null;
            Object[] args = joinPoint.getArgs();
            if (ArrayUtils.isNotEmpty(args)) {
                if (args[0] != null && args[0] instanceof WebSocketServerEvent) {
                    serverEvent = (WebSocketServerEvent) args[0];
                }
            }
            if (serverEvent != null) {
                MDC.put("sessionID", "[" + serverEvent.getSessionId() + "]");
            }
            joinPoint.proceed();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            MDC.remove("sessionID");
        }
    }
}

package com.ozonetel.occ.advice;

import com.ozonetel.occ.model.Agent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * This class logs all the Redis operations and results of Operations.
 *
 * @author V.J.Pavan Srinivas
 */
@Aspect
public class RedisLoggingAdvice {

    private static final Logger logger = Logger.getLogger(RedisLoggingAdvice.class);

    @Pointcut("execution(public * com.ozonetel.occ.service.RedisManager*.*(..))")
    public void methodsToBeLogged() {
    }

    @Around("this(com.ozonetel.occ.service.RedisManager)")
    public Object logRedisOps(ProceedingJoinPoint joinPoint) {
        Object returnValue = null;
        try {
            List<Object> argList = new ArrayList<>();
            Object[] args = joinPoint.getArgs();

            for (Object arg : args) {
                if (arg != null && arg.getClass().isArray()) {
                    argList.add(Arrays.asList((Object[]) arg));
                } else {
                    if (arg instanceof Agent) {
                        argList.add(((Agent) arg).toInfoString());
                    } else {
                        argList.add(arg);

                    }
                }
            }
            returnValue = joinPoint.proceed();

            logger.info("Call " + joinPoint.getSignature().toShortString() + " Args: " + argList + "  --> ReurnValue:" + returnValue);
            return returnValue;
        } catch (Throwable ex) {
            logger.error("Exception calling [" + joinPoint.getSignature().toShortString() + "] Args: ( " + (joinPoint.getArgs() == null ? null : Arrays.asList(joinPoint.getArgs())) + " )" + ex.getMessage(), ex);
        }
        return null;

    }
}

package com.ozonetel.occ.util;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pavanj
 */
public class LockServiceImpl {

    Set<String> pool = new HashSet<String>();

    public void lock(String name) {
        logger.info("Trying to get lock on :" + name);
        synchronized (pool) {
            logger.info("Entered sync block for :" + name);
            while (pool.contains(name)) // already being locked
            {
                try {
                    pool.wait();           // wait for release
                } catch (InterruptedException ignore) {
                    logger.error(ignore.getMessage(), ignore);
                }
            }
            pool.add(name);// I lock it
            logger.info("\uD83D\uDD12 Got lock on :" + name);
        }

    }

    public void unlock(String name) {
        logger.info("Trying to release lock on :" + name);
        synchronized (pool) {
            logger.info("Entered sync block for :" + name);
            pool.remove(name);
            logger.info("\uD83D\uDD13 Released lock on :" + name);
            pool.notifyAll();
        }

    }

    private static Logger logger = LoggerFactory.getLogger(LockServiceImpl.class);
}

package com.ozonetel.occ.service.impl;

import com.dinstone.beanstalkc.BeanstalkClient;
import com.dinstone.beanstalkc.BeanstalkClientFactory;
import com.dinstone.beanstalkc.Job;
import com.ozonetel.occ.service.BeanstalkService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pavanj
 */
public class BeanstalkServiceImpl implements BeanstalkService {

    public BeanstalkServiceImpl(BeanstalkClientFactory beanstalkClientFactory) {
        logger.debug("Beanstalk service initialized");
        this.beanstalkClientFactory = beanstalkClientFactory;
    }

    public void init() {
        logger.debug("🤺 initializing beanstalk client...");
        callEndBeanStalkClient = beanstalkClientFactory.createBeanstalkClient();
        callEndBeanStalkClient.useTube(callEndTube);
        callEndBeanStalkClient.ignoreTube("default");
        logger.debug("👀 👀  Started watching:|{}| and using:|{}|", callEndBeanStalkClient.listTubeWatched(), callEndBeanStalkClient.listTubeUsed());
        liveEventBeanStalkClient = beanstalkClientFactory.createBeanstalkClient();
        liveEventBeanStalkClient.useTube(liveEventsTube);
        liveEventBeanStalkClient.ignoreTube("default");
        logger.debug("👀 👀  Started watching:|{}| and using:|{}|", liveEventBeanStalkClient.listTubeWatched(), liveEventBeanStalkClient.listTubeUsed());
    }

    public Job peekJob(long jobId) {
        return callEndBeanStalkClient.peek(jobId);
    }

    @Override
    public long addCallCompletedEvent(String job) {
        return callEndBeanStalkClient.putJob(1, 0, 5000, job.getBytes());
    }

    public long addLiveEvent(String job) {
        return liveEventBeanStalkClient.putJob(1, 0, 5000, job.getBytes());
    }

    public void cleanUp() {
        if (callEndBeanStalkClient != null) {
            callEndBeanStalkClient.close();

            logger.info("Beanstalk client closed..");
        }
    }

    public void setBeanstalkClientFactory(BeanstalkClientFactory beanstalkClientFactory) {
        this.beanstalkClientFactory = beanstalkClientFactory;
    }

    public void setCallEndTube(String callEndTube) {
        this.callEndTube = callEndTube;
    }
    
    public void setLiveEventsTube(String liveEventsTube) {
        this.liveEventsTube = liveEventsTube;
    }

    private BeanstalkClientFactory beanstalkClientFactory;
    private String callEndTube;
    private String liveEventsTube;
    private static BeanstalkClient callEndBeanStalkClient;
    private static BeanstalkClient liveEventBeanStalkClient;

    private static Logger logger = LoggerFactory.getLogger(BeanstalkServiceImpl.class);

}

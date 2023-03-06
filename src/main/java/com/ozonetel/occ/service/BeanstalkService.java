package com.ozonetel.occ.service;

/**
 *
 * @author pavanj
 */
public interface BeanstalkService {

    public long addCallCompletedEvent(String job);

    public long addLiveEvent(String job);

}

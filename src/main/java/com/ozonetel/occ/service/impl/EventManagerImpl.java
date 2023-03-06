package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.RedisKeys;
import com.ozonetel.occ.dao.EventDao;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.AgentEventCache;
import com.ozonetel.occ.model.Event;
import com.ozonetel.occ.service.BeanstalkService;
import com.ozonetel.occ.service.EventManager;
import com.ozonetel.occ.service.GenericManager;
import com.ozonetel.occ.service.RedisManager;
import com.ozonetel.occ.service.UserManager;
import com.ozonetel.occ.util.JsonUtil;
import com.ozonetel.occ.util.LockServiceImpl;
import java.util.Calendar;
import java.util.Date;

import javax.jws.WebService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

@WebService(serviceName = "EventService", endpointInterface = "com.ozonetel.occ.service.EventManager")
public class EventManagerImpl extends GenericManagerImpl<Event, Long> implements EventManager {

    EventDao eventDao;

    public EventManagerImpl(EventDao eventDao) {
        super(eventDao);
        this.eventDao = eventDao;
    }

    public void logEvent_new(String eventName, Long userId, String userName, Long agentUniqId, String agentLoginId, Agent.Mode currentAgentMode, Date startTime, Long ucid, String eventMessage, String miscDetails) {
        String lockKey = "event_lock_" + agentUniqId;
        try {
            lockService.lock(lockKey);
            Event event = new Event();
            event.setAgentId(agentUniqId);
            event.setUserId(userId);
            event.setEvent(eventName);
            event.setMode(Event.AgentMode.valueOf(currentAgentMode.name()));
            event.setStartTime(new Date());
            event.setUcid(ucid);
            event.setEventData(eventMessage);
            event.setMiscDetails(miscDetails);

            redisAgentManager.lpush("ca:agent-events:" + userName, 0, new GsonBuilder().serializeNulls().create().toJson(event));

        } finally {
            lockService.unlock(lockKey);
        }
    }

    @Override
    public void logEvent(String eventName, Long userId, String userName, Long agentUniqId, String agentLoginId, Agent.Mode currentAgentMode, Date startTime, Long ucid, String eventMessage, String miscDetails) {
        String lockKey = "event_lock_" + agentUniqId;
        Event event = null;
        try {

            lockService.lock(lockKey);
            log.debug("Before modifying -> eventName:" + eventName + "|userName:" + userName + "|agentUniqId:" + agentUniqId + "|agentLoginId:" + agentLoginId + "|currentAgentMode:" + currentAgentMode + "|startTime:" + startTime + "|ucid:" + ucid + "|eventMessage:" + eventMessage + "|miscDetails:" + miscDetails + " => " + DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.S"));
            startTime = new Date();//--> overriding to fix insertion sequence bug.
            // startTime = new Date((startTime.getTime() / 1000) * 1000);//--> Remove millies to fix time rounding issue in MySQL.https://stackoverflow.com/questions/20861366/rounding-issues-storing-java-util-date-into-mysql-datetime
//            log.debug("Start time after:" + startTime.getTime() + " -> ");
            log.debug("After modifying -> eventName:" + eventName + "|userName:" + userName + "|agentUniqId:" + agentUniqId + "|agentLoginId:" + agentLoginId + "|currentAgentMode:" + currentAgentMode + "|startTime:" + startTime + "|ucid:" + ucid + "|eventMessage:" + eventMessage + "|miscDetails:" + miscDetails + " => " + DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss.S"));

            updatePreviousEvent(userId, userName, agentUniqId, agentLoginId, startTime);
            event = new Event();
            event.setAgentId(agentUniqId);
            event.setUserId(userId);
            event.setEvent(eventName);
            event.setMode(Event.AgentMode.valueOf(currentAgentMode.name()));
            event.setStartTime(startTime);
            event.setUcid(ucid);
            event.setEventData(eventMessage);
            event.setMiscDetails(miscDetails);
            if (!"login".equalsIgnoreCase(eventName) && !"logout".equalsIgnoreCase(eventName)) {
                saveInRedis(userName, agentLoginId, event);
                log.debug("Saving in redis....." + event);
            } else { //save login , logout events in DB directly.
                if ("login".equalsIgnoreCase(eventName)) {
                    checkProperLogout(userId, userName, agentUniqId, agentLoginId, startTime);
                }
                log.debug("Saving in DB...:" + event);
                event = this.save(event);
                updateAgentEventCache(agentUniqId, event);
            }

            if (StringUtils.equalsIgnoreCase(eventName, Constants.EVENT_LOGOUT)) {
                updateLastLoginEvent(userId, userName, agentUniqId, agentLoginId, startTime);
            }
        } finally {
            lockService.unlock(lockKey);
        }

        try {
            if (event != null) {
                log.debug("Added live event to beanstalk | job id : " + beanstalkService.addLiveEvent(new Gson().toJson(event)));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // ---> Storing agents eligible for inbound.//--->asked and trashed by upper management
        /*if ((StringUtils.equalsIgnoreCase(eventName, "idle") || StringUtils.equalsIgnoreCase(eventName, "release"))
                && (currentAgentMode == Agent.Mode.INBOUND || currentAgentMode == Agent.Mode.BLENDED)) {
            redisAgentManager.sadd("ca:" + userName + ":inbound-eligible:agents", agentLoginId);
        } else {// 🤔 Jim! you are not eligible for Inbound call.
            redisAgentManager.srem("ca:" + userName + ":inbound-eligible:agents", agentLoginId);
        }*/
    }

    private void updateAgentEventCache_check(Long agentId, Event event) {
        if (StringUtils.equalsIgnoreCase(event.getEvent(), "login")) {
            AgentEventCache agentEventCache = agentEventCacheManager.get(agentId);
            if (agentEventCache == null) {
                agentEventCache = new AgentEventCache();
                agentEventCache.setAgentId(agentId);
            }

            agentEventCache.setLoginId(event.getEventId());
            log.debug("updating login event id in cahce:" + event);

            //agentEventCache.setLastEventId(event.getEventId());
            agentEventCacheManager.save(agentEventCache);
        }
    }

    private void updateAgentEventCache(Long agentId, Event event) {
        //AgentEventCache agentEventCache = agentEventCacheManager.get(agentId);
        AgentEventCache agentEventCache = getCacheObjectFromRedis(agentId);

        if (agentEventCache == null) {
            agentEventCache = new AgentEventCache();
            agentEventCache.setAgentId(agentId);
        }
        if (StringUtils.equalsIgnoreCase(event.getEvent(), "login")) {
            agentEventCache.setLoginId(event.getEventId());
            log.debug("updating login event id in cahce:" + event);
        }
        agentEventCache.setLastEventId(event.getEventId());
        redisAgentManager.hset(RedisKeys.AGENT_EVENTIDS_CACHE, "" + agentId, new Gson().toJson(agentEventCache));
        //agentEventCacheManager.save(agentEventCache);
    }

    private void checkProperLogout(Long userId, String user, Long agentUniqId, String agentLoginId, Date date) {
//        Event event = getLastEventForAgent(a.getUser().getUsername(), a.getAgentId());
        Event event = getLastEventFromDB(agentUniqId, agentLoginId, user, userId);
        if (event != null && !("logout".equalsIgnoreCase(event.getEvent()))) {
            event = new Event();
            event.setAgentId(agentUniqId);
            event.setUserId(userId);
            event.setStartTime(Calendar.getInstance().getTime());
            event.setEndTime(Calendar.getInstance().getTime());
            event.setEvent("logout");
            event = this.save(event);
            updateAgentEventCache(agentUniqId, event);
            updateLastLoginEvent(userId, user, agentUniqId, agentLoginId, date);
        }
    }

    private void saveInRedis(String username, String agentLoginId, Event event) {
        redisAgentManager.hset(RedisKeys.AGENT_EVENTS_HASH, username + ":" + agentLoginId, jsonUtil.convertToJson(event));
    }

    private Event getEventFromRedis(String username, String agentLoginId) {
        String lastEvent = redisAgentManager.hget(RedisKeys.AGENT_EVENTS_HASH, username + ":" + agentLoginId);
        Event event = null;
        if (StringUtils.isNotBlank(lastEvent)) {
            event = jsonUtil.convertFromJson(lastEvent, Event.class);
            redisAgentManager.hdel(RedisKeys.AGENT_EVENTS_HASH, username + ":" + agentLoginId);
        }
        return event;
    }

    @Override
    public void updatePreviousEvent(Long userId, String user, Long agentUniqId, String agentLoginId, Date endDate) {

        Event previousEvent = getLastEventForAgent(userId, user, agentUniqId, agentLoginId);

        if (previousEvent != null && previousEvent.getEvent() != null && !previousEvent.getEvent().equalsIgnoreCase("login")) {
            // ----> Last event comes from redis which doesn't store user and agent objects.
            //  so we have to set them before saving to database
            log.debug("Updating previous event:" + previousEvent);
            previousEvent.setAgentId(agentUniqId);
            previousEvent.setUserId(userId);
            previousEvent.setEndTime(endDate);
            previousEvent = this.save(previousEvent);//save in db
            log.debug("Saved event in DB:" + previousEvent);
            updateAgentEventCache(agentUniqId, previousEvent);
        }
    }

    @Override
    public void updateLastLoginEvent(Long userId, String user, Long agentUniqId, String agentLoginId, Date time) {
        //AgentEventCache agentEventCache = agentEventCacheManager.get(agentUniqId);
        AgentEventCache agentEventCache = getCacheObjectFromRedis(agentUniqId);

        Event lastLoginEvent = null;
        if (agentEventCache != null && agentEventCache.getLoginId() != null) {
            lastLoginEvent = this.get(agentEventCache.getLoginId());
            log.debug("Got last login event from cache:" + lastLoginEvent + "|" + agentEventCache);
        } else {
            lastLoginEvent = eventDao.getLastLoginEventForAgent(userId, agentUniqId);
            log.debug("Cache sold for some cash(💵)? Where is the cache for the event?:" + lastLoginEvent);
        }

        if (lastLoginEvent != null) {
            lastLoginEvent.setEndTime(time);
            this.save(lastLoginEvent);
        }

    }

    @Override
    public void updateChatSessionsCountInBusyEvent(String user, Long agentUniqId, String agentLoginId, int sessionCount) {
        Event event = getLastEventForAgent(userManager.getUserByUsername(user).getId(), user, agentUniqId, agentLoginId);
        event.setMiscDetails("ChatsHandled:" + sessionCount);
        saveInRedis(user, agentLoginId, event);

    }

    @Override
    public Event save(Event event) {
        //TODO Enable reconnects in production
//        if (StringUtils.equalsIgnoreCase(event.getEvent(), "reconnect") || StringUtils.equalsIgnoreCase(event.getEventData(), "reconnect")) {
//            redisAgentManager.hincrBy(event.getUser().getUsername() + ":reconnects:" + new SimpleDateFormat("YYYY_MM_dd").format(new Date()), event.getAgent().getAgentId(), 1);
//        }
        return eventDao.save(event);
    }

    @Override
    public Event getLastEventForAgent(Long userId, String user, Long agentUniqId, String agentLoginId) {
        Event event = getEventFromRedis(user, agentLoginId);
        if (event == null) {
            log.debug("Got event null from redis for agent:" + agentLoginId + " | user:" + user);
            event = getLastEventFromDB(agentUniqId, agentLoginId, user, userId);
        }
        return event;
    }

    private Event getLastEventFromDB(Long agentUniqId, String agentLoginId, String user, Long userId) {
        Event event = null;
        AgentEventCache agentEventCache = getCacheObjectFromRedis(agentUniqId);

        if (agentEventCache == null || agentEventCache.getLastEventId() == null) {
            log.debug("🏌  🏌   🏌   Cache missed for agent:" + agentLoginId + " |user:" + user + " | " + agentEventCache);
            event = eventDao.getLastEventForAgentNew(userId, agentUniqId);
        } else {
            event = this.get(agentEventCache.getLastEventId());
            log.debug("💸  💸  💸  💸  Got previous event from cache:" + event);
        }

        return event;
    }

    private AgentEventCache getCacheObjectFromRedis(Long agentUniqId) {
        AgentEventCache agentEventCache = null;
        String strngAgentEventCache = redisAgentManager.hget(RedisKeys.AGENT_EVENTIDS_CACHE, "" + agentUniqId);
        if (StringUtils.isNotBlank(strngAgentEventCache)) {
            agentEventCache = new Gson().fromJson(strngAgentEventCache, AgentEventCache.class);
        }
        return agentEventCache;
    }

    public void setRedisAgentManager(RedisManager<Agent> redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setAgentEventCacheManager(GenericManager<AgentEventCache, Long> agentEventCacheManager) {
        this.agentEventCacheManager = agentEventCacheManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setLockService(LockServiceImpl lockService) {
        this.lockService = lockService;
    }

    public void setBeanstalkService(final BeanstalkService beanstalkService) {
        this.beanstalkService = beanstalkService;
    }

    private RedisManager<Agent> redisAgentManager;
    private UserManager userManager;
    private JsonUtil<Event> jsonUtil = new JsonUtil<>();
    private GenericManager<AgentEventCache, Long> agentEventCacheManager;
    private LockServiceImpl lockService;
    private BeanstalkService beanstalkService;

}

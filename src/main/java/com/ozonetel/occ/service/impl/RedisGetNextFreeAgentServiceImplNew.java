package com.ozonetel.occ.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jamesmurty.utils.XMLBuilder;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.MultiSkillFallback;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.RedisGeneralQueueManager;
import com.ozonetel.occ.service.RedisGetNextFreeAgentService;
import com.ozonetel.occ.service.RedisManager;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

/**
 *
 * @author pavanj
 */
public class RedisGetNextFreeAgentServiceImplNew implements RedisGetNextFreeAgentService {

    @Override
    public String getNextFreeAgent(String username, String campaignId, String did, String callerId, String ucid, String agentMonitorUcid, String skillName, String agentId, String agentPh) {

        log.info("----------Executing redis getnextfree agent for user:" + username);
        long callStartTime = System.currentTimeMillis();
        Jedis jedis = jedisSentinelPool.getResource();

        Agent freeAgent = null;
        try {
            XMLBuilder xmlb = XMLBuilder.create("response");
            xmlb.e("action").t("nextAgent");

            if (agentId != null || agentPh != null) {
                xmlb.e("DD").t("1");
            } else {
                xmlb.e("DD").t("0");
            }

            if (StringUtils.isBlank(campaignId)) {
                campaignId = redisCampaignManager.hget("camptype_did-id:map", "inbound:did:" + did);
            }

            if (campaignId == null) {
                xmlb.e("status").t("0");
                xmlb.e("message").t("Invalid campaign..");
                return xmlb.asString();
            }

//            String tmpSkillId = redisSkillManager.getString(username + ":skill:" + skillName);
            String tmpSkillId = redisSkillManager.hget(username + ":skills:name-id-map", skillName);
            Skill tmpSkill1 = redisSkillManager.get(username + ":skills", "skill:" + tmpSkillId);

            log.trace("Temp skill:" + tmpSkillId + " | " + tmpSkill1);

            log.trace("%%%% Campaign Id:" + campaignId);

            Campaign campaign = redisCampaignManager.get(username + ":campaigns", "campaign:" + campaignId);

            log.debug("Campaign:" + campaign);

            if (campaign == null) {
                log.debug("No campaign exists with this did:" + did);
                xmlb.e("status").t("0");
                xmlb.e("message").t("Invalid campaign..");
                return xmlb.asString();
            }
            if (campaign.getIsDelete() || !StringUtils.equalsIgnoreCase(campaign.getPosition(), "running")) {
                log.debug("Campaign is not running or deleted:" + campaign.getCampignName());
                xmlb.e("status").t("0");
                xmlb.e("message").t("Campaign is not running or deleted..");
                return xmlb.asString();
            }

            if (campaign.isOffLineMode()) {
                log.debug("Campaign is offline");
                return "Campaign is offline";
            }

            xmlb.e("cId").t(campaignId);

            String skillId = redisSkillManager.getString(username + ":skill:" + skillName);

            if (skillId == null) {
                log.debug("user:" + username + ": " + "User '" + username + "' doesn't have skill with name:" + skillName);
                xmlb.e("status").t("0");
                xmlb.e("message").t("Invalid Skill...");
                return xmlb.asString();
            }

            if (!redisSkillManager.sismember(username + ":campaign:" + campaignId + ":skills", skillId)) {
                log.debug("user:" + username + ": " + "Skill '" + skillName + "' is not assigned to campaign:" + campaign);
                xmlb.e("status").t("0");
                xmlb.e("message").t("Skill '" + skillName + "' is not assigned to campaign:" + campaign.getCampignName());
                return xmlb.asString();
            }

            Skill skill = redisSkillManager.get(username + ":skills", "skill:" + skillId);
            xmlb.e("recOn").t("" + skill.getRecOn());
            xmlb.e("status").t("1");

            XMLBuilder qXml = xmlb.e("Q");

            qXml.e("S").t("" + skill.getQueueSize());
            qXml.e("T").t("" + skill.getQueueTimeOut());
            XMLBuilder fbrEle = qXml.e("fbr");
            String fallBackRule = skill.getFallBackRule();

            switch (fallBackRule) {
                case "1": // ----> Dialout//dialout
                    fbrEle.a("type", "DO")
                            .a("isSip", "" + skill.getDialOutNumber().isSip())
                            .t(skill.getDialOutNumber().getDialOutNumber());
                    break;
                case "2"://-----> Disconnect
                    fbrEle.a("type", "DIS");
                    break;
                case "3"://-----> voicemail
                    fbrEle.a("type", "VM");
                    break;
                case "4"://-----> Skill Transfer
                    fbrEle.a("type", "TS").t(skill.getQueueSkillTransfer().getSkillName());
                    break;
                case "5"://-----> IVR Transfer
                    fbrEle.a("type", "TI").t(skill.getQueueIvrTransfer());
                    break;
                case "6"://-----> Mult skill fall back
                    MultiSkillFallback multiSkill = new Gson().fromJson(skill.getFallbackDetails(), MultiSkillFallback.class);
                    multiSkill.setMainSkill(skillName);
                    multiSkill.setSkillIndex(0);

                    switch (multiSkill.getFallbackType()) {
                        case "1": // ----> Dialout//dialout
                            multiSkill.setFallbackType("DO");
                            break;
                        case "2"://-----> Disconnect
                            multiSkill.setFallbackType("DIS");
                            break;
                        case "3"://-----> voicemail
                            multiSkill.setFallbackType("VM");
                            break;
                        case "4"://-----> Skill Transfer
                            multiSkill.setFallbackType("TS");
                            break;
                        case "5"://-----> IVR Transfer
                            multiSkill.setFallbackType("TI");
                            break;
                    }
                    fbrEle.a("type", "MS").t(new GsonBuilder().create().toJson(multiSkill));
                    break;
            }

            /**
             * -----------------------------------------------------------------------
             * | | | | | | | | | | | FreeAgent check starts now | | | | | | | |
             * | | |
             * -----------------------------------------------------------------------
             */
            log.info("|||||||||| Entering synchronized block..");
            boolean addedToQueue = false;

            synchronized (this) {
                callStartTime = System.currentTimeMillis();
                log.debug("user:" + username + ": " + "getting queue size..");
                Long queueSize = redisGeneralQueueManager.getQueueSize(did, username, agentMonitorUcid, callerId, skill);
                log.debug("user:" + username + ": " + " | Queue size:" + queueSize);

                if (queueSize != null && queueSize != 0) {//If there are people waiting in the queue add to the queue and return :/.
                    Long queuePosition = redisGeneralQueueManager.checkQueue(did, username, agentMonitorUcid, callerId, skill, callStartTime);
                    addedToQueue = true;
                    qXml.e("P").t("" + (queuePosition + 1));
                    log.debug("^^^^Queue size is not 0 so returning..| Size :" + queueSize + " | Queue position:" + queuePosition);
                    if (queuePosition != null && queuePosition != 0) {
                        return xmlb.asString();
                    }
                }
                //username:did:skillname:firstStep => IdleAgents ∩ LoggedinAgents ∩ SkillAgents
                //take intersection of agents for did and skill, with idle agents
                //instead of intersection, we can maintain a setString of idle agents for did and skill
                jedis.sinterstore(StringUtils.lowerCase(username + ":" + did + ":" + skillName + ":firstStep"),
                        //                    StringUtils.lowerCase(username + ":skill:" + skillId + ":agents"),
                        StringUtils.lowerCase(username + ":state:" + Agent.State.IDLE),
                        StringUtils.lowerCase(username + ":loggedin"),
                        StringUtils.lowerCase(username + ":skill:" + skillId + ":agents"));

                if (log.isTraceEnabled()) {
                    log.trace("Idle agents    :" + redisAgentManager.smembers(username + ":state:" + Agent.State.IDLE));

                    log.trace("LoggedIn agents:" + redisAgentManager.smembers(username + ":loggedin"));

                    log.trace("Skill(" + skillName + ") agents:" + redisAgentManager.smembers(username + ":skill:" + skillId + ":agents"));
                }

                Set<String> firstStepAgents = redisAgentManager.smembers(username + ":" + did + ":" + skillName + ":firstStep");
                log.debug("FirstStep agents(idle ∩ loggedin ∩ skillagents):" + firstStepAgents);

                if (firstStepAgents != null && !firstStepAgents.isEmpty()) {
                    //username:did:skillname:secondStep => Inbound Agents ∩ Blended Agents 
                    //take union of blended(mode 4) and inbound agents(mode 0)
                    //similarly maintain a separate setString for this.
                    jedis.sunionstore(StringUtils.lowerCase(username + ":" + did + ":" + skillName + ":secondStep"),
                            StringUtils.lowerCase(username + ":mode:" + Agent.Mode.INBOUND),
                            StringUtils.lowerCase(username + ":mode:" + Agent.Mode.BLENDED));
                    Set<String> secondStepAgents = redisAgentManager.smembers(username + ":" + did + ":" + skillName + ":secondStep");

                    log.debug("SecondStep agents(Inbound  ∪ Blended):" + secondStepAgents);

                    if (secondStepAgents != null && !secondStepAgents.isEmpty()) {

                        //finally take the intersection of the above two steps
                        // getFromJson all free agents for this user.
                        Set<String> agentLoginIdsToCheck = jedis.sinter(StringUtils.lowerCase(username + ":" + did + ":" + skillName + ":firstStep"),
                                StringUtils.lowerCase(username + ":" + did + ":" + skillName + ":secondStep"));

                        log.debug("Free Agents ( FirstStep ∩ SecondStep):" + agentLoginIdsToCheck);

                        if (agentLoginIdsToCheck != null && !agentLoginIdsToCheck.isEmpty()) {
                            Map<Double, String> agentSortedMap = new TreeMap<>();
                            String agent_unique_id;
                            Double score;
                            //Agent redisAgent;

                            // -----> Sort agents according to last answered time.
                            for (String tmpAgentLoginId : agentLoginIdsToCheck) {
                                agent_unique_id = redisAgentManager.getString(username + ":agent:" + tmpAgentLoginId);
                                score = redisAgentManager.zscore(username + ":agent:scores", tmpAgentLoginId);

                                if (agent_unique_id == null) {
                                    log.error("@@@@@@@@@@@BadSync | For agent_id:" + tmpAgentLoginId + " | Score:" + score);
                                } else {
                                    agentSortedMap.put(score, agent_unique_id);
                                }
                            }

                            if (log.isTraceEnabled()) {
                                log.trace("---------------User:" + username + ": " + "Agents to verify -----------");
                                for (Map.Entry<Double, String> tmpAgentEntry : agentSortedMap.entrySet()) {
                                    log.trace("Score:" + tmpAgentEntry.getKey() + " | " + tmpAgentEntry);
                                }
                                log.trace("-----------------------------------------------------------------------");
                            }

                            Set<String> agentSkills;
                            Set<String> tmpQueueKeys;
                            Long position;
                            Agent redisAgent = null;

//                            int counter=0;
                            for (Map.Entry<Double, String> tmpAgentEntry : agentSortedMap.entrySet()) {
                                redisAgent = redisAgentManager.get(username + ":agents", StringUtils.lowerCase("agent:" + tmpAgentEntry.getValue()));
                                // ----> If agent is not free or has some direct calls check another agent.
                                if (redisAgent == null) {
                                    log.error("@@@@@@@@@@@BadSync |Agent object null For agent Unique id:" + tmpAgentEntry.getValue());
                                    continue;
                                }
                                if (redisAgent.getNextFlag() != 0 || !redisAgent.isActive() || redisAgent.getState() != Agent.State.IDLE || redisAgent.getDirectCallCount() != 0) {
                                    log.debug("###Not free :" + redisAgent);
                                    continue;
                                }

//                                freeAgent = redisAgent;
//                                break;// -----> You found the free agent, break it..☹.
                                redisSkillManager.del("tmp:" + username + ":queueunion");
                                redisSkillManager.del("tmp:" + username + ":zqueue");
                                log.debug("user:" + username + ": " + redisAgent);
                                agentSkills = redisAgentManager.smembers(username + ":agent:" + redisAgent.getAgentId() + ":skills");

                                //If agent doesn't have more than one skill assign the agent.
                                if (agentSkills.size() == 1) {
                                    log.debug("'" + redisAgent.getAgentId() + "' has only one skill.., so giving ..++");
                                    freeAgent = redisAgent;
                                    break;
                                } else {
                                    tmpQueueKeys = new HashSet<>(agentSkills.size() + 1);
                                    if (!addedToQueue) {
                                        redisAgentManager.zadd("tmp:" + username + ":zqueue", callStartTime, agentMonitorUcid);
                                        tmpQueueKeys.add(StringUtils.lowerCase("tmp:" + username + ":zqueue"));
                                    }

                                    // ----- > If agent has more than one skill union all the skills of the agent and check the 
                                    //         caller position in the union list. If the caller is on top of the union list give the agent.
                                    for (String tmpSkill : agentSkills) {
                                        tmpQueueKeys.add(StringUtils.lowerCase("queue:" + username + ":" + tmpSkill));
                                    }
                                    log.debug("user:" + username + ": " + "^^^^ all queues:" + tmpQueueKeys);
                                    redisAgentManager.zunionstore("tmp:" + username + ":queueunion", tmpQueueKeys.toArray(new String[tmpQueueKeys.size()]));
                                    log.debug("user:" + username + ": " + "@@@@@ zunion :" + redisAgentManager.zrangeWithScores("tmp:" + username + ":queueunion", 0, -1));
                                    position = redisAgentManager.zrank("tmp:" + username + ":queueunion", agentMonitorUcid);
                                    if (position != null && position == 0) {
                                        freeAgent = redisAgent;
                                        break;
                                    }
                                }

                            }

                            if (freeAgent != null) {
                                xmlb.e("a").a("id", freeAgent.getAgentId())
                                        .a("sms", String.valueOf(false))
                                        .a("isSip", "" + freeAgent.getFwpNumber().isSip())
                                        .t(freeAgent.getPhoneNumber());
                                qXml.e("P").t("1");
                                log.debug("user:" + username + ": " + "Got free agent as:" + freeAgent.toLongString());
                                redisAgentManager.zadd(username + ":agent:scores", System.currentTimeMillis() * freeAgent.getPriority(), freeAgent.getAgentId());
                                freeAgent.setNextFlag(1L);
                                freeAgent.setContact(callerId);
                                freeAgent.setLastSelected(System.currentTimeMillis());
                                freeAgent.setIdleTime(System.currentTimeMillis());
                         //REVISIT: Commented as we are not using agent.user --COmpletely comment the code.
                                //redisAgentManager.save(freeAgent.getUser().getUsername() + ":agents", agentManager.getRedisKey(freeAgent), freeAgent);
                                log.debug("Saved redis agent:" + freeAgent.toInfoString());

                                log.debug("user:" + username + ": " + "Deleting from queue:" + agentMonitorUcid);
                                redisGeneralQueueManager.deleteFromQueue(did, username, agentMonitorUcid, callerId, skillName);
                            } else {
                                log.debug("user:" + username + ": " + "----------------No free agent available");
                            }
                        } else {
                            log.debug("user:" + username + ": " + "----------------No free agent available");
                        }
                    }//end if - second step agents check
                } else {
                    log.debug("user:" + username + ": " + "----------------No free agent available in first step only {idle ∩ loggedin ∩ skillagents}");

//                    if (log.isTraceEnabled()) {
//                        log.trace("-----------------------Where are they then-----------------------");
//                        for (Agent.State state : Agent.State.values()) {
//                            log.trace(username + ":state:" + state + " = " + redisAgentManager.smembers(username + ":state:" + state));
//                        }
//                        log.trace("----------------------------------------------------------------");
//                    }
                }

            } // <----- End of synchronized block.

            if (freeAgent == null && !addedToQueue) {//if no free agent available add to queue.
                long queuePosition = redisGeneralQueueManager.checkQueue(did, username, agentMonitorUcid, callerId, skill, callStartTime);
                qXml.e("P").t("" + (queuePosition + 1));
            }

            log.info("|||||||||| Left synchronized block..");

            return xmlb.asString();
        } catch (ParserConfigurationException | FactoryConfigurationError | JsonSyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
            jedisSentinelPool.returnResource(jedis);
        }

        return "<response/>";

    }

    public void setRedisCampaignManager(RedisManager<Campaign> redisCampaignManager) {
        this.redisCampaignManager = redisCampaignManager;
    }

    public void setRedisSkillManager(RedisManager<Skill> redisSkillManager) {
        this.redisSkillManager = redisSkillManager;
    }

    public void setRedisAgentManager(RedisManager<Agent> redisAgentManager) {
        this.redisAgentManager = redisAgentManager;
    }

    public void setRedisGeneralQueueManager(RedisGeneralQueueManager redisGeneralQueueManager) {
        this.redisGeneralQueueManager = redisGeneralQueueManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public void setJedisSentinelPool(JedisPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
    }

    public static void setLog(Logger log) {
        RedisGetNextFreeAgentServiceImplNew.log = log;
    }
    //
    // ----- > Redis managers
    private RedisManager<Campaign> redisCampaignManager;
    private RedisManager<Skill> redisSkillManager;
    private RedisManager<Agent> redisAgentManager;
    private RedisGeneralQueueManager redisGeneralQueueManager;
    //
    // -----> Regular managers
    private AgentManager agentManager;
    //
    // ----- > Properties
    private JedisPool jedisSentinelPool;
    private static Logger log = Logger.getLogger(RedisGetNextFreeAgentServiceImplNew.class);
}

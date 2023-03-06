package com.ozonetel.occ.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.asteriskjava.live.AsteriskServer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.Agent.State;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DataManager;
import com.ozonetel.occ.service.DialNumberManager;
import com.ozonetel.occ.service.OCCManager;
import com.ozonetel.occ.util.AppContext;
import org.springframework.context.ApplicationContext;


/**
 *	CampaignJob.java
 *	NarayanaBabu.Nalluri
 *	Date : Oct 27, 2010
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */
public class CampaignJob {
	
	protected Log log = LogFactory.getLog(CampaignJob.class);
	
//	protected WebApplicationContext webApplicationContext = null;
	protected ApplicationContext webApplicationContext = null;
	
	protected Queue<Data> beanQueue = new LinkedList<Data>();
	
	protected CampaignManager campaignManager;
	
	protected AgentManager agentManager;
	
	protected DataManager dataManager;

        protected DialNumberManager dialNumberManager;
        protected OCCManager occManager;
	
	protected Campaign campaign;
	
	public CampaignJob(Long campId) {
		
//		webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
	webApplicationContext = AppContext.getApplicationContext();	
            agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
		campaignManager = (CampaignManager)webApplicationContext.getBean("campaignManager");
		dataManager = (DataManager)webApplicationContext.getBean("dataManager");
		dialNumberManager = (DialNumberManager)webApplicationContext.getBean("dialNumberManager");
		dialNumberManager = (DialNumberManager)webApplicationContext.getBean("dialNumberManager");
		occManager = (OCCManager) webApplicationContext.getBean("occManager");
		campaign = campaignManager.get(campId);
		
		// Supports Resume campaign : Loading the phone numbers whose playback_info is NULL 
//		beanQueue.clear();
//		List<Data> datas = dataManager.getDataByCampaignIdAndCalledInfo(campaign.getCampaignId());
//		//List<Data> datas = dataManager.getDataByCampaignId(campaign.getCampaignId());
//		for (Data data : datas) {
//			beanQueue.add(data);
//		}
	}
	
	protected boolean numbersRemainingToBeCalled() {
		
		if(campaign.getCurrentTrail() > 0 && (campaign.getCurrentTrail() < campaign.getRuleNot() * 3))
			return true;
	
		for(Data data: beanQueue){
			log.debug("mcb.getState() : "+data.getState());
			if (data.getState().equals("Fail")) {
				return true;
			}
		}
		return false;
	}

	public void originateCallCount(int callCount) {
		try {
//			occManager.setDialedCallCount(OCCManager.getDialedCallCount()+ callCount);
			Campaign c = campaignManager.get(campaign.getCampaignId());
			String position = c.getPosition();
			log.debug("Campaign Position : "+position);
			if (null != position && (("PAUSED".equalsIgnoreCase(position.trim()) || "CREATED".equalsIgnoreCase(position.trim())) )) {
				boolean isCampaignComplete = "COMPLETED".equalsIgnoreCase(position.trim());
				if(isCampaignComplete){
					releaseAgents();
				}
				return;
			}
			if (!numbersRemainingToBeCalled()) {
					makeCampaignComplete();
			} else {
				log.debug("Calling numbers:"+callCount);
				callNumbers(callCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	protected void releaseAgents() {
		log.debug("Releasing Agents.....!!!!");
		List<Agent> agents = agentManager.getAgentsByCampaign(campaign.getCampaignId());
		for (Agent agent : agents) {
                        log.debug("Releasing Agent ="+agent.getAgentId());
			if(agent.getState() != State.BUSY) {
//				agent.setCampaign(null);
				agent.setCampaignId(null);
//				agent.setState(State.AUX);
                                //Setting Idle Instead of Aux. We need to Explicitly make them Aux
				agent.setState(State.IDLE);
				agent = agentManager.save(agent);
				// Whenever the campaing is Completed we are making all agents to LOGOUT
				// Because Agent has to assign to any Campaign before Login
				if(null == webApplicationContext){
					webApplicationContext = AppContext.getApplicationContext();
				}
//				String customerId = webApplicationContext.getServletContext().getInitParameter("customerId");
//				OCCManager.requestTB("LogoutAgentAction", agent.getAgentId() + "|" + customerId);
			}
		}
		
	}

	//Every time we move to a new iteration, we get the list of failed customers from the data table and set them to the map
	//Also we increment the campaingTrail value and save the campaign state.
	protected void moveToNextIteration(){
		campaign = campaignManager.get(campaign.getCampaignId());
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {e.printStackTrace();
		}
		Integer currentTrail = campaign.getCurrentTrail();
		log.debug("*************Query tried_number : "+currentTrail);
		
		// support Resume Campaign : using playback_info as a flag and marking call as either called or null
		List<Data> datas = dataManager.getDataByCampaignId(campaign.getCampaignId());
		for (Data data : datas) {
			data.setPlayback_info(null);
			dataManager.save(data);
		}
		
		beanQueue.clear();
		for (Data data : datas) {
			log.debug("*************Adding : "+data);
			beanQueue.add( data);
		}
		int tries = currentTrail.intValue();
		log.debug("INSIDE FUNCTION  FINISHED ITERATION: "+tries);
		tries = tries + 1;
		log.debug("Next Iteration ::::: " + tries);

		campaign.setCurrentTrail(new Integer(tries));
		
		//log.debug("CurretnTrail***********"+campaign+" --- "+campaign.getCurrentTrail());
		campaign = campaignManager.save(campaign);
		//log.debug("Updated campaign when moving to next iteration:"+campaign.toString());
	}
	
	protected void makeCampaignComplete(){
		campaign.setPosition("COMPLETED");
		campaign = campaignManager.save(campaign);
		log.debug("Finished campaign in callNumbers:"+ campaign.toString());
		log.debug("Campaign is COMPLETED ...");
		releaseAgents();
	}
	
	protected void callNumbers(int callCount) {
		String destination;
		for (int i = 0; i < callCount; i++) {
			try {
				Data bean = null;
				while (true) {
					if (beanQueue.size() > 0) {
						bean = beanQueue.poll();
						log.debug("Getting next phone number to call : "+bean.getDest());
					} else {
						// log.debug("Before resetting iterator :"+campaign.toString());
						//beanListIterator = beanMap.values().iterator();
						// log.debug("After resetting iterator :"+campaign.toString());
						log.debug("Current Trail : "+campaign.getCurrentTrail());
						if (campaign.getCurrentTrail() > 0 && (campaign.getCurrentTrail() < campaign.getRuleNot() * 3)) {
							moveToNextIteration();
						}else{
							makeCampaignComplete();
							return;
						} 
						if(beanQueue.size() > 0){
							bean = beanQueue.poll();
							log.debug("Getting next phone number to call : "+bean.getDest());
						}else{
							makeCampaignComplete();
							return;
						}
					}
					destination = bean.getNextNumber();
					bean.setIndex(bean.getIndex()+1);
					log.debug("Previous State : "+bean.getState());
					Data d = dataManager.get(bean.getData_id());
					log.debug("Current State : "+d.getState());
					bean.setState(d.getState());
					
					dataManager.save(bean);
					if (null == destination) {
						continue;
					}
					log.debug("****** State Before Calling Number *****");
					log.debug("Position of Campaign "+campaign.getCampignName()+" is "+campaign.getPosition());
					log.debug("Iteration of Campaign "+campaign.getCurrentTrail());
					log.debug("Calling Number : "+destination+" -- State : "+bean.getState());
					//log.debug("Idle Agents Count : "+OCCManager.getIdleAgents(campaign.getCampaignId()));
//					log.debug("Idle Agents Count : "+occManager.getIdleAgents());
					
					if (bean.getState().equals("Fail")) {
						// log.debug("This bean satifies all requirements. It has a fail state and number"+bean.getData_id());
						break;
					} else {
						// log.debug("As bean position is Success moving to next bean to verify");
						continue;
					}
				}
				Map<String, String> map = new HashMap<String, String>();
				map.put("oz_ani", destination);
				String valueOf = String.valueOf(bean.getData_id());
				map.put("data_id", valueOf);
				map.put("call_data", bean.getCall_data());
				map.put("campaign_id", String.valueOf(campaign.getCampaignId()));
				map.put("port", "2"); // hardcoded particullary for tellsmart
				String string = campaign.getCurrentTrail().toString();
				//log.debug("*************Sending tried_number : "+string);
				map.put("tried_number", string);
				
				log.debug("OCCD REQUEST [to:CallServer,action:OriginateCall,oz_ani:"+destination+",data_id:"+valueOf+",call_data:"+bean.getCall_data()+",campaign_id:"+String.valueOf(campaign.getCampaignId())+",tried_number:"+string+"]");
				AsteriskServer asteriskServer = SingletonAsteriskServer.getAsteriskServer();
				if(null != asteriskServer)
					asteriskServer.originateToExtensionAsync("local/2222@dialer","call_queue", "2235", 1, 15000L, null, map, null);
				else
					log.error("Connection With AsteriskServer got Failed.");
				
				//Support Resume Campaign : by making playback_info 'called'
				Data data = dataManager.get(bean.getData_id());
				data.setPlayback_info("called");
				dataManager.save(data);
				
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
	}
}
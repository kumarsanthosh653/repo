/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service.impl;

import java.util.List;


import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.DialNumber;
import com.ozonetel.occ.service.OCCManager;
import java.util.HashMap;
import java.util.Map;
import org.asteriskjava.live.AsteriskServer;

/**
 *	CampaignJobByAgent.java
 *	Rajesh
 *	Date : Oct 27, 2010
 *	Email : rajesh@ozonetel.com
 */
public class CampaignJobByAgent extends CampaignJob {

    public CampaignJobByAgent(Long campId) {
        super(campId);
        
    }

//    @Override
    public void originateCallCount(String campId) {
        try {
            campaign = campaignManager.get(new Long(campId));
            Campaign c = campaignManager.get(campaign.getCampaignId());
            String position = c.getPosition();
            log.debug("Campaign Position : " + position);
            if (null != position && ("PAUSED".equalsIgnoreCase(position.trim()) || "CREATED".equalsIgnoreCase(position.trim()))) {
                boolean isCampaignComplete = "COMPLETED".equalsIgnoreCase(position.trim());
                if (isCampaignComplete) {
                    releaseAgents();
                }
                return;
            }
            //Check whether the Camapign has More Data
            if (dataManager.isCampaignComplete(campaign.getCampaignId())) {
                makeCampaignComplete();
            }else {
               //Check if the Iteration completes
//               log.debug("Checking if the Iteration is Complete="+dataManager.getDataByCampaignIdAndCalledInfo(campaign.getCampaignId()));
               if(dataManager.getDataByCampaignIdAndCalledInfo(campaign.getCampaignId()) == null){
                moveToNextIteration();
                }
                this.callNumbers(campId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Override
    protected void moveToNextIteration() {
        log.debug("Moving to Next Iteration");
        campaign = campaignManager.get(campaign.getCampaignId());
//        try {
//            Thread.sleep(5000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // support Resume Campaign : using playback_info as a flag and marking call as either called or null
        List<Data> datas = dataManager.getDataByCampaignId(campaign.getCampaignId());
        for (Data data : datas) {
            data.setPlayback_info(null);
            dataManager.save(data);
        }
        campaign.setCurrentTrail(campaign.getCurrentTrail() + 1);
        //log.debug("CurretnTrail***********"+campaign+" --- "+campaign.getCurrentTrail());
        campaign = campaignManager.save(campaign);
        //log.debug("Updated campaign when moving to next iteration:"+campaign.toString());
    }

//    @Override
    protected void callNumbers(String campId) {
        //Forcing to Make stop for 2 seconds for each call
        //b`coz of screen pop(.net) sending the events IDLE folowed by Pause so in the Mean time
        //this application checks for Idle and Calling Numbers ., to stop that here we are forcing for 2 secs delay
                try {
                     log.debug("Before Calling Waiting for 2 Seconds");
                     Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                    e.printStackTrace();
                    }

        String destination;
        //get the List of Idle Agents By Campaign

        List<Agent> agents = agentManager.getIdleAgentsByCampaign(campaign.getCampaignId());

        log.debug("No of Idle Agents are =" + agents.size());
//        OCCManager.setDialedCallCount(OCCManager.getDialedCallCount() + agents.size());
        for (int i = 0; i < agents.size(); i++) {
            Agent a = agents.get(i);
            Data bean = dataManager.getDataByCampaignIdAndCalledInfo(campaign.getCampaignId(), a.getId());
            if (bean != null) {


                destination = bean.getNextNumber();
                log.debug("Got the  Number to Call is " + destination);
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
                //For Agent Wise Send Agent Also
                map.put("agentWise","true");
                map.put("agentId",a.getAgentId());
                map.put("did",campaign.getdId());
                
                log.debug("OCCD REQUEST [to:CallServer,action:OriginateCall,oz_ani:" + destination + ",data_id:" + valueOf + ",call_data:" + bean.getCall_data() + ",campaign_id:" + String.valueOf(campaign.getCampaignId()) + ",tried_number:" + string + "]");
                AsteriskServer asteriskServer = SingletonAsteriskServer.getAsteriskServer();
                if (null != asteriskServer) {
                    String dailerContext = "local/"+destination+"@dialer";
                    asteriskServer.originateToExtensionAsync(dailerContext, "call_queue", "2235", 1, 15000L, null, map, null);
//                    asteriskServer.originateToExtensionAsync("local/2222@dialer", "dialer", "2235", 1, 15000L, null, map, null);
                } else {
                    log.error("Connection With AsteriskServer got Failed.");
                }

//                                int triedNumber = Integer.parseInt(bean.getPlayback_info())+1;
//				bean.setPlayback_info(""+triedNumber);


          /*      DialNumber dialNumber = new DialNumber();
                dialNumber.setAgentId(a.getAgentId());
                dialNumber.setUserId(a.getUser().getId());
                dialNumber.setOzAni(destination);
                dialNumber.setCallData(bean.getCall_data());
                dialNumber.setCampaignId(campaign.getCampaignId());
                dialNumber.setDid(campaign.getdId());
                dialNumber.setDataId(bean.getData_id());
                dialNumberManager.save(dialNumber);*/

                bean.setCurrentTrail(bean.getCurrentTrail() + 1);
                bean.setPlayback_info("called");
                bean.setIndex(bean.getIndex()+1);
                dataManager.save(bean);
            }else{
                log.debug("No Data Found for AGENT ["+a.getAgentId()+"] to Dial the Number");
                //Check whether the Camapign is Cpmpleted
                if (dataManager.isCampaignComplete(campaign.getCampaignId())) {
                makeCampaignComplete();
            }
            }
        }
    }
}

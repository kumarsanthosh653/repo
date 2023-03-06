package com.ozonetel.occ.webapp.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.CallBack;
import com.ozonetel.occ.model.CallDisposition;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Data;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.model.Role;
import com.ozonetel.occ.model.User;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CallBackManager;
import com.ozonetel.occ.service.CallDispositionManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DataManager;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.util.DateUtil;



public class DataAction extends BaseAction implements Preparable {
    private DataManager dataManager;
    
    private List datas = new ArrayList();
    private Data data;
    private Long  data_id;
  //  private CallGroup callGroup;
  //  private CallGroupManager callGroupManager;
    private Long disposition;
    private String date;
    private String message;
	private String hour;
	private String minute;
	
	 DispositionManager dispositionManager;
	 
	 private Campaign campaign;
	    
	    private CampaignManager campaignManager;
	    
	    private Long campaignId;

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public Long getDisposition() {
		return disposition;
	}

	public void setDisposition(Long disposition) {
		this.disposition = disposition;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
    
    public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	List<Campaign> campaignList = new ArrayList<Campaign>();
    
    
  //  List<CallGroup> callGroupList = new ArrayList<CallGroup>(); 

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public List getDatas() {
        return datas;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String dataId = getRequest().getParameter("data.data_id");
            if (dataId != null && !dataId.equals("")) {
                data = dataManager.get(new Long(dataId));
            }
        }
    }

 //   List<Campaign> campaignList = new ArrayList<Campaign>();
    
    
    
    
    public String search(){
    	 Long campaignId2 = getCampaignId();
    	 String userName = getRequest().getRemoteUser();
         User user = userManager.getUserByUsername(userName);
         log.debug("SelectedCampaign : "+campaignId2);
         campaignList =  campaignManager.getCampaignsByUserId(user.getId());
         if(null != campaignId2) {
 			datas.addAll(dataManager.getDataByCampaignId(campaignId2));
 		//	log.debug("DataList : "+dataList);
         }else{
         	datas = dataManager.getAll();
         }
         return SUCCESS;
    }
    
    AgentManager agentManager;
	CallBackManager callBackManager;
	CallDispositionManager callDispManager;

	public void setCallDispManager(CallDispositionManager callDispManager) {
		this.callDispManager = callDispManager;
	}

	public void setCallBackManager(CallBackManager callBackManager) {
		this.callBackManager = callBackManager;
	}

	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}
    
    public String saveDisposition(){
    	
    	String agentId = getRequest().getParameter("agentId");
    	String callerId = getRequest().getParameter("callerId");
    	String reason = dispositionManager.get(getDisposition()).getReason();
    	log.debug("Disposition : "+reason);
    	try{
			if(!"CallBack".equalsIgnoreCase(reason)){
				log.debug("Received Call Disposition..!!!!");
				CallDisposition disp = new CallDisposition();
				final Agent agent = agentManager.getAgentByAgentIdV2(getRequest().getRemoteUser(),agentId);
				if(null != agent){
					disp.setAgent(agent);
					disp.setCallDate(Calendar.getInstance().getTime());
//					disp.setCampaign(agent.getCampaignId());
					disp.setCallerId(callerId);
					disp.setDisposition(reason);
					disp.setComments(getMessage());
					callDispManager.save(disp);
					saveMessage("Saved Call Disposition to DB..!!!!");
					log.debug("Saved Call Disposition to DB..!!!!");
				}else{
					saveErrors("Error: No Agent with id "+agentId);
					log.debug("Error: No Agent with id "+agentId);
				}
				//return "Success : Saving Call Dispositions";
			}else{
				CallBack callBack = new CallBack();
				final Agent agent = agentManager.getAgentByAgentIdV2(getRequest().getRemoteUser(),agentId);
				if(null != agent){
					callBack.setAgent(agent);
					String time = (getHour() != null && !getHour().equals("") ? getHour() : "00")+":"+(getMinute() != null && !getMinute().equals("") ? getMinute(): "00");
					callBack.setCallbackDate(DateUtil.convertStringToDate("MM/dd/yyyy HH:mm",getDate()+" "+time));
					callBack.setCallbackNumber(callerId);
					callBack.setComments(getMessage());
					callBackManager.save(callBack);
					saveMessage("Saved Callback to DB..!!!!");
					log.debug("Saved CallBack to DB..!!!!");
				}else{
					saveMessage("Error: No Agent with id "+agentId);
					log.debug("Error: No Agent with id "+agentId);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			//return "Error : Saving Call Dispositions";
		}
		getRequest().setAttribute("dispStatus", "Completed");
		return SUCCESS;
    }

   

	public void setDispositionManager(DispositionManager dispositionManager) {
		this.dispositionManager = dispositionManager;
	}

	public List<Disposition> getDispositions() {
		return dispositionManager.getAll();
	}
    
    public String screenPopData(){
    	
    	//String agentId = getRequest().getParameter("agentId");
    	//String callerId = getRequest().getParameter("callerId");
    	String dataId = getRequest().getParameter("dataId");
    	//datas = dataManager.getDataByPhoneNumber(callerId);
    	if(null != dataId && null != datas){
    		datas.clear();
    		datas.add(dataManager.get(new Long(dataId)));
    	}
    	return SUCCESS;
    }
    
    public String list() {
        //datas = dataManager.getAll();
    	datas.clear();
    	List<Data> dataList = new ArrayList<Data>();
    	String userName = getRequest().getRemoteUser();
        User user = userManager.getUserByUsername(userName);
       /* List<CallGroup> callGroupList =  callGroupManager.getGroupsByUserId(user.getId());
        for (Iterator iterator = callGroupList.iterator(); iterator.hasNext();) {
			CallGroup callGroup = (CallGroup) iterator.next();
			dataList.addAll(dataManager.getDataByCallGroupId(callGroup.getCallGroupId()));
		}*/
       /* Long campaignId2 = getCampaignId();
        log.debug("SelectedCampaign : "+campaignId2);
        campaignList =  campaignManager.getCampaignsByUserId(user.getId());
        if(null != campaignId2) {
			datas.addAll(dataManager.getDataByCampaignId(campaignId2));
			log.debug("DataList : "+dataList);
        }else{
        	datas = dataManager.getAll();
        }
        */
        Role adminRole = roleManager.getRole("ROLE_ADMIN");
        if(null != user && !user.getRoles().contains(adminRole)){
        	campaignList =  campaignManager.getCampaignsByUserId(user.getId());
        	for (Iterator iterator = campaignList.iterator(); iterator.hasNext();) {
    			Campaign campaign = (Campaign) iterator.next();
    			datas.addAll(dataManager.getDataByCampaignId(campaign.getCampaignId()));
    		}
	     //   datas = dataList;
        } /*else{
        	datas = dataManager.getAll();
        }*/else{
        	campaignList =  campaignManager.getCampaignsByUserId(user.getId());
        	datas = dataManager.getAll();
        }
        
     //   datas = dataManager.getAll();
       
        return SUCCESS;
    }
    
    
 /*   public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}
*/
	public void setData_id(Long  data_id) {
        this. data_id =  data_id;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String delete() {
        dataManager.remove(data.getData_id());
        saveMessage(getText("data.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (data_id != null) {
            data = dataManager.get(data_id);
        } else {
            data = new Data();
        }
        String userName = getRequest().getRemoteUser();
        User user = userManager.getUserByUsername(userName);
        campaignList =  campaignManager.getCampaignsByUserId(user.getId());
        return SUCCESS;
    }

    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }

        if (delete != null) {
            return delete();
        }

        boolean isNew = (data.getData_id() == null);
        
        Long campaignId = Long.valueOf(getRequest().getParameter("data.campaign.campaignId"));
        Campaign campaign = campaignManager.get(campaignId);
        if(null != campaign)
        	log.debug("Campaign Name : "+campaign.getCampignName());
        
        data.setCampaign(campaign);
        
        dataManager.save(data);

        String key = (isNew) ? "data.added" : "data.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public CampaignManager getCampaignManager() {
		return campaignManager;
	}

	public void setCampaignManager(CampaignManager campaignManager) {
		this.campaignManager = campaignManager;
	}

	public List<Campaign> getCampaignList() {
		
		
		return campaignList;
	}

	public void setCampaignList(List<Campaign> campaignList) {
		this.campaignList = campaignList;
	}
    
    

	/*public CallGroupManager getCallGroupManager() {
		return callGroupManager;
	}

	public void setCallGroupManager(CallGroupManager callGroupManager) {
		this.callGroupManager = callGroupManager;
	}

	public List<CallGroup> getCallGroupList() {
		return callGroupList;
	}

	public void setCallGroupList(List<CallGroup> callGroupList) {
		this.callGroupList = callGroupList;
	}

	public CallGroup getCallGroup() {
		return callGroup;
	}

	public void setCallGroup(CallGroup callGroup) {
		this.callGroup = callGroup;
	}*/
}
package com.ozonetel.occ.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.Constants;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.Campaign;
import com.ozonetel.occ.model.Disposition;
import com.ozonetel.occ.model.PreviewData;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.CampaignManager;
import com.ozonetel.occ.service.DispositionManager;
import com.ozonetel.occ.service.PreviewDataManager;
import com.ozonetel.occ.util.AppContext;
import org.springframework.context.ApplicationContext;

public class PreviewDataAction extends BaseAction implements Preparable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PreviewDataManager previewDataManager;
    private DispositionManager dispositionManager;
    
    private List<PreviewData> previewDatas;
    
    private CampaignManager campaignManager;
    
    private List<PreviewData> previewReports;
    
    private Long campaignId;
    
    private String agentId;
    
    private AgentManager agentManager;
    
    private PreviewData previewData;
    
    private Long  preview_data_id;
    

    public void setPreviewDataManager(PreviewDataManager previewDataManager) {
        this.previewDataManager = previewDataManager;
    }
    
    public void setAgentManger(AgentManager agentManager) {
		this.agentManager = agentManager;
	}
    
    public void setCampaignManager(CampaignManager campaignManager) {
		this.campaignManager = campaignManager;
	}
    
    public void setDispositionManager(DispositionManager dispositionManager) {
		this.dispositionManager = dispositionManager;
	}

    public List<PreviewData> getPreviewDatas() {
        return previewDatas;
    }

    public List<Disposition> getDispositions() {
		return dispositionManager.getAll();
	}
    
    
    public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public List<PreviewData> getPreviewReports() {
		return previewReports;
	}

	public void setPreviewReports(List<PreviewData> previewReports) {
		this.previewReports = previewReports;
	}

	/**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String previewDataId = getRequest().getParameter("previewData.preview_data_id");
            if (previewDataId != null && !previewDataId.equals("")) {
                previewData = previewDataManager.get(new Long(previewDataId));
            }
        }
    }
    

    public List<Agent> getAgentList() {
    	if(null == agentManager){
//    		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
    		ApplicationContext webApplicationContext = AppContext.getApplicationContext();
    		agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
    	}
    	
    	return agentManager.getAll();
    }
    
    public List<Campaign> getCampaignList() {
        List<Campaign> campaigns = new ArrayList<Campaign>();
        campaigns.add(null);
        if (getRequest().isUserInRole(Constants.ADMIN_ROLE)) {
            campaigns.addAll(campaignManager.getAll());
        } else {
            campaigns.addAll(campaignManager.getCampaignsByUserId(userManager.getUserByUsername(getRequest().getRemoteUser()).getId()));
        }
        return campaigns;
    }
    
    public String report() {
    	
    	System.out.println("Preview Data Report...!!");
    	 if(null != previewReports)
    		 previewReports.clear();
         boolean isExec = true;
         StringBuilder queryString = new StringBuilder("select distinct r from PreviewData r where ");
         
         Map<String,Object> params = new HashMap<String, Object>();
         System.out.println("campaignId : "+campaignId);
         if (campaignId != null && !campaignId.equals("")) {
             queryString.append("r.campaign.campaignId =:campaignId and ");
             log.debug("getCampaignId() : "+getCampaignId());
             params.put("campaignId", getCampaignId());
         }
         
         System.out.println("agentId : "+agentId);
         if (agentId != null && !agentId.equals("")) {
             queryString.append("r.agentId =:agentId and ");
             log.debug("getAgentId() : "+getAgentId());
             params.put("agentId", getAgentId());
         }
         
        /* if (getStatus() != null && !getStatus().equals("")) {
             queryString.append("r.status =:status and ");
             if (getStatus().equalsIgnoreCase("fail")) {
                // queryString.append("r.campaign.campaignId = c.campaignId and r.triedNumber = c.ruleNot ");
             	 queryString.append("r.campaign.campaignId = c.campaignId");
             }
             log.debug("getStatus() : "+getStatus());
             params.put("status", getStatus());
         }
*/
         if (queryString.toString().endsWith("where ")) {
             queryString.delete(queryString.toString().length() - 6, queryString.toString().length());
             isExec = false;
         } else if (queryString.toString().endsWith("and ")) {
             queryString.delete(queryString.toString().length() - 4, queryString.toString().length());
         }

         queryString.append("order by r.campaign.campaignId desc");
         log.debug("queryString.toString() : "+queryString.toString());
         if (isExec) {
        	 previewReports = previewDataManager.findByNamedParams(queryString.toString(), params);
         }
         if(null != previewReports)
         	log.debug("Reports Size : "+previewReports.size());
         
         return SUCCESS;
    }

    public String list() {
    	String agentId = getRequest().getParameter("agentId");
    	if(null == agentId)
    		agentId = String.valueOf(getRequest().getSession().getAttribute("agentId"));
    	
       // previewDatas = previewDataManager.getAll();
    	if(null == agentManager){
//    		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
    		ApplicationContext webApplicationContext = AppContext.getApplicationContext();
    		agentManager = (AgentManager) webApplicationContext.getBean("agentManager");
    	}
    	if(null != agentId){
    		getRequest().getSession().setAttribute("agentId", agentId);
    		if(null != agentManager){
//	    		Campaign c = ̧agentManager.getCampaignByAgentId(agentId);
	    		Campaign c = null;
	    		if(null != c)
	    			previewDatas = previewDataManager.getDataByAgentIdAndCamapign(agentId, c.getCampaignId());
	    		else
	    			previewDatas = previewDataManager.getDataByAgentId(agentId);
    		}else{
    			System.out.println("AgentManager is NULL");
    			previewDatas = previewDataManager.getDataByAgentId(agentId);
    		}
    	}else{
    		System.out.println("getting All Preview datas");
    		previewDatas = previewDataManager.getAllDistinct();
    	}
        return SUCCESS;
    }

    public void setPreview_data_id(Long  preview_data_id) {
        this. preview_data_id =  preview_data_id;
    }

    public PreviewData getPreviewData() {
        return previewData;
    }

    public void setPreviewData(PreviewData previewData) {
        this.previewData = previewData;
    }

    public String delete() {
        previewDataManager.remove(previewData.getId());
        saveMessage(getText("previewData.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (preview_data_id != null) {
            previewData = previewDataManager.get(preview_data_id);
        } else {
            previewData = new PreviewData();
        }

        return SUCCESS;
    }

    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }

        if (delete != null) {
            return delete();
        }

        boolean isNew = (previewData.getId() == null);

        previewDataManager.save(previewData);

        String key = (isNew) ? "previewData.added" : "previewData.updated";
        saveMessage(getText(key));

        return SUCCESS;
        
       /* if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }*/
    }
}
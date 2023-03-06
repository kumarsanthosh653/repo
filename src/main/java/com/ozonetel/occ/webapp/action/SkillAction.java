package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.model.Agent;
import com.ozonetel.occ.model.FwpNumber;
import com.ozonetel.occ.model.LabelValue;
import com.ozonetel.occ.model.Location;
import com.ozonetel.occ.service.SkillManager;
import com.ozonetel.occ.model.Skill;
import com.ozonetel.occ.service.AgentManager;
import com.ozonetel.occ.service.DialOutNumberManager;
import com.ozonetel.occ.service.FwpNumberManager;
import com.ozonetel.occ.service.LocationManager;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import org.hibernate.exception.ConstraintViolationException;

public class SkillAction extends BaseAction implements Preparable {

    private SkillManager skillManager;
    private List skills;

    private Skill skill;
    private Long id;
    private String[] assignedAgents;
    private String[] assignedFwpNumbers;
    List<Agent> agentList = new ArrayList<Agent>();

    private AgentManager agentManager;

    private DialOutNumberManager dialOutNumberManager;
    private LocationManager locationManager;
    private FwpNumberManager fwpNumberManager;
    private List dialOutNumbers;
    
    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public List getSkills() {
        return skills;
    }

    public List<Location> getLocations() {
        return locationManager.getLocationsByUser(getRequest().getRemoteUser());
    }

    public List getDialOutNumbers(){
        return dialOutNumbers = dialOutNumberManager.getDialOutNumbersByUser(getRequest().getRemoteUser());
    }


     public List<Agent> getAgentList() {
        /**
         * to show all agents to all skills, but agent will be assigned to one Skill only
         */
        List<Agent> myAgList = new ArrayList<Agent>();
        List<Agent> agents = agentManager.getAgentsByUser(getRequest().getRemoteUser());
//        for (Agent agent : agents) {
//            if (agent.getSkill() == null) {
//                myAgList.add(agent);
//            } else {
//                if (agent.getSkill().getId().equals(id)) {
//                    myAgList.add(agent);
//                }
//            }
//        }
//        return myAgList;
        return agents;
    }
     public void setAgentList(List<Agent> agentList) {
        this.agentList = agentList;
    }
    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String skillId = getRequest().getParameter("skill.id");
            if (skillId != null && !skillId.equals("")) {
                skill = skillManager.get(new Long(skillId));
            }
        }
    }

    public String list() {
        skills = skillManager.getSkillsByUser(getRequest().getRemoteUser());
        return SUCCESS;
    }

    public String[] getAssignedAgents() {
        return assignedAgents;
    }

    public void setAssignedAgents(String[] assignedAgents) {
        this.assignedAgents = assignedAgents;
    }
    public String[] getAssignedFwpNumbers() {
        return assignedFwpNumbers;
    }

    public void setAssignedFwpNumbers(String[] assignedFwpNumbers) {
        this.assignedFwpNumbers = assignedFwpNumbers;
    }


    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }
    
    public FwpNumberManager getFwpNumberManager() {
        return fwpNumberManager;
    }

    public void setFwpNumberManager(FwpNumberManager fwpNumberManager) {
        this.fwpNumberManager = fwpNumberManager;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public String delete() {
        skillManager.remove(skill.getId());
        saveMessage(getText("skill.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            skill = skillManager.get(id);
            Set<Agent> agents = skill.getAgents();
            Set<FwpNumber> fwpNumbers = skill.getFwpNumbers();
            assignedAgents = new String[agents.size()];
            assignedFwpNumbers = new String[fwpNumbers.size()];
            
            int i = 0;
            for (Agent agent : agents) {
                String agentId = agent.getAgentId();
                assignedAgents[i] = agentId;
                i++;
            }
            i =0;
            for (FwpNumber fwpNumber : fwpNumbers) {
                String fwp = fwpNumber.getId().toString();
                assignedFwpNumbers[i] = fwp;
                i++;
            }
        } else {
            skill = new Skill();
        }

        //get The List of Available agents
        List<Agent> availableAgents = agentManager.getAgentsByUser(getRequest().getRemoteUser());
        
        availableAgents.removeAll(skill.getAgents());

        List<LabelValue> aList = new ArrayList<LabelValue>();
        for (Agent agent : availableAgents) {
        	aList.add(new LabelValue(agent.getAgentName(), agent.getAgentId()));
		}
        
        getRequest().getSession().setAttribute("availableAgents", aList);
       
        //get the List of available Fwp Numbers
        List<FwpNumber> availableFwpNumbers = fwpNumberManager.getFwpNumbersByUser(getRequest().getRemoteUser());
        
        availableFwpNumbers.removeAll(skill.getFwpNumbers());

        List<LabelValue> fwpList = new ArrayList<LabelValue>();
        for (FwpNumber fwpNumber : availableFwpNumbers) {
        	fwpList.add(new LabelValue(fwpNumber.getPhoneNumber(), fwpNumber.getId().toString()));
		}
        
        getRequest().getSession().setAttribute("availableFwpNumbers", fwpList);

        return SUCCESS;
    }

    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }

        if (delete != null) {
            return delete();
        }
//        log.debug("Get Diaout="+skill.getDialoutNumber().getId());
        boolean isNew = (skill.getId() == null);
        if (isNew) {
            skill.setUser(userManager.getUserByUsername(getRequest().getRemoteUser()));
        }

        skill.getAgents().clear(); // APF-788: Removing roles from user doesn't work
        String[] skillAgents = getRequest().getParameterValues("assignedAgents");
        if (skillAgents == null) {
            saveErrors(getText("campaign.agent.noselect"));
            return INPUT;
        } else {
            log.debug(skillAgents.length);
            for (int i = 0; skillAgents != null && i < skillAgents.length; i++) {
                String agent = skillAgents[i];
//                log.debug(agent);
                Agent agentByAgentId = agentManager.getAgentByAgentIdV2(getRequest().getRemoteUser(), agent);
                log.debug(agentByAgentId);
                if (null != agentByAgentId) {
                    skill.addAgent(agentByAgentId);
//                    log.debug("agent added");
                }
            }
        }
        
        //Assign Fwp Numbers to Skills
        skill.getFwpNumbers().clear(); 
        String[] skillFwpNumbers = getRequest().getParameterValues("assignedFwpNumbers");
        
            log.debug(getRequest().getParameterValues("assignedFwpNumbers"));
            for (int i = 0; skillFwpNumbers != null && i < skillFwpNumbers.length; i++) {
                String fwpNumberId = skillFwpNumbers[i];
                log.debug("FWP Numbers="+fwpNumberId);
                FwpNumber fwpNumber = fwpNumberManager.get(new Long(fwpNumberId));
                log.debug(fwpNumber);
                if (null != fwpNumber) {
                    skill.addFwpNumber(fwpNumber);
                    log.debug("fwpNumber added for Hunting");
                }
            }
        
        String key = "";
        try{
        skillManager.save(skill);
        key = (isNew) ? "skill.added" : "skill.updated";
        }catch(ConstraintViolationException e){
            key = "skill.uniqueError";
            log.debug("ConstraintViolationException");
        }catch(Exception e){
            key = "skill.uniqueError";
            log.debug("Unknown Exception");
        }

       
        saveMessage(getText(key));

//        if (!isNew) {
//            return INPUT;
//        } else {
            return SUCCESS;
//        }
    }

    /**
     * @return the dialOutNumberManager
     */
    public DialOutNumberManager getDialOutNumberManager() {
        return dialOutNumberManager;
    }

    /**
     * @param dialOutNumberManager the dialOutNumberManager to set
     */
    public void setDialOutNumberManager(DialOutNumberManager dialOutNumberManager) {
        this.dialOutNumberManager = dialOutNumberManager;
    }
}

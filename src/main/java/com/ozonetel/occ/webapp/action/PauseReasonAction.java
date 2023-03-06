package com.ozonetel.occ.webapp.action;

import com.opensymphony.xwork2.Preparable;
import com.ozonetel.occ.service.PauseReasonManager;
import com.ozonetel.occ.model.PauseReason;
import com.ozonetel.occ.webapp.action.BaseAction;

import java.util.List;

public class PauseReasonAction extends BaseAction implements Preparable {
    private PauseReasonManager pauseReasonManager;
    private List pauseReasons;
    private PauseReason pauseReason;
    private Long  id;

    public void setPauseReasonManager(PauseReasonManager pauseReasonManager) {
        this.pauseReasonManager = pauseReasonManager;
    }

    public List getPauseReasons() {
        return pauseReasons;
    }

    /**
     * Grab the entity from the database before populating with request parameters
     */
    public void prepare() {
        if (getRequest().getMethod().equalsIgnoreCase("post")) {
            // prevent failures on new
            String pauseReasonId = getRequest().getParameter("pauseReason.id");
            if (pauseReasonId != null && !pauseReasonId.equals("")) {
                pauseReason = pauseReasonManager.get(new Long(pauseReasonId));
            }
        }
    }

    public String list() {
        pauseReasons = pauseReasonManager.getPauseReasonByUser(getRequest().getRemoteUser());
        return SUCCESS;
    }

    public void setId(Long  id) {
        this. id =  id;
    }

    public PauseReason getPauseReason() {
        return pauseReason;
    }

    public void setPauseReason(PauseReason pauseReason) {
        this.pauseReason = pauseReason;
    }

    public String delete() {
        pauseReasonManager.remove(pauseReason.getId());
        saveMessage(getText("pauseReason.deleted"));

        return SUCCESS;
    }

    public String edit() {
        if (id != null) {
            pauseReason = pauseReasonManager.get(id);
        } else {
            pauseReason = new PauseReason();
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

        boolean isNew = (pauseReason.getId() == null);
        if(isNew){
            pauseReason.setUser(userManager.getUserByUsername(getRequest().getRemoteUser()));
        }
        pauseReasonManager.save(pauseReason);

        String key = (isNew) ? "pauseReason.added" : "pauseReason.updated";
        saveMessage(getText(key));

        if (!isNew) {
            return INPUT;
        } else {
            return SUCCESS;
        }
    }
}
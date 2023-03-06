/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ozonetel.occ.webapp.action;

import com.ozonetel.occ.service.HoldDetailManager;

/**
 *
 * @author rajeshchary
 */
public class HoldAction extends BaseAction{

    private HoldDetailManager holdDetailManager;

    public HoldDetailManager getHoldDetailManager() {
        return holdDetailManager;
    }

    public void setHoldDetailManager(HoldDetailManager holdDetailManager) {
        this.holdDetailManager = holdDetailManager;
    }

    public String save(){
        return SUCCESS;
    }

}

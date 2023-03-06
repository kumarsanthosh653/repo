/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import com.ozonetel.occ.model.HttpResponseDetails;

/**
 *
 * @author pavanj
 */
public interface CallbacksExecutorService {


    public HttpResponseDetails sendCallbackDetails(String url, String ucid,boolean dispositionSet,String disp,String comment);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import com.ozonetel.occ.model.IvrFlow;
import java.util.List;
import javax.jws.WebService;

/**
 *
 * @author rajeshdas
 */
@WebService
public interface IvrFlowManager extends GenericManager<IvrFlow, Long> {
    public List<IvrFlow> getFeedbackIVRList(Long userId);
}

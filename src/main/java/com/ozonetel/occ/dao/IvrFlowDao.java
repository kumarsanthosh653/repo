/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.dao;

import com.ozonetel.occ.model.IvrFlow;
import java.util.List;

/**
 *
 * @author rajeshdas
 */
public interface IvrFlowDao extends GenericDao<IvrFlow, Long> {
    public List<IvrFlow> getFeedbackIVRList(Long userId);
}

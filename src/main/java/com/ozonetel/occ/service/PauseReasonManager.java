package com.ozonetel.occ.service;

import com.ozonetel.occ.model.PauseReason;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface PauseReasonManager extends GenericManager<PauseReason, Long> {

    public List<PauseReason> getPauseReasonByUser(String userName);
}
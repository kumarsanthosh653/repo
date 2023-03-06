package com.ozonetel.occ.util;

import com.ozonetel.occ.model.CallEvent;

/**
 *
 * @author pavanj
 */
public interface CallListener {

    public void callCompleted(CallEvent callEvent);
    public void callStarted(CallEvent callEvent);
}

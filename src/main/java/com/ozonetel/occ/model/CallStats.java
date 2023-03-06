package com.ozonetel.occ.model;
/**
 *	CallStats.java
 *	NarayanaBabu.Nalluri
 *	Date : Aug 15, 2010
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */
public class CallStats {


	Integer activeCalls = 0;
	
	Integer callsRinging = 0;
	
	Integer callsInQueue = 0;
	
	Integer callsDialed = 0;
	
	Integer callsConnected = 0;
	
	Integer callsDropped = 0;
        
        String avgTalkTime = "0";
        
         String avgPickTime = "0";
        
	
	
	public Integer getCallsDropped() {
		return callsDropped;
	}

	public void setCallsDropped(Integer callsDropped) {
		this.callsDropped = callsDropped;
	}

	public Integer getActiveCalls() {
		return activeCalls;
	}

	public void setActiveCalls(Integer activeCalls) {
		this.activeCalls = activeCalls;
	}

	public Integer getCallsRinging() {
		return callsRinging;
	}

	public void setCallsRinging(Integer callsRinging) {
		this.callsRinging = callsRinging;
	}

	public Integer getCallsInQueue() {
		return callsInQueue;
	}

	public void setCallsInQueue(Integer callsInQueue) {
		this.callsInQueue = callsInQueue;
	}
	
	public Integer getCallsDialed() {
		return callsDialed;
	}

	public void setCallsDialed(Integer callsDialed) {
		this.callsDialed = callsDialed;
	}

	public Integer getCallsConnected() {
		return callsConnected;
	}

	public void setCallsConnected(Integer callsConnected) {
		this.callsConnected = callsConnected;
	}

    /**
     * @return the avgTalkTime
     */
    public String getAvgTalkTime() {
        return avgTalkTime;
    }

    /**
     * @param avgTalkTime the avgTalkTime to set
     */
    public void setAvgTalkTime(String avgTalkTime) {
        this.avgTalkTime = avgTalkTime;
    }

    /**
     * @return the avgPickTime
     */
    public String getAvgPickTime() {
        return avgPickTime;
    }

    /**
     * @param avgPickTime the avgPickTime to set
     */
    public void setAvgPickTime(String avgPickTime) {
        this.avgPickTime = avgPickTime;
    }
}


package com.ozonetel.occ.model;
/**
 *	AgentStats.java
 *	NarayanaBabu.Nalluri
 *	Date : Aug 12, 2010
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */
public class AgentStats {
		
		
		Integer agentsLoggedIn = 0;
		
		Integer agentsBusy = 0;
		
		Integer agentsWaiting = 0;
		
		Integer agentsPaused = 0;
		
                private Integer callsInQueue = 0;
		

		public Integer getAgentsLoggedIn() {
			return agentsLoggedIn;
		}

		public void setAgentsLoggedIn(Integer agentsLoggedIn) {
			this.agentsLoggedIn = agentsLoggedIn;
		}

		public Integer getAgentsBusy() {
			return agentsBusy;
		}

		public void setAgentsBusy(Integer agentsBusy) {
			this.agentsBusy = agentsBusy;
		}

		public Integer getAgentsWaiting() {
			return agentsWaiting;
		}

		public void setAgentsWaiting(Integer agentsWaiting) {
			this.agentsWaiting = agentsWaiting;
		}

		public Integer getAgentsPaused() {
			return agentsPaused;
		}

		public void setAgentsPaused(Integer agentsPaused) {
			this.agentsPaused = agentsPaused;
		}

    /**
     * @return the callsInQueue
     */
    public Integer getCallsInQueue() {
        return callsInQueue;
    }

    /**
     * @param callsInQueue the callsInQueue to set
     */
    public void setCallsInQueue(Integer callsInQueue) {
        this.callsInQueue = callsInQueue;
    }

		
	}

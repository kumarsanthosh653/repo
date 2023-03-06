package com.ozonetel.occ.model;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

/**
 *	Data.java
 *	@author NBabu
 *	Date  : Oct 28, 2009
 *	Email : nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */

@Entity
@Table(name="Data")
@NamedQueries ({
    @NamedQuery(name="data", query="select d from Data d where d.agent.agentId =:agentId and IFNULL(DATE(d.callBackTime),'') = DATE(current_date)"),
    @NamedQuery(name="dataByCamapignIdAndDisposition", query="select d from Data d where d.campaign.campaignId = :campaignId and d.disposition = :disposition"),
    @NamedQuery(name="dataByCamapignIdAndNotDisposition", query="select d from Data d where d.campaign.campaignId = :campaignId and d.disposition is null"),
    @NamedQuery(name="dataByCamapignId", query="select d from Data d where d.campaign.campaignId = :campaignId")
})
public class Data extends BaseObject{


	private Long data_id;

	private String state;

//	private Queue<String> dataArray = new LinkedList<String>();;

//        private String[] arr1=new String[3];
        
        private String[] dataArray = new String[3];
        
	private Integer index;

        private boolean isDone;

	private Integer currentTrail;

	private String dest;

	private String alt_dest;

	private String playback_info;

	private String Status;

	private String call_data;

	private Campaign campaign;

	private String accNumber;

	private String feedBack;

	private String poss;

	private String currBal;

	private String dpd;

        private int originalSize=0;
        
	private String creditLimit;

	private Date createDate;

	private Date dob;

	private String address;

        private String disposition;

        private Date callBackTime;

        private Agent agent;
	public Data(){

	}

	public Data(String[] arr){
               
                index =0;
		switch(arr.length)
		{
		case 1:
			dataArray[0]=arr[0];
			dataArray[1]=null;
			dataArray[2]=null;
                        originalSize = 1;
			break;
		case 2:
			dataArray[0]=arr[0];
			dataArray[1]=arr[1];
			dataArray[2]=null;
                        originalSize = 2;
			break;
		case 3:
			dataArray[0]=arr[0];
			dataArray[1]=arr[1];
			dataArray[2]=arr[2];
                        originalSize = 3;
			break;

		}
	}

	@Column(name="state")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

        @Transient
        public int getTriesRemaining()
        {
//            int done=numberOfAttempts()-Integer.parseInt(getPlayback_info());
            int done=numberOfAttempts()-getCurrentTrail();
            return done;
        }

        
        public int numberOfAttempts()
        {
            int numberOfTries=getCampaign().getRuleNot();
            return originalSize*numberOfTries;
        }
//        public void resetQueue(String[] arr){
//            switch(arr.length)
//		{
//		case 1:
//			dataArray.add(arr[0]);
//                        originalSize=1;
//			break;
//		case 2:
//			dataArray.add(arr[0]);
//                        dataArray.add(arr[1]);
//                        originalSize=2;
//			break;
//		case 3:
//			dataArray.add(arr[0]);
//                        dataArray.add(arr[1]);
//                        dataArray.add(arr[2]);
//                        originalSize=3;
//			break;
//
//		}
//        }
	@Transient
	public String getNextNumber() {
		String ret = dataArray[index];
		return ret;
	}
        
	@Transient
	public String[] getDataArray() {
		return dataArray;
	}
	public void setDataArray(String[] dataArray) {
		this.dataArray = dataArray;
	}


	@Column(name="data_index")
	public Integer getIndex() {
		return index;
	}


	public void setIndex(Integer index) {
		if(index!=null & index == originalSize){
			index = 0;
		}
		this.index = index;
	}

	@Column(name="current_trail")
	public Integer getCurrentTrail() {
            if(currentTrail != null){
            	return currentTrail;
            }else{
                return 0;
            }
	}

	public void setCurrentTrail(Integer currentTrail) {
		this.currentTrail = currentTrail;
	}


	@ManyToOne(targetEntity=Campaign.class)
    @JoinColumn(name="campaign_id")
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}


    @ManyToOne(targetEntity=Agent.class)
    @JoinColumn(name="agent_id")
	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="data_id")
	public Long getData_id() {
		return data_id;
	}

	public void setData_id(Long data_id) {
		this.data_id = data_id;
	}

	@Column(name="dest")
	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
            if(StringUtils.contains(dest,"~")){
		String[] arr=dest.split("~");
		switch(arr.length)
		{
		case 1:
			dataArray[0]=arr[0];
			dataArray[1]=null;
			dataArray[2]=null;
                        originalSize = 1;
			break;
		case 2:
			dataArray[0]=arr[0];
			dataArray[1]=arr[1];
			dataArray[2]=null;
                        originalSize = 2;
			break;
		case 3:
			dataArray[0]=arr[0];
			dataArray[1]=arr[1];
			dataArray[2]=arr[2];
                        originalSize = 3;
			break;

		}
            }
	}

	@Column(name="alt_dest")
	public String getAlt_dest() {
		return alt_dest;
	}

	public void setAlt_dest(String alt_dest) {
		this.alt_dest = alt_dest;
	}

	@Column(name="playback_info")
	public String getPlayback_info() {
//            if(playback_info == null)
//                return "0";
//            else
		return playback_info;
	}

	public void setPlayback_info(String playback_info) {
		this.playback_info = playback_info;
	}

	@Column(name="status")
	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	@Column(name="call_data")
	public String getCall_data() {
		return call_data;
	}

	public void setCall_data(String call_data) {
		this.call_data = call_data;
	}

	@Column(name="acc_number")
	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	@Column(name="feedback")
	public String getFeedBack() {
		return feedBack;
	}

	public void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}

	@Column(name="poss")
	public String getPoss() {
		return poss;
	}

	public void setPoss(String poss) {
		this.poss = poss;
	}

	@Column(name="curr_bal")
	public String getCurrBal() {
		return currBal;
	}

	public void setCurrBal(String currBal) {
		this.currBal = currBal;
	}

	@Column(name="dpd")
	public String getDpd() {
		return dpd;
	}

	public void setDpd(String dpd) {
		this.dpd = dpd;
	}

	@Column(name="credit_limit")
	public String getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}

	@Column(name="create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name="dob")
	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	@Column(name="address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

/*
	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return (data_id != null ? data_id.hashCode() : 0);
	}*/



	@Override
	public String toString() {
		return "next number="+getNextNumber();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 2;
		result = prime * result + ((Status == null) ? 0 : Status.hashCode());
		result = prime * result
				+ ((accNumber == null) ? 0 : accNumber.hashCode());
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result
				+ ((alt_dest == null) ? 0 : alt_dest.hashCode());
		result = prime * result
				+ ((call_data == null) ? 0 : call_data.hashCode());
		result = prime * result
				+ ((campaign == null) ? 0 : campaign.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((creditLimit == null) ? 0 : creditLimit.hashCode());
		result = prime * result + ((currBal == null) ? 0 : currBal.hashCode());
		result = prime * result
				+ ((currentTrail == null) ? 0 : currentTrail.hashCode());
		result = prime * result + Arrays.hashCode(dataArray);
		result = prime * result + ((data_id == null) ? 0 : data_id.hashCode());
		result = prime * result + ((dest == null) ? 0 : dest.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((dpd == null) ? 0 : dpd.hashCode());
		result = prime * result
				+ ((feedBack == null) ? 0 : feedBack.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result
				+ ((playback_info == null) ? 0 : playback_info.hashCode());
		result = prime * result + ((poss == null) ? 0 : poss.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		/*if (!super.equals(obj))
			return false;*/
		if (getClass() != obj.getClass())
			return false;
		Data other = (Data) obj;
		if (Status == null) {
			if (other.Status != null)
				return false;
		} else if (!Status.equals(other.Status))
			return false;
		if (accNumber == null) {
			if (other.accNumber != null)
				return false;
		} else if (!accNumber.equals(other.accNumber))
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (alt_dest == null) {
			if (other.alt_dest != null)
				return false;
		} else if (!alt_dest.equals(other.alt_dest))
			return false;
		if (call_data == null) {
			if (other.call_data != null)
				return false;
		} else if (!call_data.equals(other.call_data))
			return false;
		if (campaign == null) {
			if (other.campaign != null)
				return false;
		} else if (!campaign.equals(other.campaign))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (creditLimit == null) {
			if (other.creditLimit != null)
				return false;
		} else if (!creditLimit.equals(other.creditLimit))
			return false;
		if (currBal == null) {
			if (other.currBal != null)
				return false;
		} else if (!currBal.equals(other.currBal))
			return false;
		if (currentTrail == null) {
			if (other.currentTrail != null)
				return false;
		} else if (!currentTrail.equals(other.currentTrail))
			return false;
		if (!Arrays.equals(dataArray, other.dataArray))
			return false;
		if (data_id == null) {
			if (other.data_id != null)
				return false;
		} else if (!data_id.equals(other.data_id))
			return false;
		if (dest == null) {
			if (other.dest != null)
				return false;
		} else if (!dest.equals(other.dest))
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (dpd == null) {
			if (other.dpd != null)
				return false;
		} else if (!dpd.equals(other.dpd))
			return false;
		if (feedBack == null) {
			if (other.feedBack != null)
				return false;
		} else if (!feedBack.equals(other.feedBack))
			return false;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (playback_info == null) {
			if (other.playback_info != null)
				return false;
		} else if (!playback_info.equals(other.playback_info))
			return false;
		if (poss == null) {
			if (other.poss != null)
				return false;
		} else if (!poss.equals(other.poss))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

    /**
     * @return the isDone
     */
    @Column(name="isDone" )
    public boolean getIsDone() {
        return isDone;
    }

    /**
     * @param isDone the isDone to set
     */
    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * @return the dispositon
     */
    @Column(name="disposition")
    public String getDisposition() {
        return disposition;
    }

    /**
     * @param dispositon the dispositon to set
     */
    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    /**
     * @return the callBackTime
     */

    @Column(name="callbackdatetime")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getCallBackTime() {
        return callBackTime;
    }

    /**
     * @param callBackTime the callBackTime to set
     */
    public void setCallBackTime(Date callBackTime) {
        this.callBackTime = callBackTime;
    }
}
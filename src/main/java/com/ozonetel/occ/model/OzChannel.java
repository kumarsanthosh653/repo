package com.ozonetel.occ.model;

/**
 * OzChannel.java NarayanaBabu.Nalluri Date : Aug 28, 2010 Email :
 * nbabu@ozonetel.com, nb.nalluri@yahoo.com
 */
public class OzChannel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6729477715527555787L;

	private String pid;
	private String prt;
	private String ch;
	private String mode;
	private String org;
	private String dad;
	private String oad;
	private String rad;
	private String ctx;
	private String state;

	public OzChannel() {

	}

	public OzChannel(String pid, String prt, String ch, String mode,
			String org, String dad, String oad, String rad, String ctx,
			String state) {
		this.pid = pid;
		this.prt = prt;
		this.ch = ch;
		this.mode = mode;
		this.org = org;
		this.dad = dad;
		this.oad = oad;
		this.rad = rad;
		this.ctx = ctx;
		this.state = state;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPrt() {
		return prt;
	}

	public void setPrt(String prt) {
		this.prt = prt;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getDad() {
		return dad;
	}

	public void setDad(String dad) {
		this.dad = dad;
	}

	public String getOad() {
		return oad;
	}

	public void setOad(String oad) {
		this.oad = oad;
	}

	public String getRad() {
		return rad;
	}

	public void setRad(String rad) {
		this.rad = rad;
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}

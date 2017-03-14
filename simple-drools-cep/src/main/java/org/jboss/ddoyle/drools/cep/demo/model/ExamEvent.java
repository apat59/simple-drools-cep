package org.jboss.ddoyle.drools.cep.demo.model;

import java.io.Serializable;

/**
 * Seeb Message
 * @author apatrici
 *
 */
public class ExamEvent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1572579673902935058L;
	/**
	 * 
	 */

	public ExamEvent(String activity, String state, String sessionId, long timestamp, String detail) {
		super();
		this.activity = activity;
		this.state = state;
		this.sessionId = sessionId;
		this.timestamp = timestamp;
		this.detail = detail;
	}
	
	private String activity;
	private String state;
	private String detail;
	private String sessionId;
	private long timestamp;
	
	
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}

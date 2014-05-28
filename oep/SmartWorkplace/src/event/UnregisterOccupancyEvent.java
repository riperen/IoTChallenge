/* (c) 2014 AMIS.  All rights reserved. */
package event;

import org.json.simple.JSONObject;


public class UnregisterOccupancyEvent {
	private String cardId;
	private String deviceId;
	private String json="";
	
	public UnregisterOccupancyEvent(String cardId, String deviceId) {
		super();
		this.cardId = cardId;
		this.deviceId = deviceId;
		this.json=this.toJSON();
	}
	
	public UnregisterOccupancyEvent(Object cardId, Object deviceId) {
		super();
		
		this.cardId = cardId.toString();
		this.deviceId = deviceId.toString();
		this.json=this.toJSON();
	}
	
	public String toJSON() {
		JSONObject o = new JSONObject();
		
		o.put("cardId", this.cardId.toString());
		o.put("deviceId", this.deviceId);
		
		return o.toJSONString();
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	public String toString() {
		return "Occupancy [ cardID = " +this.cardId + ", deviceId = " + this.deviceId;
	}
}

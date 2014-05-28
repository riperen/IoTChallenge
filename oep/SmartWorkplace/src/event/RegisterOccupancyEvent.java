/* (c) 2014 AMIS.  All rights reserved. */
package event;

import org.json.simple.JSONObject;


public class RegisterOccupancyEvent {
	private String cardId;
	private String deviceId;
	private int roomId;
	private int deskNum;
	private String json="";
	
	public RegisterOccupancyEvent(String cardId, String deviceId, int roomId, int deskNum) {
		super();
		this.cardId = cardId;
		this.deviceId = deviceId;
		this.roomId = roomId;
		this.deskNum = deskNum;
		this.json=this.toJSON();
	}
	
	public RegisterOccupancyEvent(Object cardId, Object deviceId, Object roomId, Object deskNum) {
		super();
		
		this.cardId = cardId.toString();
		this.deviceId = deviceId.toString();
		this.roomId = Integer.parseInt(roomId.toString());
		this.deskNum = Integer.parseInt(deskNum.toString());
		this.json=this.toJSON();
	}
	
	public String toJSON() {
		JSONObject o = new JSONObject();
		
		o.put("cardId", this.cardId.toString());
		o.put("deviceId", this.deviceId);
		o.put("roomId", this.roomId);
		o.put("deskNum", this.deskNum);
		
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

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getDeskNum() {
		return deskNum;
	}

	public void setDeskNum(int deskNum) {
		this.deskNum = deskNum;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	public String toString() {
		return "Occupancy [ cardID = " +this.cardId + ", deviceId = " + this.deviceId
				+ ", roomId = " + this.roomId + ", deskNumber" + this.deskNum;
	}
}

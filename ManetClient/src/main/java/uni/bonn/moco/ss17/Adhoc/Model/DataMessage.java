package uni.bonn.moco.ss17.Adhoc.Model;

import java.util.Date;

import org.json.simple.JSONObject;

public class DataMessage {

	private Long sender;
	private String type;
	private Long receiver;
	private Long next_hop;
	private String data;
	private Date time;

	public Long getSender() {
		return sender;
	}

	public void setSender(Long sender) {
		this.sender = sender;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getReceiver() {
		return receiver;
	}

	public void setReceiver(Long receiver) {
		this.receiver = receiver;
	}

	public Long getNext_hop() {
		return next_hop;
	}

	public void setNext_hop(Long next_hop) {
		this.next_hop = next_hop;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public JSONObject getJSONFormat() {

		JSONObject jobj = new JSONObject();

		jobj.put("type", this.type);
		jobj.put("sender", this.sender);
		jobj.put("receiver", this.receiver);
		jobj.put("next_hop", this.next_hop);
		jobj.put("data", this.data);

		return jobj;
	}
}

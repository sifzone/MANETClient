package uni.bonn.moco.ss17.Adhoc.Model;

import java.util.Date;

import org.json.simple.JSONObject;

public class HelloMessage {
	private Long sender;
	private String type;
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
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public JSONObject getJSONFormat(){
		
		JSONObject jobj = new JSONObject();
		
		jobj.put("type", this.type);
		jobj.put("sender", this.sender);
		
		return jobj;
	}
	
}

package uni.bonn.moco.ss17.Adhoc.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

public class TopologyMessage {
	private Long sender;
	private String type;
	private Long sequence;
	private List<Long> neighbours = new ArrayList<Long>();
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

	public Long getSequence() {
		return sequence;
	}

	public void setSequence(Long sequenceNumber) {
		this.sequence = sequenceNumber;
	}

	public List<Long> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(List<Long> neighbours) {
		this.neighbours = neighbours;
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
		jobj.put("sequence", this.sequence);
		jobj.put("neighbors", this.neighbours);

		return jobj;
	}

}

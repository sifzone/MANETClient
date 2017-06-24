package uni.bonn.moco.ss17.Adhoc.Model;

import java.util.Date;

public class WelcomeMessage {
	private Long id;
	private String type;
	private Date time;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

}

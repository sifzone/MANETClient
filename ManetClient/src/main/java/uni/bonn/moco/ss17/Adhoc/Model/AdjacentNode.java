package uni.bonn.moco.ss17.Adhoc.Model;

public class AdjacentNode {

	private String neighborName;
	private long weight;

	public AdjacentNode() {
		super();
	}

	public AdjacentNode(String neighborName, long weight) {
		super();
		this.neighborName = neighborName;
		this.weight = weight;
	}

	public String getNeighborName() {
		return neighborName;
	}

	public void setNeighborName(String neighborName) {
		this.neighborName = neighborName;
	}

	public long getWeight() {
		return weight;
	}

	public void setWeight(long weight) {
		this.weight = weight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((neighborName == null) ? 0 : neighborName.hashCode());
		result = prime * result + (int) (weight ^ (weight >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdjacentNode other = (AdjacentNode) obj;
		if (neighborName == null) {
			if (other.neighborName != null)
				return false;
		} else if (!neighborName.equals(other.neighborName))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AdjacentNode [neighborName=" + neighborName + ", weight=" + weight + "]";
	}

}

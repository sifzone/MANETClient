package uni.bonn.moco.ss17.ManetClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import uni.bonn.moco.ss17.Adhoc.Model.AdjacentNode;
import uni.bonn.moco.ss17.Adhoc.Model.HelloMessage;
import uni.bonn.moco.ss17.Adhoc.Model.TopologyMessage;

public class ManetNode {
	private Long id;
	private ConcurrentHashMap<Long, Date> neighborsTimer = new ConcurrentHashMap<Long, Date>();
	private List<Long> neighbors = Collections.synchronizedList(new ArrayList<Long>());

	private AtomicLong sequence;
	private ConcurrentHashMap<Long, TopologyMessage> topologyMessages = new ConcurrentHashMap<Long, TopologyMessage>();
	private List<Long> topologyNodes = Collections.synchronizedList(new ArrayList<Long>());

	private ConcurrentHashMap<Long, List<Long>> adjacentNodes = new ConcurrentHashMap<Long, List<Long>>();
	private ConcurrentHashMap<String, List<AdjacentNode>> graph = new ConcurrentHashMap<String, List<AdjacentNode>>();
	private Map<String, String> nextHopTable = new ConcurrentHashMap<String, String>();

	public ManetNode() {
		this.sequence = new AtomicLong(0);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ConcurrentHashMap<Long, Date> getNeighborsTimer() {
		return neighborsTimer;
	}

	public void setNeighborsTimer(ConcurrentHashMap<Long, Date> neighbors) {
		this.neighborsTimer = neighbors;
	}

	public void addHelloMessage(HelloMessage helloMessage) {
		Long id = (Long) helloMessage.getSender();

		if (this.neighbors.indexOf(id) == -1) {
			System.out.println("NEW \"Neighbor\":" + id);
			this.neighbors.add(id);
		}
		this.neighborsTimer.put(id, new Date());
	}

	public void deleteNeighbors(long id) {
		System.out.println("LOST \"Neighbor\":" + id);
		this.neighbors.remove(this.neighbors.indexOf(id));
		this.neighborsTimer.remove(id);
	}

	public List<Long> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Long> neighbors) {
		this.neighbors = neighbors;
	}

	public long getSequence() {
		return sequence.get();
	}

	public void setSequence(long sequence) {
		this.sequence.set(sequence);
	}

	public TopologyMessage getTopologyOfNodes(Long id) {
		return topologyMessages.get(id);
	}

	public boolean setTopologyOfNodes(Long id, TopologyMessage newTMessage) {

		if (this.topologyNodes.indexOf(id) == -1)
			this.topologyNodes.add(id);

		if (this.topologyMessages.containsKey(id)) {
			TopologyMessage oldTMessage = this.topologyMessages.get(id);
			this.adjacentNodes.put(id, oldTMessage.getNeighbours());

			if (newTMessage.getSequence() > oldTMessage.getSequence()) {
				this.topologyMessages.put(id, newTMessage);
				System.out.println("NEW \"Topology\":" + id);
				return true;
			}
		} else {
			this.adjacentNodes.put(id, newTMessage.getNeighbours());
			this.topologyMessages.put(id, newTMessage);
			System.out.println("NEW \"Topology\":" + id);
			return true;
		}

		return false;

	}

	public void deleteTopology(Long id) {
		System.out.println("LOST \"Topology\":" + id);
		this.topologyNodes.remove(this.topologyNodes.indexOf(id));
		this.topologyMessages.remove(id);
	}

	public List<Long> getTopologyNodes() {
		return topologyNodes;
	}

	public void setTopologyNodes(List<Long> receivedTopologies) {
		this.topologyNodes = receivedTopologies;
	}

	public Long getNextHop(Long target) {
		createRoutingTable();
		return Long.parseLong(this.nextHopTable.get(target.toString()));
	}

	public void createRoutingTable() {

		// adding other topology
		for (Long key : this.adjacentNodes.keySet()) {
			graph.put(key.toString(), createAdjacentNodesList(this.adjacentNodes.get(key)));
		}
		// adding own neighbors
		graph.put(this.id.toString(), createAdjacentNodesList(this.neighbors));

		DijkstraRoutingAlgorithm dijkstra = new DijkstraRoutingAlgorithm(this.id.toString(), graph);
		this.nextHopTable = dijkstra.getNext_hop_table();
		// dijkstra.printRountingTable();
		dijkstra.printNextHopTable();
	}

	public List<AdjacentNode> createAdjacentNodesList(List<Long> neighbors) {
		List<AdjacentNode> adjacentNodes = new ArrayList<AdjacentNode>();
		for (Long neighbor : neighbors) {
			adjacentNodes.add(new AdjacentNode(neighbor.toString(), 1));
		}

		return adjacentNodes;
	}
}

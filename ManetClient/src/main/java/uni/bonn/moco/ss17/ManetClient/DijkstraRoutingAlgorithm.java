package uni.bonn.moco.ss17.ManetClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import uni.bonn.moco.ss17.Adhoc.Model.AdjacentNode;

public class DijkstraRoutingAlgorithm {

	private ConcurrentHashMap<String, Node> routingTable;
	private ConcurrentHashMap<String, String> nextHopTable;
	private String source;
	private List<String> otherNodes;

	class Node implements Comparable<Node> {
		private String id;
		private String parent;
		private Long distanceFromSource;

		public Node() {
		}

		public Node(String id, Long distanceFromSource, String parent) {
			this.id = id;
			this.distanceFromSource = distanceFromSource;
			this.parent = parent;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setDistanceFromSource(Long distanceFromSource) {
			this.distanceFromSource = distanceFromSource;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public String getId() {
			return id;
		}

		public Long getDistanceFromSource() {
			return distanceFromSource;
		}

		public String getParent() {
			return parent;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((distanceFromSource == null) ? 0 : distanceFromSource.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
			Node other = (Node) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (distanceFromSource == null) {
				if (other.distanceFromSource != null)
					return false;
			} else if (!distanceFromSource.equals(other.distanceFromSource))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (!parent.equals(other.parent))
				return false;
			return true;
		}

		// @Override
		public int compareTo(Node o) {
			return (this.distanceFromSource).compareTo(o.distanceFromSource);
		}

		private DijkstraRoutingAlgorithm getOuterType() {
			return DijkstraRoutingAlgorithm.this;
		}

		@Override
		public String toString() {
			return "Node [id=" + id + ", distanceFromSource=" + distanceFromSource + ", parent=" + parent + "]";
		}

	}

	public DijkstraRoutingAlgorithm(String source, ConcurrentHashMap<String, List<AdjacentNode>> graph) {

		this.source = source;
		this.routingTable = new ConcurrentHashMap<String, Node>();
		this.nextHopTable = new ConcurrentHashMap<String, String>();
		List<Node> visited = new ArrayList<Node>();
		List<Node> unvisited = new ArrayList<Node>();
		List<Node> nextNeighbors = new ArrayList<Node>();
		otherNodes = new ArrayList<String>();

		//System.out.println("\n\nDijkstra Routing Algorithm in process....\n\n");

		/**
		 * Lets distance of start vertex from start vertex = 0 Lets distance of
		 * all other vertices from start vertex = Infinity or Max Value
		 */
		Node node;
		for (String key : graph.keySet()) {
			if (key.compareTo(source) == 0) {
				node = new Node(source.toString(), new Long(0), null);
				routingTable.put(source, node);
				nextNeighbors.add(node);
				//System.out.println("Node[" + source + "]-" + node.toString());
			} else {
				node = new Node(key, Long.MAX_VALUE, null);
				routingTable.put(key, node);
				otherNodes.add(key);
				//System.out.println("Node[" + key + "]-" + node.toString());
			}
			unvisited.add(node);
		}
		//System.out.println("");

		/**
		 * Loop Visit the unvisted vertex with the smallest known distance from
		 * the start vertex For the current vertex, examine its unvisited
		 * neighbors For the current vertex, calculate distance of each neighbor
		 * from start vertex If the calculated distance of a vertex is less than
		 * the known distance, update the shortest distance Update the previous
		 * vertex for each of the updated distances Add the current vertex to
		 * the list of visited vertices Until all vertices visited
		 */
		while (!unvisited.isEmpty()) {

			Node currentNode;

			// FIND UNVISITED NODE WITH SMALLEST DISTANCE FROM START VERTEX
			// --------------------------------------------
			//System.out.println("Size of nextNeighbors: " + nextNeighbors.size());

			Collections.sort(nextNeighbors);
			currentNode = nextNeighbors.get(0);
			nextNeighbors.remove(0);
			//System.out.println("Node with minimum distance value : \n\t" + currentNode + "\n");

			// DISTANCE BETWEEN CURRENT NODE AND ITS NEIGHBORS and UPDATE
			// --------------------------------------------
			//System.out.println("Neighbors: ");
			List<AdjacentNode> neighborsCurrentNode = graph.get(currentNode.getId());
			for (AdjacentNode currentNeighbor : neighborsCurrentNode) {
				Node newNode = routingTable.get(currentNeighbor.getNeighborName());
				if (unvisited.contains(newNode)
						&& (newNode.getDistanceFromSource() >= currentNode.getDistanceFromSource()
								+ currentNeighbor.getWeight())) {
					newNode.setDistanceFromSource(currentNode.getDistanceFromSource() + currentNeighbor.getWeight());
					newNode.setParent(currentNode.getId());

					routingTable.put(currentNeighbor.getNeighborName(), newNode);
					System.out.println(currentNode.getId() + "-neighbor-" + newNode.toString());

					if (nextNeighbors.contains(newNode))
						nextNeighbors.add(nextNeighbors.indexOf(newNode), newNode);
					else
						nextNeighbors.add(newNode);
				}
			}
			System.out.println(" ");

			// UPDATE CURRENT NODE WITH VISITED
			visited.add(currentNode);
			unvisited.remove(currentNode);
			//System.out.println("Visited Nodes: " + visited.toString());
			//System.out.println("Unvisited Nodes: " + unvisited.toString());
			//System.out.println("NextNeighbors Nodes: " + nextNeighbors.toString() + "\n");
		}
	}

	public ConcurrentHashMap<String, Node> getRoutingTable() {
		return routingTable;
	}

	public void setRoutingTable(ConcurrentHashMap<String, Node> routingTable) {
		this.routingTable = routingTable;
	}

	public void printRountingTable() {
		for (String key : routingTable.keySet()) {
			Node node = routingTable.get(key);
			System.out.println(key + ": Cost:" + node.getDistanceFromSource() + ", Parent:" + node.getParent());
		}
	}

	public ConcurrentHashMap<String, String> getNext_hop_table() {
		System.out.println("Source:" + this.source);

		String parent;
		String next_hop;
		for (String nodeName : otherNodes) {
			parent = routingTable.get(nodeName).getParent();
			System.out.println("CurrentNode:" + nodeName + "-Parent:" + parent);
			next_hop = parent;

			while (parent.compareTo(this.source) != 0) {
				next_hop = parent;
				parent = routingTable.get(next_hop).getParent();
			}
			this.nextHopTable.put(nodeName, next_hop);
		}

		return this.nextHopTable;
	}

	public void printNextHopTable() {
		System.out.println("Next_Hop_Table");
		for (String node : nextHopTable.keySet()) {
			String nextHop = nextHopTable.get(node);
			System.out.println(node + "-" + nextHop);
		}
	}
}

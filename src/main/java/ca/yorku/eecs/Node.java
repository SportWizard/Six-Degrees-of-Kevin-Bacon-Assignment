package ca.yorku.eecs;

/**
 * This class is used to represent the node in neo4j
 */
public abstract class Node {
	protected String id;
	protected int distance;
	protected Node pi; // The node that introduced the current node
	
	public Node(String id, int distance) {
		this.id = id;
		this.distance = distance;
	}
	
	public Node(String id, int distance, Node pi) {
		this.id = id;
		this.distance = distance;
		this.pi = pi;
	}
	
	/**
	 * @return The id of the node
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return The distance of the node
	 */
	public int getDistance() {
		return this.distance;
	}
	
	/**
	 * @return The neighbour that introduced this node
	 */
	public Node getPi() {
		return this.pi;
	}
	
	/**
	 * @param obj
	 * @return Whether the two Object matches
	 */
	public abstract boolean equals(Object obj);
}

package ca.yorku.eecs;

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
	
	public String getId() {
		return this.id;
	}
	
	public int getDistance() {
		return this.distance;
	}
	
	public Node getPi() {
		return this.pi;
	}
	
	public abstract boolean equals(Object obj);
}

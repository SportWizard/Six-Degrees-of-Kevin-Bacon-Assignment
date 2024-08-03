package ca.yorku.eecs;

public class Actor extends Node {
	public Actor(String id, int distance) {
		super(id, distance);
	}
	
	public Actor(String id, int distance, Node pi) {
		super(id, distance, pi);
	}
}

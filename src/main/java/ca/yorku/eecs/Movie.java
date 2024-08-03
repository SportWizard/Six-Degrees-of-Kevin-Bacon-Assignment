package ca.yorku.eecs;

public class Movie extends Node {
	public Movie(String id, int distance) {
		super(id, distance);
	}
	
	public Movie(String id, int distance, Node pi) {
		super(id, distance, pi);
	}
}

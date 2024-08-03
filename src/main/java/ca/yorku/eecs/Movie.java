package ca.yorku.eecs;

public class Movie extends Node {
	public Movie(String id, int distance) {
		super(id, distance);
	}
	
	public Movie(String id, int distance, Node pi) {
		super(id, distance, pi);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		Movie other = (Movie) obj;
		
		return this.id == other.id && this.distance == other.distance;
	}
}

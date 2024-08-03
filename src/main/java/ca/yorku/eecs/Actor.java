package ca.yorku.eecs;

public class Actor extends Node {
	public Actor(String id, int distance) {
		super(id, distance);
	}
	
	public Actor(String id, int distance, Node pi) {
		super(id, distance, pi);
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        
        Actor other = (Actor) obj;
        
        return id.equals(other.id);
    }
}

package ca.yorku.eecs;

/**
 * This class is used to represent the actor node in neo4j
 */
public class Actor extends Node {
	public Actor(String id, int distance) {
		super(id, distance);
	}
	
	public Actor(String id, int distance, Node pi) {
		super(id, distance, pi);
	}
	
	/**
	 * @param obj
	 * @return Whether the two Object matches
	 */
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

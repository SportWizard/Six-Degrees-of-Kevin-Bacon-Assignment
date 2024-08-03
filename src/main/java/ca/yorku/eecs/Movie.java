package ca.yorku.eecs;

/**
 * This class is used to represent the movie node in neo4j
 */
public class Movie extends Node {
	public Movie(String id, int distance) {
		super(id, distance);
	}
	
	public Movie(String id, int distance, Node pi) {
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
        
        Movie other = (Movie) obj;
        
        return id.equals(other.id);
    }
}

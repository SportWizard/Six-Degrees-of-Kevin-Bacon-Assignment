package ca.yorku.eecs;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;

public class Utils {
    public static String uriDb = "bolt://localhost:7687";
    public static String uriUser = "http://localhost:8080";
    public static Config config = Config.builder().withoutEncryption().build();
    public static Driver driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"), config);
    
    // Allow quick change of labels, properties and relationship's name
    public static final String actorLabel = "actor";
	public static final String actorNameProperty = "name";
	public static final String actorIdProperty = "actorId";
	
	public static final String movieLabel = "movie";
	public static final String movieNameProperty = "name";
	public static final String movieIdProperty = "movieId";
	
	public static final String infoLabel = "info";
	public static final String infoIdProperty = "infoId";
	public static String imdbRatingProperty = "imdbRating";
	public static String mpaaRatingProperty = "mpaaRating";
	public static String yearProperty = "year";
	
	public static final String hasRelationship = "HAS";
	public static final String actedInRelationship = "ACTED_IN";

	public static String convert(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
    
    /**
     * Start from Kevin Bacon and traverse the graph using BFS (Breadth First Search) until it reaches the actor
     * @param actorId
     * @return The node of the actor
     */
    public static Node bfs(String actorId) {
    	Queue<Node> queue = new LinkedList<Node>();
    	Set<Node> visited = new HashSet<Node>();
    	Node node = null;
    	boolean found = false;
    	
    	try {
    		Node kevinBacon = new Actor(Utils.findKevinBaconId(), 0);
    		kevinBacon.setPi(kevinBacon);
    		
    		System.out.println("Start BFS:");
    		
    		queue.add(kevinBacon);
        	
        	while (!queue.isEmpty() && !found) {
        		node = queue.remove();
        		
        		System.out.printf("Id: %s, Distance: %d, Pi: %s\n", node.getId(), node.getDistance(), node.getPi());
        		
        		if (node.getId().equals(actorId))
        			found = true; // The actor is found
        		else
        			Utils.enqueueNeighbors(node, queue, visited); // Enqueue the neighbours
        	}
    	}
    	catch (Exception e) {
    		System.err.println(e.getMessage());
    	}
    	
    	if (found)
    		return node;
    	else
    		return null;
    }
    
    /**
     * @return Kevin Bacon's id
     */
    private static String findKevinBaconId() {
    	String response = null;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	String query = String.format("MATCH (a:%s) WHERE a.%s = \"Kevin Bacon\" RETURN a.%s AS actorId", Utils.actorLabel, Utils.actorNameProperty, Utils.actorIdProperty);
            	StatementResult results = tx.run(query);
            	
            	response = results.next().get("actorId").toString();
            }
		}
		
		return response.substring(1, response.length()-1); // Remove the quotation marks
    }
    
    /**
     * Queue all the nodes that hasn't been visited
     * @param node
     * @param queue
     * @param visited
     */
    private static void enqueueNeighbors(Node node, Queue<Node> queue, Set<Node> visited) {
    	try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	StatementResult results;
            	String query;
            	Record record;
            	String id;
            	
            	// Depending on the node, it performs different query
            	if (node instanceof Actor) {
            		// Match the actor that has the same id, and return all the movies that's connected to them
            		query = String.format("MATCH (a:%s {%s: $actorId}) OPTIONAL MATCH (a)-[r:%s]->(m:%s) RETURN m.%s AS movies", Utils.actorLabel, Utils.actorIdProperty, Utils.actedInRelationship, Utils.movieLabel, Utils.movieIdProperty);
            		results = tx.run(query, Values.parameters("actorId", node.getId()));
            		
            		record = results.next();
                	
                	if (!record.get("movies").isNull()) {
                		id = record.get("movies").toString();
                		Movie movie = new Movie(id.substring(1, id.length()-1), node.getDistance(), node); // The distance will the same as the actor, but the distance doesn't matter
                		
                		// Check if the node has already been visited
                		if (!visited.contains(movie)) {
                			queue.add(movie);
                			visited.add(movie);
                		}
                	}
                	
                	while (results.hasNext()) {
                		record = results.next();
                		id = record.get("movies").toString();
                		Movie movie = new Movie(id.substring(1, id.length()-1), node.getDistance(), node); // The distance will the same as the actor, but the distance doesn't matter
                		
                		if (!visited.contains(movie)) {
                			queue.add(movie);
                			visited.add(movie);
                		}
                	}
            	}
            	else if (node instanceof Movie) {
            		// Match the movie that has the same id, and return all the actors that's connected to it
            		query = String.format("MATCH (m:%s {%s: $movieId}) OPTIONAL MATCH (a:%s)-[r:%s]->(m) RETURN a.%s AS actors", Utils.movieLabel, Utils.movieIdProperty, Utils.actorLabel, Utils.actedInRelationship, Utils.actorIdProperty);
            		results = tx.run(query, Values.parameters("movieId", node.getId()));
                	
            		record = results.next();
                	
                	if (!record.get("actors").isNull()) {
                		id = record.get("actors").toString();
                		Actor actor = new Actor(id.substring(1, id.length()-1), node.getDistance() + 1, node);
                		
                		// Check if the node has already been visited
                		if (!visited.contains(actor)) {
                			queue.add(actor);
                			visited.add(actor);
                		}
                	}
                    	
                	while (results.hasNext()) {
                		record = results.next();
                		id = record.get("actors").toString();
                		Actor actor = new Actor(id.substring(1, id.length()-1), node.getDistance() + 1, node);
                		
                		if (!visited.contains(actor)) {
                			queue.add(actor);
                			visited.add(actor);
                		}
                	}
            	}
            }
        }
    }
}

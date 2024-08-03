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
    		
    		queue.add(kevinBacon);
        	
        	while (!queue.isEmpty() && !found) {
        		node = queue.remove();
        		
        		if (node.getId().equals(actorId))
        			found = true;
        		else
        			Utils.enqueueNeighbors(node, queue, visited);
        	}
    	}
    	catch (Exception e) {
    		System.err.println(e.getMessage());
    	}
    	
    	return node;
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
		
		return response;
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
            	Record record;
            	
            	// Depending on the node, it performs different query
            	if (node instanceof Actor) {
            		// Match the actor that has the same id, and return all the movies that's connected to them
            		results = tx.run("MATCH (a:Actor {actorId: $actorId}) OPTIONAL MATCH (a)-[r:ACTED_IN]->(m:Movie) RETURN m.movieId AS movies", Values.parameters("actorId", node.getId()));
            		
            		record = results.next();
                	
                	if (!record.get("actorId").isNull()) {
                		Actor actor = new Actor(record.get("actorId").toString(), node.getDistance() + 1, node);
                		
                		// Check if the node has already been visited
                		if (!visited.contains(actor)) {
                			queue.add(actor);
                			visited.add(actor);
                		}
                	}
                	
                	while (results.hasNext()) {
                		record = results.next();
                		Actor actor = new Actor(record.get("actorId").toString(), node.getDistance() + 1, node);
                		
                		if (!visited.contains(actor)) {
                			queue.add(actor);
                			visited.add(actor);
                		}
                	}
            	}
            	else if (node instanceof Movie) {
            		// Match the movie that has the same id, and return all the actors that's connected to it
            		results = tx.run("MATCH (m:movie {movieId: $movieId}) OPTIONAL MATCH (a:Actor)-[r:ACTED_IN]->(m) RETURN a.actorId AS actors", Values.parameters("movieId", node.getId()));
            		
            		if (node instanceof Movie) {
                		results = tx.run("MATCH (a:Actor {actorId: $actorId}) OPTIONAL MATCH (a)-[r:ACTED_IN]->(m:Movie) RETURN m.movieId AS movies", Values.parameters("actorId", node.getId()));
                		
                		record = results.next();
                    	
                    	if (!record.get("actorId").isNull()) {
                    		Movie movie = new Movie(record.get("movieId").toString(), node.getDistance() + 1, node);
                    		
                    		// Check if the node has already been visited
                    		if (!visited.contains(movie)) {
                    			queue.add(movie);
                    			visited.add(movie);
                    		}
                    	}
                    	
                    	while (results.hasNext()) {
                    		record = results.next();
                    		Actor actor = new Actor(record.get("actorId").toString(), node.getDistance() + 1, node);
                    		
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
}
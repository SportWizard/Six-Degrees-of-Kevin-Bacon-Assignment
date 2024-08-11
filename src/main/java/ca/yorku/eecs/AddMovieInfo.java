package ca.yorku.eecs;

import java.io.IOException;
import java.net.URLDecoder;

import org.json.*;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.StatementResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class connects movie with info
 */
public class AddMovieInfo implements HttpHandler {

	/**
	 * Confirming the correct method sent
	 * @param request request
	 */
	@Override
	public void handle(HttpExchange request) {
		try {
			// Only accept POST request
			if (request.getRequestMethod().equals("PUT"))
				this.handlePut(request);
			else
				request.sendResponseHeaders(404, -1); // If the request method is incorrect
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles PUT request from the client
	 * @param request
	 * @throws IOException
	 * @throws JSONException
	 */
	public void handlePut(HttpExchange request) throws IOException, JSONException {
		String body = Utils.convert(request.getRequestBody()); // Convert request to String
		
		if (body.isEmpty()) {
	    	String queryParam = request.getRequestURI().toString().split("\\?jsonStr=")[1];
	    	body = URLDecoder.decode(queryParam, "UTF-8");
	    }
		
	    JSONObject data = new JSONObject(body); // Convert JSON to an object
	    
	    int statusCode = this.validateRequestData(data);
	    
	    if (statusCode == 200) {
	        String movieId = data.getString(Utils.movieIdProperty);
	        String infoId = data.getString(Utils.infoIdProperty);
	        
	        System.out.println("Movie Id: " + movieId);
	        System.out.println("Info Id: " + infoId);

	        try {
	            this.createConnection(movieId, infoId);
	        }
	        catch (Exception e) { // Catch exception from createConnection
	            System.err.print("Caught Exception: " + e.getMessage());
	            statusCode = 500;
	        }
	    }
	    else
	    	System.out.println("Bad request: The request format is incorrect or required parameters are missing or there is a duplicate or either the movie or info has already established a relationship with another node");

	    this.sendResponse(request, statusCode);
	}
	
	/**
	 * Validate the request sent by the client
	 * @param data
	 * @return Status code of whether the request was OK (200) or invalid (400)
	 * @throws JSONException 
	 */
	private int validateRequestData(JSONObject data) throws JSONException {
		try {
			if (!data.has(Utils.movieIdProperty) || !data.has(Utils.infoIdProperty))
	            return 400; // Bad request
	        
	        String movieId = data.getString(Utils.movieIdProperty);
	        String infoId = data.getString(Utils.infoIdProperty);

	        if (!this.findMovie(movieId) || !this.findInfo(infoId))
	            return 404; // movie or info not found

	        if (this.duplicate(movieId, infoId) || this.hasRelationship(movieId, "movie") || this.hasRelationship(infoId, "info"))
	            return 400; // Relationship already established between these two nodes or with other nodes

	        return 200; // OK
		}
	    catch (Exception e) { // Catch exception from duplicate, findMovie and findInfo
			System.err.print("Caught Exception: " + e.getMessage());
			return 500; // Internal Server Error
		}
	}
	
	/**
	 * @param movieId
	 * @param infoId
	 * @return Whether the movie and info already establish a connection
	 */
	private boolean duplicate(String movieId, String infoId) throws Exception {
		boolean hasDuplicate = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Returns the connection where the movie matches the movieId and the info matches the infoId
            	String query = String.format("MATCH (m:%s {%s: $movieId})-[h:%s]-(i:%s {%s: $infoId}) RETURN h", Utils.movieLabel, Utils.movieIdProperty, Utils.hasRelationship, Utils.infoLabel, Utils.infoIdProperty);
            	StatementResult results = tx.run(query, Values.parameters("movieId", movieId, "infoId", infoId)); // Run query
            	
            	// Check if results has any return
            	if (results.hasNext())
            		hasDuplicate = true;
            }
		}
		
		return hasDuplicate;
	}
	
	/**
	 * @param id
	 * @return Whether the node already has a relationship
	 */
	private boolean hasRelationship(String id, String type) {
		boolean hasRationship = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	String query = null;
            	
            	// Returns all connections for either the movie or the info
            	if (type.equals("movie"))
            		query = String.format("MATCH (m:%s {%s: $id})-[h:%s]-(i:%s) RETURN h", Utils.movieLabel, Utils.movieIdProperty, Utils.hasRelationship, Utils.infoLabel);
            	else if (type.equals("info"))
            		query = String.format("MATCH (m:%s)-[h:%s]-(i:%s {%s: $id}) RETURN h", Utils.movieLabel, Utils.hasRelationship, Utils.infoLabel, Utils.infoIdProperty);
            		
            	StatementResult results = tx.run(query, Values.parameters("id", id)); // Run query
            	
            	// Check if results has any return
            	if (results.hasNext())
            		hasRationship = true;
            }
		}
		
		return hasRationship;
	}
	
	/**
	 * @param movieId
	 * @return Whether the movie exist or not
	 * @throws Exception
	 */
	private boolean findMovie(String movieId) throws Exception {
		boolean exist = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Returns the movie that matches the movieId
            	String query = String.format("MATCH (m:%s) WHERE m.%s = $movieId RETURN m", Utils.movieLabel, Utils.movieIdProperty);
            	StatementResult results = tx.run(query, Values.parameters("movieId", movieId)); // Run query
            	
            	// Check if results has any return
            	if (results.hasNext())
            		exist = true;
            }
		}
		
		return exist;
	}
	
	/**
	 * @param infoId
	 * @return Whether the info exist or not
	 * @throws Exception
	 */
	private boolean findInfo(String infoId) throws Exception {
		boolean exist = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Returns the info that matches the infoId
            	String query = String.format("MATCH (i:%s) WHERE i.%s = $infoId RETURN i", Utils.infoLabel, Utils.infoIdProperty);
            	StatementResult results = tx.run(query, Values.parameters("infoId", infoId)); // Run query
            	
            	// Check if results has any return
            	if (results.hasNext())
            		exist = true;
            }
		}
		
		return exist;
	}
	
	/**
	 * Create a connect between movie and info
	 * @param movieId
	 * @param infoId
	 */
	private void createConnection(String movieId, String infoId) throws Exception {
	    try (Session session = Utils.driver.session()) {
	    	// Get the movie and info that matches movieId and infoId, respectively. Then, connect movie to info
	    	String query = String.format("MATCH (m:%s) WITH m MATCH (i:%s) WHERE m.%s = $movieId AND i.%s = $infoId CREATE (m)-[h:%s]->(i)", Utils.movieLabel, Utils.infoLabel, Utils.movieIdProperty, Utils.infoIdProperty, Utils.hasRelationship);
	        session.run(query, Values.parameters("movieId", movieId, "infoId", infoId)); // Run the query in Neo4j
	        System.out.println("Neo4j transaction successfully ran");
	    }
	}

	/**
	 * Send status code back to the client
	 * @param request
	 * @param statusCode
	 * @throws IOException
	 */
	private void sendResponse(HttpExchange request, int statusCode) throws IOException {
	    request.sendResponseHeaders(statusCode, -1); // .sendResponseHeaders(Status code, Response length). If response length is unknown, use -1
	}
}
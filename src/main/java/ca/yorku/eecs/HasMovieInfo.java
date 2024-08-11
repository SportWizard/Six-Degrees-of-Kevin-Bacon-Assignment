package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
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
 * This class is used to get actor from the database (neo4j)
 */
public class  HasMovieInfo implements HttpHandler {

	/**
	 * Confirming the correct method sent
	 */
	@Override
	public void handle(HttpExchange request) {
		try {
			if (request.getRequestMethod().equals("GET"))
				this.handleGet(request);
			else
				request.sendResponseHeaders(404, -1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles Get request
	 * @param request
	 * @throws IOException
	 * @throws JSONException
	 */
	public void handleGet(HttpExchange request) throws IOException, JSONException {
		String body = Utils.convert(request.getRequestBody()); // Convert request to String
		if (body.isEmpty()) {
			String queryParam = request.getRequestURI().toString().split("\\?jsonStr=")[1];
			body = URLDecoder.decode(queryParam, "UTF-8");
		}
	    JSONObject data = new JSONObject(body); // Convert JSON to an object
	    
	    int statusCode = this.validateRequestData(data);
	    String response = null;
	    
	    if (statusCode == 200) {
	    	try {
	    		response = this.HasRelationship(data.getString(Utils.movieIdProperty), data.getString(Utils.infoIdProperty));
	    	}
	    	catch (Exception e) {
	    		System.err.println("Caught Exception: " + e.getMessage());
	        	response = e.getMessage();
	    	}
	    }
		
		this.sendResponse(request, statusCode, response);
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
	        
	        return 200; // OK
		}
		catch (Exception e) {
			System.err.print("Caught Exception: " + e.getMessage());
			return 500; // Internal Server Error
		}
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
	 * @param movieId
	 * @param infoId
	 * @return Whether the movie and info has a relationship
	 * @throws Exception
	 */
	private String HasRelationship(String movieId, String infoId) throws Exception {
		String response = null;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Match movie with movieId and info with infoId. Then match the find the relationship and return true if it exists, else false. OPTIONAL MATCH means that the pattern can be matched if it exists
            	String query = String.format("MATCH (m:%s {%s: $movieId}), (i:%s {%s: $infoId}) OPTIONAL MATCH (m)-[h:%s]-(i) RETURN m.%s AS movieId, i.%s AS infoId, EXISTS ((m)-[:%s]-(i)) AS hasRelationship", Utils.movieLabel, Utils.movieIdProperty, Utils.infoLabel, Utils.infoIdProperty, Utils.hasRelationship, Utils.movieIdProperty, Utils.infoIdProperty, Utils.hasRelationship);
            	StatementResult results = tx.run(query, Values.parameters("movieId", movieId, "infoId", infoId));  // Use "AS" to rename key, since it will appear the name in the JSON 
            	
            	JSONObject json = new JSONObject();
            	
            	Record record = results.next();
            	
            	json.put("movieId", record.get("movieId").asString()); 
            	json.put("infoId", record.get("infoId").asString());
            	json.put("hasRelationship", record.get("hasRelationship").asBoolean());
            	
            	response = json.toString();
            }
		}
		
		return response;
	}
	
	/**
	 * Send status code back to the client
	 * @param request
	 * @param statusCode
	 * @throws IOException
	 */
	private void sendResponse(HttpExchange request, int statusCode, String response) throws IOException {
		if (statusCode == 200) {
		    request.sendResponseHeaders(statusCode, response.length()); // .sendResponseHeaders(Status code, Response length)
		    
		    // Overwrite the response body with the response
		    OutputStream os = request.getResponseBody();
		    os.write(response.getBytes());
		    os.close();
		}
		else
			request.sendResponseHeaders(statusCode, -1);
	}
}
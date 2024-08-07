package ca.yorku.eecs;

import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
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
public class GetActor implements HttpHandler {
	
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
	    JSONObject data = new JSONObject(body); // Convert JSON to an object
	    
	    int statusCode = this.validateRequestData(data);
	    String response = null;
	    
	    if (statusCode == 200) {
	    	try {
	    		response = this.getActor(data.getString(Utils.actorIdProperty));
	    		
	    		// Check if there is an actor in the database that matches with actorId
	    		if (response == null)
	    			statusCode = 404;
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
		    if (data.has(Utils.actorIdProperty))
		        return 200; // OK
		    else
		        return 400; // Bad request
		}
		catch (Exception e) {
			System.err.print("Caught Exception: " + e.getMessage());
			return 500; // Internal Server Error
		}
	}
	
	/**
	 * Get an actor that matches the actorId
	 * @param actorId
	 * @return the actor with the matching actorId
	 * @throws Exception
	 */
	private String getActor(String actorId) throws Exception {
		String response = null;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Match the actor with their movies, and return the one that match the actorId OPTIONAL MATCH means that the pattern can be matched if it exists
            	String query = String.format("OPTIONAL MATCH (a:%s {%s: $actorId}) OPTIONAL MATCH (a)-[r:%s]->(m:%s) RETURN a.%s AS actorId, a.%s AS name, m.%s AS movies", Utils.actorLabel, Utils.actorIdProperty, Utils.actedInRelationship, Utils.movieLabel, Utils.actorIdProperty, Utils.actorNameProperty, Utils.movieIdProperty);
            	StatementResult results = tx.run(query, Values.parameters("actorId", actorId)); // Use "AS" to rename key, since it will appear the name in the JSON 
            	
            	JSONObject json = new JSONObject();
            	
            	Record record = results.next();
            	
            	// Check whether it returned anything
            	if (!record.get("actorId").isNull()) {
	            	json.put("actorId", record.get("actorId").asString()); 
	            	json.put("name", record.get("name").asString());
	            	
	            	ArrayList<String> movies = new ArrayList<String>();
	            	
	            	if (!record.get("movies").isNull())
	            		movies.add(record.get("movies").asString());
	            	
	            	while (results.hasNext()) {
	            		record = results.next();
	            		movies.add(record.get("movies").asString());
	            	}
	            	
	            	json.put("movies", movies.toString());
	            	
	            	response = json.toString();
            	}
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
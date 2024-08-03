package ca.yorku.eecs;

import java.io.IOException;
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
 * This class is used to add actor to the database (neo4j)
 */
public class AddActor implements HttpHandler{
	// Allow quick change of labels and properties' name
	private String actorLabel = "Actor";
	private String nameProperty = "name";
	private String actorIdProperty = "actorId";

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
	    JSONObject data = new JSONObject(body); // Convert JSON to an object

	    int statusCode = this.validateRequestData(data);
	    
	    // Validate and process data, then save to the database
	    if (statusCode == 200) {
	        String name = data.getString(this.nameProperty);
	        String actorId = data.getString(this.actorIdProperty);
	        
	        System.out.println("Name: " + name);
	        System.out.println("Actor Id: " + actorId);

	        try {
	            this.createActor(name, actorId);
	        }
	        catch (Exception e) { // Catch exception from createActor
	            System.err.print("Caught Exception: " + e.getMessage());
	            statusCode = 500;
	        }
	    }
	    else
	    	System.out.println("Bad request: The request format is incorrect or required parameters are missing or duplicate actorId");

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
		    if (data.has(this.nameProperty) && data.has(this.actorIdProperty) && !this.duplicate(data.getString(this.actorIdProperty)))
		        return 200; // OK
		    else
		        return 400; // Bad request
		}
		catch (Exception e) { // Catch exception from duplicate
			System.err.print("Caught Exception: " + e.getMessage());
			return 500; // Internal Server Error
		}
	}
	
	/**
	 * @param actorId
	 * @return whether actorId is a duplicate
	 */
	private boolean duplicate(String actorId) throws Exception {
		boolean hasDuplicate = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Returns the actor that matches the actorId
            	String query = String.format("MATCH (a:%s) WHERE a.%s = $actorId RETURN a", this.actorLabel, this.actorIdProperty);
            	StatementResult results = tx.run(query, Values.parameters("actorId", actorId)); // Run query
            	
            	// Check if results has any return
            	if (results.hasNext())
            		hasDuplicate = true;
            }
		}
		
		return hasDuplicate;
	}

	/**
	 * Create a new node for the Actor and save it to the database (neo4j)
	 * @param name
	 * @param actorId
	 */
	private void createActor(String name, String actorId) {
	    try (Session session = Utils.driver.session()) { // The parameter is to make sure the session is closed after it has finished
	    	String query = String.format("CREATE (a:%s {%s: $name, %s: $actorId})", this.actorLabel, this.nameProperty, this.actorIdProperty);
	        session.run("CREATE (a:Actor {name: $name, actorId: $actorId})", Values.parameters("name", name, "actorId", actorId)); // Run the query in Neo4j
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

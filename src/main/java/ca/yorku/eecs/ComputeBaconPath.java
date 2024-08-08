package ca.yorku.eecs;

import java.util.ArrayList;
import java.util.List;
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

public class ComputeBaconPath implements HttpHandler {
	
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
	    		response = this.baconPath(data.getString(Utils.actorIdProperty));
	    		
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
		    if (!data.has(Utils.actorIdProperty))
		    	return 400; // Bad request
		    
		    String actorId = data.getString(Utils.actorIdProperty);
		    
		    if (!this.findActor(actorId))
		    	return 404; // Actor not found
		    
		    return 200; // OK
		}
		catch (Exception e) {
			System.err.print("Caught Exception: " + e.getMessage());
			return 500; // Internal Server Error
		}
	}
	
	/**
	 * @param actorId
	 * @return Whether the actor exist or not
	 * @throws Exception
	 */
	private boolean findActor(String actorId) throws Exception {
		boolean exist = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Returns the movie that matches the movieId
            	String query = String.format("MATCH (a:%s) WHERE a.%s = $actorId RETURN a", Utils.actorLabel, Utils.actorIdProperty);
            	StatementResult results = tx.run(query, Values.parameters("actorId", actorId)); // Run query
            	
            	// Check if results has any return
            	if (results.hasNext())
            		exist = true;
            }
		}
		
		return exist;
	}
	
	/**
	 * @param actorId
	 * @return The bacon path from the actor to Kevin Bacon
	 * @throws JSONException 
	 */
	private String baconPath(String actorId) throws JSONException {
		String response = null;
		
		BFS bfs = new BFS();
		ca.yorku.eecs.Node node = bfs.traverse(actorId);
		
		if (node != null) {
			JSONObject json = new JSONObject();
			
			List<String> path = this.getPath(node);
			
			json.put("baconPath", path.toString());
			
			response = json.toString();
		}
		
		return response;
	}
	
	/**
	 * @param node
	 * @return The path from the actor to Kevin Bacon
	 */
	private List<String> getPath(ca.yorku.eecs.Node node) {
		List<String> path = new ArrayList<String>();
		ca.yorku.eecs.Node curNode = node;
		
		// Add the id of the nodes in the path
		while (curNode != curNode.getPi()) {
			path.add(curNode.getId());
			curNode = curNode.getPi();
		}
		
		path.add(curNode.getId()); // Add Kevin Bacon's id
		
		return path;
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

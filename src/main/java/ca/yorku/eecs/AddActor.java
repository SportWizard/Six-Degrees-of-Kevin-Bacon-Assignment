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
import org.neo4j.driver.v1.StatementResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class is used to add actor to the database (neo4j)
 */
public class AddActor implements HttpHandler{

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
	        String name = data.getString("name");
	        String actorId = data.getString("actorId");

	        try {
	            this.createActor(name, actorId);
	        }
	        catch (Exception e) {
	            System.err.print("Caught Exception: " + e.getMessage());
	            statusCode = 500;
	        }
	    }

	    this.sendResponse(request, statusCode);
	}

	/**
	 * Validate the request sent by the client
	 * @param data
	 * @return Status code of whether the request was OK (200) or invalid (400)
	 */
	private int validateRequestData(JSONObject data) {
	    if (data.has("name") && data.has("actorId"))
	        return 200;
	    else
	        return 400;
	}

	/**
	 * Create a new node for the Actor and save it to the database (neo4j)
	 * @param name
	 * @param actorId
	 */
	private void createActor(String name, String actorId) {
	    try (Session session = Utils.driver.session()) { // The parameter is to make sure the session is closed after it has finished
	        session.run(String.format("CREATE (Actor {name: \"%s\", actor_id: \"%s\"})", name, actorId)); // Run the query in Neo4j
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
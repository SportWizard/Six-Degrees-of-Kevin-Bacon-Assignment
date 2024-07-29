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

public class AddActor implements HttpHandler{

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
	
	public void handlePut(HttpExchange request) throws IOException, JSONException {
		String body = Utils.convert(request.getRequestBody()); // Convert request to String
		JSONObject data = new JSONObject(body); // Convert JSON to an object
		
		int statusCode = 0;
		String name = "";
		String actorId = "";
		
		if (data.has("name") && data.has("actorId")) {
			name = data.getString("name");
			actorId = data.getString("actorId");
			statusCode = 200;
		}
		else
			statusCode = 400; // If the client does not provide the required information
		
		System.out.println("name: " + name);
		System.out.println("actorId: " + actorId);
		
		if (statusCode == 200) {
			// Run the PUT request with Neo4j
			try (Session session = Utils.driver.session()) { // The parameter is to make sure the session is closed after it has finished
				session.run(String.format("CREATE (Actor {name: \"%s\", actor_id: \"%s\"})", name, actorId)); // Run the String/code input in Neo4j
				System.out.println("Neo4j transaction successfully ran");
			}
			catch (Exception e) {
				System.err.print("Caught Exception: " + e.getMessage());
				statusCode = 500;
			}
		}
		
		// Send Response
		request.sendResponseHeaders(statusCode, -1); // .sendResponseHeaders(Status code, Response length). If response length is unknown, use -1
	}
}
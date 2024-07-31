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
	    JSONObject data = new JSONObject(body); // Convert JSON to an object
	    
	    int statusCode = this.validateRequestData(data);
	    
	    if (statusCode == 200) {
	        String movieId = data.getString("movieId");
	        String infoId = data.getString("infoId");
	        
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
	    	System.out.println("Bad request: The request format is incorrect or required parameters are missing");

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
		    if (data.has("movieId") && data.has("infoId") && this.findMovie(data.getString("movieId")) && this.findInfo(data.getString("infoId")))
		        return 200; // OK
		    else
		        return 400; // Bad request
		}
	    catch (Exception e) { // Catch exception from findMovie and findInfo
			System.err.print("Caught Exception: " + e.getMessage());
			return 500; // Internal Server Error
		}
	}
	
	/**
	 * @param movieId
	 * @return whether movieId exist
	 */
	private boolean findMovie(String movieId) throws Exception {
		boolean exist = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Returns the movie that matches the movieId
            	StatementResult results = tx.run("MATCH (m:Movie) WHERE m.movieId = $movieId RETURN m", Values.parameters("movieId", movieId)); // Run query
            	
            	// Check if results has any return
            	if (results.hasNext())
            		exist = true;
            }
		}
		
		return exist;
	}
	
	/**
	 * @param infoId
	 * @return whether infoId exist
	 */
	private boolean findInfo(String infoId) throws Exception {
		boolean exist = false;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Returns the info that matches the infoId
            	StatementResult results = tx.run("MATCH (i:Info) WHERE i.infoId = $infoId RETURN i", Values.parameters("infoId", infoId)); // Run query
            	
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
	private void createConnection(String movieId, String infoId) {
	    try (Session session = Utils.driver.session()) {
	        session.run("CREATE (m:MovieInfo {movieId: $movieId, infoId: $infoId})", Values.parameters("movieId", movieId, "infoId", infoId)); // Run the query in Neo4j
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
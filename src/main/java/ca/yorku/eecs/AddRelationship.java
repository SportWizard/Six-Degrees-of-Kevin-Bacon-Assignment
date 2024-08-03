package ca.yorku.eecs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.v1.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class is used to add a relationship (ACTED_IN) between an actor and a movie in the database (neo4j)
 */
public class AddRelationship implements HttpHandler {
	// Allow quick change of labels and properties' name
	private String actorLabel = "Actor";
	private String movieLabel = "Movie";
	private String actorIdProperty = "actorId";
	private String movieIdProperty = "movieId";
	private String actedInRelationship = "ACTED_IN";

    /**
     * Confirming the correct method sent
     * @param request request
     */
    @Override
    public void handle(HttpExchange request) {
        try {
            // Only accept PUT request
            if (request.getRequestMethod().equals("PUT"))
                this.handlePut(request);
            else
                request.sendResponseHeaders(404, -1); // If the request method is incorrect
        } catch (Exception e) {
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
            String actorId = data.getString(this.actorIdProperty);
            String movieId = data.getString(this.movieIdProperty);

            System.out.println("Actor Id: " + actorId);
            System.out.println("Movie Id: " + movieId);

            try {
                this.createRelationship(actorId, movieId);
            } catch (Exception e) { // Catch exception from createRelationship
                System.err.print("Caught Exception: " + e.getMessage());
                statusCode = 500;
            }
        } else
            System.out.println("Bad request: The request format is incorrect or required parameters are missing or duplicate relationship");

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
            if (data.has(this.actorIdProperty) && data.has(this.movieIdProperty) && !this.duplicateRelationship(data.getString(this.actorIdProperty), data.getString(this.movieIdProperty)))
                return 200; // OK
            else
                return 400; // Bad request
        } catch (Exception e) { // Catch exception from duplicate
            System.err.print("Caught Exception: " + e.getMessage());
            return 500; // Internal Server Error
        }
    }

    /**
     * @param actorId
     * @param movieId
     * @return whether the relationship already exists
     */
    private boolean duplicateRelationship(String actorId, String movieId) throws Exception {
        boolean hasDuplicate = false;

        try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                // Returns the relationship that matches the actorId and movieId
            	String query = String.format("MATCH (a:%s)-[r:%s]->(m:%s) WHERE a.%s = $actorId AND m.%s = $movieId RETURN r", this.actorLabel, this.actedInRelationship, this.movieLabel, this.actorIdProperty, this.movieIdProperty);
                StatementResult results = tx.run(query, Values.parameters("actorId", actorId, "movieId", movieId)); // Run query

                // Check if results has any return
                if (results.hasNext())
                    hasDuplicate = true;
            }
        }

        return hasDuplicate;
    }

    /**
     * Create a new relationship (ACTED_IN) between an actor and a movie and save it to the database (neo4j)
     * @param actorId
     * @param movieId
     */
    private void createRelationship(String actorId, String movieId) {
        try (Session session = Utils.driver.session()) { // The parameter is to make sure the session is closed after it has finished
        	String query = String.format("MATCH (a:%s), (m:%s) WHERE a.%s = $actorId AND m.%s = $movieId CREATE (a)-[:%s]->(m)", this.actorLabel, this.movieLabel, this.actorIdProperty, this.movieIdProperty, this.actedInRelationship);
            session.run(query, Values.parameters("actorId", actorId, "movieId", movieId)); // Run the query in Neo4j
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

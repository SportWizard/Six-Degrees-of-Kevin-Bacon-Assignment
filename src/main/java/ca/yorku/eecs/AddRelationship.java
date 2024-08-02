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
            String actorId = data.getString("actorId");
            String movieId = data.getString("movieId");

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
            if (data.has("actorId") && data.has("movieId") && !this.duplicateRelationship(data.getString("actorId"), data.getString("movieId")))
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
                StatementResult results = tx.run("MATCH (a:Actor)-[r:ACTED_IN]->(m:Movie) WHERE a.actorId = $actorId AND m.movieId = $movieId RETURN r", 
                                                  Values.parameters("actorId", actorId, "movieId", movieId)); // Run query

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
            session.run("MATCH (a:Actor), (m:Movie) WHERE a.actorId = $actorId AND m.movieId = $movieId CREATE (a)-[:ACTED_IN]->(m)", 
                        Values.parameters("actorId", actorId, "movieId", movieId)); // Run the query in Neo4j
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

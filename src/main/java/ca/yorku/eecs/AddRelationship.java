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
            String actorId = data.getString(Utils.actorIdProperty);
            String movieId = data.getString(Utils.movieIdProperty);

            System.out.println("Actor Id: " + actorId);
            System.out.println("Movie Id: " + movieId);

            try {
                this.createRelationship(actorId, movieId);
            } catch (Exception e) { // Catch exception from createRelationship
                System.err.print("Caught Exception: " + e.getMessage());
                statusCode = 500;
            }
        } else {
            System.out.println("Bad request: The request format is incorrect or required parameters are missing or duplicate relationship");
        }

        this.sendResponse(request, statusCode);
    }

    /**
     * Validate the request sent by the client
     * @param data
     * @return Status code of whether the request was OK (200) or invalid (400 or 404)
     * @throws JSONException 
     */
    private int validateRequestData(JSONObject data) throws JSONException {
        try {
            if (!data.has(Utils.actorIdProperty) || !data.has(Utils.movieIdProperty))
                return 400; // Bad request
            
            String actorId = data.getString(Utils.actorIdProperty);
            String movieId = data.getString(Utils.movieIdProperty);

            if (!this.entityExists(actorId, Utils.actorLabel, Utils.actorIdProperty) || 
                !this.entityExists(movieId, Utils.movieLabel, Utils.movieIdProperty))
                return 404; // Actor or movie not found

            if (this.duplicateRelationship(actorId, movieId))
                return 400; // Relationship already established

            return 200; // OK
        } catch (Exception e) { // Catch exception from duplicate, findMovie, and findActor
            System.err.print("Caught Exception: " + e.getMessage());
            return 500; // Internal Server Error
        }
    }

    /**
     * Check if an entity (actor or movie) exists in the database
     * @param id The id of the entity
     * @param label The label of the entity (Actor or Movie)
     * @param idProperty The property name of the id
     * @return boolean indicating if the entity exists
     */
    private boolean entityExists(String id, String label, String idProperty) throws Exception {
        boolean exists = false;

        try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                // Query to check if the entity exists
                String query = String.format("MATCH (e:%s) WHERE e.%s = $id RETURN e", label, idProperty);
                StatementResult result = tx.run(query, Values.parameters("id", id));

                if (result.hasNext()) {
                    exists = true;
                }
            }
        }

        return exists;
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
                String query = String.format("MATCH (a:%s)-[r:%s]->(m:%s) WHERE a.%s = $actorId AND m.%s = $movieId RETURN r", 
                                             Utils.actorLabel, Utils.actedInRelationship, Utils.movieLabel, Utils.actorIdProperty, Utils.movieIdProperty);
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
    private void createRelationship(String actorId, String movieId) throws Exception {
        try (Session session = Utils.driver.session()) { // The parameter is to make sure the session is closed after it has finished
            String query = String.format("MATCH (a:%s), (m:%s) WHERE a.%s = $actorId AND m.%s = $movieId CREATE (a)-[:%s]->(m)", 
                                         Utils.actorLabel, Utils.movieLabel, Utils.actorIdProperty, Utils.movieIdProperty, Utils.actedInRelationship);
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

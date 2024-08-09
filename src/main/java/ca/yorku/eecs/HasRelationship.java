package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;

import org.json.*;
import org.neo4j.driver.v1.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class checks if there exists a relationship between an actor and a movie in the database (neo4j)
 */
public class HasRelationship implements HttpHandler {

    /**
     * Confirming the correct method sent
     * @param request request
     */
    @Override
    public void handle(HttpExchange request) {
        try {
            // Only accept GET request
            if (request.getRequestMethod().equals("GET"))
                this.handleGet(request);
            else
                request.sendResponseHeaders(404, -1); // If the request method is incorrect
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles GET request from the client
     * @param request
     * @throws IOException
     * @throws JSONException
     */
    public void handleGet(HttpExchange request) throws IOException, JSONException {
        String body = Utils.convert(request.getRequestBody());//Convert request to String
        JSONObject data = new JSONObject(body);//Convert String to Json
        String response = null;

        int statusCode = this.validateRequestData(data);

        if (statusCode == 200) {
            try {
                response = this.hasRelationship(data.getString(Utils.movieIdProperty), data.getString(Utils.actorIdProperty));
            } catch (Exception e) { // Catch exception from hasRelationship
                System.err.print("Caught Exception: " + e.getMessage());
                statusCode = 500;
            }
        } else {
            System.out.println("Bad request: The request format is incorrect or required parameters are missing");
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
            if (!data.has(Utils.movieIdProperty) || !data.has(Utils.actorIdProperty))
                return 400; // Bad request

            String movieId = data.getString(Utils.movieIdProperty);
            String actorId = data.getString(Utils.actorIdProperty);

            if (!this.findMovie(movieId) || !this.findActor(actorId))
                return 404; // movie or actor not found

            return 200; // OK
        }
        catch (Exception e) {
            System.err.print("Caught Exception: " + e.getMessage());
            return 500; // Internal Server Error
        }
    }

    /** Method checks whether the given movieId exists within the database and returns a boolean
     * @param movieId the Id of the movie being checkeed
     * @return boolean if the movie is in the database or not
     * @throws Exception
     * */
    private boolean findMovie(String movieId) throws Exception {
        boolean exist = false;

        try (Session session = Utils.driver.session();Transaction tx = session.beginTransaction()) {

            // Returns the movie that matches the movieId
            String query = String.format("MATCH (m:%s) WHERE m.%s = $movieId RETURN m", Utils.movieLabel, Utils.movieIdProperty);
            StatementResult results = tx.run(query, Values.parameters("movieId", movieId)); // Run query

            // Check if results has any return
            if (results.hasNext())
                exist = true;
        }
        return exist;
    }

    /** Method checks whether the given actorId exists within the database and returns a boolean
     * @param actorId the Id of the actor being checkeed
     * @return boolean if the actor is in the database or not
     * @throws Exception
     * */
    private boolean findActor(String actorId) throws Exception {
        boolean exist = false;

        try (Session session = Utils.driver.session();Transaction tx = session.beginTransaction()) {
            // Returns the actor that matches the actorId
            String query = String.format("MATCH (a:%s) WHERE a.%s = $actorId RETURN a", Utils.actorLabel, Utils.actorIdProperty);
            StatementResult results = tx.run(query, Values.parameters("actorId", actorId)); // Run query
            // Check if results has any return
            if (results.hasNext())
                exist = true;

        }
        return exist;
    }
    /**
     * Check if the relationship exists between the actor and the movie in the database (neo4j)
     * @param actorId
     * @param movieId
     * @return boolean indicating if the relationship exists
     */
    private String hasRelationship(String movieId, String actorId) throws Exception {
        String response = null;

        try (Session session = Utils.driver.session();Transaction tx = session.beginTransaction()) {

            String query = String.format("MATCH (a:%s {%s: $actorId}), (m:%s {%s: $movieId}) OPTIONAL MATCH (a)-[r:%s]-(m) RETURN m.%s AS movieId, a.%s AS actorId, EXISTS ((a)-[:%s]-(m)) AS hasRelationship", Utils.actorLabel, Utils.actorIdProperty, Utils.movieLabel, Utils.movieIdProperty, Utils.actedInRelationship, Utils.movieIdProperty, Utils.actorIdProperty, Utils.actedInRelationship);
            StatementResult results = tx.run(query, Values.parameters("movieId", movieId, "actorId", actorId));  // Use "AS" to rename key, since it will appear the name in the JSON

            JSONObject json = new JSONObject();

            Record record = results.next();
            json.put("movieId", record.get("movieId").asString());
            json.put("actorId", record.get("actorId").asString());
            json.put("hasRelationship", record.get("hasRelationship").asBoolean());

            response = json.toString();
        }
        return response;
    }


    /**
     * Send status code back to the client
     * @param request
     * @param statusCode
     * @param response
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

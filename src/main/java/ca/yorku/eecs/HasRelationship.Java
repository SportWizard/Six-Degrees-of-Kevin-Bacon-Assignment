package ca.yorku.eecs;

import java.io.IOException;
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
        String queryParams = request.getRequestURI().getQuery(); // Get query parameters from URL
        JSONObject data = new JSONObject();
        for (String param : queryParams.split("&")) {
            String[] keyValue = param.split("=");
            data.put(keyValue[0], keyValue[1]);
        }

        int statusCode = this.validateRequestData(data);

        // Validate and process data, then check the relationship in the database
        if (statusCode == 200) {
            String actorId = data.getString("actorId");
            String movieId = data.getString("movieId");

            System.out.println("Actor Id: " + actorId);
            System.out.println("Movie Id: " + movieId);

            try {
                boolean hasRelationship = this.checkRelationship(actorId, movieId);
                JSONObject response = new JSONObject();
                response.put("actorId", actorId);
                response.put("movieId", movieId);
                response.put("hasRelationship", hasRelationship);
                this.sendResponse(request, 200, response.toString());
            } catch (Exception e) { // Catch exception from checkRelationship
                System.err.print("Caught Exception: " + e.getMessage());
                statusCode = 500;
                this.sendResponse(request, statusCode, "");
            }
        } else {
            System.out.println("Bad request: The request format is incorrect or required parameters are missing");
            this.sendResponse(request, statusCode, "");
        }
    }

    /**
     * Validate the request sent by the client
     * @param data
     * @return Status code of whether the request was OK (200) or invalid (400)
     * @throws JSONException 
     */
    private int validateRequestData(JSONObject data) throws JSONException {
        try {
            if (data.has("actorId") && data.has("movieId"))
                return 200; // OK
            else
                return 400; // Bad request
        } catch (Exception e) { // Catch exception from has
            System.err.print("Caught Exception: " + e.getMessage());
            return 500; // Internal Server Error
        }
    }

    /**
     * Check if the relationship exists between the actor and the movie in the database (neo4j)
     * @param actorId
     * @param movieId
     * @return boolean indicating if the relationship exists
     */
    private boolean checkRelationship(String actorId, String movieId) throws Exception {
        boolean hasRelationship = false;

        try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                // Query to check if the relationship exists
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $actorId})-[r:ACTED_IN]->(m:Movie {movieId: $movieId}) RETURN r",
                        Values.parameters("actorId", actorId, "movieId", movieId));

                if (result.hasNext()) {
                    hasRelationship = true;
                }
            }
        }

        return hasRelationship;
    }

    /**
     * Send status code back to the client
     * @param request
     * @param statusCode
     * @param response
     * @throws IOException
     */
    private void sendResponse(HttpExchange request, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes();
        request.sendResponseHeaders(statusCode, bytes.length); 
        request.getResponseBody().write(bytes);
        request.getResponseBody().close();
    }
}

}

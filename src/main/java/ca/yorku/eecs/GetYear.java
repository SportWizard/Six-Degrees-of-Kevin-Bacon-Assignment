package ca.yorku.eecs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.v1.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class is used to retrieve movies based on their release year
 */
public class GetYear implements HttpHandler {

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

        // Validate and process data, then retrieve from the database
        if (statusCode == 200) {
            int year = data.getInt("year");

            System.out.println("Year: " + year);

            try {
                JSONArray movies = this.getMoviesByYear(year);
                this.sendResponse(request, 200, movies.toString());
            } catch (Exception e) { // Catch exception from getMoviesByYear
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
            if (data.has("year"))
                return 200; // OK
            else
                return 400; // Bad request
        } catch (Exception e) { // Catch exception from has
            System.err.print("Caught Exception: " + e.getMessage());
            return 500; // Internal Server Error
        }
    }

    /**
     * Retrieve movies by year from the database (neo4j)
     * @param year
     * @return JSONArray of movies
     */
    private JSONArray getMoviesByYear(int year) throws Exception {
        JSONArray movies = new JSONArray();

        try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                // Query to get movies by year
                StatementResult result = tx.run("MATCH (m:Movie {year: $year}) RETURN m.name AS movieName",
                        Values.parameters("year", year));

                while (result.hasNext()) {
                    Record record = result.next();
                    movies.put(record.get("movieName").asString());
                }
            }
        }

        return movies;
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

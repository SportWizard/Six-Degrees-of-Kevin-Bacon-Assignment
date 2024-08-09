package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import org.neo4j.driver.v1.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class is used to retrieve a list of movies from the year given
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
        String body = Utils.convert(request.getRequestBody());//Convert request to String
        JSONObject data = new JSONObject(body);//Convert String to Json

        String response = null;
        int statusCode = this.validateRequestData(data);

        if (statusCode == 200) {

            try {
                response = getYear(Integer.parseInt(data.getString(Utils.yearProperty)));
                //checks if there exist no movies from that given year
                if (response == null) {
                    statusCode = 404;
                }
            } catch (Exception e) { // Catch exception from getYear
                System.err.print("Caught Exception: " + e.getMessage());
                response = e.getMessage();
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
            if (data.has(Utils.yearProperty))
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
    private String getYear(Integer year) throws Exception {
        String response = null;

        try (Session session = Utils.driver.session();Transaction tx = session.beginTransaction()) {
            //Matches info nodes to movie nodes where the info node contains the given year
            String query = String.format("MATCH (i:%s {%s: $year}), (i)-[h:%s]-(m:%s) RETURN m.%s AS movies", Utils.infoLabel, Utils.yearProperty, Utils.hasRelationship, Utils.movieLabel, Utils.movieIdProperty);
            StatementResult results = tx.run(query, Values.parameters("year", year)); // Use "AS" to rename key, since it will appear the name in the JSON

            JSONObject json = new JSONObject();
            //checks that there have been matches returned
            if (results.hasNext()) {
                Record record = results.next();

                List<String> movies = new ArrayList<String>();

                if (!record.get("movies").isNull())
                    movies.add(record.get("movies").asString());

                while (results.hasNext()) {
                    record = results.next();
                    movies.add(record.get("movies").asString());
                }

                json.put("movies", movies.toString());

                response = json.toString();
            }
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
            request.sendResponseHeaders(statusCode, response.length());
            OutputStream os = request.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        else
            request.sendResponseHeaders(statusCode, -1);
    }
}

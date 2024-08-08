package ca.yorku.eecs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class connects movie with ranks
 */
public class AddInfo implements HttpHandler {

    /**
     * Confirming the correct method sent
     *
     * @param request request
     */
    @Override
    public void handle(HttpExchange request) {
        try {
            if (request.getRequestMethod().equals("PUT"))
                this.handlePut(request);
            else
                request.sendResponseHeaders(404, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles PUT request from the client
     *
     * @param request
     * @throws IOException
     * @throws JSONException
     */
    public void handlePut(HttpExchange request) throws IOException, JSONException {
        String body = Utils.convert(request.getRequestBody());
        JSONObject data = new JSONObject(body);

        int statusCode = this.validateRequestData(data);

        if (statusCode == 200) {
            int imdbRating = Integer.parseInt(data.getString(Utils.imdbRatingProperty));
            String mpaaRating = data.getString(Utils.mpaaRatingProperty);
            int year = Integer.parseInt(data.getString(Utils.yearProperty));
            String infoId = data.getString(Utils.infoIdProperty);

            System.out.println("IMDB Rating: " + imdbRating);
            System.out.println("MPAA Rating: " + mpaaRating);
            System.out.println("year: " + year);
            System.out.println("Info Id: " + infoId);

            try (Session session = Utils.driver.session()) {
                String query = String.format("CREATE (i:%s {%s: $imdbRating, %s: $mpaaRating, %s: $year, %s: $infoId})", Utils.infoLabel, Utils.imdbRatingProperty, Utils.mpaaRatingProperty, Utils.yearProperty, Utils.infoIdProperty);
                session.run(query, Values.parameters("imdbRating", imdbRating, "mpaaRating", mpaaRating, "year", year, "infoId", infoId));
                System.out.println("Neo4j transaction successfully ran");
            } catch (Exception e) { // Catch exception from createConnection
                System.err.print("Caught Exception: " + e.getMessage());
                statusCode = 500;
            }
        } else
            System.out.println("Bad request: The request format is incorrect or required parameters are missing or there is a duplicate");

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
            if (data.has(Utils.infoIdProperty) && !duplicate(data.getString(Utils.infoIdProperty))){
                return 200;
            }
            return 400;
        }
        catch (Exception e) {
            System.err.print("Caught Exception: " + e.getMessage());
            return 500;
        }
    }

    /**
     * @param infoId
     * @return whether infoId is a duplicate
     */
    private boolean duplicate(String infoId) throws Exception {
        boolean hasDuplicate = false;

        try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                String query = String.format("MATCH (i:%s) WHERE i.%s = $infoId RETURN i", Utils.infoLabel, Utils.infoIdProperty);
                StatementResult results = tx.run(query, Values.parameters("infoId", infoId)); // Run query

                if (results.hasNext())
                    hasDuplicate = true;
            }
        }

        return hasDuplicate;
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


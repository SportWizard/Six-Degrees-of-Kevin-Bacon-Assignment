package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class GetMPAA implements HttpHandler {

    public void handle(HttpExchange request) {
        try {
            if (request.getRequestMethod().equals("GET")) {
                handleGet(request);
            } else {
                request.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGet(HttpExchange request) throws IOException, JSONException {
        String body = Utils.convert(request.getRequestBody());
        JSONObject data = new JSONObject(body);

        String response = null;
        int statusCode = validateRequestData(data);

        if (statusCode == 200) {
            try {
               // response = getMPAA(data.getString(Utils.movieIdProperty));
                response = getMPAA(data.getString(Utils.mpaaRatingProperty));

                if (response == null) {
                    statusCode = 404;
                }
            }
            catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                response = e.getMessage();
            }
        }

        sendResponse(request, statusCode, response);
    }

    private int validateRequestData(JSONObject data) throws JSONException {
        try {
            if (data.has(Utils.mpaaRatingProperty)) {
           // if (data.has(Utils.movieIdProperty)) {
                return 200;
            }
            return 400;
        }
        catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
            return 500;
        }
    }

    private String getMPAA(String mpaaRating) throws Exception {
        String response = null;

        try (Session session = Utils.driver.session(); Transaction tx = session.beginTransaction()) {
            String query = String.format("MATCH (i:%s)-[h:%s]->(m:%s) OPTIONAL MATCH (i.%s) AS movies", Utils.infoLabel, Utils.hasRelationship, Utils.movieLabel, Utils.mpaaRatingProperty);
            StatementResult results = tx.run(query, Values.parameters("mpaaRating", mpaaRating)); // Use "AS" to rename key, since it will appear the name in the JSON

            JSONObject json = new JSONObject();
            Record record = results.next();

            ArrayList<String> movies = new ArrayList<String>();

            if (!record.get("movies").isNull())
                movies.add(record.get("movies").asString());

            while (results.hasNext()) {
                record = results.next();
                movies.add(record.get("movies").asString());
            }

            json.put("movies", movies.toString());

            response = json.toString();

        }
        return response;
    }

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

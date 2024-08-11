package ca.yorku.eecs;

import java.util.List;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;

/*This class returns a list of movies that have the mpaaRating provided by the client (if there exist any that do) */
public class GetMPAA implements HttpHandler {

    /** This method confirms that the right request method was sent
     * @param request request from the client
     * */
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
        String body = Utils.convert(request.getRequestBody());//Convert request to String
        
        if (body.isEmpty()) {
	    	String queryParam = request.getRequestURI().toString().split("\\?jsonStr=")[1];
	    	body = URLDecoder.decode(queryParam, "UTF-8");
	    }
        
        JSONObject data = new JSONObject(body);//Convert String to Json

        String response = null;
        int statusCode = validateRequestData(data);

        if (statusCode == 200) {
            try {
                response = getMPAA(data.getString(Utils.mpaaRatingProperty));
                //checks if there don't exist any movies in the database with the mpaaRating and returns 404 code
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

    /**Method validates the request received from the client and returns the appropriate status code
     * @param data JSONObject of the client request
     * @throws JSONException
     * @return status code for the client request*/
    private int validateRequestData(JSONObject data) throws JSONException {
        try {
            if (data.has(Utils.mpaaRatingProperty)) {
                return 200; //OK
            }
            return 400;//Bad request
        }
        catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
            return 500; //Internal Server Error
        }
    }

    /**Retrieves a list of movies that have the mpaaRating provided by the client by matching info nodes to movie nodes
     * @param mpaaRating the movie rating provided by the user client
     * @return list of movies that have the mpaaRating provided
     * @throws Exception*/
    private String getMPAA(String mpaaRating) throws Exception {
        String response = null;

        try (Session session = Utils.driver.session(); Transaction tx = session.beginTransaction()) {
            //Matches info nodes to movie nodes where the info node has the given mpaaRating
            String query = String.format("MATCH (i:%s {%s: $mpaaRating}), (i)-[h:%s]-(m:%s) RETURN m.%s AS movies", Utils.infoLabel, Utils.mpaaRatingProperty, Utils.hasRelationship, Utils.movieLabel, Utils.movieIdProperty);
            StatementResult results = tx.run(query, Values.parameters("mpaaRating", mpaaRating)); // Use "AS" to rename key, since it will appear the name in the JSON

            JSONObject json = new JSONObject();
            //Checks whether there are any results
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

    /** Methods send the status code back to the client
     * @param request the request from the client
     * @param statusCode the statusCode (that was determined in validateRequestData
     * @param response the string response of the client's request (list of movies)
     * @throws IOException*/
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

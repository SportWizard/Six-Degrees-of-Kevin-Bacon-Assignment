package ca.yorku.eecs;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import org.json.*;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.StatementResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/* This class is used to retrieve a movie from the database*/
public class GetMovie implements HttpHandler {

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

    /**This method handles the get request to retrieve the necessary info
     * @param request request from the client
     * @throws IOException
     * @throws JSONException*/
    public void handleGet(HttpExchange request) throws IOException, JSONException {
        String body = Utils.convert(request.getRequestBody()); //Convert request to String

        if (body.isEmpty()) {
            String queryParam = request.getRequestURI().toString().split("\\?movieId=")[1];
            body = URLDecoder.decode(queryParam, "UTF-8");

        }
        JSONObject data = new JSONObject(body); //Convert String to Json

        String response = null;
        int statusCode = validateRequestData(data);

        if (statusCode == 200) {
            try {
                response = getMovie(data.getString(Utils.movieIdProperty));

                //checks if there exists no such movie in the database and returns 404 code
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
            if (data.has(Utils.movieIdProperty)) {
                return 200; //OK
            }
            return 400; //Bad request
        }
        catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
            return 500; //Internal Server Error
        }
    }

    /**Retrieves the node of the movie with the given movieId
     * @param movieId the id associated with the given movie
     * @return the movie with the matching movieId, its name, and a list of actors in the movie
     * @throws Exception*/
    private String getMovie(String movieId) throws Exception {
        String response = null;

        try (Session session = Utils.driver.session(); Transaction tx = session.beginTransaction()) {
            //Matches the actors that act in this movie and returns their actorId
            String query = String.format("MATCH (m:%s {%s: $movieId}) OPTIONAL MATCH (a:%s)-[r:%s]->(m) RETURN m.%s AS movieId, m.%s AS name, a.%s AS actors", Utils.movieLabel, Utils.movieIdProperty, Utils.actorLabel, Utils.actedInRelationship, Utils.movieIdProperty, Utils.movieNameProperty, Utils.actorIdProperty);
            StatementResult results = tx.run(query, Values.parameters("movieId", movieId)); // Use "AS" to rename key, since it will appear the name in the JSON

            JSONObject json = new JSONObject();
            Record record = results.next();

            //Checks whether the results returned anything
            if (!record.get("movieId").isNull()) {
                json.put("movieId", record.get("movieId").asString());
                json.put("name", record.get("name").asString());

                List<String> actors = new ArrayList<String>();

                if (!record.get("actors").isNull()) {
                    actors.add(record.get("actors").asString());
                }

                while (results.hasNext()) {
                    record = results.next();
                    actors.add(record.get("actors").asString());
                }

                json.put("actors", actors.toString());

                response = json.toString();
            }
        }
        return response;
    }

    /** Methods send the status code back to the client
     * @param request the request from the client
     * @param statusCode the statusCode (that was determined in validateRequestData
     * @param response the string response of the client's request (movieId, name, and list of actors)
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

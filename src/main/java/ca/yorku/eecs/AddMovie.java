package ca.yorku.eecs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.StatementResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/* AddMovie class is used to add a valid movie node to the database */
public class AddMovie implements HttpHandler{
	
    //empty constructor
    public AddMovie(){}

    /** Method handles the request method accordingly
    * @param request request from the client
    * */
    @Override
    public void handle(HttpExchange request) {
        try {
            if (request.getRequestMethod().equals("PUT")) {
                handlePut(request);
            } else {
                request.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/** Method handles the request received, and then sends the proper status code back
 * @param request request from the client
 * @throws IOException
 * @throws JSONException
 * */
    public void handlePut(HttpExchange request) throws IOException, JSONException{
        String body = Utils.convert(request.getRequestBody());
        JSONObject data = new JSONObject(body);

        int statusCode = validateRequestData(data);

        if (statusCode == 200) {
            String name = data.getString(Utils.movieNameProperty);
            String movieId = data.getString(Utils.movieIdProperty);

            System.out.println("name :" + name);
            System.out.println("movieId :" + movieId);

            try (Session session = Utils.driver.session()){
            	String query = String.format("CREATE (m:%s {%s: $name, %s: $movieId})", Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
                session.run(query, Values.parameters("name", name, "movieId", movieId));
                System.out.println("Neo4j transaction ran successfully");
            } catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                statusCode = 500;
            }
        } else {
            System.out.println("Bad request: Either the request format is incorrect, required parameters are missing, or duplicate movieId");
        }
        request.sendResponseHeaders(statusCode, -1);
    }

    /** Method validates the request received and returns the corresponding status code of the request
     * @param data JSONObject obtained from the client's request
     * @return status code of the request
     * @throws JSONException
     * */
    private int validateRequestData(JSONObject data) throws JSONException {
        try {
            if (data.has(Utils.movieNameProperty) && data.has(Utils.movieIdProperty) && !duplicate(data.getString(Utils.movieIdProperty))) {
                return 200;
            }
            return 400;
        }
        catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
            return 500;
        }
    }

    /** Method checks if the given information is a duplicate of an existing node
     * @param movieId unique ID associated with a movie
     * @throws Exception
     * */
    private boolean duplicate(String movieId) throws Exception {
        try (Session session = Utils.driver.session(); Transaction tx = session.beginTransaction()) {
        	String query = String.format("MATCH (m:%s) WHERE m.%s = $movieId RETURN m", Utils.movieLabel, Utils.movieIdProperty);
            StatementResult results = tx.run(query, Values.parameters("movieId", movieId));
            return results.hasNext();
        }
    }
}

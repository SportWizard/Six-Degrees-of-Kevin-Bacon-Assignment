package ca.yorku.eecs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.StatementResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddMovie implements HttpHandler{

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

    public void handlePut(HttpExchange request) throws IOException, JSONException{
        String body = Utils.convert(request.getRequestBody());
        JSONObject data = new JSONObject(body);

        int statusCode = validateRequestData(data);

        if (statusCode == 200) {
            String name = data.getString("name");
            String movieId = data.getString("movieID");

            System.out.println("name :" + name);
            System.out.println("movieId :" + movieId);

            try (Session session = Utils.driver.session()){
                session.run("CREATE (m:Movie {name: $name, movieId: $movieId})", Values.parameters("name", name, "movieId", movieId));
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

    private int validateRequestData(JSONObject data) {
        try {
            if (data.has("name") && data.has("movieId") && !duplicate(data.getString("movieId"))) {
                return 200;
            }
            return 400;
        }
        catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
            return 500;
        }
    }

    private boolean duplicate(String movieId) throws Exception {
        try (Session session = Utils.driver.session(); Transaction tx = session.beginTransaction()) {
            StatementResult results = tx.run("MATCH (m:Movie) WHERE m.movieId = $movieId RETURN m", Values.parameters("movieId", movieId));
            return results.hasNext();
        }
    }
}

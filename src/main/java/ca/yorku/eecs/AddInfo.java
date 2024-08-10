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
 * This class creates a set of information related to a specific movie
 */
public class AddInfo implements HttpHandler {

    /**
     * Confirming the correct method sent
     * @param request request
     */
    @Override
    public void handle(HttpExchange request) {
        try {
            if (request.getRequestMethod().equals("PUT"))
                this.handlePut(request);
            else
                request.sendResponseHeaders(404, -1);// If the request method is incorrect
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles PUT request from the client
     * @param request
     * @throws IOException
     * @throws JSONException
     */
    public void handlePut(HttpExchange request) throws IOException, JSONException {
        String body = Utils.convert(request.getRequestBody());// Convert request to String
        JSONObject data = new JSONObject(body);// Convert JSON to an object

        int statusCode = this.validateRequestData(data);
        // Validate and process data, then save to the database
        if (statusCode == 200) {
        	try {
        		// Default values
        		double imdbRating = this.getImdb(data);
        		String mpaaRating = this.getMpaa(data);
        		Integer year = this.getyear(data);
        		String infoId = data.getString(Utils.infoIdProperty);

	            System.out.println("IMDB Rating: " + imdbRating);
	            System.out.println("MPAA Rating: " + mpaaRating);
	            System.out.println("year: " + year);
	            System.out.println("Info Id: " + infoId);
            
	            try (Session session = Utils.driver.session()) {
	                String query = String.format("CREATE (i:%s {%s: $imdbRating, %s: $mpaaRating, %s: $year, %s: $infoId})", Utils.infoLabel, Utils.imdbRatingProperty, Utils.mpaaRatingProperty, Utils.yearProperty, Utils.infoIdProperty);
	                session.run(query, Values.parameters("imdbRating", imdbRating, "mpaaRating", mpaaRating, "year", year, "infoId", infoId));
	                System.out.println("Neo4j transaction successfully ran");
	            }
        	}
            catch (Exception e) { // Catch exception from try block
                System.err.print("Caught Exception: " + e.getMessage());
                statusCode = 500;
            }
        }
        else
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
                return 200; //OK
            }
            return 400; //Bad Request
        }
        catch (Exception e) {// Catch exception from duplicate
            System.err.print("Caught Exception: " + e.getMessage());
            return 500;// Internal Server Error
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
                // Returns the info node that matches the infoId
                String query = String.format("MATCH (i:%s) WHERE i.%s = $infoId RETURN i", Utils.infoLabel, Utils.infoIdProperty);
                StatementResult results = tx.run(query, Values.parameters("infoId", infoId)); // Run query
                // Check if results has any return
                if (results.hasNext())
                    hasDuplicate = true;
            }
        }
        return hasDuplicate;
    }
    
    /**
     * @param data
     * @return IMDB rating from input
     * @throws JSONException
     */
    private double getImdb(JSONObject data) throws JSONException {
    	if (data.has(Utils.imdbRatingProperty))
    		return data.getDouble(Utils.imdbRatingProperty);
    	else
    		return 0;
    }
    
    /**
     * @param data
     * @return MPAA rating from input
     * @throws JSONException
     */
    private String getMpaa(JSONObject data) throws JSONException {
    	if (data.has(Utils.mpaaRatingProperty))
    		return data.getString(Utils.mpaaRatingProperty);
    	else
    		return "";
    }
    
    /**
     * @param data
     * @return Year from input
     * @throws JSONException
     */
    private int getyear(JSONObject data) throws JSONException {
    	if (data.has(Utils.yearProperty))
    		return data.getInt(Utils.yearProperty);
    	else
    		return 0;
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


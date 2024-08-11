package ca.yorku.eecs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Collections;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

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

/**
 * This class is used to get the top n movie by their IMDB rating
 */
public class GetRank implements HttpHandler {
	private String nInput = "n"; // Input
	
	/**
	 * Confirming the correct method sent
	 */
	@Override
	public void handle(HttpExchange request) {
		try {
			if (request.getRequestMethod().equals("GET"))
				this.handleGet(request);
			else
				request.sendResponseHeaders(404, -1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles Get request
	 * @param request
	 * @throws IOException
	 * @throws JSONException
	 */
	public void handleGet(HttpExchange request) throws IOException, JSONException {
		String body = Utils.convert(request.getRequestBody()); // Convert request to String
		
		if (body.isEmpty()) {
	    	String queryParam = request.getRequestURI().toString().split("\\?jsonStr=")[1];
	    	body = URLDecoder.decode(queryParam, "UTF-8");
	    }
		
	    JSONObject data = new JSONObject(body); // Convert JSON to an object
	    
	    int statusCode = this.validateRequestData(data);
	    String response = null;
	    
	    if (statusCode == 200) {
	    	try {
	    		response = this.getTopMovies(data.getInt(this.nInput), this.getMoviesAndRatings());
	    	}
	    	catch (Exception e) {
	    		System.err.println("Caught Exception: " + e.getMessage());
	        	response = e.getMessage();
	    	}
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
		    if (!data.has(this.nInput))
		        return 400; // Bad request
		    
		    int n = data.getInt(this.nInput);
		    
		    if (n < 0 || n > this.countMovie())
		    	return 404;
		    
	        return 200; // OK
		}
		catch (Exception e) {
			System.err.print("Caught Exception: " + e.getMessage());
			return 500; // Internal Server Error
		}
	}
	
	/**
	 * @return The number of movies in the database
	 */
	private int countMovie() throws Exception {
		int count = 0;
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Count the number of movies
            	String query = String.format("MATCH (m:%s) RETURN COUNT(m) AS movieCount", Utils.movieLabel);
            	StatementResult results = tx.run(query);
            	
            	Record record = results.next();
            	
            	count = record.get("movieCount").asInt();
            }
        }
		
		return count;
	}
	
	/**
	 * Get all the movies with their corresponding IMDB rating
	 * @return
	 * @throws Exception
	 */
	private Hashtable<Double, ArrayList<String>> getMoviesAndRatings() throws Exception {
		Hashtable<Double, ArrayList<String>> moviesByRating = new Hashtable<Double, ArrayList<String>>();
		
		try (Session session = Utils.driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
            	// Count the number of movies
            	String query = String.format("MATCH (m:%s) OPTIONAL MATCH (m)-[r:%s]->(i:%s) RETURN m.%s AS movieId, i.%s As imdbRating", Utils.movieLabel, Utils.hasRelationship, Utils.infoLabel, Utils.movieIdProperty, Utils.imdbRatingProperty);
            	StatementResult results = tx.run(query);
            	
            	// Movies' index correspond to the ratings' index
            	while (results.hasNext()) {
                    Record record = results.next();
                    
                    // Check if the movieId and imdbRating are not null
                    if (!record.get("movieId").isNull() && !record.get("imdbRating").isNull()) {
                        String movie = record.get("movieId").asString();
                        double rating = record.get("imdbRating").asDouble();
                        
                        ArrayList<String> movies = moviesByRating.get(rating);
                        if (movies == null) {
                            movies = new ArrayList<>();
                            moviesByRating.put(rating, movies);
                        }
                        
                        // Add movie to the list
                        movies.add(movie);
                    }
                }
            }
        }
		
		return moviesByRating;
	}
	
	/**
	 * Get an actor that matches the actorId
	 * @param actorId
	 * @return the actor with the matching actorId
	 * @throws Exception
	 */
	private String getTopMovies(int n, Hashtable<Double, ArrayList<String>> moviesByRating) throws Exception {
		ArrayList<String> topMovies = new ArrayList<String>();
		ArrayList<String> movies;
		
		// Selection sort
		while (topMovies.size() < n) {
			double maxRating = Collections.max(moviesByRating.keySet()); // Get the highest rating
			
			movies = moviesByRating.get(maxRating); // Get all the movies that has the highest rating
			
			// Add all the movies in the rating or until it reaches n
			for (int i = 0; i < movies.size() && topMovies.size() < n; i++)
				topMovies.add(movies.get(i));
			
			moviesByRating.remove(maxRating);
		}
		
		JSONObject json = new JSONObject();
		
		json.put("movies", topMovies);
		
		return json.toString();
	}
	
	/**
	 * Send status code back to the client
	 * @param request
	 * @param statusCode
	 * @throws IOException
	 */
	private void sendResponse(HttpExchange request, int statusCode, String response) throws IOException {
		if (statusCode == 200) {
		    request.sendResponseHeaders(statusCode, response.length()); // .sendResponseHeaders(Status code, Response length)
		    
		    // Overwrite the response body with the response
		    OutputStream os = request.getResponseBody();
		    os.write(response.getBytes());
		    os.close();
		}
		else
			request.sendResponseHeaders(statusCode, -1);
	}
}

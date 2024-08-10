package ca.yorku.eecs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;

/**
 * Unit test for simple App.
 * Important: Start the server for the App and the neo4j before running the junit test
 */
public class AppTest extends TestCase {
	private final String rootPath = "http://localhost:8080";
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( AppTest.class );
    }
    
    private HttpURLConnection addActor(String actorName, String actorId) throws Exception {
    	HttpURLConnection connection = null;
    	
    	try {
    		// Define the URL for the API endpoint
    		URL url = new URL(this.rootPath + "/api/v1/addActor"); // Specify url link
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT"); // Request method
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true); // Intent to send request body to the server
    		
    		// Add actor
    		JSONObject json = new JSONObject();
    		json.put("name", actorName);
    		json.put("actorId", actorId);
    		
    		// Send request
    		OutputStream os = connection.getOutputStream();
    		String input = json.toString();
		    os.write(input.getBytes());
		    os.flush();
		    os.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception :" + e.getMessage());
    	}
    	
		return connection;
    }
    
    private HttpURLConnection addMovie(String movieName, String movieId) throws Exception {
    	HttpURLConnection connection = null;
    	
    	try {
	    	// Define the URL for the API endpoint
			URL url = new URL(this.rootPath + "/api/v1/addMovie");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			
			// Add movie
			JSONObject json = new JSONObject();
			json.put("name", movieName);
			json.put("movieId", movieId);
			
			// Send request
			OutputStream os = connection.getOutputStream();
			String input = json.toString();
		    os.write(input.getBytes());
		    os.flush();
		    os.close();
    	}
    	catch (Exception e) {
    		throw new Exception(e);
    	}
    	
    	return connection;
    }
    
    private HttpURLConnection addRelationship(String actorId, String movieId) throws Exception {
    	HttpURLConnection connection;
    	
    	try {
	    	URL url = new URL(this.rootPath + "/api/v1/addRelationship");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			
			JSONObject json = new JSONObject();
			json.put("actorId", actorId);
			json.put("movieId", movieId);
			
			OutputStream os = connection.getOutputStream();
			String input = json.toString();
		    os.write(input.getBytes());
		    os.flush();
		    os.close();
    	}
    	catch (Exception e) {
    		throw new Exception(e);
    	}
    	
    	return connection;
    }
	private HttpURLConnection getActor(String actorId) throws Exception {
		HttpURLConnection connection;

		try {
			String jsonStr = String.format("{%s:%s}", Utils.actorIdProperty, actorId);
			String encoded = URLEncoder.encode(jsonStr, "UTF-8");

			URL url = new URL(this.rootPath + "/api/v1/getActor?jsonStr=" + encoded);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json;utf-8");
		}
		catch (Exception e) {
			throw new Exception(e);
		}

		return connection;
	}

	private HttpURLConnection getMovie(String movieId) throws Exception {
		HttpURLConnection connection;

		try {
			String jsonStr = String.format("{%s:%s}", Utils.movieIdProperty, movieId);
			String encoded = URLEncoder.encode(jsonStr, "UTF-8");

			URL url = new URL(this.rootPath + "/api/v1/getMovie?jsonStr=" + encoded);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json;utf-8");
		}
		catch (Exception e) {
			throw new Exception(e);
		}

		return connection;
	}
	/**
     * Verifies that adding an actor with valid details returns a 200 status code
     */
    public void testAddActorPass() { // Name of the test method must start with test
    	HttpURLConnection connection = null;
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";

		try {
			// Add actor
			connection = this.addActor(actorName, actorId);
			
		    // Get response
		    int statusCode = connection.getResponseCode();
		    int expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		// Disconnect
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try (Session session = Utils.driver.session()) { 
		    	String query = String.format("MATCH (a:%s {%s: $actorName, %s: $actorId}) DELETE a", Utils.actorLabel, Utils.actorNameProperty, Utils.actorIdProperty);
		    	session.run(query, Values.parameters("actorName", actorName, "actorId", actorId)); // Run the query in Neo4j
		    }
		    catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that adding an actor with invalid details returns a 400 status code
     */
    public void testAddActorFail() {
    	HttpURLConnection connection = null;
    	String actorId = "nm1001213";
    	
    	try {
    		// Add Actor
    		connection = this.addActor(null, actorId);
		    
		    // Get response
		    int statusCode = connection.getResponseCode();
		    int expected = 400;
		    assertEquals("Incorrect status code add actor", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try (Session session = Utils.driver.session()) { 
		    	String query = String.format("MATCH (a:%s {%s: $actorId}) DELETE a", Utils.actorLabel, Utils.actorIdProperty);
		    	session.run(query, Values.parameters("actorId", actorId)); // Run the query in Neo4j
		    }
		    catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that adding a movie with valid details returns a 200 status code
     */
    public void testAddMoviePass() {
    	HttpURLConnection connection = null;
    	String movieName = "Parasite";
		String movieId = "nm7001453";
    	
    	try {
    		// Add movie
    		connection = this.addMovie(movieName, movieId);
		    
		    // Get response
		    int statusCode = connection.getResponseCode();
		    int expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		// Disconnect
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try (Session session = Utils.driver.session()) { 
		    	String query = String.format("MATCH (m:%s {%s: $movieName, %s: $movieId}) DELETE m", Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
		    	session.run(query, Values.parameters("movieName", movieName, "movieId", movieId));
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that adding a movie with invalid details returns a 400 status code
     */
    public void testAddMovieFail() {
    	HttpURLConnection connection = null;
    	String movieName = "Parasite";
    	
    	try {
    		// Add movie
    		connection = this.addMovie(movieName, null);
		    
		    // Get response
		    int statusCode = connection.getResponseCode();
		    int expected = 400;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try (Session session = Utils.driver.session()) { 
		    	String query = String.format("MATCH (m:%s {%s: $movieName}) DELETE m", Utils.movieLabel, Utils.movieNameProperty);
		    	session.run(query, Values.parameters("movieName", movieName));
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that adding a relationship with valid details returns a 200 status code
     */
    public void testAddRelationshipPass() {
    	HttpURLConnection connection = null;
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";
    	String movieName = "Parasite";
		String movieId = "nm7001453";

    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		connection = this.addActor(actorName, actorId);
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
    		// Add movie
		    connection = this.addMovie(movieName, movieId);
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    		
    		// Add Relationship
		    
		    connection = this.addRelationship(actorId, movieId);
		    
		    // Get response
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add relationship", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try (Session session = Utils.driver.session()) {
		    	String query = String.format("MATCH (a:%s {%s: $actorName, %s: $actorId}), (m:%s {%s: $movieName, %s: $movieId}) DETACH DELETE a, m", Utils.actorLabel, Utils.actorNameProperty, Utils.actorIdProperty, Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
		    	session.run(query, Values.parameters("actorName", actorName, "actorId", actorId, "movieName", movieName, "movieId", movieId));
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that adding a relationship with invalid details returns a 404 status code
     */
    public void testAddRelationshipFail() {
    	HttpURLConnection connection = null;
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";
		String movieName = "Parasite";
		String movieId = "nm7001453";
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		connection = this.addActor(actorName, actorId);
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
    		// Add movie
		    connection = this.addMovie(movieName, movieId);
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    		
    		// Add Relationship
		    connection = this.addRelationship(null, movieId);
		    
		    // Get response
		    statusCode = connection.getResponseCode();
		    expected = 400;
		    assertEquals("Incorrect status code for add relationship", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try (Session session = Utils.driver.session()) {
		    	String query = String.format("MATCH (a:%s {%s: $actorName, %s: $actorId}), (m:%s {%s: $movieName, %s: $movieId}) DETACH DELETE a, m", Utils.actorLabel, Utils.actorNameProperty, Utils.actorIdProperty, Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
		    	session.run(query, Values.parameters("actorName", actorName, "actorId", actorId, "movieName", movieName, "movieId", movieId));
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    // Additional test cases
    
    /**
     * Verifies that adding a relationship with invalid details returns a 404 status code
     */
    public void testAddRelationshipFail2() {
    	HttpURLConnection connection = null;
		String actorId = "nm1001213";
		String movieName = "Parasite";
		String movieId = "nm7001453";
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add movie
		    connection = this.addMovie(movieName, movieId);
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    		
    		// Add Relationship
		    connection = this.addRelationship(actorId, movieId);
		    
		    // Get response
		    statusCode = connection.getResponseCode();
		    expected = 404;
		    assertEquals("Incorrect status code add relationship", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try (Session session = Utils.driver.session()) {
		    	String query = String.format("MATCH (m:%s {%s: $movieName, %s: $movieId}) DETACH DELETE m", Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
		    	session.run(query, Values.parameters("movieName", movieName, "movieId", movieId));
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }

	public void testGetMoviePass() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String movieName = "Lord of the Rings";
		String movieId = "nm7450264";

		String actorName = "Elijah Wood";
		String actorId = "nm6250725";

		try {
			int expected = 200;

			//Add actor
			connection = this.addActor(actorName, actorId);
			assertEquals("Incorrect status code for add actor", expected, connection.getResponseCode());
			// Add movie
			connection = this.addMovie(movieName, movieId);
			assertEquals("Incorrect status code for add movie", expected, connection.getResponseCode());
			//add Relationship
			connection = this.addRelationship(actorId, movieId);
			assertEquals("Incorrect status code for add relationship", expected, connection.getResponseCode());

			// Get response
			connection = this.getMovie(movieId);
			assertEquals("Incorrect status code for get movie", expected, connection.getResponseCode());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e.getMessage());
		}
		finally {
			// Disconnect
			if (connection != null)
				connection.disconnect();

			// Remove node(s) added
			try (Session session = Utils.driver.session()) {
				String query = String.format("MATCH (m:%s {%s: $movieName, %s: $movieId}) DETACH DELETE m", Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
				session.run(query, Values.parameters("movieName", movieName, "movieId", movieId));
			}
			try (Session session = Utils.driver.session()) {
				String query = String.format("MATCH (a:%s {%s: $actorName, %s: $actorId}) DELETE a", Utils.actorLabel, Utils.actorNameProperty, Utils.actorIdProperty);
				session.run(query, Values.parameters("actorName", actorName, "actorId", actorId)); // Run the query in Neo4j
			}

			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	public void testGetMovieFail() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String movieName = "Lord of the Rings";
		String movieId = "nm7450264";


		try {

			int expected = 200;
			// Add movie
			connection = this.addMovie(movieName, movieId);
			assertEquals("Incorrect status code for add movie", expected, connection.getResponseCode());


			// Get response
			connection = this.getMovie(null);
			expected = 400;
			assertEquals("Incorrect status code for get movie", expected, connection.getResponseCode());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e.getMessage());
		}
		finally {
			// Disconnect
			if (connection != null)
				connection.disconnect();

			// Remove node(s) added
			try (Session session = Utils.driver.session()) {
				String query = String.format("MATCH (m:%s {%s: $movieName, %s: $movieId}) DELETE m", Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
				session.run(query, Values.parameters("movieName", movieName, "movieId", movieId));
			}

			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}


}
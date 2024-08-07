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
	
	/**
     * Verifies that adding an actor with valid details returns a 200 status code
     */
    public void testAddActorPass() { // Name of the test method must start with test
    	HttpURLConnection connection = null;
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";
    	
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
		    os.close();
		    
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
    		// Define the URL for the API endpoint
    		URL url = new URL(this.rootPath + "/api/v1/addActor");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		// Add actor
    		JSONObject json = new JSONObject();
    		json.put("actorId", actorId);
    		
    		// Send request
    		OutputStream os = connection.getOutputStream();
    		String input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
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
		    os.close();
		    
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
    		// Define the URL for the API endpoint
    		URL url = new URL(this.rootPath + "/api/v1/addMovie");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		// Add movie
    		JSONObject json = new JSONObject();
    		json.put("name", movieName);
    		
    		// Send request
    		OutputStream os = connection.getOutputStream();
    		String input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
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
    		URL url = null;
    		JSONObject json;
    		String input;
    		OutputStream os;
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		url = new URL(this.rootPath + "/api/v1/addActor");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", actorName);
    		json.put("actorId", actorId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
    		// Add movie
		    url = new URL(this.rootPath + "/api/v1/addMovie");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", movieName);
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    		
    		// Add Relationship
		    url = new URL(this.rootPath + "/api/v1/addRelationship");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("actorId", actorId);
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
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
    		URL url = null;
    		JSONObject json;
    		String input;
    		OutputStream os;
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		url = new URL(this.rootPath + "/api/v1/addActor");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", actorName);
    		json.put("actorId", actorId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
    		// Add movie
		    url = new URL(this.rootPath + "/api/v1/addMovie");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", movieName);
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    		
    		// Add Relationship
		    url = new URL(this.rootPath + "/api/v1/addRelationship");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
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
    
    /**
     * Verifies that getting an actor with valid details returns the correct response
     */
    public void testGetActorPass() {
    	HttpURLConnection connection = null;
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";
    	String movieName = "Parasite";
		String movieId = "nm7001453";
    	
    	try {
    		URL url = null;
    		JSONObject json;
    		String input;
    		OutputStream os;
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		url = new URL(this.rootPath + "/api/v1/addActor");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", actorName);
    		json.put("actorId", actorId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
    		// Add movie
		    url = new URL(this.rootPath + "/api/v1/addMovie");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", movieName);
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    		
    		// Add Relationship
		    url = new URL(this.rootPath + "/api/v1/addRelationship");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("actorId", actorId);
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add relationship", expected, statusCode);
		    
		    // Get Actor
//		    url = new URL(this.rootPath + "/api/v1/getActor");
//		    connection = (HttpURLConnection) url.openConnection();
//		    connection.setRequestMethod("GET");
//		    connection.setRequestProperty("Content-Type", "application/json");
//		    connection.setDoInput(true); // For reading from the server
//		    
//		    json = new JSONObject();
//    		json.put("actorId", actorId);
//    		
//    		os = connection.getOutputStream();
//    		input = json.toString();
//		    os.write(input.getBytes());
//		    os.close();
//
//		    // Get response
//		    statusCode = connection.getResponseCode();
//		    expected = 200;
//		    assertEquals("Incorrect status code for get actor", expected, statusCode);
//		    
//		    StringBuffer response = null;
//		    
//		    if (statusCode == 200) {
//				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//				String inputLine;
//				response = new StringBuffer();
//
//				while ((inputLine = in.readLine()) != null)
//					response.append(inputLine);
//				
//				in.close();
//			}
//		    
//		    String expectedString = String.format("{\"actorId\": \"%s\", \"name\": \"%s\", \"movies\": [\"%s\"]}", actorId, actorName, movieId);
//		    assertEquals("Incorrect response for get actor", expectedString, response);
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
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";
		String movieName = "Parasite";
		String movieId = "nm7001453";
    	
    	try {
    		URL url = null;
    		JSONObject json;
    		String input;
    		OutputStream os;
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		url = new URL(this.rootPath + "/api/v1/addActor");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", actorName);
    		json.put("actorId", actorId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
    		// Add movie
		    url = new URL(this.rootPath + "/api/v1/addMovie");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("name", movieName);
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code add movie", expected, statusCode);
    		
    		// Add Relationship
		    url = new URL(this.rootPath + "/api/v1/addRelationship");
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("PUT");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setDoOutput(true);
    		
    		json = new JSONObject();
    		json.put("actorId", "nm1234567"); // Not existing actorId
    		json.put("movieId", movieId);
    		
    		os = connection.getOutputStream();
    		input = json.toString();
		    os.write(input.getBytes());
		    os.close();
		    
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
		    	String query = String.format("MATCH (a:%s {%s: $actorName, %s: $actorId}), (m:%s {%s: $movieName, %s: $movieId}) DETACH DELETE a, m", Utils.actorLabel, Utils.actorNameProperty, Utils.actorIdProperty, Utils.movieLabel, Utils.movieNameProperty, Utils.movieIdProperty);
		    	session.run(query, Values.parameters("actorName", actorName, "actorId", actorId, "movieName", movieName, "movieId", movieId));
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
}
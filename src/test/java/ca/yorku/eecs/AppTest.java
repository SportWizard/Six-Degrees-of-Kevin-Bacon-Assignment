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
import java.net.URLEncoder;
import java.util.Random;
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
    
    /**
     * Add actor to the database
     * @param actorName
     * @param actorId
     * @return The Http connection
     * @throws Exception
     */
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
    
    /**
     * Add movie to the database
     * @param movieName
     * @param movieId
     * @return The http connection
     * @throws Exception
     */
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
    
    /**
     * Add relationship to the database
     * @param actorId
     * @param movieId
     * @return The http connection
     * @throws Exception
     */
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
    
    /**
     * Add info to the database
     * @param infoId
     * @param mpaaRating
     * @param year
     * @param imdbRanking
     * @return The http connection
     * @throws Exception
     */
    private HttpURLConnection addInfo(String infoId, String mpaaRating, String year, String imdbRanking) throws Exception {
        HttpURLConnection connection = null;

        try {
            // Define the URL for the API endpoint
            URL url = new URL(this.rootPath + "/api/v1/addInfo");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Add info
            JSONObject json = new JSONObject();
            json.put("infoId", infoId);
            json.put("mpaaRating", mpaaRating);
            json.put("year", year);
            json.put("imdbRanking", imdbRanking);

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
    
    /**
     * Add movieInfo to the database
     * @param infoId
     * @param movieId
     * @return The http connection
     * @throws Exception
     */
    private HttpURLConnection addMovieInfo(String infoId, String movieId) throws Exception {
        HttpURLConnection connection;

        try {
            URL url = new URL(this.rootPath + "/api/v1/addMovieInfo");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("infoId", infoId);
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
    
    /**
     * Delete node from database
     * @param id
     * @param label
     * @param property
     * @throws Exception
     */
    private void deleteNode(String id, String label, String property) throws Exception {
    	try (Session session = Utils.driver.session()) {
	    	String query = String.format("MATCH (n:%s {%s: $id}) DETACH DELETE n", label, property);
	    	session.run(query, Values.parameters("id", id));
	    }
    }
    
    /**
     * Send Json for Get request
     * @param jsonStr
     * @param request
     * @return The http connection
     * @throws Exception
     */
    private HttpURLConnection getRequest(String jsonStr, String request) throws Exception {
    	HttpURLConnection connection;
    	
    	try {
    		String encoded = URLEncoder.encode(jsonStr, "UTF-8");
    		
    		String path = String.format("/api/v1/%s?jsonStr=", request);
    		URL url = new URL(this.rootPath + path + encoded);
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
		    try { 
		    	this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
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
		    try { 
		    	this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
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
		    try { 
		    	this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
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
    		
    		// Add relationship
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
		    try {
				this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
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
    		
    		// Add relationship
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
		    	this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
		    	this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
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
    		
    		// Add relationship
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
		    	this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
		    	this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting an actor with valid details returns a 200 status code
     */
    public void testGetActorPass() {
    	HttpURLConnection connection = null;
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";
		String[] movieName = {"Parasite", "The Dark Knight"};
		String[] movieId = {"nm7001453", "nm1234567"};
		
		try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		connection = this.addActor(actorName, actorId);
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
			    
	    		// Add relationship
			    connection = this.addRelationship(actorId, movieId[i]);
		    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add relationship", expected, statusCode);
		    }
		    
		    // Get actor
		    String jsonStr = String.format("{\"%s\":%s}", Utils.actorIdProperty, actorId);
		    connection = this.getRequest(jsonStr, "getActor");
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code get actor", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
		    try {
		    	this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting an actor with invalid details returns a 400 status code
     */
    public void testGetActorFail() {
    	HttpURLConnection connection = null;
    	String actorName = "Denzel Washington";
		String actorId = "nm1001213";
		String[] movieName = {"Parasite", "The Dark Knight"};
		String[] movieId = {"nm7001453", "nm1234567"};
		
		try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		connection = this.addActor(actorName, actorId);
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
			    
	    		// Add relationship
			    connection = this.addRelationship(actorId, movieId[i]);
		    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add relationship", expected, statusCode);
		    }
		    
		    // Get actor
		    String jsonStr = String.format("{}");
		    connection = this.getRequest(jsonStr, "getActor");
		    
		    statusCode = connection.getResponseCode();
		    expected = 400;
		    assertEquals("Incorrect status code get actor", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
		    	this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting an actor with invalid details returns a 400 status code
     */
    public void testGetActorFail2() {
    	HttpURLConnection connection = null;
		String actorId = "nm1001213";
		
		try {
    		int statusCode;
    		int expected;
		    
		    // Get actor
		    String jsonStr = String.format("{\"%s\":%s}", Utils.actorIdProperty, actorId);
		    connection = this.getRequest(jsonStr, "getActor");
		    
		    statusCode = connection.getResponseCode();
		    expected = 404;
		    assertEquals("Incorrect status code get actor", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    	}
    }
    
    /**
     * Verifies that getting bacon number with valid details returns a 200 status code
     */
    public void testComputeBaconNumberPass() {
    	HttpURLConnection connection = null;
    	String[] actorName = {"Kevin Bacon", "Al Pacino", "Keanu Reeves", "Hugo Weaving"}; // Must contain Kevin Bacon
		String[] actorId = {"nm1001213", "nm1001214", "nm1001215", "nm1001216"};
		String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		for (int i = 0; i < actorName.length; i++) {
	    		connection = this.addActor(actorName[i], actorId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		}
    		
    		// Add movie
		    for (int i = 0; i < movieName.length; i++) {
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    int p1 = 0;
		    int p2 = 0;
		    
		    // Add relationship
		    while (p1 < actorName.length && p2 < movieName.length) {
		    	if (p1 <= p2) {
				    connection = this.addRelationship(actorId[p1], movieId[p2]);
				    p1++;
		    	}
		    	else {
		    		connection = this.addRelationship(actorId[p1], movieId[p2]);
		    		p2++;
		    	}
		    	
	    		statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add relationship", expected, statusCode);
		    }
		    
		    
		    // Get bacon number
		    Random rand = new Random();
		    int index = rand.nextInt(actorId.length);
		    
		    String jsonStr = String.format("{\"%s\":%s}", Utils.actorIdProperty, actorId[index]);
		    connection = this.getRequest(jsonStr, "computeBaconNumber");
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for get bacon number", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < actorId.length; i++)
    				this.deleteNode(actorId[i], Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting bacon number with invalid details returns a 400 status code
     */
    public void testComputeBaconNumberFail() {
    	HttpURLConnection connection = null;
    	String[] actorName = {"Kevin Bacon", "Al Pacino", "Keanu Reeves", "Hugo Weaving"}; // Must contain Kevin Bacon
		String[] actorId = {"nm1001213", "nm1001214", "nm1001215", "nm1001216"};
		String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		for (int i = 0; i < actorName.length; i++) {
	    		connection = this.addActor(actorName[i], actorId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		}
    		
    		// Add movie
		    for (int i = 0; i < movieName.length; i++) {
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    int p1 = 0;
		    int p2 = 0;
		    
		    // Add relationship
		    while (p1 < actorName.length && p2 < movieName.length) {
		    	if (p1 <= p2) {
				    connection = this.addRelationship(actorId[p1], movieId[p2]);
				    p1++;
		    	}
		    	else {
		    		connection = this.addRelationship(actorId[p1], movieId[p2]);
		    		p2++;
		    	}
		    	
	    		statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add relationship", expected, statusCode);
		    }
		    
		    
		    // Get bacon number
		    String jsonStr = String.format("{}");
		    connection = this.getRequest(jsonStr, "computeBaconNumber");
		    
		    statusCode = connection.getResponseCode();
		    expected = 400;
		    assertEquals("Incorrect status code for get bacon number", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < actorId.length; i++)
    				this.deleteNode(actorId[i], Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting bacon number with invalid details returns a 404 status code
     */
    public void testComputeBaconNumberFail2() {
    	HttpURLConnection connection = null;
    	String[] actorName = {"Kevin Bacon", "Al Pacino", "Keanu Reeves", "Hugo Weaving"}; // Must contain Kevin Bacon
		String[] actorId = {"nm1001213", "nm1001214", "nm1001215", "nm1001216"};
		String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		for (int i = 0; i < actorName.length; i++) {
	    		connection = this.addActor(actorName[i], actorId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		}
    		
    		// Add movie
		    for (int i = 0; i < movieName.length; i++) {
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    // Get bacon number
		    Random rand = new Random();
		    int index = rand.nextInt(actorId.length);
		    
		    // If it chose Kevin Bacon's id, then redo
		    while (actorId[index].equals(actorId[0]))
		    	index = rand.nextInt(actorId.length);
		    
		    String jsonStr = String.format("{\"%s\":%s}", Utils.actorIdProperty, actorId[index]);
		    connection = this.getRequest(jsonStr, "computeBaconNumber");
		    
		    statusCode = connection.getResponseCode();
		    expected = 404;
		    assertEquals("Incorrect status code for get bacon number", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < actorId.length; i++)
    				this.deleteNode(actorId[i], Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting bacon path with valid details returns a 200 status code
     */
    public void testComputeBaconPathPass() {
    	HttpURLConnection connection = null;
    	String[] actorName = {"Kevin Bacon", "Al Pacino", "Keanu Reeves", "Hugo Weaving"}; // Must contain Kevin Bacon
		String[] actorId = {"nm1001213", "nm1001214", "nm1001215", "nm1001216"};
		String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		for (int i = 0; i < actorName.length; i++) {
	    		connection = this.addActor(actorName[i], actorId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		}
    		
    		// Add movie
		    for (int i = 0; i < movieName.length; i++) {
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    int p1 = 0;
		    int p2 = 0;
		    
		    // Add relationship
		    while (p1 < actorName.length && p2 < movieName.length) {
		    	if (p1 <= p2) {
				    connection = this.addRelationship(actorId[p1], movieId[p2]);
				    p1++;
		    	}
		    	else {
		    		connection = this.addRelationship(actorId[p1], movieId[p2]);
		    		p2++;
		    	}
		    	
	    		statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add relationship", expected, statusCode);
		    }
		    
		    
		    // Get bacon path
		    Random rand = new Random();
		    int index = rand.nextInt(actorId.length);
		    
		    String jsonStr = String.format("{\"%s\":%s}", Utils.actorIdProperty, actorId[index]);
		    connection = this.getRequest(jsonStr, "computeBaconPath");
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for get bacon path", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < actorId.length; i++)
    				this.deleteNode(actorId[i], Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting bacon number with invalid details returns a 400 status code
     */
    public void testComputeBaconPathFail() {
    	HttpURLConnection connection = null;
    	String[] actorName = {"Kevin Bacon", "Al Pacino", "Keanu Reeves", "Hugo Weaving"}; // Must contain Kevin Bacon
		String[] actorId = {"nm1001213", "nm1001214", "nm1001215", "nm1001216"};
		String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		for (int i = 0; i < actorName.length; i++) {
	    		connection = this.addActor(actorName[i], actorId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		}
    		
    		// Add movie
		    for (int i = 0; i < movieName.length; i++) {
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    int p1 = 0;
		    int p2 = 0;
		    
		    // Add relationship
		    while (p1 < actorName.length && p2 < movieName.length) {
		    	if (p1 <= p2) {
				    connection = this.addRelationship(actorId[p1], movieId[p2]);
				    p1++;
		    	}
		    	else {
		    		connection = this.addRelationship(actorId[p1], movieId[p2]);
		    		p2++;
		    	}
		    	
	    		statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add relationship", expected, statusCode);
		    }
		    
		    // Get bacon path
		    String jsonStr = String.format("{}");
		    connection = this.getRequest(jsonStr, "computeBaconPath");
		    
		    statusCode = connection.getResponseCode();
		    expected = 400;
		    assertEquals("Incorrect status code for get bacon path", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < actorId.length; i++)
    				this.deleteNode(actorId[i], Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting bacon number with invalid details returns a 404 status code
     */
    public void testComputeBaconPathFail2() {
    	HttpURLConnection connection = null;
    	String[] actorName = {"Kevin Bacon", "Al Pacino", "Keanu Reeves", "Hugo Weaving"}; // Must contain Kevin Bacon
		String[] actorId = {"nm1001213", "nm1001214", "nm1001215", "nm1001216"};
		String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
    	
    	try {
    		int statusCode;
    		int expected;
    		
    		// Add actor
    		for (int i = 0; i < actorName.length; i++) {
	    		connection = this.addActor(actorName[i], actorId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code for add actor", expected, statusCode);
    		}
    		
    		// Add movie
		    for (int i = 0; i < movieName.length; i++) {
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    // Get bacon path
		    Random rand = new Random();
		    int index = rand.nextInt(actorId.length);
		    
		    // If it chose Kevin Bacon's id, then redo
		    while (actorId[index].equals(actorId[0]))
		    	index = rand.nextInt(actorId.length);
		    
		    String jsonStr = String.format("{\"%s\":%s}", Utils.actorIdProperty, actorId[index]);
		    connection = this.getRequest(jsonStr, "computeBaconPath");
		    
		    statusCode = connection.getResponseCode();
		    expected = 404;
		    assertEquals("Incorrect status code for get bacon path", expected, statusCode);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < actorId.length; i++)
    				this.deleteNode(actorId[i], Utils.actorLabel, Utils.actorIdProperty);
		    	
		    	for (int i = 0; i < movieId.length; i++)
		    		this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting rank with valid details returns a 200 status code
     */
    public void testGetRankPass() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "G"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get rank
		    Random rand = new Random();
		    int index = rand.nextInt(movieId.length+1);;
		    
		    String jsonStr = String.format("{\"%s\":%d}", "n", index);
		    connection = this.getRequest(jsonStr, "getRank");
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for get rank", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting rank with invalid details returns a 400 status code
     */
    public void testGetRankFail() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "G"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get rank
		    String jsonStr = String.format("{}");
		    connection = this.getRequest(jsonStr, "getRank");
		    
		    statusCode = connection.getResponseCode();
		    expected = 400;
		    assertEquals("Incorrect status code for get rank", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting rank with invalid details returns a 404 status code
     */
    public void testGetRankFail2() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "G"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get rank
		    Random rand = new Random();
		    int low = -10000;
		    int high = 20000;
		    int index = rand.nextInt(high) - low;
		    
		    while (0 <= index && index <= movieId.length+1) {
		    	System.out.println(index);
		    	index = rand.nextInt(high) - low;
		    }
		    
		    String jsonStr = String.format("{\"%s\":%d}", "n", index);
		    connection = this.getRequest(jsonStr, "getRank");
		    
		    statusCode = connection.getResponseCode();
		    expected = 404;
		    assertEquals("Incorrect status code for get rank", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting MPAA with valid details returns a 200 status code
     */
    public void testGetMPAAPass() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "PG-13"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get MPAA
		    Random rand = new Random();
		    int index = rand.nextInt(infoMpaa.length);
		    
		    String jsonStr = String.format("{\"%s\":%s}", Utils.mpaaRatingProperty, infoMpaa[index]);
		    connection = this.getRequest(jsonStr, "getMPAA");
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for get MPAA", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting MPAA with valid details returns a 400 status code
     */
    public void testGetMPAAFail() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "PG-13"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get MPAA
		    String jsonStr = String.format("{}");
		    connection = this.getRequest(jsonStr, "getMPAA");
		    
		    statusCode = connection.getResponseCode();
		    expected = 400;
		    assertEquals("Incorrect status code for get MPAA", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting MPAA with valid details returns a 404 status code
     */
    public void testGetMPAAFail2() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "PG-13"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get MPAA
		    String jsonStr = String.format("{\"%s\":%s}", Utils.mpaaRatingProperty, "Invalid");
		    connection = this.getRequest(jsonStr, "getMPAA");
		    
		    statusCode = connection.getResponseCode();
		    expected = 404;
		    assertEquals("Incorrect status code for get MPAA", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting year with valid details returns a 200 status code
     */
    public void testGetYearPass() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "PG-13"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get year
		    Random rand = new Random();
		    int index = rand.nextInt(infoYear.length);
		    
		    String jsonStr = String.format("{\"%s\":%s}", Utils.yearProperty, infoYear[index]);
		    connection = this.getRequest(jsonStr, "getYear");
		    
		    statusCode = connection.getResponseCode();
		    expected = 200;
		    assertEquals("Incorrect status code for get year", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting year with valid details returns a 400 status code
     */
    public void testGetYearFail() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "PG-13"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get year
		    String jsonStr = String.format("{}");
		    connection = this.getRequest(jsonStr, "getYear");
		    
		    statusCode = connection.getResponseCode();
		    expected = 400;
		    assertEquals("Incorrect status code for get year", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }
    
    /**
     * Verifies that getting year with valid details returns a 404 status code
     */
    public void testGetYearFail2() {
    	HttpURLConnection connection = null;
    	String[] movieName = {"Parasite", "The Dark Knight", "A Few Good Men"};
		String[] movieId = {"nm7001453", "nm1234567", "nm1234568"};
		String[] infoImdb = {"8.5", "8.6", "7.5"};
		String[] infoMpaa = {"R", "PG-13", "PG-13"};
		String[] infoYear = {"2016", "2012", "2005"};
		String[] infoId = {"nm0987654", "nm0864213", "nm2857493"};
		
		try {
			int statusCode;
    		int expected;
    		
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add movie
			    connection = this.addMovie(movieName[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movie", expected, statusCode);
		    }
		    
		    for (int i = 0; i < movieName.length; i++) {
		    	// Add info
			    connection = this.addInfo(infoId[i], infoMpaa[i], infoYear[i], infoImdb[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add info", expected, statusCode);
			    
			    // Add movieInfo
			    connection = this.addMovieInfo(infoId[i], movieId[i]);
			    
			    statusCode = connection.getResponseCode();
			    expected = 200;
			    assertEquals("Incorrect status code add movieInfo", expected, statusCode);
		    }
		    
		    // Get year
		    String jsonStr = String.format("{\"%s\":%s}", Utils.yearProperty, "-1");
		    connection = this.getRequest(jsonStr, "getYear");
		    
		    statusCode = connection.getResponseCode();
		    expected = 404;
		    assertEquals("Incorrect status code for get year", expected, statusCode);
		}
		catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception occurred: " + e.getMessage());
    	}
    	finally {
    		if (connection != null)
    			connection.disconnect();
    		
    		// Remove node(s) added
    		try {
    			for (int i = 0; i < movieId.length; i++)
    				this.deleteNode(movieId[i], Utils.movieLabel, Utils.movieIdProperty);
    			
    			for (int i = 0; i < infoId.length; i++)
    				this.deleteNode(infoId[i], Utils.infoLabel, Utils.infoIdProperty);
		    }
    		catch (Exception e) {
		    	System.err.println("Exception caught: " + e.getMessage());
		    }
    	}
    }

	/*Verifies that a 200 status code is returned when an existing movie is searched for */
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
			String jsonStr = String.format("{%s:%s}", Utils.movieIdProperty, movieId);
			connection = this.getRequest(jsonStr, "getMovie");
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
			try {
				this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			}
			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 404 status code (not found) is returned when an id not belonging to any movie is provided */
	public void testGetMovieFail() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String movieId = "nm7450264";

		try {
			// Get response
			String jsonStr = String.format("{%s:%s}", Utils.movieIdProperty, movieId);
			connection = this.getRequest(jsonStr, "getMovie");
			int expected = 404;
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
			try  {
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			} catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 200 status code is returned when a valid existing relationship is checked*/
	public void testHasRelationshipPass() { // Name of the test method must start with test
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
			String jsonStr = String.format("{%s:%s, %s:%s}", Utils.movieIdProperty, movieId, Utils.actorIdProperty, actorId);
			connection = this.getRequest(jsonStr, "hasRelationship");
			assertEquals("Incorrect status code for has relationship", expected, connection.getResponseCode());
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
			try {
				this.deleteNode(actorId, Utils.actorLabel, Utils.actorIdProperty);
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			} catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 404 not found status code is returned when a null property is provided*/
	public void testHasRelationshipFail() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String movieName = "Lord of the Rings";
		String movieId = "nm7450264";

		try {
			int expected = 200;
			//add movie
			connection = this.addMovie(movieName, movieId);
			assertEquals("Incorrect status code for add movie", expected, connection.getResponseCode());

			// Get response
			expected = 404; //since actorId won't be found given that it is null
			String jsonStr = String.format("{%s:%s, %s:%s}", Utils.movieIdProperty, movieId, Utils.actorIdProperty, null);
			connection = this.getRequest(jsonStr, "hasRelationship");
			assertEquals("Incorrect status code for has relationship", expected, connection.getResponseCode());
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
			try {
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			} catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 200 code is returned when a valid info node is created */
	public void testAddInfoPass() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String infoId = "nm4562095";
		String year = "2014";
		String imdbRating = "5.7";
		String mpaaRating = "R";

		try {
			// Add info node
			connection = this.addInfo(infoId, mpaaRating, year, imdbRating);
			// Get response
			int expected = 200;
			assertEquals("Incorrect status code for add info", expected, connection.getResponseCode());
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
				this.deleteNode(infoId, Utils.infoLabel, Utils.infoIdProperty);
			}
			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 400 status code is returned when trying to add a node with duplicated infoId */
	public void testAddInfoFail() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String infoId = "nm4562095";
		String year = "2014";
		String imdbRating = "5.7";
		String mpaaRating = "R";

		try {
			// Add info node
			connection = this.addInfo(infoId, mpaaRating, null, null);
			// Get response
			int expected = 200;
			assertEquals("Incorrect status code for add info", expected, connection.getResponseCode());

			//Try adding another info node with the same Id, but different info
			connection = this.addInfo(infoId, null, year, imdbRating);
			expected = 400;
			assertEquals("Incorrect status code for add info", expected, connection.getResponseCode());
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
			try {
				this.deleteNode(infoId, Utils.infoLabel, Utils.infoIdProperty);
			}
			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 200 code is returned when a valid info node to movie node relationship is created */
	public void testAddMovieInfoPass() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String infoId = "nm4562095";
		String year = "1993";
		String imdbRating = "8.2";
		String mpaaRating = "PG-13";

		String movieName = "Jurassic Park";
		String movieId = "nm7405724";
		try {
			// Add info node
			connection = this.addInfo(infoId, mpaaRating, year, imdbRating);
			// Get response
			int expected = 200;
			assertEquals("Incorrect status code for add info", expected, connection.getResponseCode());

			//Add movie node
			connection = this.addMovie(movieName, movieId);
			//Get response
			assertEquals("Incorrect status code for add movie", expected, connection.getResponseCode());

			//create relationship between the two nodes
			connection = this.addMovieInfo(infoId, movieId);
			assertEquals("Incorrect status code for add movie info", expected, connection.getResponseCode());
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
			try {
				this.deleteNode(infoId, Utils.infoLabel, Utils.infoIdProperty);
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			}

			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 404 (not found) code is returned when trying to create an invalid relationship between an existing movie node and non-existing info node */
	public void testAddMovieInfoFail() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String infoId = "nm4562095";

		String movieName = "Jurassic Park";
		String movieId = "nm7405724";
		try {
			//Add movie node
			connection = this.addMovie(movieName, movieId);
			//Get response
			int expected = 200;
			assertEquals("Incorrect status code for add movie", expected, connection.getResponseCode());

			//No info node with this infoId exists
			connection = this.addMovieInfo(infoId, movieId);
			expected = 404;
			assertEquals("Incorrect status code for add movie info", expected, connection.getResponseCode());
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
			try {
				this.deleteNode(infoId, Utils.infoLabel, Utils.infoIdProperty);
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			}

			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 200 code is returned when searching for a valid/existing movie node/info node relationship */
	public void testHasMovieInfoPass() { // Name of the test method must start with test
		HttpURLConnection connection = null;
		String infoId = "nm4562095";
		String year = "1993";
		String imdbRating = "8.2";
		String mpaaRating = "PG-13";

		String movieName = "Jurassic Park";
		String movieId = "nm7405724";

		try {
			int expected = 200;
			// Add info node
			connection = this.addInfo(infoId, mpaaRating, year, imdbRating);
			// Get response
			assertEquals("Incorrect status code for add info", expected, connection.getResponseCode());

			//Add movie node
			connection = this.addMovie(movieName, movieId);
			//Get response
			assertEquals("Incorrect status code for add movie", expected, connection.getResponseCode());

			//create relationship
			connection = this.addMovieInfo(infoId, movieId);
			assertEquals("Incorrect status code for add movie info", expected, connection.getResponseCode());

			//get response
			String jsonStr = String.format("{%s:%s, %s:%s}", Utils.movieIdProperty, movieId, Utils.infoIdProperty, infoId);
			connection = this.getRequest(jsonStr, "hasMovieInfo");
			assertEquals("Incorrect status code for has movie info", expected, connection.getResponseCode());
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
			try {
				this.deleteNode(infoId, Utils.infoLabel, Utils.infoIdProperty);
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			}

			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

	/*Verifies that a 400 status code is returned when making a request with missing parameters*/
	public void testHasMovieInfoFail() { // Name of the test method must start with test
		HttpURLConnection connection = null;

		String movieName = "Jurassic Park";
		String movieId = "nm7405724";

		try {
			int expected = 200;
			//Add movie node
			connection = this.addMovie(movieName, movieId);
			//Get response
			assertEquals("Incorrect status code for add movie", expected, connection.getResponseCode());

			//No info information
			String jsonStr = String.format("{%s:%s}", Utils.movieIdProperty, movieId);
			connection = this.getRequest(jsonStr, "hasMovieInfo");
			expected = 400;
			assertEquals("Incorrect status code for has movie info", expected, connection.getResponseCode());
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
			try {
				this.deleteNode(movieId, Utils.movieLabel, Utils.movieIdProperty);
			}
			catch (Exception e) {
				System.err.println("Exception caught: " + e.getMessage());
			}
		}
	}

}
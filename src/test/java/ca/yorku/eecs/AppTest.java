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
 */
public class AppTest extends TestCase
{   private final String rootPath = "http://localhost:8080";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigorous Test :-)
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

    private HttpURLConnection getActor(String actorId) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.rootPath + "/api/v1/getActor");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Request method
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("actorId", actorId);

            OutputStream os = connection.getOutputStream();
            String input = json.toString();
            os.write(input.getBytes());
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception :" + e.getMessage());
        }

        return connection;
    }

        public void testAddActorPass() { // Name of the test method must start with test
            HttpURLConnection connection = null;
            String actorName = "Elijah Wood";
            String actorId = "nm5927021";

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
    public void testGetActorPass() {
        HttpURLConnection connection = null;
        String actorName = "Elijah Wood";
        String actorId = "nm5927021";

        try {
            // Add actor
            connection = this.addActor(actorName, actorId);
            connection = null;

            connection = this.getActor(actorId);
            int statusCode = connection.getResponseCode();
            int expected = 200;
            assertEquals("Incorrect status code for get actor", expected, statusCode);

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



    public void testApp()
    {
        assertTrue( true );
    }
}

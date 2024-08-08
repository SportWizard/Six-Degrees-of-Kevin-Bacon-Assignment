package ca.yorku.eecs;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;

public class Utils {
    public static String uriDb = "bolt://localhost:7687";
    public static String uriUser = "http://localhost:8080";
    public static Config config = Config.builder().withoutEncryption().build();
    public static Driver driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"), config);
    
    // Allow quick change of labels, properties and relationship's name
    public static final String actorLabel = "actor";
	public static final String actorNameProperty = "name";
	public static final String actorIdProperty = "actorId";
	
	public static final String movieLabel = "movie";
	public static final String movieNameProperty = "name";
	public static final String movieIdProperty = "movieId";
	
	public static final String infoLabel = "info";
	public static final String infoIdProperty = "infoId";
	public static final String imdbRatingProperty = "imdbRating";
	public static final String mpaaRatingProperty = "mpaaRating";
	public static final String yearProperty = "year";
	
	public static final String hasRelationship = "HAS";
	public static final String actedInRelationship = "ACTED_IN";

	public static String convert(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}

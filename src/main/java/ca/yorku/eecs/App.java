package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App {
    static int PORT = 8080;
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

        // Put methods
        server.createContext("/api/v1/addActor", new AddActor());
        server.createContext("/api/v1/addMovieInfo", new AddMovieInfo());
        server.createContext("/api/v1/addMovie", new AddMovie());
        server.createContext("/api/v1/addRelationship", new AddRelationship());
        server.createContext("/api/v1/addInfo", new AddInfo());

        // Get methods
        server.createContext("/api/v1/getActor", new GetActor());
        server.createContext("/api/v1/hasMovieInfo", new HasMovieInfo());
        server.createContext("/api/v1/computeBaconNumber", new ComputeBaconNumber());
        server.createContext("/api/v1/computeBaconPath", new ComputeBaconPath());
        server.createContext("/api/v1/getMovie", new GetMovie());
        server.createContext("/api/v1/getYear", new GetYear());
        server.createContext("/api/v1/hasRelationship", new HasRelationship());
        server.createContext("/api/v1/getMPAA", new GetMPAA());
        server.createContext("/api/v1/getRank", new GetRank());



        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
}

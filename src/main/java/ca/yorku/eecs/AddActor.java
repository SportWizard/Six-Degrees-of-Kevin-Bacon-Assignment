package ca.yorku.eecs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddActor implements HttpHandler{

	@Override
	public void handle(HttpExchange request) {
		try {
			// Only accept POST request
			if (request.getRequestMethod().equals("POST"))
				this.handlePost(request);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handlePost(HttpExchange request) throws IOException, JSONException {
		
	}
}
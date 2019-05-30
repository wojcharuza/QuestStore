package Controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.io.OutputStream;

public class MentorHandleStudents implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response  = "mentor";
        sendResponse(httpExchange, response);
    }





    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

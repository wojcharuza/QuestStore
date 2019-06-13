package Service;

import com.sun.net.httpserver.HttpExchange;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RequestResponseService {

    public void getLoginPage(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/index.twig");
        JtwigModel model = JtwigModel.newModel();
        String response = template.render(model);
        sendResponse(httpExchange, response);
    }


    public void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    public Map<String, String> getFormData(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        Map<String, String> inputs = parseFormData(formData);
        return  inputs;
    }


    public Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }









}

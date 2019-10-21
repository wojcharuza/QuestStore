package Controller;


import Dao.DaoException;
import Dao.LoginDao;
import Dao.SessionDao;
import Service.RequestResponseService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import idGenerators.IdGenerator;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class LoginController implements HttpHandler {
    private LoginDao loginDao;
    private SessionDao sessionDao;
    private RequestResponseService reqRespServ;
    private static final String SESSION_COOKIE_NAME = "sessionId";


    public LoginController(LoginDao loginDao, SessionDao sessionDao, RequestResponseService reqRespServ){
        this.loginDao = loginDao;
        this.sessionDao = sessionDao;
        this.reqRespServ = reqRespServ;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws  IOException {
        String method = httpExchange.getRequestMethod();
        HttpCookie cookie;

        if (method.equals("GET")){
           getLoginPage(httpExchange);
        }

        else if (method.equals("POST")){
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map<String, String> data = reqRespServ.parseFormData(formData);
            String email = data.get("email");
            IdGenerator idGenerator = new IdGenerator();
            String sessionId = idGenerator.generateId(10);

            try {
                int userId = loginDao.getIdByMail(email);
                sessionDao.addSession(userId, sessionId);

            } catch (DaoException e) {
                e.printStackTrace();
            }

            cookie = new HttpCookie(SESSION_COOKIE_NAME, sessionId);
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());

            try {
                String permission = loginDao.checkPermission(data.get("email"), data.get("password"));
                switch (permission){
                    case "admin":

                        httpExchange.getResponseHeaders().set("Location", "admin/mentors");
                        httpExchange.sendResponseHeaders(302,-1);

                        break;

                    case "mentor":
                        httpExchange.getResponseHeaders().set("Location", "mentor/students");
                        httpExchange.sendResponseHeaders(302,-1);
                        break;


                    case "student":
                        httpExchange.getResponseHeaders().set("Location", "student/profile");
                        httpExchange.sendResponseHeaders(302,-1);
                        break;

                    default:
                        getLoginPage(httpExchange);
                }

            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
    }

    private void getLoginPage(HttpExchange httpExchange) throws IOException {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/index.twig");
        JtwigModel model = JtwigModel.newModel();
        String response = template.render(model);
        reqRespServ.sendResponse(httpExchange, response);
    }


    public static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }
}

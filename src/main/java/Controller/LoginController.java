package Controller;


import Dao.DaoException;
import Dao.LoginDao;
import Dao.SessionDao;
import Service.RequestResponseService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import idGenerators.IdGenerator;
import java.io.*;
import java.net.HttpCookie;
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
           reqRespServ.getLoginPage(httpExchange);
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
                        httpExchange.sendResponseHeaders(302,0);

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
                        reqRespServ.getLoginPage(httpExchange);
                }

            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
    }
}

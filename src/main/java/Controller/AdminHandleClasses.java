package Controller;

import Dao.ClassroomDao;
import Dao.ClassroomDaoImpl;
import Dao.DaoException;
import Dao.MentorDao;
import Model.Classroom;
import Model.Mentor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminHandleClasses implements HttpHandler {
    private ClassroomDao classroomDao;
    private MentorDao mentorDao;

    public AdminHandleClasses(ClassroomDao classroomDao, MentorDao mentorDao) {
        this.classroomDao = classroomDao;
        this.mentorDao = mentorDao;
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if (method.equals("GET")){
            try {
                getPage(httpExchange);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
        if (method.equals("POST")) {
            Map<String, String> inputs = getFormData(httpExchange);
            String date = inputs.get("date");
            String mentor = inputs.get("mentor");
            System.out.println(date);
            System.out.println(mentor);
        }
    }

    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        List<Classroom> classrooms = classroomDao.getClassrooms();
        List<Mentor> mentors = mentorDao.getMentors();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/classes.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("classrooms", classrooms);
        model.with("mentors", mentors);
        String response = template.render(model);

        sendResponse(httpExchange, response);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> getFormData(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        System.out.println(formData);
        Map inputs = LoginController.parseFormData(formData);
        return inputs;
    }
}

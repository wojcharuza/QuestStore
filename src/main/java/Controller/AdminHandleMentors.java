package Controller;

import Dao.ClassroomDao;
import Dao.DaoException;
import Dao.MentorDao;
import Dao.StudentDao;
import Model.Classroom;
import Model.Mentor;
import Model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class AdminHandleMentors implements HttpHandler {
    private MentorDao mentorDao;
    private ClassroomDao classroomDao;
    private StudentDao studentDao;

    public AdminHandleMentors(MentorDao mentorDao, ClassroomDao classroomDao, StudentDao studentDao){
        this.mentorDao = mentorDao;
        this.studentDao = studentDao;
        this.classroomDao = classroomDao;
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
    }





    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        List<Mentor> mentors = mentorDao.getMentors();
        List<Classroom> classrooms = classroomDao.getClassrooms();
        List<Student> students = studentDao.getAllStudents();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin_mentors.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("mentors", mentors);
        model.with("classrooms", classrooms);
        model.with("students", students);
        String response = template.render(model);
        sendResponse(httpExchange, response);
    }




    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

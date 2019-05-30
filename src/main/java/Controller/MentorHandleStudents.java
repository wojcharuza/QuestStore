package Controller;

import Dao.DaoException;
import Dao.StudentDao;
import Model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class MentorHandleStudents implements HttpHandler {
    private StudentDao studentDao;

    public MentorHandleStudents(StudentDao studentDao) {
        this.studentDao = studentDao;
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
        List<Student> students = studentDao.getAllStudents();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor_students.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("students", students);
        String response = template.render(model);
        sendResponse(httpExchange, response);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Dao.StudentDao;
import Model.Card;
import Model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


public class MentorHandleStudents implements HttpHandler {
    private StudentDao studentDao;
    private CardDao cardDao;

    public MentorHandleStudents(StudentDao studentDao, CardDao cardDao) {
        this.studentDao = studentDao;
        this.cardDao = cardDao;
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
            String firstName = inputs.get("firstName");
            String lastName = inputs.get("lastName");
            String email = inputs.get("email");
            int id = Integer.valueOf(inputs.get("studentId"));
            try {
                studentDao.editStudent(id, firstName, lastName, email);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        List<Student> students = studentDao.getAllStudents();
        List<Card> quests = cardDao.getQuests();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor_students.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("students", students);
        model.with("quests", quests);
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
        Map<String, String> inputs = LoginController.parseFormData(formData);
        return inputs;
    }
}

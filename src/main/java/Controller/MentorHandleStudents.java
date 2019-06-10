package Controller;

import Dao.*;
import Model.Card;
import Model.Classroom;
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
    private TransactionDao transactionDao;
    private ClassroomDao classroomDao;

    public MentorHandleStudents(StudentDao studentDao, CardDao cardDao, TransactionDao transactionDao, ClassroomDao classroomDao) {
        this.studentDao = studentDao;
        this.cardDao = cardDao;
        this.transactionDao = transactionDao;
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

        if (method.equals("POST")) {
            Map<String, String> inputs = getFormData(httpExchange);
            if (inputs.get("formType").equals("editStudent")) {
                editStudent(inputs);
            } else if (inputs.get("formType").equals("addQuest")) {
                addQuestCompleted(httpExchange, inputs);
            } else if (inputs.get("formType").equals("addStudent")) {
                addStudent(httpExchange, inputs);
            }
        }
    }

    private void editStudent(Map<String, String> inputs) {
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

    private void addStudent(HttpExchange httpExchange, Map<String, String> inputs) throws IOException {
        String name = inputs.get("firstName");
        String surname = inputs.get("surname");
        String email = inputs.get("email");
        String password = inputs.get("password");
        int classRoomId = Integer.parseInt(inputs.get("classroom"));
        try {
            studentDao.addNewStudent(name, surname, email, password, classRoomId);
            getPage(httpExchange);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    private void addQuestCompleted(HttpExchange httpExchange, Map<String, String> inputs) throws IOException {
        int id = Integer.valueOf(inputs.get("student"));
        String quest = inputs.get("title");
        try {
            transactionDao.markQuestCompletedByStudent(quest, id);
            getPage(httpExchange);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        List<Student> students = studentDao.getAllStudents();
        List<Card> quests = cardDao.getQuests();
        List<Classroom> classrooms = classroomDao.getClassrooms();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor_students.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("students", students);
        model.with("quests", quests);
        model.with("classrooms", classrooms);
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

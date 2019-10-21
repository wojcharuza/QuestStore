package Controller;

import Dao.*;
import Model.Card;
import Model.Classroom;
import Model.Student;
import Service.RequestResponseService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


public class MentorHandleStudents implements HttpHandler {
    private StudentDao studentDao;
    private CardDao cardDao;
    private TransactionDao transactionDao;
    private ClassroomDao classroomDao;
    private SessionHandler sessionHandler;
    private RequestResponseService reqRespServ;

    public MentorHandleStudents(StudentDao studentDao, CardDao cardDao,
                                TransactionDao transactionDao, ClassroomDao classroomDao,
                                SessionHandler sessionHandler,
                                RequestResponseService reqRespServ) {

        this.studentDao = studentDao;
        this.cardDao = cardDao;
        this.transactionDao = transactionDao;
        this.classroomDao = classroomDao;
        this.sessionHandler = sessionHandler;
        this.reqRespServ = reqRespServ;
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


        else if (method.equals("POST")) {
            Map<String, String> inputs = reqRespServ.getFormData(httpExchange);

            if (inputs.get("formType").equals("editStudent")) {
                editStudent(inputs);
            }
            else if (inputs.get("formType").equals("addQuest")) {
                addQuestCompleted(httpExchange, inputs);
            }
            else if (inputs.get("formType").equals("addStudent")) {
                addStudent(httpExchange, inputs);
            }
            else if (inputs.get("formType").equals("deleteStudent")) {
                try {
                    deleteStudent(inputs);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }
            else if(inputs.get("formType").equals("logout")){
                try {
                    sessionHandler.deleteSession(httpExchange);
                    reqRespServ.getLoginPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
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


    private void deleteStudent(Map<String, String> inputs) throws DaoException {
        studentDao.deleteStudent(Integer.valueOf(inputs.get("deleteStudentId")));
        transactionDao.deleteTransactionsById(Integer.valueOf(inputs.get("deleteStudentId")));
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
        SessionHandler sessionHandler = new SessionHandler();
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Student> students = studentDao.getStudentsByMentor(userId);
            List<Card> quests = cardDao.getQuests();
            List<Classroom> classrooms = classroomDao.getClassrooms();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor_students.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("students", students);
            model.with("quests", quests);
            model.with("classrooms", classrooms);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        } catch (DaoException | NoSuchElementException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }
}

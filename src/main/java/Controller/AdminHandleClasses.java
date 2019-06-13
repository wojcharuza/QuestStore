package Controller;

import Dao.*;
import Model.Classroom;
import Model.Mentor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import java.io.*;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AdminHandleClasses implements HttpHandler {

    private ClassroomDao classroomDao;
    private MentorDao mentorDao;
    private SessionHandler sessionHandler;
    private StudentDao studentDao;
    private TransactionDao transactionDao;

    public AdminHandleClasses(ClassroomDao classroomDao,
                              MentorDao mentorDao,
                              SessionHandler sessionHandler,
                              StudentDao studentDao,
                              TransactionDao transactionDao) {

        this.classroomDao = classroomDao;
        this.mentorDao = mentorDao;
        this.sessionHandler = sessionHandler;
        this.studentDao = studentDao;
        this.transactionDao = transactionDao;
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
            Map<String, String> inputs = getFormData(httpExchange);

            if(inputs.get("formType").equals("addClass")){
                addClassroom(inputs);
                try {
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }

            else if(inputs.get("formType").equals("deleteClass")){
                try {
                    deleteClassroom(inputs);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }

            else if (inputs.get("formType").equals("assignMentor")) {
                try {
                    assignNewMentor(inputs);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }

            else if(inputs.get("formType").equals("logout")){
                try {
                    sessionHandler.deleteSession(httpExchange);
                    getLoginPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    private void deleteClassroom(Map<String, String> inputs) throws DaoException {
        int classRoomId = Integer.valueOf(inputs.get("deleteClassId"));
        classroomDao.deleteClassRoom(classRoomId);
        studentDao.deleteStudentsFromClassroom(classRoomId);
        List<Integer> studentsIds = studentDao.getStudentsIdsFromClassroom(classRoomId);
        transactionDao.deleteTransactionsByIds(studentsIds);
    }


    private void assignNewMentor(Map<String, String> inputs) throws DaoException {
        int classId = Integer.valueOf(inputs.get("AssignedClassId"));
        int mentorId = Integer.valueOf(inputs.get("mentor"));
        classroomDao.assignNewMentor(classId, mentorId);
    }


    private void addClassroom(Map<String, String> inputs){
        String date = inputs.get("date");
        date = date.replaceAll("/", "-");
        String mentorId = inputs.get("mentor");
        try {
            classroomDao.addClassroom(date, Integer.valueOf(mentorId));
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }


    private void getLoginPage(HttpExchange httpExchange) throws IOException{
        httpExchange.getResponseHeaders().set("Location", "/login");
        httpExchange.sendResponseHeaders(302,0);
    }


    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        SessionHandler sessionHandler = new SessionHandler();
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Classroom> classrooms = classroomDao.getClassrooms();
            List<Mentor> mentors = mentorDao.getMentors();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin_classes.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("classrooms", classrooms);
            model.with("mentors", mentors);
            String response = template.render(model);
            sendResponse(httpExchange, response);
        }
        catch (DaoException | NoSuchElementException e){
            getLoginPage(httpExchange);
        }
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
        return  inputs;
    }
}

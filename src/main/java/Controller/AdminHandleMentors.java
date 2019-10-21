package Controller;

import Dao.ClassroomDao;
import Dao.DaoException;
import Dao.MentorDao;
import Dao.StudentDao;
import Model.Classroom;
import Model.Mentor;
import Model.Student;
import Service.RequestResponseService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AdminHandleMentors implements HttpHandler {
    private MentorDao mentorDao;
    private ClassroomDao classroomDao;
    private StudentDao studentDao;
    private SessionHandler sessionHandler;
    private RequestResponseService reqRespServ;

    public AdminHandleMentors(MentorDao mentorDao, ClassroomDao classroomDao,
                              StudentDao studentDao, SessionHandler sessionHandler,
                              RequestResponseService reqRespServ){
        this.mentorDao = mentorDao;
        this.studentDao = studentDao;
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

            if(inputs.get("formType").equals("editMentor")){
                editMentor(inputs);
            }

            else if(inputs.get("formType").equals("addMentor")){
                addMentor(inputs);
                try {
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }

            else if (inputs.get("formType").equals("deleteMentor")) {
                try {
                    deleteMentor(inputs);
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


    private void addMentor(Map<String, String> inputs){
        String firstName = inputs.get("firstName");
        String lastName = inputs.get("lastName");
        String email = inputs.get("email");
        String password = inputs.get("password");
        try{
            mentorDao.addMentor(firstName, lastName, email, password);
        }
        catch (DaoException e){
            e.printStackTrace();
        }
    }


    private void editMentor(Map<String, String> inputs){
        String firstName = inputs.get("firstName");
        String lastName = inputs.get("lastName");
        String email = inputs.get("email");
        int id = Integer.valueOf(inputs.get("mentorId"));
        try {
            mentorDao.editMentor(id, firstName, lastName, email);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }


    private void deleteMentor(Map<String, String> inputs) throws DaoException {
        mentorDao.deleteMentor(Integer.valueOf(inputs.get("deleteMentorId")));
        classroomDao.setMentorIdAsNull(Integer.valueOf(inputs.get("deleteMentorId")));
    }


    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        SessionHandler sessionHandler = new SessionHandler();
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);

        try{
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Mentor> mentors = mentorDao.getMentors();
            List<Classroom> classrooms = classroomDao.getClassrooms();
            List<Student> students = studentDao.getAllStudents();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin_mentors.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("mentors", mentors);
            model.with("classrooms", classrooms);
            model.with("students", students);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        }
        catch (DaoException | NoSuchElementException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }
}

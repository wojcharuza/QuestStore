import Controller.*;
import Dao.*;
import Service.RequestResponseService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;


public class Main {

    public static void main(String[] args){
        RequestResponseService reqRespServ = new RequestResponseService();
        SessionDao sessionDao = new SessionDaoImpl();
        LoginDao loginDao = new LoginDaoImpl();
        ClassroomDao classroomDao = new ClassroomDaoImpl();
        MentorDao mentorDao = new MentorDaoImpl();
        StudentDao studentDao = new StudentDaoImpl();
        LevelDao levelDao = new LevelDaoImpl();
        CardDao cardDao = new CardDaoImpl();
        TransactionDao transactionDao = new TransactionDaoImpl();
        SessionHandler sessionHandler = new SessionHandler();

        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/login", new LoginController(loginDao, sessionDao, reqRespServ));
        server.createContext("/static", new Static());

        server.createContext("/admin/mentors", new AdminHandleMentors(mentorDao, classroomDao, studentDao, sessionHandler, reqRespServ));
        server.createContext("/admin/classes", new AdminHandleClasses(classroomDao, mentorDao, sessionHandler, studentDao, transactionDao, reqRespServ));
        server.createContext("/admin/levels", new AdminHandleLevels(levelDao, sessionHandler, reqRespServ));

        server.createContext("/mentor/students", new MentorHandleStudents(studentDao, cardDao, transactionDao, classroomDao, sessionHandler, reqRespServ));
        server.createContext("/mentor/artifacts", new MentorHandleArtifacts(cardDao, sessionHandler, reqRespServ));
        server.createContext("/mentor/quests", new MentorHandleQuests(cardDao, sessionHandler, reqRespServ));

        server.createContext("/student/profile", new StudentHandleProfile(studentDao,transactionDao, levelDao, sessionHandler, reqRespServ));
        server.createContext("/student/shop", new StudentHandleShop(cardDao, studentDao, transactionDao, sessionHandler, reqRespServ));
        server.createContext("/student/contribution", new StudentHandleContribution(cardDao, studentDao, transactionDao, sessionHandler, reqRespServ));

        server.setExecutor(null);
        server.start();




    }
}

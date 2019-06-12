import Controller.*;
import Dao.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;


public class Main {



    public static void main(String[] args) throws IOException, DaoException {
        SessionDao sessionDao = new SessionDaoImpl();
        LoginDao loginDao = new LoginDaoImpl();
        ClassroomDao classroomDao = new ClassroomDaoImpl();
        MentorDao mentorDao = new MentorDaoImpl();
        StudentDao studentDao = new StudentDaoImpl();
        LevelDao levelDao = new LevelDaoImpl();
        CardDao cardDao = new CardDaoImpl();
        TransactionDao transactionDao = new TransactionDaoImpl();
        SessionHandler sessionHandler = new SessionHandler();

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/login", new LoginController(loginDao, sessionDao));
        server.createContext("/static", new Static());
        server.createContext("/student/profile", new StudentHandleProfile(studentDao,transactionDao, levelDao));
        server.createContext("/student/shop", new StudentHandleShop(cardDao, studentDao, transactionDao));
        server.createContext("/admin/mentors", new AdminHandleMentors(mentorDao, classroomDao, studentDao, sessionHandler));
        server.createContext("/admin/classes", new AdminHandleClasses(classroomDao, mentorDao, sessionHandler));
        server.createContext("/admin/levels", new AdminHandleLevels(levelDao, sessionHandler));
        server.createContext("/mentor/students", new MentorHandleStudents(studentDao, cardDao, transactionDao, classroomDao));
        server.createContext("/mentor/artifacts", new MentorHandleArtifacts(cardDao));
        server.createContext("/mentor/quests", new MentorHandleQuests(cardDao));

        server.setExecutor(null);
        server.start();




    }
}

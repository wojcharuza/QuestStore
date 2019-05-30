import Controller.*;
import Dao.*;
import Model.Card;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    //TODO DATA JEST WYSYLANA W POSTACI URL WIEC TRZEBA JA ZDEKODOWAC


    public static void main(String[] args) throws IOException {
        LoginDao loginDao = new LoginDaoImpl();
        ClassroomDao classroomDao = new ClassroomDaoImpl();
        MentorDao mentorDao = new MentorDaoImpl();
        StudentDao studentDao = new StudentDaoImpl();
        CardDao cardDao = new CardDaoImpl();
        TransactionDao transactionDao = new TransactionDaoImpl();

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/login", new LoginController(loginDao));
        server.createContext("/static", new Static());
        server.createContext("/student/profile", new StudentHandleProfile(studentDao,transactionDao));
        server.createContext("/student/shop", new StudentHandleShop(cardDao, studentDao, transactionDao));

        server.createContext("/admin/mentors", new AdminHandleMentors(mentorDao, classroomDao, studentDao));
        server.createContext("/admin/classes", new AdminHandleClasses(classroomDao, mentorDao));
        server.createContext("/mentor/students", new MentorHandleStudents());
        server.createContext("/mentor/artifacts", new MentorHandleArtifacts(cardDao));
        server.setExecutor(null);
        server.start();




    }
}

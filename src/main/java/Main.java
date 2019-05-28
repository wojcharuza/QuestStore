import Controller.*;
import Dao.LoginDao;
import Dao.LoginDaoImpl;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        LoginDao loginDao = new LoginDaoImpl();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/login", new LoginController(loginDao));
        server.createContext("/static", new Static());
        server.createContext("/mentor/students", new AdminHandleMentors());
        server.createContext("/admin/classes", new AdminHandleClasses());





        server.createContext("/mentor/students", new MentorHandleStudents());
        server.setExecutor(null);
        server.start();




    }
}

package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Dao.StudentDao;
import Dao.TransactionDao;
import Model.Card;
import Model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentHandleProfile  implements HttpHandler  {

    private StudentDao studentDao;
    private TransactionDao transactionDao;
    private CardDao cardDao;


    public StudentHandleProfile(StudentDao studentDao, TransactionDao transactionDao){
        this.studentDao = studentDao;
        this.transactionDao = transactionDao;
        //this.cardDao = cardDao;
    }



    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpCookie cookie;
        String method = httpExchange.getRequestMethod();
        String sessionCookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        System.out.println(sessionCookie+ "final coooooookie");
        String email = getEmailFromCookie(sessionCookie);
        System.out.println(email);



        if (method.equals("GET")){
            getLoginPage(httpExchange, email);
        }
    }

    private void getLoginPage(HttpExchange httpExchange, String email) throws IOException{
        Student student = getLoggedStudentByMail(email);
        List<Card> studentCards = getLoggedStudentCards(student.getId());
        System.out.println(studentCards.size());

        int studentEXP = getLoggedStudentEXP(studentCards);



        String fullname = student.getFirstName() + " " + student.getLastName();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/profile.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("Name", fullname);
        model.with("experience", studentEXP);
        model.with("coolcoins", student.getCoolcoins());
        model.with("studentCards", studentCards);


        String response = template.render(model);
        sendResponse(httpExchange, response);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    public String getEmailFromCookie(String cookie){
        String[] splitedCoookie = cookie.split(";");
        String email = splitedCoookie[2];
        String updatedEmail = email.replace("email=", "");
        String trueMail = updatedEmail.substring(2,updatedEmail.length()-1);
        return trueMail;
    }

    public Student getLoggedStudentByMail(String email) throws IOException {
        Student student = new Student.Builder().build();
        try{
            student = studentDao.getStudentByEmail(email);
            System.out.println(student.getLastName() + "this is name");


        }catch (DaoException e){
            e.printStackTrace();
        }
        return student;
    }

    public List<Card> getLoggedStudentCards(int studentID) throws IOException {
        List<Card> userCards = new ArrayList<>();
        try{
            userCards = transactionDao.getCardsUsedByStudent(studentID);
        }catch (DaoException e){
            e.printStackTrace();
        }
        return userCards;
    }

    public int getLoggedStudentEXP (List<Card> studentCards){
        int studentEXP = 0;
        for (Card c: studentCards){
            studentEXP = studentEXP + c.getCoolcoinValue();
        }
        return studentEXP;
    }




}

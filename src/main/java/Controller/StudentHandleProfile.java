package Controller;

import Dao.*;
import Model.Card;
import Model.Level;
import Model.Student;
import com.google.common.collect.Lists;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.*;

public class StudentHandleProfile  implements HttpHandler  {

    private StudentDao studentDao;
    private TransactionDao transactionDao;
    private CardDao cardDao;
    private  LevelDao levelDao;
    CookieHelper cookieHelper = new CookieHelper();


    public StudentHandleProfile(StudentDao studentDao, TransactionDao transactionDao, LevelDao levelDao){
        this.studentDao = studentDao;
        this.transactionDao = transactionDao;
        //this.cardDao = cardDao;
        this.levelDao = levelDao;
    }



    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> cookie = getSessionCookie(httpExchange);
        String method = httpExchange.getRequestMethod();

        //String sessionCookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        //cookie = HttpCookie.parse()

        System.out.println(cookie.get().getValue() + "   get 1 from session cookie");
        String email = getEmailFromCookie(cookie.get().getValue());
        System.out.println(email + " get name");



        if (method.equals("GET")){
            getLoginPage(httpExchange, email);
        }
    }

    private void getLoginPage(HttpExchange httpExchange, String email) throws IOException{
        Student student = getLoggedStudentByMail(email);

        List<Card> studentCards = getLoggedStudentCards(student.getId());
        List<Card> studentQuests = getStudentQuests(student.getId());

        int studentExp = getLoggedStudentEXP(studentQuests);
        int percentExp = percentOfEXP(studentExp);


        String fullname = student.getFirstName() + " " + student.getLastName();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/profile.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("Name", fullname);
        model.with("experience", studentExp);
        model.with("coolcoins", student.getCoolcoins());
        model.with("studentCards", studentCards);
        model.with("percentEXP", percentExp);


        String response = template.render(model);
        sendResponse(httpExchange, response);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    public String getEmailFromCookie(String emailFromCookie){
        System.out.println(emailFromCookie + "   cookie in method");
        String trueMail = emailFromCookie.substring(1,emailFromCookie.length()-1);
        System.out.println(trueMail + "mail in method");
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



    public List<Card> getStudentQuests(int studentID){

        List<Card> quests = new ArrayList<>();
        try{
            quests = transactionDao.questsComplitedByStudent(studentID);
            return quests;
        }catch (DaoException e){
            e.printStackTrace();
        }
        return quests;
    }





    public int getLoggedStudentEXP (List<Card> studentCards){

        int studentEXP = 0;
        for (Card c: studentCards){
            String type = c.getCardType();
            if(type.startsWith("quest") ){
                studentEXP = studentEXP + c.getCoolcoinValue();
            }
        }
        System.out.println(studentEXP + "");
        return studentEXP;
    }




    private int percentOfEXP (int studentExp){

        List<Level> levels = new ArrayList<>();
        int percentExp = 0;

        System.out.println(percentExp);

        try {
            levels = levelDao.getLevels();
        }catch (DaoException e){
            e.printStackTrace();
        }
        System.out.println(levels.get(1).getLevelNumber());


        for (int i = 0; i <= levels.size(); i++){
            int levelExp = levels.get(i).getExperienceNeeded();
            int nextLevelExp = levels.get(i+1).getExperienceNeeded();
            if (studentExp <= levelExp ){
                int expNeeded = levelExp;
                percentExp = (studentExp*100)/expNeeded;
                return percentExp;

            } else if (studentExp <= nextLevelExp && studentExp>levelExp){
                int expNeeded = nextLevelExp;
                percentExp = (studentExp*100)/expNeeded;
                return percentExp;

            }
        }

        System.out.println(percentExp + "    percent exp");
        return percentExp;
    }

    private Optional<HttpCookie> getSessionCookie(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        System.out.println(cookies + "lista w get session Cookie");
        return cookieHelper.findCookieByName("email", cookies);
    }

}

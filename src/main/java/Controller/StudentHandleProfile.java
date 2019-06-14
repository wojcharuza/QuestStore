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
    private  LevelDao levelDao;
    private SessionHandler sessionHandler;


    public StudentHandleProfile(StudentDao studentDao, TransactionDao transactionDao, LevelDao levelDao, SessionHandler sessionHandler){
        this.studentDao = studentDao;
        this.transactionDao = transactionDao;
        this.levelDao = levelDao;
        this.sessionHandler = sessionHandler;
    }



    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if (method.equals("GET")){
            getPage(httpExchange);
        } else if (method.equals("POST")) {
            try {
                sessionHandler.deleteSession(httpExchange);
                getLoginPage(httpExchange);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPage(HttpExchange httpExchange) throws IOException{
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);

            Student student = studentDao.getStudent(userId);

            List<Card> studentCards = getLoggedStudentCards(userId);
            List<Card> studentQuests = getStudentQuests(userId);
            int studentExp = getLoggedStudentEXP(studentQuests);
            int percentExp = percentOfEXP(studentExp, getLevelsFromDatabase());
            int studentLevel = getStudentLevel(getLevelsFromDatabase(), studentExp);
            String fullname = student.getFirstName() + " " + student.getLastName();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/profile.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("Name", fullname);
            model.with("experience", studentExp);
            model.with("coolcoins", student.getCoolcoins());
            model.with("studentCards", studentCards);
            model.with("percentEXP", percentExp);
            model.with("level", studentLevel);
            String response = template.render(model);
            sendResponse(httpExchange, response);
        } catch (DaoException | NoSuchElementException e) {
            getLoginPage(httpExchange);
        }
    }

    private void getLoginPage(HttpExchange httpExchange) throws IOException{
        httpExchange.getResponseHeaders().set("Location", "/login");
        httpExchange.sendResponseHeaders(302,0);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
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
        return studentEXP;
    }




    private int percentOfEXP (int studentExp, List<Level> levels){
        int percentExp = 1;

        for (int i = 0; i <= levels.size(); i++){
            int levelExp = levels.get(i).getExperienceNeeded();
            int nextLevelExp = levels.get(i+1).getExperienceNeeded();
            if(studentExp == 0){
                return 0;
            }
            if (studentExp >= levels.get(levels.size() - 1).getExperienceNeeded()){
                percentExp = 99;
                return percentExp;
            }

            if (studentExp <= levelExp ){
                int expNeeded = levelExp;
                percentExp = (studentExp*100)/expNeeded;
                return percentExp;

            } else if (studentExp <= nextLevelExp && studentExp>levelExp){
                int expNeeded = nextLevelExp - levelExp;
                percentExp = ((studentExp-levelExp)*100)/expNeeded;
                return percentExp;
            }
        }
        return percentExp;
    }

    private int getStudentLevel(List<Level> levels, int studentExp){
        int currentLevel = 1;
        for (int i = 0; i <= levels.size(); i++){
            int levelExp = levels.get(i).getExperienceNeeded();
            int nextLevelExp = levels.get(i+1).getExperienceNeeded();
            if (studentExp >= levelExp && studentExp < nextLevelExp){
                currentLevel = levels.get(i).getLevelNumber();
                return currentLevel;
            } else if(studentExp > levels.get(levels.size() - 1).getLevelNumber()){
                currentLevel = levels.get(levels.size() - 1).getLevelNumber();
                return currentLevel;
            } else {
                currentLevel = 999;
                return currentLevel;
            }
        }
        return currentLevel;
    }

    private List<Level> getLevelsFromDatabase(){
        List<Level> levels = new ArrayList<>();
        try {
            levels = levelDao.getLevels();
        }catch (DaoException e){
            e.printStackTrace();
        }
        //System.out.println(levels.get(1).getLevelNumber());
        return levels;
    }
}

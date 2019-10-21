package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Dao.StudentDao;
import Dao.TransactionDao;
import Model.Card;
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
import java.util.*;

public class StudentHandleShop implements HttpHandler{

    private CardDao cardDao;
    private StudentDao studentDao;
    private TransactionDao transactionDao;
    private SessionHandler sessionHandler;
    private RequestResponseService reqRespServ;

    public StudentHandleShop(CardDao cardDao,StudentDao studentDao,
                             TransactionDao transactionDao, SessionHandler sessionHandler,
                             RequestResponseService reqRespServ){
        this.cardDao = cardDao;
        this.studentDao = studentDao;
        this.transactionDao = transactionDao;
        this.sessionHandler = sessionHandler;
        this.reqRespServ = reqRespServ;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        Student student = getStudentByID(httpExchange,cookie);

        if (method.equals("GET")) {
            getPage(httpExchange);
        }
        Map<String, String> inputs = reqRespServ.getFormData(httpExchange);

        if (method.equals("POST")) {

            if (inputs.get("formType").equals("title")) {
                String title = inputs.get("title");
                if (verifyAbilityOfPurchase(title, student.getCoolcoins())) {
                    addTransactionToDatabase(title, student);
                    getSuccessPage(httpExchange);

                } else {
                    getFailedPage(httpExchange);

                }


            } else if (inputs.get("formType").equals("logout")) {
                try {
                    sessionHandler.deleteSession(httpExchange);
                    reqRespServ.getLoginPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getPage(HttpExchange httpExchange) throws IOException{
        List<Card> artifacts = getArtifacts();
        List<Card> artifactsBasic = getBasicArtifacts(artifacts);
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/shop.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("artifacts", artifactsBasic);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        }catch (DaoException | NoSuchElementException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }
    

    public void getSuccessPage(HttpExchange httpExchange)throws IOException{
        List<Card> artifacts = getArtifacts();
        List<Card> artifactsBasic = getBasicArtifacts(artifacts);
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/purchase_success.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("artifacts", artifactsBasic);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        }catch (DaoException | NoSuchElementException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }


    public void getFailedPage (HttpExchange httpExchange)throws IOException{
        List<Card> artifacts = getArtifacts();
        List<Card> artifactsBasic = getBasicArtifacts(artifacts);
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);

            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/purchase_failed.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("artifacts", artifactsBasic);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        }catch (DaoException | NoSuchElementException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }

    public List<Card> getArtifacts(){
        List<Card> artifacts = new ArrayList<>();
        try{
            artifacts = cardDao.getArtifacts();
            return artifacts;
        }catch (DaoException e){
            e.printStackTrace();
        }
        return artifacts;
    }


    public List<Card> getBasicArtifacts(List<Card> artifacts){
        List<Card> basicArtifacts = new ArrayList<>();
        for (Card c: artifacts){
            if (c.getCardType().contains("basic")){
                basicArtifacts.add(c);

            }
        }
        return basicArtifacts;
    }


    public Student getStudentByID(HttpExchange httpExchange, Optional<HttpCookie> cookie){
        Student student = new Student.Builder().build();
        try{
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            student = studentDao.getStudent(userId);
            return student;

        }catch (DaoException | NoSuchElementException e){
            e.printStackTrace();
        }
        return student;
    }


    public void addTransactionToDatabase(String cardTitle, Student student){
        int studentId = student.getId();

        try{
            transactionDao.addTransaction(studentId,cardTitle);

        }catch (DaoException e){
            e.printStackTrace();
        }
    }


    public boolean verifyAbilityOfPurchase(String cardTitle, int coolcoinBalance){
        List<Card> artifacts = getArtifacts();
        int coolcoinValueOfBuyingCard;
        for (Card c: artifacts){
            if(c.getTitle().equals(cardTitle)){
                 coolcoinValueOfBuyingCard = c.getCoolcoinValue();
                 if(coolcoinValueOfBuyingCard*(-1) < coolcoinBalance){
                     return true;
                 }
            }
        }
        return false;
    }
}

package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Dao.StudentDao;
import Dao.TransactionDao;
import Model.Card;
import Model.GroupTransaction;
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
import java.nio.file.NoSuchFileException;
import java.util.*;

public class StudentHandleContribution implements HttpHandler {

    private CardDao cardDao;
    private StudentDao studentDao;
    private TransactionDao transactionDao;
    private SessionHandler sessionHandler;
    private RequestResponseService reqRespServ;

    public StudentHandleContribution (CardDao cardDao,StudentDao studentDao,
                                      TransactionDao transactionDao,SessionHandler sessionHandler,
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

        if (method.equals("GET")){
            getPage(httpExchange);
        }

        else if (method.equals("POST")) {
            Student student = getLoggedStudentByMail(httpExchange);
            Map<String, String> inputs = reqRespServ.getFormData(httpExchange);

            if (inputs.get("formType").equals("title")) {
                String title = inputs.get("title");
                String donation = inputs.get("donation");
                int donationValue = Integer.valueOf(donation);
                if (donation.matches("[0-9]+") && donationValue > 0 && donationValue <= student.getCoolcoins()) {
                    addGroupTransactionToDatabase(title, student, donationValue);
                    if (isDonationComplete(title)) {
                        List<Integer> donatorsIds = getDonatorsId(title);
                        for (Integer i : donatorsIds) {
                            int studentId = i;
                            addTransactionToDatabase(title, studentId);
                        }
                        deleteComplitedContribution(title);
                        getPage(httpExchange);

                    } else {
                        getPage(httpExchange);
                    }
                }else {
                    getFailedPage(httpExchange);
                }
            }
            if (inputs.get("formType").equals("logout")){
                System.out.println(inputs + "inputs after logout");
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
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Card> artifacts = getArtifacts();
            List<Card> artifactsRare = getRareArtifacts(artifacts);
            List<GroupTransaction> groupTransactions = getGroupTransactions();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/contribution.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("artifacts", artifactsRare);
            model.with("groupTransactions", groupTransactions);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        }catch (DaoException | NoSuchFileException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }


    public void getFailedPage (HttpExchange httpExchange)throws IOException{
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        String email = getEmailFromCookie(cookie,httpExchange);
        try{
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Card> artifacts = getArtifacts();
            List<Card> artifactsRare = getRareArtifacts(artifacts);
            List<GroupTransaction> groupTransactions = getGroupTransactions();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/contribution_failed.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("artifacts", artifactsRare);
            model.with("groupTransactions", groupTransactions);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        }catch (DaoException | NoSuchFileException e){
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


    public List<Card> getRareArtifacts(List<Card> artifacts){
        List<Card> rareArtifacts = new ArrayList<>();
        for (Card c: artifacts){
            if (c.getCardType().contains("rare")){
                rareArtifacts.add(c);
            }
        }
        return rareArtifacts;
    }


    public Student getLoggedStudentByMail(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        String email = getEmailFromCookie(cookie,httpExchange);
        Student student = new Student.Builder().build();
        try{
            student = studentDao.getStudentByEmail(email);
        }catch (DaoException e){
            e.printStackTrace();
        }
        return student;
    }


    public String getEmailFromCookie(Optional<HttpCookie> cookie, HttpExchange httpExchange){
        String email = "";
        try{
            int studentId = sessionHandler.getUserId(httpExchange,cookie);
            Student student  = studentDao.getStudent(studentId);
            email = student.getEmail();
            return email;
        }catch (DaoException | NoSuchElementException e){
            e.printStackTrace();
        }
        return email;
    }


    public void addGroupTransactionToDatabase(String cardTitle, Student student, int donation ){
        int studentId = student.getId();

        try{
            transactionDao.addGroupTransaction(studentId, cardTitle, donation);
            transactionDao.archivedGroupTransaction(studentId,cardTitle,donation);
        }catch (DaoException e){
            e.printStackTrace();
        }
    }


    public List<GroupTransaction> getGroupTransactions(){
        Map <String, Integer> groupTransactions = new HashMap<>();
        List<GroupTransaction> transactions = new ArrayList<>();

        try{
            groupTransactions = transactionDao.getGroupTransactions();
        }catch (DaoException e){
            e.printStackTrace();
        }
        for (Map.Entry<String, Integer> entry : groupTransactions.entrySet()) {
            transactions.add(new GroupTransaction(entry.getKey(),entry.getValue()));
        }
        return transactions;
    }


    public boolean isDonationComplete(String title){

        List<Card> rareArtifacts = getRareArtifacts(getArtifacts());

        List<GroupTransaction> transactions = getGroupTransactions();
        GroupTransaction lastDonatedCard;
        for(GroupTransaction g: transactions){
            if (title.contains(g.getTitle())){
                lastDonatedCard = g;
                //System.out.println(g.getTitle() + "last donated card");
                for (Card c: rareArtifacts){
                    if (c.getTitle().contains(g.getTitle())){
                        int valueOfArtifact = c.getCoolcoinValue();
                        int coolcoinsCollected = lastDonatedCard.getDonationValue();
                        if (valueOfArtifact*(-1) <= coolcoinsCollected){
                            return true;
                        }
                    }
                }

            }
        }

        return false;
    }


    public void addTransactionToDatabase(String cardTitle, Integer studentId){
        try{
            transactionDao.addTransaction(studentId,cardTitle);
        }catch (DaoException e){
            e.printStackTrace();
        }
    }


    public List<Integer> getDonatorsId(String title){
        List<Integer> donatorsId = new ArrayList<>();
        try{
            donatorsId = transactionDao.getDonatorsId(title);
            return donatorsId;
        }catch (DaoException e){
            e.printStackTrace();
        }
        return donatorsId;
    }


    public void deleteComplitedContribution(String title){
        try {
            transactionDao.deleteComplitedContribution(title);

        }catch (DaoException e){
            e.printStackTrace();
        }
    }

}



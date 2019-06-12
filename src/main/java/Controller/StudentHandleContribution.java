package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Dao.StudentDao;
import Dao.TransactionDao;
import Model.Card;
import Model.GroupTransaction;
import Model.Student;
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

public class StudentHandleContribution implements HttpHandler {

    private CardDao cardDao;
    private StudentDao studentDao;
    private TransactionDao transactionDao;
    CookieHelper cookieHelper = new CookieHelper();

    public StudentHandleContribution (CardDao cardDao,StudentDao studentDao, TransactionDao transactionDao){
        this.cardDao = cardDao;
        this.studentDao = studentDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> cookie = getSessionCookie(httpExchange);
        String method = httpExchange.getRequestMethod();
        String email = getEmailFromCookie(cookie.get().getValue());




        if (method.equals("GET")){
            getLoginPage(httpExchange);
        }

        if (method.equals("POST")){
            Student student = getLoggedStudentByMail(email);
            Map<String, String> inputs = getFormData(httpExchange);

            if(inputs.get("formType").equals("title")) {
                String title = inputs.get("title");
                String donation = inputs.get("donation");
                int donationValue = Integer.valueOf(donation);

                //System.out.println(donation + donation);
                //System.out.println(inputs);

                if(donation.matches("[0-9]+") && donationValue>0 && donationValue<student.getCoolcoins()){
                    addGroupTransactionToDatabase(title, student, donationValue);

                    if(isDonationComplete(title)){

                        List<Integer> donatorsIds = getDonatorsId(title);

                        for(Integer i: donatorsIds){
                            int studentId = i;
                            addTransactionToDatabase(title,studentId);
                            List<GroupTransaction> gropuTrans= getGropuTransactionByIDAndTitle(studentId,title);

                            System.out.println(gropuTrans.get(0).getDonationValue());

                        }
                        deleteComplitedContribution(title);
                        System.out.println("donation has been complited");


                    } else{
                        System.out.println( "donation not completed");
                    }

                    getLoginPage(httpExchange);
                } else {
                    getFailedPage(httpExchange);

                }

            }

        }
    }

    private void getLoginPage(HttpExchange httpExchange) throws IOException{
        List<Card> artifacts = getArtifacts();
        List<Card> artifactsRare = getRareArtifacts(artifacts);
        List<GroupTransaction> groupTransactions = getGroupTransactions();


        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/contribution.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("artifacts", artifactsRare);
        model.with("groupTransactions", groupTransactions);
        String response = template.render(model);
        sendResponse(httpExchange, response);
    }


    public void getFailedPage (HttpExchange httpExchange)throws IOException{
        List<Card> artifacts = getArtifacts();
        List<Card> artifactsRare = getRareArtifacts(artifacts);

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/contribution_failed.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("artifacts", artifactsRare);

        String response = template.render(model);
        sendResponse(httpExchange, response);
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
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


    public Student getLoggedStudentByMail(String email) throws IOException {
        Student student = new Student.Builder().build();
        try{
            student = studentDao.getStudentByEmail(email);
            //System.out.println(student.getLastName() + "this is name");


        }catch (DaoException e){
            e.printStackTrace();
        }
        return student;
    }

    public String getEmailFromCookie(String emailFromCookie){
        System.out.println(emailFromCookie + "   cookie in method");
        String trueMail = emailFromCookie.substring(1,emailFromCookie.length()-1);
        System.out.println(trueMail + "mail in method");
        return trueMail;
    }
    private Map<String, String> getFormData(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        Map<String, String> inputs = LoginController.parseFormData(formData);
        return inputs;
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
        //System.out.println(groupTransactions + "grupowe tranzakcje");
        return transactions;

    }

    private Optional<HttpCookie> getSessionCookie(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        //System.out.println(cookies + "lista w get session Cookie");
        return cookieHelper.findCookieByName("email", cookies);
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
                        //System.out.println(valueOfArtifact + "value of artifact");
                        //System.out.println(coolcoinsCollected + "value of coolcoins collected");
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
    public List<GroupTransaction> getGropuTransactionByIDAndTitle(int studentID, String title){
        List<GroupTransaction> groupTransactionsList = new ArrayList<>();
        try {
            groupTransactionsList = transactionDao.getGroupTransactionsByIdAndTitle(studentID,title);
            return groupTransactionsList;
        }catch (DaoException e){
            e.printStackTrace();
        }
        return groupTransactionsList;
    }

}



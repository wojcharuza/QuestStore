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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentHandleShop implements HttpHandler{

    private CardDao cardDao;
    private StudentDao studentDao;
    private TransactionDao transactionDao;

    public StudentHandleShop(CardDao cardDao,StudentDao studentDao, TransactionDao transactionDao){
        this.cardDao = cardDao;
        this.studentDao = studentDao;
        this.transactionDao = transactionDao;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpCookie cookie;
        String method = httpExchange.getRequestMethod();
        String sessionCookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        String email = getEmailFromCookie(sessionCookie);



        if (method.equals("GET")){
            getLoginPage(httpExchange);
        }

        if (method.equals("POST")){
            Student student = getLoggedStudentByMail(email);
            Map<String, String> inputs = getFormData(httpExchange);

            if(inputs.get("formType").equals("title")) {
                String title = inputs.get("title");
                System.out.println(inputs);
                System.out.println(title);
                if (verifyAbilityOfPurchase(title, student.getCoolcoins())) {
                    addTransactionToDatabase(title, student);
                    getSuccessPage(httpExchange);

                } else {
                    getFailedPage(httpExchange);

                }

            }

        }
    }

    private void getLoginPage(HttpExchange httpExchange) throws IOException{
        List<Card> artifacts = getArtifacts();
        System.out.println(artifacts.size());
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/shop.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("artifacts", artifacts);
        String response = template.render(model);
        sendResponse(httpExchange, response);
    }
    public void getSuccessPage(HttpExchange httpExchange)throws IOException{
        List<Card> artifacts = getArtifacts();
        System.out.println(artifacts.size());
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/purchase_success.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("artifacts", artifacts);
        String response = template.render(model);
        sendResponse(httpExchange, response);

    }
    public void getFailedPage (HttpExchange httpExchange)throws IOException{
        List<Card> artifacts = getArtifacts();
        System.out.println(artifacts.size());
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/purchase_failed.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("artifacts", artifacts);
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

    public String getEmailFromCookie(String cookie){
        String[] splitedCoookie = cookie.split(";");
        String email = splitedCoookie[2];
        String updatedEmail = email.replace("email=", "");
        String trueMail = updatedEmail.substring(2,updatedEmail.length()-1);
        return trueMail;
    }
    private Map<String, String> getFormData(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        Map<String, String> inputs = LoginController.parseFormData(formData);
        return inputs;
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

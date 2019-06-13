package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Model.Card;
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

public class MentorHandleArtifacts implements HttpHandler {
    private CardDao cardDao;
    private SessionHandler sessionHandler;

    public MentorHandleArtifacts(CardDao cardDao, SessionHandler sessionHandler) {
        this.cardDao = cardDao;
        this.sessionHandler = sessionHandler;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            try {
                getPage(httpExchange);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("POST")){
            Map<String, String> inputs = getFormData(httpExchange);

            if(inputs.get("formType").equals("addCard")){
                try {
                    addCard(inputs);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }
            else if(inputs.get("formType").equals("editCard")){
                try {
                    editCard(inputs);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            } else if(inputs.get("formType").equals("logout")){
                try {
                    sessionHandler.deleteSession(httpExchange);
                    getLoginPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void editCard(Map<String, String> inputs) throws DaoException {
        String oldTitle = inputs.get("oldTitle");
        String newTitle = inputs.get("title");
        String description = inputs.get("description");
        String coolcooinValue = inputs.get("price");
        String cardType = inputs.get("cardType");
        cardDao.editCard(oldTitle, newTitle, description, "", cardType, Integer.valueOf(coolcooinValue) *-1);
    }


    private void addCard(Map<String, String> inputs) throws DaoException {
        cardDao.addCard(inputs.get("title"), inputs.get("description"),
                "", inputs.get("cardType"), Integer.valueOf(inputs.get("price")) * -1);
    }


    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        SessionHandler sessionHandler = new SessionHandler();
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Card> artifacts = cardDao.getArtifacts();
            List<String> cardTypes = new ArrayList<>();
            cardTypes.add("artifact_basic");
            cardTypes.add("artifact_rare");
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor_artifacts.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("artifacts", artifacts);
            model.with("cardTypes", cardTypes);
            String response = template.render(model);
            sendResponse(httpExchange, response);
        } catch (DaoException | NoSuchElementException e){
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

    private Map<String, String> getFormData(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        Map<String, String> inputs = LoginController.parseFormData(formData);
        return inputs;
    }


}


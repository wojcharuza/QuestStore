package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Model.Card;
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

public class MentorHandleQuests implements HttpHandler {
    private CardDao cardDao;
    private SessionHandler sessionHandler;
    private RequestResponseService reqRespServ;

    public MentorHandleQuests(CardDao cardDao, SessionHandler sessionHandler, RequestResponseService reqRespServ) {
        this.cardDao = cardDao;
        this.sessionHandler = sessionHandler;
        this.reqRespServ = reqRespServ;
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
            Map<String, String> inputs = reqRespServ.getFormData(httpExchange);

            if(inputs.get("formType").equals("addQuest")){
                try {
                    addCard(inputs);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }

            else if(inputs.get("formType").equals("editQuest")){
                try {
                    editCard(inputs);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }

            else if(inputs.get("formType").equals("logout")){
                try {
                    sessionHandler.deleteSession(httpExchange);
                    reqRespServ.getLoginPage(httpExchange);
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
        cardDao.editCard(oldTitle, newTitle, description, "", cardType, Integer.valueOf(coolcooinValue));
    }


    private void addCard(Map<String, String> inputs) throws DaoException {
        cardDao.addCard(inputs.get("title"), inputs.get("description"),
                "", inputs.get("cardType"), Integer.valueOf(inputs.get("price")));
    }


    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        SessionHandler sessionHandler = new SessionHandler();
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Card> quests = cardDao.getQuests();
            List<String> cardTypes = new ArrayList<>();
            cardTypes.add("quest_basic");
            cardTypes.add("quest_rare");
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor_quests.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("quests", quests);
            model.with("cardTypes", cardTypes);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        } catch (DaoException | NoSuchElementException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }
}


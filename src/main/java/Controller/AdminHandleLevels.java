package Controller;

import Dao.DaoException;
import Dao.LevelDao;
import Model.Level;
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

public class AdminHandleLevels implements HttpHandler {
    private LevelDao levelDao;
    private SessionHandler sessionHandler;
    private RequestResponseService reqRespServ;

    public AdminHandleLevels(LevelDao levelDao, SessionHandler sessionHandler, RequestResponseService reqRespServ){
        this.levelDao = levelDao;
        this.sessionHandler = sessionHandler;
        this.reqRespServ = reqRespServ;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if (method.equals("GET")){
            try {
                getPage(httpExchange);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        }


        else if (method.equals("POST")) {
            Map<String, String> inputs = reqRespServ.getFormData(httpExchange);

            if(inputs.get("formType").equals("editLevel")){
                List<Level> levels = prepareLevels(inputs);
                try {
                    levelDao.editLevels(levels);
                    getPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }

            else if (inputs.get("formType").equals("logout")){
                try {
                    sessionHandler.deleteSession(httpExchange);
                    reqRespServ.getLoginPage(httpExchange);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        SessionHandler sessionHandler = new SessionHandler();
        Optional<HttpCookie> cookie = sessionHandler.getSessionCookie(httpExchange);
        try {
            int userId = sessionHandler.getUserId(httpExchange, cookie);
            List<Level> levels = levelDao.getLevels();
            JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin_levels.twig");
            JtwigModel model = JtwigModel.newModel();
            model.with("levels", levels);
            String response = template.render(model);
            reqRespServ.sendResponse(httpExchange, response);
        }
        catch (DaoException | NoSuchElementException e){
            reqRespServ.getLoginPage(httpExchange);
        }
    }


    private List<Level> prepareLevels(Map<String, String> stringLevels){
        List<Level> levels = new ArrayList<>();
        for(Map.Entry<String, String> entry : stringLevels.entrySet()){
            if(!entry.getKey().equals("formType")){
            Level level = new Level.Builder().withLevelNumber
                    (Integer.parseInt(entry.getKey())).withExperienceNeeded(Integer.parseInt(entry.getValue())).build();
            levels.add(level);}
        }
        return levels;
    }
}

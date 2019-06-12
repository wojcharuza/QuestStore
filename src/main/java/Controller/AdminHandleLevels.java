package Controller;

import Dao.DaoException;
import Dao.LevelDao;
import Model.Classroom;
import Model.Level;
import Model.Mentor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminHandleLevels implements HttpHandler {
    LevelDao levelDao;

    public AdminHandleLevels(LevelDao levelDao){
        this.levelDao = levelDao;
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
        if (method.equals("POST")) {
            Map<String, String> inputs = getFormData(httpExchange);
            List<Level> levels = prepareLevels(inputs);
            try {
                levelDao.editLevels(levels);
                getPage(httpExchange);
            } catch (DaoException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPage(HttpExchange httpExchange) throws IOException, DaoException {
        List<Level> levels = levelDao.getLevels();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin_levels.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("levels", levels);
        String response = template.render(model);
        sendResponse(httpExchange, response);
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
        return  inputs;
    }

    private List<Level> prepareLevels(Map<String, String> stringLevels){
        List<Level> levels = new ArrayList<>();
        for(Map.Entry<String, String> entry : stringLevels.entrySet()){
            Level level = new Level.Builder().withLevelNumber
                    (Integer.parseInt(entry.getKey())).withExperienceNeeded(Integer.parseInt(entry.getValue())).build();
            levels.add(level);
        }
        return levels;
    }











}

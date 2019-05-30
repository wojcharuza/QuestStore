package Controller;

import Dao.CardDao;
import Dao.DaoException;
import Model.Card;
import Model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class StudentHandleShop implements HttpHandler{

    private CardDao cardDao;

    public StudentHandleShop(CardDao cardDao){
        this.cardDao = cardDao;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {


        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")){
            getLoginPage(httpExchange);
        }
        if (method.equals("POST")){

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
}

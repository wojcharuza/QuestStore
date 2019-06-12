package Controller;

import Dao.DaoException;
import Dao.SessionDao;
import Dao.SessionDaoImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.net.HttpCookie;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SessionHandler{
    private CookieHelper cookieHelper;
    private SessionDao sessionDao;

    public SessionHandler(){
        this.cookieHelper =  new CookieHelper();
        this.sessionDao = new SessionDaoImpl();
    }


    public Optional<HttpCookie> getSessionCookie(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        return cookieHelper.findCookieByName("sessionId", cookies);
    }


    public int getUserId(HttpExchange httpExchange, Optional<HttpCookie> cookie)throws DaoException, NoSuchElementException {
        String sessionId = cookie.get().getValue();
        return sessionDao.getUserId(sessionId);
    }

    public void deleteSession(HttpExchange httpExchange) throws DaoException{
        Optional<HttpCookie> cookie = getSessionCookie(httpExchange);
        sessionDao.deleteSession(cookie.get().getValue());
    }
}

package Dao;

public interface SessionDao {
    void addSession(int userId, String sessionId) throws DaoException;
    void deleteSession(String sessionId) throws DaoException;
    int getUserId(String sessionId) throws DaoException;
}

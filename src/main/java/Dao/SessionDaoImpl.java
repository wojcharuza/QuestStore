package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionDaoImpl implements SessionDao{

    @Override
    public void addSession(int userId, String sessionId) throws DaoException{
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT INTO SESSIONS(user_id, session_id) VALUES(?,?)");
            stmt.setInt(1, userId);
            stmt.setString(2, sessionId);
            stmt.executeUpdate();
        }
        catch (SQLException e){
            throw new DaoException();
        }
    }


    @Override
    public void deleteSession(String sessionId) throws DaoException{
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("DELETE FROM SESSIONS WHERE session_id = ?");
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        }
        catch (SQLException e){
            throw new DaoException();
        }
    }



    public int getUserId(String sessionId) throws DaoException{
        int userId = -1;
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT user_id FROM SESSIONS WHERE session_id = ?");
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            userId = rs.getInt("user_id");
            return userId;
        }
        catch (SQLException e){
            throw new DaoException();
        }
    }
}

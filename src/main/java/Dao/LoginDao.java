package Dao;


public interface LoginDao {
    String checkPermission(String email, String password) throws DaoException;
    int getIdByMail(String mail) throws DaoException;

}





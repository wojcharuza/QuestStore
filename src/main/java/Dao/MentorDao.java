package Dao;

import Model.Mentor;
import java.util.List;

public interface MentorDao {

    void addMentor(String firstName, String lastName, String email, String password) throws DaoException;
    void editMentor(int id, String firstName, String lastName, String email) throws DaoException;
    List<Mentor> getMentors() throws DaoException;
    String getMentorNameById(int mentorId) throws DaoException;
}

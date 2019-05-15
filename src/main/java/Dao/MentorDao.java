package Dao;


import main.java.Model.Mentor;

import java.util.List;

public interface MentorDao {

    void addMentor(String firstName, String lastName, String email, String password);
    void editMentor(int id, String firstName, String lastName, String email, String password);
    List<Mentor> getMentors();
}

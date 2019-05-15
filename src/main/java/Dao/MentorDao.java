package main.java.Dao;


import main.java.Model.Mentor;

import java.util.List;

public interface MentorDao {

    void addMentor(String firstName, String lastName, String email, String password);
    void editMentor();
    List<Mentor> getMentors();
}

package main.java.Dao;


import main.java.Model.Mentor;

import java.util.List;

public interface EmployeeDao {

    void addMentor();
    void editMentor();
    List<Mentor> getMentors();
}

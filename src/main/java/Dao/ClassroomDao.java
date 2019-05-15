package main.java.Dao;

import main.java.Model.Classroom;

import java.util.List;

public interface ClassroomDao {

    List<Classroom> getClassrooms();
    void deleteClassRoom();
}

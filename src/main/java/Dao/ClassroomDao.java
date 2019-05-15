package Dao;

import Model.Classroom;

import java.util.List;

public interface ClassroomDao {

    List<Classroom> getClassrooms();
    void deleteClassRoom(int id);
}

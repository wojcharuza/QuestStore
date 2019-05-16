package Dao;

import Model.Classroom;

import java.util.List;

public interface ClassroomDao {

    List<Classroom> getClassrooms() throws DaoException;
    void deleteClassRoom(int id) throws DaoException;
}

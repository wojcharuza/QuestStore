package Dao;

import Model.Classroom;
import java.util.List;

public interface ClassroomDao {

    List<Classroom> getClassrooms() throws DaoException;
    void deleteClassRoom(int id) throws DaoException;
    void addClassroom(String date, int mentorId) throws DaoException;
    void setMentorIdAsNull(int id) throws DaoException;
}

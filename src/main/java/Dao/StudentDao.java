package Dao;

import Model.Student;
import java.util.List;

public interface StudentDao {
    void addNewStudent(String firstName, String lastName, String email, String password, int classRooomId) throws DaoException;
    void addNewStudent(String firstName, String lastName, String email, String password) throws DaoException;
    List<Student> getAllStudents() throws DaoException;
    void editStudent(int id, String firstName, String lastName, String email) throws DaoException;
    void deleteStudent(int id) throws DaoException;
    Student getStudent(int id) throws DaoException;
    Student getStudentByEmail(String email) throws DaoException;
    List<Student> getStudentsByMentor(int id) throws DaoException;
    void deleteStudentsFromClassroom(int classRoomId) throws DaoException;
    List<Integer> getStudentsIdsFromClassroom(int classRoomId) throws DaoException;

}

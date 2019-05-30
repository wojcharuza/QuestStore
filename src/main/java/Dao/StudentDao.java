package Dao;

import Model.Student;
import java.util.List;

public interface StudentDao {
    void addNewStudent(String firstName, String lastName, String email, String password) throws DaoException;
    List<Student> getAllStudents() throws DaoException;
    void editStudent(int id, String firstName, String lastName, String email, String password) throws DaoException;
    void deleteStudent(int id) throws DaoException;
    Student getStudent(int id) throws DaoException;
    Student getStudentByEmail(String email) throws DaoException;
}

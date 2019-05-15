package Dao;

import Model.Student;

import java.util.List;

public interface StudentDao {
    void addNewStudent(String firstName, String lastName, String email, String password);
    List<Student> getAllStudents();
    void editStudent(int id, String firstName, String lastName, String email, String password);
    void deleteStudent(int id);
    Student getStudent(int id);
}

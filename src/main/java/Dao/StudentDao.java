package Dao;

import main.java.Model.Student;

import java.util.List;

public interface StudentDao {
    void addNewStudent(String firstName, String lastName, String email, String password);
    List<Student> getAllStudents();
    void editStudent(int id, String firstName, String lastName, String email, String password);
    void deleteStudent();
    Student getStudent(int id);
}

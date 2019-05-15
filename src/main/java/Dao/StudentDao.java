package Dao;

import Model.Student;

import java.util.List;

public interface StudentDao {
    void addNewStudent();
    List<Student> getAllStudents();
    void editStudent();
    void deleteStudent();
    Student getStudent();
}

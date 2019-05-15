package main.java;

import main.java.Dao.MentorDaoImpl;
import main.java.Dao.StudentDaoImpl;

public class Main {



    public static void main(String[] args) {
        StudentDaoImpl studentDao = new StudentDaoImpl();
        System.out.println(studentDao.getCoolcoinBalance(4));
    }

}

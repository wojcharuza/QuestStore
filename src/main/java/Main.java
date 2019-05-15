


import Dao.MentorDaoImpl;
import Dao.StudentDaoImpl;

public class Main {



    public static void main(String[] args) {
        StudentDaoImpl studentDao = new StudentDaoImpl();
        System.out.println(studentDao.getCoolcoinBalance(4));
    }

}

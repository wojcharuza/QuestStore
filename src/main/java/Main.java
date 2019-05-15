


import Dao.ClassroomDaoImpl;
import Dao.MentorDaoImpl;
import Dao.StudentDaoImpl;

public class Main {



    public static void main(String[] args) {
        ClassroomDaoImpl classroomDao = new ClassroomDaoImpl();
        classroomDao.deleteClassRoom(5);


        StudentDaoImpl studentDao = new StudentDaoImpl();
        System.out.println(studentDao.getCoolcoinBalance(4));

    }

}

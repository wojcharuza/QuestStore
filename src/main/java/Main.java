


import Dao.*;
import Model.Admin;
import Model.Mentor;
import Model.Student;

public class Main {



    public static void main(String[] args) {
        ClassroomDaoImpl classroomDao = new ClassroomDaoImpl();
        classroomDao.deleteClassRoom(5);


        StudentDaoImpl studentDao = new StudentDaoImpl();
        System.out.println(studentDao.getCoolcoinBalance(4));

        LoginDaoImpl loginDaoImpl = new LoginDaoImpl();

        //loginDaoImpl.getUser("halo@halo","halo");
//        Mentor mentro = loginDaoImpl.getMentor("halo@halo","halo");
//        System.out.println(mentro.getFirstName() + mentro.getLastName());
//
        Admin admin = loginDaoImpl.getAdmin("halo@halo","halo");
        System.out.println(admin.getFirstName() + admin.getLastName());
    }
}

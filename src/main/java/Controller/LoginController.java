package Controller;
//
//import Dao.LoginDaoImpl;
//import Model.Admin;
//import Model.Mentor;
//import Model.Student;
//
//import javax.swing.plaf.synth.SynthTextAreaUI;
//import java.util.Scanner;
//
//public class LoginController {
//
//    public void runLoginProcedure () {
//        Scanner scanner = new Scanner(System.in);
//        LoginDaoImpl loginDao = new LoginDaoImpl();
//
//
//        System.out.println("ener email");
//        String email = scanner.nextLine();
//        System.out.println("Enter password");
//        String password = scanner.nextLine();
//
//        String userType = loginDao.checkPermission(email,password);
//        if (userType.contains("mentor")){
//            Mentor mentor = loginDao.getMentor(email,password);
//
//        }
//        if (userType.contains("student")){
//            Student student = loginDao.getStudent(email,password);
//
//
//        }
//        if (userType.contains("admin")){
//            Admin admin = loginDao.getAdmin(email,password);
//
//        }
//    }
//
//}

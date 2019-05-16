package Dao;

import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDaoImpl implements LoginDao {



    public String checkPermission(String email, String password) {
        String permissionType = new String();
        try (Connection con = C3P0DataSource.getInstance().getConnection(); Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT email, password, permission FROM users WHERE email = '" +email+ "' AND password = '" +password+ "';");
            while (rs.next()){
                permissionType = rs.getString("permission");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissionType;
    }

    public Mentor getMentor(String email, String password){
        Mentor tempMentor = new Mentor.Builder().build();
        try (Connection con = C3P0DataSource.getInstance().getConnection(); Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email = '" +email+ "' AND password = '" +password+ "';");
            while (rs.next()) {
                email = rs.getString("email");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                password = rs.getString("password");
                int id = rs.getInt("id");
                Mentor mentor = new Mentor.Builder().withID(id).withFirstName(firstName).withLastName(lastName).withEmail(email).withPassword(password).build();
                tempMentor = mentor;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tempMentor;
    }

    public Student getStudent (String email, String password){
        StudentDaoImpl studentDao = new StudentDaoImpl();
        Student student = new Student.Builder().build();
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT * FROM users WHERE email = '" +email+ "' AND password = '" +password+ "';");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {


                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                email = rs.getString("email");
                password = rs.getString("password");
                int classroom = rs.getInt("class_id");
                int coolcoins = studentDao.getCoolcoinBalance(rs.getInt("id"));
                student = new Student.Builder().wirhId(id)
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withEmail(email)
                        .withPassword(password)
                        .withClassId(classroom)
                        .withCoolcoins(coolcoins)
                        .build();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;


    }
    public Admin getAdmin(String email, String password){

        Admin tempAdmin = new Admin.Builder().build();
        try (Connection con = C3P0DataSource.getInstance().getConnection(); Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email = '" +email+ "' AND password = '" +password+ "';");
            while (rs.next()) {
                email = rs.getString("email");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                password = rs.getString("password");
                Admin admin = new Admin.Builder().withFirstName(firstName).withLastName(lastName).withEmail(email).withPassword(password).build();
                tempAdmin = admin;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tempAdmin;

    }



}

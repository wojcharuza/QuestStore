package main.java.Dao;

import main.java.Model.Mentor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MentorDaoImpl implements MentorDao {

    public void addMentor() {

    }

    public void editMentor() {

    }

    public void getMentors() {
        try (Connection con = C3P0DataSource.getInstance().getConnection(); Statement stmt = con.createStatement()) {
            List<Mentor> mentors = new ArrayList<>();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"User\" WHERE permission = 'mentor'");
            while (rs.next()) {
                String email = rs.getString("email");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String password = rs.getString("password");
                int id = rs.getInt("id");
                System.out.println(id);
                System.out.println(email);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

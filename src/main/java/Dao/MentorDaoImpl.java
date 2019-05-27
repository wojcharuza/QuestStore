package Dao;

import Model.Mentor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorDaoImpl implements MentorDao {

    public void addMentor(String firstName, String lastName, String email, String password) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT INTO Users(first_name, last_name, email, password, permission) VALUES (?, ?, ?, ?, 'mentor')");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.executeQuery();
        } catch (SQLException e) {
            throw new DaoException();
        }

    }

    public void editMentor(int id, String firstName, String lastName, String email, String password) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("UPDATE Users SET first_name = ?, last_name = ?, email = ?, password = ? WHERE id = ?");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setInt(5, id);
            stmt.executeQuery();

        } catch (SQLException e) {
            throw new DaoException();
        }

    }

    public List<Mentor> getMentors() throws DaoException {
        List<Mentor> mentors = new ArrayList<>();
        try (Connection con = C3P0DataSource.getInstance().getConnection(); Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM Users WHERE permission = 'mentor'");
            while (rs.next()) {
                String email = rs.getString("email");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String password = rs.getString("password");
                int id = rs.getInt("id");
                Mentor mentor = new Mentor.Builder().withID(id).withFirstName(firstName).withLastName(lastName).withEmail(email).withPassword(password).build();
                mentors.add(mentor);
            }
            return mentors;

        } catch (SQLException e) {
            throw new DaoException();
        }
    }

}

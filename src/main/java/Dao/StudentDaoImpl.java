package Dao;

import Model.Card;
import Model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class StudentDaoImpl implements StudentDao {

    public void addNewStudent(String firstName, String lastName, String email, String password) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT INTO users(first_name, last_name, email, password, permission) VALUES" +
                    " (?, ?, ?, ?, 'student')");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException();
        }
    }



    public void addNewStudent(String firstName, String lastName, String email, String password, int classRoomId) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT INTO users(first_name, last_name, email, password, permission, class_id) VALUES" +
                    " (?, ?, ?, ?, 'student', ?)");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setInt(5, classRoomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException();
        }
    }





    public List<Student> getAllStudents() throws DaoException {
        List<Student> students = new ArrayList<>();
        try (Connection con = C3P0DataSource.getInstance().getConnection(); Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE permission = 'student'");
            while (rs.next()) {
                int id = rs.getInt("id");
                Student student = getStudent(id);
                students.add(student);
            }
            students.sort(comparing(Student::getId));
            return students;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    public void editStudent(int id, String firstName, String lastName, String email) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("UPDATE users SET first_name = ?, last_name = ?, email = ? " +
                    "WHERE id = ?");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setInt(4, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    public void deleteStudent(int idToDelete) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("DELETE FROM users WHERE id = ?");
            stmt.setInt(1, idToDelete);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    public Student getStudent(int id) throws DaoException {

        Student student = new Student.Builder().build();
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT * FROM users WHERE id = ? AND permission = 'student'");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                int classroom = rs.getInt("class_id");
                int coolcoins = getCoolcoinBalance(id);
                List<Card> usedArtifacts = getCardsUsedByStudent(id);
                student = new Student.Builder().wirhId(id)
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withEmail(email)
                        .withPassword(password)
                        .withClassId(classroom)
                        .withCoolcoins(coolcoins)
                        .withUsedArtifacts(usedArtifacts).build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
            return student;
    }
    public Student getStudentByEmail(String email) throws DaoException{
        Student student = new Student.Builder().build();
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT * FROM users WHERE email LIKE ? ");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String password = rs.getString("password");
                int classroom = rs.getInt("class_id");
                int coolcoins = getCoolcoinBalance(id) - getStudentDonations(id);
                List<Card> usedArtifacts = getUsedArtifacts(id);
                student = new Student.Builder().wirhId(id)
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withEmail(email)
                        .withPassword(password)
                        .withClassId(classroom)
                        .withCoolcoins(coolcoins)
                        .withUsedArtifacts(usedArtifacts).build();
            }
            return student;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException();
        }

    }

    public int getCoolcoinBalance(int id) throws DaoException {

        int balance = 0;
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT coolcoin_value FROM \"Transactions\" JOIN \"Cards\" ON " +
                    "\"Transactions\".card_title = \"Cards\".title WHERE student_id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int value = rs.getInt("coolcoin_value");
                if (value > -40){
                    balance += value;
                }

            }
            return balance;

        } catch (SQLException e) {
            throw new DaoException();
        }
    }
    public int getStudentDonations(int id) throws  DaoException {
        int donations = 0;
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT donation FROM \"archived_group_transactions\"  WHERE student_id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int value = rs.getInt("donation");
                donations += value;
            }
            return donations;

        } catch (SQLException e) {
            throw new DaoException();
        }

    }

    public List<Card> getUsedArtifacts(int id) throws DaoException {

        List<Card> usedArtifacts = new ArrayList<>();
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT card_title, description, card_type, coolcoin_value, image_path FROM " +
                    "\"Transactions\" JOIN \"Cards\" ON \"Transactions\".card_title = \"Cards\".title\n" +
                    "WHERE student_id = ? AND card_type::text like 'artifact%'");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("card_title");
                String description = rs.getString("description");
                String type = rs.getString("card_type");
                int value = rs.getInt("coolcoin_value");
                String imagePath = rs.getString("image_path");
                Card card =
                        new Card.Builder().withTitle(title)
                                            .withDescription(description)
                                            .withCardType(type).withCoolcoinValue(value)
                                            .withImagePath(imagePath).build();
                usedArtifacts.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            return usedArtifacts;
    }

    public List<Card> getCardsUsedByStudent(int studentId) throws DaoException {
        List<Card> usedCards = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT title, description, image_path, card_type, " +
                    "coolcoin_value FROM \"Transactions\" LEFT JOIN \"Cards\" ON " +
                    "\"Transactions\".card_title = \"Cards\".title WHERE student_id = ? AND " +
                    "card_type::text LIKE 'artifact%' ;");
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String title = rs.getString("title");
                String description = rs.getString("description");
                String imagePath = rs.getString("image_path");
                String card_type = rs.getString("card_type");
                int coolcoinValue = rs.getInt("coolcoin_value");
                Card card = new Card.Builder().withTitle(title).withDescription(description)
                        .withImagePath(imagePath).withCardType(card_type).withCoolcoinValue(coolcoinValue).build();
                usedCards.add(card);
            }

            return usedCards;

        } catch (SQLException e) {
            throw new DaoException();
        }
    }
}
